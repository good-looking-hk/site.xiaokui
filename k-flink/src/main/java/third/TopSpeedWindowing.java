package third;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.flink.annotation.Public;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.evictors.TimeEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.DeltaTrigger;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * An example of grouped stream windowing where different eviction and trigger policies can be used.
 * A source fetches events from cars every 100 msec containing their id, their current speed (kmh),
 * overall elapsed distance (m) and a timestamp. The streaming example triggers the top speed of
 * each car every x meters elapsed for the last y seconds.
 */
public class TopSpeedWindowing {

    @Public
    public static class Tuple4<T0, T1, T2, T3> extends org.apache.flink.api.java.tuple.Tuple4<T0, T1, T2, T3>  {

        public Tuple4() {}

        public Tuple4(T0 value0, T1 value1, T2 value2, T3 value3) {
            this.f0 = value0;
            this.f1 = value1;
            this.f2 = value2;
            this.f3 = value3;
        }

        @Override
        public String toString() {
            if (this.f2 instanceof Double) {
                int temp = ((Double) this.f2).intValue();
                return "时间" + new SimpleDateFormat("HH:mm:ss.S").format(this.f3) + "车辆=" + this.f0 + ", 当前速度=" + this.f1 + ", 总经过距离=" + temp;
            }
            return "时间" + new SimpleDateFormat("HH:mm:ss.S").format(this.f3) + "车辆=" + this.f0 + ", 当前速度=" + this.f1 + ", 总经过距离=" + this.f2;
        }
    }

    public static void main(String[] args) throws Exception {

        final ParameterTool params = ParameterTool.fromArgs(args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);

        @SuppressWarnings({"rawtypes", "serial"})
        DataStream<Tuple4<Integer, Integer, Double, Long>> carData;
        if (params.has("input")) {
            carData = env.readTextFile(params.get("input")).map(new ParseCarData());
        } else {
            System.out.println("Executing TopSpeedWindowing example with default input data set.");
            System.out.println("Use --input to specify file input.");
            carData = env.addSource(CarSource.create(2));
        }

        int evictionSec = 10;
        double triggerMeters = 50;
        // 四个入参分别为 车辆id、当前速度（kmh)、总经过距离（m）、时间戳
        // 在过去的y秒内 每x米触发一次 求每辆汽车的最高速度。这里为每10秒内每50米
        // 总体含义为：过去10秒内，每50m统计一次汽车的最高速度，是有点绕
        DataStream<Tuple4<Integer, Integer, Double, Long>> topSpeeds =
                carData.assignTimestampsAndWatermarks(new CarTimestamp())
                        .keyBy(value -> value.f0)
                        .window(GlobalWindows.create())
                        // CountEvictor：在窗口维护用户指定数量的元素，如果多于用户指定的数量，从窗口缓冲区的开头丢弃多余的元素。
                        // DeltaEvictor：使用 DeltaFunction 和一个阈值，来计算窗口缓冲区中的最后一个元素与其余每个元素之间的差值，并删除差值大于或等于阈值的元素。
                        // TimeEvictor：以毫秒为单位的时间间隔（interval）作为参数，对于给定的窗口，找到元素中的最大的时间戳max_ts，并删除时间戳小于max_ts - interval的所有元素。
                        .evictor(TimeEvictor.of(Time.of(evictionSec, TimeUnit.SECONDS)))
                        // EventTimeTrigger：通过对比Watermark和窗口的Endtime确定是否触发窗口计算，如果Watermark大于Window EndTime则触发，否则不触发，窗口将继续等待。
                        // ProcessTimeTrigger：通过对比ProcessTime和窗口EndTme确定是否触发窗口，如果ProcessTime大于EndTime则触发计算，否则窗口继续等待。
                        // ContinuousEventTimeTrigger：根据间隔时间周期性触发窗口或者Window的结束时间小于当前EndTime触发窗口计算。
                        // ContinuousProcessingTimeTrigger：根据间隔时间周期性触发窗口或者Window的结束时间小于当前ProcessTime触发窗口计算。
                        // CountTrigger：根据接入数据量是否超过设定的阙值判断是否触发窗口计算。
                        // DeltaTrigger：根据接入数据计算出来的Delta指标是否超过指定的Threshold去判断是否触发窗口计算。
                        // PurgingTrigger：可以将任意触发器作为参数转换为Purge类型的触发器，计算完成后数据将被清理。
                        .trigger(
                                DeltaTrigger.of(
                                        triggerMeters,
                                        new DeltaFunction<
                                                Tuple4<Integer, Integer, Double, Long>>() {
                                            private static final long serialVersionUID = 1L;

                                            @Override
                                            public double getDelta(
                                                    Tuple4<Integer, Integer, Double, Long>
                                                            oldDataPoint,
                                                    Tuple4<Integer, Integer, Double, Long>
                                                            newDataPoint) {
                                                // System.err.println("时间" + new SimpleDateFormat("HH:mm:ss.S").format(newDataPoint.f3) + "车辆=" + newDataPoint.f0 + ", 当前速度=" + newDataPoint.f1 + ", 总经过距离=" + newDataPoint.f2.intValue());
                                                return newDataPoint.f2 - oldDataPoint.f2;
                                            }
                                        },
                                        carData.getType().createSerializer(env.getConfig())))
                        .maxBy(1);

        if (params.has("output")) {
            topSpeeds.writeAsText(params.get("output"));
        } else {
            System.out.println("Printing result to stdout. Use --output to specify output path.");
            topSpeeds.print();
        }

        env.execute("CarTopSpeedWindowingExample");
    }

    // *************************************************************************
    // USER FUNCTIONS
    // *************************************************************************

    private static class CarSource
            implements SourceFunction<Tuple4<Integer, Integer, Double, Long>> {

        private static final long serialVersionUID = 1L;
        private Integer[] speeds;
        private Double[] distances;

        private Random rand = new Random();

        private volatile boolean isRunning = true;

        private CarSource(int numOfCars) {
            speeds = new Integer[numOfCars];
            distances = new Double[numOfCars];
            Arrays.fill(speeds, 50);
            Arrays.fill(distances, 0d);
        }

        public static CarSource create(int cars) {
            return new CarSource(cars);
        }

        /**
         * 每100毫秒，使两辆车的速度各有二分之一概率变为 55|100 或 0|45 其中的一个最小/最大的数值，其驶过的具体为 += 速度 / 3.6
         * 这里需要注意的是，驶过的距离 并不一定 与速度 呈线性正相关，关系应该是非线性正相关
         */
        @Override
        public void run(SourceContext<Tuple4<Integer, Integer, Double, Long>> ctx)
                throws Exception {

            while (isRunning) {
                Thread.sleep(100);
                for (int carId = 0; carId < speeds.length; carId++) {
                    if (rand.nextBoolean()) {
                        speeds[carId] = Math.min(100, speeds[carId] + 5);
                    } else {
                        speeds[carId] = Math.max(0, speeds[carId] - 5);
                    }
                    distances[carId] += speeds[carId] / 3.6d;
                    Tuple4<Integer, Integer, Double, Long> record =
                            new Tuple4<>(
                                    carId,
                                    speeds[carId],
                                    distances[carId],
                                    System.currentTimeMillis());
                    int i = carId;
                    System.out.println(new SimpleDateFormat("HH:mm:ss.S").format(new Date()) + " 车辆" + i + ": 速度为" + speeds[i] + ", 共行驶距离为" + distances[i].intValue());
                    ctx.collect(record);
                }
            }
        }

        @Override
        public void cancel() {
            isRunning = false;
        }
    }

    private static class ParseCarData
            extends RichMapFunction<String, Tuple4<Integer, Integer, Double, Long>> {
        private static final long serialVersionUID = 1L;

        @Override
        public Tuple4<Integer, Integer, Double, Long> map(String record) {
            String rawData = record.substring(1, record.length() - 1);
            String[] data = rawData.split(",");
            return new Tuple4<>(
                    Integer.valueOf(data[0]),
                    Integer.valueOf(data[1]),
                    Double.valueOf(data[2]),
                    Long.valueOf(data[3]));
        }
    }

    private static class CarTimestamp
            extends AscendingTimestampExtractor<Tuple4<Integer, Integer, Double, Long>> {
        private static final long serialVersionUID = 1L;

        @Override
        public long extractAscendingTimestamp(Tuple4<Integer, Integer, Double, Long> element) {
            return element.f3;
        }
    }
}
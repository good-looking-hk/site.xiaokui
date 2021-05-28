package db.controller;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import db.entity.OrderDTO;
import db.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 测试excel导出
 * @author HK
 * @date 2021-04-21 17:14
 */
@Profile("4-million-excel")
@Slf4j
@RequiredArgsConstructor
@RestController
public class TestMillionExcelController {

    private final OrderService orderService;

    private final JdbcTemplate jdbcTemplate;

    private static final Random OR = new Random();
    private static final Random AR = new Random();
    private static final Random DR = new Random();

    /**
     * 赵钱孙李周吴郑王
     * 冯陈褚卫蒋沈韩杨
     * 朱秦尤许何吕施张
     * 孔曹严华金魏陶姜
     * 辉葵黄海强斌冬韬
     * http://localhost:7070/test1
     */
    @GetMapping(value = "/test1")
    public void test1(int total, HttpServletResponse response) throws Exception {
        total *= 10000;
        long startTime = System.currentTimeMillis();
        for (int d = 0; d < total / 100; d++) {
            String item = "('%s','%d','%d','%s','%s')";
            StringBuilder sql = new StringBuilder("INSERT INTO t_user(name,sex,age,tag,remark) VALUES ");
            for (int i = 0; i < total / (total / 100); i++) {
                String name = RandomUtil.randomString("赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨朱秦尤许何吕施张孔曹严华金魏陶姜辉葵黄海强斌冬韬", 2);
                int sex= RandomUtil.randomInt(1, 3);
                int age = RandomUtil.randomInt(15, 25);
                String tag = RandomUtil.randomString("穷富中", 1);
                String remark = RandomUtil.randomString("abcdefghijklmnopqrstuvwxyz", 3);
                sql.append(String.format(item, name, sex, age, tag, remark)).append(",");
            }
            jdbcTemplate.execute(sql.substring(0, sql.lastIndexOf(",")));
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("生成" + total + "数据成功，耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        response.getWriter().flush();
    }


    /**
     * http://localhost:7070/init
     */
    @GetMapping(value = "/init")
    public void init(HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(), new ClassPathResource("t_order.sql"));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("初始化表数据成功，耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        response.getWriter().flush();
    }

    /**
     * http://localhost:7070/total
     */
    @GetMapping(value = "/total")
    public void total(HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        int row = jdbcTemplate.queryForObject("select count(*) from t_order", Integer.class);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("共有数据" + row + "(" + row / 10000 + "万)行，查询耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        response.getWriter().flush();
    }

    /**
     * http://localhost:7070/delete?maxId=100000
     */
    @GetMapping(value = "/delete")
    public void deleteData(int maxId, HttpServletResponse response) throws Exception {
        maxId *= 10000;
        long startTime = System.currentTimeMillis();
        int row = jdbcTemplate.update("delete from t_order where id <= " + maxId);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("共删除数据" + row + "行，耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        response.getWriter().flush();
    }

    /**
     * http://localhost:7070/generate?total=10000
     */
    @GetMapping(value = "/generate")
    public void generateData(int total, HttpServletResponse response) throws Exception {
        total *= 10000;
        long startTime = System.currentTimeMillis();
        for (int d = 0; d < total / 100; d++) {
            String item = "('%s','%d','2020-07-%d 00:00:00','%d')";
            StringBuilder sql = new StringBuilder("INSERT INTO t_order(order_id,amount,payment_time,order_status) VALUES ");
            for (int i = 0; i < total / (total / 100); i++) {
                sql.append(String.format(item, UUID.randomUUID().toString().replace("-", ""),
                        AR.nextInt(100000) + 1, DR.nextInt(31) + 1, OR.nextInt(3))).append(",");
            }
            jdbcTemplate.execute(sql.substring(0, sql.lastIndexOf(",")));
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write("生成" + total + "数据成功，耗时 " + (System.currentTimeMillis() - startTime) + " ms");
        response.getWriter().flush();
    }

    /**
     * 最终优化，完美，100百万数据耗时30秒即可生成
     * http://localhost:7070/export?paymentDateTimeStart=2020-07-01&paymentDateTimeEnd=2020-07-16
     */
    @GetMapping(path = "/export")
    public void export(@RequestParam(name = "paymentDateTimeStart") String paymentDateTimeStart,
                       @RequestParam(name = "paymentDateTimeEnd") String paymentDateTimeEnd,
                       HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        String fileName = URLEncoder.encode(String.format("%s-(%s).xlsx", "订单支付数据", UUID.randomUUID().toString()),
                StandardCharsets.UTF_8.toString());
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ExcelWriter writer = new ExcelWriterBuilder()
                .autoCloseStream(true)
                .excelType(ExcelTypeEnum.XLSX)
                .file(response.getOutputStream())
                .head(OrderDTO.class)
                .build();
        // xlsx文件上上限是104W行左右,这里如果超过104W需要分Sheet
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName("target");
        long lastBatchMaxId = 0L;
        int limit = 500;
        int sum = 0;
        for (; ; ) {
            log.info("最终优化，当前读取数据总数sum=" + sum);
            List<OrderDTO> list = orderService.queryByScrollingPagination(paymentDateTimeStart, paymentDateTimeEnd, lastBatchMaxId, limit);
            if (list.isEmpty()) {
                writer.finish();
                break;
            } else {
                lastBatchMaxId = list.stream().map(OrderDTO::getId).max(Long::compareTo).orElse(Long.MAX_VALUE);
                writer.write(list, writeSheet);
                sum += list.size();
            }
        }
        log.error("最终优化：导出excel成功，共" + sum + " 条，总耗时 " + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * 初步优化
     * Java堆1g内存，可以下载，能用，相对于服务器直接崩溃这个好一点点，但由于sql查询未使用索引，所以瓶颈在sql查询这一块
     * http://localhost:7070/export1?paymentDateTimeStart=2020-07-01&paymentDateTimeEnd=2020-07-16
     */
    @GetMapping(path = "/export1")
    public void export1(@RequestParam(name = "paymentDateTimeStart") String paymentDateTimeStart,
                        @RequestParam(name = "paymentDateTimeEnd") String paymentDateTimeEnd,
                        HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        String fileName = URLEncoder.encode(String.format("%s-(%s).xlsx", "订单支付数据", UUID.randomUUID().toString()),
                StandardCharsets.UTF_8.toString());
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ExcelWriter writer = new ExcelWriterBuilder()
                .autoCloseStream(true)
                .excelType(ExcelTypeEnum.XLSX)
                .file(response.getOutputStream())
                .head(OrderDTO.class)
                .build();
        // xlsx文件上上限是104W行左右,这里如果超过104W需要分Sheet
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName("target");
        int offset = 0;
        int size = 10000;
        int sum = 0;
        for (; ; ) {
            log.info("初步优化，当前数据位置为offset=" + offset);
            List<OrderDTO> list = orderService.queryOptimize(paymentDateTimeStart, paymentDateTimeEnd, offset, size);
            if (list.isEmpty()) {
                writer.finish();
                break;
            } else {
                offset += size;
                writer.write(list, writeSheet);
                sum += list.size();
            }
        }
        log.error("初步优化：导出excel成功，共 " + sum + " 条，总耗时 " + (System.currentTimeMillis() - startTime) + " ms");
    }

    /**
     * 未优化
     * Java堆1g内存，直接卡死，服务器崩溃，只能重启
     * http://localhost:7070/export2?paymentDateTimeStart=2020-07-01&paymentDateTimeEnd=2020-07-16
     */
    @GetMapping(path = "/export2")
    public void export2(@RequestParam(name = "paymentDateTimeStart") String paymentDateTimeStart,
                        @RequestParam(name = "paymentDateTimeEnd") String paymentDateTimeEnd,
                        HttpServletResponse response) throws Exception {
        long startTime = System.currentTimeMillis();
        String fileName = URLEncoder.encode(String.format("%s-(%s).xlsx", "订单支付数据", UUID.randomUUID().toString()),
                StandardCharsets.UTF_8.toString());
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ExcelWriter writer = new ExcelWriterBuilder()
                .autoCloseStream(true)
                .excelType(ExcelTypeEnum.XLSX)
                .file(response.getOutputStream())
                .head(OrderDTO.class)
                .build();
        // xlsx文件上上限是104W行左右,这里如果超过104W需要分Sheet
        WriteSheet writeSheet = new WriteSheet();
        writeSheet.setSheetName("target");
        List<OrderDTO> list = orderService.queryAll(paymentDateTimeStart, paymentDateTimeEnd);
        writer.write(list, writeSheet);
        writer.finish();
        log.error("未优化：导出excel成功，共 " + list.size() + " 条，总耗时 " + (System.currentTimeMillis() - startTime) + " ms");
    }
}

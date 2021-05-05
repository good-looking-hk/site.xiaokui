package site.xiaokui.spring.test.bean;

import site.xiaokui.spring.bean.annotation.Autowired;

/**
 * @author HK
 * @date 2019-09-01 11:58
 */
public class Room {

    @Autowired
    private Human mathTeacher;

    private Human englishTeacher;

    private Human chineseTeacher;

    /**
     * 由于JDK是面向接口代理的，所以这里是Human
     */
    @Autowired
    public Room(Human englishTeacher) {
        this.englishTeacher = englishTeacher;
    }

    @Autowired
    private void setChineseTeacher(Human chineseTeacher) {
        this.chineseTeacher = chineseTeacher;
    }

    @Override
    public String toString() {
        return this.englishTeacher + "\t" + this.mathTeacher + "\t" + this.chineseTeacher;
    }

    public void showTeacher() {
        System.out.println("数学老师自我介绍：" + this.mathTeacher + "\t英语老师自我介绍：" + this.englishTeacher
                + "\t语文老师自我介绍：" + this.chineseTeacher);
    }

    static class InnerRoom {
        InnerRoom(){}
        @Autowired
        private Human student;
    }
}

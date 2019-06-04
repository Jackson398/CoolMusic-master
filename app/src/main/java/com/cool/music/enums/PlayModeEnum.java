package com.cool.music.enums;

/**
 * 1.自定义枚举类中的括号是用来添加常量额外的参数
 * 2.枚举类中要添加参数对应的域（变量）
 * 3.构造器中输入的参数要与声明的顺序、数量匹配
 * 4.枚举类中域声明为private final，保证安全
 */
public enum PlayModeEnum {
    LOOP(0),
    SHUFFLE(1),
    SINGLE(2);

    private final int value;

    PlayModeEnum(int value) {
        this.value = value;
    }

    public static PlayModeEnum valueOf(int value) {
        switch (value) {
            case 1:
                return SHUFFLE;
            case 2:
                return SINGLE;
            case 0:
            default:
                return LOOP;
        }
    }

    public int value() {
        return value;
    }
}

package com.Itemprocessor_customize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String name;
    private int age;

    //此为定制处理器的User文件
    //根据需求筛选id为偶数的用户

}

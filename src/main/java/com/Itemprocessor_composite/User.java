package com.Itemprocessor_composite;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    @NotBlank(message = "用户名不能为null或空")
    private String name;
    private int age;

    //此为组合处理器的User文件

}

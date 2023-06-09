package com.Itemprocessor_script;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String name;
    private int age;

    //首先先在pom文件中导入SB的validate

}

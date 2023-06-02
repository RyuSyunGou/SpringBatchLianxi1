package com.ItemReader_flat_json;

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


//对应的user.json为标准的json文件，内容为list集合的形式
}

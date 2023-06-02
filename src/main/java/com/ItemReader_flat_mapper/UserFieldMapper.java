package com.ItemReader_flat_mapper;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


//将解析出来的数据进行(自定义)封装。（重写）
public class UserFieldMapper implements FieldSetMapper<User> {
    @Override
    public User mapFieldSet(FieldSet fieldSet) throws BindException {
        User User = new User();
        //自定映射

        User.setId(fieldSet.readLong("id"));
        User.setName(fieldSet.readString("name"));
        User.setAge(fieldSet.readInt("age"));
        String addr = "" + fieldSet.readString("province") + " "
                + fieldSet.readString("city") + "" + fieldSet.readString("area");
        User.setAddress(addr);
        return User;
    }
}


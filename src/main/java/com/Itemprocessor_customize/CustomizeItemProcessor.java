package com.Itemprocessor_customize;

import org.springframework.batch.item.ItemProcessor;

//此为自定处理器
public class CustomizeItemProcessor implements ItemProcessor<User, User> {
    @Override
    public User process(User item) throws Exception {
        return item.getId() % 2 == 0 ? item : null;
    }
}

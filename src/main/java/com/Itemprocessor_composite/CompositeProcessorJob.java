package com.Itemprocessor_composite;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemProcessorAdapter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.BeanValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class CompositeProcessorJob {
    //使用校验处理器
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer


    @Bean
    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(List<?extends User> items) throws Exception {
                items.forEach(System.err::println);

            }
        };
    }


    @Bean
    //参数校验
    public BeanValidatingItemProcessor<User> beanValidatingItemProcessor() {
        BeanValidatingItemProcessor itemProcessor = new BeanValidatingItemProcessor();
        itemProcessor.setFilter(true);//若数据不满足条件，则直接抛弃
        return itemProcessor;

    }


    @Bean
    //已经定义好的 用户名转换类
    //当前的需求为：使用适配处理器调用该类里的toUpperCase来实现用户名的大写转换
    public UserServiceImpl userService(){
        return new UserServiceImpl();
    }


    @Bean
    //需要使用现有类的转换逻辑，将用户名进行转换为大写
    //此为处理逻辑
    public ItemProcessorAdapter<User, User> itemProcessorAdapter(){
        ItemProcessorAdapter<User, User> adapter = new ItemProcessorAdapter<>();
        adapter.setTargetObject(userService());//找到要适配的 逻辑类↑上方的UerServiceImpl，将其导入ItemProcessorAdapter逻辑，并传给↓的processor
        adapter.setTargetMethod("toUpperCase");
        return adapter;
    }


    //将各个步骤组合
    public CompositeItemProcessor<User,User>compositeItemProcessor(){
        CompositeItemProcessor compositeItemProcessor = new CompositeItemProcessor();
        //将多个处理器进行组合在一起，也就是将asLits中的参数校验处理器与适配处理器组合在一起
        compositeItemProcessor.setDelegates(Arrays.asList(beanValidatingItemProcessor(),itemProcessorAdapter()));

        return compositeItemProcessor;


    }







    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                //获取文件或数据/资源
                .resource(new ClassPathResource("users-validate.txt"))
                //解析数据--指定解析器使用 # 来分割，默认为，（逗号）
                .delimited().delimiter("#")
                //截取数据后，以以下方式命名。//delimited 截取
                .names("id", "name", "age")
                //封装数据，将读取的数据封装到user对象中
                .targetType(User.class)
                .build();

    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次性从User中拿多少数据
                .reader(itemReader())
                .processor(compositeItemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("composite-processor-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CompositeProcessorJob.class, args);
    }
}

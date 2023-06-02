package com.ItemReader_flat_json;

import com.ItemReader_flat_mapper.User;
import com.ItemReader_flat_mapper.UserFieldMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.GsonJsonObjectReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class JsonFlatReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer


    @Bean
    public ItemWriter<com.ItemReader_flat_mapper.User> itemWriter() {
        return new ItemWriter<com.ItemReader_flat_mapper.User>() {
            @Override
            //此处的Override为检测所调用的方法是否正确
            public void write(List<? extends com.ItemReader_flat_mapper.User> items) throws Exception {
                items.forEach(System.err::println);
                //此处的err为输出的文字格式类型，err为，在运行时把结果以红色也就是err的格式来输出，较为醒目

            }
        };
    }

    @Bean
    public JsonItemReader<User>itemReader(){

        //此处使用的为Ali的Jackson框架
        //参数：读取json格式文件并转换成具体对象类型：User.class
        JacksonJsonObjectReader<User>jsonObjectReader = new JacksonJsonObjectReader<>(User.class);

        ObjectMapper objectMapper = new ObjectMapper();
        //该ObjectMapper逻辑为构建一个映射规则
        jsonObjectReader.setMapper(objectMapper);
        //构建规则为set
        return new JsonItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("user.json"))
                .jsonObjectReader(jsonObjectReader)
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
                .<com.ItemReader_flat_mapper.User, User>chunk(1)//一次性从User中拿多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("json-flat-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JsonFlatReaderJob.class, args);
    }
}
//读Json文件的ItermReader
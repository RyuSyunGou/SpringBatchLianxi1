package com.ItemReader_db_cursor;

import com.ItemReader_db_cursor.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class CursorDBReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer
    @Autowired
    private DataSource dataSource;
    //告知从数据库sql中拿数据，然后给下面的dataSource


    @Bean
    public ItemWriter<com.ItemReader_db_cursor.User> itemWriter() {
        return new ItemWriter<com.ItemReader_db_cursor.User>() {
            @Override
            //此处的Override为检测所调用的方法是否正确
            public void write(List<? extends com.ItemReader_db_cursor.User> items) throws Exception {
                items.forEach(System.err::println);
                //此处的err为输出的文字格式类型，err为，在运行时把结果以红色也就是err的格式来输出，较为醒目

            }
        };
    }

//    @Bean
//    public JsonItemReader<User>itemReader(){
//
//        //此处使用的为Ali的Jackson框架
//        //参数：读取json格式文件并转换成具体对象类型：User.class
//        JacksonJsonObjectReader<User>jsonObjectReader = new JacksonJsonObjectReader<>(User.class);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        //该ObjectMapper逻辑为构建一个映射规则
//        jsonObjectReader.setMapper(objectMapper);
//        //构建规则为set
//        return new JsonItemReaderBuilder<User>()
//                .name("userItemReader")
//                .resource(new ClassPathResource("user.json"))
//                .jsonObjectReader(jsonObjectReader)
//                .build();
//    }

    @Bean
    public UserRowMapper userRowMapper(){
        return new UserRowMapper();
    }
    //将列数据根据UserRowMapper的映射规则来与对象属性进行一一映射


    @Bean
    public JdbcCursorItemReader<com.ItemReader_db_cursor.User>itemReader(){
        //用jdbc建立起数据库与object之间的关系
        return new JdbcCursorItemReaderBuilder<com.ItemReader_db_cursor.User>()
                .name("userItemReader")
                //从dataSource处拉取数据，已经由Spring容器自己实现去连接数据库
                .dataSource(dataSource)
                //执行sql的查找数据语句并将数据以游标发方式一条一条地传回来
                .sql("select * from user")
                //自定义映射，将从数据库中读出来的数据与用户对象的属性一一映射
                .rowMapper(userRowMapper())
                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次性从User中拿多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("cursor-db-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CursorDBReaderJob.class, args);
    }
}
//读Json文件的ItermReader
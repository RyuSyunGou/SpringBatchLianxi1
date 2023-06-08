package com.ItemReader_db_page;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class PageDBReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer
    @Autowired
    private DataSource dataSource;
    //告知从数据库sql中拿数据，然后给下面的dataSource


    @Bean
    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            //此处的Override为检测所调用的方法是否正确
            public void write(List<? extends User> items) throws Exception {
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
    public PagingQueryProvider pagingQueryProvider() throws Exception {
        //此为分页查询逻辑（方法）
        //没有泛型，所以要作对象出来
        SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
        factoryBean.setDataSource(dataSource);//从此处抓取数据
        factoryBean.setSelectClause("select *");//sql语句
        factoryBean.setFromClause("from user");//拼接来分页查询，方便后续更改与查看
        factoryBean.setWhereClause("where age>:age");//查询的条件,age表示占位符
        factoryBean.setSortKey("id");//以id进行排序

        return factoryBean.getObject();


    }


    @Bean
    //jdbc游标读取
    public JdbcPagingItemReader<User> itemReader() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("age", 16);

        return new JdbcPagingItemReaderBuilder<User>()
                .name("userItemReader")
                .dataSource(dataSource)
                //row映射
                .rowMapper(userRowMapper())
                //分页显示10
                .pageSize(10)
                .queryProvider(pagingQueryProvider()) //分页逻辑
                .parameterValues(map)//sql条件
                .build();

    }

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }
//    将列数据根据UserRowMapper的映射规则来与对象属性进行一一映射


    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step1")
                .<User, User>chunk(1)//一次性从User中拿多少数据
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("page-db-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(PageDBReaderJob.class, args);
    }
}
//读Json文件的ItermReader
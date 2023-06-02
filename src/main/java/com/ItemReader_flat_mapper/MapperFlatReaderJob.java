package com.ItemReader_flat_mapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class MapperFlatReaderJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer


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

    @Bean
    public UserFieldMapper userFieldMapper(){
        return new UserFieldMapper();
    }
    //此为补齐映射逻辑，存进bean里，后续想用时可以直接拿来用

    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                //获取文件或数据/资源
                .resource(new ClassPathResource("user2.txt"))
                //解析数据--指定解析器使用 # 来分割，默认为，（逗号），txt文件用的是#
                .delimited().delimiter("#")
                //截取数据后，以以下方式命名
                .names("id", "name", "age","province","city","area")
                //封装数据，将读取的数据封装到user对象中
//                .targetType(User.class)//此句为自动封装，在这无效
                .fieldSetMapper(userFieldMapper())//映射逻辑去手动封装
                .build();
        //.build为把对象构建build出来了


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
        return jobBuilderFactory.get("mapper-flat-reader-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(MapperFlatReaderJob.class, args);
    }
}

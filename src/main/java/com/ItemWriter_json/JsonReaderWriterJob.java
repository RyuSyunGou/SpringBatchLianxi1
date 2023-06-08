package com.ItemWriter_json;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class JsonReaderWriterJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer


//    @Bean
//    public ItemWriter<User> itemWriter() {
//        return new ItemWriter<User>() {
//            @Override
//            public void write(List<? extends User> items) throws Exception {
//                items.forEach(System.err::println);
//
//            }
//        };
//    }//此处未控制台的输出



    //输出到文件中
//    @Bean
//    public FlatFileItemWriter<User> itemWriter(){
//        return new FlatFileItemWriterBuilder<User>()
//                .name("userFlatItemWriter")//输出的操作名
//                .resource(new PathResource("C:/IDEA/outUser.txt"))//输出的路径
//                .formatted()//要进行格式输出
//                .format("id:%s，姓名:%s,年龄：%s")//规定输出的格式
//                .names("id","name","age")
//                .shouldDeleteIfEmpty(true)//拓展：如果读入数据未空，则输出时候不创建文件（删除空文件）
//                .shouldDeleteIfExists(true)//拓展：如果输出文件已存在，则删除
//                .append(true)//拓展：如果输出文件已存在，则不删除，而是直接追加到现有文件中
//                .build();
//    }

    //输出到json文件
    @Bean
    public JacksonJsonObjectMarshaller<User>jacksonJsonObjectMarshaller(){
        return new JacksonJsonObjectMarshaller<>();
    }

    @Bean
    //Json对象调度器
    public JsonFileItemWriter<User>itemWriter(){
        return new JsonFileItemWriterBuilder<User>()
                .name("userJsonItemWriter")
                .resource(new PathResource("C:/IDEA/outUser.json"))//Json已经规定好格式，无需声明
                //Json对象调度器，将user对象存为json格式，输出到文档中
                .jsonObjectMarshaller(jacksonJsonObjectMarshaller())
                .build();

    }


    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                //获取文件或数据/资源
                .resource(new ClassPathResource("user.txt"))
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
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("json-writer-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(JsonReaderWriterJob.class, args);
    }
}

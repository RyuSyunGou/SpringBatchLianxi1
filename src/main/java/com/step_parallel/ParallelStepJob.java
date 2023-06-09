package com.step_parallel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class ParallelStepJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Bean
    public JsonItemReader<User>jsonItemReader(){
        ObjectMapper objectMapper=new ObjectMapper();
        JacksonJsonObjectReader<User>jsonObjectReader=new JacksonJsonObjectReader<>(User.class);
        jsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<User>()
                .name("userJsonItemReader")
                .jsonObjectReader(jsonObjectReader)
                .resource(new ClassPathResource("user-parallel-json"))
                .build();

    }

    @Bean
    public FlatFileItemReader<User>flatFileItemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("user-parallel.txt"))
                .delimited().delimiter("#")
                .names("id","name","age")
                .targetType(User.class)
                .build();
    }

    @Bean
    public ItemWriter<User>itemWriter(){
        return new ItemWriter<User>() {
            @Override
            public void write(List<?extends User>items) throws Exception {
                items.forEach(System.err::println);

            }
        };
    }


    //以下两个步骤并行处理
    @Bean
  /*  步骤【2】，处理平面文件*/
    public Step flatStep(){
        return stepBuilderFactory.get("flatStep")
                .<User,User>chunk(2)
                .reader(flatFileItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
/*    步骤【3】，处理json文件*/
    public Step jsonStep(){
        return stepBuilderFactory.get("jsonStep")
                .<User,User>chunk(2)
                .reader(jsonItemReader())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public Job job(){

        ///并行操作1：flat文件读取
        Flow flowParallel1 = new FlowBuilder<Flow>("flowParallel1")
                .start(flatStep())
                .build();


        ///并行操作2：json文件读取
        Flow flowParallel2 = new FlowBuilder<Flow>("flowParallel2")
                .start(jsonStep())
                .split(new SimpleAsyncTaskExecutor())//开启线程执行操作
                .add(flowParallel1)//在本【并行操作2】的基础上，add加上【并行操作1】，即同时运行两个并行操作
                .build();

        //若想继续拼接并行3和并行4等，按照这个逻辑往下写即可




        return jobBuilderFactory.get("parallel-step-job")
                .start(flowParallel2)//因为flowParallel1加在了flowParallel2上，也就是flowParallel2包括了flowParallel1，所以此处选flowParallel2
                .end()
                .build();
    }





    public static void main(String[]args){
        SpringApplication.run(ParallelStepJob.class,args);

    }


}
//重点就是用.split(new SimpleAsyncTaskExecutor())来开启线程
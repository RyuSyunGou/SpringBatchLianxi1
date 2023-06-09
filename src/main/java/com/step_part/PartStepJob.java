package com.step_part;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class PartStepJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //从步骤(工作步骤/分区步骤)--读操作
    @Bean
    public FlatFileItemReader<User>itemReader(){
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("user.txt"))
                .delimited().delimiter("#")
                .names("id","name","age")
                .targetType(User.class)
                .build();
    }

    //从步骤(工作步骤/分区步骤)--写操作
    @Bean
    public ItemWriter<User>itemWriter(){
        return new ItemWriter<User>(){
            @Override
            public void write(List<?extends User>items)throws Exception{
                items.forEach(System.err::println);
            }
        };
    }


    //从步骤(工作步骤/分区步骤)--
    @Bean
    public Step workstep(){

        return stepBuilderFactory.get("workStep")
                .<User,User>chunk(10)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

}

package com.step_thread;

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
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.util.List;

//读取user.txt文件并封装进user对象中并打印
@EnableBatchProcessing
@SpringBootApplication
public class ThreadStepJob {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    //job→step--chunk--reader--writer


    @Bean
    public ItemWriter<User> itemWriter() {
        return new ItemWriter<User>() {
            @Override
            public void write(List<? extends User> items) throws Exception {
                items.forEach(System.err::println);

            }
        };
    }

    @Bean
    public FlatFileItemReader<User> itemReader() {
        return new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .saveState(false)//防止状态被覆盖
                //获取文件或数据/资源
                .resource(new ClassPathResource("user-thread.txt"))
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
                //任务执行器，(开启多线程执行)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory.get("thread-step-job")
                .start(step())
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ThreadStepJob.class, args);
    }
}
//注：（1）
//userItemReader（）加上saveState(false),Spring Batch 提供大部分拿到ItemReader是有状态的，作业重启基本通过状态来确定作业停止位置，
//而在多线程环境中，如果对象维护状态被多个线程访问，可能存在线程间状态相互覆盖问题。所以设置为false表示关闭状态，但也也意味着作业不能重启，
//其线程所产生的输出也就不会被重复覆盖

//（2）
//step（）方法加上.taskExecutor(new SimpleAsyncTaskExecutor())为作业步骤添加多线程处理能力，以块chunk为单位，一个块为一个线程，输出的线程顺序为乱序

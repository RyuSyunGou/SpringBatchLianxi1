//package com.param_Validator;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParametersValidator;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.StepContribution;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
//import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.scope.context.ChunkContext;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Bean;
//
//import java.util.Map;
//
//@SpringBootApplication
////为启动注解，保证为SB的启动类
//@EnableBatchProcessing
////告诉SB的容器加载Batch的环境，启动SB Batch逻辑
//public class ParamValidatorJob {
//    //job调度器→启动job
//    @Autowired
//    private JobLauncher jobLauncher;
//
//    //job构造器工厂→构建Job对象
//    @Autowired
//    private JobBuilderFactory jobBuilderFactory;
//
//    //step构造器工厂→构建Step的对象
//    @Autowired
//    private StepBuilderFactory stepBuilderFactory;
//
//    //构建一个step对象执行的任务（构建逻辑对象）
//    @Bean
//    public Tasklet tasklet() {
//        return new Tasklet() {
//            @Override
//            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
//
//                Map<String,Object> jobParameters = chunkContext.getStepContext().getJobParameters();
//
//                System.out.println("param-name:" + jobParameters.get("name"));
//                //根据需求，用batch打印出HELLO等字样,即为Step要实现的逻辑
//                return RepeatStatus.FINISHED;
//                //RepeatStatus.FINISHED;→告知这个打印hello的batch完成了。执行完了
//                //Tasklet{}里面这些动作即为要执行的step步骤的逻辑上
//            }
//        };
//    }
//
//
//    //构建一个step对象
//
//    @Bean
//    //创建一个对象并交由Bean管理
//    public Step step1() {
//        //先构建一个Step，然后Step里面含有step1
//
//        return stepBuilderFactory.get("step1").tasklet(tasklet()).build();
//        //build()为与step相关的标准构造器逻辑，然后在↑加上return,返回，即→运行step1，返回build构建出来的东西
//        //tasklet 执行step逻辑，由step逻辑构成一个大的Step
//        //tasklet里面的tasklet为上面打印HELLO的逻辑，把其放进step1里去执行
//
//    }
//
//    @Bean
//    public NameParamValidator nameParamValidator(){
//        return new NameParamValidator();
//    }
//
//    //构造完step后，构造以step组成的Job
//    @Bean
//    public Job job() {
//        return jobBuilderFactory.get("name-param-validate-job")
//                .start(step1())
//                .validator((JobParametersValidator) nameParamValidator())//指定参数校验器
//                .build();
//        //构建job，然后该job用start来启动上面已经构造好的step1来构建job
//        //然后用return来返回 这个job构造出来的东西
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(ParamValidatorJob.class, args);
//        //启动整个HelloJob，来运行其中构建job和构建step的内容
//    }
//}
//
///*跑动顺序：先跑起来，启动 SpringApplication容器，→该Spring容器去找上面的Bean以及Bean里面的东西
//→找到Bean里的东西,先会找到Job，然后创建Job出来，根据Job里面的逻辑也就是start逻辑
// →去找Step→然后根据step1的内容找到跑上方tasklet内容里的东西即为打印HellO World*//*
//
//
//
//
//
//*/
///*
// jobLauncher--Job--Step
//        ↓      ↓    ↓
//         JobRepository
//*//*
//

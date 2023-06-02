//package com.param_Validator;
//
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.util.StringUtils;
//
////当name值为null或空串时，校验不通过
//public class NameParamValidator {
//    //参数校验器示例，对nam进行参数校验
//    @Override
//    public void validate(JobParameters parameters) throws JobParametersInvalidException{
//        String name = parameters.getString("name");
//        if (!StringUtils.hasText(name)){
//            throw new JobParametersInvalidException("name 参数不能为空");
//        }
//    }
//
//
//}

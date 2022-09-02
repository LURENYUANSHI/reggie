package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//全局异常处理器
@Slf4j
@RestControllerAdvice()
public class GlobalExceptionHandler {
    //异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String>ExceptionHandler(SQLIntegrityConstraintViolationException ex){
    log.error(ex.getMessage());

    if(ex.getMessage().contains("Duplicate entry")){
        String[] s = ex.getMessage().split(" ");
        String msg=s[2]+"已存在";
      return  R.error(msg);
    }


    return R.error("未知错误");
    }
    //异常处理方法
    @ExceptionHandler(CustomException.class)
    public R<String>ExceptionHandler(CustomException ex){
        log.error(ex.getMessage());

        return R.error(ex.getMessage());
    }


}

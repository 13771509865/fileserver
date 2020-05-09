package com.yozosoft.fileserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoufeng
 * @description 全局异常处理, 只能捕获controller抛出的
 * @create 2020-05-06 14:53
 **/
@ControllerAdvice
@Slf4j
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity argValidExceptionHandler(BindException e) {
        List<ObjectError> allErrors = e.getAllErrors();
        List<String> message = new ArrayList<>();
        for (ObjectError error : allErrors) {
            message.add(error.getDefaultMessage());
        }
        return ResponseEntity.ok("参数错误");
    }


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity defaultExcepitonHandler(Exception ex) {
        log.error("全局异常处理器捕获异常,请检查", ex);
        return ResponseEntity.ok("服务器未知错误");
    }

}

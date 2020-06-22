package com.yozosoft.fileserver.exception;

import com.yozosoft.common.exception.YozoServiceException;
import com.yozosoft.fileserver.constants.EnumResultCode;
import com.yozosoft.fileserver.utils.JsonResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
        return ResponseEntity.ok(JsonResultUtils.buildMapResult(EnumResultCode.E_INVALID_PARAM.getValue(), null, StringUtils.join(message, ",")));
    }

    @ExceptionHandler(YozoServiceException.class)
    @ResponseBody
    public ResponseEntity yozoExceptionHandler(YozoServiceException e) {
        return new ResponseEntity(JsonResultUtils.buildMapResult(e.getCode(), null, e.getMessage()), HttpStatus.valueOf(200));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity maxFileExceptionHandler(MaxUploadSizeExceededException e) {
        return ResponseEntity.ok(JsonResultUtils.buildMapResultByResultCode(EnumResultCode.E_UPLOAD_FILE_SIZE_OVERSIZE));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity defaultExcepitonHandler(Throwable ex) {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), HttpStatus.METHOD_NOT_ALLOWED);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        } else if (ex instanceof HttpMessageNotReadableException || ex instanceof MissingServletRequestParameterException) {
            return new ResponseEntity<>(EnumResultCode.E_INVALID_PARAM.getInfo(), HttpStatus.BAD_REQUEST);
        }
        log.error("全局异常处理器捕获异常,请检查", ex);
        return ResponseEntity.ok(JsonResultUtils.buildMapResultByResultCode(EnumResultCode.E_SERVER_UNKNOWN_ERROR));
    }

}

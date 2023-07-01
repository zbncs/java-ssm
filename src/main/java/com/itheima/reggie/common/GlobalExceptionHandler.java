package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 *
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());

        if (exception.getMessage().contains("Duplicate entry")) {
            String[] s = exception.getMessage().split(" ");
            String mes = s[2] + "已存在";
            return R.error(mes);
        }

        return R.error("失败了");
    }
}

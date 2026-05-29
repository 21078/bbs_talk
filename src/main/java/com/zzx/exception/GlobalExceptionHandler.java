package com.zzx.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * 全局异常处理器
 * 处理应用程序中所有未捕获的异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * 捕获所有异常并将错误信息显示在错误页面上
     *
     * @param e 异常对象
     * @param model Spring MVC模型对象
     * @return 错误页面视图名称
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHander(Exception e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}


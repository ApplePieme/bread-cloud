package com.breadme.breadcloud.handler;

import com.breadme.breadcloud.exception.BreadCloudException;
import com.breadme.breadcloud.util.R;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 17:01
 */
@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R fail(Exception e) {
        log.info("全局异常处理: \n", e);
        return R.fail().message("服务器出错啦!请稍后重试!");
    }

    @ResponseBody
    @ExceptionHandler(BreadCloudException.class)
    public R fail(BreadCloudException e) {
        log.info("自定义异常: \n", e);
        return R.fail().message(e.getMessage()).code(e.getCode());
    }
}

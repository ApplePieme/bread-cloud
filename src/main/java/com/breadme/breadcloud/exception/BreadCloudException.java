package com.breadme.breadcloud.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BreadCloudException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;
}

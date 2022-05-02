package com.breadme.breadcloud.entity.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

/**
 * 用户 DTO 对象
 *
 * @author breadme@foxmail.com
 * @date 2022/5/2 16:39
 */
@Data
@FieldNameConstants
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String srcPassword;

    private String newPassword;
}

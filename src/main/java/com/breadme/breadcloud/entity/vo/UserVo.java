package com.breadme.breadcloud.entity.vo;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户 VO 对象
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 1:37
 */
@Data
@FieldNameConstants
public class UserVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String nickname;

    private String phone;

    private String avatar;

    private Date gmtCreate;

    private Date gmtModified;
}

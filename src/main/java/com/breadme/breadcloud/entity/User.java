package com.breadme.breadcloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 15:53
 */
@Data
@FieldNameConstants
@TableName("user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @TableField("username")
    private String username;

    @TableField("nickname")
    private String nickname;

    @NotBlank(message = "密码不能为空")
    @Length(min = 8, message = "请输入8位以上的密码")
    @TableField("password")
    private String password;

    @TableField("phone")
    private String phone;

    @TableField("avatar")
    private String avatar;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}

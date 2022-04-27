package com.breadme.breadcloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

/**
 * 目录实体
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 15:53
 */
@Data
@FieldNameConstants
@TableName("directory")
public class Directory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("parent_id")
    private Long parentId;

    @TableField("name")
    private String name;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}

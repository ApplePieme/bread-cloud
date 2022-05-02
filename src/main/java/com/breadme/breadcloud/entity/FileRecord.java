package com.breadme.breadcloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件记录实体
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 15:53
 */
@Data
@FieldNameConstants
@TableName("file_record")
public class FileRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("file_id")
    private Long fileId;

    @TableField("directory_id")
    private Long directoryId;

    @TableField("file_name")
    private String fileName;

    @TableField("extension_name")
    private String extensionName;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create", fill = FieldFill.INSERT)
    private Date gmtCreate;

    @TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}

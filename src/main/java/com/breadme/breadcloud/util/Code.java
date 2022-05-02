package com.breadme.breadcloud.util;

/**
 * 各种状态码
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:25
 */
public interface Code {
    /**
     * 请求成功
     */
    Integer SUCCESS = 200;

    /**
     * 请求失败
     */
    Integer FAIL = 400;

    /**
     * 参数非法
     */
    Integer PARAM_ILLEGAL = 401;

    /**
     * 鉴权失败
     */
    Integer AUTH_FAIL = 402;

    /**
     * 用户已存在
     */
    Integer EXISTING_USER = 403;

    /**
     * 文件已上传到服务器本地
     */
    Integer LOCAL_UPLOADED = 1;

    /**
     * 文件已上传到 HDFS
     */
    Integer HDFS_UPLOADED = 2;
}

package com.breadme.breadcloud.util;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回对象
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:22
 */
@Data
public class R {
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private Map<String, Object> data = new HashMap<>();

    private R() {

    }

    public static R success() {
        R r = new R();
        r.setSuccess(true);
        r.setCode(Code.SUCCESS);
        r.setMessage("成功");
        return r;
    }

    public static R fail() {
        R r = new R();
        r.setSuccess(false);
        r.setCode(Code.FAIL);
        r.setMessage("失败");
        return r;
    }

    public R success(Boolean b) {
        this.setSuccess(b);
        return this;
    }

    public R message(String msg) {
        this.setMessage(msg);
        return this;
    }

    public R code(Integer code) {
        this.setCode(code);
        return this;
    }

    public R data(String key, Object val) {
        this.data.put(key, val);
        return this;
    }

    public R data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}

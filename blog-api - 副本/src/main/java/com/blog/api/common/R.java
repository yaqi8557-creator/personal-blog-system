package com.blog.api.common;

import lombok.Data;

@Data
public class R<T> {
    private Integer code;
    private String message;
    private T data;

    private R() {}

    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = "操作成功";
        r.data = data;
        return r;
    }

    public static <T> R<T> success(String message, T data) {
        R<T> r = new R<>();
        r.code = 200;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> R<T> error(String message) {
        R<T> r = new R<>();
        r.code = 500;
        r.message = message;
        r.data = null;
        return r;
    }

    public static <T> R<T> error(Integer code, String message) {
        R<T> r = new R<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }
}

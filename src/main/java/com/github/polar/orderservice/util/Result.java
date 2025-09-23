package com.github.polar.orderservice.util;

public record Result<T, E>(T data, E error) {

    public static <T, E> Result<T, E> ok(T data) {
        return new Result<>(data, null);
    }

    public static <T, E> Result<T, E> error(E err) {
        return new Result<>(null, err);
    }

    public boolean isOk() {
        return data != null;
    }

    public T ok() {
        if (!isOk()) {
            throw new IllegalStateException("Result is not ok");
        }
        return data;
    }

    public E err() {
        if (!isError()) {
            throw new IllegalStateException("Result is not error");
        }
        return error;
    }

    public boolean isError() {
        return error != null;
    }
}

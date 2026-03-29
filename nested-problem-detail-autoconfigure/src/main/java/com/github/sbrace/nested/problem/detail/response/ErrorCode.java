package com.github.sbrace.nested.problem.detail.response;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ErrorCode {
    public static final String PRE_FIX = "A00";
    public static final String UNKNOW = "000";

    public static String httpStatusCode(@Nullable HttpStatusCode httpStatusCode) {
        return httpStatusCode(httpStatusCode, PRE_FIX);
    }

    public static String httpStatusCode(@Nullable HttpStatusCode httpStatusCode, String prefix) {
        if (null == httpStatusCode) {
            return prefix + UNKNOW;
        }
        return prefix + httpStatusCode.value();
    }

    public static String httpStatusValue(int httpStatusValue) {
        HttpStatus resolve = HttpStatus.resolve(httpStatusValue);
        return httpStatusCode(resolve);
    }
}

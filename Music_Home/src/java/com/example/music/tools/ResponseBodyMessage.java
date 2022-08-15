package com.example.music.tools;

import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @author qiu
 * @version 1.8.0
 */
@Data
@SuppressWarnings("all")
public class ResponseBodyMessage<T> {
    //状态码
    private int status;
    private String message;
    //响应正文
    private T data;

    public ResponseBodyMessage(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}

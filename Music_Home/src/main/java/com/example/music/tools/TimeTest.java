package com.example.music.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qiu
 * @version 1.8.0
 */
public class TimeTest {
    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = simpleDateFormat.format(new Date());

        System.out.println(time);
    }
}

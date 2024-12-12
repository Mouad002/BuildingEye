package com.example.buildingeye.functional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MyConfig {
    public static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("src\\main\\resources\\com\\example\\buildingeye\\config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

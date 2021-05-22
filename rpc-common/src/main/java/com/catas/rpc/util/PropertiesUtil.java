package com.catas.rpc.util;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Slf4j
public final class PropertiesUtil {

    public static Properties readProperties(String fileName) {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String configPath = "";
        if (url != null) {
            configPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configPath), StandardCharsets.UTF_8);
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (IOException e) {
            log.error("读取配置文件失败: {}", fileName);
        }
        return properties;
    }
}

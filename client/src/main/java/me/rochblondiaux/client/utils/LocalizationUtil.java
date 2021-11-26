package me.rochblondiaux.client.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Localization Util")
public class LocalizationUtil {

    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream inputStream = LocalizationUtil.class.getResourceAsStream("/localization.properties");
            if (Objects.isNull(inputStream))
                throw new FileNotFoundException("Internalization file not found in the classpath");
            else
                properties.load(inputStream);
        } catch (IOException e) {
            log.error("Couldn't load internalization file from classpath!", e);
        }
    }

    public static String getLocalization(String key) {
        return properties.getProperty(key, "unknown");
    }
}

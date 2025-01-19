package com.itguigu.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

public class RandomStringGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RandomStringGenerator.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int STRING_LENGTH = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static void main(String[] args) {
        String randomString = generateRandomString(STRING_LENGTH);
        logger.info("Generated Random String: " + randomString);
    }

    public static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
}

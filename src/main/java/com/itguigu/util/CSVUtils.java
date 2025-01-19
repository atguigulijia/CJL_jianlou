package com.itguigu.util;
/**
 * @Author: lijia
 * @Description: TODO
 * @CreateTime: 2024/3/13 19:27
 **/

import com.itguigu.model.GoodsCategoryInfo;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVUtils {

    public static <T> void writeToCSV(Class<T> clazz, List<T> data, String path, String sep) throws IOException, ReflectiveOperationException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            // 写入表头
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                writer.print(fields[i].getName());
                if (i < fields.length - 1) {
                    writer.print(sep);
                }
            }
            writer.println();

            // 写入数据
            for (T obj : data) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(obj);
                    if (value instanceof Map) {
                        // 如果是Map类型的属性，只写入map的key-value
                        Map<String, Object> map = (Map<String, Object>) value;
                        StringBuilder sb = new StringBuilder();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            sb.append("<").append(entry.getKey()).append(",").append(entry.getValue()).append(">");
                        }
                        writer.print(sb.toString());
                    } else {
                        //空字符串处理
                        if (value instanceof String && ((String) value).isEmpty()) {
                            writer.print("null");
                        } else {
                            writer.print(value);
                        }
                    }
                    writer.print(i == fields.length - 1 ? "" : sep);
                }
                writer.println();
            }
        }
    }


    public static <T> List<T> readFromCSV(Class<T> clazz, String path, String sep) throws IOException, ReflectiveOperationException {
        List<T> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            // 读取表头
            String line = reader.readLine();
            String[] headers = line.split(sep);

            // 读取数据
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(sep);
//                System .out.println(values.length);
//                System.out.println(Arrays.toString(values));
                T obj = clazz.getDeclaredConstructor().newInstance();
                for (int i = 0; i < headers.length; i++) {
                    Field field = clazz.getDeclaredField(headers[i]);
                    field.setAccessible(true);
                    setValue(obj, field, values[i]);
                }
                data.add(obj);
            }
        }
        return data;
    }

    private static void setValue(Object obj, Field field, String value) throws ReflectiveOperationException {
        Class<?> fieldType = field.getType();
        if (value == null || value.isEmpty() || "null".equals(value)) {
            field.set(obj, null);
        } else if (fieldType == String.class) {
            field.set(obj, value);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            field.set(obj, Integer.parseInt(value));
        } else if (fieldType == AtomicInteger.class) {
            field.set(obj, new AtomicInteger(Integer.parseInt(value)));
        } else if (fieldType == Long.class || fieldType == long.class) {
            field.set(obj, Long.parseLong(value));
        } else if (fieldType == Double.class || fieldType == double.class) {
            field.set(obj, Double.parseDouble(value));
        } else if (fieldType == Map.class) {
            Map<String, Object> map = new HashMap<>();
            String[] pairs = value.split("><");
            for (String pair : pairs) {
                pair = pair.replaceAll("[<>]", "");
                String[] keyValue = pair.split(",");
                map.put(keyValue[0], keyValue[1]);
            }
            field.set(obj, map);
        } else if (fieldType == List.class) {
            List<Object> list = new ArrayList<>();
            String[] elements = value.split("><");
            for (String element : elements) {
                element = element.replaceAll("[<>]", "");
                list.add(element);
            }
            field.set(obj, list);
        } else if (fieldType == GoodsCategoryInfo.class) {
            // 特殊处理 GoodsCategoryInfo 类型
            field.set(obj, GoodsCategoryInfo.fromString(value));
        } else {
            throw new IllegalArgumentException("Unsupported field type: " + fieldType);
        }
    }

    public static <T> void writeToCSV(Class<T> clazz, List<T> data, String path, String sep, String... headersToInclude) throws IOException, ReflectiveOperationException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            // 写入表头
            for (int i = 0; i < headersToInclude.length; i++) {
                String header = headersToInclude[i];
                writer.print(header);
                if (i < headersToInclude.length - 1) {
                    writer.print(sep);
                }
            }
            writer.println();

            // 写入数据
            for (T obj : data) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < headersToInclude.length; i++) {
                    String header = headersToInclude[i];
                    Field field = clazz.getDeclaredField(header);
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value == null || "".equals(value) ) {
                        sb.append("null");
                    } else if (field.getType() == Map.class) {
                        Map<?, ?> map = (Map<?, ?>) value;
                        //空判断
                        if (map.isEmpty() || map==null){
                            sb.append("null");
                        }
                        StringBuilder mapSB = new StringBuilder();
                        for (Map.Entry<?, ?> entry : map.entrySet()) {
                            mapSB.append("<").append(entry.getKey()).append(",").append(entry.getValue()).append(">");
                        }
                        sb.append(mapSB.toString());
                    } else if (field.getType() == List.class) {
                        List<?> list = (List<?>) value;
                        StringBuilder listSB = new StringBuilder();
                        for (Object listItem : list) {
                            listSB.append("<").append(listItem).append(">");
                        }
                        sb.append(listSB.toString());
                    } else {
                        sb.append(value);
                    }
                    if (i < headersToInclude.length - 1) {
                        sb.append(sep);
                    }
                }
                writer.println(sb.toString());
            }
        }
    }


    public static boolean isValidCSVFile(String filePath) {
        String fileExtension = getFileExtension(filePath);
        return fileExtension.equals("csv");
    }

    private static String getFileExtension(String filePath) {
        int lastIndexOfDot = filePath.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // No extension found
        }
        return filePath.substring(lastIndexOfDot + 1).toLowerCase();
    }


    private static boolean contains(String[] array, String value) {
        for (String str : array) {
            if (str.equals(value)) {
                return true;
            }
        }
        return false;
    }




}

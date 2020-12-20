package org.diplom.blog.dto;

import java.util.Arrays;

/**
 * @author Andrey.Kazakov
 * @date 20.12.2020
 */
public enum StorageType {
    LOCAL("local"),
    CLOUD("cloud");

    private final String type;

    private StorageType(String type){
        this.type = type;
    }

    public static StorageType StorageType(String type) throws Exception {
        return Arrays.stream(StorageType.values())
                .filter(v -> v.toString().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new Exception(String.format("В StorageType отсутствует значение '%s'", type)));
    }
}

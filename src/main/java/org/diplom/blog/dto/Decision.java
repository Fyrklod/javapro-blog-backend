package org.diplom.blog.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public enum Decision {
    @JsonProperty("decline")
    DECLINE("decline"),
    @JsonProperty("accept")
    ACCEPT("accept");

    private final String decision;

    private Decision(String decision){
        this.decision = decision;
    }

    @JsonCreator
    public static Decision fromString(String decision) throws Exception {
        return Arrays.stream(Decision.values())
                .filter(v -> v.toString().equals(decision))
                .findFirst()
                .orElseThrow(() -> new Exception(String.format("В Decision отсутствует значение '%s'", decision)));
    }

    @Override
    public String toString() {
        return decision;
    }
}

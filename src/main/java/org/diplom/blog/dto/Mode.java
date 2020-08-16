package org.diplom.blog.dto;

import lombok.SneakyThrows;

public enum Mode {
    RECENT("recent"),
    POPULAR("popular"),
    BEST("best"),
    EARLY("early");

    private String mode;

    private Mode(String mode){
        this.mode = mode;
    }

    public String toString(){
        return mode;
    }

    @SneakyThrows
    public static Mode fromString(String mode){
        switch (mode) {
            case "recent": return RECENT;
            case "popular": return POPULAR;
            case "best": return BEST;
            case "early": return EARLY;
            default:
                throw new Exception(String.format("В Mode отсутствует значение '%s'",mode) );
        }
    }
}

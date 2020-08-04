package org.diplom.blog.model;

public enum Mode {
    RECENT("recent "),
    POPULAR("popular"),
    BEST("best"),
    EARLY("early");

    private String mode;

    Mode(String mode){
        this.mode = mode;
    }
}

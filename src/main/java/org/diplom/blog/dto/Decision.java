package org.diplom.blog.dto;

public enum Decision {
    DECLINE("decline"),
    ACCEPT("accept");

    private String decision;

    private Decision(String decision){
        this.decision = decision;
    }
}

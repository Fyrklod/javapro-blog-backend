package org.diplom.blog.dto.model;

public enum Decision {
    DECLINE("decline"),
    ACCEPT("accept");

    private String decision;

    Decision(String decision){
        this.decision = decision;
    }
}

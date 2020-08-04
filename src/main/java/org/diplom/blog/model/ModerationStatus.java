package org.diplom.blog.model;

public enum ModerationStatus {
    NEW("new"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    private String status;

    ModerationStatus(String status){
        this.status = status;
    }
}

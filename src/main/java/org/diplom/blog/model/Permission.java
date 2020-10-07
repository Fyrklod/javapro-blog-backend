package org.diplom.blog.model;

/**
 * @author Andrey.Kazakov
 * @date 02.10.2020
 */
public enum Permission {
    READ("user:reader"),
    WRITE("user:writer"),
    APPROVE("user:approver");

    private final String permission;

    private Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}

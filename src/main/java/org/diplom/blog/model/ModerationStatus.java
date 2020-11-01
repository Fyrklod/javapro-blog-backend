package org.diplom.blog.model;

import lombok.SneakyThrows;
import org.diplom.blog.dto.PostStatus;

import java.util.Arrays;

public enum ModerationStatus {
    NEW("new"),
    ACCEPTED("accepted"),
    DECLINED("declined");

    private String status;

    private ModerationStatus(String status){
        this.status = status;
    }

    @Override
    public String toString(){
        return status;
    }

    /*@SneakyThrows
    public static ModerationStatus fromString(String status){
        switch (status) {
            case "new": return NEW;
            case "accepted": return ACCEPTED;
            case "declined": return DECLINED;
            default:
                throw new Exception("Данного значения не существует");
        }
    }*/
    @SneakyThrows
    public static ModerationStatus fromString(String status) {
        return Arrays.stream(ModerationStatus.values())
                .filter(v -> v.toString().equals(status))
                .findFirst()
                .orElseThrow(() -> new Exception(String.format("В ModerationStatus отсутствует значение '%s'", status)));
    }

    @SneakyThrows
    public static ModerationStatus fromPostStatus(PostStatus status){
        switch (status) {
            case PENDING: return NEW;
            case PUBLISHED: return ACCEPTED;
            case DECLINED: return DECLINED;
            default:
                throw new Exception("Данного значения не существует");
        }
    }
}

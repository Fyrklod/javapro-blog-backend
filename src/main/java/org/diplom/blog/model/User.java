package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_moderator", nullable = false)
    private boolean isModerator;

    @CreationTimestamp
    @Column(name = "reg_time", nullable = false,
            columnDefinition = "timestamp with time zone")
    private LocalDateTime regTime;

    @Column(name = "name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "code")
    private String code;

    @Column(name = "photo", columnDefinition="TEXT")
    private String photo;

    public Role getRole() {
        return isModerator ? Role.MODERATOR : Role.USER;
    }
}

package org.diplom.blog.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "time", nullable = false,
            columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "secret_code", nullable = false)
    private String secretCode;
}

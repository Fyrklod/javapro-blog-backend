package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.DatabaseMetaData;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "secret_code", nullable = false)
    private String secretCode;
}

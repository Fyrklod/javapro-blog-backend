package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="post_id")
    private Long postId;

    @CreationTimestamp
    @Column(name = "time", nullable = false,
            columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    @Column(name = "value", nullable = false)
    private Integer value;

    public PostVote (Long postId, Long userId, Integer value) {
        this.postId = postId;
        this.userId = userId;
        this.value = value;
    }
}

package org.diplom.blog.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="parent_id")
    private PostComment parent;

    @OneToOne
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User author;

    @CreationTimestamp
    @Column(name = "time", nullable = false,
            columnDefinition = "timestamp with time zone")
    private LocalDateTime time;

    @Column(name = "text", nullable = false, columnDefinition="TEXT")
    private String text;
}

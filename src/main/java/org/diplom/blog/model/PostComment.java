package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User author;

    @Column(name = "time", nullable = false)
    private Date time;

    @Column(name = "text", nullable = false, columnDefinition="TEXT")
    private String text;
}

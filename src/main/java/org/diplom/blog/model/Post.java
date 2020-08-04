package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="moderator_id")
    private User moderator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    private User author;

    @Column(name = "time", nullable = false)
    private Date date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false, columnDefinition="TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PostVote> votes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> postComments;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tag2post",
               joinColumns = {@JoinColumn(name = "post_id")},
               inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;
}

package org.diplom.blog.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @Basic
    @Column(name = "moderation_status", nullable = false)
    private String moderationStatusValue;

    @Transient
    private ModerationStatus moderationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="moderator_id")
    private User moderator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User author;

    @CreationTimestamp
    @Column(name = "time", nullable = false,
            columnDefinition = "timestamp with time zone")
    private Date date;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "text", nullable = false, columnDefinition="TEXT")
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostVote> votes;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostComment> postComments;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "tag2post",
               joinColumns = {@JoinColumn(name = "post_id")},
               inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    private List<Tag> tags;

    @PostLoad
    private void fillTransient(){
        if(!StringUtils.isEmpty(moderationStatusValue)){
            this.moderationStatus = ModerationStatus.fromString(moderationStatusValue);
        }
    }

    @PrePersist
    private void fillPersistent(){
        if(moderationStatus!=null){
            this.moderationStatusValue = moderationStatus.toString();
        }
    }
}

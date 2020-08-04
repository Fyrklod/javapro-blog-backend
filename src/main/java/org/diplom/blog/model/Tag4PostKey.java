package org.diplom.blog.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Tag4PostKey implements Serializable {
    @Column(name="post_id")
    private Long postId;

    @Column(name="tag_id")
    private Long tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag4PostKey that = (Tag4PostKey) o;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, tagId);
    }
}

package org.diplom.blog.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/*@Getter
@Setter*/
@Data
@NoArgsConstructor
@Entity
@Table(name = "tag2post")
public class Tag2Post implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EmbeddedId
    private Tag4PostKey tag4PostKey;
}

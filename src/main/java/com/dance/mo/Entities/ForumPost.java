package com.dance.mo.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ForumPost implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    private String title;
    private String content;
    private LocalDate postDate;
    @JsonIgnore
    @OneToMany( cascade = CascadeType.ALL,mappedBy = "forumPost")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    private User author;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ForumPost")
    private Set<React> reacts ;

}

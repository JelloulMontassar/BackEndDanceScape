package com.dance.mo.Entities;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    private String content;
    private LocalDate commentDate;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "comment")
    private List<ReactComment> reactComments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentComment")
    private List<SousComment> sousComments = new ArrayList<>();

    @ManyToOne
    private ForumPost forumPost ;
}

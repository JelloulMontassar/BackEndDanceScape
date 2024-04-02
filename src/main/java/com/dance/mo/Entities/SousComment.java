package com.dance.mo.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Entity
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SousComment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scommentId;
    private String content;
    private LocalDate commentDate;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment parentComment;
}

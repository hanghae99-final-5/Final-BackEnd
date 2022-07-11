package com.hanghae.todoli.matching;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long requesterId;

    @Column
    private Long respondentId;

    public Matching(Long requesterId, Long respondentId) {
        this.requesterId = requesterId;
        this.respondentId = respondentId;
    }
}

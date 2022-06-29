package com.hanghae.todoli.models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    private Long googleId;

    @Column
    private Boolean matchingState;

    @OneToOne(fetch = FetchType.LAZY)
    private Character character;

    @OneToMany
    private List<Alarm> alarms = new ArrayList<>();

    @OneToMany
    private List<Todo> todos = new ArrayList<>();

    public Member(String username, String nickname, String password, Boolean matchingState, Character character) {
        this.character = character;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.matchingState = matchingState;
    }
}

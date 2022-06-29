package com.hanghae.todoli.security.jwt;

import com.hanghae.todoli.models.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final Member Member;

    public UserDetailsImpl(Member Member) {
        this.Member = Member;
    }

    public UserDetailsImpl(Member member) {
        this.member = member;
    }

    public Member getMember() {
        return Member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return Member.getPassword();
    }

    @Override
    public String getUsername() {
        return Member.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}

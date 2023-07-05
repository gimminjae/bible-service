package com.example.bo.member.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@JsonIncludeProperties({"nickname", "role", "createDateTime"}) //"memId", "username"
public class AuthUser extends User {
    private final String memId;
    private final String nickname;
    private final String username;
    private final LocalDateTime createDateTime;
    private String role;
    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String memId, String nickname, String username1, LocalDateTime createDateTime) {
        super(username, password, authorities);
        this.memId = memId;
        this.nickname = nickname;
        this.username = username1;
        this.createDateTime = createDateTime;
    }

    public AuthUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String memId, String nickname, String username1, LocalDateTime createDateTime) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.memId = memId;
        this.nickname = nickname;
        this.username = username1;
        this.createDateTime = createDateTime;
    }
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return super.getAuthorities().stream().collect(Collectors.toSet());
    }
    public AuthUser(Member member, List<GrantedAuthority> authorities) {
        super(member.getMemId(), member.getPassword(), authorities);
        this.memId = member.getMemId();
        this.nickname = member.getNickname();
        this.username = member.getUsername();
        this.role = authorities.get(0).toString();
        this.createDateTime = member.getCreateDateTime();
    }
}

package com.example.projectv1.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data   //it will generate setter, getter, to string
@Builder    //build object
@NoArgsConstructor
@AllArgsConstructor //the constructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue //it will set the strategy to AUTO
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

//    after implementing UserDetails, add role => class type = ENUM, also add the annotation
    @Enumerated(EnumType.ORDINAL) //let JPA return value
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); // it will return role name (ADMIN and USER) ordered by number because it was ordinal
    }

    @Override
    public String getUsername() {
        return email; // set email as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // should be true so the user not expired
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // samee
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // same
    }

    @Override
    public boolean isEnabled() {
        return true; // same
    }
}

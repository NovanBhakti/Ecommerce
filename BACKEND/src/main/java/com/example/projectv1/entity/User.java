package com.example.projectv1.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data   //it will generate setter, getter, to string
@Builder    //build object
@NoArgsConstructor
@AllArgsConstructor //the constructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

    @OneToOne(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "profile_picture_id")
    private ProfilePicture profilePicture;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "forgot_password_id")
    private ResetPassword resetPassword;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "email_verification_id")
    private EmailVerification emailVerification;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> userAddress = new ArrayList<>();

    @Id
    @GeneratedValue
    private Integer id;

    @Getter
    private String firstName;
    @Getter
    private String lastName;

    private LocalDate dob;

    private String gender;

    private String email;

    private String password;

//    after implementing UserDetails, add role => class type = ENUM, also add the annotation
    @Enumerated(EnumType.STRING) //let JPA return value
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

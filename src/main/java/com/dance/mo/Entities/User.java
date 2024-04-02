package com.dance.mo.Entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;

    private Date birthday;
    @NonNull
    @Email
    private String email;
    @NonNull
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;
    private long resetToken;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    private  byte[] profileImage;
    @Transient
    private static byte[] defaultProfileImage;
    private Integer phoneNumber;




    ///////REL
    @OneToMany(cascade= CascadeType.ALL, mappedBy = "author")
    private List<ForumPost> forumPosts;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creator")
    private List<ChatRoom> chatRooms;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "dsUsers")
    private  List<DanceSchool> danceSchools;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "buyer")
    private Ticket ticket;

    @OneToMany (mappedBy = "competitor",cascade = CascadeType.ALL)
    private List<Competition> competitions;

    @ManyToMany(cascade = CascadeType.ALL , mappedBy = "resUsers")
    private List<Result> results;


////////
///return list of roles

    ///get all the authoroties from roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return  List.of(new SimpleGrantedAuthority(role.name()));

    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    static {
        try {
            defaultProfileImage = StreamUtils.copyToByteArray(
                    Objects.requireNonNull(User.class.getClassLoader().getResourceAsStream("mohamedimage/profileImage.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @PrePersist
    public void prePersist() {
        if (profileImage == null || profileImage.length == 0) {
            profileImage = defaultProfileImage;
        }
    }
}

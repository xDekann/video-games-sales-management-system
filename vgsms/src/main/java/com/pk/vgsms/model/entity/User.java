package com.pk.vgsms.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private Boolean enabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserDetails userDetails;

    @OneToOne
    @MapsId
    @JoinColumn(name = "fk_auth")
    private Authority authority;

    public void connectAuthority(Authority authority) {
        if (authority == null) {return;}
        this.authority = authority;
    }

    public void connectUserDetails(UserDetails userDetails) {
        if (userDetails == null) {return;}
        this.userDetails = userDetails;
    }
}

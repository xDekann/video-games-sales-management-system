package com.pk.vgsms.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_detail")
public class UserDetails {

    @Id
    @Column(name = "id_user_detail")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id_user_detail")
    private User user;

    public void connectUser(User user) {
        if (user == null)
            return;
        this.user = user;
    }
}

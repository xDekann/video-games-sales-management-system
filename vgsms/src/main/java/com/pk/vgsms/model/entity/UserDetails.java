package com.pk.vgsms.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetails that = (UserDetails) o;
        return name.equals(that.name) && surname.equals(that.surname) && email.equals(that.email) && city.equals(that.city) && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, email, city, address);
    }
}

package com.pk.vgsms.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "authority")
public class Authority {

    public Authority(String authorityName) {
        this.authorityName = authorityName;
    }

    @Id
    @Column(name = "id_auth")
    private Long id;

    @Column(name = "authority_name")
    private String authorityName;

    @OneToOne(mappedBy = "authority", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private User user;
}

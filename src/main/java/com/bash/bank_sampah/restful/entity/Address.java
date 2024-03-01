package com.bash.bank_sampah.restful.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    private String id;
    @Column(name = "street")
    public String street;
    @Column(name = "city")
    public String city;
    @Column(name = "province")
    public String province;
    @Column(name = "country")
    public String country;
    @Column(name = "postal_code")
    public String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "id")
    public Contact contact;
}

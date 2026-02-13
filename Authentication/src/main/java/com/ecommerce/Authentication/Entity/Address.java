package com.ecommerce.Authentication.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", nullable = false)
    Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    User user;

    @Column(name = "address")
    String address;
    @Column(name = "pincode")
    Integer pincode;
    @Column(name="Phone_Number")
    String phoneNumber;

}

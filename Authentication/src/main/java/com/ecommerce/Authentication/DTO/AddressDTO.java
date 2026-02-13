package com.ecommerce.Authentication.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private String address;
    private Integer pincode;
    private String phoneNumber;
}
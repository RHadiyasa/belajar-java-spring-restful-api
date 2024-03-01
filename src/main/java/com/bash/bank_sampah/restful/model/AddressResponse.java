package com.bash.bank_sampah.restful.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {

    public String id;
    public String street;
    public String city;
    public String province;
    public String country;
    public String postalCode;

}

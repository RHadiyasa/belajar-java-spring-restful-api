package com.bash.bank_sampah.restful.model;


import lombok.*;
import org.springframework.jdbc.core.SqlReturnType;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponse {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public String phone;

}

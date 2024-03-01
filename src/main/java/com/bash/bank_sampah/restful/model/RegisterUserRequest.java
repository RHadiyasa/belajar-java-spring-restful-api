package com.bash.bank_sampah.restful.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {
    @NotBlank // username dilarang kosong
    @Size(max = 255)
    public String username;

    @NotBlank
    @Size(max = 255)
    public String password;

    @NotBlank
    @Size(max = 255)
    public String name;

}

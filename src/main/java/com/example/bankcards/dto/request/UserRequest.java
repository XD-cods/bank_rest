package com.example.bankcards.dto.request;

import com.example.bankcards.utility.constant.RegExConstant;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

    @NotBlank(message = "{user.first.name.blank}")
    String firstName,

    @NotBlank(message = "{user.last.name.blank}")
    String lastName,

    @Email(message = "{user.email.invalid}")
    String email,

    @NotBlank(message = "{user.password.blank}")
    @Size(message = "{user.password.size}", min = 4)
    @Pattern(message = "{user.password.pattern}", regexp = RegExConstant.userPasswordRegEx)
    String password

) {
}
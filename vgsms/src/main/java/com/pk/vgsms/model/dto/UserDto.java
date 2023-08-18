package com.pk.vgsms.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {

    private Long id;

    @NotBlank(message = "Username must not be blank!")
    @Size(min = 3, max = 45, message = "Username must contain more than 2 and less than 46 characters!")
    private String username;

    @NotBlank(message = "Name must not be blank!")
    @Size(min = 3, max = 45, message = "Name must contain more than 2 and less than 46 characters!")
    @Pattern(regexp = "[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+", message = "The user's name can only contain letters.")
    private String name;

    @NotBlank(message = "Surname must not be blank!")
    @Size(min = 2, max = 45, message = "Surname must contain more than 1 and less than 46 characters!")
    private String surname;

    @Email(regexp = ".+[@].+[\\.].+", message = "Invalid address!")
    @Size(max = 45, message = "Email must contain less than 46 characters!")
    @NotBlank(message = "Email must not be blank!")
    private String email;

    @NotBlank(message = "City must not be blank!")
    @Size(min = 3, max = 45, message = "City must contain more than 2 and less than 46 characters!")
    @Pattern(regexp = "[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]+", message = "The user's city can only contain letters.")
    private String city;

    @NotBlank(message = "Address must not be blank!")
    @Size(min = 3, max = 100, message = "City must contain more than 2 and less than 100 characters!")
    @Pattern(regexp = "^[A-Za-z0-9\\-.,\\/\\\\ ]+$", message = "Invalid address")
    private String address;

    private String password;

    private String authorityName;

    private Boolean enabled;
}

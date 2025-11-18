package com.company.university.common.vo;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    public static final String POSTAL_CODE_REGEX = "\\d{2}-\\d{3}";
    public static final String POSTAL_CODE_MESSAGE = "Kod pocztowy musi być w formacie 00-000";

    private String street;
    private String number;
    private String city;

    @Pattern(regexp = POSTAL_CODE_REGEX, message = POSTAL_CODE_MESSAGE)
    private String postalCode;

    private String country;
}
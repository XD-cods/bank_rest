package com.example.bankcards.utility.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RegExConstant {

    public static final String cardNumberRegEx = "^(\\d{4}\\s){3}\\d{4}$";

    public static final String userPasswordRegEx = "^(?=.*[A-Za-z])(?=.*\\\\d).*$";

}

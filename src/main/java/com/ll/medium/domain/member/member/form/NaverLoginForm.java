package com.ll.medium.domain.member.member.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverLoginForm {
    private String code;
    private String state;
    private String grantType;
    private String clientId;
    private String clientSecret;
}

package com.ll.medium.domain.member.member.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverToken {
    private String access_token;
    private String refresh_token;
    private String error;
    private String error_description;
}

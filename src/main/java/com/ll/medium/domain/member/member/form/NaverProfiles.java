package com.ll.medium.domain.member.member.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NaverProfiles {
    private Response response;

    @Getter
    @Setter
    public class Response {
        private String nickname;
        private String email;
    }
}

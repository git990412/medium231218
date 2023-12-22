package com.ll.medium.domain.post.post.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WriteForm {
    @NotBlank
    private String title;
    @NotBlank
    private String body;
    private boolean isPublished;
}
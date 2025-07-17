package org.com.moodbook.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReviewUpdateRequest {

    @NotBlank
    private String content;

    @Min(1)
    @Max(5)
    private int starRating;

    public ReviewUpdateRequest(String content, int starRating) {
        this.content = content;
        this.starRating = starRating;
    }
}

package org.com.moodbook.review.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.com.moodbook.review.entity.Review;

@Getter
@Builder
public class ReviewResponse {

    private Long reviewId;
    private String reviewerName;
    private String content;
    private int starRating;
    private LocalDateTime createdAt;

    public static ReviewResponse fromEntity(Review review) {
        return ReviewResponse.builder()
            .reviewId(review.getId())
            .reviewerName(review.getMember().getName())
            .content(review.getContent())
            .starRating(review.getStarRating())
            .createdAt(review.getCreatedAt())
            .build();
    }


}

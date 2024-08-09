package dev.backend.wakuwaku.domain.restaurant.dto.response;

import dev.backend.wakuwaku.global.infra.google.places.dto.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponse {
    private String relativePublishTimeDescription;
    private float rating;
    private String text;
    private String authorName;
    private String authorProfileUrl;

    public ReviewResponse(Review review) {
        this.relativePublishTimeDescription = review.getRelativePublishTimeDescription();
        this.rating = review.getRating();
        this.text = review.getText().getText();
        this.authorName = review.getAuthorAttribution().getDisplayName();
        this.authorProfileUrl = review.getAuthorAttribution().getPhotoUri();
    }
}

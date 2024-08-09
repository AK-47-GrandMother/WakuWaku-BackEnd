package dev.backend.wakuwaku.domain.restaurant.entity;

import dev.backend.wakuwaku.domain.BaseEntity;
import dev.backend.wakuwaku.global.infra.google.places.dto.Photo;
import dev.backend.wakuwaku.global.infra.google.places.dto.Places;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Restaurant extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeId;

    private String name;

    private double lat;

    private double lng;

    private List<String> photos = new ArrayList<>();

    private int userRatingsTotal;

    private double rating;

    public Restaurant(Places places) {
        this.placeId = places.getId();
        this.name = places.getDisplayName().getText();
        this.lat = places.getLocation().getLatitude();
        this.lng = places.getLocation().getLongitude();
        this.photos = places.getPhotos().stream()
                .map(Photo::getPhotoUrl)
                .toList();
        this.userRatingsTotal = places.getUserRatingCount();
        this.rating = places.getRating();
    }
}

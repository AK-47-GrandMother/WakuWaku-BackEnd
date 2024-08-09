package dev.backend.wakuwaku.domain.restaurant.dto.response;

import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import dev.backend.wakuwaku.global.infra.google.places.dto.Location;
import lombok.Getter;

import java.util.List;

@Getter
public class SimpleInfoRestaurant {
    private final String placeId;
    private final String name;
    private final Number rating;
    private final Number userRatingsTotal;
    private final Location location;
    private final List<String> photoUrl;

    public SimpleInfoRestaurant(Restaurant restaurant) {
        this.placeId = restaurant.getPlaceId();
        this.name = restaurant.getName();
        this.rating = restaurant.getRating();
        this.userRatingsTotal = restaurant.getUserRatingsTotal();
        this.location = new Location(restaurant.getLat(), restaurant.getLng());
        this.photoUrl = restaurant.getPhotos();
    }
}

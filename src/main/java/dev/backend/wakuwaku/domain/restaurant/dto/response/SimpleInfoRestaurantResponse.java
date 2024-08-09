package dev.backend.wakuwaku.domain.restaurant.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SimpleInfoRestaurantResponse {
    private List<SimpleInfoRestaurant> simpleInfoRestaurants;
    private int totalPage;

    public SimpleInfoRestaurantResponse(List<SimpleInfoRestaurant> simpleInfoRestaurants, int totalPage) {
        this.simpleInfoRestaurants = simpleInfoRestaurants;
        this.totalPage = totalPage;
    }
}

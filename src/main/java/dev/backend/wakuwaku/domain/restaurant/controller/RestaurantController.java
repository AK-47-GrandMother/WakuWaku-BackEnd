package dev.backend.wakuwaku.domain.restaurant.controller;

import dev.backend.wakuwaku.domain.restaurant.dto.response.DetailsInfoRestaurantResponse;
import dev.backend.wakuwaku.domain.restaurant.dto.response.Restaurants;
import dev.backend.wakuwaku.domain.restaurant.dto.response.SimpleInfoRestaurant;
import dev.backend.wakuwaku.domain.restaurant.dto.response.SimpleInfoRestaurantResponse;
import dev.backend.wakuwaku.domain.restaurant.service.RestaurantService;
import dev.backend.wakuwaku.global.infra.google.places.dto.Places;
import dev.backend.wakuwaku.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wakuwaku/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    public BaseResponse<SimpleInfoRestaurantResponse> getSimpleInfoRestaurants(@RequestParam("search") String searchWord,
                                                                               @RequestParam(name = "page",
                                                                                       required = false,
                                                                                       defaultValue = "1") Integer page) {
        Restaurants restaurants = restaurantService.getSimpleRestaurants(searchWord, page);

        List<SimpleInfoRestaurant> simpleInfoRestaurants = restaurants.getRestaurants().stream()
                .map(SimpleInfoRestaurant::new)
                .toList();

        return new BaseResponse<>(new SimpleInfoRestaurantResponse(simpleInfoRestaurants, restaurants.getTotalPage()));
    }

    @GetMapping("/{placeId}/details")
    public BaseResponse<DetailsInfoRestaurantResponse> getDetailsInfoRestaurant(@PathVariable("placeId") String placeId) {
        Places places = restaurantService.getDetailsRestaurant(placeId);

        return new BaseResponse<>(new DetailsInfoRestaurantResponse(places));
    }
}

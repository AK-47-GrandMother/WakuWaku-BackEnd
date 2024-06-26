package dev.backend.wakuwaku.domain.restaurant.repository;

import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findByPlaceId(String placeId);
}

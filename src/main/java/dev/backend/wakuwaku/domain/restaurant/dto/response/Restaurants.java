package dev.backend.wakuwaku.domain.restaurant.dto.response;

import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Redis에 저장된 List 타입의 객체는 역직렬화 할 때, List를 감싸는 Wrapper 클래스를 사용하지 않으면 해당 객체를 찾지 못하고 에러가 발생한다.
 * 따라서, 아래와 같이 List를 감싸는 Wrapper 클래스를 생성하여 Redis에 저장하고 조회하도록 한다
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Restaurants {
    private List<Restaurant> restaurants = new ArrayList<>();
    private int totalPage = 0;
}

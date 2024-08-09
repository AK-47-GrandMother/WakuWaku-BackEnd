package dev.backend.wakuwaku.global.infra.redis.service;

import com.redis.testcontainers.RedisContainer;
import dev.backend.wakuwaku.global.infra.google.places.dto.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class RedisServiceTest {
    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
    }

    @Autowired
    private RedisService redisService;

    private static final String REDIS_KEY = "도쿄";

    private static final String NAME = "우동신";

    private static final String PLACE_ID = "ChIJAQCl79GMGGARZheneHqgIUs";

    @AfterEach
    void AfterEach() {
        redisService.deletePlaces(REDIS_KEY);
    }

    @Test
    @DisplayName("Redis에 정상적으로 저장되어야 한다.")
    void savePlaces() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));

        // when
        redisService.savePlaces(REDIS_KEY, placesList);

        // then
        List<Places> getPlaces = redisService.getPlacesByRedis(REDIS_KEY, 1);

        assertThat(getPlaces).isNotEmpty().hasSize(2);
        assertThat(getPlaces).extracting("rating").containsExactly(4.1, 4.3);
    }

    @Test
    @DisplayName("Redis에 저장된 데이터를 정상적으로 조회해야 한다.")
    void getPlaces() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));

        redisService.savePlaces(REDIS_KEY, placesList);

        // when
        List<Places> placesByRedis = redisService.getPlacesByRedis(REDIS_KEY, 1);

        // then
        assertThat(placesByRedis).isNotEmpty().hasSize(2);
        assertThat(placesByRedis).extracting("rating").containsExactly(4.1, 4.3);
    }

    @Test
    @DisplayName("page를 0 이하로 입력 시 빈 리스트가 반환되어야 한다.")
    void getPlacesWithPageZero() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));
        redisService.savePlaces(REDIS_KEY, placesList);

        // when
        List<Places> placesByRedis = redisService.getPlacesByRedis(REDIS_KEY, 0);

        // then
        assertThat(placesByRedis).isEmpty();
    }

    @Test
    @DisplayName("해당 page에 데이터가 존재하지 않으면 빈 리스트가 반환되어야 한다.")
    void getPlacesWithEmptyList() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));
        redisService.savePlaces(REDIS_KEY, placesList);

        // when
        List<Places> placesByRedis = redisService.getPlacesByRedis(REDIS_KEY, 2);

        // then
        assertThat(placesByRedis).isEmpty();
    }

    @Test
    @DisplayName("해당 검색어의 페이지 개수를 정상적으로 반환해야 한다.")
    void getTotalPage() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));

        redisService.savePlaces(REDIS_KEY, placesList);

        // when
        int totalPages = redisService.getTotalPage(REDIS_KEY);

        // then
        assertThat(totalPages).isEqualTo(1);
    }

    @Test
    @DisplayName("페이지가 null 이면 0을 반환해야 한다.")
    void getTotalPageIsNull() {
        // when
        int totalPages = redisService.getTotalPage(REDIS_KEY);

        // then
        assertThat(totalPages).isZero();
    }

    @Test
    @DisplayName("Redis의 데이터가 정상적으로 삭제되어야 한다.")
    void deletePlaces() {
        // given
        List<Places> placesList = List.of(createPlaces(4.1), createPlaces(4.3));

        redisService.savePlaces(REDIS_KEY, placesList);

        // when
        redisService.deletePlaces(REDIS_KEY);

        // then
        List<Places> placesByRedis = redisService.getPlacesByRedis(REDIS_KEY, 1);

        assertThat(placesByRedis).isEmpty();
    }

    private Places createPlaces(double rating) {
        List<Photo> photos = new ArrayList<>();

        List<Review> reviews = new ArrayList<>();

        dev.backend.wakuwaku.global.infra.google.places.dto.DisplayName name = new dev.backend.wakuwaku.global.infra.google.places.dto.DisplayName(NAME);

        Location location = new Location(35.686489, 139.697001);

        List<String> weekdayDescriptions = new ArrayList<>();

        weekdayDescriptions.add("월요일:오전11:00~오후10:00,화요일:오전11:00~오후10:00,수요일:오전11:00~오후10:00,목요일:오전11:00~오후10:00,금요일:오전11:00~오후10:00,토요일:오전11:00~오후10:00,일요일:오전11:00~오후10:00");

        CurrentOpeningHours currentOpeningHours = new CurrentOpeningHours(true, weekdayDescriptions);

        Photo photo = Photo.builder()
                .photoUrl("https://lh3.googleusercontent.com/places/ANXAkqG2xQHKla3ebHNhRNrgMFi4WB6hGbR6LZTd2ig0PK5qTwkIvk0EP1fzPQ8UXmAt3FcU1Gz0XjYYCQJvFJQQVhAMss2GtKenoAI=s4800-w1440-h810")
                .build();

        photos.add(photo);

        LocalizedText editorialSummary = new LocalizedText("다양한 반주와 함께 바삭한 튀김과 수제 우동을 맛 볼 수 있는 아담한 식당입니다.");

        LocalizedText reviewText = new LocalizedText("붓카게 우동과 자루소바 주문\\n직원 매우 친절함.의외로 튀김 맛있다\\n우동 자체는 꽤 맛있긴 하지만 이게 3시간을 기다릴 맛인가?이 가격 값을 하는가? 하면 별점 3점인 맛\\n웨이팅이 아예 없고 근처에 있다면 갈만한 집이긴 한데 절대 기다려서 먹을 맛 아님!!!!\\n면발이 탱글하다는데 개인적으로 오사카에서 먹은 다른 우동 집이 오백만배 탱글하고 맛있었음\\n손님도 로컬 거의 없고 대부분 외국인임\\n우연히 내가 그 근처를 지나가고 있는데 웨이팅이 없다? 할 때만 가십쇼");

        AuthorAttribution authorAttribution = new AuthorAttribution("촉촉한볼따구", "https://lh3.googleusercontent.com/a-/ALV-UjVoB_ILlpMn32pQwKbd5R99vuCX8mSb5T4rUzTRBBRts8CQolD9=s128-c0x00000000-cc-rp-mo");

        Review review = Review.builder()
                .relativePublishTimeDescription("3주 전")
                .rating(3)
                .text(reviewText)
                .authorAttribution(authorAttribution)
                .build();

        reviews.add(review);

        return Places.builder()
                .id(PLACE_ID)
                .displayName(name)
                .rating(rating)
                .location(location)
                .currentOpeningHours(currentOpeningHours)
                .photos(photos)
                .dineIn(true)
                .takeout(false)
                .delivery(false)
                .editorialSummary(editorialSummary)
                .reviews(reviews)
                .nationalPhoneNumber(null)
                .formattedAddress("일본 〒151-0053 Tokyo, Shibuya City, Yoyogi, 2-chōme−20−１６ 相馬ビル １F")
                .websiteUri("http://www.udonshin.com/")
                .userRatingCount(3965)
                .reservable(true)
                .servesBreakfast(false)
                .servesLunch(true)
                .servesDinner(true)
                .servesBeer(true)
                .servesWine(false)
                .servesVegetarianFood(false)
                .build();
    }
}

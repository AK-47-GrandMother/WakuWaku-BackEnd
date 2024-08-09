package dev.backend.wakuwaku.domain.restaurant.service;

import dev.backend.wakuwaku.domain.restaurant.dto.response.Restaurants;
import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import dev.backend.wakuwaku.domain.restaurant.repository.RestaurantRepository;
import dev.backend.wakuwaku.global.exception.ExceptionStatus;
import dev.backend.wakuwaku.global.exception.WakuWakuException;
import dev.backend.wakuwaku.global.infra.google.places.details.GooglePlacesDetailsService;
import dev.backend.wakuwaku.global.infra.google.places.dto.*;
import dev.backend.wakuwaku.global.infra.google.places.textsearch.GooglePlacesTextSearchService;
import dev.backend.wakuwaku.global.infra.redis.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static dev.backend.wakuwaku.domain.restaurant.service.constant.SearchWordConstant.*;
import static dev.backend.wakuwaku.global.exception.ExceptionStatus.INVALID_PARAMETER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.thenThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private GooglePlacesDetailsService googlePlacesDetailsService;

    @Mock
    private GooglePlacesTextSearchService googlePlacesTextSearchService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private RestaurantService restaurantService;

    private static final String PLACE_ID = "ChIJAQCl79GMGGARZheneHqgIUs";

    private static final String NAME = "우동신";

    private static final int TOTAL_PAGE = 5;

    private final List<Photo> photos = new ArrayList<>();

    private final List<Review> reviews = new ArrayList<>();

    private Places places;

    @BeforeEach
    void setUp() {
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

        places = Places.builder()
                .id(PLACE_ID)
                .displayName(name)
                .rating(4.1)
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

    @Test
    @DisplayName("저장할 식당이 없다면 식당 저장 성공")
    void saveSuccess() {
        // given
        Restaurant restaurant = new Restaurant(places);

        given(restaurantRepository.findByPlaceId(anyString())).willReturn(Optional.empty());
        given(restaurantRepository.save(any(Restaurant.class))).willReturn(restaurant);

        // when
        Restaurant savedRestaurant = restaurantService.save(restaurant);

        // then
        assertThat(savedRestaurant).isEqualTo(restaurant);
        then(restaurantRepository).should().save(restaurant);
    }

    @Test
    @DisplayName("요청한 Place Id가 있다면 식당 저장 X")
    void saveFailed() {
        // given
        Restaurant restaurant = new Restaurant(places);
        Restaurant duplicateRestaurant = new Restaurant(places);

        given(restaurantRepository.findByPlaceId(PLACE_ID)).willReturn(Optional.of(restaurant));

        // when
        Restaurant existingRestaurant = restaurantService.save(duplicateRestaurant);

        // then
        assertThat(existingRestaurant.getPlaceId()).isEqualTo(PLACE_ID);
        then(restaurantRepository).should(never()).save(duplicateRestaurant);
    }

    @DisplayName("id 값으로 식당 조회")
    @Test
    void findById() {
        // given
        Restaurant restaurant = new Restaurant(places);

        given(restaurantRepository.findById(anyLong())).willReturn(Optional.of(restaurant));

        // when
        Restaurant restaurantById = restaurantService.findById(anyLong());

        // then
        assertThat(restaurantById).isEqualTo(restaurant);
        then(restaurantRepository).should().findById(anyLong());
    }

    @DisplayName("id가 유효하지 않다면 INVALID_PARAMETER 예외가 발생해야 한다.")
    @Test
    void failFindById() {
        // when
        thenThrownBy(
                () -> restaurantService.findById(anyLong())
        )
                .isInstanceOf(WakuWakuException.class)
                .extracting("status")
                .isEqualTo(INVALID_PARAMETER);
    }

    @DisplayName("Redis 에 존재한다면 캐시된 데이터로 식당의 간단한 정보를 얻어야 한다.")
    @Test
    void getSimpleRestaurantsByRedis() {
        // given
        String searchWord = "도쿄";

        List<Places> placesList = new ArrayList<>();
        placesList.add(places);

        Restaurant restaurant = new Restaurant(places);

        given(redisService.getPlacesByRedis(searchWord, TOTAL_PAGE)).willReturn(placesList);
        given(redisService.getTotalPage(searchWord)).willReturn(TOTAL_PAGE);

        // when
        Restaurants restaurants = restaurantService.getSimpleRestaurants(searchWord, TOTAL_PAGE);

        // then
        assertThat(restaurants.getRestaurants()).hasSize(1);
        assertThat(restaurants.getRestaurants().get(0).getPlaceId()).isEqualTo(restaurant.getPlaceId());
        assertThat(restaurants.getRestaurants().get(0).getName()).isEqualTo(restaurant.getName());
        assertThat(restaurants.getRestaurants().get(0).getLat()).isEqualTo(restaurant.getLat());
        assertThat(restaurants.getRestaurants().get(0).getLng()).isEqualTo(restaurant.getLng());
        assertThat(restaurants.getRestaurants().get(0).getRating()).isEqualTo(restaurant.getRating());
        assertThat(restaurants.getRestaurants().get(0).getUserRatingsTotal()).isEqualTo(restaurant.getUserRatingsTotal());
        assertThat(restaurants.getRestaurants().get(0).getPhotos()).isEqualTo(restaurant.getPhotos());
        assertThat(restaurants.getTotalPage()).isEqualTo(TOTAL_PAGE);

        then(redisService).should().getPlacesByRedis( searchWord, TOTAL_PAGE);
        then(redisService).should().getTotalPage( searchWord);
    }

    @DisplayName("Page 번호가 1이고 Redis 에 존재하지 않는 정보는 Google Places API 를 호출하여 식당의 간단한 정보를 얻어야 한다.")
    @Test
    void getSimpleRestaurantsByGooglePlacesAPIAndPageNumberOne() {
        // given
        String searchWord = "도쿄";

        List<Places> placesList = new ArrayList<>();
        placesList.add(places);

        Restaurant restaurant = new Restaurant(places);

        given(googlePlacesTextSearchService.getRestaurantsByTextSearch(JAPAN_WITH_SPACE + searchWord + FRONT_OF_RESTAURANT + RESTAURANT, 0)).willReturn(placesList);
        given(redisService.getPlacesByRedis(searchWord, 1)).willReturn(Collections.emptyList());
        given(redisService.getTotalPage(searchWord)).willReturn(1);
        willDoNothing().given(redisService).savePlaces(searchWord, placesList);

        // when
        Restaurants restaurants = restaurantService.getSimpleRestaurants(searchWord, 1);

        // then
        assertThat(restaurants.getRestaurants()).hasSize(1);
        assertThat(restaurants.getRestaurants().get(0).getPlaceId()).isEqualTo(restaurant.getPlaceId());
        assertThat(restaurants.getRestaurants().get(0).getName()).isEqualTo(restaurant.getName());
        assertThat(restaurants.getRestaurants().get(0).getLat()).isEqualTo(restaurant.getLat());
        assertThat(restaurants.getRestaurants().get(0).getLng()).isEqualTo(restaurant.getLng());
        assertThat(restaurants.getRestaurants().get(0).getRating()).isEqualTo(restaurant.getRating());
        assertThat(restaurants.getRestaurants().get(0).getUserRatingsTotal()).isEqualTo(restaurant.getUserRatingsTotal());
        assertThat(restaurants.getRestaurants().get(0).getPhotos()).isEqualTo(restaurant.getPhotos());
        assertThat(restaurants.getTotalPage()).isEqualTo(1);

        then(googlePlacesTextSearchService).should().getRestaurantsByTextSearch(JAPAN_WITH_SPACE + searchWord + FRONT_OF_RESTAURANT + RESTAURANT, 0);
        then(redisService).should().getPlacesByRedis( searchWord, 1);
        then(redisService).should(times(2)).getTotalPage( searchWord);
        then(redisService).should().savePlaces(searchWord, placesList);
    }

    @DisplayName("Page 번호가 1이 아니고 Redis 에 존재하지 않는 정보는 빈 Restaurants 를 해야 한다.")
    @Test
    void getSimpleRestaurantsByGooglePlacesAPIAndPageNumberIsNotOne() {
        // given
        String searchWord = "도쿄";

        given(redisService.getPlacesByRedis(searchWord, TOTAL_PAGE)).willReturn(null);

        // when
        Restaurants restaurants = restaurantService.getSimpleRestaurants(searchWord, TOTAL_PAGE);

        // then
        assertThat(restaurants.getRestaurants()).isEmpty();
        assertThat(restaurants.getRestaurants()).isEmpty();
        assertThat(restaurants.getTotalPage()).isZero();

        then(redisService).should().getPlacesByRedis( searchWord, TOTAL_PAGE);
    }

    @DisplayName("검색어가 null 이면 INVALID_SEARCH_WORD 예외를 반환해야 한다.")
    @Test
    void failTextSearchByNullSearchWord() {
        // when & then
        thenThrownBy(
                () -> restaurantService.getSimpleRestaurants(null, TOTAL_PAGE)
        )
                .isInstanceOf(WakuWakuException.class)
                .extracting("status")
                .isEqualTo(ExceptionStatus.INVALID_SEARCH_WORD);
    }

    @DisplayName("검색어를 입력하지 않으면 INVALID_SEARCH_WORD 예외를 반환해야 한다.")
    @Test
    void failTextSearchByNoSearchWord() {
        // when & then
        thenThrownBy(
                () -> restaurantService.getSimpleRestaurants("", TOTAL_PAGE)
        )
                .isInstanceOf(WakuWakuException.class)
                .extracting("status")
                .isEqualTo(ExceptionStatus.INVALID_SEARCH_WORD);
    }

    @DisplayName("검색어가 공백이면 INVALID_SEARCH_WORD 예외를 반환해야 한다.")
    @Test
    void failTextSearchBySpaceSearchWord() {
        // when & then
        thenThrownBy(
                () -> restaurantService.getSimpleRestaurants(" ", TOTAL_PAGE)
        )
                .isInstanceOf(WakuWakuException.class)
                .extracting("status")
                .isEqualTo(ExceptionStatus.INVALID_SEARCH_WORD);
    }

    @DisplayName("검색어가 탭이면 INVALID_SEARCH_WORD 예외를 반환해야 한다.")
    @Test
    void failTextSearchByTabSearchWord() {
        // when & then
        thenThrownBy(
                () -> restaurantService.getSimpleRestaurants("  ", TOTAL_PAGE)
        )
                .isInstanceOf(WakuWakuException.class)
                .extracting("status")
                .isEqualTo(ExceptionStatus.INVALID_SEARCH_WORD);
    }

    @DisplayName("식당의 자세한 정보를 얻음")
    @Test
    void getDetailsRestaurant() {
        // given
        given(googlePlacesDetailsService.getRestaurantByDetailsSearch(PLACE_ID)).willReturn(places);

        // when
        Places detailsRestaurant = restaurantService.getDetailsRestaurant(PLACE_ID);

        // then
        assertThat(detailsRestaurant).isEqualTo(places);
    }
}
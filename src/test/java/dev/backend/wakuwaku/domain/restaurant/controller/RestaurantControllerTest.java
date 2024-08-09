package dev.backend.wakuwaku.domain.restaurant.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.SimpleType;
import dev.backend.wakuwaku.domain.restaurant.dto.response.Restaurants;
import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import dev.backend.wakuwaku.domain.restaurant.service.RestaurantService;
import dev.backend.wakuwaku.global.infra.google.places.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = RestaurantController.class)
class RestaurantControllerTest {
    @MockBean
    private RestaurantService restaurantService;

    private MockMvc mockMvc;

    private static final String BASE_URL = "/wakuwaku/v1/restaurant";

    private static final String PLACE_ID = "ChIJAQCl79GMGGARZheneHqgIUs";

    private static final String SEARCH_WORD = "도쿄";

    private static final String NAME = "우동신";

    private static final int TOTAL_PAGE = 5;

    private final List<Photo> photos = new ArrayList<>();

    private final List<Review> reviews = new ArrayList<>();

    private Places places;

    @BeforeEach
    void setUp (final WebApplicationContext context,
               final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();

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
    @DisplayName("Simple Info 조회. 즉, 사용자가 검색하여 얻는 데이터를 반환")
    void getSimpleInfoRestaurants() throws Exception {
        // given
        List<Restaurant> restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant(places));

        Restaurants restaurants = new Restaurants(restaurantList, TOTAL_PAGE);

        given(restaurantService.getSimpleRestaurants(anyString(), anyInt())).willReturn(restaurants);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("search", SEARCH_WORD)
                    .queryParam("page", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].placeId").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].name").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].rating").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].userRatingsTotal").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].location.latitude").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].location.longitude").exists())
                .andExpect(jsonPath("data.simpleInfoRestaurants[*].photoUrl").exists())
                .andDo(MockMvcRestDocumentationWrapper.document("get simple info",
                        resource(ResourceSnippetParameters.builder()
                                .tag("Get Simple Info")
                                .summary("Get Simple Info By Search Word")
                                .description("응답(simpleInfoRestaurants[]의 size) 개수가 최대 10개까지 가능함.")
                                .queryParameters(
                                        parameterWithName("search").description("검색어 (장소명 혹은 지역명만 입력) (예시: 도쿄, 후쿠오카, 나가사키)"),
                                        parameterWithName("page").type(SimpleType.INTEGER).description("페이지 번호").defaultValue(1).optional()
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 시 반환되는 code 값"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 시 반환되는 메시지"),
                                        fieldWithPath("data.simpleInfoRestaurants[].placeId").type(JsonFieldType.STRING).description("Details Info 에 활용할 식당의 Place Id"),
                                        fieldWithPath("data.simpleInfoRestaurants[].name").type(JsonFieldType.STRING).description("식당 이름"),
                                        fieldWithPath("data.simpleInfoRestaurants[].rating").type(JsonFieldType.NUMBER).optional().description("식당 별점"),
                                        fieldWithPath("data.simpleInfoRestaurants[].userRatingsTotal").type(JsonFieldType.NUMBER).optional().description("식당 총 리뷰 수"),
                                        fieldWithPath("data.simpleInfoRestaurants[].location.latitude").type(JsonFieldType.NUMBER).description("위도"),
                                        fieldWithPath("data.simpleInfoRestaurants[].location.longitude").type(JsonFieldType.NUMBER).description("경도"),
                                        fieldWithPath("data.simpleInfoRestaurants[].photoUrl").type(JsonFieldType.ARRAY).optional().description("식당 대표 사진 URL"),
                                        fieldWithPath("data.totalPage").type(JsonFieldType.NUMBER).optional().description("이 검색어에 대한 총 페이지 수"))
                                .build()
                        )
                       )
                );

        then(restaurantService).should().getSimpleRestaurants(anyString(), anyInt());
    }

    @Test
    @DisplayName("Details Info 조회. 즉, 사용자가 검색하여 얻는 데이터 중 하나의 데이터의 세부적인 정보를 반환")
    void getDetailsInfoRestaurant() throws Exception {
        // given
        given(restaurantService.getDetailsRestaurant(PLACE_ID)).willReturn(places);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders
                    .get(BASE_URL + "/{placeId}" + "/details", PLACE_ID)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.name").exists())
                .andExpect(jsonPath("data.editorialSummary").exists())
                .andExpect(jsonPath("data.photos").exists())
                .andExpect(jsonPath("data.dineIn").exists())
                .andExpect(jsonPath("data.delivery").exists())
                .andExpect(jsonPath("data.takeout").exists())
                .andExpect(jsonPath("data.website").exists())
                .andExpect(jsonPath("data.formattedPhoneNumber").isEmpty())
                .andExpect(jsonPath("data.formattedAddress").exists())
                .andExpect(jsonPath("data.reservable").exists())
                .andExpect(jsonPath("data.servesBreakfast").exists())
                .andExpect(jsonPath("data.servesLunch").exists())
                .andExpect(jsonPath("data.servesDinner").exists())
                .andExpect(jsonPath("data.servesBeer").exists())
                .andExpect(jsonPath("data.servesWine").exists())
                .andExpect(jsonPath("data.servesVegetarianFood").exists())
                .andExpect(jsonPath("data.userRatingsTotal").exists())
                .andExpect(jsonPath("data.rating").exists())
                .andExpect(jsonPath("data.reviews").exists())
                .andExpect(jsonPath("data.openNow").exists())
                .andExpect(jsonPath("data.weekdayText").exists())
                .andDo(MockMvcRestDocumentationWrapper.document("get details info",
                        resource(ResourceSnippetParameters.builder()
                                    .tag("Get Details Info")
                                    .description("Get Details Info By PlaceId")
                                    .pathParameters(
                                    parameterWithName("placeId").description("Details Info를 얻고 싶은 식당의 Place Id")
                                    )
                                    .responseFields(
                                            fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 시 반환되는 code 값"),
                                            fieldWithPath("message").type(JsonFieldType.STRING).description("응답 시 반환되는 메시지"),
                                            fieldWithPath("data.name").type(JsonFieldType.STRING).description("식당 이름"),
                                            fieldWithPath("data.editorialSummary").type(JsonFieldType.STRING).optional().description("식당에 대한 간단한 소개글"),
                                            fieldWithPath("data.photos").type(JsonFieldType.ARRAY).optional().description("식당 사진 (최대 10개)"),
                                            fieldWithPath("data.dineIn").type(JsonFieldType.BOOLEAN).optional().description("매장 내 식사 가능 여부"),
                                            fieldWithPath("data.delivery").type(JsonFieldType.BOOLEAN).optional().description("배달 가능 여부"),
                                            fieldWithPath("data.takeout").type(JsonFieldType.BOOLEAN).optional().description("포장 가능 여부"),
                                            fieldWithPath("data.website").type(JsonFieldType.STRING).optional().description("해당 식당의 웹 사이트 주소"),
                                            fieldWithPath("data.formattedPhoneNumber").type(JsonFieldType.STRING).optional().description("식당 전화번호"),
                                            fieldWithPath("data.formattedAddress").type(JsonFieldType.STRING).description("식당 주소"),
                                            fieldWithPath("data.reservable").type(JsonFieldType.BOOLEAN).optional().description("예약 가능 여부"),
                                            fieldWithPath("data.servesBreakfast").type(JsonFieldType.BOOLEAN).optional().description("아침 식사 가능 여부"),
                                            fieldWithPath("data.servesLunch").type(JsonFieldType.BOOLEAN).optional().description("점심 식사 가능 여부"),
                                            fieldWithPath("data.servesDinner").type(JsonFieldType.BOOLEAN).optional().description("저녁 식사 가능 여부"),
                                            fieldWithPath("data.servesBeer").type(JsonFieldType.BOOLEAN).optional().description("맥주 판매 여부"),
                                            fieldWithPath("data.servesWine").type(JsonFieldType.BOOLEAN).optional().description("와인 판매 여부"),
                                            fieldWithPath("data.servesVegetarianFood").type(JsonFieldType.BOOLEAN).optional().description("채식주의자를 위한 메뉴 유무"),
                                            fieldWithPath("data.userRatingsTotal").type(JsonFieldType.NUMBER).optional().description("식당의 총 리뷰 수"),
                                            fieldWithPath("data.rating").type(JsonFieldType.NUMBER).optional().description("식당 별점"),
                                            fieldWithPath("data.reviews[].authorName").type(JsonFieldType.STRING).description("리뷰 작성자 이름"),
                                            fieldWithPath("data.reviews[].rating").type(JsonFieldType.NUMBER).description("리뷰 작성자의 별점"),
                                            fieldWithPath("data.reviews[].relativePublishTimeDescription").type(JsonFieldType.STRING).description("현 시간으로부터 식당 리뷰 작성한 날짜"),
                                            fieldWithPath("data.reviews[].authorProfileUrl").type(JsonFieldType.STRING).description("리뷰 작성자의 프로필 사진 URL"),
                                            fieldWithPath("data.reviews[].text").type(JsonFieldType.STRING).description("리뷰 내용"),
                                            fieldWithPath("data.openNow").type(JsonFieldType.BOOLEAN).optional().description("검색한 시점에서의 식당 오픈 여부"),
                                            fieldWithPath("data.weekdayText").type(JsonFieldType.ARRAY).optional().description("요일 별 식당 운영 시간")
                                    )
                                    .build()
                        )
                       )
                );

        then(restaurantService).should().getDetailsRestaurant(PLACE_ID);
    }
}
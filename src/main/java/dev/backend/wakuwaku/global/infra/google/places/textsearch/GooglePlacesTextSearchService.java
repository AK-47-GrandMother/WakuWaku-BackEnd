package dev.backend.wakuwaku.global.infra.google.places.textsearch;

import com.google.gson.Gson;
import dev.backend.wakuwaku.global.infra.google.places.dto.Photo;
import dev.backend.wakuwaku.global.infra.google.places.dto.Places;
import dev.backend.wakuwaku.global.infra.google.places.photo.GooglePlacesPhotoService;
import dev.backend.wakuwaku.global.infra.google.places.textsearch.dto.request.NextPageRequest;
import dev.backend.wakuwaku.global.infra.google.places.textsearch.dto.request.TextSearchRequest;
import dev.backend.wakuwaku.global.infra.google.places.textsearch.dto.response.TextSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static dev.backend.wakuwaku.global.exception.WakuWakuException.NOT_EXISTED_NEXT_PAGE_TOKEN;
import static dev.backend.wakuwaku.global.infra.google.places.textsearch.constant.TextSearchConstant.*;

@Service
public class GooglePlacesTextSearchService {
    private final RestClient restClient;

    private final GooglePlacesPhotoService googlePlacesPhotoService;

    @Value("${google-places}")
    private String apiKey;

    public GooglePlacesTextSearchService(RestClient.Builder restClientBuilder, @Autowired GooglePlacesPhotoService googlePlacesPhotoService) {
        this.restClient = restClientBuilder.build();
        this.googlePlacesPhotoService = googlePlacesPhotoService;
    }

    public List<Places> getRestaurantsByTextSearch(String searchWord, int cnt) {
        TextSearchResponse textSearchResponse = restClient.post()
                .uri(TEXT_SEARCH_URL)
                .header(TEXT_SEARCH_RESPONSE_FIELDS_HEADER, TEXT_SEARCH_RESPONSE_FIELDS)
                .header(TEXT_SEARCH_REQUEST_API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getTextSearchRequestBody(searchWord))
                .retrieve()
                .body(TextSearchResponse.class);

        cnt += 1;

        return getPlaces(textSearchResponse, searchWord, cnt);
    }

    // pageToken 을 얻은 searchWord 와 같은 searchWord 로 검색해야만 제대로 동작함. (그렇지 않으면 에러 발생)
    // pageToken 만 가지고는 API 요청할 수 없음. (에러 발생)
    public List<Places> getRestaurantByNextPageToken(String searchWord, String pageToken, int cnt) {
        if (pageToken == null || pageToken.isEmpty()) {
            throw NOT_EXISTED_NEXT_PAGE_TOKEN;
        }

        TextSearchResponse textSearchResponse = restClient.post()
                .uri(TEXT_SEARCH_URL)
                .header(TEXT_SEARCH_RESPONSE_FIELDS_HEADER, TEXT_SEARCH_RESPONSE_FIELDS)
                .header(TEXT_SEARCH_REQUEST_API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(getNextPageRequestBody(searchWord, pageToken))
                .retrieve()
                .body(TextSearchResponse.class);

        return getPlaces(textSearchResponse, searchWord, cnt);
    }

    private List<Places> getPlaces(TextSearchResponse textSearchResponse, String searchWord, int cnt) {
        if (textSearchResponse == null || textSearchResponse.getPlaces() == null || textSearchResponse.getPlaces().isEmpty()) {
            return Collections.emptyList();
        }

        return getUsablePlaces(textSearchResponse, searchWord, cnt);
    }

    private List<Places> getUsablePlaces(TextSearchResponse textSearchResponse, String searchWord, int cnt) {
        List<Places> places = textSearchResponse.getPlaces();

        List<Places> actualPlaces = new ArrayList<>();

        for (Places place : places) {
            List<Photo> photos = place.getPhotos();

            if (photos == null || photos.isEmpty() || place.getPlusCode() == null || place.getPlusCode().getCompoundCode() == null || place.getPlusCode().getCompoundCode().contains(INVALID_RESTAURANT_LOCATION)) {
                continue;
            }

            // 하나의 사진(대표 사진)만을 얻으면 되므로
            // 각 식당 객체 사진에 대한 API 를 한 번씩만 호출
            Photo photo = photos.get(0);

            String actualPhotoUrl = googlePlacesPhotoService.getActualPhotoUrl(photo, apiKey);
            photo.createPhotoURL(actualPhotoUrl);

            // 첫 번째 값 제외하고 나머지 9개의 값이 null 로 표시되므로
            // size() 가 1인 리스트를 반환
            photos.clear();
            photos.add(photo);

            actualPlaces.add(place);
        }

        if (isResultEnough(actualPlaces)) {
            if (cnt > 3) {
                return actualPlaces;
            }

            return getRestaurantsByTextSearch(searchWord, cnt);
        }

        return actualPlaces;
    }

    private String getTextSearchRequestBody(String searchWord) {
        TextSearchRequest textSearchRequest = new TextSearchRequest(searchWord);

        Gson gson = new Gson();

        return gson.toJson(textSearchRequest);
    }

    private String getNextPageRequestBody(String searchWord, String nextPageToken) {
        NextPageRequest newestTextSearchRequest = new NextPageRequest(searchWord, nextPageToken);

        Gson gson = new Gson();

        return gson.toJson(newestTextSearchRequest);
    }

    private boolean isResultEnough(List<Places> places) {
        return places.isEmpty();
    }
}

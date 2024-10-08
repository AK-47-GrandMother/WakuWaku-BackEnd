package dev.backend.wakuwaku.global.infra.oauth.kakao.client;

import dev.backend.wakuwaku.global.infra.oauth.kakao.dto.KakaoMemberResponse;
import dev.backend.wakuwaku.global.infra.oauth.kakao.dto.KakaoToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

public interface KakaoApiClient {
    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoToken fetchToken(@RequestBody MultiValueMap<String, String> params);

    // 추가
    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoMemberResponse fetchMember(@RequestHeader(name = AUTHORIZATION) String bearerToken, @RequestHeader(name = CONTENT_TYPE) String contentType);
}
package dev.backend.wakuwaku.domain.oauth.controller;

import com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.google.gson.Gson;
import dev.backend.wakuwaku.domain.oauth.dto.OauthServerType;
import dev.backend.wakuwaku.domain.oauth.dto.request.LoginRequest;
import dev.backend.wakuwaku.domain.oauth.service.OauthService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.HashMap;
import java.util.Map;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = OauthController.class)
class OauthControllerTest {

    @MockBean
    private OauthService oauthService;

    @MockBean
    private EntityManager entityManager;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp(final WebApplicationContext context, final RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    @DisplayName("OAuth Authorization Code 요청 URL로 리디렉션 테스트")
    void redirectAuthCodeRequestUrl() throws Exception {
        // given
        String oauthServerType = "google";
        String redirectUrl = "https://accounts.google.com/o/oauth2/auth";
        given(oauthService.getAuthCodeRequestUrl(OauthServerType.GOOGLE)).willReturn(redirectUrl);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/oauth/{oauthServerType}", oauthServerType))
                .andExpect(status().is3xxRedirection())
                .andDo(MockMvcRestDocumentationWrapper.document("redirect-auth-code-request-url",
                        resource(ResourceSnippetParameters.builder()
                                .tag("OAuth")
                                .description("OAuth Authorization Code 요청 URL로 리디렉션 (인증 코드 요청)")
                                .pathParameters(
                                        parameterWithName("oauthServerType").description("OAuth 서버 타입: NAVER, KAKAO, GOOGLE")
                                )
                                .build()
                        )
                ));

        then(oauthService).should().getAuthCodeRequestUrl(OauthServerType.GOOGLE);
    }

    @Test
    @DisplayName("OAuth 로그인 테스트")
    void login() throws Exception {
        // given
        Long memberId = 1L;
        String code = "authorization_code";
        Map<String, Long> responseMap = new HashMap<>();
        responseMap.put("id", memberId);
        given(oauthService.login(OauthServerType.GOOGLE, code)).willReturn(responseMap);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.createOauthServerType(OauthServerType.GOOGLE);
        loginRequest.createCode(code);

        // Gson 객체 생성
        Gson gson = new Gson();
        String jsonContent = gson.toJson(loginRequest);

        // when & then
        mockMvc.perform(post("/oauth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(memberId))
                .andDo(MockMvcRestDocumentationWrapper.document("oauth-login",
                        resource(ResourceSnippetParameters.builder()
                                .tag("OAuth")
                                .description("OAuth 로그인.")
                                .requestFields(
                                        fieldWithPath("oauthServerType").type(JsonFieldType.STRING).description("OAuth 서버 타입: NAVER, KAKAO, GOOGLE"),
                                        fieldWithPath("code").type(JsonFieldType.STRING).description("인증 코드")
                                )
                                .responseFields(
                                        fieldWithPath("code").type(JsonFieldType.NUMBER).description("응답 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("회원 ID")
                                )
                                .build()
                        )
                ));

        then(oauthService).should().login(OauthServerType.GOOGLE, code);
    }
}

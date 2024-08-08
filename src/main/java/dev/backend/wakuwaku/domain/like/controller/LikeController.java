package dev.backend.wakuwaku.domain.like.controller;

import dev.backend.wakuwaku.domain.like.dto.request.LikePushRequest;
import dev.backend.wakuwaku.domain.like.dto.response.GetLikeResponse;
import dev.backend.wakuwaku.domain.like.service.LikeService;
import dev.backend.wakuwaku.global.response.BaseResponse;
import dev.backend.wakuwaku.global.exception.WakuWakuException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/wakuwaku/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/push")
    public BaseResponse<String> pushLike(@RequestBody LikePushRequest likePushRequest) {
        Long memberId = likePushRequest.getMemberId();
        Long restaurantId = likePushRequest.getRestaurantId();

        if (memberId == null) {
            throw WakuWakuException.NOT_EXISTED_MEMBER_INFO;
        } else if (restaurantId == null) {
            throw WakuWakuException.NOT_EXISTED_PLACE_ID;
        }


        Long id = likeService.pushLike(memberId, restaurantId);
        String message = id != null ? "찜하기 성공" : "찜하기 실패";

        return new BaseResponse<>(message);
    }

    @GetMapping("/list")
    public BaseResponse<List<GetLikeResponse>> findAll() {
        List<GetLikeResponse> likeResponses = likeService.findAll().stream()
                .map(GetLikeResponse::new)
                .collect(Collectors.toList());

        return new BaseResponse<>(likeResponses);
    }
}

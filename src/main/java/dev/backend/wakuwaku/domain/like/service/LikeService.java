package dev.backend.wakuwaku.domain.like.service;

import dev.backend.wakuwaku.domain.like.entity.Like;
import dev.backend.wakuwaku.domain.like.repository.LikeRepository;
import dev.backend.wakuwaku.domain.member.entity.Member;
import dev.backend.wakuwaku.domain.member.service.MemberService;
import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import dev.backend.wakuwaku.domain.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static dev.backend.wakuwaku.global.exception.WakuWakuException.NOT_EXISTED_MEMBER_INFO;
import static dev.backend.wakuwaku.global.exception.WakuWakuException.NOT_EXISTED_PLACE_ID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberService memberService;
    private final RestaurantService restaurantService;

    public Long pushLike(Long memberId, Long restaurantId) {
        Like like = likeRepository.findByMemberIdAndRestaurantId(memberId, restaurantId);

        if (like != null) {
            like.updateLikeStatus("Y".equals(like.getLikeStatus()) ? "N" : "Y");
        } else {
            Member member = memberService.findById(memberId);
            Restaurant restaurant = restaurantService.findById(restaurantId);

            if (member == null) throw NOT_EXISTED_MEMBER_INFO;
            else if (restaurant == null) throw NOT_EXISTED_PLACE_ID;

            like = Like.builder()
                    .member(member)
                    .restaurant(restaurant)
                    .likeStatus("Y")
                    .build();

            likeRepository.save(like);
        }
        return like.getId();
    }

    public List<Like> findAll() {
        return likeRepository.findAll();
    }
}

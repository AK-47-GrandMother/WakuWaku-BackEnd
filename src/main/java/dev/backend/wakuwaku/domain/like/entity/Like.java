package dev.backend.wakuwaku.domain.like.entity;

import dev.backend.wakuwaku.domain.StatusEntity;
import dev.backend.wakuwaku.domain.member.entity.Member;
import dev.backend.wakuwaku.domain.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "like_table")
public class Like extends StatusEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "id")
    private Restaurant restaurant;

    @Column
    private String likeStatus;

    @Builder
    public Like(Long id, Member member, Restaurant restaurant, String likeStatus) {
        this.id = id;
        this.member = member;
        this.restaurant = restaurant;
        this.likeStatus = likeStatus;
    }

    public void updateLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }
}

package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.DailyConditionType;
import foregg.foreggserver.domain.enums.EmotionType;
import foregg.foreggserver.dto.dailyDTO.DailyRequestDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Daily extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DailyConditionType dailyConditionType;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private EmotionType emotionType;

    @Column(length = 2048)
    private String image;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    private String reply;

    public void setEmotionType(EmotionType emotionType) {
        this.emotionType = emotionType;
    }

    public void updateDaily(DailyRequestDTO dto) {
        this.dailyConditionType = dto.getDailyConditionType();
        this.content = dto.getContent();
    }

}

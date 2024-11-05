package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    private boolean isOpen;

    @Setter
    private String thoughts;

    @Setter
    private boolean isParticipating;

    @Builder.Default
    private List<String> successDays = new ArrayList<>();

    public void setSuccessDays(String today) {
        this.successDays.add(today);
    }

    @PrePersist
    @PostLoad
    private void initializeSuccessDays() {
        if (this.successDays == null) {
            this.successDays = new ArrayList<>();
        }
    }


}

package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ChallengeSuccess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "challenge_participation_id")
    private ChallengeParticipation challengeParticipation;

    @Column(nullable = false)
    private String date;

    private String comment;

}

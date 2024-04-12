package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.RecordType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Schedule_id")
    private Schedule schedule;

    @OneToOne
    @JoinColumn(name = "injection_id")
    private Injection injection;

    @OneToOne
    @JoinColumn(name = "sideEffect_id")
    private SideEffect sideEffect;

}

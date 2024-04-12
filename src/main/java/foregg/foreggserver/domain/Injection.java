package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.InjectionPart;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Injection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String sideEffect;

    @Column(nullable = false)
    private String caution;

    @Enumerated(EnumType.STRING)
    private InjectionPart injectionPart;

    @OneToOne(mappedBy = "injection")
    private Record record;

}

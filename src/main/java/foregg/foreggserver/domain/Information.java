package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.InformationType;
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
public class Information extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    private InformationType informationType;

}

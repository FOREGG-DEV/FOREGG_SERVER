package foregg.foreggserver.domain;


import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.SubsidyColorType;
import foregg.foreggserver.dto.subsidyDTO.SubsidyRequestDTO;
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
public class Subsidy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int count;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int expenditure = 0;

    @Column(nullable = false)
    private int available;

    @Enumerated(EnumType.STRING)
    private SubsidyColorType color;

    public void updateExpenditure(int expenditure) {
        this.expenditure = expenditure;
        available = this.amount - expenditure;
    }

    public void updateSubsidy(SubsidyRequestDTO dto) {
        this.nickname = dto.getNickname();
        this.amount = dto.getAmount();
        this.content = dto.getContent();
    }

}

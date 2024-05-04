package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.LedgerType;
import foregg.foreggserver.dto.ledgerDTO.LedgerRequestDTO;
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
public class Ledger extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LedgerType ledgerType;

    @Column(nullable = false)
    private String date;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int count;

    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateLedger(LedgerRequestDTO dto) {
        this.ledgerType = dto.getLedgerType();
        this.date = dto.getDate();
        this.content = dto.getContent();
        this.amount = dto.getAmount();
        this.count = dto.getCount();
        if (dto.getMemo() != null) {
            this.memo = dto.getMemo();
        }
    }

}

package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expenditure extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade(CascadeType.ALL)
    private Ledger ledger;

    @ManyToOne(fetch = FetchType.LAZY)
    private Subsidy subsidy;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void setSubsidy(Subsidy subsidy) {
        this.subsidy = subsidy;
    }

}

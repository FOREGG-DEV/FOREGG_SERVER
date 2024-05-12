package foregg.foreggserver.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.SurgeryType;
import foregg.foreggserver.dto.myPageDTO.MyPageRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Surgery extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SurgeryType surgeryType;

    private int count;

    private String startAt;

    @OneToOne(mappedBy = "surgery")
    private User user;

    public void update(MyPageRequestDTO dto) {
        this.surgeryType = dto.getSurgeryType();
        this.count = dto.getCount();
        this.startAt = dto.getStartDate();
    }

}

package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.recordDTO.RecordRequestDTO;
import foregg.foreggserver.util.DateUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Record extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RecordType type;

    @Column(nullable = false)
    private String name;

    private String date;

    private String start_date;

    private String end_date;

    private String repeat_date;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL)
    private List<RepeatTime> repeatTimes;

    private String dose;

    private String memo;

    @Column(columnDefinition = "TEXT")
    private String medical_record;

    private String yearmonth;

    private List<String> start_end_yearmonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Schedule_id")
    private Schedule schedule;

    @OneToMany(mappedBy = "record",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SideEffect> sideEffect;

    public void updateRecord(RecordRequestDTO dto) {
        this.type = dto.getRecordType();
        this.name = dto.getName();
        this.date = dto.getDate();
        this.start_date = dto.getStartDate();
        this.end_date = dto.getEndDate();
        this.repeat_date = dto.getRepeatDate();
        this.dose = dto.getDose();
        this.memo = dto.getMemo();
        if (dto.getDate() != null) {
            this.yearmonth = DateUtil.getYearAndMonth(dto.getDate());
            this.start_end_yearmonth = null;
        }
        if (dto.getStartDate() != null) {
            this.start_end_yearmonth = DateUtil.getMonthsBetween(dto.getStartDate(), dto.getEndDate());
            this.yearmonth = null;
        }
    }

    public void setMedical_record(String memo) {
        this.medical_record = memo;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

}

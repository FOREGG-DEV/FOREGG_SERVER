package foregg.foreggserver.domain;

import foregg.foreggserver.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int rrn;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phonenumber;

    private String region;

    @Column(nullable = false)
    private int spouseCode;

    private LocalDateTime createAt;

    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Surgery> surgeries;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Inquiry> inquiries;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Notification> notifications;

}

package foregg.foreggserver.domain;

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
@SequenceGenerator(
        name = "spouse_code_seq_generator",
        sequenceName = "spouse_code_seq",
        allocationSize = 1
)
public class SpouseCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spouse_code_seq_generator")
    private Long id;

    @Column(nullable = false)
    private String code;

    public SpouseCode(String generatedCode) {
        this.code = generatedCode;
    }
}

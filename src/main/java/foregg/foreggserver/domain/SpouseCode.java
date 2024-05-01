package foregg.foreggserver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpouseCode {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String code;

    public SpouseCode(String generatedCode) {
        this.code = generatedCode;
    }
}

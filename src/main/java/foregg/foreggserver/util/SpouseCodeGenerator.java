package foregg.foreggserver.util;

import foregg.foreggserver.domain.SpouseCode;
import foregg.foreggserver.repository.SpouseCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Random;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpouseCodeGenerator {

    private final SpouseCodeRepository spouseCodeRepository;

    public String generateRandomCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        char[] code = new char[6];
        Random random = new Random();
        String generatedCode;
        Optional<SpouseCode> existingCode;
        do {
            for (int i = 0; i < 6; i++) {
                code[i] = characters.charAt(random.nextInt(characters.length()));
            }
            generatedCode = new String(code);
            existingCode = spouseCodeRepository.findByCode(generatedCode);
        } while (existingCode.isPresent());
        SpouseCode spouseCode = SpouseCode.builder().code(generatedCode).build();
        spouseCodeRepository.save(spouseCode);
        return generatedCode; // SpouseCode 객체를 String으로 변환하여 반환
    }
}

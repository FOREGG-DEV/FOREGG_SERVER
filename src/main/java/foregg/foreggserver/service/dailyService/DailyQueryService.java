package foregg.foreggserver.service.dailyService;

import foregg.foreggserver.converter.DailyConverter;
import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO;
import foregg.foreggserver.dto.dailyDTO.DailyTotalResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.DailyRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;
    private final UserQueryService userQueryService;

    public DailyTotalResponseDTO getDaily() {
        User user = userQueryService.returnWifeOrHusband();
        List<DailyResponseDTO> resultList = new ArrayList<>();
        Optional<List<Daily>> daily = dailyRepository.findByUser(user);
        if (daily.isEmpty()) {
            return null;
        }
        List<Daily> dailyList = daily.get();
        for (Daily d : dailyList) {
            resultList.add(DailyConverter.toDailyResponseDTO(d));
        }
        return DailyTotalResponseDTO.builder()
                .dailyResponseDTO(resultList)
                .build();
    }

    public Daily getTodayDaily() {
        String today = DateUtil.formatLocalDateTime(LocalDate.now());
        Optional<Daily> byDate = dailyRepository.findByDate(today);
        if (byDate.isEmpty()) {
            return null;
        }
        return byDate.get();
    }

}

package foregg.foreggserver.service.dailyService;

import foregg.foreggserver.apiPayload.exception.handler.DailyHandler;
import foregg.foreggserver.converter.DailyConverter;
import foregg.foreggserver.domain.Daily;
import foregg.foreggserver.domain.SideEffect;
import foregg.foreggserver.domain.User;
import foregg.foreggserver.dto.dailyDTO.DailyResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.DailyRepository;
import foregg.foreggserver.repository.SideEffectRepository;
import foregg.foreggserver.service.userService.UserQueryService;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FOUND_DAILY;
import static foregg.foreggserver.dto.dailyDTO.DailyResponseDTO.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class DailyQueryService {

    private final DailyRepository dailyRepository;
    private final UserQueryService userQueryService;
    private final SideEffectRepository sideEffectRepository;

    public DailyAllResponseDTO getAllDaily(int page) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Pageable pageable = PageRequest.of(page, 10);
        Page<Daily> dailyPage = dailyRepository.findByUser(user, pageable);

        return DailyConverter.toDailyAllResponse(dailyPage);
    }


    public DailyResponseDTO getDaily(String date) {
        User user = userQueryService.returnWifeOrHusband();
        Daily daily = dailyRepository.findByDateAndUser(date, user);
        if (daily == null) {
            throw new DailyHandler(NOT_FOUND_DAILY);
        }
        return DailyConverter.toDailyResponseDTO(daily);
    }

    public List<DailyByCountResponseDTO> dailyByCount(int count) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Daily> dailyList = dailyRepository.findByUserAndCount(user, count).orElseThrow(() -> new DailyHandler(NOT_FOUND_DAILY));
        return DailyConverter.toDailyByCountResponseDTO(dailyList);
    }

    public Daily getTodayDaily(User spouse) {
        String today = DateUtil.formatLocalDateTime(LocalDate.now());
        Optional<Daily> byDate = dailyRepository.findByUserAndDate(spouse,today);
        if (byDate.isEmpty()) {
            return null;
        }
        return byDate.get();
    }

    public List<SideEffect> getNullAndAfterTodaySideEffect() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<SideEffect> result = new ArrayList<>();
        List<SideEffect> foundSideEffect = sideEffectRepository.findByUserAndRecord(user, null);
        for (SideEffect sf : foundSideEffect) {
            LocalDate sideEffectDate = DateUtil.toLocalDate(sf.getDate());
            if (!sideEffectDate.isBefore(LocalDate.now())) {
                result.add(sf);
            }
        }
        return result;
    }

}

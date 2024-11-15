package foregg.foreggserver.service.questionService;

import foregg.foreggserver.apiPayload.exception.handler.DailyHandler;
import foregg.foreggserver.domain.Question;
import foregg.foreggserver.repository.QuestionRepository;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FRIDAY;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuestionQueryService {

    private final QuestionRepository questionRepository;

    public String getSpecialQuestion(String date) {
        Question question = questionRepository.findByDate(date);
        if (question == null) {
            return null;
        }
        return questionRepository.findByDate(date).getContent();
    }

    public String getTodaySpecialQuestion() {
        String today = DateUtil.getTodayDayOfWeek();
        if (!today.equals("Fri")) {
            throw new DailyHandler(NOT_FRIDAY);
        }

        Question question = questionRepository.findByDate(today);
        return question.getContent();
    }


}

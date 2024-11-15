package foregg.foreggserver.service.questionService;

import foregg.foreggserver.apiPayload.exception.handler.DailyHandler;
import foregg.foreggserver.domain.Question;
import foregg.foreggserver.repository.QuestionRepository;
import foregg.foreggserver.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.NOT_FRIDAY;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;

    public String specialQuestion() {

        if (!DateUtil.getTodayDayOfWeek().equals("Fri")) {
            throw new DailyHandler(NOT_FRIDAY);
        }
        String today = LocalDate.now().toString();

        //오늘 날짜를 가진 question이 있다면 그거 추출
        Question question = questionRepository.findByDate(today);
        if (question != null) {
            return question.getContent();
        }

        //오늘 날짜를 가진 question이 없다면 date 필드가 null인 것들 중에 하나 추출
        List<Question> foundQuestions = questionRepository.findByDateIsNull();
        log.info("date 필드가 null인 question "+ foundQuestions);
        if (foundQuestions.isEmpty()) {
            questionRepository.updateAllDatesToNull();
            Optional<Question> randomQuestion = questionRepository.findRandomQuestion();
            if (randomQuestion.isPresent()) {
                randomQuestion.get().setDate(today);
                return randomQuestion.get().getContent();
            } else{
                return null;
            }
        }
        Question firstQuestion = foundQuestions.get(0);
        firstQuestion.setDate(today);

        return firstQuestion.getContent();

    }

}

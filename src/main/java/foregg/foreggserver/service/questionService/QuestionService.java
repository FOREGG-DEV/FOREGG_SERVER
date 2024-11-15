package foregg.foreggserver.service.questionService;

import foregg.foreggserver.domain.Question;
import foregg.foreggserver.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Scheduled(cron = "0 0 0 * * FRI")
    public void setSpecialQuestion() {

        String today = LocalDate.now().toString();

        //오늘 날짜를 가진 question이 없다면 date 필드가 null인 것들 중에 하나 추출
        List<Question> foundQuestions = questionRepository.findByDateIsNull();
        //모든 스폐셜 질문이 한번씩 할당이 되었을 때,
        if (foundQuestions.isEmpty()) {
            questionRepository.updateAllDatesToNull();
            Optional<Question> randomQuestion = questionRepository.findRandomQuestion();
            if (randomQuestion.isPresent()) {
                randomQuestion.get().setDate(today);
            }
        }
        else{
            Question firstQuestion = foundQuestions.get(0);
            firstQuestion.setDate(today);
        }
    }
}

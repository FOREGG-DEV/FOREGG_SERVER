package foregg.foreggserver.service.questionService;

import foregg.foreggserver.domain.Question;
import foregg.foreggserver.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}

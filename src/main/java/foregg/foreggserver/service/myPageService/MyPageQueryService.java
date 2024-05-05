package foregg.foreggserver.service.myPageService;

import foregg.foreggserver.apiPayload.exception.handler.MyPageHandler;
import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.SurgeryHandler;
import foregg.foreggserver.converter.MyPageConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.myPageDTO.MyPageBoardResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageFAQResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageRecordResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageResponseDTO;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.*;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageQueryService {

    private final SurgeryRepository surgeryRepository;
    private final UserQueryService userQueryService;
    private final RecordRepository recordRepository;
    private final BoardRepository boardRepository;
    private final FAQRepository faqRepository;

    public MyPageResponseDTO getInformation() {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        Surgery surgery = surgeryRepository.findByUser(user).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
        return MyPageConverter.toMyPageResponseDTO(user, surgery);
    }

    public List<MyPageRecordResponseDTO> getMedicalInformation(String sort) {
        User user = userQueryService.getUser(SecurityUtil.getCurrentUser());
        List<Record> records = recordRepository.findByUser(user).orElseThrow(() -> new RecordHandler(NOT_FOUND_MY_RECORD));
        List<MyPageRecordResponseDTO> resultList = new ArrayList<>();

        if (sort.equals("medicine")) {
            return getMedicineRecord(records);
        } else if (sort.equals("injection")) {
            return getInjectionRecord(records);
        }else{
            throw new MyPageHandler(_BAD_REQUEST);
        }
    }

    public List<MyPageBoardResponseDTO> getBoards() {
        List<MyPageBoardResponseDTO> resultList = new ArrayList<>();
        List<Board> boards = boardRepository.findAll();

        for (Board board : boards) {
            resultList.add(MyPageConverter.toMyPageBoardResponseDTO(board));
        }
        return resultList;
    }

    public List<MyPageBoardResponseDTO> boardSearch(String keyword) {
        List<MyPageBoardResponseDTO> resultList = new ArrayList<>();
        List<Board> boards = boardRepository.findByTitleContaining(keyword);

        for (Board board : boards) {
            resultList.add(MyPageConverter.toMyPageBoardResponseDTO(board));
        }
        return resultList;
    }

    public List<MyPageFAQResponseDTO> getFAQs() {
        List<MyPageFAQResponseDTO> resultList = new ArrayList<>();
        List<FAQ> faqs = faqRepository.findAll();

        for (FAQ faq : faqs) {
            resultList.add(MyPageConverter.toMyPageFAQResponseDTO(faq));
        }
        return resultList;
    }

    public List<MyPageFAQResponseDTO> faqSearch(String keyword) {
        List<MyPageFAQResponseDTO> resultList = new ArrayList<>();
        List<FAQ> faqs = faqRepository.findByQuestionContaining(keyword);

        for (FAQ faq : faqs) {
            resultList.add(MyPageConverter.toMyPageFAQResponseDTO(faq));
        }
        return resultList;
    }

    private List<MyPageRecordResponseDTO> getMedicineRecord(List<Record> records) {
        List<MyPageRecordResponseDTO> resultList = new ArrayList<>();
        for (Record record : records) {
            if (record.getType() == RecordType.MEDICINE) {
                resultList.add(MyPageConverter.toMyPageRecordResponseDTO(record));
            }
        }
        return resultList;
    }

    private List<MyPageRecordResponseDTO> getInjectionRecord(List<Record> records) {
        List<MyPageRecordResponseDTO> resultList = new ArrayList<>();
        for (Record record : records) {
            if (record.getType() == RecordType.INJECTION) {
                resultList.add(MyPageConverter.toMyPageRecordResponseDTO(record));
            }
        }
        return resultList;
    }

}

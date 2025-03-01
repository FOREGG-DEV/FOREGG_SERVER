package foregg.foreggserver.service.myPageService;

import foregg.foreggserver.apiPayload.exception.handler.MyPageHandler;
import foregg.foreggserver.apiPayload.exception.handler.RecordHandler;
import foregg.foreggserver.apiPayload.exception.handler.SurgeryHandler;
import foregg.foreggserver.converter.MyPageConverter;
import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.domain.enums.RecordType;
import foregg.foreggserver.dto.myPageDTO.*;
import foregg.foreggserver.jwt.SecurityUtil;
import foregg.foreggserver.repository.*;
import foregg.foreggserver.service.userService.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static foregg.foreggserver.apiPayload.code.status.ErrorStatus.*;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageQueryService {

    private final SurgeryRepository surgeryRepository;
    private final UserQueryService userQueryService;
    private final RecordRepository recordRepository;

    public MyPageResponseDTO getInformation() {
        User me = userQueryService.getUser();
        User spouse = userQueryService.returnSpouse();
        String spouseNickname = null;
        Surgery surgery;
        if (SecurityUtil.ifCurrentUserIsHusband()) {
            Surgery foundSurgery = surgeryRepository.findByUser(spouse).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
            surgery = foundSurgery;
        }else{
            Surgery foundSurgery = surgeryRepository.findByUser(me).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
            surgery = foundSurgery;
        }

        if (spouse != null) {
            spouseNickname = spouse.getNickname();
        }
        return MyPageConverter.toMyPageResponseDTO(me, surgery, spouseNickname);
    }

    public MyPageMedicalRecordResponseDTO getMedicalInformation(String sort) {
        User infoUser = userQueryService.returnWifeOrHusband();
        List<Record> records = recordRepository.findByUser(infoUser).orElseThrow(() -> new RecordHandler(NOT_FOUND_MY_RECORD));

        if (sort.equals("medicine")) {
            return getMedicineRecord(records);
        } else if (sort.equals("injection")) {
            return getInjectionRecord(records);
        }else{
            throw new MyPageHandler(_BAD_REQUEST);
        }
    }

    public int getSurgeryCount() {
        User wife = userQueryService.returnWifeOrHusband();
        Surgery surgery = surgeryRepository.findByUser(wife).orElseThrow(() -> new SurgeryHandler(NOT_FOUND_MY_SURGERY));
        return surgery.getCount();
    }

    private MyPageMedicalRecordResponseDTO getMedicineRecord(List<Record> records) {
        List<MyPageRecordResponseDTO> resultList = new ArrayList<>();
        for (Record record : records) {
            if (record.getType() == RecordType.MEDICINE) {
                resultList.add(MyPageConverter.toMyPageRecordResponseDTO(record));
            }
        }
        return MyPageMedicalRecordResponseDTO.builder().myPageRecordResponseDTO(resultList).build();
    }

    private MyPageMedicalRecordResponseDTO getInjectionRecord(List<Record> records) {
        List<MyPageRecordResponseDTO> resultList = new ArrayList<>();
        for (Record record : records) {
            if (record.getType() == RecordType.INJECTION) {
                resultList.add(MyPageConverter.toMyPageRecordResponseDTO(record));
            }
        }
        return MyPageMedicalRecordResponseDTO.builder().myPageRecordResponseDTO(resultList).build();
    }

}

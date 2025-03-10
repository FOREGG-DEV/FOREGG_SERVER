package foregg.foreggserver.converter;

import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.dto.myPageDTO.MyPageRecordResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageResponseDTO;

public class MyPageConverter {

    public static MyPageResponseDTO toMyPageResponseDTO(User me,Surgery surgery, String spouseName) {

        if (surgery == null) {
            return MyPageResponseDTO.builder()
                    .id(me.getId())
                    .nickname(me.getNickname())
                    .surgeryType(null)
                    .count(0)
                    .startDate(null)
                    .ssn(me.getSsn())
                    .spouseCode(me.getSpouseCode())
                    .spouse(spouseName)
                    .challengeNickname(me.getChallengeName()).build();
        }

        return MyPageResponseDTO.builder()
                .id(me.getId())
                .nickname(me.getNickname())
                .surgeryType(surgery.getSurgeryType())
                .count(surgery.getCount())
                .startDate(surgery.getStartAt())
                .ssn(me.getSsn())
                .spouseCode(me.getSpouseCode())
                .spouse(spouseName)
                .challengeNickname(me.getChallengeName()).build();
    }

    public static MyPageRecordResponseDTO toMyPageRecordResponseDTO(Record record) {

        if (record.getDate() != null) {
            return MyPageRecordResponseDTO.builder()
                    .id(record.getId())
                    .date(record.getDate())
                    .startDate(null)
                    .endDate(null)
                    .repeatDays(null)
                    .name(record.getName())
                    .dose(record.getDose())
                    .build();
        }

        return MyPageRecordResponseDTO.builder()
                .id(record.getId())
                .date(null)
                .startDate(record.getStart_date())
                .endDate(record.getEnd_date())
                .repeatDays(record.getRepeat_date())
                .name(record.getName())
                .dose(record.getDose())
                .build();
    }
}

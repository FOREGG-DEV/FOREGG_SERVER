package foregg.foreggserver.converter;

import foregg.foreggserver.domain.*;
import foregg.foreggserver.domain.Record;
import foregg.foreggserver.dto.myPageDTO.MyPageBoardResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageFAQResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageRecordResponseDTO;
import foregg.foreggserver.dto.myPageDTO.MyPageResponseDTO;

public class MyPageConverter {

    public static MyPageResponseDTO toMyPageResponseDTO(User me,Surgery surgery, String spouseName) {

        if (surgery == null) {
            return MyPageResponseDTO.builder()
                    .nickname(me.getNickname())
                    .surgeryType(null)
                    .count(0)
                    .startDate(null)
                    .ssn(me.getSsn())
                    .spouseCode(me.getSpouseCode())
                    .spouse(spouseName).build();
        }

        return MyPageResponseDTO.builder()
                .nickname(me.getNickname())
                .surgeryType(surgery.getSurgeryType())
                .count(surgery.getCount())
                .startDate(surgery.getStartAt())
                .ssn(me.getSsn())
                .spouseCode(me.getSpouseCode())
                .spouse(spouseName).build();
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
                    .build();
        }

        return MyPageRecordResponseDTO.builder()
                .id(record.getId())
                .date(null)
                .startDate(record.getStart_date())
                .endDate(record.getEnd_date())
                .repeatDays(record.getRepeat_date())
                .name(record.getName())
                .build();
    }

    public static MyPageBoardResponseDTO toMyPageBoardResponseDTO(Board board) {
        return MyPageBoardResponseDTO.builder()
                .id(board.getId())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .date(board.getDate())
                .build();
    }

    public static MyPageFAQResponseDTO toMyPageFAQResponseDTO(FAQ faq) {
        return MyPageFAQResponseDTO.builder()
                .id(faq.getId())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .build();
    }

}

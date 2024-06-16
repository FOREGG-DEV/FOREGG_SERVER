package foregg.foreggserver.converter;

import foregg.foreggserver.domain.Information;
import foregg.foreggserver.dto.informationDTO.InformationResponseDTO;
import foregg.foreggserver.util.Parser;

import java.util.ArrayList;
import java.util.List;

public class InformationConverter {

    public static List<InformationResponseDTO> toInformationResponseDTO(List<Information> information) {

        List<InformationResponseDTO> dtos = new ArrayList<>();
        for (Information info : information) {
            InformationResponseDTO result = InformationResponseDTO.builder()
                    .id(info.getId())
                    .informationType(info.getInformationType())
                    .tag(Parser.parseString(info.getTag()))
                    .image(info.getImage())
                    .url(info.getUrl())
                    .build();

            dtos.add(result);
        }
        return dtos;
    }
}

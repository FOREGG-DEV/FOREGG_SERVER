package foregg.foreggserver.service.informationService;

import foregg.foreggserver.converter.InformationConverter;
import foregg.foreggserver.domain.Information;
import foregg.foreggserver.domain.enums.InformationType;
import foregg.foreggserver.dto.informationDTO.InformationResponseDTO;
import foregg.foreggserver.repository.InformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InformationQueryService {

    private final InformationRepository informationRepository;

    public List<InformationResponseDTO> getAll() {
        List<Information> foundInformation = informationRepository.findAll();
        if (foundInformation.isEmpty()) {
            return null;
        }
        return InformationConverter.toInformationResponseDTO(foundInformation);
    }

    public List<InformationResponseDTO> getBySort(InformationType sort) {
        Optional<List<Information>> foundInformation = informationRepository.findByInformationType(sort);
        if (foundInformation.isEmpty()) {
            return null;
        }
        List<Information> information = foundInformation.get();
        return InformationConverter.toInformationResponseDTO(information);
    }

}

package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.domain.enums.InformationType;
import foregg.foreggserver.dto.informationDTO.InformationResponseDTO;
import foregg.foreggserver.service.informationService.InformationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/information")
public class InformationController {

    private final InformationQueryService informationQueryService;

    @GetMapping("")
    public ApiResponse<List<InformationResponseDTO>> all() {
        List<InformationResponseDTO> result = informationQueryService.getAll();
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/bySort")
    public ApiResponse<List<InformationResponseDTO>> bySort(@RequestParam(name = "sort") InformationType sort) {
        List<InformationResponseDTO> result = informationQueryService.getBySort(sort);
        return ApiResponse.onSuccess(result);
    }
}

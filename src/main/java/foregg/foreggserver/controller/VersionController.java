package foregg.foreggserver.controller;

import foregg.foreggserver.apiPayload.ApiResponse;
import foregg.foreggserver.service.versionService.VersionQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/version")
@Tag(name = "앱 버전 추출 API")
public class VersionController {

    private final VersionQueryService versionQueryService;

    @GetMapping("")
    public ApiResponse<String> getAppVersion() {
        return ApiResponse.onSuccess(versionQueryService.getAppVersion());
    }

}

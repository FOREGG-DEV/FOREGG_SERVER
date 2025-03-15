package foregg.foreggserver.service.versionService;

import foregg.foreggserver.domain.Version;
import foregg.foreggserver.repository.VersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class VersionQueryService {

    private final VersionRepository versionRepository;

    public String getAppVersion() {
        Optional<Version> foundVersion = versionRepository.findById(1L);
        return foundVersion.get().getVersion();
    }
}

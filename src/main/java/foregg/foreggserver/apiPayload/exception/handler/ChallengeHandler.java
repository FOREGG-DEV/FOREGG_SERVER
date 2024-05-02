package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class ChallengeHandler extends GeneralException {

    public ChallengeHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}


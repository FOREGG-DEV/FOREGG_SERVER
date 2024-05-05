package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class SurgeryHandler extends GeneralException {

    public SurgeryHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}


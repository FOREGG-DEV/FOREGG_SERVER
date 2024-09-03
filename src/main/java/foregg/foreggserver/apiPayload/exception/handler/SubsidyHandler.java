package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class SubsidyHandler extends GeneralException {

    public SubsidyHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}

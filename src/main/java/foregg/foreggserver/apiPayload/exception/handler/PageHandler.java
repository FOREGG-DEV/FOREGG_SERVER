package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class PageHandler extends GeneralException {

    public PageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }

}

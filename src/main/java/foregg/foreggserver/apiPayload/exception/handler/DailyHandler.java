package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class DailyHandler extends GeneralException {

    public DailyHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

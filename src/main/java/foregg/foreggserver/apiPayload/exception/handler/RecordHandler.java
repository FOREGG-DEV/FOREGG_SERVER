package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class RecordHandler extends GeneralException {

    public RecordHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

package foregg.foreggserver.apiPayload.exception.handler;

import foregg.foreggserver.apiPayload.code.BaseErrorCode;
import foregg.foreggserver.apiPayload.exception.GeneralException;

public class MyPageHandler extends GeneralException {

    public MyPageHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}


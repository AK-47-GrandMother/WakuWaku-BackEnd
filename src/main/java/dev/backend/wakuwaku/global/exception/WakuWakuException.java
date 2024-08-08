package dev.backend.wakuwaku.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WakuWakuException extends RuntimeException {
    private final ExceptionStatus status;

    public static final WakuWakuException INVALID_PARAMETER             = new WakuWakuException(ExceptionStatus.INVALID_PARAMETER);
    public static final WakuWakuException INVALID_URL                   = new WakuWakuException(ExceptionStatus.INVALID_URL);
    public static final WakuWakuException INTERNAL_SERVER_ERROR         = new WakuWakuException(ExceptionStatus.INTERNAL_SERVER_ERROR);
    public static final WakuWakuException NOT_EXISTED_FILE              = new WakuWakuException(ExceptionStatus.NOT_EXISTED_FILE);
    public static final WakuWakuException DUPLICATED_EMAIL              = new WakuWakuException(ExceptionStatus.DUPLICATED_EMAIL);
    public static final WakuWakuException NONE_USER                     = new WakuWakuException(ExceptionStatus.NONE_USER);
    public static final WakuWakuException INVALID_SEARCH_WORD           = new WakuWakuException(ExceptionStatus.INVALID_SEARCH_WORD);
    public static final WakuWakuException NOT_EXISTED_NEXT_PAGE_TOKEN   = new WakuWakuException(ExceptionStatus.NOT_EXISTED_NEXT_PAGE_TOKEN);
    public static final WakuWakuException NOT_EXISTED_PLACE_ID          = new WakuWakuException(ExceptionStatus.NOT_EXISTED_PLACE_ID);
    public static final WakuWakuException NOT_EXISTED_DETAILS_RESPONSE  = new WakuWakuException(ExceptionStatus.NOT_EXISTED_DETAILS_RESPONSE);
    public static final WakuWakuException NONE_PHOTO_URL                = new WakuWakuException(ExceptionStatus.NONE_PHOTO_URL);
    public static final WakuWakuException NOT_EXISTED_MEMBER_INFO       = new WakuWakuException(ExceptionStatus.NOT_EXISTED_MEMBER_INFO);

    public static final WakuWakuException FAILED_TO_LOGIN               = new WakuWakuException(ExceptionStatus.FALIED_TO_LOGIN);

    public static final WakuWakuException NOT_EXISTED_SOCIAL_TYPE       = new WakuWakuException(ExceptionStatus.NOT_EXISTED_SOCIAL_TYPE);

}

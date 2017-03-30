package enums;

/**
 * Created by Natnael on 3/29/2017.
 *
 */
public enum ServerStatus {
    SUCCESS,
    INVALIDINPUT,
    NULLITY,
    UNKNOWNERROR;

    public boolean isSuccessful() {
        return this == SUCCESS;
    }
}

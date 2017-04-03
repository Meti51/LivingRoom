package enums;

/**
 * Currently not in use.
 *
 * Created on 3/29/2017.
 * @author Natnael Seifu [seifu003]
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

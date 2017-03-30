package server.command;

/**
 * Created by Natnael on 3/30/2017.
 *
 */
public class Command {

    private String function;
    private String payload;
    private String raw;

    public Command(String raw) {
        this.raw = raw;
    }

    public String getFunction() {
        return parse(raw)[0];
    }

    public String getPayload() {
        return parse(raw)[1];
    }

    /**
     * validation has to happen before this method is called
     *
     * @param command - raw message
     * @return - expected [FUNC,PAYLOAD]
     */
    private String[] parse(String command) {

        String clean = command.replace("<", "");
        clean = clean.replace(">", "");
        String cmd[] = clean.split(",");

        /* Expected result [function, string] */
        String[] result;
        result = new String[2];

        /* Prevent from possible null exception */
        result[0] = "";
        result[1] = "";

        String payload = "";
        for (int i = 1; i < cmd.length; i++) {
            payload = payload.concat(cmd[i].trim());
            if (i < cmd.length - 1) {
                payload = payload.concat(",");
            }
        }
        result[1] = payload;
        result[0] = cmd[0].trim().toUpperCase();

        return result;
    }
}

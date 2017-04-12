package server.command;


/**
 * Parse Raw commands
 *
 * Created on 3/30/2017.
 * @author Natnael Seifu [seifu003]
 */
public class Command {

    private String function;
    private String payload;

    public Command(String raw) {
        String[] p = parse(raw);
        this.function = p[0];
        this.payload = p[1];

    }

    public String getFunction() {
        return function;
    }

    public String getPayload() {
        return payload;
    }

    public String toString() {
        return function + " " + payload;
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

        /* message after FUNC string is removed */
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

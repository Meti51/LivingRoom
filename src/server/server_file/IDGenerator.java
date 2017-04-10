package server.server_file;

import java.util.UUID;

/**
 *
 * Created on 4/9/2017.
 * @author Natnael Seifu [seifu003]
 */
class IDGenerator {

  /**
   *
   * @return -
   * @see UUID
   */
  static String issueUniqueID() {
    UUID unique_id = UUID.randomUUID();

    return unique_id.toString();
  }
}

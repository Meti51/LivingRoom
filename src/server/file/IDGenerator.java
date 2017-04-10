package server.file;

/**
 *
 * Created on 4/9/2017.
 * @author Natnael Seifu [seifu003]
 */
class IDGenerator {

  /**
   *
   * @param ip_addr -
   * @param filename -
   * @return hash value of concat(ip_addr, filename)
   */
  static String getUniqueID(String ip_addr, String filename) {
    String combined = ip_addr + filename;
    int unique_id = combined.hashCode();

    return String.valueOf(unique_id);
  }
}

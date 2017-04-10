package server.file;

/**
 * Created on 4/9/2017.
 * @author Natnael Seifu [seifu003]
 */
public class File {

  private String id;
  private String client_ip_addr;
  private String client_port;
  /* filename == path of directory/ + filename */
  private String filename;

  public File(String filename, String client_ip_addr, String client_port) {
    this.client_ip_addr = client_ip_addr;
    this.client_port = client_port;
    this.filename = filename;
    this.id = IDGenerator.getUniqueID(client_ip_addr, filename);
  }

  public String getId() {
    return id;
  }

  public String getClient_ip_addr() {
    return client_ip_addr;
  }

  public String getClient_port() {
    return client_port;
  }

  public String getFilename() {
    return filename;
  }

  public String toString() {
    return id + " " + filename + " " +
        client_ip_addr + " " + client_port;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof File) {
      File of = (File) obj;

      if (of.getId().equals(this.getId())) {
        return true;
      }
    }

    return false;
  }
}

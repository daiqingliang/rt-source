package sun.rmi.transport.proxy;

final class CGIPingCommand implements CGICommandHandler {
  public String getName() { return "ping"; }
  
  public void execute(String paramString) {
    System.out.println("Status: 200 OK");
    System.out.println("Content-type: application/octet-stream");
    System.out.println("Content-length: 0");
    System.out.println("");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\CGIPingCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
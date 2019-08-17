package sun.rmi.transport.proxy;

interface CGICommandHandler {
  String getName();
  
  void execute(String paramString) throws CGIClientException, CGIServerException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\transport\proxy\CGICommandHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
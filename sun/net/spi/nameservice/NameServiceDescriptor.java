package sun.net.spi.nameservice;

public interface NameServiceDescriptor {
  NameService createNameService() throws Exception;
  
  String getProviderName();
  
  String getType();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\spi\nameservice\NameServiceDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
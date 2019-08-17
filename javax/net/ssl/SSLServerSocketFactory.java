package javax.net.ssl;

import java.security.NoSuchAlgorithmException;
import javax.net.ServerSocketFactory;

public abstract class SSLServerSocketFactory extends ServerSocketFactory {
  private static SSLServerSocketFactory theFactory;
  
  private static boolean propertyChecked;
  
  private static void log(String paramString) {
    if (SSLSocketFactory.DEBUG)
      System.out.println(paramString); 
  }
  
  public static ServerSocketFactory getDefault() {
    if (theFactory != null)
      return theFactory; 
    if (!propertyChecked) {
      propertyChecked = true;
      String str = SSLSocketFactory.getSecurityProperty("ssl.ServerSocketFactory.provider");
      if (str != null) {
        log("setting up default SSLServerSocketFactory");
        try {
          Class clazz = null;
          try {
            clazz = Class.forName(str);
          } catch (ClassNotFoundException classNotFoundException) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            if (classLoader != null)
              clazz = classLoader.loadClass(str); 
          } 
          log("class " + str + " is loaded");
          SSLServerSocketFactory sSLServerSocketFactory;
          (sSLServerSocketFactory = (SSLServerSocketFactory)clazz.newInstance()).log("instantiated an instance of class " + str);
          theFactory = sSLServerSocketFactory;
          return sSLServerSocketFactory;
        } catch (Exception exception) {
          log("SSLServerSocketFactory instantiation failed: " + exception);
          theFactory = new DefaultSSLServerSocketFactory(exception);
          return theFactory;
        } 
      } 
    } 
    try {
      return SSLContext.getDefault().getServerSocketFactory();
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      return new DefaultSSLServerSocketFactory(noSuchAlgorithmException);
    } 
  }
  
  public abstract String[] getDefaultCipherSuites();
  
  public abstract String[] getSupportedCipherSuites();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\SSLServerSocketFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package sun.net.www.protocol.http;

import java.io.IOException;
import java.lang.reflect.Constructor;
import sun.util.logging.PlatformLogger;

public abstract class Negotiator {
  static Negotiator getNegotiator(HttpCallerInfo paramHttpCallerInfo) {
    Constructor constructor;
    try {
      Class clazz = Class.forName("sun.net.www.protocol.http.spnego.NegotiatorImpl", true, null);
      constructor = clazz.getConstructor(new Class[] { HttpCallerInfo.class });
    } catch (ClassNotFoundException classNotFoundException) {
      finest(classNotFoundException);
      return null;
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new AssertionError(reflectiveOperationException);
    } 
    try {
      return (Negotiator)constructor.newInstance(new Object[] { paramHttpCallerInfo });
    } catch (ReflectiveOperationException reflectiveOperationException) {
      finest(reflectiveOperationException);
      Throwable throwable = reflectiveOperationException.getCause();
      if (throwable != null && throwable instanceof Exception)
        finest((Exception)throwable); 
      return null;
    } 
  }
  
  public abstract byte[] firstToken() throws IOException;
  
  public abstract byte[] nextToken(byte[] paramArrayOfByte) throws IOException;
  
  private static void finest(Exception paramException) {
    PlatformLogger platformLogger = HttpURLConnection.getHttpLogger();
    if (platformLogger.isLoggable(PlatformLogger.Level.FINEST))
      platformLogger.finest("NegotiateAuthentication: " + paramException); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\protocol\http\Negotiator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
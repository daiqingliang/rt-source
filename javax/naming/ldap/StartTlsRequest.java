package javax.naming.ldap;

import com.sun.naming.internal.VersionHelper;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;
import javax.naming.ConfigurationException;
import javax.naming.NamingException;

public class StartTlsRequest implements ExtendedRequest {
  public static final String OID = "1.3.6.1.4.1.1466.20037";
  
  private static final long serialVersionUID = 4441679576360753397L;
  
  public String getID() { return "1.3.6.1.4.1.1466.20037"; }
  
  public byte[] getEncodedValue() { return null; }
  
  public ExtendedResponse createExtendedResponse(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws NamingException {
    if (paramString != null && !paramString.equals("1.3.6.1.4.1.1466.20037"))
      throw new ConfigurationException("Start TLS received the following response instead of 1.3.6.1.4.1.1466.20037: " + paramString); 
    StartTlsResponse startTlsResponse = null;
    ServiceLoader serviceLoader = ServiceLoader.load(StartTlsResponse.class, getContextClassLoader());
    Iterator iterator = serviceLoader.iterator();
    while (startTlsResponse == null && privilegedHasNext(iterator))
      startTlsResponse = (StartTlsResponse)iterator.next(); 
    if (startTlsResponse != null)
      return startTlsResponse; 
    try {
      VersionHelper versionHelper = VersionHelper.getVersionHelper();
      Class clazz = versionHelper.loadClass("com.sun.jndi.ldap.ext.StartTlsResponseImpl");
      startTlsResponse = (StartTlsResponse)clazz.newInstance();
    } catch (IllegalAccessException illegalAccessException) {
      throw wrapException(illegalAccessException);
    } catch (InstantiationException instantiationException) {
      throw wrapException(instantiationException);
    } catch (ClassNotFoundException classNotFoundException) {
      throw wrapException(classNotFoundException);
    } 
    return startTlsResponse;
  }
  
  private ConfigurationException wrapException(Exception paramException) {
    ConfigurationException configurationException = new ConfigurationException("Cannot load implementation of javax.naming.ldap.StartTlsResponse");
    configurationException.setRootCause(paramException);
    return configurationException;
  }
  
  private final ClassLoader getContextClassLoader() { return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return Thread.currentThread().getContextClassLoader(); }
        }); }
  
  private static final boolean privilegedHasNext(final Iterator<StartTlsResponse> iter) {
    Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(iter.hasNext()); }
        });
    return bool.booleanValue();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\StartTlsRequest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
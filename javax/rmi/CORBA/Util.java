package javax.rmi.CORBA;

import com.sun.corba.se.impl.javax.rmi.CORBA.Util;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.SerializablePermission;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class Util {
  private static final UtilDelegate utilDelegate = (UtilDelegate)createDelegate("javax.rmi.CORBA.UtilClass");
  
  private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";
  
  private static final String ALLOW_CREATEVALUEHANDLER_PROP = "jdk.rmi.CORBA.allowCustomValueHandler";
  
  private static boolean allowCustomValueHandler = readAllowCustomValueHandlerProperty();
  
  private static boolean readAllowCustomValueHandlerProperty() { return ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() { return Boolean.valueOf(Boolean.getBoolean("jdk.rmi.CORBA.allowCustomValueHandler")); }
        })).booleanValue(); }
  
  public static RemoteException mapSystemException(SystemException paramSystemException) { return (utilDelegate != null) ? utilDelegate.mapSystemException(paramSystemException) : null; }
  
  public static void writeAny(OutputStream paramOutputStream, Object paramObject) {
    if (utilDelegate != null)
      utilDelegate.writeAny(paramOutputStream, paramObject); 
  }
  
  public static Object readAny(InputStream paramInputStream) { return (utilDelegate != null) ? utilDelegate.readAny(paramInputStream) : null; }
  
  public static void writeRemoteObject(OutputStream paramOutputStream, Object paramObject) {
    if (utilDelegate != null)
      utilDelegate.writeRemoteObject(paramOutputStream, paramObject); 
  }
  
  public static void writeAbstractObject(OutputStream paramOutputStream, Object paramObject) {
    if (utilDelegate != null)
      utilDelegate.writeAbstractObject(paramOutputStream, paramObject); 
  }
  
  public static void registerTarget(Tie paramTie, Remote paramRemote) {
    if (utilDelegate != null)
      utilDelegate.registerTarget(paramTie, paramRemote); 
  }
  
  public static void unexportObject(Remote paramRemote) throws NoSuchObjectException {
    if (utilDelegate != null)
      utilDelegate.unexportObject(paramRemote); 
  }
  
  public static Tie getTie(Remote paramRemote) { return (utilDelegate != null) ? utilDelegate.getTie(paramRemote) : null; }
  
  public static ValueHandler createValueHandler() {
    isCustomSerializationPermitted();
    return (utilDelegate != null) ? utilDelegate.createValueHandler() : null;
  }
  
  public static String getCodebase(Class paramClass) { return (utilDelegate != null) ? utilDelegate.getCodebase(paramClass) : null; }
  
  public static Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader) throws ClassNotFoundException { return (utilDelegate != null) ? utilDelegate.loadClass(paramString1, paramString2, paramClassLoader) : null; }
  
  public static boolean isLocal(Stub paramStub) throws RemoteException { return (utilDelegate != null) ? utilDelegate.isLocal(paramStub) : 0; }
  
  public static RemoteException wrapException(Throwable paramThrowable) { return (utilDelegate != null) ? utilDelegate.wrapException(paramThrowable) : null; }
  
  public static Object[] copyObjects(Object[] paramArrayOfObject, ORB paramORB) throws RemoteException { return (utilDelegate != null) ? utilDelegate.copyObjects(paramArrayOfObject, paramORB) : null; }
  
  public static Object copyObject(Object paramObject, ORB paramORB) throws RemoteException { return (utilDelegate != null) ? utilDelegate.copyObject(paramObject, paramORB) : null; }
  
  private static Object createDelegate(String paramString) {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null) {
      Properties properties = getORBPropertiesFile();
      if (properties != null)
        str = properties.getProperty(paramString); 
    } 
    if (str == null)
      return new Util(); 
    try {
      return loadDelegateClass(str).newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      INITIALIZE iNITIALIZE = new INITIALIZE("Cannot instantiate " + str);
      iNITIALIZE.initCause(classNotFoundException);
      throw iNITIALIZE;
    } catch (Exception exception) {
      INITIALIZE iNITIALIZE = new INITIALIZE("Error while instantiating" + str);
      iNITIALIZE.initCause(exception);
      throw iNITIALIZE;
    } 
  }
  
  private static Class loadDelegateClass(String paramString) throws ClassNotFoundException {
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      return Class.forName(paramString, false, classLoader);
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        return RMIClassLoader.loadClass(paramString);
      } catch (MalformedURLException classNotFoundException) {
        String str = "Could not load " + paramString + ": " + classNotFoundException.toString();
        ClassNotFoundException classNotFoundException1 = new ClassNotFoundException(str);
        throw classNotFoundException1;
      } 
    } 
  }
  
  private static Properties getORBPropertiesFile() { return (Properties)AccessController.doPrivileged(new GetORBPropertiesFileAction()); }
  
  private static void isCustomSerializationPermitted() {
    SecurityManager securityManager = System.getSecurityManager();
    if (!allowCustomValueHandler && securityManager != null)
      securityManager.checkPermission(new SerializablePermission("enableCustomValueHandler")); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.rmi;

import com.sun.corba.se.impl.javax.rmi.PortableRemoteObject;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.net.MalformedURLException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.util.Properties;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;
import org.omg.CORBA.INITIALIZE;

public class PortableRemoteObject {
  private static final PortableRemoteObjectDelegate proDelegate = (PortableRemoteObjectDelegate)createDelegate("javax.rmi.CORBA.PortableRemoteObjectClass");
  
  private static final String PortableRemoteObjectClassKey = "javax.rmi.CORBA.PortableRemoteObjectClass";
  
  protected PortableRemoteObject() throws RemoteException {
    if (proDelegate != null)
      exportObject((Remote)this); 
  }
  
  public static void exportObject(Remote paramRemote) throws RemoteException {
    if (proDelegate != null)
      proDelegate.exportObject(paramRemote); 
  }
  
  public static Remote toStub(Remote paramRemote) throws NoSuchObjectException { return (proDelegate != null) ? proDelegate.toStub(paramRemote) : null; }
  
  public static void unexportObject(Remote paramRemote) throws RemoteException {
    if (proDelegate != null)
      proDelegate.unexportObject(paramRemote); 
  }
  
  public static Object narrow(Object paramObject, Class paramClass) throws ClassCastException { return (proDelegate != null) ? proDelegate.narrow(paramObject, paramClass) : null; }
  
  public static void connect(Remote paramRemote1, Remote paramRemote2) throws RemoteException {
    if (proDelegate != null)
      proDelegate.connect(paramRemote1, paramRemote2); 
  }
  
  private static Object createDelegate(String paramString) {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null) {
      Properties properties = getORBPropertiesFile();
      if (properties != null)
        str = properties.getProperty(paramString); 
    } 
    if (str == null)
      return new PortableRemoteObject(); 
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
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\PortableRemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
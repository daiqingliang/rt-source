package javax.rmi.CORBA;

import com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public abstract class Stub extends ObjectImpl implements Serializable {
  private static final long serialVersionUID = 1087775603798577179L;
  
  private StubDelegate stubDelegate = null;
  
  private static Class stubDelegateClass = null;
  
  private static final String StubClassKey = "javax.rmi.CORBA.StubClass";
  
  public int hashCode() {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    return (this.stubDelegate != null) ? this.stubDelegate.hashCode(this) : 0;
  }
  
  public boolean equals(Object paramObject) {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    return (this.stubDelegate != null) ? this.stubDelegate.equals(this, paramObject) : 0;
  }
  
  public String toString() {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    if (this.stubDelegate != null) {
      String str = this.stubDelegate.toString(this);
      return (str == null) ? super.toString() : str;
    } 
    return super.toString();
  }
  
  public void connect(ORB paramORB) throws RemoteException {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    if (this.stubDelegate != null)
      this.stubDelegate.connect(this, paramORB); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    if (this.stubDelegate != null)
      this.stubDelegate.readObject(this, paramObjectInputStream); 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.stubDelegate == null)
      setDefaultDelegate(); 
    if (this.stubDelegate != null)
      this.stubDelegate.writeObject(this, paramObjectOutputStream); 
  }
  
  private void setDefaultDelegate() {
    if (stubDelegateClass != null)
      try {
        this.stubDelegate = (StubDelegate)stubDelegateClass.newInstance();
      } catch (Exception exception) {} 
  }
  
  private static Object createDelegate(String paramString) {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
    if (str == null) {
      Properties properties = getORBPropertiesFile();
      if (properties != null)
        str = properties.getProperty(paramString); 
    } 
    if (str == null)
      return new StubDelegateImpl(); 
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
  
  static  {
    Object object = createDelegate("javax.rmi.CORBA.StubClass");
    if (object != null)
      stubDelegateClass = object.getClass(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\Stub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
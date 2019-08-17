package sun.rmi.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.util.HashMap;
import java.util.Map;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import sun.misc.VM;
import sun.security.action.GetPropertyAction;

public class MarshalInputStream extends ObjectInputStream {
  private static final boolean useCodebaseOnlyProperty = !((String)AccessController.doPrivileged(new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true"))).equalsIgnoreCase("false");
  
  protected static Map<String, Class<?>> permittedSunClasses = new HashMap(3);
  
  private boolean skipDefaultResolveClass = false;
  
  private final Map<Object, Runnable> doneCallbacks = new HashMap(3);
  
  private boolean useCodebaseOnly = useCodebaseOnlyProperty;
  
  public MarshalInputStream(InputStream paramInputStream) throws IOException, StreamCorruptedException { super(paramInputStream); }
  
  public Runnable getDoneCallback(Object paramObject) { return (Runnable)this.doneCallbacks.get(paramObject); }
  
  public void setDoneCallback(Object paramObject, Runnable paramRunnable) { this.doneCallbacks.put(paramObject, paramRunnable); }
  
  public void done() {
    for (Runnable runnable : this.doneCallbacks.values())
      runnable.run(); 
    this.doneCallbacks.clear();
  }
  
  public void close() {
    done();
    super.close();
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException {
    Object object = readLocation();
    String str1 = paramObjectStreamClass.getName();
    ClassLoader classLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String str2 = null;
    if (!this.useCodebaseOnly && object instanceof String)
      str2 = (String)object; 
    try {
      return RMIClassLoader.loadClass(str2, str1, classLoader);
    } catch (AccessControlException accessControlException) {
      return checkSunClass(str1, accessControlException);
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        if (Character.isLowerCase(str1.charAt(0)) && str1.indexOf('.') == -1)
          return super.resolveClass(paramObjectStreamClass); 
      } catch (ClassNotFoundException classNotFoundException1) {}
      throw classNotFoundException;
    } 
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString) throws IOException, ClassNotFoundException {
    StreamChecker streamChecker1 = this.streamChecker;
    if (streamChecker1 != null)
      streamChecker1.checkProxyInterfaceNames(paramArrayOfString); 
    Object object = readLocation();
    ClassLoader classLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
    String str = null;
    if (!this.useCodebaseOnly && object instanceof String)
      str = (String)object; 
    return RMIClassLoader.loadProxyClass(str, paramArrayOfString, classLoader);
  }
  
  private static ClassLoader latestUserDefinedLoader() { return VM.latestUserDefinedLoader(); }
  
  private Class<?> checkSunClass(String paramString, AccessControlException paramAccessControlException) throws AccessControlException {
    Permission permission = paramAccessControlException.getPermission();
    String str = null;
    if (permission != null)
      str = permission.getName(); 
    Class clazz = (Class)permittedSunClasses.get(paramString);
    if (str == null || clazz == null || (!str.equals("accessClassInPackage.sun.rmi.server") && !str.equals("accessClassInPackage.sun.rmi.registry")))
      throw paramAccessControlException; 
    return clazz;
  }
  
  protected Object readLocation() throws IOException, ClassNotFoundException { return readObject(); }
  
  void skipDefaultResolveClass() { this.skipDefaultResolveClass = true; }
  
  void useCodebaseOnly() { this.useCodebaseOnly = true; }
  
  void setStreamChecker(StreamChecker paramStreamChecker) {
    this.streamChecker = paramStreamChecker;
    SharedSecrets.getJavaObjectInputStreamAccess().setValidator(this, paramStreamChecker);
  }
  
  protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    ObjectStreamClass objectStreamClass = super.readClassDescriptor();
    validateDesc(objectStreamClass);
    return objectStreamClass;
  }
  
  private void validateDesc(ObjectStreamClass paramObjectStreamClass) {
    StreamChecker streamChecker1;
    synchronized (this) {
      streamChecker1 = this.streamChecker;
    } 
    if (streamChecker1 != null)
      streamChecker1.validateDescriptor(paramObjectStreamClass); 
  }
  
  static  {
    try {
      String str1 = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
      String str2 = "sun.rmi.registry.RegistryImpl_Stub";
      permittedSunClasses.put(str1, Class.forName(str1));
      permittedSunClasses.put(str2, Class.forName(str2));
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError("Missing system class: " + classNotFoundException.getMessage());
    } 
  }
  
  static interface StreamChecker extends ObjectStreamClassValidator {
    void checkProxyInterfaceNames(String[] param1ArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\rmi\server\MarshalInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
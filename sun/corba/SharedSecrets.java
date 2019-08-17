package sun.corba;

import java.lang.reflect.Method;
import sun.misc.JavaOISAccess;
import sun.misc.Unsafe;

public class SharedSecrets {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static JavaCorbaAccess javaCorbaAccess;
  
  private static final Method getJavaOISAccessMethod;
  
  private static JavaOISAccess javaOISAccess;
  
  public static JavaOISAccess getJavaOISAccess() {
    if (javaOISAccess == null)
      try {
        javaOISAccess = (JavaOISAccess)getJavaOISAccessMethod.invoke(null, new Object[0]);
      } catch (Exception exception) {
        throw new ExceptionInInitializerError(exception);
      }  
    return javaOISAccess;
  }
  
  public static JavaCorbaAccess getJavaCorbaAccess() {
    if (javaCorbaAccess == null)
      unsafe.ensureClassInitialized(com.sun.corba.se.impl.io.ValueUtility.class); 
    return javaCorbaAccess;
  }
  
  public static void setJavaCorbaAccess(JavaCorbaAccess paramJavaCorbaAccess) { javaCorbaAccess = paramJavaCorbaAccess; }
  
  static  {
    try {
      Class clazz = Class.forName("sun.misc.SharedSecrets");
      getJavaOISAccessMethod = clazz.getMethod("getJavaOISAccess", new Class[0]);
    } catch (Exception exception) {
      throw new ExceptionInInitializerError(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\corba\SharedSecrets.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

class DefaultDatagramSocketImplFactory {
  private static final Class<?> prefixImplClass;
  
  private static float version;
  
  private static boolean preferIPv4Stack = false;
  
  private static final boolean useDualStackImpl;
  
  private static String exclBindProp;
  
  private static final boolean exclusiveBind;
  
  static DatagramSocketImpl createDatagramSocketImpl(boolean paramBoolean) throws SocketException {
    if (prefixImplClass != null)
      try {
        return (DatagramSocketImpl)prefixImplClass.newInstance();
      } catch (Exception exception) {
        throw new SocketException("can't instantiate DatagramSocketImpl");
      }  
    return (useDualStackImpl && !paramBoolean) ? new DualStackPlainDatagramSocketImpl(exclusiveBind) : new TwoStacksPlainDatagramSocketImpl((exclusiveBind && !paramBoolean));
  }
  
  static  {
    Class clazz = null;
    boolean bool1 = false;
    boolean bool2 = true;
    AccessController.doPrivileged(new PrivilegedAction<Object>() {
          public Object run() {
            version = 0.0F;
            try {
              version = Float.parseFloat(System.getProperties().getProperty("os.version"));
              preferIPv4Stack = Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack"));
              exclBindProp = System.getProperty("sun.net.useExclusiveBind");
            } catch (NumberFormatException numberFormatException) {
              assert false : numberFormatException;
            } 
            return null;
          }
        });
    if (version >= 6.0D && !preferIPv4Stack)
      bool1 = true; 
    if (exclBindProp != null) {
      bool2 = (exclBindProp.length() == 0) ? 1 : Boolean.parseBoolean(exclBindProp);
    } else if (version < 6.0D) {
      bool2 = false;
    } 
    String str = null;
    try {
      str = (String)AccessController.doPrivileged(new GetPropertyAction("impl.prefix", null));
      if (str != null)
        clazz = Class.forName("java.net." + str + "DatagramSocketImpl"); 
    } catch (Exception exception) {
      System.err.println("Can't find class: java.net." + str + "DatagramSocketImpl: check impl.prefix property");
    } 
    prefixImplClass = clazz;
    useDualStackImpl = bool1;
    exclusiveBind = bool2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\net\DefaultDatagramSocketImplFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
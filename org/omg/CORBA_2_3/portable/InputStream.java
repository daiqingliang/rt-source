package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.InputStream;

public abstract class InputStream extends InputStream {
  private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowInputStreamSubclass";
  
  private static final boolean allowSubclass = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = System.getProperty("jdk.corba.allowInputStreamSubclass");
          return Boolean.valueOf((str == null) ? false : (!str.equalsIgnoreCase("false")));
        }
      })).booleanValue();
  
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && !allowSubclass)
      securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation")); 
    return null;
  }
  
  private InputStream(Void paramVoid) {}
  
  public InputStream() { this(checkPermission()); }
  
  public Serializable read_value() { throw new NO_IMPLEMENT(); }
  
  public Serializable read_value(Class paramClass) { throw new NO_IMPLEMENT(); }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper) { throw new NO_IMPLEMENT(); }
  
  public Serializable read_value(String paramString) { throw new NO_IMPLEMENT(); }
  
  public Serializable read_value(Serializable paramSerializable) { throw new NO_IMPLEMENT(); }
  
  public Object read_abstract_interface() { throw new NO_IMPLEMENT(); }
  
  public Object read_abstract_interface(Class paramClass) { throw new NO_IMPLEMENT(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA_2_3\portable\InputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package org.omg.CORBA_2_3.portable;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.OutputStream;

public abstract class OutputStream extends OutputStream {
  private static final String ALLOW_SUBCLASS_PROP = "jdk.corba.allowOutputStreamSubclass";
  
  private static final boolean allowSubclass = ((Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str = System.getProperty("jdk.corba.allowOutputStreamSubclass");
          return Boolean.valueOf((str == null) ? false : (!str.equalsIgnoreCase("false")));
        }
      })).booleanValue();
  
  private static Void checkPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && !allowSubclass)
      securityManager.checkPermission(new SerializablePermission("enableSubclassImplementation")); 
    return null;
  }
  
  private OutputStream(Void paramVoid) {}
  
  public OutputStream() { this(checkPermission()); }
  
  public void write_value(Serializable paramSerializable) { throw new NO_IMPLEMENT(); }
  
  public void write_value(Serializable paramSerializable, Class paramClass) { throw new NO_IMPLEMENT(); }
  
  public void write_value(Serializable paramSerializable, String paramString) { throw new NO_IMPLEMENT(); }
  
  public void write_value(Serializable paramSerializable, BoxedValueHelper paramBoxedValueHelper) { throw new NO_IMPLEMENT(); }
  
  public void write_abstract_interface(Object paramObject) { throw new NO_IMPLEMENT(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA_2_3\portable\OutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
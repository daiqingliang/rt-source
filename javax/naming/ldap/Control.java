package javax.naming.ldap;

import java.io.Serializable;

public interface Control extends Serializable {
  public static final boolean CRITICAL = true;
  
  public static final boolean NONCRITICAL = false;
  
  String getID();
  
  boolean isCritical();
  
  byte[] getEncodedValue();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\Control.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
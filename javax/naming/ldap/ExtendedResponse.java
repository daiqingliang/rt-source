package javax.naming.ldap;

import java.io.Serializable;

public interface ExtendedResponse extends Serializable {
  String getID();
  
  byte[] getEncodedValue();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\ExtendedResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
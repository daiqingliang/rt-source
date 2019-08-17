package javax.naming.ldap;

import javax.naming.NamingException;

public interface HasControls {
  Control[] getControls() throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\HasControls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
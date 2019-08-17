package javax.naming.ldap;

import javax.naming.NamingException;

public interface UnsolicitedNotification extends ExtendedResponse, HasControls {
  String[] getReferrals();
  
  NamingException getException();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\UnsolicitedNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
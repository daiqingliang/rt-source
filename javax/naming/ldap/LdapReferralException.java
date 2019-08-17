package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ReferralException;

public abstract class LdapReferralException extends ReferralException {
  private static final long serialVersionUID = -1668992791764950804L;
  
  protected LdapReferralException(String paramString) { super(paramString); }
  
  protected LdapReferralException() {}
  
  public abstract Context getReferralContext() throws NamingException;
  
  public abstract Context getReferralContext(Hashtable<?, ?> paramHashtable) throws NamingException;
  
  public abstract Context getReferralContext(Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl) throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\LdapReferralException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
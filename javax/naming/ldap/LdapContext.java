package javax.naming.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public interface LdapContext extends DirContext {
  public static final String CONTROL_FACTORIES = "java.naming.factory.control";
  
  ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest) throws NamingException;
  
  LdapContext newInstance(Control[] paramArrayOfControl) throws NamingException;
  
  void reconnect(Control[] paramArrayOfControl) throws NamingException;
  
  Control[] getConnectControls() throws NamingException;
  
  void setRequestControls(Control[] paramArrayOfControl) throws NamingException;
  
  Control[] getRequestControls() throws NamingException;
  
  Control[] getResponseControls() throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\LdapContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.naming.ldap;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.NotContextException;
import javax.naming.directory.InitialDirContext;

public class InitialLdapContext extends InitialDirContext implements LdapContext {
  private static final String BIND_CONTROLS_PROPERTY = "java.naming.ldap.control.connect";
  
  public InitialLdapContext() throws NamingException { super(null); }
  
  public InitialLdapContext(Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl) throws NamingException {
    super(true);
    Hashtable hashtable = (paramHashtable == null) ? new Hashtable(11) : (Hashtable)paramHashtable.clone();
    if (paramArrayOfControl != null) {
      Control[] arrayOfControl = new Control[paramArrayOfControl.length];
      System.arraycopy(paramArrayOfControl, 0, arrayOfControl, 0, paramArrayOfControl.length);
      hashtable.put("java.naming.ldap.control.connect", arrayOfControl);
    } 
    hashtable.put("java.naming.ldap.version", "3");
    init(hashtable);
  }
  
  private LdapContext getDefaultLdapInitCtx() throws NamingException {
    Context context = getDefaultInitCtx();
    if (!(context instanceof LdapContext)) {
      if (context == null)
        throw new NoInitialContextException(); 
      throw new NotContextException("Not an instance of LdapContext");
    } 
    return (LdapContext)context;
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest) throws NamingException { return getDefaultLdapInitCtx().extendedOperation(paramExtendedRequest); }
  
  public LdapContext newInstance(Control[] paramArrayOfControl) throws NamingException { return getDefaultLdapInitCtx().newInstance(paramArrayOfControl); }
  
  public void reconnect(Control[] paramArrayOfControl) throws NamingException { getDefaultLdapInitCtx().reconnect(paramArrayOfControl); }
  
  public Control[] getConnectControls() throws NamingException { return getDefaultLdapInitCtx().getConnectControls(); }
  
  public void setRequestControls(Control[] paramArrayOfControl) throws NamingException { getDefaultLdapInitCtx().setRequestControls(paramArrayOfControl); }
  
  public Control[] getRequestControls() throws NamingException { return getDefaultLdapInitCtx().getRequestControls(); }
  
  public Control[] getResponseControls() throws NamingException { return getDefaultLdapInitCtx().getResponseControls(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\InitialLdapContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
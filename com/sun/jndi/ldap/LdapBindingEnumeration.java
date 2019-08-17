package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.spi.DirectoryManager;

final class LdapBindingEnumeration extends AbstractLdapNamingEnumeration<Binding> {
  private final AccessControlContext acc = AccessController.getContext();
  
  LdapBindingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation) throws NamingException { super(paramLdapCtx, paramLdapResult, paramName, paramContinuation); }
  
  protected Binding createItem(String paramString, final Attributes attrs, Vector<Control> paramVector) throws NamingException {
    Binding binding;
    Object object = null;
    String str = getAtom(paramString);
    if (paramAttributes.get(Obj.JAVA_ATTRIBUTES[2]) != null)
      try {
        object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
              public Object run() throws NamingException { return Obj.decodeObject(attrs); }
            },  this.acc);
      } catch (PrivilegedActionException privilegedActionException) {
        throw (NamingException)privilegedActionException.getException();
      }  
    if (object == null)
      object = new LdapCtx(this.homeCtx, paramString); 
    CompositeName compositeName = new CompositeName();
    compositeName.add(str);
    try {
      object = DirectoryManager.getObjectInstance(object, compositeName, this.homeCtx, this.homeCtx.envprops, paramAttributes);
    } catch (NamingException null) {
      throw binding;
    } catch (Exception null) {
      NamingException namingException = new NamingException("problem generating object using object factory");
      namingException.setRootCause(binding);
      throw namingException;
    } 
    if (paramVector != null) {
      binding = new BindingWithControls(compositeName.toString(), object, this.homeCtx.convertControls(paramVector));
    } else {
      binding = new Binding(compositeName.toString(), object);
    } 
    binding.setNameInNamespace(paramString);
    return binding;
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext) throws NamingException { return (AbstractLdapNamingEnumeration)paramLdapReferralContext.listBindings(this.listArg); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapBindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
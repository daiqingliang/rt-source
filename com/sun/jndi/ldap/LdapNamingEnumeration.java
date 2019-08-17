package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

final class LdapNamingEnumeration extends AbstractLdapNamingEnumeration<NameClassPair> {
  private static final String defaultClassName = javax.naming.directory.DirContext.class.getName();
  
  LdapNamingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation) throws NamingException { super(paramLdapCtx, paramLdapResult, paramName, paramContinuation); }
  
  protected NameClassPair createItem(String paramString, Attributes paramAttributes, Vector<Control> paramVector) throws NamingException {
    NameClassPair nameClassPair;
    String str = null;
    Attribute attribute;
    if ((attribute = paramAttributes.get(Obj.JAVA_ATTRIBUTES[2])) != null) {
      str = (String)attribute.get();
    } else {
      str = defaultClassName;
    } 
    CompositeName compositeName = new CompositeName();
    compositeName.add(getAtom(paramString));
    if (paramVector != null) {
      nameClassPair = new NameClassPairWithControls(compositeName.toString(), str, this.homeCtx.convertControls(paramVector));
    } else {
      nameClassPair = new NameClassPair(compositeName.toString(), str);
    } 
    nameClassPair.setNameInNamespace(paramString);
    return nameClassPair;
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext) throws NamingException { return (AbstractLdapNamingEnumeration)paramLdapReferralContext.list(this.listArg); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapNamingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
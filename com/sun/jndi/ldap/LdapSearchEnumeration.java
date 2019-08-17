package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapName;
import javax.naming.spi.DirectoryManager;

final class LdapSearchEnumeration extends AbstractLdapNamingEnumeration<SearchResult> {
  private Name startName;
  
  private LdapCtx.SearchArgs searchArgs = null;
  
  private final AccessControlContext acc = AccessController.getContext();
  
  LdapSearchEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, String paramString, LdapCtx.SearchArgs paramSearchArgs, Continuation paramContinuation) throws NamingException {
    super(paramLdapCtx, paramLdapResult, paramSearchArgs.name, paramContinuation);
    this.startName = new LdapName(paramString);
    this.searchArgs = paramSearchArgs;
  }
  
  protected SearchResult createItem(String paramString, final Attributes attrs, Vector<Control> paramVector) throws NamingException {
    SearchResult searchResult;
    String str2;
    String str1;
    Object object = null;
    boolean bool = true;
    try {
      LdapName ldapName = new LdapName(paramString);
      if (this.startName != null && ldapName.startsWith(this.startName)) {
        str1 = ldapName.getSuffix(this.startName.size()).toString();
        str2 = ldapName.getSuffix(this.homeCtx.currentParsedDN.size()).toString();
      } else {
        bool = false;
        str2 = str1 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, paramString, this.homeCtx.hasLdapsScheme);
      } 
    } catch (NamingException namingException) {
      bool = false;
      str2 = str1 = LdapURL.toUrlString(this.homeCtx.hostname, this.homeCtx.port_number, paramString, this.homeCtx.hasLdapsScheme);
    } 
    CompositeName compositeName1 = new CompositeName();
    if (!str1.equals(""))
      compositeName1.add(str1); 
    CompositeName compositeName2 = new CompositeName();
    if (!str2.equals(""))
      compositeName2.add(str2); 
    this.homeCtx.setParents(paramAttributes, compositeName2);
    if (this.searchArgs.cons.getReturningObjFlag()) {
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
      try {
        object = DirectoryManager.getObjectInstance(object, compositeName2, bool ? this.homeCtx : null, this.homeCtx.envprops, paramAttributes);
      } catch (NamingException namingException) {
        throw namingException;
      } catch (Exception exception) {
        NamingException namingException = new NamingException("problem generating object using object factory");
        namingException.setRootCause(exception);
        throw namingException;
      } 
      if ((searchResult = this.searchArgs.reqAttrs) != null) {
        BasicAttributes basicAttributes = new BasicAttributes(true);
        byte b;
        for (b = 0; b < searchResult.length; b++)
          basicAttributes.put(searchResult[b], null); 
        for (b = 0; b < Obj.JAVA_ATTRIBUTES.length; b++) {
          if (basicAttributes.get(Obj.JAVA_ATTRIBUTES[b]) == null)
            paramAttributes.remove(Obj.JAVA_ATTRIBUTES[b]); 
        } 
      } 
    } 
    if (paramVector != null) {
      searchResult = new SearchResultWithControls(bool ? compositeName1.toString() : str1, object, paramAttributes, bool, this.homeCtx.convertControls(paramVector));
    } else {
      searchResult = new SearchResult(bool ? compositeName1.toString() : str1, object, paramAttributes, bool);
    } 
    searchResult.setNameInNamespace(paramString);
    return searchResult;
  }
  
  public void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException) {
    this.startName = null;
    super.appendUnprocessedReferrals(paramLdapReferralException);
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext) throws NamingException { return (AbstractLdapNamingEnumeration)paramLdapReferralContext.search(this.searchArgs.name, this.searchArgs.filter, this.searchArgs.cons); }
  
  protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> paramAbstractLdapNamingEnumeration) {
    super.update(paramAbstractLdapNamingEnumeration);
    LdapSearchEnumeration ldapSearchEnumeration = (LdapSearchEnumeration)paramAbstractLdapNamingEnumeration;
    this.startName = ldapSearchEnumeration.startName;
  }
  
  void setStartName(Name paramName) { this.startName = paramName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapSearchEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
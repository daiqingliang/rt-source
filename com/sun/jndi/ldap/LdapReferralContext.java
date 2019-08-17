package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.SearchFilter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.spi.NamingManager;

final class LdapReferralContext implements DirContext, LdapContext {
  private DirContext refCtx = null;
  
  private Name urlName = null;
  
  private String urlAttrs = null;
  
  private String urlScope = null;
  
  private String urlFilter = null;
  
  private LdapReferralException refEx = null;
  
  private boolean skipThisReferral = false;
  
  private int hopCount = 1;
  
  private NamingException previousEx = null;
  
  LdapReferralContext(LdapReferralException paramLdapReferralException, Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl1, Control[] paramArrayOfControl2, String paramString, boolean paramBoolean, int paramInt) throws NamingException {
    this.refEx = paramLdapReferralException;
    if (this.skipThisReferral = paramBoolean)
      return; 
    if (paramHashtable != null) {
      paramHashtable = (Hashtable)paramHashtable.clone();
      if (paramArrayOfControl1 == null)
        paramHashtable.remove("java.naming.ldap.control.connect"); 
    } else if (paramArrayOfControl1 != null) {
      paramHashtable = new Hashtable<?, ?>(5);
    } 
    if (paramArrayOfControl1 != null) {
      Control[] arrayOfControl = new Control[paramArrayOfControl1.length];
      System.arraycopy(paramArrayOfControl1, 0, arrayOfControl, 0, paramArrayOfControl1.length);
      paramHashtable.put("java.naming.ldap.control.connect", arrayOfControl);
    } 
    while (true) {
      try {
        str = this.refEx.getNextReferral();
        if (str == null) {
          if (this.previousEx != null)
            throw (NamingException)this.previousEx.fillInStackTrace(); 
          throw new NamingException("Illegal encoding: referral is empty");
        } 
      } catch (LdapReferralException ldapReferralException) {
        if (paramInt == 2)
          throw ldapReferralException; 
        this.refEx = ldapReferralException;
        continue;
      } 
      Reference reference = new Reference("javax.naming.directory.DirContext", new StringRefAddr("URL", str));
      try {
        object = NamingManager.getObjectInstance(reference, null, null, paramHashtable);
        break;
      } catch (NamingException namingException) {
        if (paramInt == 2)
          throw namingException; 
        this.previousEx = namingException;
      } catch (Exception exception) {
        NamingException namingException = new NamingException("problem generating object using object factory");
        namingException.setRootCause(exception);
        throw namingException;
      } 
    } 
    if (object instanceof DirContext) {
      this.refCtx = (DirContext)object;
      if (this.refCtx instanceof LdapContext && paramArrayOfControl2 != null)
        ((LdapContext)this.refCtx).setRequestControls(paramArrayOfControl2); 
      initDefaults(str, paramString);
    } else {
      NotContextException notContextException = new NotContextException("Cannot create context for: " + str);
      notContextException.setRemainingName((new CompositeName()).add(paramString));
      throw notContextException;
    } 
  }
  
  private void initDefaults(String paramString1, String paramString2) throws NamingException {
    String str;
    try {
      LdapURL ldapURL = new LdapURL(paramString1);
      str = ldapURL.getDN();
      this.urlAttrs = ldapURL.getAttributes();
      this.urlScope = ldapURL.getScope();
      this.urlFilter = ldapURL.getFilter();
    } catch (NamingException namingException) {
      str = paramString1;
      this.urlAttrs = this.urlScope = this.urlFilter = null;
    } 
    if (str == null) {
      str = paramString2;
    } else {
      str = "";
    } 
    if (str == null) {
      this.urlName = null;
    } else {
      this.urlName = str.equals("") ? new CompositeName() : (new CompositeName()).add(str);
    } 
  }
  
  public void close() throws NamingException {
    if (this.refCtx != null) {
      this.refCtx.close();
      this.refCtx = null;
    } 
    this.refEx = null;
  }
  
  void setHopCount(int paramInt) {
    this.hopCount = paramInt;
    if (this.refCtx != null && this.refCtx instanceof LdapCtx)
      ((LdapCtx)this.refCtx).setHopCount(paramInt); 
  }
  
  public Object lookup(String paramString) throws NamingException { return lookup(toName(paramString)); }
  
  public Object lookup(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.lookup(overrideName(paramName));
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException { bind(toName(paramString), paramObject); }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.bind(overrideName(paramName), paramObject);
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException { rebind(toName(paramString), paramObject); }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.rebind(overrideName(paramName), paramObject);
  }
  
  public void unbind(String paramString) throws NamingException { unbind(toName(paramString)); }
  
  public void unbind(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.unbind(overrideName(paramName));
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException { rename(toName(paramString1), toName(paramString2)); }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.rename(overrideName(paramName1), toName(this.refEx.getNewRdn()));
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException { return list(toName(paramString)); }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    try {
      NamingEnumeration namingEnumeration = null;
      if (this.urlScope != null && this.urlScope.equals("base")) {
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningObjFlag(true);
        searchControls.setSearchScope(0);
        namingEnumeration = this.refCtx.search(overrideName(paramName), "(objectclass=*)", searchControls);
      } else {
        namingEnumeration = this.refCtx.list(overrideName(paramName));
      } 
      this.refEx.setNameResolved(true);
      ((ReferralEnumeration)namingEnumeration).appendUnprocessedReferrals(this.refEx);
      return namingEnumeration;
    } catch (LdapReferralException ldapReferralException) {
      ldapReferralException.appendUnprocessedReferrals(this.refEx);
      throw (NamingException)ldapReferralException.fillInStackTrace();
    } catch (NamingException namingException) {
      if (this.refEx != null && !this.refEx.hasMoreReferrals())
        this.refEx.setNamingException(namingException); 
      if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions()))
        throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
      throw namingException;
    } 
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException { return listBindings(toName(paramString)); }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    try {
      NamingEnumeration namingEnumeration = null;
      if (this.urlScope != null && this.urlScope.equals("base")) {
        SearchControls searchControls = new SearchControls();
        searchControls.setReturningObjFlag(true);
        searchControls.setSearchScope(0);
        namingEnumeration = this.refCtx.search(overrideName(paramName), "(objectclass=*)", searchControls);
      } else {
        namingEnumeration = this.refCtx.listBindings(overrideName(paramName));
      } 
      this.refEx.setNameResolved(true);
      ((ReferralEnumeration)namingEnumeration).appendUnprocessedReferrals(this.refEx);
      return namingEnumeration;
    } catch (LdapReferralException ldapReferralException) {
      ldapReferralException.appendUnprocessedReferrals(this.refEx);
      throw (NamingException)ldapReferralException.fillInStackTrace();
    } catch (NamingException namingException) {
      if (this.refEx != null && !this.refEx.hasMoreReferrals())
        this.refEx.setNamingException(namingException); 
      if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions()))
        throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
      throw namingException;
    } 
  }
  
  public void destroySubcontext(String paramString) throws NamingException { destroySubcontext(toName(paramString)); }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.destroySubcontext(overrideName(paramName));
  }
  
  public Context createSubcontext(String paramString) throws NamingException { return createSubcontext(toName(paramString)); }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.createSubcontext(overrideName(paramName));
  }
  
  public Object lookupLink(String paramString) throws NamingException { return lookupLink(toName(paramString)); }
  
  public Object lookupLink(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.lookupLink(overrideName(paramName));
  }
  
  public NameParser getNameParser(String paramString) throws NamingException { return getNameParser(toName(paramString)); }
  
  public NameParser getNameParser(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getNameParser(overrideName(paramName));
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException { return composeName(toName(paramString1), toName(paramString2)).toString(); }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.composeName(paramName1, paramName2);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.addToEnvironment(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.removeFromEnvironment(paramString);
  }
  
  public Hashtable<?, ?> getEnvironment() throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getEnvironment();
  }
  
  public Attributes getAttributes(String paramString) throws NamingException { return getAttributes(toName(paramString)); }
  
  public Attributes getAttributes(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getAttributes(overrideName(paramName));
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException { return getAttributes(toName(paramString), paramArrayOfString); }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getAttributes(overrideName(paramName), paramArrayOfString);
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException { modifyAttributes(toName(paramString), paramInt, paramAttributes); }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.modifyAttributes(overrideName(paramName), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException { modifyAttributes(toName(paramString), paramArrayOfModificationItem); }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.modifyAttributes(overrideName(paramName), paramArrayOfModificationItem);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { bind(toName(paramString), paramObject, paramAttributes); }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.bind(overrideName(paramName), paramObject, paramAttributes);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException { rebind(toName(paramString), paramObject, paramAttributes); }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    this.refCtx.rebind(overrideName(paramName), paramObject, paramAttributes);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException { return createSubcontext(toName(paramString), paramAttributes); }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.createSubcontext(overrideName(paramName), paramAttributes);
  }
  
  public DirContext getSchema(String paramString) throws NamingException { return getSchema(toName(paramString)); }
  
  public DirContext getSchema(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getSchema(overrideName(paramName));
  }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException { return getSchemaClassDefinition(toName(paramString)); }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return this.refCtx.getSchemaClassDefinition(overrideName(paramName));
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException { return search(toName(paramString), SearchFilter.format(paramAttributes), new SearchControls()); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException { return search(paramName, SearchFilter.format(paramAttributes), new SearchControls()); }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(paramArrayOfString);
    return search(toName(paramString), SearchFilter.format(paramAttributes), searchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(paramArrayOfString);
    return search(paramName, SearchFilter.format(paramAttributes), searchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException { return search(toName(paramString1), paramString2, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    try {
      NamingEnumeration namingEnumeration = this.refCtx.search(overrideName(paramName), overrideFilter(paramString), overrideAttributesAndScope(paramSearchControls));
      this.refEx.setNameResolved(true);
      ((ReferralEnumeration)namingEnumeration).appendUnprocessedReferrals(this.refEx);
      return namingEnumeration;
    } catch (LdapReferralException ldapReferralException) {
      ldapReferralException.appendUnprocessedReferrals(this.refEx);
      throw (NamingException)ldapReferralException.fillInStackTrace();
    } catch (NamingException namingException) {
      if (this.refEx != null && !this.refEx.hasMoreReferrals())
        this.refEx.setNamingException(namingException); 
      if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions()))
        throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
      throw namingException;
    } 
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return search(toName(paramString1), paramString2, paramArrayOfObject, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    try {
      NamingEnumeration namingEnumeration;
      if (this.urlFilter != null) {
        namingEnumeration = this.refCtx.search(overrideName(paramName), this.urlFilter, overrideAttributesAndScope(paramSearchControls));
      } else {
        namingEnumeration = this.refCtx.search(overrideName(paramName), paramString, paramArrayOfObject, overrideAttributesAndScope(paramSearchControls));
      } 
      this.refEx.setNameResolved(true);
      ((ReferralEnumeration)namingEnumeration).appendUnprocessedReferrals(this.refEx);
      return namingEnumeration;
    } catch (LdapReferralException ldapReferralException) {
      ldapReferralException.appendUnprocessedReferrals(this.refEx);
      throw (NamingException)ldapReferralException.fillInStackTrace();
    } catch (NamingException namingException) {
      if (this.refEx != null && !this.refEx.hasMoreReferrals())
        this.refEx.setNamingException(namingException); 
      if (this.refEx != null && (this.refEx.hasMoreReferrals() || this.refEx.hasMoreReferralExceptions()))
        throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
      throw namingException;
    } 
  }
  
  public String getNameInNamespace() throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    return (this.urlName != null && !this.urlName.isEmpty()) ? this.urlName.get(0) : "";
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    return ((LdapContext)this.refCtx).extendedOperation(paramExtendedRequest);
  }
  
  public LdapContext newInstance(Control[] paramArrayOfControl) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    return ((LdapContext)this.refCtx).newInstance(paramArrayOfControl);
  }
  
  public void reconnect(Control[] paramArrayOfControl) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    ((LdapContext)this.refCtx).reconnect(paramArrayOfControl);
  }
  
  public Control[] getConnectControls() throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    return ((LdapContext)this.refCtx).getConnectControls();
  }
  
  public void setRequestControls(Control[] paramArrayOfControl) throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    ((LdapContext)this.refCtx).setRequestControls(paramArrayOfControl);
  }
  
  public Control[] getRequestControls() throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    return ((LdapContext)this.refCtx).getRequestControls();
  }
  
  public Control[] getResponseControls() throws NamingException {
    if (this.skipThisReferral)
      throw (NamingException)this.refEx.appendUnprocessedReferrals(null).fillInStackTrace(); 
    if (!(this.refCtx instanceof LdapContext))
      throw new NotContextException("Referral context not an instance of LdapContext"); 
    return ((LdapContext)this.refCtx).getResponseControls();
  }
  
  private Name toName(String paramString) throws InvalidNameException { return paramString.equals("") ? new CompositeName() : (new CompositeName()).add(paramString); }
  
  private Name overrideName(Name paramName) throws InvalidNameException { return (this.urlName == null) ? paramName : this.urlName; }
  
  private SearchControls overrideAttributesAndScope(SearchControls paramSearchControls) {
    if (this.urlScope != null || this.urlAttrs != null) {
      SearchControls searchControls = new SearchControls(paramSearchControls.getSearchScope(), paramSearchControls.getCountLimit(), paramSearchControls.getTimeLimit(), paramSearchControls.getReturningAttributes(), paramSearchControls.getReturningObjFlag(), paramSearchControls.getDerefLinkFlag());
      if (this.urlScope != null)
        if (this.urlScope.equals("base")) {
          searchControls.setSearchScope(0);
        } else if (this.urlScope.equals("one")) {
          searchControls.setSearchScope(1);
        } else if (this.urlScope.equals("sub")) {
          searchControls.setSearchScope(2);
        }  
      if (this.urlAttrs != null) {
        StringTokenizer stringTokenizer = new StringTokenizer(this.urlAttrs, ",");
        int i = stringTokenizer.countTokens();
        String[] arrayOfString = new String[i];
        for (byte b = 0; b < i; b++)
          arrayOfString[b] = stringTokenizer.nextToken(); 
        searchControls.setReturningAttributes(arrayOfString);
      } 
      return searchControls;
    } 
    return paramSearchControls;
  }
  
  private String overrideFilter(String paramString) { return (this.urlFilter == null) ? paramString : this.urlFilter; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapReferralContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
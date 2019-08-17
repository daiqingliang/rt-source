package com.sun.jndi.url.ldap;

import com.sun.jndi.ldap.LdapURL;
import com.sun.jndi.toolkit.url.GenericURLDirContext;
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
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.ResolveResult;

public final class ldapURLContext extends GenericURLDirContext {
  ldapURLContext(Hashtable<?, ?> paramHashtable) { super(paramHashtable); }
  
  protected ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException { return ldapURLContextFactory.getUsingURLIgnoreRootDN(paramString, paramHashtable); }
  
  protected Name getURLSuffix(String paramString1, String paramString2) throws NamingException {
    LdapURL ldapURL = new LdapURL(paramString2);
    String str = (ldapURL.getDN() != null) ? ldapURL.getDN() : "";
    CompositeName compositeName = new CompositeName();
    if (!"".equals(str))
      compositeName.add(str); 
    return compositeName;
  }
  
  public Object lookup(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.lookup(paramString);
  }
  
  public Object lookup(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.lookup(paramName);
  }
  
  public void bind(String paramString, Object paramObject) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.bind(paramString, paramObject);
  }
  
  public void bind(Name paramName, Object paramObject) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.bind(paramName, paramObject);
  }
  
  public void rebind(String paramString, Object paramObject) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.rebind(paramString, paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.rebind(paramName, paramObject);
  }
  
  public void unbind(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.unbind(paramString);
  }
  
  public void unbind(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.unbind(paramName);
  }
  
  public void rename(String paramString1, String paramString2) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString1))
      throw new InvalidNameException(paramString1); 
    if (LdapURL.hasQueryComponents(paramString2))
      throw new InvalidNameException(paramString2); 
    super.rename(paramString1, paramString2);
  }
  
  public void rename(Name paramName1, Name paramName2) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName1.get(0)))
      throw new InvalidNameException(paramName1.toString()); 
    if (LdapURL.hasQueryComponents(paramName2.get(0)))
      throw new InvalidNameException(paramName2.toString()); 
    super.rename(paramName1, paramName2);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.list(paramString);
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.list(paramName);
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.listBindings(paramString);
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.listBindings(paramName);
  }
  
  public void destroySubcontext(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.destroySubcontext(paramString);
  }
  
  public void destroySubcontext(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.destroySubcontext(paramName);
  }
  
  public Context createSubcontext(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.createSubcontext(paramString);
  }
  
  public Context createSubcontext(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.createSubcontext(paramName);
  }
  
  public Object lookupLink(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.lookupLink(paramString);
  }
  
  public Object lookupLink(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.lookupLink(paramName);
  }
  
  public NameParser getNameParser(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.getNameParser(paramString);
  }
  
  public NameParser getNameParser(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.getNameParser(paramName);
  }
  
  public String composeName(String paramString1, String paramString2) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString1))
      throw new InvalidNameException(paramString1); 
    if (LdapURL.hasQueryComponents(paramString2))
      throw new InvalidNameException(paramString2); 
    return super.composeName(paramString1, paramString2);
  }
  
  public Name composeName(Name paramName1, Name paramName2) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName1.get(0)))
      throw new InvalidNameException(paramName1.toString()); 
    if (LdapURL.hasQueryComponents(paramName2.get(0)))
      throw new InvalidNameException(paramName2.toString()); 
    return super.composeName(paramName1, paramName2);
  }
  
  public Attributes getAttributes(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.getAttributes(paramString);
  }
  
  public Attributes getAttributes(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.getAttributes(paramName);
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.getAttributes(paramString, paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.getAttributes(paramName, paramArrayOfString);
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.modifyAttributes(paramString, paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.modifyAttributes(paramName, paramInt, paramAttributes);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.modifyAttributes(paramString, paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.modifyAttributes(paramName, paramArrayOfModificationItem);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.bind(paramString, paramObject, paramAttributes);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.bind(paramName, paramObject, paramAttributes);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    super.rebind(paramString, paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    super.rebind(paramName, paramObject, paramAttributes);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.createSubcontext(paramString, paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.createSubcontext(paramName, paramAttributes);
  }
  
  public DirContext getSchema(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.getSchema(paramString);
  }
  
  public DirContext getSchema(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.getSchema(paramName);
  }
  
  public DirContext getSchemaClassDefinition(String paramString) throws NamingException {
    if (LdapURL.hasQueryComponents(paramString))
      throw new InvalidNameException(paramString); 
    return super.getSchemaClassDefinition(paramString);
  }
  
  public DirContext getSchemaClassDefinition(Name paramName) throws NamingException {
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.getSchemaClassDefinition(paramName);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes) throws NamingException { return LdapURL.hasQueryComponents(paramString) ? searchUsingURL(paramString) : super.search(paramString, paramAttributes); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramAttributes); 
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.search(paramName, paramAttributes);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException { return LdapURL.hasQueryComponents(paramString) ? searchUsingURL(paramString) : super.search(paramString, paramAttributes, paramArrayOfString); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramAttributes, paramArrayOfString); 
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.search(paramName, paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls) throws NamingException { return LdapURL.hasQueryComponents(paramString1) ? searchUsingURL(paramString1) : super.search(paramString1, paramString2, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramString, paramSearchControls); 
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.search(paramName, paramString, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException { return LdapURL.hasQueryComponents(paramString1) ? searchUsingURL(paramString1) : super.search(paramString1, paramString2, paramArrayOfObject, paramSearchControls); }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls) throws NamingException {
    if (paramName.size() == 1)
      return search(paramName.get(0), paramString, paramArrayOfObject, paramSearchControls); 
    if (LdapURL.hasQueryComponents(paramName.get(0)))
      throw new InvalidNameException(paramName.toString()); 
    return super.search(paramName, paramString, paramArrayOfObject, paramSearchControls);
  }
  
  private NamingEnumeration<SearchResult> searchUsingURL(String paramString) throws NamingException {
    LdapURL ldapURL = new LdapURL(paramString);
    ResolveResult resolveResult = getRootURLContext(paramString, this.myEnv);
    dirContext = (DirContext)resolveResult.getResolvedObj();
    try {
      return dirContext.search(resolveResult.getRemainingName(), setFilterUsingURL(ldapURL), setSearchControlsUsingURL(ldapURL));
    } finally {
      dirContext.close();
    } 
  }
  
  private static String setFilterUsingURL(LdapURL paramLdapURL) {
    String str = paramLdapURL.getFilter();
    if (str == null)
      str = "(objectClass=*)"; 
    return str;
  }
  
  private static SearchControls setSearchControlsUsingURL(LdapURL paramLdapURL) {
    SearchControls searchControls = new SearchControls();
    String str1 = paramLdapURL.getScope();
    String str2 = paramLdapURL.getAttributes();
    if (str1 == null) {
      searchControls.setSearchScope(0);
    } else if (str1.equals("sub")) {
      searchControls.setSearchScope(2);
    } else if (str1.equals("one")) {
      searchControls.setSearchScope(1);
    } else if (str1.equals("base")) {
      searchControls.setSearchScope(0);
    } 
    if (str2 == null) {
      searchControls.setReturningAttributes(null);
    } else {
      StringTokenizer stringTokenizer = new StringTokenizer(str2, ",");
      int i = stringTokenizer.countTokens();
      String[] arrayOfString = new String[i];
      for (byte b = 0; b < i; b++)
        arrayOfString[b] = stringTokenizer.nextToken(); 
      searchControls.setReturningAttributes(arrayOfString);
    } 
    return searchControls;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jnd\\url\ldap\ldapURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
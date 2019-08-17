package com.sun.jndi.ldap;

import com.sun.jndi.url.ldap.ldapURLContextFactory;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.AuthenticationException;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

public final class LdapCtxFactory implements ObjectFactory, InitialContextFactory {
  public static final String ADDRESS_TYPE = "URL";
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable) throws Exception {
    if (!isLdapRef(paramObject))
      return null; 
    ldapURLContextFactory ldapURLContextFactory = new ldapURLContextFactory();
    String[] arrayOfString = getURLs((Reference)paramObject);
    return ldapURLContextFactory.getObjectInstance(arrayOfString, paramName, paramContext, paramHashtable);
  }
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable) throws NamingException {
    try {
      String str = (paramHashtable != null) ? (String)paramHashtable.get("java.naming.provider.url") : null;
      if (str == null)
        return new LdapCtx("", "localhost", 389, paramHashtable, false); 
      String[] arrayOfString = LdapURL.fromList(str);
      if (arrayOfString.length == 0)
        throw new ConfigurationException("java.naming.provider.url property does not contain a URL"); 
      return getLdapCtxInstance(arrayOfString, paramHashtable);
    } catch (LdapReferralException ldapReferralException) {
      if (paramHashtable != null && "throw".equals(paramHashtable.get("java.naming.referral")))
        throw ldapReferralException; 
      Control[] arrayOfControl = (paramHashtable != null) ? (Control[])paramHashtable.get("java.naming.ldap.control.connect") : null;
      return (LdapCtx)ldapReferralException.getReferralContext(paramHashtable, arrayOfControl);
    } 
  }
  
  private static boolean isLdapRef(Object paramObject) {
    if (!(paramObject instanceof Reference))
      return false; 
    String str = LdapCtxFactory.class.getName();
    Reference reference = (Reference)paramObject;
    return str.equals(reference.getFactoryClassName());
  }
  
  private static String[] getURLs(Reference paramReference) throws NamingException {
    byte b = 0;
    String[] arrayOfString1 = new String[paramReference.size()];
    Enumeration enumeration = paramReference.getAll();
    while (enumeration.hasMoreElements()) {
      RefAddr refAddr = (RefAddr)enumeration.nextElement();
      if (refAddr instanceof javax.naming.StringRefAddr && refAddr.getType().equals("URL"))
        arrayOfString1[b++] = (String)refAddr.getContent(); 
    } 
    if (b == 0)
      throw new ConfigurationException("Reference contains no valid addresses"); 
    if (b == paramReference.size())
      return arrayOfString1; 
    String[] arrayOfString2 = new String[b];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, b);
    return arrayOfString2;
  }
  
  public static DirContext getLdapCtxInstance(Object paramObject, Hashtable<?, ?> paramHashtable) throws NamingException {
    if (paramObject instanceof String)
      return getUsingURL((String)paramObject, paramHashtable); 
    if (paramObject instanceof String[])
      return getUsingURLs((String[])paramObject, paramHashtable); 
    throw new IllegalArgumentException("argument must be an LDAP URL String or array of them");
  }
  
  private static DirContext getUsingURL(String paramString, Hashtable<?, ?> paramHashtable) throws NamingException {
    DirContext dirContext = null;
    LdapURL ldapURL = new LdapURL(paramString);
    String str1 = ldapURL.getDN();
    String str2 = ldapURL.getHost();
    int i = ldapURL.getPort();
    String str3 = null;
    String[] arrayOfString;
    if (str2 == null && i == -1 && str1 != null && (str3 = ServiceLocator.mapDnToDomainName(str1)) != null && (arrayOfString = ServiceLocator.getLdapService(str3, paramHashtable)) != null) {
      String str4 = ldapURL.getScheme() + "://";
      String[] arrayOfString1 = new String[arrayOfString.length];
      String str5 = ldapURL.getQuery();
      String str6 = ldapURL.getPath() + ((str5 != null) ? str5 : "");
      for (byte b = 0; b < arrayOfString.length; b++)
        arrayOfString1[b] = str4 + arrayOfString[b] + str6; 
      dirContext = getUsingURLs(arrayOfString1, paramHashtable);
      ((LdapCtx)dirContext).setDomainName(str3);
    } else {
      dirContext = new LdapCtx(str1, str2, i, paramHashtable, ldapURL.useSsl());
      ((LdapCtx)dirContext).setProviderUrl(paramString);
    } 
    return dirContext;
  }
  
  private static DirContext getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable) throws NamingException {
    NamingException namingException = null;
    Object object = null;
    byte b = 0;
    while (b < paramArrayOfString.length) {
      try {
        return getUsingURL(paramArrayOfString[b], paramHashtable);
      } catch (AuthenticationException authenticationException) {
        throw authenticationException;
      } catch (NamingException namingException1) {
        namingException = namingException1;
        b++;
      } 
    } 
    throw namingException;
  }
  
  public static Attribute createTypeNameAttr(Class<?> paramClass) {
    Vector vector = new Vector(10);
    String[] arrayOfString = getTypeNames(paramClass, vector);
    if (arrayOfString.length > 0) {
      BasicAttribute basicAttribute = new BasicAttribute(Obj.JAVA_ATTRIBUTES[6]);
      for (byte b = 0; b < arrayOfString.length; b++)
        basicAttribute.add(arrayOfString[b]); 
      return basicAttribute;
    } 
    return null;
  }
  
  private static String[] getTypeNames(Class<?> paramClass, Vector<String> paramVector) {
    getClassesAux(paramClass, paramVector);
    Class[] arrayOfClass = paramClass.getInterfaces();
    for (byte b1 = 0; b1 < arrayOfClass.length; b1++)
      getClassesAux(arrayOfClass[b1], paramVector); 
    String[] arrayOfString = new String[paramVector.size()];
    byte b2 = 0;
    for (String str : paramVector)
      arrayOfString[b2++] = str; 
    return arrayOfString;
  }
  
  private static void getClassesAux(Class<?> paramClass, Vector<String> paramVector) {
    if (!paramVector.contains(paramClass.getName()))
      paramVector.addElement(paramClass.getName()); 
    for (paramClass = paramClass.getSuperclass(); paramClass != null; paramClass = paramClass.getSuperclass())
      getTypeNames(paramClass, paramVector); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapCtxFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
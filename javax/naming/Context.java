package javax.naming;

import java.util.Hashtable;

public interface Context {
  public static final String INITIAL_CONTEXT_FACTORY = "java.naming.factory.initial";
  
  public static final String OBJECT_FACTORIES = "java.naming.factory.object";
  
  public static final String STATE_FACTORIES = "java.naming.factory.state";
  
  public static final String URL_PKG_PREFIXES = "java.naming.factory.url.pkgs";
  
  public static final String PROVIDER_URL = "java.naming.provider.url";
  
  public static final String DNS_URL = "java.naming.dns.url";
  
  public static final String AUTHORITATIVE = "java.naming.authoritative";
  
  public static final String BATCHSIZE = "java.naming.batchsize";
  
  public static final String REFERRAL = "java.naming.referral";
  
  public static final String SECURITY_PROTOCOL = "java.naming.security.protocol";
  
  public static final String SECURITY_AUTHENTICATION = "java.naming.security.authentication";
  
  public static final String SECURITY_PRINCIPAL = "java.naming.security.principal";
  
  public static final String SECURITY_CREDENTIALS = "java.naming.security.credentials";
  
  public static final String LANGUAGE = "java.naming.language";
  
  public static final String APPLET = "java.naming.applet";
  
  Object lookup(Name paramName) throws NamingException;
  
  Object lookup(String paramString) throws NamingException;
  
  void bind(Name paramName, Object paramObject) throws NamingException;
  
  void bind(String paramString, Object paramObject) throws NamingException;
  
  void rebind(Name paramName, Object paramObject) throws NamingException;
  
  void rebind(String paramString, Object paramObject) throws NamingException;
  
  void unbind(Name paramName) throws NamingException;
  
  void unbind(String paramString) throws NamingException;
  
  void rename(Name paramName1, Name paramName2) throws NamingException;
  
  void rename(String paramString1, String paramString2) throws NamingException;
  
  NamingEnumeration<NameClassPair> list(Name paramName) throws NamingException;
  
  NamingEnumeration<NameClassPair> list(String paramString) throws NamingException;
  
  NamingEnumeration<Binding> listBindings(Name paramName) throws NamingException;
  
  NamingEnumeration<Binding> listBindings(String paramString) throws NamingException;
  
  void destroySubcontext(Name paramName) throws NamingException;
  
  void destroySubcontext(String paramString) throws NamingException;
  
  Context createSubcontext(Name paramName) throws NamingException;
  
  Context createSubcontext(String paramString) throws NamingException;
  
  Object lookupLink(Name paramName) throws NamingException;
  
  Object lookupLink(String paramString) throws NamingException;
  
  NameParser getNameParser(Name paramName) throws NamingException;
  
  NameParser getNameParser(String paramString) throws NamingException;
  
  Name composeName(Name paramName1, Name paramName2) throws NamingException;
  
  String composeName(String paramString1, String paramString2) throws NamingException;
  
  Object addToEnvironment(String paramString, Object paramObject) throws NamingException;
  
  Object removeFromEnvironment(String paramString) throws NamingException;
  
  Hashtable<?, ?> getEnvironment() throws NamingException;
  
  void close() throws NamingException;
  
  String getNameInNamespace() throws NamingException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\Context.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
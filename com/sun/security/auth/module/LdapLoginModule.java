package com.sun.security.auth.module;

import com.sun.security.auth.LdapPrincipal;
import com.sun.security.auth.UserPrincipal;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class LdapLoginModule implements LoginModule {
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  private static final String USERNAME_KEY = "javax.security.auth.login.name";
  
  private static final String PASSWORD_KEY = "javax.security.auth.login.password";
  
  private static final String USER_PROVIDER = "userProvider";
  
  private static final String USER_FILTER = "userFilter";
  
  private static final String AUTHC_IDENTITY = "authIdentity";
  
  private static final String AUTHZ_IDENTITY = "authzIdentity";
  
  private static final String USERNAME_TOKEN = "{USERNAME}";
  
  private static final Pattern USERNAME_PATTERN = Pattern.compile("\\{USERNAME\\}");
  
  private String userProvider;
  
  private String userFilter;
  
  private String authcIdentity;
  
  private String authzIdentity;
  
  private String authzIdentityAttr = null;
  
  private boolean useSSL = true;
  
  private boolean authFirst = false;
  
  private boolean authOnly = false;
  
  private boolean useFirstPass = false;
  
  private boolean tryFirstPass = false;
  
  private boolean storePass = false;
  
  private boolean clearPass = false;
  
  private boolean debug = false;
  
  private boolean succeeded = false;
  
  private boolean commitSucceeded = false;
  
  private String username;
  
  private char[] password;
  
  private LdapPrincipal ldapPrincipal;
  
  private UserPrincipal userPrincipal;
  
  private UserPrincipal authzPrincipal;
  
  private Subject subject;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, Object> sharedState;
  
  private Map<String, ?> options;
  
  private LdapContext ctx;
  
  private Matcher identityMatcher = null;
  
  private Matcher filterMatcher = null;
  
  private Hashtable<String, Object> ldapEnvironment;
  
  private SearchControls constraints = null;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    this.ldapEnvironment = new Hashtable(9);
    this.ldapEnvironment.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    for (String str : paramMap2.keySet()) {
      if (str.indexOf(".") > -1)
        this.ldapEnvironment.put(str, paramMap2.get(str)); 
    } 
    this.userProvider = (String)paramMap2.get("userProvider");
    if (this.userProvider != null)
      this.ldapEnvironment.put("java.naming.provider.url", this.userProvider); 
    this.authcIdentity = (String)paramMap2.get("authIdentity");
    if (this.authcIdentity != null && this.authcIdentity.indexOf("{USERNAME}") != -1)
      this.identityMatcher = USERNAME_PATTERN.matcher(this.authcIdentity); 
    this.userFilter = (String)paramMap2.get("userFilter");
    if (this.userFilter != null) {
      if (this.userFilter.indexOf("{USERNAME}") != -1)
        this.filterMatcher = USERNAME_PATTERN.matcher(this.userFilter); 
      this.constraints = new SearchControls();
      this.constraints.setSearchScope(2);
      this.constraints.setReturningAttributes(new String[0]);
    } 
    this.authzIdentity = (String)paramMap2.get("authzIdentity");
    if (this.authzIdentity != null && this.authzIdentity.startsWith("{") && this.authzIdentity.endsWith("}")) {
      if (this.constraints != null) {
        this.authzIdentityAttr = this.authzIdentity.substring(1, this.authzIdentity.length() - 1);
        this.constraints.setReturningAttributes(new String[] { this.authzIdentityAttr });
      } 
      this.authzIdentity = null;
    } 
    if (this.authcIdentity != null)
      if (this.userFilter != null) {
        this.authFirst = true;
      } else {
        this.authOnly = true;
      }  
    if ("false".equalsIgnoreCase((String)paramMap2.get("useSSL"))) {
      this.useSSL = false;
      this.ldapEnvironment.remove("java.naming.security.protocol");
    } else {
      this.ldapEnvironment.put("java.naming.security.protocol", "ssl");
    } 
    this.tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    this.useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    this.storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    this.clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    if (this.debug)
      if (this.authFirst) {
        System.out.println("\t\t[LdapLoginModule] authentication-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
      } else if (this.authOnly) {
        System.out.println("\t\t[LdapLoginModule] authentication-only mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
      } else {
        System.out.println("\t\t[LdapLoginModule] search-first mode; " + (this.useSSL ? "SSL enabled" : "SSL disabled"));
      }  
  }
  
  public boolean login() throws LoginException {
    if (this.userProvider == null)
      throw new LoginException("Unable to locate the LDAP directory service"); 
    if (this.debug)
      System.out.println("\t\t[LdapLoginModule] user provider: " + this.userProvider); 
    if (this.tryFirstPass) {
      try {
        attemptAuthentication(true);
        this.succeeded = true;
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] tryFirstPass succeeded"); 
        return true;
      } catch (LoginException loginException) {
        cleanState();
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] tryFirstPass failed: " + loginException.toString()); 
      } 
    } else if (this.useFirstPass) {
      try {
        attemptAuthentication(true);
        this.succeeded = true;
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] useFirstPass succeeded"); 
        return true;
      } catch (LoginException loginException) {
        cleanState();
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] useFirstPass failed"); 
        throw loginException;
      } 
    } 
    try {
      attemptAuthentication(false);
      this.succeeded = true;
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] authentication succeeded"); 
      return true;
    } catch (LoginException loginException) {
      cleanState();
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] authentication failed"); 
      throw loginException;
    } 
  }
  
  public boolean commit() throws LoginException {
    if (!this.succeeded)
      return false; 
    if (this.subject.isReadOnly()) {
      cleanState();
      throw new LoginException("Subject is read-only");
    } 
    Set set = this.subject.getPrincipals();
    if (!set.contains(this.ldapPrincipal))
      set.add(this.ldapPrincipal); 
    if (this.debug)
      System.out.println("\t\t[LdapLoginModule] added LdapPrincipal \"" + this.ldapPrincipal + "\" to Subject"); 
    if (!set.contains(this.userPrincipal))
      set.add(this.userPrincipal); 
    if (this.debug)
      System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.userPrincipal + "\" to Subject"); 
    if (this.authzPrincipal != null && !set.contains(this.authzPrincipal)) {
      set.add(this.authzPrincipal);
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + this.authzPrincipal + "\" to Subject"); 
    } 
    cleanState();
    this.commitSucceeded = true;
    return true;
  }
  
  public boolean abort() throws LoginException {
    if (this.debug)
      System.out.println("\t\t[LdapLoginModule] aborted authentication"); 
    if (!this.succeeded)
      return false; 
    if (this.succeeded == true && !this.commitSucceeded) {
      this.succeeded = false;
      cleanState();
      this.ldapPrincipal = null;
      this.userPrincipal = null;
      this.authzPrincipal = null;
    } else {
      logout();
    } 
    return true;
  }
  
  public boolean logout() throws LoginException {
    if (this.subject.isReadOnly()) {
      cleanState();
      throw new LoginException("Subject is read-only");
    } 
    Set set = this.subject.getPrincipals();
    set.remove(this.ldapPrincipal);
    set.remove(this.userPrincipal);
    if (this.authzIdentity != null)
      set.remove(this.authzPrincipal); 
    cleanState();
    this.succeeded = false;
    this.commitSucceeded = false;
    this.ldapPrincipal = null;
    this.userPrincipal = null;
    this.authzPrincipal = null;
    if (this.debug)
      System.out.println("\t\t[LdapLoginModule] logged out Subject"); 
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean) throws LoginException {
    getUsernamePassword(paramBoolean);
    if (this.password == null || this.password.length == 0)
      throw new FailedLoginException("No password was supplied"); 
    String str = "";
    if (this.authFirst || this.authOnly) {
      String str1 = replaceUsernameToken(this.identityMatcher, this.authcIdentity, this.username);
      this.ldapEnvironment.put("java.naming.security.credentials", this.password);
      this.ldapEnvironment.put("java.naming.security.principal", str1);
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username); 
      try {
        this.ctx = new InitialLdapContext(this.ldapEnvironment, null);
      } catch (NamingException namingException) {
        throw (LoginException)(new FailedLoginException("Cannot bind to LDAP server")).initCause(namingException);
      } 
      if (this.userFilter != null) {
        str = findUserDN(this.ctx);
      } else {
        str = str1;
      } 
    } else {
      try {
        this.ctx = new InitialLdapContext(this.ldapEnvironment, null);
      } catch (NamingException namingException) {
        throw (LoginException)(new FailedLoginException("Cannot connect to LDAP server")).initCause(namingException);
      } 
      str = findUserDN(this.ctx);
      try {
        this.ctx.addToEnvironment("java.naming.security.authentication", "simple");
        this.ctx.addToEnvironment("java.naming.security.principal", str);
        this.ctx.addToEnvironment("java.naming.security.credentials", this.password);
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + this.username); 
        this.ctx.reconnect(null);
      } catch (NamingException namingException) {
        throw (LoginException)(new FailedLoginException("Cannot bind to LDAP server")).initCause(namingException);
      } 
    } 
    if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
      this.sharedState.put("javax.security.auth.login.name", this.username);
      this.sharedState.put("javax.security.auth.login.password", this.password);
    } 
    this.userPrincipal = new UserPrincipal(this.username);
    if (this.authzIdentity != null)
      this.authzPrincipal = new UserPrincipal(this.authzIdentity); 
    try {
      this.ldapPrincipal = new LdapPrincipal(str);
    } catch (InvalidNameException invalidNameException) {
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] cannot create LdapPrincipal: bad DN"); 
      throw (LoginException)(new FailedLoginException("Cannot create LdapPrincipal")).initCause(invalidNameException);
    } 
  }
  
  private String findUserDN(LdapContext paramLdapContext) throws LoginException {
    String str = "";
    if (this.userFilter != null) {
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] searching for entry belonging to user: " + this.username); 
    } else {
      if (this.debug)
        System.out.println("\t\t[LdapLoginModule] cannot search for entry belonging to user: " + this.username); 
      throw new FailedLoginException("Cannot find user's LDAP entry");
    } 
    try {
      String str1 = replaceUsernameToken(this.filterMatcher, this.userFilter, escapeUsernameChars());
      NamingEnumeration namingEnumeration = paramLdapContext.search("", str1, this.constraints);
      if (namingEnumeration.hasMore()) {
        SearchResult searchResult = (SearchResult)namingEnumeration.next();
        str = searchResult.getNameInNamespace();
        if (this.debug)
          System.out.println("\t\t[LdapLoginModule] found entry: " + str); 
        if (this.authzIdentityAttr != null) {
          Attribute attribute = searchResult.getAttributes().get(this.authzIdentityAttr);
          if (attribute != null) {
            Object object = attribute.get();
            if (object instanceof String)
              this.authzIdentity = (String)object; 
          } 
        } 
        namingEnumeration.close();
      } else if (this.debug) {
        System.out.println("\t\t[LdapLoginModule] user's entry not found");
      } 
    } catch (NamingException namingException) {}
    if (str.equals(""))
      throw new FailedLoginException("Cannot find user's LDAP entry"); 
    return str;
  }
  
  private String escapeUsernameChars() {
    int i = this.username.length();
    StringBuilder stringBuilder = new StringBuilder(i + 16);
    for (byte b = 0; b < i; b++) {
      char c = this.username.charAt(b);
      switch (c) {
        case '*':
          stringBuilder.append("\\\\2A");
          break;
        case '(':
          stringBuilder.append("\\\\28");
          break;
        case ')':
          stringBuilder.append("\\\\29");
          break;
        case '\\':
          stringBuilder.append("\\\\5C");
          break;
        case '\000':
          stringBuilder.append("\\\\00");
          break;
        default:
          stringBuilder.append(c);
          break;
      } 
    } 
    return stringBuilder.toString();
  }
  
  private String replaceUsernameToken(Matcher paramMatcher, String paramString1, String paramString2) { return (paramMatcher != null) ? paramMatcher.replaceAll(paramString2) : paramString1; }
  
  private void getUsernamePassword(boolean paramBoolean) throws LoginException {
    if (paramBoolean) {
      this.username = (String)this.sharedState.get("javax.security.auth.login.name");
      this.password = (char[])this.sharedState.get("javax.security.auth.login.password");
      return;
    } 
    if (this.callbackHandler == null)
      throw new LoginException("No CallbackHandler available to acquire authentication information from the user"); 
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback(rb.getString("username."));
    arrayOfCallback[1] = new PasswordCallback(rb.getString("password."), false);
    try {
      this.callbackHandler.handle(arrayOfCallback);
      this.username = ((NameCallback)arrayOfCallback[0]).getName();
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[1]).getPassword();
      this.password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, this.password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[1]).clearPassword();
    } catch (IOException iOException) {
      throw new LoginException(iOException.toString());
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new LoginException("Error: " + unsupportedCallbackException.getCallback().toString() + " not available to acquire authentication information from the user");
    } 
  }
  
  private void cleanState() {
    this.username = null;
    if (this.password != null) {
      Arrays.fill(this.password, ' ');
      this.password = null;
    } 
    try {
      if (this.ctx != null)
        this.ctx.close(); 
    } catch (NamingException namingException) {}
    this.ctx = null;
    if (this.clearPass) {
      this.sharedState.remove("javax.security.auth.login.name");
      this.sharedState.remove("javax.security.auth.login.password");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\LdapLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
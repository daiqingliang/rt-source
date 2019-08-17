package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.Config;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;
import sun.security.krb5.internal.ktab.KeyTab;

@Exported
public class Krb5LoginModule implements LoginModule {
  private Subject subject;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, Object> sharedState;
  
  private Map<String, ?> options;
  
  private boolean debug = false;
  
  private boolean storeKey = false;
  
  private boolean doNotPrompt = false;
  
  private boolean useTicketCache = false;
  
  private boolean useKeyTab = false;
  
  private String ticketCacheName = null;
  
  private String keyTabName = null;
  
  private String princName = null;
  
  private boolean useFirstPass = false;
  
  private boolean tryFirstPass = false;
  
  private boolean storePass = false;
  
  private boolean clearPass = false;
  
  private boolean refreshKrb5Config = false;
  
  private boolean renewTGT = false;
  
  private boolean isInitiator = true;
  
  private boolean succeeded = false;
  
  private boolean commitSucceeded = false;
  
  private String username;
  
  private EncryptionKey[] encKeys = null;
  
  KeyTab ktab = null;
  
  private Credentials cred = null;
  
  private PrincipalName principal = null;
  
  private KerberosPrincipal kerbClientPrinc = null;
  
  private KerberosTicket kerbTicket = null;
  
  private KerberosKey[] kerbKeys = null;
  
  private StringBuffer krb5PrincName = null;
  
  private boolean unboundServer = false;
  
  private char[] password = null;
  
  private static final String NAME = "javax.security.auth.login.name";
  
  private static final String PWD = "javax.security.auth.login.password";
  
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction<ResourceBundle>() {
        public ResourceBundle run() { return ResourceBundle.getBundle("sun.security.util.AuthResources"); }
      });
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    this.storeKey = "true".equalsIgnoreCase((String)paramMap2.get("storeKey"));
    this.doNotPrompt = "true".equalsIgnoreCase((String)paramMap2.get("doNotPrompt"));
    this.useTicketCache = "true".equalsIgnoreCase((String)paramMap2.get("useTicketCache"));
    this.useKeyTab = "true".equalsIgnoreCase((String)paramMap2.get("useKeyTab"));
    this.ticketCacheName = (String)paramMap2.get("ticketCache");
    this.keyTabName = (String)paramMap2.get("keyTab");
    if (this.keyTabName != null)
      this.keyTabName = KeyTab.normalize(this.keyTabName); 
    this.princName = (String)paramMap2.get("principal");
    this.refreshKrb5Config = "true".equalsIgnoreCase((String)paramMap2.get("refreshKrb5Config"));
    this.renewTGT = "true".equalsIgnoreCase((String)paramMap2.get("renewTGT"));
    String str = (String)paramMap2.get("isInitiator");
    if (str != null)
      this.isInitiator = "true".equalsIgnoreCase(str); 
    this.tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    this.useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    this.storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    this.clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    if (this.debug)
      System.out.print("Debug is  " + this.debug + " storeKey " + this.storeKey + " useTicketCache " + this.useTicketCache + " useKeyTab " + this.useKeyTab + " doNotPrompt " + this.doNotPrompt + " ticketCache is " + this.ticketCacheName + " isInitiator " + this.isInitiator + " KeyTab is " + this.keyTabName + " refreshKrb5Config is " + this.refreshKrb5Config + " principal is " + this.princName + " tryFirstPass is " + this.tryFirstPass + " useFirstPass is " + this.useFirstPass + " storePass is " + this.storePass + " clearPass is " + this.clearPass + "\n"); 
  }
  
  public boolean login() throws LoginException {
    if (this.refreshKrb5Config)
      try {
        if (this.debug)
          System.out.println("Refreshing Kerberos configuration"); 
        Config.refresh();
      } catch (KrbException krbException) {
        LoginException loginException = new LoginException(krbException.getMessage());
        loginException.initCause(krbException);
        throw loginException;
      }  
    String str = System.getProperty("sun.security.krb5.principal");
    if (str != null) {
      this.krb5PrincName = new StringBuffer(str);
    } else if (this.princName != null) {
      this.krb5PrincName = new StringBuffer(this.princName);
    } 
    validateConfiguration();
    if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*"))
      this.unboundServer = true; 
    if (this.tryFirstPass) {
      try {
        attemptAuthentication(true);
        if (this.debug)
          System.out.println("\t\t[Krb5LoginModule] authentication succeeded"); 
        this.succeeded = true;
        cleanState();
        return true;
      } catch (LoginException loginException) {
        cleanState();
        if (this.debug)
          System.out.println("\t\t[Krb5LoginModule] tryFirstPass failed with:" + loginException.getMessage()); 
      } 
    } else if (this.useFirstPass) {
      try {
        attemptAuthentication(true);
        this.succeeded = true;
        cleanState();
        return true;
      } catch (LoginException loginException) {
        if (this.debug)
          System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + loginException.getMessage()); 
        this.succeeded = false;
        cleanState();
        throw loginException;
      } 
    } 
    try {
      attemptAuthentication(false);
      this.succeeded = true;
      cleanState();
      return true;
    } catch (LoginException loginException) {
      if (this.debug)
        System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + loginException.getMessage()); 
      this.succeeded = false;
      cleanState();
      throw loginException;
    } 
  }
  
  private void attemptAuthentication(boolean paramBoolean) throws LoginException {
    if (this.krb5PrincName != null)
      try {
        this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
      } catch (KrbException krbException) {
        LoginException loginException = new LoginException(krbException.getMessage());
        loginException.initCause(krbException);
        throw loginException;
      }  
    try {
      if (this.useTicketCache) {
        if (this.debug)
          System.out.println("Acquire TGT from Cache"); 
        this.cred = Credentials.acquireTGTFromCache(this.principal, this.ticketCacheName);
        if (this.cred != null && !isCurrent(this.cred))
          if (this.renewTGT) {
            this.cred = renewCredentials(this.cred);
          } else {
            this.cred = null;
            if (this.debug)
              System.out.println("Credentials are no longer valid"); 
          }  
        if (this.cred != null && this.principal == null)
          this.principal = this.cred.getClient(); 
        if (this.debug) {
          System.out.println("Principal is " + this.principal);
          if (this.cred == null)
            System.out.println("null credentials from Ticket Cache"); 
        } 
      } 
      if (this.cred == null) {
        KrbAsReqBuilder krbAsReqBuilder;
        if (this.principal == null) {
          promptForName(paramBoolean);
          this.principal = new PrincipalName(this.krb5PrincName.toString(), 1);
        } 
        if (this.useKeyTab) {
          if (!this.unboundServer) {
            krbAsReqBuilder = new KerberosPrincipal(this.principal.getName());
            this.ktab = (this.keyTabName == null) ? KeyTab.getInstance(krbAsReqBuilder) : KeyTab.getInstance(krbAsReqBuilder, new File(this.keyTabName));
          } else {
            this.ktab = (this.keyTabName == null) ? KeyTab.getUnboundInstance() : KeyTab.getUnboundInstance(new File(this.keyTabName));
          } 
          if (this.isInitiator && Krb5Util.keysFromJavaxKeyTab(this.ktab, this.principal).length == 0) {
            this.ktab = null;
            if (this.debug)
              System.out.println("Key for the principal " + this.principal + " not available in " + ((this.keyTabName == null) ? "default key tab" : this.keyTabName)); 
          } 
        } 
        if (this.ktab == null) {
          promptForPass(paramBoolean);
          krbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.password);
          if (this.isInitiator)
            this.cred = krbAsReqBuilder.action().getCreds(); 
          if (this.storeKey)
            this.encKeys = krbAsReqBuilder.getKeys(this.isInitiator); 
        } else {
          krbAsReqBuilder = new KrbAsReqBuilder(this.principal, this.ktab);
          if (this.isInitiator)
            this.cred = krbAsReqBuilder.action().getCreds(); 
        } 
        krbAsReqBuilder.destroy();
        if (this.debug) {
          System.out.println("principal is " + this.principal);
          HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
          if (this.ktab != null) {
            System.out.println("Will use keytab");
          } else if (this.storeKey) {
            for (byte b = 0; b < this.encKeys.length; b++)
              System.out.println("EncryptionKey: keyType=" + this.encKeys[b].getEType() + " keyBytes (hex dump)=" + hexDumpEncoder.encodeBuffer(this.encKeys[b].getBytes())); 
          } 
        } 
        if (this.isInitiator && this.cred == null)
          throw new LoginException("TGT Can not be obtained from the KDC "); 
      } 
    } catch (KrbException krbException) {
      LoginException loginException = new LoginException(krbException.getMessage());
      loginException.initCause(krbException);
      throw loginException;
    } catch (IOException iOException) {
      LoginException loginException = new LoginException(iOException.getMessage());
      loginException.initCause(iOException);
      throw loginException;
    } 
  }
  
  private void promptForName(boolean paramBoolean) throws LoginException {
    this.krb5PrincName = new StringBuffer("");
    if (paramBoolean) {
      this.username = (String)this.sharedState.get("javax.security.auth.login.name");
      if (this.debug)
        System.out.println("username from shared state is " + this.username + "\n"); 
      if (this.username == null) {
        System.out.println("username from shared state is null\n");
        throw new LoginException("Username can not be obtained from sharedstate ");
      } 
      if (this.debug)
        System.out.println("username from shared state is " + this.username + "\n"); 
      if (this.username != null && this.username.length() > 0) {
        this.krb5PrincName.insert(0, this.username);
        return;
      } 
    } 
    if (this.doNotPrompt)
      throw new LoginException("Unable to obtain Principal Name for authentication "); 
    if (this.callbackHandler == null)
      throw new LoginException("No CallbackHandler available to garner authentication information from the user"); 
    try {
      String str = System.getProperty("user.name");
      Callback[] arrayOfCallback = new Callback[1];
      MessageFormat messageFormat = new MessageFormat(rb.getString("Kerberos.username.defUsername."));
      Object[] arrayOfObject = { str };
      arrayOfCallback[0] = new NameCallback(messageFormat.format(arrayOfObject));
      this.callbackHandler.handle(arrayOfCallback);
      this.username = ((NameCallback)arrayOfCallback[0]).getName();
      if (this.username == null || this.username.length() == 0)
        this.username = str; 
      this.krb5PrincName.insert(0, this.username);
    } catch (IOException iOException) {
      throw new LoginException(iOException.getMessage());
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new LoginException(unsupportedCallbackException.getMessage() + " not available to garner  authentication information  from the user");
    } 
  }
  
  private void promptForPass(boolean paramBoolean) throws LoginException {
    if (paramBoolean) {
      this.password = (char[])this.sharedState.get("javax.security.auth.login.password");
      if (this.password == null) {
        if (this.debug)
          System.out.println("Password from shared state is null"); 
        throw new LoginException("Password can not be obtained from sharedstate ");
      } 
      if (this.debug)
        System.out.println("password is " + new String(this.password)); 
      return;
    } 
    if (this.doNotPrompt)
      throw new LoginException("Unable to obtain password from user\n"); 
    if (this.callbackHandler == null)
      throw new LoginException("No CallbackHandler available to garner authentication information from the user"); 
    try {
      Callback[] arrayOfCallback = new Callback[1];
      String str = this.krb5PrincName.toString();
      MessageFormat messageFormat = new MessageFormat(rb.getString("Kerberos.password.for.username."));
      Object[] arrayOfObject = { str };
      arrayOfCallback[0] = new PasswordCallback(messageFormat.format(arrayOfObject), false);
      this.callbackHandler.handle(arrayOfCallback);
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[0]).getPassword();
      if (arrayOfChar == null)
        throw new LoginException("No password provided"); 
      this.password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, this.password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[0]).clearPassword();
      for (byte b = 0; b < arrayOfChar.length; b++)
        arrayOfChar[b] = ' '; 
      arrayOfChar = null;
      if (this.debug) {
        System.out.println("\t\t[Krb5LoginModule] user entered username: " + this.krb5PrincName);
        System.out.println();
      } 
    } catch (IOException iOException) {
      throw new LoginException(iOException.getMessage());
    } catch (UnsupportedCallbackException unsupportedCallbackException) {
      throw new LoginException(unsupportedCallbackException.getMessage() + " not available to garner  authentication information from the user");
    } 
  }
  
  private void validateConfiguration() {
    if (this.doNotPrompt && !this.useTicketCache && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass)
      throw new LoginException("Configuration Error - either doNotPrompt should be  false or at least one of useTicketCache,  useKeyTab, tryFirstPass and useFirstPass should be true"); 
    if (this.ticketCacheName != null && !this.useTicketCache)
      throw new LoginException("Configuration Error  - useTicketCache should be set to true to use the ticket cache" + this.ticketCacheName); 
    if (((this.keyTabName != null) ? 1 : 0) & (!this.useKeyTab ? 1 : 0))
      throw new LoginException("Configuration Error - useKeyTab should be set to true to use the keytab" + this.keyTabName); 
    if (this.storeKey && this.doNotPrompt && !this.useKeyTab && !this.tryFirstPass && !this.useFirstPass)
      throw new LoginException("Configuration Error - either doNotPrompt should be set to  false or at least one of tryFirstPass, useFirstPass or useKeyTab must be set to true for storeKey option"); 
    if (this.renewTGT && !this.useTicketCache)
      throw new LoginException("Configuration Error - either useTicketCache should be  true or renewTGT should be false"); 
    if (this.krb5PrincName != null && this.krb5PrincName.toString().equals("*") && this.isInitiator)
      throw new LoginException("Configuration Error - principal cannot be * when isInitiator is true"); 
  }
  
  private boolean isCurrent(Credentials paramCredentials) {
    Date date = paramCredentials.getEndTime();
    return (date != null) ? ((System.currentTimeMillis() <= date.getTime())) : true;
  }
  
  private Credentials renewCredentials(Credentials paramCredentials) {
    Credentials credentials;
    try {
      if (!paramCredentials.isRenewable())
        throw new RefreshFailedException("This ticket is not renewable"); 
      if (System.currentTimeMillis() > this.cred.getRenewTill().getTime())
        throw new RefreshFailedException("This ticket is past its last renewal time."); 
      credentials = paramCredentials.renew();
      if (this.debug)
        System.out.println("Renewed Kerberos Ticket"); 
    } catch (Exception exception) {
      credentials = null;
      if (this.debug)
        System.out.println("Ticket could not be renewed : " + exception.getMessage()); 
    } 
    return credentials;
  }
  
  public boolean commit() throws LoginException {
    if (!this.succeeded)
      return false; 
    if (this.isInitiator && this.cred == null) {
      this.succeeded = false;
      throw new LoginException("Null Client Credential");
    } 
    if (this.subject.isReadOnly()) {
      cleanKerberosCred();
      throw new LoginException("Subject is Readonly");
    } 
    Set set1 = this.subject.getPrivateCredentials();
    Set set2 = this.subject.getPrincipals();
    this.kerbClientPrinc = new KerberosPrincipal(this.principal.getName());
    if (this.isInitiator)
      this.kerbTicket = Krb5Util.credsToTicket(this.cred); 
    if (this.storeKey && this.encKeys != null) {
      if (this.encKeys.length == 0) {
        this.succeeded = false;
        throw new LoginException("Null Server Key ");
      } 
      this.kerbKeys = new KerberosKey[this.encKeys.length];
      for (byte b = 0; b < this.encKeys.length; b++) {
        Integer integer = this.encKeys[b].getKeyVersionNumber();
        this.kerbKeys[b] = new KerberosKey(this.kerbClientPrinc, this.encKeys[b].getBytes(), this.encKeys[b].getEType(), (integer == null) ? 0 : integer.intValue());
      } 
    } 
    if (!this.unboundServer && !set2.contains(this.kerbClientPrinc))
      set2.add(this.kerbClientPrinc); 
    if (this.kerbTicket != null && !set1.contains(this.kerbTicket))
      set1.add(this.kerbTicket); 
    if (this.storeKey)
      if (this.encKeys == null) {
        if (this.ktab != null) {
          if (!set1.contains(this.ktab))
            set1.add(this.ktab); 
        } else {
          this.succeeded = false;
          throw new LoginException("No key to store");
        } 
      } else {
        for (byte b = 0; b < this.kerbKeys.length; b++) {
          if (!set1.contains(this.kerbKeys[b]))
            set1.add(this.kerbKeys[b]); 
          this.encKeys[b].destroy();
          this.encKeys[b] = null;
          if (this.debug) {
            System.out.println("Added server's key" + this.kerbKeys[b]);
            System.out.println("\t\t[Krb5LoginModule] added Krb5Principal  " + this.kerbClientPrinc.toString() + " to Subject");
          } 
        } 
      }  
    this.commitSucceeded = true;
    if (this.debug)
      System.out.println("Commit Succeeded \n"); 
    return true;
  }
  
  public boolean abort() throws LoginException {
    if (!this.succeeded)
      return false; 
    if (this.succeeded == true && !this.commitSucceeded) {
      this.succeeded = false;
      cleanKerberosCred();
    } else {
      logout();
    } 
    return true;
  }
  
  public boolean logout() throws LoginException {
    if (this.debug)
      System.out.println("\t\t[Krb5LoginModule]: Entering logout"); 
    if (this.subject.isReadOnly()) {
      cleanKerberosCred();
      throw new LoginException("Subject is Readonly");
    } 
    this.subject.getPrincipals().remove(this.kerbClientPrinc);
    Iterator iterator = this.subject.getPrivateCredentials().iterator();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (object instanceof KerberosTicket || object instanceof KerberosKey || object instanceof KeyTab)
        iterator.remove(); 
    } 
    cleanKerberosCred();
    this.succeeded = false;
    this.commitSucceeded = false;
    if (this.debug)
      System.out.println("\t\t[Krb5LoginModule]: logged out Subject"); 
    return true;
  }
  
  private void cleanKerberosCred() {
    try {
      if (this.kerbTicket != null)
        this.kerbTicket.destroy(); 
      if (this.kerbKeys != null)
        for (byte b = 0; b < this.kerbKeys.length; b++)
          this.kerbKeys[b].destroy();  
    } catch (DestroyFailedException destroyFailedException) {
      throw new LoginException("Destroy Failed on Kerberos Private Credentials");
    } 
    this.kerbTicket = null;
    this.kerbKeys = null;
    this.kerbClientPrinc = null;
  }
  
  private void cleanState() {
    if (this.succeeded) {
      if (this.storePass && !this.sharedState.containsKey("javax.security.auth.login.name") && !this.sharedState.containsKey("javax.security.auth.login.password")) {
        this.sharedState.put("javax.security.auth.login.name", this.username);
        this.sharedState.put("javax.security.auth.login.password", this.password);
      } 
    } else {
      this.encKeys = null;
      this.ktab = null;
      this.principal = null;
    } 
    this.username = null;
    this.password = null;
    if (this.krb5PrincName != null && this.krb5PrincName.length() != 0)
      this.krb5PrincName.delete(0, this.krb5PrincName.length()); 
    this.krb5PrincName = null;
    if (this.clearPass) {
      this.sharedState.remove("javax.security.auth.login.name");
      this.sharedState.remove("javax.security.auth.login.password");
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\Krb5LoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
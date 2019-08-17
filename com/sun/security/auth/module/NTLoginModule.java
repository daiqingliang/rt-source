package com.sun.security.auth.module;

import com.sun.security.auth.NTDomainPrincipal;
import com.sun.security.auth.NTNumericCredential;
import com.sun.security.auth.NTSidDomainPrincipal;
import com.sun.security.auth.NTSidGroupPrincipal;
import com.sun.security.auth.NTSidPrimaryGroupPrincipal;
import com.sun.security.auth.NTSidUserPrincipal;
import com.sun.security.auth.NTUserPrincipal;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class NTLoginModule implements LoginModule {
  private NTSystem ntSystem;
  
  private Subject subject;
  
  private CallbackHandler callbackHandler;
  
  private Map<String, ?> sharedState;
  
  private Map<String, ?> options;
  
  private boolean debug = false;
  
  private boolean debugNative = false;
  
  private boolean succeeded = false;
  
  private boolean commitSucceeded = false;
  
  private NTUserPrincipal userPrincipal;
  
  private NTSidUserPrincipal userSID;
  
  private NTDomainPrincipal userDomain;
  
  private NTSidDomainPrincipal domainSID;
  
  private NTSidPrimaryGroupPrincipal primaryGroup;
  
  private NTSidGroupPrincipal[] groups;
  
  private NTNumericCredential iToken;
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2) {
    this.subject = paramSubject;
    this.callbackHandler = paramCallbackHandler;
    this.sharedState = paramMap1;
    this.options = paramMap2;
    this.debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    this.debugNative = "true".equalsIgnoreCase((String)paramMap2.get("debugNative"));
    if (this.debugNative == true)
      this.debug = true; 
  }
  
  public boolean login() throws LoginException {
    this.succeeded = false;
    this.ntSystem = new NTSystem(this.debugNative);
    if (this.ntSystem == null) {
      if (this.debug)
        System.out.println("\t\t[NTLoginModule] Failed in NT login"); 
      throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information");
    } 
    if (this.ntSystem.getName() == null)
      throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information"); 
    this.userPrincipal = new NTUserPrincipal(this.ntSystem.getName());
    if (this.debug) {
      System.out.println("\t\t[NTLoginModule] succeeded importing info: ");
      System.out.println("\t\t\tuser name = " + this.userPrincipal.getName());
    } 
    if (this.ntSystem.getUserSID() != null) {
      this.userSID = new NTSidUserPrincipal(this.ntSystem.getUserSID());
      if (this.debug)
        System.out.println("\t\t\tuser SID = " + this.userSID.getName()); 
    } 
    if (this.ntSystem.getDomain() != null) {
      this.userDomain = new NTDomainPrincipal(this.ntSystem.getDomain());
      if (this.debug)
        System.out.println("\t\t\tuser domain = " + this.userDomain.getName()); 
    } 
    if (this.ntSystem.getDomainSID() != null) {
      this.domainSID = new NTSidDomainPrincipal(this.ntSystem.getDomainSID());
      if (this.debug)
        System.out.println("\t\t\tuser domain SID = " + this.domainSID.getName()); 
    } 
    if (this.ntSystem.getPrimaryGroupID() != null) {
      this.primaryGroup = new NTSidPrimaryGroupPrincipal(this.ntSystem.getPrimaryGroupID());
      if (this.debug)
        System.out.println("\t\t\tuser primary group = " + this.primaryGroup.getName()); 
    } 
    if (this.ntSystem.getGroupIDs() != null && this.ntSystem.getGroupIDs().length > 0) {
      String[] arrayOfString = this.ntSystem.getGroupIDs();
      this.groups = new NTSidGroupPrincipal[arrayOfString.length];
      for (byte b = 0; b < arrayOfString.length; b++) {
        this.groups[b] = new NTSidGroupPrincipal(arrayOfString[b]);
        if (this.debug)
          System.out.println("\t\t\tuser group = " + this.groups[b].getName()); 
      } 
    } 
    if (this.ntSystem.getImpersonationToken() != 0L) {
      this.iToken = new NTNumericCredential(this.ntSystem.getImpersonationToken());
      if (this.debug)
        System.out.println("\t\t\timpersonation token = " + this.ntSystem.getImpersonationToken()); 
    } 
    this.succeeded = true;
    return this.succeeded;
  }
  
  public boolean commit() throws LoginException {
    if (!this.succeeded) {
      if (this.debug)
        System.out.println("\t\t[NTLoginModule]: did not add any Principals to Subject because own authentication failed."); 
      return false;
    } 
    if (this.subject.isReadOnly())
      throw new LoginException("Subject is ReadOnly"); 
    Set set1 = this.subject.getPrincipals();
    if (!set1.contains(this.userPrincipal))
      set1.add(this.userPrincipal); 
    if (this.userSID != null && !set1.contains(this.userSID))
      set1.add(this.userSID); 
    if (this.userDomain != null && !set1.contains(this.userDomain))
      set1.add(this.userDomain); 
    if (this.domainSID != null && !set1.contains(this.domainSID))
      set1.add(this.domainSID); 
    if (this.primaryGroup != null && !set1.contains(this.primaryGroup))
      set1.add(this.primaryGroup); 
    for (byte b = 0; this.groups != null && b < this.groups.length; b++) {
      if (!set1.contains(this.groups[b]))
        set1.add(this.groups[b]); 
    } 
    Set set2 = this.subject.getPublicCredentials();
    if (this.iToken != null && !set2.contains(this.iToken))
      set2.add(this.iToken); 
    this.commitSucceeded = true;
    return true;
  }
  
  public boolean abort() throws LoginException {
    if (this.debug)
      System.out.println("\t\t[NTLoginModule]: aborted authentication attempt"); 
    if (!this.succeeded)
      return false; 
    if (this.succeeded == true && !this.commitSucceeded) {
      this.ntSystem = null;
      this.userPrincipal = null;
      this.userSID = null;
      this.userDomain = null;
      this.domainSID = null;
      this.primaryGroup = null;
      this.groups = null;
      this.iToken = null;
      this.succeeded = false;
    } else {
      logout();
    } 
    return this.succeeded;
  }
  
  public boolean logout() throws LoginException {
    if (this.subject.isReadOnly())
      throw new LoginException("Subject is ReadOnly"); 
    Set set1 = this.subject.getPrincipals();
    if (set1.contains(this.userPrincipal))
      set1.remove(this.userPrincipal); 
    if (set1.contains(this.userSID))
      set1.remove(this.userSID); 
    if (set1.contains(this.userDomain))
      set1.remove(this.userDomain); 
    if (set1.contains(this.domainSID))
      set1.remove(this.domainSID); 
    if (set1.contains(this.primaryGroup))
      set1.remove(this.primaryGroup); 
    for (byte b = 0; this.groups != null && b < this.groups.length; b++) {
      if (set1.contains(this.groups[b]))
        set1.remove(this.groups[b]); 
    } 
    Set set2 = this.subject.getPublicCredentials();
    if (set2.contains(this.iToken))
      set2.remove(this.iToken); 
    this.succeeded = false;
    this.commitSucceeded = false;
    this.userPrincipal = null;
    this.userDomain = null;
    this.userSID = null;
    this.domainSID = null;
    this.groups = null;
    this.primaryGroup = null;
    this.iToken = null;
    this.ntSystem = null;
    if (this.debug)
      System.out.println("\t\t[NTLoginModule] completed logout processing"); 
    return true;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\NTLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
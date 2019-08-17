package com.sun.jndi.ldap;

import java.util.Hashtable;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapReferralException;

public final class LdapReferralException extends LdapReferralException {
  private static final long serialVersionUID = 627059076356906399L;
  
  private int handleReferrals;
  
  private Hashtable<?, ?> envprops;
  
  private String nextName;
  
  private Control[] reqCtls;
  
  private Vector<?> referrals = null;
  
  private int referralIndex = 0;
  
  private int referralCount = 0;
  
  private boolean foundEntry = false;
  
  private boolean skipThisReferral = false;
  
  private int hopCount = 1;
  
  private NamingException errorEx = null;
  
  private String newRdn = null;
  
  private boolean debug = false;
  
  LdapReferralException nextReferralEx = null;
  
  LdapReferralException(Name paramName1, Object paramObject, Name paramName2, String paramString1, Hashtable<?, ?> paramHashtable, String paramString2, int paramInt, Control[] paramArrayOfControl) {
    super(paramString1);
    if (this.debug)
      System.out.println("LdapReferralException constructor"); 
    setResolvedName(paramName1);
    setResolvedObj(paramObject);
    setRemainingName(paramName2);
    this.envprops = paramHashtable;
    this.nextName = paramString2;
    this.handleReferrals = paramInt;
    this.reqCtls = (paramInt == 1 || paramInt == 4) ? paramArrayOfControl : null;
  }
  
  public Context getReferralContext() throws NamingException { return getReferralContext(this.envprops, null); }
  
  public Context getReferralContext(Hashtable<?, ?> paramHashtable) throws NamingException { return getReferralContext(paramHashtable, null); }
  
  public Context getReferralContext(Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl) throws NamingException {
    if (this.debug)
      System.out.println("LdapReferralException.getReferralContext"); 
    LdapReferralContext ldapReferralContext = new LdapReferralContext(this, paramHashtable, paramArrayOfControl, this.reqCtls, this.nextName, this.skipThisReferral, this.handleReferrals);
    ldapReferralContext.setHopCount(this.hopCount + 1);
    if (this.skipThisReferral)
      this.skipThisReferral = false; 
    return ldapReferralContext;
  }
  
  public Object getReferralInfo() {
    if (this.debug) {
      System.out.println("LdapReferralException.getReferralInfo");
      System.out.println("  referralIndex=" + this.referralIndex);
    } 
    return hasMoreReferrals() ? this.referrals.elementAt(this.referralIndex) : null;
  }
  
  public void retryReferral() {
    if (this.debug)
      System.out.println("LdapReferralException.retryReferral"); 
    if (this.referralIndex > 0)
      this.referralIndex--; 
  }
  
  public boolean skipReferral() {
    if (this.debug)
      System.out.println("LdapReferralException.skipReferral"); 
    this.skipThisReferral = true;
    try {
      getNextReferral();
    } catch (ReferralException referralException) {}
    return (hasMoreReferrals() || hasMoreReferralExceptions());
  }
  
  void setReferralInfo(Vector<?> paramVector, boolean paramBoolean) {
    if (this.debug)
      System.out.println("LdapReferralException.setReferralInfo"); 
    this.referrals = paramVector;
    this.referralCount = (paramVector == null) ? 0 : paramVector.size();
    if (this.debug)
      if (paramVector != null) {
        for (byte b = 0; b < this.referralCount; b++)
          System.out.println("  [" + b + "] " + paramVector.elementAt(b)); 
      } else {
        System.out.println("setReferralInfo : referrals == null");
      }  
  }
  
  String getNextReferral() throws ReferralException {
    if (this.debug)
      System.out.println("LdapReferralException.getNextReferral"); 
    if (hasMoreReferrals())
      return (String)this.referrals.elementAt(this.referralIndex++); 
    if (hasMoreReferralExceptions())
      throw this.nextReferralEx; 
    return null;
  }
  
  LdapReferralException appendUnprocessedReferrals(LdapReferralException paramLdapReferralException) {
    if (this.debug) {
      System.out.println("LdapReferralException.appendUnprocessedReferrals");
      dump();
      if (paramLdapReferralException != null)
        paramLdapReferralException.dump(); 
    } 
    LdapReferralException ldapReferralException1 = this;
    if (!ldapReferralException1.hasMoreReferrals()) {
      ldapReferralException1 = this.nextReferralEx;
      if (this.errorEx != null && ldapReferralException1 != null)
        ldapReferralException1.setNamingException(this.errorEx); 
    } 
    if (this == paramLdapReferralException)
      return ldapReferralException1; 
    if (paramLdapReferralException != null && !paramLdapReferralException.hasMoreReferrals())
      paramLdapReferralException = paramLdapReferralException.nextReferralEx; 
    if (paramLdapReferralException == null)
      return ldapReferralException1; 
    LdapReferralException ldapReferralException2;
    for (ldapReferralException2 = ldapReferralException1; ldapReferralException2.nextReferralEx != null; ldapReferralException2 = ldapReferralException2.nextReferralEx);
    ldapReferralException2.nextReferralEx = paramLdapReferralException;
    return ldapReferralException1;
  }
  
  boolean hasMoreReferrals() {
    if (this.debug)
      System.out.println("LdapReferralException.hasMoreReferrals"); 
    return (!this.foundEntry && this.referralIndex < this.referralCount);
  }
  
  boolean hasMoreReferralExceptions() {
    if (this.debug)
      System.out.println("LdapReferralException.hasMoreReferralExceptions"); 
    return (this.nextReferralEx != null);
  }
  
  void setHopCount(int paramInt) {
    if (this.debug)
      System.out.println("LdapReferralException.setHopCount"); 
    this.hopCount = paramInt;
  }
  
  void setNameResolved(boolean paramBoolean) {
    if (this.debug)
      System.out.println("LdapReferralException.setNameResolved"); 
    this.foundEntry = paramBoolean;
  }
  
  void setNamingException(NamingException paramNamingException) {
    if (this.debug)
      System.out.println("LdapReferralException.setNamingException"); 
    if (this.errorEx == null) {
      paramNamingException.setRootCause(this);
      this.errorEx = paramNamingException;
    } 
  }
  
  String getNewRdn() throws ReferralException {
    if (this.debug)
      System.out.println("LdapReferralException.getNewRdn"); 
    return this.newRdn;
  }
  
  void setNewRdn(String paramString) {
    if (this.debug)
      System.out.println("LdapReferralException.setNewRdn"); 
    this.newRdn = paramString;
  }
  
  NamingException getNamingException() {
    if (this.debug)
      System.out.println("LdapReferralException.getNamingException"); 
    return this.errorEx;
  }
  
  void dump() {
    System.out.println();
    System.out.println("LdapReferralException.dump");
    for (LdapReferralException ldapReferralException = this; ldapReferralException != null; ldapReferralException = ldapReferralException.nextReferralEx)
      ldapReferralException.dumpState(); 
  }
  
  private void dumpState() {
    System.out.println("LdapReferralException.dumpState");
    System.out.println("  hashCode=" + hashCode());
    System.out.println("  foundEntry=" + this.foundEntry);
    System.out.println("  skipThisReferral=" + this.skipThisReferral);
    System.out.println("  referralIndex=" + this.referralIndex);
    if (this.referrals != null) {
      System.out.println("  referrals:");
      for (byte b = 0; b < this.referralCount; b++)
        System.out.println("    [" + b + "] " + this.referrals.elementAt(b)); 
    } else {
      System.out.println("  referrals=null");
    } 
    System.out.println("  errorEx=" + this.errorEx);
    if (this.nextReferralEx == null) {
      System.out.println("  nextRefEx=null");
    } else {
      System.out.println("  nextRefEx=" + this.nextReferralEx.hashCode());
    } 
    System.out.println();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapReferralException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
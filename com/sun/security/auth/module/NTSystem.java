package com.sun.security.auth.module;

import jdk.Exported;

@Exported
public class NTSystem {
  private String userName;
  
  private String domain;
  
  private String domainSID;
  
  private String userSID;
  
  private String[] groupIDs;
  
  private String primaryGroupID;
  
  private long impersonationToken;
  
  private native void getCurrent(boolean paramBoolean);
  
  private native long getImpersonationToken0();
  
  public NTSystem() { this(false); }
  
  NTSystem(boolean paramBoolean) {
    loadNative();
    getCurrent(paramBoolean);
  }
  
  public String getName() { return this.userName; }
  
  public String getDomain() { return this.domain; }
  
  public String getDomainSID() { return this.domainSID; }
  
  public String getUserSID() { return this.userSID; }
  
  public String getPrimaryGroupID() { return this.primaryGroupID; }
  
  public String[] getGroupIDs() { return (this.groupIDs == null) ? null : (String[])this.groupIDs.clone(); }
  
  public long getImpersonationToken() {
    if (this.impersonationToken == 0L)
      this.impersonationToken = getImpersonationToken0(); 
    return this.impersonationToken;
  }
  
  private void loadNative() { System.loadLibrary("jaas_nt"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\module\NTSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
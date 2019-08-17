package com.sun.security.auth;

import java.security.CodeSource;
import java.security.PermissionCollection;
import javax.security.auth.Policy;
import javax.security.auth.Subject;
import jdk.Exported;
import sun.security.provider.AuthPolicyFile;

@Exported(false)
@Deprecated
public class PolicyFile extends Policy {
  private final AuthPolicyFile apf = new AuthPolicyFile();
  
  public void refresh() { this.apf.refresh(); }
  
  public PermissionCollection getPermissions(Subject paramSubject, CodeSource paramCodeSource) { return this.apf.getPermissions(paramSubject, paramCodeSource); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\PolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.jmx.remote.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;

public class JMXSubjectDomainCombiner extends SubjectDomainCombiner {
  private static final CodeSource nullCodeSource = new CodeSource(null, (Certificate[])null);
  
  private static final ProtectionDomain pdNoPerms = new ProtectionDomain(nullCodeSource, new Permissions(), null, null);
  
  public JMXSubjectDomainCombiner(Subject paramSubject) { super(paramSubject); }
  
  public ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2) {
    ProtectionDomain[] arrayOfProtectionDomain;
    if (paramArrayOfProtectionDomain1 == null || paramArrayOfProtectionDomain1.length == 0) {
      arrayOfProtectionDomain = new ProtectionDomain[1];
      arrayOfProtectionDomain[0] = pdNoPerms;
    } else {
      arrayOfProtectionDomain = new ProtectionDomain[paramArrayOfProtectionDomain1.length + 1];
      for (byte b = 0; b < paramArrayOfProtectionDomain1.length; b++)
        arrayOfProtectionDomain[b] = paramArrayOfProtectionDomain1[b]; 
      arrayOfProtectionDomain[paramArrayOfProtectionDomain1.length] = pdNoPerms;
    } 
    return super.combine(arrayOfProtectionDomain, paramArrayOfProtectionDomain2);
  }
  
  public static AccessControlContext getContext(Subject paramSubject) { return new AccessControlContext(AccessController.getContext(), new JMXSubjectDomainCombiner(paramSubject)); }
  
  public static AccessControlContext getDomainCombinerContext(Subject paramSubject) { return new AccessControlContext(new AccessControlContext(new ProtectionDomain[0]), new JMXSubjectDomainCombiner(paramSubject)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\remote\security\JMXSubjectDomainCombiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
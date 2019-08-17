package sun.security.provider;

import java.net.MalformedURLException;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PolicySpi;
import java.security.ProtectionDomain;
import java.security.URIParameter;

public final class PolicySpiFile extends PolicySpi {
  private PolicyFile pf;
  
  public PolicySpiFile(Policy.Parameters paramParameters) {
    if (paramParameters == null) {
      this.pf = new PolicyFile();
    } else {
      if (!(paramParameters instanceof URIParameter))
        throw new IllegalArgumentException("Unrecognized policy parameter: " + paramParameters); 
      URIParameter uRIParameter = (URIParameter)paramParameters;
      try {
        this.pf = new PolicyFile(uRIParameter.getURI().toURL());
      } catch (MalformedURLException malformedURLException) {
        throw new IllegalArgumentException("Invalid URIParameter", malformedURLException);
      } 
    } 
  }
  
  protected PermissionCollection engineGetPermissions(CodeSource paramCodeSource) { return this.pf.getPermissions(paramCodeSource); }
  
  protected PermissionCollection engineGetPermissions(ProtectionDomain paramProtectionDomain) { return this.pf.getPermissions(paramProtectionDomain); }
  
  protected boolean engineImplies(ProtectionDomain paramProtectionDomain, Permission paramPermission) { return this.pf.implies(paramProtectionDomain, paramPermission); }
  
  protected void engineRefresh() { this.pf.refresh(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\provider\PolicySpiFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
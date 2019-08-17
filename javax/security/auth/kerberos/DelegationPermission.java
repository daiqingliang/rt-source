package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;

public final class DelegationPermission extends BasicPermission implements Serializable {
  private static final long serialVersionUID = 883133252142523922L;
  
  private String subordinate;
  
  private String service;
  
  public DelegationPermission(String paramString) {
    super(paramString);
    init(paramString);
  }
  
  public DelegationPermission(String paramString1, String paramString2) {
    super(paramString1, paramString2);
    init(paramString1);
  }
  
  private void init(String paramString) {
    StringTokenizer stringTokenizer = null;
    if (!paramString.startsWith("\""))
      throw new IllegalArgumentException("service principal [" + paramString + "] syntax invalid: improperly quoted"); 
    stringTokenizer = new StringTokenizer(paramString, "\"", false);
    this.subordinate = stringTokenizer.nextToken();
    if (stringTokenizer.countTokens() == 2) {
      stringTokenizer.nextToken();
      this.service = stringTokenizer.nextToken();
    } else if (stringTokenizer.countTokens() > 0) {
      throw new IllegalArgumentException("service principal [" + stringTokenizer.nextToken() + "] syntax invalid: improperly quoted");
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof DelegationPermission))
      return false; 
    DelegationPermission delegationPermission = (DelegationPermission)paramPermission;
    return (this.subordinate.equals(delegationPermission.subordinate) && this.service.equals(delegationPermission.service));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof DelegationPermission))
      return false; 
    DelegationPermission delegationPermission = (DelegationPermission)paramObject;
    return implies(delegationPermission);
  }
  
  public int hashCode() { return getName().hashCode(); }
  
  public PermissionCollection newPermissionCollection() { return new KrbDelegationPermissionCollection(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.defaultWriteObject(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getName());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\DelegationPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Principal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import sun.security.util.ResourcesMgr;

public final class PrivateCredentialPermission extends Permission {
  private static final long serialVersionUID = 5284372143517237068L;
  
  private static final CredOwner[] EMPTY_PRINCIPALS = new CredOwner[0];
  
  private String credentialClass;
  
  private Set<Principal> principals;
  
  private CredOwner[] credOwners;
  
  private boolean testing = false;
  
  PrivateCredentialPermission(String paramString, Set<Principal> paramSet) {
    super(paramString);
    this.credentialClass = paramString;
    synchronized (paramSet) {
      if (paramSet.size() == 0) {
        this.credOwners = EMPTY_PRINCIPALS;
      } else {
        this.credOwners = new CredOwner[paramSet.size()];
        byte b = 0;
        for (Principal principal : paramSet)
          this.credOwners[b++] = new CredOwner(principal.getClass().getName(), principal.getName()); 
      } 
    } 
  }
  
  public PrivateCredentialPermission(String paramString1, String paramString2) {
    super(paramString1);
    if (!"read".equalsIgnoreCase(paramString2))
      throw new IllegalArgumentException(ResourcesMgr.getString("actions.can.only.be.read.")); 
    init(paramString1);
  }
  
  public String getCredentialClass() { return this.credentialClass; }
  
  public String[][] getPrincipals() {
    if (this.credOwners == null || this.credOwners.length == 0)
      return new String[0][0]; 
    String[][] arrayOfString = new String[this.credOwners.length][2];
    for (byte b = 0; b < this.credOwners.length; b++) {
      arrayOfString[b][0] = (this.credOwners[b]).principalClass;
      arrayOfString[b][1] = (this.credOwners[b]).principalName;
    } 
    return arrayOfString;
  }
  
  public boolean implies(Permission paramPermission) {
    if (paramPermission == null || !(paramPermission instanceof PrivateCredentialPermission))
      return false; 
    PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission)paramPermission;
    return !impliesCredentialClass(this.credentialClass, privateCredentialPermission.credentialClass) ? false : impliesPrincipalSet(this.credOwners, privateCredentialPermission.credOwners);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof PrivateCredentialPermission))
      return false; 
    PrivateCredentialPermission privateCredentialPermission = (PrivateCredentialPermission)paramObject;
    return (implies(privateCredentialPermission) && privateCredentialPermission.implies(this));
  }
  
  public int hashCode() { return this.credentialClass.hashCode(); }
  
  public String getActions() { return "read"; }
  
  public PermissionCollection newPermissionCollection() { return null; }
  
  private void init(String paramString) {
    if (paramString == null || paramString.trim().length() == 0)
      throw new IllegalArgumentException("invalid empty name"); 
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, " ", true);
    String str1 = null;
    String str2 = null;
    if (this.testing)
      System.out.println("whole name = " + paramString); 
    this.credentialClass = stringTokenizer.nextToken();
    if (this.testing)
      System.out.println("Credential Class = " + this.credentialClass); 
    if (!stringTokenizer.hasMoreTokens()) {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
      Object[] arrayOfObject = { paramString };
      throw new IllegalArgumentException(messageFormat.format(arrayOfObject) + ResourcesMgr.getString("Credential.Class.not.followed.by.a.Principal.Class.and.Name"));
    } 
    while (stringTokenizer.hasMoreTokens()) {
      stringTokenizer.nextToken();
      str1 = stringTokenizer.nextToken();
      if (this.testing)
        System.out.println("    Principal Class = " + str1); 
      if (!stringTokenizer.hasMoreTokens()) {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
        Object[] arrayOfObject = { paramString };
        throw new IllegalArgumentException(messageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Class.not.followed.by.a.Principal.Name"));
      } 
      stringTokenizer.nextToken();
      str2 = stringTokenizer.nextToken();
      if (!str2.startsWith("\"")) {
        MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
        Object[] arrayOfObject = { paramString };
        throw new IllegalArgumentException(messageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Name.must.be.surrounded.by.quotes"));
      } 
      if (!str2.endsWith("\"")) {
        while (stringTokenizer.hasMoreTokens()) {
          str2 = str2 + stringTokenizer.nextToken();
          if (str2.endsWith("\""))
            break; 
        } 
        if (!str2.endsWith("\"")) {
          MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("permission.name.name.syntax.invalid."));
          Object[] arrayOfObject = { paramString };
          throw new IllegalArgumentException(messageFormat.format(arrayOfObject) + ResourcesMgr.getString("Principal.Name.missing.end.quote"));
        } 
      } 
      if (this.testing)
        System.out.println("\tprincipalName = '" + str2 + "'"); 
      str2 = str2.substring(1, str2.length() - 1);
      if (str1.equals("*") && !str2.equals("*"))
        throw new IllegalArgumentException(ResourcesMgr.getString("PrivateCredentialPermission.Principal.Class.can.not.be.a.wildcard.value.if.Principal.Name.is.not.a.wildcard.value")); 
      if (this.testing)
        System.out.println("\tprincipalName = '" + str2 + "'"); 
      arrayList.add(new CredOwner(str1, str2));
    } 
    this.credOwners = new CredOwner[arrayList.size()];
    arrayList.toArray(this.credOwners);
  }
  
  private boolean impliesCredentialClass(String paramString1, String paramString2) {
    if (paramString1 == null || paramString2 == null)
      return false; 
    if (this.testing)
      System.out.println("credential class comparison: " + paramString1 + "/" + paramString2); 
    return paramString1.equals("*") ? true : paramString1.equals(paramString2);
  }
  
  private boolean impliesPrincipalSet(CredOwner[] paramArrayOfCredOwner1, CredOwner[] paramArrayOfCredOwner2) {
    if (paramArrayOfCredOwner1 == null || paramArrayOfCredOwner2 == null)
      return false; 
    if (paramArrayOfCredOwner2.length == 0)
      return true; 
    if (paramArrayOfCredOwner1.length == 0)
      return false; 
    for (byte b = 0; b < paramArrayOfCredOwner1.length; b++) {
      boolean bool = false;
      for (byte b1 = 0; b1 < paramArrayOfCredOwner2.length; b1++) {
        if (paramArrayOfCredOwner1[b].implies(paramArrayOfCredOwner2[b1])) {
          bool = true;
          break;
        } 
      } 
      if (!bool)
        return false; 
    } 
    return true;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (getName().indexOf(" ") == -1 && getName().indexOf("\"") == -1) {
      this.credentialClass = getName();
      this.credOwners = EMPTY_PRINCIPALS;
    } else {
      init(getName());
    } 
  }
  
  static class CredOwner implements Serializable {
    private static final long serialVersionUID = -5607449830436408266L;
    
    String principalClass;
    
    String principalName;
    
    CredOwner(String param1String1, String param1String2) {
      this.principalClass = param1String1;
      this.principalName = param1String2;
    }
    
    public boolean implies(Object param1Object) {
      if (param1Object == null || !(param1Object instanceof CredOwner))
        return false; 
      CredOwner credOwner = (CredOwner)param1Object;
      return ((this.principalClass.equals("*") || this.principalClass.equals(credOwner.principalClass)) && (this.principalName.equals("*") || this.principalName.equals(credOwner.principalName)));
    }
    
    public String toString() {
      MessageFormat messageFormat = new MessageFormat(ResourcesMgr.getString("CredOwner.Principal.Class.class.Principal.Name.name"));
      Object[] arrayOfObject = { this.principalClass, this.principalName };
      return messageFormat.format(arrayOfObject);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\PrivateCredentialPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
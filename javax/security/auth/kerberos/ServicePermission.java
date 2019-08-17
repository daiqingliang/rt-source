package javax.security.auth.kerberos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

public final class ServicePermission extends Permission implements Serializable {
  private static final long serialVersionUID = -1227585031618624935L;
  
  private static final int INITIATE = 1;
  
  private static final int ACCEPT = 2;
  
  private static final int ALL = 3;
  
  private static final int NONE = 0;
  
  private int mask;
  
  private String actions;
  
  public ServicePermission(String paramString1, String paramString2) {
    super(paramString1);
    init(paramString1, getMask(paramString2));
  }
  
  private void init(String paramString, int paramInt) {
    if (paramString == null)
      throw new NullPointerException("service principal can't be null"); 
    if ((paramInt & 0x3) != paramInt)
      throw new IllegalArgumentException("invalid actions mask"); 
    this.mask = paramInt;
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof ServicePermission))
      return false; 
    ServicePermission servicePermission = (ServicePermission)paramPermission;
    return ((this.mask & servicePermission.mask) == servicePermission.mask && impliesIgnoreMask(servicePermission));
  }
  
  boolean impliesIgnoreMask(ServicePermission paramServicePermission) { return (getName().equals("*") || getName().equals(paramServicePermission.getName()) || (paramServicePermission.getName().startsWith("@") && getName().endsWith(paramServicePermission.getName()))); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof ServicePermission))
      return false; 
    ServicePermission servicePermission = (ServicePermission)paramObject;
    return ((this.mask & servicePermission.mask) == servicePermission.mask && getName().equals(servicePermission.getName()));
  }
  
  public int hashCode() { return getName().hashCode() ^ this.mask; }
  
  private static String getActions(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = false;
    if ((paramInt & true) == 1) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("initiate");
    } 
    if ((paramInt & 0x2) == 2) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("accept");
    } 
    return stringBuilder.toString();
  }
  
  public String getActions() {
    if (this.actions == null)
      this.actions = getActions(this.mask); 
    return this.actions;
  }
  
  public PermissionCollection newPermissionCollection() { return new KrbServicePermissionCollection(); }
  
  int getMask() { return this.mask; }
  
  private static int getMask(String paramString) {
    if (paramString == null)
      throw new NullPointerException("action can't be null"); 
    if (paramString.equals(""))
      throw new IllegalArgumentException("action can't be empty"); 
    byte b = 0;
    char[] arrayOfChar = paramString.toCharArray();
    if (arrayOfChar.length == 1 && arrayOfChar[0] == '-')
      return b; 
    for (int i = arrayOfChar.length - 1; i != -1; i -= j) {
      int j;
      char c;
      while (i != -1 && ((c = arrayOfChar[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
        i--; 
      if (i >= 7 && (arrayOfChar[i - 7] == 'i' || arrayOfChar[i - 7] == 'I') && (arrayOfChar[i - 6] == 'n' || arrayOfChar[i - 6] == 'N') && (arrayOfChar[i - 5] == 'i' || arrayOfChar[i - 5] == 'I') && (arrayOfChar[i - 4] == 't' || arrayOfChar[i - 4] == 'T') && (arrayOfChar[i - 3] == 'i' || arrayOfChar[i - 3] == 'I') && (arrayOfChar[i - 2] == 'a' || arrayOfChar[i - 2] == 'A') && (arrayOfChar[i - 1] == 't' || arrayOfChar[i - 1] == 'T') && (arrayOfChar[i] == 'e' || arrayOfChar[i] == 'E')) {
        j = 8;
        b |= 0x1;
      } else if (i >= 5 && (arrayOfChar[i - 5] == 'a' || arrayOfChar[i - 5] == 'A') && (arrayOfChar[i - 4] == 'c' || arrayOfChar[i - 4] == 'C') && (arrayOfChar[i - 3] == 'c' || arrayOfChar[i - 3] == 'C') && (arrayOfChar[i - 2] == 'e' || arrayOfChar[i - 2] == 'E') && (arrayOfChar[i - 1] == 'p' || arrayOfChar[i - 1] == 'P') && (arrayOfChar[i] == 't' || arrayOfChar[i] == 'T')) {
        j = 6;
        b |= 0x2;
      } else {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      } 
      boolean bool = false;
      while (i >= j && !bool) {
        switch (arrayOfChar[i - j]) {
          case ',':
            bool = true;
            break;
          case '\t':
          case '\n':
          case '\f':
          case '\r':
          case ' ':
            break;
          default:
            throw new IllegalArgumentException("invalid permission: " + paramString);
        } 
        i--;
      } 
    } 
    return b;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.actions == null)
      getActions(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getName(), getMask(this.actions));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\kerberos\ServicePermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
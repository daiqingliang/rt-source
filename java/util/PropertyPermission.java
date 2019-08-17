package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

public final class PropertyPermission extends BasicPermission {
  private static final int READ = 1;
  
  private static final int WRITE = 2;
  
  private static final int ALL = 3;
  
  private static final int NONE = 0;
  
  private int mask;
  
  private String actions;
  
  private static final long serialVersionUID = 885438825399942851L;
  
  private void init(int paramInt) {
    if ((paramInt & 0x3) != paramInt)
      throw new IllegalArgumentException("invalid actions mask"); 
    if (paramInt == 0)
      throw new IllegalArgumentException("invalid actions mask"); 
    if (getName() == null)
      throw new NullPointerException("name can't be null"); 
    this.mask = paramInt;
  }
  
  public PropertyPermission(String paramString1, String paramString2) {
    super(paramString1, paramString2);
    init(getMask(paramString2));
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof PropertyPermission))
      return false; 
    PropertyPermission propertyPermission = (PropertyPermission)paramPermission;
    return ((this.mask & propertyPermission.mask) == propertyPermission.mask && super.implies(propertyPermission));
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof PropertyPermission))
      return false; 
    PropertyPermission propertyPermission = (PropertyPermission)paramObject;
    return (this.mask == propertyPermission.mask && getName().equals(propertyPermission.getName()));
  }
  
  public int hashCode() { return getName().hashCode(); }
  
  private static int getMask(String paramString) {
    byte b = 0;
    if (paramString == null)
      return b; 
    if (paramString == "read")
      return 1; 
    if (paramString == "write")
      return 2; 
    if (paramString == "read,write")
      return 3; 
    char[] arrayOfChar = paramString.toCharArray();
    int i = arrayOfChar.length - 1;
    if (i < 0)
      return b; 
    while (i != -1) {
      int j;
      char c;
      while (i != -1 && ((c = arrayOfChar[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
        i--; 
      if (i >= 3 && (arrayOfChar[i - 3] == 'r' || arrayOfChar[i - 3] == 'R') && (arrayOfChar[i - 2] == 'e' || arrayOfChar[i - 2] == 'E') && (arrayOfChar[i - 1] == 'a' || arrayOfChar[i - 1] == 'A') && (arrayOfChar[i] == 'd' || arrayOfChar[i] == 'D')) {
        j = 4;
        b |= 0x1;
      } else if (i >= 4 && (arrayOfChar[i - 4] == 'w' || arrayOfChar[i - 4] == 'W') && (arrayOfChar[i - 3] == 'r' || arrayOfChar[i - 3] == 'R') && (arrayOfChar[i - 2] == 'i' || arrayOfChar[i - 2] == 'I') && (arrayOfChar[i - 1] == 't' || arrayOfChar[i - 1] == 'T') && (arrayOfChar[i] == 'e' || arrayOfChar[i] == 'E')) {
        j = 5;
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
      i -= j;
    } 
    return b;
  }
  
  static String getActions(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = false;
    if ((paramInt & true) == 1) {
      bool = true;
      stringBuilder.append("read");
    } 
    if ((paramInt & 0x2) == 2) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("write");
    } 
    return stringBuilder.toString();
  }
  
  public String getActions() {
    if (this.actions == null)
      this.actions = getActions(this.mask); 
    return this.actions;
  }
  
  int getMask() { return this.mask; }
  
  public PermissionCollection newPermissionCollection() { return new PropertyPermissionCollection(); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.actions == null)
      getActions(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getMask(this.actions));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\PropertyPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.management;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.StringTokenizer;

public class MBeanServerPermission extends BasicPermission {
  private static final long serialVersionUID = -5661980843569388590L;
  
  private static final int CREATE = 0;
  
  private static final int FIND = 1;
  
  private static final int NEW = 2;
  
  private static final int RELEASE = 3;
  
  private static final int N_NAMES = 4;
  
  private static final String[] names = { "createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer" };
  
  private static final int CREATE_MASK = 1;
  
  private static final int FIND_MASK = 2;
  
  private static final int NEW_MASK = 4;
  
  private static final int RELEASE_MASK = 8;
  
  private static final int ALL_MASK = 15;
  
  private static final String[] canonicalNames = new String[16];
  
  int mask;
  
  public MBeanServerPermission(String paramString) { this(paramString, null); }
  
  public MBeanServerPermission(String paramString1, String paramString2) {
    super(getCanonicalName(parseMask(paramString1)), paramString2);
    this.mask = parseMask(paramString1);
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("MBeanServerPermission actions must be null: " + paramString2); 
  }
  
  MBeanServerPermission(int paramInt) {
    super(getCanonicalName(paramInt));
    this.mask = impliedMask(paramInt);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.mask = parseMask(getName());
  }
  
  static int simplifyMask(int paramInt) {
    if ((paramInt & true) != 0)
      paramInt &= 0xFFFFFFFB; 
    return paramInt;
  }
  
  static int impliedMask(int paramInt) {
    if ((paramInt & true) != 0)
      paramInt |= 0x4; 
    return paramInt;
  }
  
  static String getCanonicalName(int paramInt) {
    if (paramInt == 15)
      return "*"; 
    paramInt = simplifyMask(paramInt);
    synchronized (canonicalNames) {
      if (canonicalNames[paramInt] == null)
        canonicalNames[paramInt] = makeCanonicalName(paramInt); 
    } 
    return canonicalNames[paramInt];
  }
  
  private static String makeCanonicalName(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b < 4; b++) {
      if ((paramInt & true << b) != 0) {
        if (stringBuilder.length() > 0)
          stringBuilder.append(','); 
        stringBuilder.append(names[b]);
      } 
    } 
    return stringBuilder.toString().intern();
  }
  
  private static int parseMask(String paramString) {
    if (paramString == null)
      throw new NullPointerException("MBeanServerPermission: target name can't be null"); 
    paramString = paramString.trim();
    if (paramString.equals("*"))
      return 15; 
    if (paramString.indexOf(',') < 0)
      return impliedMask(1 << nameIndex(paramString.trim())); 
    int i = 0;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      int j = nameIndex(str.trim());
      i |= 1 << j;
    } 
    return impliedMask(i);
  }
  
  private static int nameIndex(String paramString) {
    for (byte b = 0; b < 4; b++) {
      if (names[b].equals(paramString))
        return b; 
    } 
    String str = "Invalid MBeanServerPermission name: \"" + paramString + "\"";
    throw new IllegalArgumentException(str);
  }
  
  public int hashCode() { return this.mask; }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof MBeanServerPermission))
      return false; 
    MBeanServerPermission mBeanServerPermission = (MBeanServerPermission)paramPermission;
    return ((this.mask & mBeanServerPermission.mask) == mBeanServerPermission.mask);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof MBeanServerPermission))
      return false; 
    MBeanServerPermission mBeanServerPermission = (MBeanServerPermission)paramObject;
    return (this.mask == mBeanServerPermission.mask);
  }
  
  public PermissionCollection newPermissionCollection() { return new MBeanServerPermissionCollection(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\MBeanServerPermission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
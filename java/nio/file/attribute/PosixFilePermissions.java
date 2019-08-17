package java.nio.file.attribute;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class PosixFilePermissions {
  private static void writeBits(StringBuilder paramStringBuilder, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramBoolean1) {
      paramStringBuilder.append('r');
    } else {
      paramStringBuilder.append('-');
    } 
    if (paramBoolean2) {
      paramStringBuilder.append('w');
    } else {
      paramStringBuilder.append('-');
    } 
    if (paramBoolean3) {
      paramStringBuilder.append('x');
    } else {
      paramStringBuilder.append('-');
    } 
  }
  
  public static String toString(Set<PosixFilePermission> paramSet) {
    StringBuilder stringBuilder = new StringBuilder(9);
    writeBits(stringBuilder, paramSet.contains(PosixFilePermission.OWNER_READ), paramSet.contains(PosixFilePermission.OWNER_WRITE), paramSet.contains(PosixFilePermission.OWNER_EXECUTE));
    writeBits(stringBuilder, paramSet.contains(PosixFilePermission.GROUP_READ), paramSet.contains(PosixFilePermission.GROUP_WRITE), paramSet.contains(PosixFilePermission.GROUP_EXECUTE));
    writeBits(stringBuilder, paramSet.contains(PosixFilePermission.OTHERS_READ), paramSet.contains(PosixFilePermission.OTHERS_WRITE), paramSet.contains(PosixFilePermission.OTHERS_EXECUTE));
    return stringBuilder.toString();
  }
  
  private static boolean isSet(char paramChar1, char paramChar2) {
    if (paramChar1 == paramChar2)
      return true; 
    if (paramChar1 == '-')
      return false; 
    throw new IllegalArgumentException("Invalid mode");
  }
  
  private static boolean isR(char paramChar) { return isSet(paramChar, 'r'); }
  
  private static boolean isW(char paramChar) { return isSet(paramChar, 'w'); }
  
  private static boolean isX(char paramChar) { return isSet(paramChar, 'x'); }
  
  public static Set<PosixFilePermission> fromString(String paramString) {
    if (paramString.length() != 9)
      throw new IllegalArgumentException("Invalid mode"); 
    EnumSet enumSet = EnumSet.noneOf(PosixFilePermission.class);
    if (isR(paramString.charAt(0)))
      enumSet.add(PosixFilePermission.OWNER_READ); 
    if (isW(paramString.charAt(1)))
      enumSet.add(PosixFilePermission.OWNER_WRITE); 
    if (isX(paramString.charAt(2)))
      enumSet.add(PosixFilePermission.OWNER_EXECUTE); 
    if (isR(paramString.charAt(3)))
      enumSet.add(PosixFilePermission.GROUP_READ); 
    if (isW(paramString.charAt(4)))
      enumSet.add(PosixFilePermission.GROUP_WRITE); 
    if (isX(paramString.charAt(5)))
      enumSet.add(PosixFilePermission.GROUP_EXECUTE); 
    if (isR(paramString.charAt(6)))
      enumSet.add(PosixFilePermission.OTHERS_READ); 
    if (isW(paramString.charAt(7)))
      enumSet.add(PosixFilePermission.OTHERS_WRITE); 
    if (isX(paramString.charAt(8)))
      enumSet.add(PosixFilePermission.OTHERS_EXECUTE); 
    return enumSet;
  }
  
  public static FileAttribute<Set<PosixFilePermission>> asFileAttribute(Set<PosixFilePermission> paramSet) {
    paramSet = new HashSet<PosixFilePermission>(paramSet);
    for (PosixFilePermission posixFilePermission : paramSet) {
      if (posixFilePermission == null)
        throw new NullPointerException(); 
    } 
    final Set<PosixFilePermission> value = paramSet;
    return new FileAttribute<Set<PosixFilePermission>>() {
        public String name() { return "posix:permissions"; }
        
        public Set<PosixFilePermission> value() { return Collections.unmodifiableSet(value); }
      };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\PosixFilePermissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
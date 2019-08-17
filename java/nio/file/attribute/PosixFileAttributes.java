package java.nio.file.attribute;

import java.util.Set;

public interface PosixFileAttributes extends BasicFileAttributes {
  UserPrincipal owner();
  
  GroupPrincipal group();
  
  Set<PosixFilePermission> permissions();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\attribute\PosixFileAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class TempFileHelper {
  private static final Path tmpdir = Paths.get((String)AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")), new String[0]);
  
  private static final boolean isPosix = FileSystems.getDefault().supportedFileAttributeViews().contains("posix");
  
  private static final SecureRandom random = new SecureRandom();
  
  private static Path generatePath(String paramString1, String paramString2, Path paramPath) {
    long l = random.nextLong();
    l = (l == Float.MIN_VALUE) ? 0L : Math.abs(l);
    Path path = paramPath.getFileSystem().getPath(paramString1 + Long.toString(l) + paramString2, new String[0]);
    if (path.getParent() != null)
      throw new IllegalArgumentException("Invalid prefix or suffix"); 
    return paramPath.resolve(path);
  }
  
  private static Path create(Path paramPath, String paramString1, String paramString2, boolean paramBoolean, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException {
    if (paramString1 == null)
      paramString1 = ""; 
    if (paramString2 == null)
      paramString2 = paramBoolean ? "" : ".tmp"; 
    if (paramPath == null)
      paramPath = tmpdir; 
    if (isPosix && paramPath.getFileSystem() == FileSystems.getDefault())
      if (paramArrayOfFileAttribute.length == 0) {
        paramArrayOfFileAttribute = new FileAttribute[1];
        paramArrayOfFileAttribute[0] = paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions;
      } else {
        boolean bool = false;
        for (byte b = 0; b < paramArrayOfFileAttribute.length; b++) {
          if (paramArrayOfFileAttribute[b].name().equals("posix:permissions")) {
            bool = true;
            break;
          } 
        } 
        if (!bool) {
          FileAttribute[] arrayOfFileAttribute = new FileAttribute[paramArrayOfFileAttribute.length + 1];
          System.arraycopy(paramArrayOfFileAttribute, 0, arrayOfFileAttribute, 0, paramArrayOfFileAttribute.length);
          paramArrayOfFileAttribute = arrayOfFileAttribute;
          paramArrayOfFileAttribute[paramArrayOfFileAttribute.length - 1] = paramBoolean ? PosixPermissions.dirPermissions : PosixPermissions.filePermissions;
        } 
      }  
    SecurityManager securityManager = System.getSecurityManager();
    while (true) {
      Path path;
      try {
        path = generatePath(paramString1, paramString2, paramPath);
      } catch (InvalidPathException invalidPathException) {
        if (securityManager != null)
          throw new IllegalArgumentException("Invalid prefix or suffix"); 
        throw invalidPathException;
      } 
      try {
        return paramBoolean ? Files.createDirectory(path, paramArrayOfFileAttribute) : Files.createFile(path, paramArrayOfFileAttribute);
      } catch (SecurityException securityException) {
        if (paramPath == tmpdir && securityManager != null)
          throw new SecurityException("Unable to create temporary file or directory"); 
        throw securityException;
      } catch (FileAlreadyExistsException fileAlreadyExistsException) {}
    } 
  }
  
  static Path createTempFile(Path paramPath, String paramString1, String paramString2, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException { return create(paramPath, paramString1, paramString2, false, paramArrayOfFileAttribute); }
  
  static Path createTempDirectory(Path paramPath, String paramString, FileAttribute<?>[] paramArrayOfFileAttribute) throws IOException { return create(paramPath, paramString, null, true, paramArrayOfFileAttribute); }
  
  private static class PosixPermissions {
    static final FileAttribute<Set<PosixFilePermission>> filePermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
    
    static final FileAttribute<Set<PosixFilePermission>> dirPermissions = PosixFilePermissions.asFileAttribute(EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\TempFileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
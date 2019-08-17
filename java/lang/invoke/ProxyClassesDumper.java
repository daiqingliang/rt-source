package java.lang.invoke;

import java.io.FilePermission;
import java.lang.invoke.ProxyClassesDumper;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.Objects;
import sun.util.logging.PlatformLogger;

final class ProxyClassesDumper {
  private static final char[] HEX = { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private static final char[] BAD_CHARS = { '\\', ':', '*', '?', '"', '<', '>', '|' };
  
  private static final String[] REPLACEMENT = { "%5C", "%3A", "%2A", "%3F", "%22", "%3C", "%3E", "%7C" };
  
  private final Path dumpDir;
  
  public static ProxyClassesDumper getInstance(String paramString) {
    if (null == paramString)
      return null; 
    try {
      paramString = paramString.trim();
      final Path dir = Paths.get((paramString.length() == 0) ? "." : paramString, new String[0]);
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              ProxyClassesDumper.validateDumpDir(dir);
              return null;
            }
          },  null, new Permission[] { new FilePermission("<<ALL FILES>>", "read, write") });
      return new ProxyClassesDumper(path);
    } catch (InvalidPathException invalidPathException) {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Path " + paramString + " is not valid - dumping disabled", invalidPathException);
    } catch (IllegalArgumentException illegalArgumentException) {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning(illegalArgumentException.getMessage() + " - dumping disabled");
    } 
    return null;
  }
  
  private ProxyClassesDumper(Path paramPath) { this.dumpDir = (Path)Objects.requireNonNull(paramPath); }
  
  private static void validateDumpDir(Path paramPath) {
    if (!Files.exists(paramPath, new java.nio.file.LinkOption[0]))
      throw new IllegalArgumentException("Directory " + paramPath + " does not exist"); 
    if (!Files.isDirectory(paramPath, new java.nio.file.LinkOption[0]))
      throw new IllegalArgumentException("Path " + paramPath + " is not a directory"); 
    if (!Files.isWritable(paramPath))
      throw new IllegalArgumentException("Directory " + paramPath + " is not writable"); 
  }
  
  public static String encodeForFilename(String paramString) {
    int i = paramString.length();
    StringBuilder stringBuilder = new StringBuilder(i);
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c <= '\037') {
        stringBuilder.append('%');
        stringBuilder.append(HEX[c >> '\004' & 0xF]);
        stringBuilder.append(HEX[c & 0xF]);
      } else {
        byte b1;
        for (b1 = 0; b1 < BAD_CHARS.length; b1++) {
          if (c == BAD_CHARS[b1]) {
            stringBuilder.append(REPLACEMENT[b1]);
            break;
          } 
        } 
        if (b1 >= BAD_CHARS.length)
          stringBuilder.append(c); 
      } 
    } 
    return stringBuilder.toString();
  }
  
  public void dumpClass(String paramString, byte[] paramArrayOfByte) {
    Path path;
    try {
      path = this.dumpDir.resolve(encodeForFilename(paramString) + ".class");
    } catch (InvalidPathException invalidPathException) {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Invalid path for class " + paramString);
      return;
    } 
    try {
      Path path1 = path.getParent();
      Files.createDirectories(path1, new java.nio.file.attribute.FileAttribute[0]);
      Files.write(path, paramArrayOfByte, new java.nio.file.OpenOption[0]);
    } catch (Exception exception) {
      PlatformLogger.getLogger(ProxyClassesDumper.class.getName()).warning("Exception writing to path at " + path.toString());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\invoke\ProxyClassesDumper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
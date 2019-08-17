package java.nio.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

class CopyMoveHelper {
  private static CopyOption[] convertMoveToCopyOptions(CopyOption... paramVarArgs) throws AtomicMoveNotSupportedException {
    int i = paramVarArgs.length;
    CopyOption[] arrayOfCopyOption = new CopyOption[i + 2];
    for (byte b = 0; b < i; b++) {
      CopyOption copyOption = paramVarArgs[b];
      if (copyOption == StandardCopyOption.ATOMIC_MOVE)
        throw new AtomicMoveNotSupportedException(null, null, "Atomic move between providers is not supported"); 
      arrayOfCopyOption[b] = copyOption;
    } 
    arrayOfCopyOption[i] = LinkOption.NOFOLLOW_LINKS;
    arrayOfCopyOption[i + 1] = StandardCopyOption.COPY_ATTRIBUTES;
    return arrayOfCopyOption;
  }
  
  static void copyToForeignTarget(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    CopyOptions copyOptions = CopyOptions.parse(paramVarArgs);
    new LinkOption[1][0] = LinkOption.NOFOLLOW_LINKS;
    LinkOption[] arrayOfLinkOption = copyOptions.followLinks ? new LinkOption[0] : new LinkOption[1];
    BasicFileAttributes basicFileAttributes = Files.readAttributes(paramPath1, BasicFileAttributes.class, arrayOfLinkOption);
    if (basicFileAttributes.isSymbolicLink())
      throw new IOException("Copying of symbolic links not supported"); 
    if (copyOptions.replaceExisting) {
      Files.deleteIfExists(paramPath2);
    } else if (Files.exists(paramPath2, new LinkOption[0])) {
      throw new FileAlreadyExistsException(paramPath2.toString());
    } 
    if (basicFileAttributes.isDirectory()) {
      Files.createDirectory(paramPath2, new java.nio.file.attribute.FileAttribute[0]);
    } else {
      try (InputStream null = Files.newInputStream(paramPath1, new OpenOption[0])) {
        Files.copy(inputStream, paramPath2, new CopyOption[0]);
      } 
    } 
    if (copyOptions.copyAttributes) {
      BasicFileAttributeView basicFileAttributeView = (BasicFileAttributeView)Files.getFileAttributeView(paramPath2, BasicFileAttributeView.class, new LinkOption[0]);
      try {
        basicFileAttributeView.setTimes(basicFileAttributes.lastModifiedTime(), basicFileAttributes.lastAccessTime(), basicFileAttributes.creationTime());
      } catch (Throwable throwable) {
        try {
          Files.delete(paramPath2);
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } 
  }
  
  static void moveToForeignTarget(Path paramPath1, Path paramPath2, CopyOption... paramVarArgs) throws IOException {
    copyToForeignTarget(paramPath1, paramPath2, convertMoveToCopyOptions(paramVarArgs));
    Files.delete(paramPath1);
  }
  
  private static class CopyOptions {
    boolean replaceExisting = false;
    
    boolean copyAttributes = false;
    
    boolean followLinks = true;
    
    static CopyOptions parse(CopyOption... param1VarArgs) {
      CopyOptions copyOptions = new CopyOptions();
      for (CopyOption copyOption : param1VarArgs) {
        if (copyOption == StandardCopyOption.REPLACE_EXISTING) {
          copyOptions.replaceExisting = true;
        } else if (copyOption == LinkOption.NOFOLLOW_LINKS) {
          copyOptions.followLinks = false;
        } else if (copyOption == StandardCopyOption.COPY_ATTRIBUTES) {
          copyOptions.copyAttributes = true;
        } else {
          if (copyOption == null)
            throw new NullPointerException(); 
          throw new UnsupportedOperationException("'" + copyOption + "' is not a recognized copy option");
        } 
      } 
      return copyOptions;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\CopyMoveHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
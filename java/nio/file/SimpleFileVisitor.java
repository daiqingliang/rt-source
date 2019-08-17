package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class SimpleFileVisitor<T> extends Object implements FileVisitor<T> {
  public FileVisitResult preVisitDirectory(T paramT, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    Objects.requireNonNull(paramT);
    Objects.requireNonNull(paramBasicFileAttributes);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFile(T paramT, BasicFileAttributes paramBasicFileAttributes) throws IOException {
    Objects.requireNonNull(paramT);
    Objects.requireNonNull(paramBasicFileAttributes);
    return FileVisitResult.CONTINUE;
  }
  
  public FileVisitResult visitFileFailed(T paramT, IOException paramIOException) throws IOException {
    Objects.requireNonNull(paramT);
    throw paramIOException;
  }
  
  public FileVisitResult postVisitDirectory(T paramT, IOException paramIOException) throws IOException {
    Objects.requireNonNull(paramT);
    if (paramIOException != null)
      throw paramIOException; 
    return FileVisitResult.CONTINUE;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\SimpleFileVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
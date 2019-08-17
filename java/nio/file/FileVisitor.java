package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

public interface FileVisitor<T> {
  FileVisitResult preVisitDirectory(T paramT, BasicFileAttributes paramBasicFileAttributes) throws IOException;
  
  FileVisitResult visitFile(T paramT, BasicFileAttributes paramBasicFileAttributes) throws IOException;
  
  FileVisitResult visitFileFailed(T paramT, IOException paramIOException) throws IOException;
  
  FileVisitResult postVisitDirectory(T paramT, IOException paramIOException) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.nio.file;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public interface SecureDirectoryStream<T> extends DirectoryStream<T> {
  SecureDirectoryStream<T> newDirectoryStream(T paramT, LinkOption... paramVarArgs) throws IOException;
  
  SeekableByteChannel newByteChannel(T paramT, Set<? extends OpenOption> paramSet, FileAttribute<?>... paramVarArgs) throws IOException;
  
  void deleteFile(T paramT) throws IOException;
  
  void deleteDirectory(T paramT) throws IOException;
  
  void move(T paramT1, SecureDirectoryStream<T> paramSecureDirectoryStream, T paramT2) throws IOException;
  
  <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(Class<V> paramClass);
  
  <V extends java.nio.file.attribute.FileAttributeView> V getFileAttributeView(T paramT, Class<V> paramClass, LinkOption... paramVarArgs);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\SecureDirectoryStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
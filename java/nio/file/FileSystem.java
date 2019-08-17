package java.nio.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

public abstract class FileSystem implements Closeable {
  public abstract FileSystemProvider provider();
  
  public abstract void close();
  
  public abstract boolean isOpen();
  
  public abstract boolean isReadOnly();
  
  public abstract String getSeparator();
  
  public abstract Iterable<Path> getRootDirectories();
  
  public abstract Iterable<FileStore> getFileStores();
  
  public abstract Set<String> supportedFileAttributeViews();
  
  public abstract Path getPath(String paramString, String... paramVarArgs);
  
  public abstract PathMatcher getPathMatcher(String paramString);
  
  public abstract UserPrincipalLookupService getUserPrincipalLookupService();
  
  public abstract WatchService newWatchService() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileSystem.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.nio.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;

public interface Path extends Comparable<Path>, Iterable<Path>, Watchable {
  FileSystem getFileSystem();
  
  boolean isAbsolute();
  
  Path getRoot();
  
  Path getFileName();
  
  Path getParent();
  
  int getNameCount();
  
  Path getName(int paramInt);
  
  Path subpath(int paramInt1, int paramInt2);
  
  boolean startsWith(Path paramPath);
  
  boolean startsWith(String paramString);
  
  boolean endsWith(Path paramPath);
  
  boolean endsWith(String paramString);
  
  Path normalize();
  
  Path resolve(Path paramPath);
  
  Path resolve(String paramString);
  
  Path resolveSibling(Path paramPath);
  
  Path resolveSibling(String paramString);
  
  Path relativize(Path paramPath);
  
  URI toUri();
  
  Path toAbsolutePath();
  
  Path toRealPath(LinkOption... paramVarArgs) throws IOException;
  
  File toFile();
  
  WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>[] paramArrayOfKind, WatchEvent.Modifier... paramVarArgs) throws IOException;
  
  WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>... paramVarArgs) throws IOException;
  
  Iterator<Path> iterator();
  
  int compareTo(Path paramPath);
  
  boolean equals(Object paramObject);
  
  int hashCode();
  
  String toString();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\Path.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
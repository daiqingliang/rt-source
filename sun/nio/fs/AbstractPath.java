package sun.nio.fs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractPath implements Path {
  public final boolean startsWith(String paramString) { return startsWith(getFileSystem().getPath(paramString, new String[0])); }
  
  public final boolean endsWith(String paramString) { return endsWith(getFileSystem().getPath(paramString, new String[0])); }
  
  public final Path resolve(String paramString) { return resolve(getFileSystem().getPath(paramString, new String[0])); }
  
  public final Path resolveSibling(Path paramPath) {
    if (paramPath == null)
      throw new NullPointerException(); 
    Path path = getParent();
    return (path == null) ? paramPath : path.resolve(paramPath);
  }
  
  public final Path resolveSibling(String paramString) { return resolveSibling(getFileSystem().getPath(paramString, new String[0])); }
  
  public final Iterator<Path> iterator() { return new Iterator<Path>() {
        private int i = 0;
        
        public boolean hasNext() { return (this.i < AbstractPath.this.getNameCount()); }
        
        public Path next() {
          if (this.i < AbstractPath.this.getNameCount()) {
            Path path = AbstractPath.this.getName(this.i);
            this.i++;
            return path;
          } 
          throw new NoSuchElementException();
        }
        
        public void remove() { throw new UnsupportedOperationException(); }
      }; }
  
  public final File toFile() { return new File(toString()); }
  
  public final WatchKey register(WatchService paramWatchService, WatchEvent.Kind<?>... paramVarArgs) throws IOException { return register(paramWatchService, paramVarArgs, new WatchEvent.Modifier[0]); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
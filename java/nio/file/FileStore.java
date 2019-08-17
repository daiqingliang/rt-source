package java.nio.file;

import java.io.IOException;
import java.nio.file.attribute.FileAttributeView;

public abstract class FileStore {
  public abstract String name();
  
  public abstract String type();
  
  public abstract boolean isReadOnly();
  
  public abstract long getTotalSpace() throws IOException;
  
  public abstract long getUsableSpace() throws IOException;
  
  public abstract long getUnallocatedSpace() throws IOException;
  
  public abstract boolean supportsFileAttributeView(Class<? extends FileAttributeView> paramClass);
  
  public abstract boolean supportsFileAttributeView(String paramString);
  
  public abstract <V extends java.nio.file.attribute.FileStoreAttributeView> V getFileStoreAttributeView(Class<V> paramClass);
  
  public abstract Object getAttribute(String paramString) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
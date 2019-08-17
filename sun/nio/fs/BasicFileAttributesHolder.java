package sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;

public interface BasicFileAttributesHolder {
  BasicFileAttributes get();
  
  void invalidate();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\BasicFileAttributesHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
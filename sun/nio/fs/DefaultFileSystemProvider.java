package sun.nio.fs;

import java.nio.file.spi.FileSystemProvider;

public class DefaultFileSystemProvider {
  public static FileSystemProvider create() { return new WindowsFileSystemProvider(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\DefaultFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package sun.nio.fs;

import java.nio.file.spi.FileTypeDetector;

public class DefaultFileTypeDetector {
  public static FileTypeDetector create() { return new RegistryFileTypeDetector(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\DefaultFileTypeDetector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
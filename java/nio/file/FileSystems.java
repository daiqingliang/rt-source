package java.nio.file;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;
import sun.nio.fs.DefaultFileSystemProvider;

public final class FileSystems {
  public static FileSystem getDefault() { return DefaultFileSystemHolder.defaultFileSystem; }
  
  public static FileSystem getFileSystem(URI paramURI) {
    String str = paramURI.getScheme();
    for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
      if (str.equalsIgnoreCase(fileSystemProvider.getScheme()))
        return fileSystemProvider.getFileSystem(paramURI); 
    } 
    throw new ProviderNotFoundException("Provider \"" + str + "\" not found");
  }
  
  public static FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap) throws IOException { return newFileSystem(paramURI, paramMap, null); }
  
  public static FileSystem newFileSystem(URI paramURI, Map<String, ?> paramMap, ClassLoader paramClassLoader) throws IOException {
    String str = paramURI.getScheme();
    for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
      if (str.equalsIgnoreCase(fileSystemProvider.getScheme()))
        return fileSystemProvider.newFileSystem(paramURI, paramMap); 
    } 
    if (paramClassLoader != null) {
      ServiceLoader serviceLoader = ServiceLoader.load(FileSystemProvider.class, paramClassLoader);
      for (FileSystemProvider fileSystemProvider : serviceLoader) {
        if (str.equalsIgnoreCase(fileSystemProvider.getScheme()))
          return fileSystemProvider.newFileSystem(paramURI, paramMap); 
      } 
    } 
    throw new ProviderNotFoundException("Provider \"" + str + "\" not found");
  }
  
  public static FileSystem newFileSystem(Path paramPath, ClassLoader paramClassLoader) throws IOException {
    if (paramPath == null)
      throw new NullPointerException(); 
    Map map = Collections.emptyMap();
    for (FileSystemProvider fileSystemProvider : FileSystemProvider.installedProviders()) {
      try {
        return fileSystemProvider.newFileSystem(paramPath, map);
      } catch (UnsupportedOperationException unsupportedOperationException) {}
    } 
    if (paramClassLoader != null) {
      ServiceLoader serviceLoader = ServiceLoader.load(FileSystemProvider.class, paramClassLoader);
      for (FileSystemProvider fileSystemProvider : serviceLoader) {
        try {
          return fileSystemProvider.newFileSystem(paramPath, map);
        } catch (UnsupportedOperationException unsupportedOperationException) {}
      } 
    } 
    throw new ProviderNotFoundException("Provider not found");
  }
  
  private static class DefaultFileSystemHolder {
    static final FileSystem defaultFileSystem = defaultFileSystem();
    
    private static FileSystem defaultFileSystem() {
      FileSystemProvider fileSystemProvider = (FileSystemProvider)AccessController.doPrivileged(new PrivilegedAction<FileSystemProvider>() {
            public FileSystemProvider run() { return FileSystems.DefaultFileSystemHolder.getDefaultProvider(); }
          });
      return fileSystemProvider.getFileSystem(URI.create("file:///"));
    }
    
    private static FileSystemProvider getDefaultProvider() {
      FileSystemProvider fileSystemProvider = DefaultFileSystemProvider.create();
      String str = System.getProperty("java.nio.file.spi.DefaultFileSystemProvider");
      if (str != null)
        for (String str1 : str.split(",")) {
          try {
            Class clazz = Class.forName(str1, true, ClassLoader.getSystemClassLoader());
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] { FileSystemProvider.class });
            fileSystemProvider = (FileSystemProvider)constructor.newInstance(new Object[] { fileSystemProvider });
            if (!fileSystemProvider.getScheme().equals("file"))
              throw new Error("Default provider must use scheme 'file'"); 
          } catch (Exception exception) {
            throw new Error(exception);
          } 
        }  
      return fileSystemProvider;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\FileSystems.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.activation;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class FileTypeMap {
  private static FileTypeMap defaultMap = null;
  
  private static Map<ClassLoader, FileTypeMap> map = new WeakHashMap();
  
  public abstract String getContentType(File paramFile);
  
  public abstract String getContentType(String paramString);
  
  public static void setDefaultFileTypeMap(FileTypeMap paramFileTypeMap) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      try {
        securityManager.checkSetFactory();
      } catch (SecurityException securityException) {
        if (FileTypeMap.class.getClassLoader() == null || FileTypeMap.class.getClassLoader() != paramFileTypeMap.getClass().getClassLoader())
          throw securityException; 
      }  
    map.remove(SecuritySupport.getContextClassLoader());
    defaultMap = paramFileTypeMap;
  }
  
  public static FileTypeMap getDefaultFileTypeMap() {
    if (defaultMap != null)
      return defaultMap; 
    ClassLoader classLoader = SecuritySupport.getContextClassLoader();
    FileTypeMap fileTypeMap = (FileTypeMap)map.get(classLoader);
    if (fileTypeMap == null) {
      fileTypeMap = new MimetypesFileTypeMap();
      map.put(classLoader, fileTypeMap);
    } 
    return fileTypeMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\FileTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
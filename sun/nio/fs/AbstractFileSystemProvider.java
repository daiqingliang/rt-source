package sun.nio.fs;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

abstract class AbstractFileSystemProvider extends FileSystemProvider {
  private static String[] split(String paramString) {
    String[] arrayOfString = new String[2];
    int i = paramString.indexOf(':');
    if (i == -1) {
      arrayOfString[0] = "basic";
      arrayOfString[1] = paramString;
    } else {
      arrayOfString[0] = paramString.substring(0, i++);
      arrayOfString[1] = (i == paramString.length()) ? "" : paramString.substring(i);
    } 
    return arrayOfString;
  }
  
  abstract DynamicFileAttributeView getFileAttributeView(Path paramPath, String paramString, LinkOption... paramVarArgs);
  
  public final void setAttribute(Path paramPath, String paramString, Object paramObject, LinkOption... paramVarArgs) throws IOException {
    String[] arrayOfString = split(paramString);
    if (arrayOfString[0].length() == 0)
      throw new IllegalArgumentException(paramString); 
    DynamicFileAttributeView dynamicFileAttributeView = getFileAttributeView(paramPath, arrayOfString[0], paramVarArgs);
    if (dynamicFileAttributeView == null)
      throw new UnsupportedOperationException("View '" + arrayOfString[0] + "' not available"); 
    dynamicFileAttributeView.setAttribute(arrayOfString[1], paramObject);
  }
  
  public final Map<String, Object> readAttributes(Path paramPath, String paramString, LinkOption... paramVarArgs) throws IOException {
    String[] arrayOfString = split(paramString);
    if (arrayOfString[0].length() == 0)
      throw new IllegalArgumentException(paramString); 
    DynamicFileAttributeView dynamicFileAttributeView = getFileAttributeView(paramPath, arrayOfString[0], paramVarArgs);
    if (dynamicFileAttributeView == null)
      throw new UnsupportedOperationException("View '" + arrayOfString[0] + "' not available"); 
    return dynamicFileAttributeView.readAttributes(arrayOfString[1].split(","));
  }
  
  abstract boolean implDelete(Path paramPath, boolean paramBoolean) throws IOException;
  
  public final void delete(Path paramPath) throws IOException { implDelete(paramPath, true); }
  
  public final boolean deleteIfExists(Path paramPath) throws IOException { return implDelete(paramPath, false); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\nio\fs\AbstractFileSystemProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
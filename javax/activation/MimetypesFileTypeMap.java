package javax.activation;

import com.sun.activation.registries.LogSupport;
import com.sun.activation.registries.MimeTypeFile;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Vector;

public class MimetypesFileTypeMap extends FileTypeMap {
  private MimeTypeFile[] DB;
  
  private static final int PROG = 0;
  
  private static String defaultType = "application/octet-stream";
  
  public MimetypesFileTypeMap() {
    Vector vector = new Vector(5);
    MimeTypeFile mimeTypeFile = null;
    vector.addElement(null);
    LogSupport.log("MimetypesFileTypeMap: load HOME");
    try {
      String str = System.getProperty("user.home");
      if (str != null) {
        String str1 = str + File.separator + ".mime.types";
        mimeTypeFile = loadFile(str1);
        if (mimeTypeFile != null)
          vector.addElement(mimeTypeFile); 
      } 
    } catch (SecurityException securityException) {}
    LogSupport.log("MimetypesFileTypeMap: load SYS");
    try {
      String str = System.getProperty("java.home") + File.separator + "lib" + File.separator + "mime.types";
      mimeTypeFile = loadFile(str);
      if (mimeTypeFile != null)
        vector.addElement(mimeTypeFile); 
    } catch (SecurityException securityException) {}
    LogSupport.log("MimetypesFileTypeMap: load JAR");
    loadAllResources(vector, "META-INF/mime.types");
    LogSupport.log("MimetypesFileTypeMap: load DEF");
    mimeTypeFile = loadResource("/META-INF/mimetypes.default");
    if (mimeTypeFile != null)
      vector.addElement(mimeTypeFile); 
    this.DB = new MimeTypeFile[vector.size()];
    vector.copyInto(this.DB);
  }
  
  private MimeTypeFile loadResource(String paramString) {
    inputStream = null;
    try {
      inputStream = SecuritySupport.getResourceAsStream(getClass(), paramString);
      if (inputStream != null) {
        MimeTypeFile mimeTypeFile = new MimeTypeFile(inputStream);
        if (LogSupport.isLoggable())
          LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types file: " + paramString); 
        return mimeTypeFile;
      } 
      if (LogSupport.isLoggable())
        LogSupport.log("MimetypesFileTypeMap: not loading mime types file: " + paramString); 
    } catch (IOException iOException) {
      if (LogSupport.isLoggable())
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, iOException); 
    } catch (SecurityException securityException) {
      if (LogSupport.isLoggable())
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, securityException); 
    } finally {
      try {
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {}
    } 
    return null;
  }
  
  private void loadAllResources(Vector paramVector, String paramString) {
    boolean bool = false;
    try {
      URL[] arrayOfURL;
      ClassLoader classLoader = null;
      classLoader = SecuritySupport.getContextClassLoader();
      if (classLoader == null)
        classLoader = getClass().getClassLoader(); 
      if (classLoader != null) {
        arrayOfURL = SecuritySupport.getResources(classLoader, paramString);
      } else {
        arrayOfURL = SecuritySupport.getSystemResources(paramString);
      } 
      if (arrayOfURL != null) {
        if (LogSupport.isLoggable())
          LogSupport.log("MimetypesFileTypeMap: getResources"); 
        for (byte b = 0; b < arrayOfURL.length; b++) {
          URL uRL = arrayOfURL[b];
          inputStream = null;
          if (LogSupport.isLoggable())
            LogSupport.log("MimetypesFileTypeMap: URL " + uRL); 
          try {
            inputStream = SecuritySupport.openStream(uRL);
            if (inputStream != null) {
              paramVector.addElement(new MimeTypeFile(inputStream));
              bool = true;
              if (LogSupport.isLoggable())
                LogSupport.log("MimetypesFileTypeMap: successfully loaded mime types from URL: " + uRL); 
            } else if (LogSupport.isLoggable()) {
              LogSupport.log("MimetypesFileTypeMap: not loading mime types from URL: " + uRL);
            } 
          } catch (IOException iOException) {
            if (LogSupport.isLoggable())
              LogSupport.log("MimetypesFileTypeMap: can't load " + uRL, iOException); 
          } catch (SecurityException securityException) {
            if (LogSupport.isLoggable())
              LogSupport.log("MimetypesFileTypeMap: can't load " + uRL, securityException); 
          } finally {
            try {
              if (inputStream != null)
                inputStream.close(); 
            } catch (IOException iOException) {}
          } 
        } 
      } 
    } catch (Exception exception) {
      if (LogSupport.isLoggable())
        LogSupport.log("MimetypesFileTypeMap: can't load " + paramString, exception); 
    } 
    if (!bool) {
      LogSupport.log("MimetypesFileTypeMap: !anyLoaded");
      MimeTypeFile mimeTypeFile = loadResource("/" + paramString);
      if (mimeTypeFile != null)
        paramVector.addElement(mimeTypeFile); 
    } 
  }
  
  private MimeTypeFile loadFile(String paramString) {
    MimeTypeFile mimeTypeFile = null;
    try {
      mimeTypeFile = new MimeTypeFile(paramString);
    } catch (IOException iOException) {}
    return mimeTypeFile;
  }
  
  public MimetypesFileTypeMap(String paramString) throws IOException {
    this();
    this.DB[0] = new MimeTypeFile(paramString);
  }
  
  public MimetypesFileTypeMap(InputStream paramInputStream) {
    this();
    try {
      this.DB[0] = new MimeTypeFile(paramInputStream);
    } catch (IOException iOException) {}
  }
  
  public void addMimeTypes(String paramString) throws IOException {
    if (this.DB[false] == null)
      this.DB[0] = new MimeTypeFile(); 
    this.DB[0].appendToRegistry(paramString);
  }
  
  public String getContentType(File paramFile) { return getContentType(paramFile.getName()); }
  
  public String getContentType(String paramString) {
    int i = paramString.lastIndexOf(".");
    if (i < 0)
      return defaultType; 
    String str = paramString.substring(i + 1);
    if (str.length() == 0)
      return defaultType; 
    for (byte b = 0; b < this.DB.length; b++) {
      if (this.DB[b] != null) {
        String str1 = this.DB[b].getMIMETypeString(str);
        if (str1 != null)
          return str1; 
      } 
    } 
    return defaultType;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\activation\MimetypesFileTypeMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
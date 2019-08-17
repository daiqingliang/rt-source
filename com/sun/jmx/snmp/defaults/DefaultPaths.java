package com.sun.jmx.snmp.defaults;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DefaultPaths {
  private static final String INSTALL_PATH_RESOURCE_NAME = "com/sun/jdmk/defaults/install.path";
  
  private static String etcDir;
  
  private static String tmpDir;
  
  private static String installDir;
  
  public static String getInstallDir() { return (installDir == null) ? useRessourceFile() : installDir; }
  
  public static String getInstallDir(String paramString) { return (installDir == null) ? ((paramString == null) ? getInstallDir() : (getInstallDir() + File.separator + paramString)) : ((paramString == null) ? installDir : (installDir + File.separator + paramString)); }
  
  public static void setInstallDir(String paramString) { installDir = paramString; }
  
  public static String getEtcDir() { return (etcDir == null) ? getInstallDir("etc") : etcDir; }
  
  public static String getEtcDir(String paramString) { return (etcDir == null) ? ((paramString == null) ? getEtcDir() : (getEtcDir() + File.separator + paramString)) : ((paramString == null) ? etcDir : (etcDir + File.separator + paramString)); }
  
  public static void setEtcDir(String paramString) { etcDir = paramString; }
  
  public static String getTmpDir() { return (tmpDir == null) ? getInstallDir("tmp") : tmpDir; }
  
  public static String getTmpDir(String paramString) { return (tmpDir == null) ? ((paramString == null) ? getTmpDir() : (getTmpDir() + File.separator + paramString)) : ((paramString == null) ? tmpDir : (tmpDir + File.separator + paramString)); }
  
  public static void setTmpDir(String paramString) { tmpDir = paramString; }
  
  private static String useRessourceFile() {
    inputStream = null;
    bufferedReader = null;
    try {
      inputStream = DefaultPaths.class.getClassLoader().getResourceAsStream("com/sun/jdmk/defaults/install.path");
      if (inputStream == null)
        return null; 
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
      installDir = bufferedReader.readLine();
    } catch (Exception exception) {
      try {
        if (inputStream != null)
          inputStream.close(); 
        if (bufferedReader != null)
          bufferedReader.close(); 
      } catch (Exception exception) {}
    } finally {
      try {
        if (inputStream != null)
          inputStream.close(); 
        if (bufferedReader != null)
          bufferedReader.close(); 
      } catch (Exception exception) {}
    } 
    return installDir;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jmx\snmp\defaults\DefaultPaths.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
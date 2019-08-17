package sun.net.www;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.util.StringTokenizer;
import sun.security.action.GetPropertyAction;

class MimeLauncher extends Thread {
  URLConnection uc;
  
  MimeEntry m;
  
  String genericTempFileTemplate;
  
  InputStream is;
  
  String execPath;
  
  MimeLauncher(MimeEntry paramMimeEntry, URLConnection paramURLConnection, InputStream paramInputStream, String paramString1, String paramString2) throws ApplicationLaunchException {
    super(paramString2);
    this.m = paramMimeEntry;
    this.uc = paramURLConnection;
    this.is = paramInputStream;
    this.genericTempFileTemplate = paramString1;
    String str = this.m.getLaunchString();
    if (!findExecutablePath(str)) {
      String str1;
      int i = str.indexOf(' ');
      if (i != -1) {
        str1 = str.substring(0, i);
      } else {
        str1 = str;
      } 
      throw new ApplicationLaunchException(str1);
    } 
  }
  
  protected String getTempFileName(URL paramURL, String paramString) {
    null = paramString;
    int i = null.lastIndexOf("%s");
    String str1 = null.substring(0, i);
    String str2 = "";
    if (i < null.length() - 2)
      str2 = null.substring(i + 2); 
    long l = System.currentTimeMillis() / 1000L;
    int j = 0;
    while ((j = str1.indexOf("%s")) >= 0)
      str1 = str1.substring(0, j) + l + str1.substring(j + 2); 
    String str3 = paramURL.getFile();
    String str4 = "";
    int k = str3.lastIndexOf('.');
    if (k >= 0 && k > str3.lastIndexOf('/'))
      str4 = str3.substring(k); 
    str3 = "HJ" + paramURL.hashCode();
    return str1 + str3 + l + str4 + str2;
  }
  
  public void run() {
    try {
      String str1 = this.m.getTempFileTemplate();
      if (str1 == null)
        str1 = this.genericTempFileTemplate; 
      str1 = getTempFileName(this.uc.getURL(), str1);
      try {
        fileOutputStream = new FileOutputStream(str1);
        byte[] arrayOfByte = new byte[2048];
        int j = 0;
        try {
          while ((j = this.is.read(arrayOfByte)) >= 0)
            fileOutputStream.write(arrayOfByte, 0, j); 
        } catch (IOException iOException) {
        
        } finally {
          fileOutputStream.close();
          this.is.close();
        } 
      } catch (IOException iOException) {}
      int i = 0;
      String str2;
      for (str2 = this.execPath; (i = str2.indexOf("%t")) >= 0; str2 = str2.substring(0, i) + this.uc.getContentType() + str2.substring(i + 2));
      boolean bool;
      for (bool = false; (i = str2.indexOf("%s")) >= 0; bool = true)
        str2 = str2.substring(0, i) + str1 + str2.substring(i + 2); 
      if (!bool)
        str2 = str2 + " <" + str1; 
      Runtime.getRuntime().exec(str2);
    } catch (IOException iOException) {}
  }
  
  private boolean findExecutablePath(String paramString) {
    String str1;
    if (paramString == null || paramString.length() == 0)
      return false; 
    int i = paramString.indexOf(' ');
    if (i != -1) {
      str1 = paramString.substring(0, i);
    } else {
      str1 = paramString;
    } 
    File file = new File(str1);
    if (file.isFile()) {
      this.execPath = paramString;
      return true;
    } 
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("exec.path"));
    if (str2 == null)
      return false; 
    StringTokenizer stringTokenizer = new StringTokenizer(str2, "|");
    while (stringTokenizer.hasMoreElements()) {
      String str3 = (String)stringTokenizer.nextElement();
      String str4 = str3 + File.separator + str1;
      file = new File(str4);
      if (file.isFile()) {
        this.execPath = str3 + File.separator + paramString;
        return true;
      } 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\MimeLauncher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
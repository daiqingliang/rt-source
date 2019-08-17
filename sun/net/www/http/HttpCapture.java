package sun.net.www.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import sun.net.NetProperties;
import sun.util.logging.PlatformLogger;

public class HttpCapture {
  private File file = null;
  
  private boolean incoming = true;
  
  private BufferedWriter out = null;
  
  private static boolean initialized = false;
  
  private static void init() {
    initialized = true;
    String str = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
          public String run() { return NetProperties.get("sun.net.http.captureRules"); }
        });
    if (str != null && !str.isEmpty()) {
      try {
        bufferedReader = new BufferedReader(new FileReader(str));
      } catch (FileNotFoundException fileNotFoundException) {
        return;
      } 
      try {
        for (str1 = bufferedReader.readLine(); str1 != null; str1 = bufferedReader.readLine()) {
          str1 = str1.trim();
          if (!str1.startsWith("#")) {
            String[] arrayOfString = str1.split(",");
            if (arrayOfString.length == 2) {
              if (patterns == null) {
                patterns = new ArrayList();
                capFiles = new ArrayList();
              } 
              patterns.add(Pattern.compile(arrayOfString[0].trim()));
              capFiles.add(arrayOfString[1].trim());
            } 
          } 
        } 
      } catch (IOException iOException) {
        try {
          bufferedReader.close();
        } catch (IOException iOException) {}
      } finally {
        try {
          bufferedReader.close();
        } catch (IOException iOException) {}
      } 
    } 
  }
  
  private static boolean isInitialized() { return initialized; }
  
  private HttpCapture(File paramFile, URL paramURL) {
    this.file = paramFile;
    try {
      this.out = new BufferedWriter(new FileWriter(this.file, true));
      this.out.write("URL: " + paramURL + "\n");
    } catch (IOException iOException) {
      PlatformLogger.getLogger(HttpCapture.class.getName()).severe(null, iOException);
    } 
  }
  
  public void sent(int paramInt) throws IOException {
    if (this.incoming) {
      this.out.write("\n------>\n");
      this.incoming = false;
      this.out.flush();
    } 
    this.out.write(paramInt);
  }
  
  public void received(int paramInt) throws IOException {
    if (!this.incoming) {
      this.out.write("\n<------\n");
      this.incoming = true;
      this.out.flush();
    } 
    this.out.write(paramInt);
  }
  
  public void flush() { this.out.flush(); }
  
  public static HttpCapture getCapture(URL paramURL) {
    if (!isInitialized())
      init(); 
    if (patterns == null || patterns.isEmpty())
      return null; 
    String str = paramURL.toString();
    for (byte b = 0; b < patterns.size(); b++) {
      Pattern pattern = (Pattern)patterns.get(b);
      if (pattern.matcher(str).find()) {
        File file1;
        String str1 = (String)capFiles.get(b);
        if (str1.indexOf("%d") >= 0) {
          Random random = new Random();
          do {
            String str2 = str1.replace("%d", Integer.toString(random.nextInt()));
            file1 = new File(str2);
          } while (file1.exists());
        } else {
          file1 = new File(str1);
        } 
        return new HttpCapture(file1, paramURL);
      } 
    } 
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\http\HttpCapture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
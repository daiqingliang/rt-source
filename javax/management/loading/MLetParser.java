package javax.management.loading;

import com.sun.jmx.defaults.JmxProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class MLetParser {
  private int c;
  
  private static String tag = "mlet";
  
  public void skipSpace(Reader paramReader) throws IOException {
    while (this.c >= 0 && (this.c == 32 || this.c == 9 || this.c == 10 || this.c == 13))
      this.c = paramReader.read(); 
  }
  
  public String scanIdentifier(Reader paramReader) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    while ((this.c >= 97 && this.c <= 122) || (this.c >= 65 && this.c <= 90) || (this.c >= 48 && this.c <= 57) || this.c == 95) {
      stringBuilder.append((char)this.c);
      this.c = paramReader.read();
    } 
    return stringBuilder.toString();
  }
  
  public Map<String, String> scanTag(Reader paramReader) throws IOException {
    HashMap hashMap = new HashMap();
    skipSpace(paramReader);
    while (this.c >= 0 && this.c != 62) {
      if (this.c == 60)
        throw new IOException("Missing '>' in tag"); 
      String str1 = scanIdentifier(paramReader);
      String str2 = "";
      skipSpace(paramReader);
      if (this.c == 61) {
        int i = -1;
        this.c = paramReader.read();
        skipSpace(paramReader);
        if (this.c == 39 || this.c == 34) {
          i = this.c;
          this.c = paramReader.read();
        } 
        StringBuilder stringBuilder = new StringBuilder();
        while (this.c > 0 && ((i < 0 && this.c != 32 && this.c != 9 && this.c != 10 && this.c != 13 && this.c != 62) || (i >= 0 && this.c != i))) {
          stringBuilder.append((char)this.c);
          this.c = paramReader.read();
        } 
        if (this.c == i)
          this.c = paramReader.read(); 
        skipSpace(paramReader);
        str2 = stringBuilder.toString();
      } 
      hashMap.put(str1.toLowerCase(), str2);
      skipSpace(paramReader);
    } 
    return hashMap;
  }
  
  public List<MLetContent> parse(URL paramURL) throws IOException {
    String str1 = "parse";
    String str2 = "<arg type=... value=...> tag requires type parameter.";
    String str3 = "<arg type=... value=...> tag requires value parameter.";
    String str4 = "<arg> tag outside <mlet> ... </mlet>.";
    String str5 = "<mlet> tag requires either code or object parameter.";
    String str6 = "<mlet> tag requires archive parameter.";
    URLConnection uRLConnection = paramURL.openConnection();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uRLConnection.getInputStream(), "UTF-8"));
    paramURL = uRLConnection.getURL();
    ArrayList arrayList1 = new ArrayList();
    Map map = null;
    ArrayList arrayList2 = new ArrayList();
    ArrayList arrayList3 = new ArrayList();
    while (true) {
      this.c = bufferedReader.read();
      if (this.c == -1)
        break; 
      if (this.c == 60) {
        this.c = bufferedReader.read();
        if (this.c == 47) {
          this.c = bufferedReader.read();
          String str7 = scanIdentifier(bufferedReader);
          if (this.c != 62)
            throw new IOException("Missing '>' in tag"); 
          if (str7.equalsIgnoreCase(tag)) {
            if (map != null)
              arrayList1.add(new MLetContent(paramURL, map, arrayList2, arrayList3)); 
            map = null;
            arrayList2 = new ArrayList();
            arrayList3 = new ArrayList();
          } 
          continue;
        } 
        String str = scanIdentifier(bufferedReader);
        if (str.equalsIgnoreCase("arg")) {
          Map map1 = scanTag(bufferedReader);
          String str7 = (String)map1.get("type");
          if (str7 == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str2);
            throw new IOException(str2);
          } 
          if (map != null) {
            arrayList2.add(str7);
          } else {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str4);
            throw new IOException(str4);
          } 
          String str8 = (String)map1.get("value");
          if (str8 == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str3);
            throw new IOException(str3);
          } 
          if (map != null) {
            arrayList3.add(str8);
            continue;
          } 
          JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str4);
          throw new IOException(str4);
        } 
        if (str.equalsIgnoreCase(tag)) {
          map = scanTag(bufferedReader);
          if (map.get("code") == null && map.get("object") == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str5);
            throw new IOException(str5);
          } 
          if (map.get("archive") == null) {
            JmxProperties.MLET_LOGGER.logp(Level.FINER, MLetParser.class.getName(), str1, str6);
            throw new IOException(str6);
          } 
        } 
      } 
    } 
    bufferedReader.close();
    return arrayList1;
  }
  
  public List<MLetContent> parseURL(String paramString) throws IOException {
    URL uRL;
    if (paramString.indexOf(':') <= 1) {
      String str2;
      String str1 = System.getProperty("user.dir");
      if (str1.charAt(0) == '/' || str1.charAt(0) == File.separatorChar) {
        str2 = "file:";
      } else {
        str2 = "file:/";
      } 
      uRL = new URL(str2 + str1.replace(File.separatorChar, '/') + "/");
      uRL = new URL(uRL, paramString);
    } else {
      uRL = new URL(paramString);
    } 
    return parse(uRL);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\MLetParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
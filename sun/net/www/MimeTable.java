package sun.net.www;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.FileNameMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

public class MimeTable implements FileNameMap {
  private Hashtable<String, MimeEntry> entries = new Hashtable();
  
  private Hashtable<String, MimeEntry> extensionMap = new Hashtable();
  
  private static String tempFileTemplate;
  
  private static final String filePreamble = "sun.net.www MIME content-types table";
  
  private static final String fileMagic = "#sun.net.www MIME content-types table";
  
  protected static String[] mailcapLocations;
  
  MimeTable() { load(); }
  
  public static MimeTable getDefaultTable() { return DefaultInstanceHolder.defaultInstance; }
  
  public static FileNameMap loadTable() { return getDefaultTable(); }
  
  public int getSize() { return this.entries.size(); }
  
  public String getContentTypeFor(String paramString) {
    MimeEntry mimeEntry = findByFileName(paramString);
    return (mimeEntry != null) ? mimeEntry.getType() : null;
  }
  
  public void add(MimeEntry paramMimeEntry) {
    this.entries.put(paramMimeEntry.getType(), paramMimeEntry);
    String[] arrayOfString = paramMimeEntry.getExtensions();
    if (arrayOfString == null)
      return; 
    for (byte b = 0; b < arrayOfString.length; b++)
      this.extensionMap.put(arrayOfString[b], paramMimeEntry); 
  }
  
  public MimeEntry remove(String paramString) {
    MimeEntry mimeEntry = (MimeEntry)this.entries.get(paramString);
    return remove(mimeEntry);
  }
  
  public MimeEntry remove(MimeEntry paramMimeEntry) {
    String[] arrayOfString = paramMimeEntry.getExtensions();
    if (arrayOfString != null)
      for (byte b = 0; b < arrayOfString.length; b++)
        this.extensionMap.remove(arrayOfString[b]);  
    return (MimeEntry)this.entries.remove(paramMimeEntry.getType());
  }
  
  public MimeEntry find(String paramString) {
    MimeEntry mimeEntry = (MimeEntry)this.entries.get(paramString);
    if (mimeEntry == null) {
      Enumeration enumeration = this.entries.elements();
      while (enumeration.hasMoreElements()) {
        MimeEntry mimeEntry1 = (MimeEntry)enumeration.nextElement();
        if (mimeEntry1.matches(paramString))
          return mimeEntry1; 
      } 
    } 
    return mimeEntry;
  }
  
  public MimeEntry findByFileName(String paramString) {
    String str = "";
    int i = paramString.lastIndexOf('#');
    if (i > 0)
      paramString = paramString.substring(0, i - 1); 
    i = paramString.lastIndexOf('.');
    i = Math.max(i, paramString.lastIndexOf('/'));
    i = Math.max(i, paramString.lastIndexOf('?'));
    if (i != -1 && paramString.charAt(i) == '.')
      str = paramString.substring(i).toLowerCase(); 
    return findByExt(str);
  }
  
  public MimeEntry findByExt(String paramString) { return (MimeEntry)this.extensionMap.get(paramString); }
  
  public MimeEntry findByDescription(String paramString) {
    Enumeration enumeration = elements();
    while (enumeration.hasMoreElements()) {
      MimeEntry mimeEntry = (MimeEntry)enumeration.nextElement();
      if (paramString.equals(mimeEntry.getDescription()))
        return mimeEntry; 
    } 
    return find(paramString);
  }
  
  String getTempFileTemplate() { return tempFileTemplate; }
  
  public Enumeration<MimeEntry> elements() { return this.entries.elements(); }
  
  public void load() {
    Properties properties = new Properties();
    File file = null;
    try {
      String str = System.getProperty("content.types.user.table");
      if (str != null) {
        file = new File(str);
        if (!file.exists())
          file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties"); 
      } else {
        file = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "content-types.properties");
      } 
      BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
      properties.load(bufferedInputStream);
      bufferedInputStream.close();
    } catch (IOException iOException) {
      System.err.println("Warning: default mime table not found: " + file.getPath());
      return;
    } 
    parse(properties);
  }
  
  void parse(Properties paramProperties) {
    String str = (String)paramProperties.get("temp.file.template");
    if (str != null) {
      paramProperties.remove("temp.file.template");
      tempFileTemplate = str;
    } 
    Enumeration enumeration = paramProperties.propertyNames();
    while (enumeration.hasMoreElements()) {
      String str1 = (String)enumeration.nextElement();
      String str2 = paramProperties.getProperty(str1);
      parse(str1, str2);
    } 
  }
  
  void parse(String paramString1, String paramString2) {
    MimeEntry mimeEntry = new MimeEntry(paramString1);
    StringTokenizer stringTokenizer = new StringTokenizer(paramString2, ";");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      parse(str, mimeEntry);
    } 
    add(mimeEntry);
  }
  
  void parse(String paramString, MimeEntry paramMimeEntry) {
    String str1 = null;
    String str2 = null;
    boolean bool = false;
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "=");
    while (stringTokenizer.hasMoreTokens()) {
      if (bool) {
        str2 = stringTokenizer.nextToken().trim();
        continue;
      } 
      str1 = stringTokenizer.nextToken().trim();
      bool = true;
    } 
    fill(paramMimeEntry, str1, str2);
  }
  
  void fill(MimeEntry paramMimeEntry, String paramString1, String paramString2) {
    if ("description".equalsIgnoreCase(paramString1)) {
      paramMimeEntry.setDescription(paramString2);
    } else if ("action".equalsIgnoreCase(paramString1)) {
      paramMimeEntry.setAction(getActionCode(paramString2));
    } else if ("application".equalsIgnoreCase(paramString1)) {
      paramMimeEntry.setCommand(paramString2);
    } else if ("icon".equalsIgnoreCase(paramString1)) {
      paramMimeEntry.setImageFileName(paramString2);
    } else if ("file_extensions".equalsIgnoreCase(paramString1)) {
      paramMimeEntry.setExtensions(paramString2);
    } 
  }
  
  String[] getExtensions(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    int i = stringTokenizer.countTokens();
    String[] arrayOfString = new String[i];
    for (byte b = 0; b < i; b++)
      arrayOfString[b] = stringTokenizer.nextToken(); 
    return arrayOfString;
  }
  
  int getActionCode(String paramString) {
    for (byte b = 0; b < MimeEntry.actionKeywords.length; b++) {
      if (paramString.equalsIgnoreCase(MimeEntry.actionKeywords[b]))
        return b; 
    } 
    return 0;
  }
  
  public boolean save(String paramString) {
    if (paramString == null)
      paramString = System.getProperty("user.home" + File.separator + "lib" + File.separator + "content-types.properties"); 
    return saveAsProperties(new File(paramString));
  }
  
  public Properties getAsProperties() {
    Properties properties = new Properties();
    Enumeration enumeration = elements();
    while (enumeration.hasMoreElements()) {
      MimeEntry mimeEntry = (MimeEntry)enumeration.nextElement();
      properties.put(mimeEntry.getType(), mimeEntry.toProperty());
    } 
    return properties;
  }
  
  protected boolean saveAsProperties(File paramFile) {
    fileOutputStream = null;
    try {
      fileOutputStream = new FileOutputStream(paramFile);
      properties = getAsProperties();
      properties.put("temp.file.template", tempFileTemplate);
      String str = System.getProperty("user.name");
      if (str != null) {
        String str1 = "; customized for " + str;
        properties.store(fileOutputStream, "sun.net.www MIME content-types table" + str1);
      } else {
        properties.store(fileOutputStream, "sun.net.www MIME content-types table");
      } 
    } catch (IOException iOException) {
      iOException.printStackTrace();
      return false;
    } finally {
      if (fileOutputStream != null)
        try {
          fileOutputStream.close();
        } catch (IOException iOException) {} 
    } 
    return true;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            tempFileTemplate = System.getProperty("content.types.temp.file.template", "/tmp/%s");
            MimeTable.mailcapLocations = new String[] { System.getProperty("user.mailcap"), System.getProperty("user.home") + "/.mailcap", "/etc/mailcap", "/usr/etc/mailcap", "/usr/local/etc/mailcap", System.getProperty("hotjava.home", "/usr/local/hotjava") + "/lib/mailcap" };
            return null;
          }
        });
  }
  
  private static class DefaultInstanceHolder {
    static final MimeTable defaultInstance = getDefaultInstance();
    
    static MimeTable getDefaultInstance() { return (MimeTable)AccessController.doPrivileged(new PrivilegedAction<MimeTable>() {
            public MimeTable run() {
              MimeTable mimeTable = new MimeTable();
              URLConnection.setFileNameMap(mimeTable);
              return mimeTable;
            }
          }); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\MimeTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
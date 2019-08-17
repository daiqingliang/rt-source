package sun.net.www;

import java.io.File;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class MimeEntry implements Cloneable {
  private String typeName;
  
  private String tempFileNameTemplate;
  
  private int action;
  
  private String command;
  
  private String description;
  
  private String imageFileName;
  
  private String[] fileExtensions;
  
  boolean starred;
  
  public static final int UNKNOWN = 0;
  
  public static final int LOAD_INTO_BROWSER = 1;
  
  public static final int SAVE_TO_FILE = 2;
  
  public static final int LAUNCH_APPLICATION = 3;
  
  static final String[] actionKeywords = { "unknown", "browser", "save", "application" };
  
  public MimeEntry(String paramString) { this(paramString, 0, null, null, null); }
  
  MimeEntry(String paramString1, String paramString2, String paramString3) {
    this.typeName = paramString1.toLowerCase();
    this.action = 0;
    this.command = null;
    this.imageFileName = paramString2;
    setExtensions(paramString3);
    this.starred = isStarred(this.typeName);
  }
  
  MimeEntry(String paramString1, int paramInt, String paramString2, String paramString3) {
    this.typeName = paramString1.toLowerCase();
    this.action = paramInt;
    this.command = paramString2;
    this.imageFileName = null;
    this.fileExtensions = null;
    this.tempFileNameTemplate = paramString3;
  }
  
  MimeEntry(String paramString1, int paramInt, String paramString2, String paramString3, String[] paramArrayOfString) {
    this.typeName = paramString1.toLowerCase();
    this.action = paramInt;
    this.command = paramString2;
    this.imageFileName = paramString3;
    this.fileExtensions = paramArrayOfString;
    this.starred = isStarred(paramString1);
  }
  
  public String getType() { return this.typeName; }
  
  public void setType(String paramString) { this.typeName = paramString.toLowerCase(); }
  
  public int getAction() { return this.action; }
  
  public void setAction(int paramInt, String paramString) {
    this.action = paramInt;
    this.command = paramString;
  }
  
  public void setAction(int paramInt) { this.action = paramInt; }
  
  public String getLaunchString() { return this.command; }
  
  public void setCommand(String paramString) { this.command = paramString; }
  
  public String getDescription() { return (this.description != null) ? this.description : this.typeName; }
  
  public void setDescription(String paramString) { this.description = paramString; }
  
  public String getImageFileName() { return this.imageFileName; }
  
  public void setImageFileName(String paramString) {
    File file = new File(paramString);
    if (file.getParent() == null) {
      this.imageFileName = System.getProperty("java.net.ftp.imagepath." + paramString);
    } else {
      this.imageFileName = paramString;
    } 
    if (paramString.lastIndexOf('.') < 0)
      this.imageFileName += ".gif"; 
  }
  
  public String getTempFileTemplate() { return this.tempFileNameTemplate; }
  
  public String[] getExtensions() { return this.fileExtensions; }
  
  public String getExtensionsAsList() {
    String str = "";
    if (this.fileExtensions != null)
      for (byte b = 0; b < this.fileExtensions.length; b++) {
        str = str + this.fileExtensions[b];
        if (b < this.fileExtensions.length - 1)
          str = str + ","; 
      }  
    return str;
  }
  
  public void setExtensions(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ",");
    int i = stringTokenizer.countTokens();
    String[] arrayOfString = new String[i];
    for (byte b = 0; b < i; b++) {
      String str = (String)stringTokenizer.nextElement();
      arrayOfString[b] = str.trim();
    } 
    this.fileExtensions = arrayOfString;
  }
  
  private boolean isStarred(String paramString) { return (paramString != null && paramString.length() > 0 && paramString.endsWith("/*")); }
  
  public Object launch(URLConnection paramURLConnection, InputStream paramInputStream, MimeTable paramMimeTable) throws ApplicationLaunchException {
    int i;
    String str;
    switch (this.action) {
      case 2:
        try {
          return paramInputStream;
        } catch (Exception exception) {
          return "Load to file failed:\n" + exception;
        } 
      case 1:
        try {
          return paramURLConnection.getContent();
        } catch (Exception exception) {
          return null;
        } 
      case 3:
        str = this.command;
        i = str.indexOf(' ');
        if (i > 0)
          str = str.substring(0, i); 
        return new MimeLauncher(this, paramURLConnection, paramInputStream, paramMimeTable.getTempFileTemplate(), str);
      case 0:
        return null;
    } 
    return null;
  }
  
  public boolean matches(String paramString) { return this.starred ? paramString.startsWith(this.typeName) : paramString.equals(this.typeName); }
  
  public Object clone() {
    MimeEntry mimeEntry = new MimeEntry(this.typeName);
    mimeEntry.action = this.action;
    mimeEntry.command = this.command;
    mimeEntry.description = this.description;
    mimeEntry.imageFileName = this.imageFileName;
    mimeEntry.tempFileNameTemplate = this.tempFileNameTemplate;
    mimeEntry.fileExtensions = this.fileExtensions;
    return mimeEntry;
  }
  
  public String toProperty() {
    StringBuffer stringBuffer = new StringBuffer();
    String str1 = "; ";
    boolean bool = false;
    int i = getAction();
    if (i != 0) {
      stringBuffer.append("action=" + actionKeywords[i]);
      bool = true;
    } 
    String str2 = getLaunchString();
    if (str2 != null && str2.length() > 0) {
      if (bool)
        stringBuffer.append(str1); 
      stringBuffer.append("application=" + str2);
      bool = true;
    } 
    if (getImageFileName() != null) {
      if (bool)
        stringBuffer.append(str1); 
      stringBuffer.append("icon=" + getImageFileName());
      bool = true;
    } 
    String str3 = getExtensionsAsList();
    if (str3.length() > 0) {
      if (bool)
        stringBuffer.append(str1); 
      stringBuffer.append("file_extensions=" + str3);
      bool = true;
    } 
    String str4 = getDescription();
    if (str4 != null && !str4.equals(getType())) {
      if (bool)
        stringBuffer.append(str1); 
      stringBuffer.append("description=" + str4);
    } 
    return stringBuffer.toString();
  }
  
  public String toString() { return "MimeEntry[contentType=" + this.typeName + ", image=" + this.imageFileName + ", action=" + this.action + ", command=" + this.command + ", extensions=" + getExtensionsAsList() + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\net\www\MimeEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.activation.registries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class MimeTypeFile {
  private String fname = null;
  
  private Hashtable type_hash = new Hashtable();
  
  public MimeTypeFile(String paramString) throws IOException {
    File file = null;
    fileReader = null;
    this.fname = paramString;
    file = new File(this.fname);
    fileReader = new FileReader(file);
    try {
      parse(new BufferedReader(fileReader));
    } finally {
      try {
        fileReader.close();
      } catch (IOException iOException) {}
    } 
  }
  
  public MimeTypeFile(InputStream paramInputStream) throws IOException { parse(new BufferedReader(new InputStreamReader(paramInputStream, "iso-8859-1"))); }
  
  public MimeTypeFile() {}
  
  public MimeTypeEntry getMimeTypeEntry(String paramString) { return (MimeTypeEntry)this.type_hash.get(paramString); }
  
  public String getMIMETypeString(String paramString) {
    MimeTypeEntry mimeTypeEntry = getMimeTypeEntry(paramString);
    return (mimeTypeEntry != null) ? mimeTypeEntry.getMIMEType() : null;
  }
  
  public void appendToRegistry(String paramString) throws IOException {
    try {
      parse(new BufferedReader(new StringReader(paramString)));
    } catch (IOException iOException) {}
  }
  
  private void parse(BufferedReader paramBufferedReader) throws IOException {
    String str1 = null;
    String str2;
    for (str2 = null; (str1 = paramBufferedReader.readLine()) != null; str2 = null) {
      if (str2 == null) {
        str2 = str1;
      } else {
        str2 = str2 + str1;
      } 
      int i = str2.length();
      if (str2.length() > 0 && str2.charAt(i - 1) == '\\') {
        str2 = str2.substring(0, i - 1);
        continue;
      } 
      parseEntry(str2);
    } 
    if (str2 != null)
      parseEntry(str2); 
  }
  
  private void parseEntry(String paramString) throws IOException {
    String str1 = null;
    String str2 = null;
    paramString = paramString.trim();
    if (paramString.length() == 0)
      return; 
    if (paramString.charAt(0) == '#')
      return; 
    if (paramString.indexOf('=') > 0) {
      LineTokenizer lineTokenizer = new LineTokenizer(paramString);
      while (lineTokenizer.hasMoreTokens()) {
        String str3 = lineTokenizer.nextToken();
        String str4 = null;
        if (lineTokenizer.hasMoreTokens() && lineTokenizer.nextToken().equals("=") && lineTokenizer.hasMoreTokens())
          str4 = lineTokenizer.nextToken(); 
        if (str4 == null) {
          if (LogSupport.isLoggable())
            LogSupport.log("Bad .mime.types entry: " + paramString); 
          return;
        } 
        if (str3.equals("type")) {
          str1 = str4;
          continue;
        } 
        if (str3.equals("exts")) {
          StringTokenizer stringTokenizer = new StringTokenizer(str4, ",");
          while (stringTokenizer.hasMoreTokens()) {
            str2 = stringTokenizer.nextToken();
            MimeTypeEntry mimeTypeEntry = new MimeTypeEntry(str1, str2);
            this.type_hash.put(str2, mimeTypeEntry);
            if (LogSupport.isLoggable())
              LogSupport.log("Added: " + mimeTypeEntry.toString()); 
          } 
        } 
      } 
    } else {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      int i = stringTokenizer.countTokens();
      if (i == 0)
        return; 
      str1 = stringTokenizer.nextToken();
      while (stringTokenizer.hasMoreTokens()) {
        MimeTypeEntry mimeTypeEntry = null;
        str2 = stringTokenizer.nextToken();
        mimeTypeEntry = new MimeTypeEntry(str1, str2);
        this.type_hash.put(str2, mimeTypeEntry);
        if (LogSupport.isLoggable())
          LogSupport.log("Added: " + mimeTypeEntry.toString()); 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\activation\registries\MimeTypeFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
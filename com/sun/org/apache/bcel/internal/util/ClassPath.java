package com.sun.org.apache.bcel.internal.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ClassPath implements Serializable {
  public static final ClassPath SYSTEM_CLASS_PATH = new ClassPath();
  
  private PathEntry[] paths;
  
  private String class_path;
  
  public ClassPath(String paramString) {
    this.class_path = paramString;
    ArrayList arrayList = new ArrayList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, SecuritySupport.getSystemProperty("path.separator"));
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (!str.equals("")) {
        File file = new File(str);
        try {
          if (SecuritySupport.getFileExists(file)) {
            if (file.isDirectory()) {
              arrayList.add(new Dir(str));
              continue;
            } 
            arrayList.add(new Zip(new ZipFile(file)));
          } 
        } catch (IOException iOException) {
          System.err.println("CLASSPATH component " + file + ": " + iOException);
        } 
      } 
    } 
    this.paths = new PathEntry[arrayList.size()];
    arrayList.toArray(this.paths);
  }
  
  public ClassPath() { this(""); }
  
  public String toString() { return this.class_path; }
  
  public int hashCode() { return this.class_path.hashCode(); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof ClassPath) ? this.class_path.equals(((ClassPath)paramObject).class_path) : 0; }
  
  private static final void getPathComponents(String paramString, ArrayList paramArrayList) {
    if (paramString != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString, File.pathSeparator);
      while (stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken();
        File file = new File(str);
        if (SecuritySupport.getFileExists(file))
          paramArrayList.add(str); 
      } 
    } 
  }
  
  public static final String getClassPath() {
    String str3;
    String str2;
    String str1;
    try {
      str1 = SecuritySupport.getSystemProperty("java.class.path");
      str2 = SecuritySupport.getSystemProperty("sun.boot.class.path");
      str3 = SecuritySupport.getSystemProperty("java.ext.dirs");
    } catch (SecurityException securityException) {
      return "";
    } 
    ArrayList arrayList1 = new ArrayList();
    getPathComponents(str1, arrayList1);
    getPathComponents(str2, arrayList1);
    ArrayList arrayList2 = new ArrayList();
    getPathComponents(str3, arrayList2);
    Iterator iterator1 = arrayList2.iterator();
    while (iterator1.hasNext()) {
      File file = new File((String)iterator1.next());
      String[] arrayOfString = SecuritySupport.getFileList(file, new FilenameFilter() {
            public boolean accept(File param1File, String param1String) {
              param1String = param1String.toLowerCase();
              return (param1String.endsWith(".zip") || param1String.endsWith(".jar"));
            }
          });
      if (arrayOfString != null)
        for (byte b = 0; b < arrayOfString.length; b++)
          arrayList1.add(str3 + File.separatorChar + arrayOfString[b]);  
    } 
    StringBuffer stringBuffer = new StringBuffer();
    Iterator iterator2 = arrayList1.iterator();
    while (iterator2.hasNext()) {
      stringBuffer.append((String)iterator2.next());
      if (iterator2.hasNext())
        stringBuffer.append(File.pathSeparatorChar); 
    } 
    return stringBuffer.toString().intern();
  }
  
  public InputStream getInputStream(String paramString) throws IOException { return getInputStream(paramString, ".class"); }
  
  public InputStream getInputStream(String paramString1, String paramString2) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = getClass().getClassLoader().getResourceAsStream(paramString1 + paramString2);
    } catch (Exception exception) {}
    return (inputStream != null) ? inputStream : getClassFile(paramString1, paramString2).getInputStream();
  }
  
  public ClassFile getClassFile(String paramString1, String paramString2) throws IOException {
    for (byte b = 0; b < this.paths.length; b++) {
      ClassFile classFile;
      if ((classFile = this.paths[b].getClassFile(paramString1, paramString2)) != null)
        return classFile; 
    } 
    throw new IOException("Couldn't find: " + paramString1 + paramString2);
  }
  
  public ClassFile getClassFile(String paramString) throws IOException { return getClassFile(paramString, ".class"); }
  
  public byte[] getBytes(String paramString1, String paramString2) throws IOException {
    InputStream inputStream = getInputStream(paramString1, paramString2);
    if (inputStream == null)
      throw new IOException("Couldn't find: " + paramString1 + paramString2); 
    DataInputStream dataInputStream = new DataInputStream(inputStream);
    byte[] arrayOfByte = new byte[inputStream.available()];
    dataInputStream.readFully(arrayOfByte);
    dataInputStream.close();
    inputStream.close();
    return arrayOfByte;
  }
  
  public byte[] getBytes(String paramString) throws IOException { return getBytes(paramString, ".class"); }
  
  public String getPath(String paramString) throws IOException {
    int i = paramString.lastIndexOf('.');
    String str = "";
    if (i > 0) {
      str = paramString.substring(i);
      paramString = paramString.substring(0, i);
    } 
    return getPath(paramString, str);
  }
  
  public String getPath(String paramString1, String paramString2) throws IOException { return getClassFile(paramString1, paramString2).getPath(); }
  
  public static interface ClassFile {
    InputStream getInputStream() throws IOException;
    
    String getPath();
    
    String getBase();
    
    long getTime();
    
    long getSize();
  }
  
  private static class Dir extends PathEntry {
    private String dir;
    
    Dir(String param1String) {
      super(null);
      this.dir = param1String;
    }
    
    ClassPath.ClassFile getClassFile(String param1String1, String param1String2) throws IOException {
      final File file = new File(this.dir + File.separatorChar + param1String1.replace('.', File.separatorChar) + param1String2);
      return SecuritySupport.getFileExists(file) ? new ClassPath.ClassFile() {
          public InputStream getInputStream() throws IOException { return new FileInputStream(file); }
          
          public String getPath() {
            try {
              return file.getCanonicalPath();
            } catch (IOException iOException) {
              return null;
            } 
          }
          
          public long getTime() { return file.lastModified(); }
          
          public long getSize() { return file.length(); }
          
          public String getBase() { return ClassPath.Dir.this.dir; }
        } : null;
    }
    
    public String toString() { return this.dir; }
  }
  
  private static abstract class PathEntry implements Serializable {
    private PathEntry() {}
    
    abstract ClassPath.ClassFile getClassFile(String param1String1, String param1String2) throws IOException;
  }
  
  private static class Zip extends PathEntry {
    private ZipFile zip;
    
    Zip(ZipFile param1ZipFile) {
      super(null);
      this.zip = param1ZipFile;
    }
    
    ClassPath.ClassFile getClassFile(String param1String1, String param1String2) throws IOException {
      final ZipEntry entry = this.zip.getEntry(param1String1.replace('.', '/') + param1String2);
      return (zipEntry != null) ? new ClassPath.ClassFile() {
          public InputStream getInputStream() throws IOException { return ClassPath.Zip.this.zip.getInputStream(entry); }
          
          public String getPath() { return entry.toString(); }
          
          public long getTime() { return entry.getTime(); }
          
          public long getSize() { return entry.getSize(); }
          
          public String getBase() { return ClassPath.Zip.this.zip.getName(); }
        } : null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\interna\\util\ClassPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
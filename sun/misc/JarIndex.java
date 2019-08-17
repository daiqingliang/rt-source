package sun.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import sun.security.action.GetPropertyAction;

public class JarIndex {
  private HashMap<String, LinkedList<String>> indexMap = new HashMap();
  
  private HashMap<String, LinkedList<String>> jarMap = new HashMap();
  
  private String[] jarFiles;
  
  public static final String INDEX_NAME = "META-INF/INDEX.LIST";
  
  private static final boolean metaInfFilenames = "true".equals(AccessController.doPrivileged(new GetPropertyAction("sun.misc.JarIndex.metaInfFilenames")));
  
  public JarIndex() {}
  
  public JarIndex(InputStream paramInputStream) throws IOException {
    this();
    read(paramInputStream);
  }
  
  public JarIndex(String[] paramArrayOfString) throws IOException {
    this();
    this.jarFiles = paramArrayOfString;
    parseJars(paramArrayOfString);
  }
  
  public static JarIndex getJarIndex(JarFile paramJarFile) throws IOException { return getJarIndex(paramJarFile, null); }
  
  public static JarIndex getJarIndex(JarFile paramJarFile, MetaIndex paramMetaIndex) throws IOException {
    JarIndex jarIndex = null;
    if (paramMetaIndex != null && !paramMetaIndex.mayContain("META-INF/INDEX.LIST"))
      return null; 
    JarEntry jarEntry = paramJarFile.getJarEntry("META-INF/INDEX.LIST");
    if (jarEntry != null)
      jarIndex = new JarIndex(paramJarFile.getInputStream(jarEntry)); 
    return jarIndex;
  }
  
  public String[] getJarFiles() { return this.jarFiles; }
  
  private void addToList(String paramString1, String paramString2, HashMap<String, LinkedList<String>> paramHashMap) {
    LinkedList linkedList = (LinkedList)paramHashMap.get(paramString1);
    if (linkedList == null) {
      linkedList = new LinkedList();
      linkedList.add(paramString2);
      paramHashMap.put(paramString1, linkedList);
    } else if (!linkedList.contains(paramString2)) {
      linkedList.add(paramString2);
    } 
  }
  
  public LinkedList<String> get(String paramString) {
    LinkedList linkedList = null;
    int i;
    if ((linkedList = (LinkedList)this.indexMap.get(paramString)) == null && (i = paramString.lastIndexOf("/")) != -1)
      linkedList = (LinkedList)this.indexMap.get(paramString.substring(0, i)); 
    return linkedList;
  }
  
  public void add(String paramString1, String paramString2) {
    String str;
    int i;
    if ((i = paramString1.lastIndexOf("/")) != -1) {
      str = paramString1.substring(0, i);
    } else {
      str = paramString1;
    } 
    addMapping(str, paramString2);
  }
  
  private void addMapping(String paramString1, String paramString2) {
    addToList(paramString1, paramString2, this.indexMap);
    addToList(paramString2, paramString1, this.jarMap);
  }
  
  private void parseJars(String[] paramArrayOfString) throws IOException {
    if (paramArrayOfString == null)
      return; 
    String str = null;
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      str = paramArrayOfString[b];
      ZipFile zipFile = new ZipFile(str.replace('/', File.separatorChar));
      Enumeration enumeration = zipFile.entries();
      while (enumeration.hasMoreElements()) {
        ZipEntry zipEntry = (ZipEntry)enumeration.nextElement();
        String str1 = zipEntry.getName();
        if (str1.equals("META-INF/") || str1.equals("META-INF/INDEX.LIST") || str1.equals("META-INF/MANIFEST.MF"))
          continue; 
        if (!metaInfFilenames || !str1.startsWith("META-INF/")) {
          add(str1, str);
          continue;
        } 
        if (!zipEntry.isDirectory())
          addMapping(str1, str); 
      } 
      zipFile.close();
    } 
  }
  
  public void write(OutputStream paramOutputStream) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(paramOutputStream, "UTF8"));
    bufferedWriter.write("JarIndex-Version: 1.0\n\n");
    if (this.jarFiles != null) {
      for (byte b = 0; b < this.jarFiles.length; b++) {
        String str = this.jarFiles[b];
        bufferedWriter.write(str + "\n");
        LinkedList linkedList = (LinkedList)this.jarMap.get(str);
        if (linkedList != null) {
          Iterator iterator = linkedList.iterator();
          while (iterator.hasNext())
            bufferedWriter.write((String)iterator.next() + "\n"); 
        } 
        bufferedWriter.write("\n");
      } 
      bufferedWriter.flush();
    } 
  }
  
  public void read(InputStream paramInputStream) throws IOException {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramInputStream, "UTF8"));
    String str1 = null;
    String str2 = null;
    Vector vector = new Vector();
    while ((str1 = bufferedReader.readLine()) != null && !str1.endsWith(".jar"));
    while (str1 != null) {
      if (str1.length() != 0)
        if (str1.endsWith(".jar")) {
          str2 = str1;
          vector.add(str2);
        } else {
          String str = str1;
          addMapping(str, str2);
        }  
      str1 = bufferedReader.readLine();
    } 
    this.jarFiles = (String[])vector.toArray(new String[vector.size()]);
  }
  
  public void merge(JarIndex paramJarIndex, String paramString) {
    for (Map.Entry entry : this.indexMap.entrySet()) {
      String str = (String)entry.getKey();
      LinkedList linkedList = (LinkedList)entry.getValue();
      for (String str1 : linkedList) {
        if (paramString != null)
          str1 = paramString.concat(str1); 
        paramJarIndex.addMapping(str, str1);
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\JarIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
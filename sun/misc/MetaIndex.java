package sun.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaIndex {
  private String[] contents;
  
  private boolean isClassOnlyJar;
  
  public static MetaIndex forJar(File paramFile) { return (MetaIndex)getJarMap().get(paramFile); }
  
  public static void registerDirectory(File paramFile) {
    File file = new File(paramFile, "meta-index");
    if (file.exists())
      try {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String str1 = null;
        String str2 = null;
        boolean bool = false;
        ArrayList arrayList = new ArrayList();
        Map map = getJarMap();
        paramFile = paramFile.getCanonicalFile();
        str1 = bufferedReader.readLine();
        if (str1 == null || !str1.equals("% VERSION 2")) {
          bufferedReader.close();
          return;
        } 
        while ((str1 = bufferedReader.readLine()) != null) {
          switch (str1.charAt(0)) {
            case '!':
            case '#':
            case '@':
              if (str2 != null && arrayList.size() > 0) {
                map.put(new File(paramFile, str2), new MetaIndex(arrayList, bool));
                arrayList.clear();
              } 
              str2 = str1.substring(2);
              if (str1.charAt(0) == '!') {
                bool = true;
                continue;
              } 
              if (bool)
                bool = false; 
              continue;
            case '%':
              continue;
          } 
          arrayList.add(str1);
        } 
        if (str2 != null && arrayList.size() > 0)
          map.put(new File(paramFile, str2), new MetaIndex(arrayList, bool)); 
        bufferedReader.close();
      } catch (IOException iOException) {} 
  }
  
  public boolean mayContain(String paramString) {
    if (this.isClassOnlyJar && !paramString.endsWith(".class"))
      return false; 
    String[] arrayOfString = this.contents;
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.startsWith(arrayOfString[b]))
        return true; 
    } 
    return false;
  }
  
  private MetaIndex(List<String> paramList, boolean paramBoolean) throws IllegalArgumentException {
    if (paramList == null)
      throw new IllegalArgumentException(); 
    this.contents = (String[])paramList.toArray(new String[0]);
    this.isClassOnlyJar = paramBoolean;
  }
  
  private static Map<File, MetaIndex> getJarMap() {
    if (jarMap == null)
      synchronized (MetaIndex.class) {
        if (jarMap == null)
          jarMap = new HashMap(); 
      }  
    assert jarMap != null;
    return jarMap;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\MetaIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
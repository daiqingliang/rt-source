package sun.misc;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarFile;

public class ClassLoaderUtil {
  public static void releaseLoader(URLClassLoader paramURLClassLoader) { releaseLoader(paramURLClassLoader, null); }
  
  public static List<IOException> releaseLoader(URLClassLoader paramURLClassLoader, List<String> paramList) {
    LinkedList linkedList = new LinkedList();
    try {
      if (paramList != null)
        paramList.clear(); 
      URLClassPath uRLClassPath = SharedSecrets.getJavaNetAccess().getURLClassPath(paramURLClassLoader);
      ArrayList arrayList = uRLClassPath.loaders;
      Stack stack = uRLClassPath.urls;
      HashMap hashMap = uRLClassPath.lmap;
      synchronized (stack) {
        stack.clear();
      } 
      synchronized (hashMap) {
        hashMap.clear();
      } 
      synchronized (uRLClassPath) {
        for (Object object : arrayList) {
          if (object != null && object instanceof URLClassPath.JarLoader) {
            URLClassPath.JarLoader jarLoader = (URLClassPath.JarLoader)object;
            JarFile jarFile = jarLoader.getJarFile();
            try {
              if (jarFile != null) {
                jarFile.close();
                if (paramList != null)
                  paramList.add(jarFile.getName()); 
              } 
            } catch (IOException iOException1) {
              String str1 = (jarFile == null) ? "filename not available" : jarFile.getName();
              String str2 = "Error closing JAR file: " + str1;
              IOException iOException2 = new IOException(str2);
              iOException2.initCause(iOException1);
              linkedList.add(iOException2);
            } 
          } 
        } 
        arrayList.clear();
      } 
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    } 
    return linkedList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ClassLoaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
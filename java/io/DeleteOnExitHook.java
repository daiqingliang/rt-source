package java.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import sun.misc.SharedSecrets;

class DeleteOnExitHook {
  private static LinkedHashSet<String> files = new LinkedHashSet();
  
  static void add(String paramString) {
    if (files == null)
      throw new IllegalStateException("Shutdown in progress"); 
    files.add(paramString);
  }
  
  static void runHooks() {
    LinkedHashSet linkedHashSet;
    synchronized (DeleteOnExitHook.class) {
      linkedHashSet = files;
      files = null;
    } 
    ArrayList arrayList = new ArrayList(linkedHashSet);
    Collections.reverse(arrayList);
    for (String str : arrayList)
      (new File(str)).delete(); 
  }
  
  static  {
    SharedSecrets.getJavaLangAccess().registerShutdownHook(2, true, new Runnable() {
          public void run() { DeleteOnExitHook.runHooks(); }
        });
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\DeleteOnExitHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
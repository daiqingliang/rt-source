package com.sun.media.sound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.sound.sampled.AudioPermission;

final class JSSecurityManager {
  private static boolean hasSecurityManager() { return (System.getSecurityManager() != null); }
  
  static void checkRecordPermission() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(new AudioPermission("record")); 
  }
  
  static void loadProperties(final Properties properties, final String filename) {
    if (hasSecurityManager()) {
      try {
        PrivilegedAction<Void> privilegedAction = new PrivilegedAction<Void>() {
            public Void run() {
              JSSecurityManager.loadPropertiesImpl(properties, filename);
              return null;
            }
          };
        AccessController.doPrivileged(privilegedAction);
      } catch (Exception exception) {
        loadPropertiesImpl(paramProperties, paramString);
      } 
    } else {
      loadPropertiesImpl(paramProperties, paramString);
    } 
  }
  
  private static void loadPropertiesImpl(Properties paramProperties, String paramString) {
    String str = System.getProperty("java.home");
    try {
      if (str == null)
        throw new Error("Can't find java.home ??"); 
      File file = new File(str, "lib");
      file = new File(file, paramString);
      str = file.getCanonicalPath();
      fileInputStream = new FileInputStream(str);
      BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
      try {
        paramProperties.load(bufferedInputStream);
      } finally {
        if (fileInputStream != null)
          fileInputStream.close(); 
      } 
    } catch (Throwable throwable) {}
  }
  
  static Thread createThread(Runnable paramRunnable, String paramString, boolean paramBoolean1, int paramInt, boolean paramBoolean2) {
    Thread thread = new Thread(paramRunnable);
    if (paramString != null)
      thread.setName(paramString); 
    thread.setDaemon(paramBoolean1);
    if (paramInt >= 0)
      thread.setPriority(paramInt); 
    if (paramBoolean2)
      thread.start(); 
    return thread;
  }
  
  static <T> List<T> getProviders(final Class<T> providerClass) {
    ArrayList arrayList = new ArrayList(7);
    PrivilegedAction<Iterator<T>> privilegedAction1 = new PrivilegedAction<Iterator<T>>() {
        public Iterator<T> run() { return ServiceLoader.load(providerClass).iterator(); }
      };
    final Iterator ps = (Iterator)AccessController.doPrivileged(privilegedAction1);
    PrivilegedAction<Boolean> privilegedAction2 = new PrivilegedAction<Boolean>() {
        public Boolean run() { return Boolean.valueOf(ps.hasNext()); }
      };
    while (((Boolean)AccessController.doPrivileged(privilegedAction2)).booleanValue()) {
      try {
        Object object = iterator.next();
        if (paramClass.isInstance(object))
          arrayList.add(0, object); 
      } catch (Throwable throwable) {}
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\JSSecurityManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
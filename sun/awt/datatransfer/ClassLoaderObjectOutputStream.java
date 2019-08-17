package sun.awt.datatransfer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectOutputStream extends ObjectOutputStream {
  private final Map<Set<String>, ClassLoader> map = new HashMap();
  
  ClassLoaderObjectOutputStream(OutputStream paramOutputStream) throws IOException { super(paramOutputStream); }
  
  protected void annotateClass(final Class<?> cl) throws IOException {
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return cl.getClassLoader(); }
        });
    HashSet hashSet = new HashSet(1);
    hashSet.add(paramClass.getName());
    this.map.put(hashSet, classLoader);
  }
  
  protected void annotateProxyClass(final Class<?> cl) throws IOException {
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() { return cl.getClassLoader(); }
        });
    Class[] arrayOfClass = paramClass.getInterfaces();
    HashSet hashSet = new HashSet(arrayOfClass.length);
    for (byte b = 0; b < arrayOfClass.length; b++)
      hashSet.add(arrayOfClass[b].getName()); 
    this.map.put(hashSet, classLoader);
  }
  
  Map<Set<String>, ClassLoader> getClassLoaderMap() { return new HashMap(this.map); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\ClassLoaderObjectOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
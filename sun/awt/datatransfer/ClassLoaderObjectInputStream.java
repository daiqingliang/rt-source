package sun.awt.datatransfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

final class ClassLoaderObjectInputStream extends ObjectInputStream {
  private final Map<Set<String>, ClassLoader> map;
  
  ClassLoaderObjectInputStream(InputStream paramInputStream, Map<Set<String>, ClassLoader> paramMap) throws IOException {
    super(paramInputStream);
    if (paramMap == null)
      throw new NullPointerException("Null map"); 
    this.map = paramMap;
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException {
    String str = paramObjectStreamClass.getName();
    HashSet hashSet = new HashSet(1);
    hashSet.add(str);
    ClassLoader classLoader = (ClassLoader)this.map.get(hashSet);
    return (classLoader != null) ? Class.forName(str, false, classLoader) : super.resolveClass(paramObjectStreamClass);
  }
  
  protected Class<?> resolveProxyClass(String[] paramArrayOfString) throws IOException, ClassNotFoundException {
    HashSet hashSet = new HashSet(paramArrayOfString.length);
    for (byte b1 = 0; b1 < paramArrayOfString.length; b1++)
      hashSet.add(paramArrayOfString[b1]); 
    ClassLoader classLoader1 = (ClassLoader)this.map.get(hashSet);
    if (classLoader1 == null)
      return super.resolveProxyClass(paramArrayOfString); 
    ClassLoader classLoader2 = null;
    boolean bool = false;
    Class[] arrayOfClass = new Class[paramArrayOfString.length];
    for (b2 = 0; b2 < paramArrayOfString.length; b2++) {
      Class clazz = Class.forName(paramArrayOfString[b2], false, classLoader1);
      if ((clazz.getModifiers() & true) == 0)
        if (bool) {
          if (classLoader2 != clazz.getClassLoader())
            throw new IllegalAccessError("conflicting non-public interface class loaders"); 
        } else {
          classLoader2 = clazz.getClassLoader();
          bool = true;
        }  
      arrayOfClass[b2] = clazz;
    } 
    try {
      return Proxy.getProxyClass(bool ? classLoader2 : classLoader1, arrayOfClass);
    } catch (IllegalArgumentException b2) {
      IllegalArgumentException illegalArgumentException;
      throw new ClassNotFoundException(null, illegalArgumentException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\datatransfer\ClassLoaderObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
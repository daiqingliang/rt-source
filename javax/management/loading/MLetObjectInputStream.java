package javax.management.loading;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;

class MLetObjectInputStream extends ObjectInputStream {
  private MLet loader;
  
  public MLetObjectInputStream(InputStream paramInputStream, MLet paramMLet) throws IOException, StreamCorruptedException {
    super(paramInputStream);
    if (paramMLet == null)
      throw new IllegalArgumentException("Illegal null argument to MLetObjectInputStream"); 
    this.loader = paramMLet;
  }
  
  private Class<?> primitiveType(char paramChar) {
    switch (paramChar) {
      case 'B':
        return byte.class;
      case 'C':
        return char.class;
      case 'D':
        return double.class;
      case 'F':
        return float.class;
      case 'I':
        return int.class;
      case 'J':
        return long.class;
      case 'S':
        return short.class;
      case 'Z':
        return boolean.class;
    } 
    return null;
  }
  
  protected Class<?> resolveClass(ObjectStreamClass paramObjectStreamClass) throws IOException, ClassNotFoundException {
    String str = paramObjectStreamClass.getName();
    if (str.startsWith("[")) {
      Class clazz;
      byte b1;
      for (b1 = 1; str.charAt(b1) == '['; b1++);
      if (str.charAt(b1) == 'L') {
        clazz = this.loader.loadClass(str.substring(b1 + 1, str.length() - 1));
      } else {
        if (str.length() != b1 + 1)
          throw new ClassNotFoundException(str); 
        clazz = primitiveType(str.charAt(b1));
      } 
      int[] arrayOfInt = new int[b1];
      for (byte b2 = 0; b2 < b1; b2++)
        arrayOfInt[b2] = 0; 
      return Array.newInstance(clazz, arrayOfInt).getClass();
    } 
    return this.loader.loadClass(str);
  }
  
  public ClassLoader getClassLoader() { return this.loader; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\loading\MLetObjectInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
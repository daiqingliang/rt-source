package sun.reflect;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

class ClassDefiner {
  static final Unsafe unsafe = Unsafe.getUnsafe();
  
  static Class<?> defineClass(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, final ClassLoader parentClassLoader) {
    ClassLoader classLoader = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
          public ClassLoader run() { return new DelegatingClassLoader(parentClassLoader); }
        });
    return unsafe.defineClass(paramString, paramArrayOfByte, paramInt1, paramInt2, classLoader, null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ClassDefiner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
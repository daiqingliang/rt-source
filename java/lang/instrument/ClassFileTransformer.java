package java.lang.instrument;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public interface ClassFileTransformer {
  byte[] transform(ClassLoader paramClassLoader, String paramString, Class<?> paramClass, ProtectionDomain paramProtectionDomain, byte[] paramArrayOfByte) throws IllegalClassFormatException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\instrument\ClassFileTransformer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
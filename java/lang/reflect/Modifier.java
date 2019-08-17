package java.lang.reflect;

import java.lang.reflect.ReflectAccess;
import java.security.AccessController;
import sun.reflect.ReflectionFactory;

public class Modifier {
  public static final int PUBLIC = 1;
  
  public static final int PRIVATE = 2;
  
  public static final int PROTECTED = 4;
  
  public static final int STATIC = 8;
  
  public static final int FINAL = 16;
  
  public static final int SYNCHRONIZED = 32;
  
  public static final int VOLATILE = 64;
  
  public static final int TRANSIENT = 128;
  
  public static final int NATIVE = 256;
  
  public static final int INTERFACE = 512;
  
  public static final int ABSTRACT = 1024;
  
  public static final int STRICT = 2048;
  
  static final int BRIDGE = 64;
  
  static final int VARARGS = 128;
  
  static final int SYNTHETIC = 4096;
  
  static final int ANNOTATION = 8192;
  
  static final int ENUM = 16384;
  
  static final int MANDATED = 32768;
  
  private static final int CLASS_MODIFIERS = 3103;
  
  private static final int INTERFACE_MODIFIERS = 3087;
  
  private static final int CONSTRUCTOR_MODIFIERS = 7;
  
  private static final int METHOD_MODIFIERS = 3391;
  
  private static final int FIELD_MODIFIERS = 223;
  
  private static final int PARAMETER_MODIFIERS = 16;
  
  static final int ACCESS_MODIFIERS = 7;
  
  public static boolean isPublic(int paramInt) { return ((paramInt & true) != 0); }
  
  public static boolean isPrivate(int paramInt) { return ((paramInt & 0x2) != 0); }
  
  public static boolean isProtected(int paramInt) { return ((paramInt & 0x4) != 0); }
  
  public static boolean isStatic(int paramInt) { return ((paramInt & 0x8) != 0); }
  
  public static boolean isFinal(int paramInt) { return ((paramInt & 0x10) != 0); }
  
  public static boolean isSynchronized(int paramInt) { return ((paramInt & 0x20) != 0); }
  
  public static boolean isVolatile(int paramInt) { return ((paramInt & 0x40) != 0); }
  
  public static boolean isTransient(int paramInt) { return ((paramInt & 0x80) != 0); }
  
  public static boolean isNative(int paramInt) { return ((paramInt & 0x100) != 0); }
  
  public static boolean isInterface(int paramInt) { return ((paramInt & 0x200) != 0); }
  
  public static boolean isAbstract(int paramInt) { return ((paramInt & 0x400) != 0); }
  
  public static boolean isStrict(int paramInt) { return ((paramInt & 0x800) != 0); }
  
  public static String toString(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    if ((paramInt & true) != 0)
      stringBuilder.append("public "); 
    if ((paramInt & 0x4) != 0)
      stringBuilder.append("protected "); 
    if ((paramInt & 0x2) != 0)
      stringBuilder.append("private "); 
    if ((paramInt & 0x400) != 0)
      stringBuilder.append("abstract "); 
    if ((paramInt & 0x8) != 0)
      stringBuilder.append("static "); 
    if ((paramInt & 0x10) != 0)
      stringBuilder.append("final "); 
    if ((paramInt & 0x80) != 0)
      stringBuilder.append("transient "); 
    if ((paramInt & 0x40) != 0)
      stringBuilder.append("volatile "); 
    if ((paramInt & 0x20) != 0)
      stringBuilder.append("synchronized "); 
    if ((paramInt & 0x100) != 0)
      stringBuilder.append("native "); 
    if ((paramInt & 0x800) != 0)
      stringBuilder.append("strictfp "); 
    if ((paramInt & 0x200) != 0)
      stringBuilder.append("interface "); 
    int i;
    return ((i = stringBuilder.length()) > 0) ? stringBuilder.toString().substring(0, i - 1) : "";
  }
  
  static boolean isSynthetic(int paramInt) { return ((paramInt & 0x1000) != 0); }
  
  static boolean isMandated(int paramInt) { return ((paramInt & 0x8000) != 0); }
  
  public static int classModifiers() { return 3103; }
  
  public static int interfaceModifiers() { return 3087; }
  
  public static int constructorModifiers() { return 7; }
  
  public static int methodModifiers() { return 3391; }
  
  public static int fieldModifiers() { return 223; }
  
  public static int parameterModifiers() { return 16; }
  
  static  {
    ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
    reflectionFactory.setLangReflectAccess(new ReflectAccess());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\reflect\Modifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
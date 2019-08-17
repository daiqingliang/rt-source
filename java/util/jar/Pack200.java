package java.util.jar;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.SortedMap;
import sun.security.action.GetPropertyAction;

public abstract class Pack200 {
  private static final String PACK_PROVIDER = "java.util.jar.Pack200.Packer";
  
  private static final String UNPACK_PROVIDER = "java.util.jar.Pack200.Unpacker";
  
  private static Class<?> packerImpl;
  
  private static Class<?> unpackerImpl;
  
  public static Packer newPacker() { return (Packer)newInstance("java.util.jar.Pack200.Packer"); }
  
  public static Unpacker newUnpacker() { return (Unpacker)newInstance("java.util.jar.Pack200.Unpacker"); }
  
  private static Object newInstance(String paramString) {
    String str = "(unknown)";
    try {
      Class clazz = "java.util.jar.Pack200.Packer".equals(paramString) ? packerImpl : unpackerImpl;
      if (clazz == null) {
        str = (String)AccessController.doPrivileged(new GetPropertyAction(paramString, ""));
        if (str != null && !str.equals("")) {
          clazz = Class.forName(str);
        } else if ("java.util.jar.Pack200.Packer".equals(paramString)) {
          clazz = com.sun.java.util.jar.pack.PackerImpl.class;
        } else {
          clazz = com.sun.java.util.jar.pack.UnpackerImpl.class;
        } 
      } 
      return clazz.newInstance();
    } catch (ClassNotFoundException classNotFoundException) {
      throw new Error("Class not found: " + str + ":\ncheck property " + paramString + " in your properties file.", classNotFoundException);
    } catch (InstantiationException instantiationException) {
      throw new Error("Could not instantiate: " + str + ":\ncheck property " + paramString + " in your properties file.", instantiationException);
    } catch (IllegalAccessException illegalAccessException) {
      throw new Error("Cannot access class: " + str + ":\ncheck property " + paramString + " in your properties file.", illegalAccessException);
    } 
  }
  
  public static interface Packer {
    public static final String SEGMENT_LIMIT = "pack.segment.limit";
    
    public static final String KEEP_FILE_ORDER = "pack.keep.file.order";
    
    public static final String EFFORT = "pack.effort";
    
    public static final String DEFLATE_HINT = "pack.deflate.hint";
    
    public static final String MODIFICATION_TIME = "pack.modification.time";
    
    public static final String PASS_FILE_PFX = "pack.pass.file.";
    
    public static final String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";
    
    public static final String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
    
    public static final String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
    
    public static final String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
    
    public static final String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
    
    public static final String PROGRESS = "pack.progress";
    
    public static final String KEEP = "keep";
    
    public static final String PASS = "pass";
    
    public static final String STRIP = "strip";
    
    public static final String ERROR = "error";
    
    public static final String TRUE = "true";
    
    public static final String FALSE = "false";
    
    public static final String LATEST = "latest";
    
    SortedMap<String, String> properties();
    
    void pack(JarFile param1JarFile, OutputStream param1OutputStream) throws IOException;
    
    void pack(JarInputStream param1JarInputStream, OutputStream param1OutputStream) throws IOException;
    
    @Deprecated
    default void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    @Deprecated
    default void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
  }
  
  public static interface Unpacker {
    public static final String KEEP = "keep";
    
    public static final String TRUE = "true";
    
    public static final String FALSE = "false";
    
    public static final String DEFLATE_HINT = "unpack.deflate.hint";
    
    public static final String PROGRESS = "unpack.progress";
    
    SortedMap<String, String> properties();
    
    void unpack(InputStream param1InputStream, JarOutputStream param1JarOutputStream) throws IOException;
    
    void unpack(File param1File, JarOutputStream param1JarOutputStream) throws IOException;
    
    @Deprecated
    default void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
    
    @Deprecated
    default void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\jar\Pack200.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
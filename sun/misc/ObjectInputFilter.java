package sun.misc;

import java.io.ObjectInputStream;
import java.io.SerializablePermission;
import java.security.AccessController;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import sun.util.logging.PlatformLogger;

@FunctionalInterface
public interface ObjectInputFilter {
  Status checkInput(FilterInfo paramFilterInfo);
  
  public static final class Config {
    private static final Object serialFilterLock = new Object();
    
    private static final PlatformLogger configLog;
    
    private static final String SERIAL_FILTER_PROPNAME = "jdk.serialFilter";
    
    private static final ObjectInputFilter configuredFilter = (ObjectInputFilter)AccessController.doPrivileged(() -> {
          String str = System.getProperty("jdk.serialFilter");
          if (str == null)
            str = Security.getProperty("jdk.serialFilter"); 
          if (str != null) {
            PlatformLogger platformLogger = PlatformLogger.getLogger("java.io.serialization");
            platformLogger.info("Creating serialization filter from {0}", new Object[] { str });
            try {
              return createFilter(str);
            } catch (RuntimeException runtimeException) {
              platformLogger.warning("Error configuring filter: {0}", runtimeException);
            } 
          } 
          return null;
        });
    
    private static ObjectInputFilter serialFilter;
    
    static void filterLog(PlatformLogger.Level param1Level, String param1String, Object... param1VarArgs) {
      if (configLog != null)
        if (PlatformLogger.Level.INFO.equals(param1Level)) {
          configLog.info(param1String, param1VarArgs);
        } else if (PlatformLogger.Level.WARNING.equals(param1Level)) {
          configLog.warning(param1String, param1VarArgs);
        } else {
          configLog.severe(param1String, param1VarArgs);
        }  
    }
    
    public static ObjectInputFilter getObjectInputFilter(ObjectInputStream param1ObjectInputStream) {
      Objects.requireNonNull(param1ObjectInputStream, "inputStream");
      return SharedSecrets.getJavaOISAccess().getObjectInputFilter(param1ObjectInputStream);
    }
    
    public static void setObjectInputFilter(ObjectInputStream param1ObjectInputStream, ObjectInputFilter param1ObjectInputFilter) {
      Objects.requireNonNull(param1ObjectInputStream, "inputStream");
      SharedSecrets.getJavaOISAccess().setObjectInputFilter(param1ObjectInputStream, param1ObjectInputFilter);
    }
    
    public static ObjectInputFilter getSerialFilter() {
      synchronized (serialFilterLock) {
        return serialFilter;
      } 
    }
    
    public static void setSerialFilter(ObjectInputFilter param1ObjectInputFilter) {
      Objects.requireNonNull(param1ObjectInputFilter, "filter");
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null)
        securityManager.checkPermission(new SerializablePermission("serialFilter")); 
      synchronized (serialFilterLock) {
        if (serialFilter != null)
          throw new IllegalStateException("Serial filter can only be set once"); 
        serialFilter = param1ObjectInputFilter;
      } 
    }
    
    public static ObjectInputFilter createFilter(String param1String) {
      Objects.requireNonNull(param1String, "pattern");
      return Global.createFilter(param1String, true);
    }
    
    public static ObjectInputFilter createFilter2(String param1String) {
      Objects.requireNonNull(param1String, "pattern");
      return Global.createFilter(param1String, false);
    }
    
    static  {
      configLog = (configuredFilter != null) ? PlatformLogger.getLogger("java.io.serialization") : null;
      serialFilter = configuredFilter;
    }
    
    static final class Global implements ObjectInputFilter {
      private final String pattern;
      
      private final List<Function<Class<?>, ObjectInputFilter.Status>> filters;
      
      private long maxStreamBytes;
      
      private long maxDepth;
      
      private long maxReferences;
      
      private long maxArrayLength;
      
      private final boolean checkComponentType;
      
      static ObjectInputFilter createFilter(String param2String, boolean param2Boolean) {
        Global global = new Global(param2String, param2Boolean);
        return global.isEmpty() ? null : global;
      }
      
      private Global(String param2String, boolean param2Boolean) {
        this.pattern = param2String;
        this.checkComponentType = param2Boolean;
        this.maxArrayLength = Float.MAX_VALUE;
        this.maxDepth = Float.MAX_VALUE;
        this.maxReferences = Float.MAX_VALUE;
        this.maxStreamBytes = Float.MAX_VALUE;
        String[] arrayOfString = param2String.split(";");
        this.filters = new ArrayList(arrayOfString.length);
        for (byte b = 0; b < arrayOfString.length; b++) {
          String str = arrayOfString[b];
          int i = str.length();
          if (i != 0 && !parseLimit(str)) {
            boolean bool = (str.charAt(0) == '!') ? 1 : 0;
            if (str.indexOf('/') >= 0)
              throw new IllegalArgumentException("invalid character \"/\" in: \"" + param2String + "\""); 
            if (str.endsWith("*")) {
              if (str.endsWith(".*")) {
                String str1 = str.substring(bool ? 1 : 0, i - 1);
                if (str1.length() < 2)
                  throw new IllegalArgumentException("package missing in: \"" + param2String + "\""); 
                if (bool) {
                  this.filters.add(param2Class -> matchesPackage(param2Class, param2String) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED);
                } else {
                  this.filters.add(param2Class -> matchesPackage(param2Class, param2String) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED);
                } 
              } else if (str.endsWith(".**")) {
                String str1 = str.substring(bool ? 1 : 0, i - 2);
                if (str1.length() < 2)
                  throw new IllegalArgumentException("package missing in: \"" + param2String + "\""); 
                if (bool) {
                  this.filters.add(param2Class -> param2Class.getName().startsWith(param2String) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED);
                } else {
                  this.filters.add(param2Class -> param2Class.getName().startsWith(param2String) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED);
                } 
              } else {
                String str1 = str.substring(bool ? 1 : 0, i - 1);
                if (bool) {
                  this.filters.add(param2Class -> param2Class.getName().startsWith(param2String) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED);
                } else {
                  this.filters.add(param2Class -> param2Class.getName().startsWith(param2String) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED);
                } 
              } 
            } else {
              String str1 = str.substring(bool ? 1 : 0);
              if (str1.isEmpty())
                throw new IllegalArgumentException("class or package missing in: \"" + param2String + "\""); 
              if (bool) {
                this.filters.add(param2Class -> param2Class.getName().equals(param2String) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED);
              } else {
                this.filters.add(param2Class -> param2Class.getName().equals(param2String) ? ObjectInputFilter.Status.ALLOWED : ObjectInputFilter.Status.UNDECIDED);
              } 
            } 
          } 
        } 
      }
      
      private boolean isEmpty() { return (this.filters.isEmpty() && this.maxArrayLength == Float.MAX_VALUE && this.maxDepth == Float.MAX_VALUE && this.maxReferences == Float.MAX_VALUE && this.maxStreamBytes == Float.MAX_VALUE); }
      
      private boolean parseLimit(String param2String) {
        int i = param2String.indexOf('=');
        if (i < 0)
          return false; 
        String str = param2String.substring(i + 1);
        if (param2String.startsWith("maxdepth=")) {
          this.maxDepth = parseValue(str);
        } else if (param2String.startsWith("maxarray=")) {
          this.maxArrayLength = parseValue(str);
        } else if (param2String.startsWith("maxrefs=")) {
          this.maxReferences = parseValue(str);
        } else if (param2String.startsWith("maxbytes=")) {
          this.maxStreamBytes = parseValue(str);
        } else {
          throw new IllegalArgumentException("unknown limit: " + param2String.substring(0, i));
        } 
        return true;
      }
      
      private static long parseValue(String param2String) throws IllegalArgumentException {
        long l = Long.parseLong(param2String);
        if (l < 0L)
          throw new IllegalArgumentException("negative limit: " + param2String); 
        return l;
      }
      
      public ObjectInputFilter.Status checkInput(ObjectInputFilter.FilterInfo param2FilterInfo) {
        if (param2FilterInfo.references() < 0L || param2FilterInfo.depth() < 0L || param2FilterInfo.streamBytes() < 0L || param2FilterInfo.references() > this.maxReferences || param2FilterInfo.depth() > this.maxDepth || param2FilterInfo.streamBytes() > this.maxStreamBytes)
          return ObjectInputFilter.Status.REJECTED; 
        Class clazz = param2FilterInfo.serialClass();
        if (clazz != null) {
          if (clazz.isArray()) {
            if (param2FilterInfo.arrayLength() >= 0L && param2FilterInfo.arrayLength() > this.maxArrayLength)
              return ObjectInputFilter.Status.REJECTED; 
            if (!this.checkComponentType)
              return ObjectInputFilter.Status.UNDECIDED; 
            do {
              clazz = clazz.getComponentType();
            } while (clazz.isArray());
          } 
          if (clazz.isPrimitive())
            return ObjectInputFilter.Status.UNDECIDED; 
          Class clazz1 = clazz;
          Optional optional = this.filters.stream().map(param2Function -> (ObjectInputFilter.Status)param2Function.apply(param2Class)).filter(param2Status -> (param2Status != ObjectInputFilter.Status.UNDECIDED)).findFirst();
          return (ObjectInputFilter.Status)optional.orElse(ObjectInputFilter.Status.UNDECIDED);
        } 
        return ObjectInputFilter.Status.UNDECIDED;
      }
      
      private static boolean matchesPackage(Class<?> param2Class, String param2String) {
        String str = param2Class.getName();
        return (str.startsWith(param2String) && str.lastIndexOf('.') == param2String.length() - 1);
      }
      
      public String toString() { return this.pattern; }
    }
  }
  
  public static interface FilterInfo {
    Class<?> serialClass();
    
    long arrayLength();
    
    long depth();
    
    long references();
    
    long streamBytes();
  }
  
  public enum Status {
    UNDECIDED, ALLOWED, REJECTED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\ObjectInputFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
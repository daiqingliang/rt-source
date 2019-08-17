package sun.corba;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

public final class Bridge {
  private static final Class[] NO_ARGS = new Class[0];
  
  private static final Permission getBridgePermission = new BridgePermission("getBridge");
  
  private static Bridge bridge = null;
  
  private final Method latestUserDefinedLoaderMethod = getLatestUserDefinedLoaderMethod();
  
  private final Unsafe unsafe = getUnsafe();
  
  private final ReflectionFactory reflectionFactory = (ReflectionFactory)AccessController.doPrivileged(new ReflectionFactory.GetReflectionFactoryAction());
  
  public static final long INVALID_FIELD_OFFSET = -1L;
  
  private Method getLatestUserDefinedLoaderMethod() { return (Method)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Method method = null;
            try {
              Class clazz = java.io.ObjectInputStream.class;
              method = clazz.getDeclaredMethod("latestUserDefinedLoader", NO_ARGS);
              method.setAccessible(true);
            } catch (NoSuchMethodException noSuchMethodException) {
              Error error = new Error("java.io.ObjectInputStream latestUserDefinedLoader " + noSuchMethodException);
              error.initCause(noSuchMethodException);
              throw error;
            } 
            return method;
          }
        }); }
  
  private Unsafe getUnsafe() {
    Field field = (Field)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Field field = null;
            try {
              Class clazz = Unsafe.class;
              field = clazz.getDeclaredField("theUnsafe");
              field.setAccessible(true);
              return field;
            } catch (NoSuchFieldException noSuchFieldException) {
              Error error = new Error("Could not access Unsafe");
              error.initCause(noSuchFieldException);
              throw error;
            } 
          }
        });
    Unsafe unsafe1 = null;
    try {
      unsafe1 = (Unsafe)field.get(null);
    } catch (Throwable throwable) {
      Error error = new Error("Could not access Unsafe");
      error.initCause(throwable);
      throw error;
    } 
    return unsafe1;
  }
  
  public static final Bridge get() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPermission(getBridgePermission); 
    if (bridge == null)
      bridge = new Bridge(); 
    return bridge;
  }
  
  public final ClassLoader getLatestUserDefinedLoader() {
    try {
      return (ClassLoader)this.latestUserDefinedLoaderMethod.invoke(null, (Object[])NO_ARGS);
    } catch (InvocationTargetException invocationTargetException) {
      Error error = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + invocationTargetException);
      error.initCause(invocationTargetException);
      throw error;
    } catch (IllegalAccessException illegalAccessException) {
      Error error = new Error("sun.corba.Bridge.latestUserDefinedLoader: " + illegalAccessException);
      error.initCause(illegalAccessException);
      throw error;
    } 
  }
  
  public final int getInt(Object paramObject, long paramLong) { return this.unsafe.getInt(paramObject, paramLong); }
  
  public final void putInt(Object paramObject, long paramLong, int paramInt) { this.unsafe.putInt(paramObject, paramLong, paramInt); }
  
  public final Object getObject(Object paramObject, long paramLong) { return this.unsafe.getObject(paramObject, paramLong); }
  
  public final void putObject(Object paramObject1, long paramLong, Object paramObject2) { this.unsafe.putObject(paramObject1, paramLong, paramObject2); }
  
  public final boolean getBoolean(Object paramObject, long paramLong) { return this.unsafe.getBoolean(paramObject, paramLong); }
  
  public final void putBoolean(Object paramObject, long paramLong, boolean paramBoolean) { this.unsafe.putBoolean(paramObject, paramLong, paramBoolean); }
  
  public final byte getByte(Object paramObject, long paramLong) { return this.unsafe.getByte(paramObject, paramLong); }
  
  public final void putByte(Object paramObject, long paramLong, byte paramByte) { this.unsafe.putByte(paramObject, paramLong, paramByte); }
  
  public final short getShort(Object paramObject, long paramLong) { return this.unsafe.getShort(paramObject, paramLong); }
  
  public final void putShort(Object paramObject, long paramLong, short paramShort) { this.unsafe.putShort(paramObject, paramLong, paramShort); }
  
  public final char getChar(Object paramObject, long paramLong) { return this.unsafe.getChar(paramObject, paramLong); }
  
  public final void putChar(Object paramObject, long paramLong, char paramChar) { this.unsafe.putChar(paramObject, paramLong, paramChar); }
  
  public final long getLong(Object paramObject, long paramLong) { return this.unsafe.getLong(paramObject, paramLong); }
  
  public final void putLong(Object paramObject, long paramLong1, long paramLong2) { this.unsafe.putLong(paramObject, paramLong1, paramLong2); }
  
  public final float getFloat(Object paramObject, long paramLong) { return this.unsafe.getFloat(paramObject, paramLong); }
  
  public final void putFloat(Object paramObject, long paramLong, float paramFloat) { this.unsafe.putFloat(paramObject, paramLong, paramFloat); }
  
  public final double getDouble(Object paramObject, long paramLong) { return this.unsafe.getDouble(paramObject, paramLong); }
  
  public final void putDouble(Object paramObject, long paramLong, double paramDouble) { this.unsafe.putDouble(paramObject, paramLong, paramDouble); }
  
  public final long objectFieldOffset(Field paramField) { return this.unsafe.objectFieldOffset(paramField); }
  
  public final void throwException(Throwable paramThrowable) { this.unsafe.throwException(paramThrowable); }
  
  public final Constructor newConstructorForSerialization(Class paramClass, Constructor paramConstructor) { return this.reflectionFactory.newConstructorForSerialization(paramClass, paramConstructor); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\corba\Bridge.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
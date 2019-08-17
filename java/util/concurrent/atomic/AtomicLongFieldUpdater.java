package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicLongFieldUpdater<T> extends Object {
  @CallerSensitive
  public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString) {
    Class clazz = Reflection.getCallerClass();
    return AtomicLong.VM_SUPPORTS_LONG_CAS ? new CASUpdater(paramClass, paramString, clazz) : new LockedUpdater(paramClass, paramString, clazz);
  }
  
  public abstract boolean compareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract boolean weakCompareAndSet(T paramT, long paramLong1, long paramLong2);
  
  public abstract void set(T paramT, long paramLong);
  
  public abstract void lazySet(T paramT, long paramLong);
  
  public abstract long get(T paramT);
  
  public long getAndSet(T paramT, long paramLong) {
    long l;
    do {
      l = get(paramT);
    } while (!compareAndSet(paramT, l, paramLong));
    return l;
  }
  
  public long getAndIncrement(T paramT) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 + 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long getAndDecrement(T paramT) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 - 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long getAndAdd(T paramT, long paramLong) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 + paramLong;
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public long incrementAndGet(T paramT) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 + 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public long decrementAndGet(T paramT) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 - 1L;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public long addAndGet(T paramT, long paramLong) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = l1 + paramLong;
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public final long getAndUpdate(T paramT, LongUnaryOperator paramLongUnaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public final long updateAndGet(T paramT, LongUnaryOperator paramLongUnaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = paramLongUnaryOperator.applyAsLong(l1);
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  public final long getAndAccumulate(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(paramT, l1, l2));
    return l1;
  }
  
  public final long accumulateAndGet(T paramT, long paramLong, LongBinaryOperator paramLongBinaryOperator) {
    long l2;
    long l1;
    do {
      l1 = get(paramT);
      l2 = paramLongBinaryOperator.applyAsLong(l1, paramLong);
    } while (!compareAndSet(paramT, l1, l2));
    return l2;
  }
  
  static boolean isAncestor(ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) {
    ClassLoader classLoader = paramClassLoader1;
    do {
      classLoader = classLoader.getParent();
      if (paramClassLoader2 == classLoader)
        return true; 
    } while (classLoader != null);
    return false;
  }
  
  private static boolean isSamePackage(Class<?> paramClass1, Class<?> paramClass2) { return (paramClass1.getClassLoader() == paramClass2.getClassLoader() && Objects.equals(getPackageName(paramClass1), getPackageName(paramClass2))); }
  
  private static String getPackageName(Class<?> paramClass) {
    String str = paramClass.getName();
    int i = str.lastIndexOf('.');
    return (i != -1) ? str.substring(0, i) : "";
  }
  
  private static final class CASUpdater<T> extends AtomicLongFieldUpdater<T> {
    private static final Unsafe U = Unsafe.getUnsafe();
    
    private final long offset;
    
    private final Class<?> cclass;
    
    private final Class<T> tclass;
    
    CASUpdater(final Class<T> tclass, final String fieldName, Class<?> param1Class2) {
      try {
        field = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
              public Field run() throws NoSuchFieldException { return tclass.getDeclaredField(fieldName); }
            });
        i = field.getModifiers();
        ReflectUtil.ensureMemberAccess(param1Class2, param1Class1, null, i);
        ClassLoader classLoader1 = param1Class1.getClassLoader();
        ClassLoader classLoader2 = param1Class2.getClassLoader();
        if (classLoader2 != null && classLoader2 != classLoader1 && (classLoader1 == null || !isAncestor(classLoader1, classLoader2)))
          ReflectUtil.checkPackageAccess(param1Class1); 
      } catch (PrivilegedActionException privilegedActionException) {
        throw new RuntimeException(privilegedActionException.getException());
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
      if (field.getType() != long.class)
        throw new IllegalArgumentException("Must be long type"); 
      if (!Modifier.isVolatile(i))
        throw new IllegalArgumentException("Must be volatile type"); 
      this.cclass = (Modifier.isProtected(i) && param1Class1.isAssignableFrom(param1Class2) && !AtomicLongFieldUpdater.isSamePackage(param1Class1, param1Class2)) ? param1Class2 : param1Class1;
      this.tclass = param1Class1;
      this.offset = U.objectFieldOffset(field);
    }
    
    private final void accessCheck(T param1T) {
      if (!this.cclass.isInstance(param1T))
        throwAccessCheckException(param1T); 
    }
    
    private final void throwAccessCheckException(T param1T) {
      if (this.cclass == this.tclass)
        throw new ClassCastException(); 
      throw new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + param1T.getClass().getName()));
    }
    
    public final boolean compareAndSet(T param1T, long param1Long1, long param1Long2) {
      accessCheck(param1T);
      return U.compareAndSwapLong(param1T, this.offset, param1Long1, param1Long2);
    }
    
    public final boolean weakCompareAndSet(T param1T, long param1Long1, long param1Long2) {
      accessCheck(param1T);
      return U.compareAndSwapLong(param1T, this.offset, param1Long1, param1Long2);
    }
    
    public final void set(T param1T, long param1Long) {
      accessCheck(param1T);
      U.putLongVolatile(param1T, this.offset, param1Long);
    }
    
    public final void lazySet(T param1T, long param1Long) {
      accessCheck(param1T);
      U.putOrderedLong(param1T, this.offset, param1Long);
    }
    
    public final long get(T param1T) {
      accessCheck(param1T);
      return U.getLongVolatile(param1T, this.offset);
    }
    
    public final long getAndSet(T param1T, long param1Long) {
      accessCheck(param1T);
      return U.getAndSetLong(param1T, this.offset, param1Long);
    }
    
    public final long getAndAdd(T param1T, long param1Long) {
      accessCheck(param1T);
      return U.getAndAddLong(param1T, this.offset, param1Long);
    }
    
    public final long getAndIncrement(T param1T) { return getAndAdd(param1T, 1L); }
    
    public final long getAndDecrement(T param1T) { return getAndAdd(param1T, -1L); }
    
    public final long incrementAndGet(T param1T) { return getAndAdd(param1T, 1L) + 1L; }
    
    public final long decrementAndGet(T param1T) { return getAndAdd(param1T, -1L) - 1L; }
    
    public final long addAndGet(T param1T, long param1Long) { return getAndAdd(param1T, param1Long) + param1Long; }
  }
  
  private static final class LockedUpdater<T> extends AtomicLongFieldUpdater<T> {
    private static final Unsafe U = Unsafe.getUnsafe();
    
    private final long offset;
    
    private final Class<?> cclass;
    
    private final Class<T> tclass;
    
    LockedUpdater(final Class<T> tclass, final String fieldName, Class<?> param1Class2) {
      Field field = null;
      int i = 0;
      try {
        field = (Field)AccessController.doPrivileged(new PrivilegedExceptionAction<Field>() {
              public Field run() throws NoSuchFieldException { return tclass.getDeclaredField(fieldName); }
            });
        i = field.getModifiers();
        ReflectUtil.ensureMemberAccess(param1Class2, param1Class1, null, i);
        ClassLoader classLoader1 = param1Class1.getClassLoader();
        ClassLoader classLoader2 = param1Class2.getClassLoader();
        if (classLoader2 != null && classLoader2 != classLoader1 && (classLoader1 == null || !isAncestor(classLoader1, classLoader2)))
          ReflectUtil.checkPackageAccess(param1Class1); 
      } catch (PrivilegedActionException privilegedActionException) {
        throw new RuntimeException(privilegedActionException.getException());
      } catch (Exception exception) {
        throw new RuntimeException(exception);
      } 
      if (field.getType() != long.class)
        throw new IllegalArgumentException("Must be long type"); 
      if (!Modifier.isVolatile(i))
        throw new IllegalArgumentException("Must be volatile type"); 
      this.cclass = (Modifier.isProtected(i) && param1Class1.isAssignableFrom(param1Class2) && !AtomicLongFieldUpdater.isSamePackage(param1Class1, param1Class2)) ? param1Class2 : param1Class1;
      this.tclass = param1Class1;
      this.offset = U.objectFieldOffset(field);
    }
    
    private final void accessCheck(T param1T) {
      if (!this.cclass.isInstance(param1T))
        throw accessCheckException(param1T); 
    }
    
    private final RuntimeException accessCheckException(T param1T) { return (this.cclass == this.tclass) ? new ClassCastException() : new RuntimeException(new IllegalAccessException("Class " + this.cclass.getName() + " can not access a protected member of class " + this.tclass.getName() + " using an instance of " + param1T.getClass().getName())); }
    
    public final boolean compareAndSet(T param1T, long param1Long1, long param1Long2) {
      accessCheck(param1T);
      synchronized (this) {
        long l = U.getLong(param1T, this.offset);
        if (l != param1Long1)
          return false; 
        U.putLong(param1T, this.offset, param1Long2);
        return true;
      } 
    }
    
    public final boolean weakCompareAndSet(T param1T, long param1Long1, long param1Long2) { return compareAndSet(param1T, param1Long1, param1Long2); }
    
    public final void set(T param1T, long param1Long) {
      accessCheck(param1T);
      synchronized (this) {
        U.putLong(param1T, this.offset, param1Long);
      } 
    }
    
    public final void lazySet(T param1T, long param1Long) { set(param1T, param1Long); }
    
    public final long get(T param1T) {
      accessCheck(param1T);
      synchronized (this) {
        return U.getLong(param1T, this.offset);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicLongFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
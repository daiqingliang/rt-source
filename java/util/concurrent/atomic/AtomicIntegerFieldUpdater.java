package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.misc.Unsafe;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public abstract class AtomicIntegerFieldUpdater<T> extends Object {
  @CallerSensitive
  public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> paramClass, String paramString) { return new AtomicIntegerFieldUpdaterImpl(paramClass, paramString, Reflection.getCallerClass()); }
  
  public abstract boolean compareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract boolean weakCompareAndSet(T paramT, int paramInt1, int paramInt2);
  
  public abstract void set(T paramT, int paramInt);
  
  public abstract void lazySet(T paramT, int paramInt);
  
  public abstract int get(T paramT);
  
  public int getAndSet(T paramT, int paramInt) {
    int i;
    do {
      i = get(paramT);
    } while (!compareAndSet(paramT, i, paramInt));
    return i;
  }
  
  public int getAndIncrement(T paramT) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i + 1;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int getAndDecrement(T paramT) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i - 1;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int getAndAdd(T paramT, int paramInt) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i + paramInt;
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public int incrementAndGet(T paramT) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i + 1;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public int decrementAndGet(T paramT) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i - 1;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public int addAndGet(T paramT, int paramInt) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = i + paramInt;
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public final int getAndUpdate(T paramT, IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public final int updateAndGet(T paramT, IntUnaryOperator paramIntUnaryOperator) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = paramIntUnaryOperator.applyAsInt(i);
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  public final int getAndAccumulate(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(paramT, i, j));
    return i;
  }
  
  public final int accumulateAndGet(T paramT, int paramInt, IntBinaryOperator paramIntBinaryOperator) {
    int j;
    int i;
    do {
      i = get(paramT);
      j = paramIntBinaryOperator.applyAsInt(i, paramInt);
    } while (!compareAndSet(paramT, i, j));
    return j;
  }
  
  private static final class AtomicIntegerFieldUpdaterImpl<T> extends AtomicIntegerFieldUpdater<T> {
    private static final Unsafe U = Unsafe.getUnsafe();
    
    private final long offset;
    
    private final Class<?> cclass;
    
    private final Class<T> tclass;
    
    AtomicIntegerFieldUpdaterImpl(final Class<T> tclass, final String fieldName, Class<?> param1Class2) {
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
      if (field.getType() != int.class)
        throw new IllegalArgumentException("Must be integer type"); 
      if (!Modifier.isVolatile(i))
        throw new IllegalArgumentException("Must be volatile type"); 
      this.cclass = (Modifier.isProtected(i) && param1Class1.isAssignableFrom(param1Class2) && !isSamePackage(param1Class1, param1Class2)) ? param1Class2 : param1Class1;
      this.tclass = param1Class1;
      this.offset = U.objectFieldOffset(field);
    }
    
    private static boolean isAncestor(ClassLoader param1ClassLoader1, ClassLoader param1ClassLoader2) {
      ClassLoader classLoader = param1ClassLoader1;
      do {
        classLoader = classLoader.getParent();
        if (param1ClassLoader2 == classLoader)
          return true; 
      } while (classLoader != null);
      return false;
    }
    
    private static boolean isSamePackage(Class<?> param1Class1, Class<?> param1Class2) { return (param1Class1.getClassLoader() == param1Class2.getClassLoader() && Objects.equals(getPackageName(param1Class1), getPackageName(param1Class2))); }
    
    private static String getPackageName(Class<?> param1Class) {
      String str = param1Class.getName();
      int i = str.lastIndexOf('.');
      return (i != -1) ? str.substring(0, i) : "";
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
    
    public final boolean compareAndSet(T param1T, int param1Int1, int param1Int2) {
      accessCheck(param1T);
      return U.compareAndSwapInt(param1T, this.offset, param1Int1, param1Int2);
    }
    
    public final boolean weakCompareAndSet(T param1T, int param1Int1, int param1Int2) {
      accessCheck(param1T);
      return U.compareAndSwapInt(param1T, this.offset, param1Int1, param1Int2);
    }
    
    public final void set(T param1T, int param1Int) {
      accessCheck(param1T);
      U.putIntVolatile(param1T, this.offset, param1Int);
    }
    
    public final void lazySet(T param1T, int param1Int) {
      accessCheck(param1T);
      U.putOrderedInt(param1T, this.offset, param1Int);
    }
    
    public final int get(T param1T) {
      accessCheck(param1T);
      return U.getIntVolatile(param1T, this.offset);
    }
    
    public final int getAndSet(T param1T, int param1Int) {
      accessCheck(param1T);
      return U.getAndSetInt(param1T, this.offset, param1Int);
    }
    
    public final int getAndAdd(T param1T, int param1Int) {
      accessCheck(param1T);
      return U.getAndAddInt(param1T, this.offset, param1Int);
    }
    
    public final int getAndIncrement(T param1T) { return getAndAdd(param1T, 1); }
    
    public final int getAndDecrement(T param1T) { return getAndAdd(param1T, -1); }
    
    public final int incrementAndGet(T param1T) { return getAndAdd(param1T, 1) + 1; }
    
    public final int decrementAndGet(T param1T) { return getAndAdd(param1T, -1) - 1; }
    
    public final int addAndGet(T param1T, int param1Int) { return getAndAdd(param1T, param1Int) + param1Int; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\concurrent\atomic\AtomicIntegerFieldUpdater.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
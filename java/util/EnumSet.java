package java.util;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import sun.misc.SharedSecrets;

public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable, Serializable {
  final Class<E> elementType;
  
  final Enum<?>[] universe;
  
  private static Enum<?>[] ZERO_LENGTH_ENUM_ARRAY = new Enum[0];
  
  EnumSet(Class<E> paramClass, Enum<?>[] paramArrayOfEnum) {
    this.elementType = paramClass;
    this.universe = paramArrayOfEnum;
  }
  
  public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> paramClass) {
    Enum[] arrayOfEnum = getUniverse(paramClass);
    if (arrayOfEnum == null)
      throw new ClassCastException(paramClass + " not an enum"); 
    return (arrayOfEnum.length <= 64) ? new RegularEnumSet(paramClass, arrayOfEnum) : new JumboEnumSet(paramClass, arrayOfEnum);
  }
  
  public static <E extends Enum<E>> EnumSet<E> allOf(Class<E> paramClass) {
    EnumSet enumSet = noneOf(paramClass);
    enumSet.addAll();
    return enumSet;
  }
  
  abstract void addAll();
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(EnumSet<E> paramEnumSet) { return paramEnumSet.clone(); }
  
  public static <E extends Enum<E>> EnumSet<E> copyOf(Collection<E> paramCollection) {
    if (paramCollection instanceof EnumSet)
      return ((EnumSet)paramCollection).clone(); 
    if (paramCollection.isEmpty())
      throw new IllegalArgumentException("Collection is empty"); 
    Iterator iterator = paramCollection.iterator();
    Enum enum = (Enum)iterator.next();
    EnumSet enumSet = of(enum);
    while (iterator.hasNext())
      enumSet.add(iterator.next()); 
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> complementOf(EnumSet<E> paramEnumSet) {
    EnumSet enumSet = copyOf(paramEnumSet);
    enumSet.complement();
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE) {
    EnumSet enumSet = noneOf(paramE.getDeclaringClass());
    enumSet.add(paramE);
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2) {
    EnumSet enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3) {
    EnumSet enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4) {
    EnumSet enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    enumSet.add(paramE4);
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> of(E paramE1, E paramE2, E paramE3, E paramE4, E paramE5) {
    EnumSet enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.add(paramE1);
    enumSet.add(paramE2);
    enumSet.add(paramE3);
    enumSet.add(paramE4);
    enumSet.add(paramE5);
    return enumSet;
  }
  
  @SafeVarargs
  public static <E extends Enum<E>> EnumSet<E> of(E paramE, E... paramVarArgs) {
    EnumSet enumSet = noneOf(paramE.getDeclaringClass());
    enumSet.add(paramE);
    for (E e : paramVarArgs)
      enumSet.add(e); 
    return enumSet;
  }
  
  public static <E extends Enum<E>> EnumSet<E> range(E paramE1, E paramE2) {
    if (paramE1.compareTo(paramE2) > 0)
      throw new IllegalArgumentException(paramE1 + " > " + paramE2); 
    EnumSet enumSet = noneOf(paramE1.getDeclaringClass());
    enumSet.addRange(paramE1, paramE2);
    return enumSet;
  }
  
  abstract void addRange(E paramE1, E paramE2);
  
  public EnumSet<E> clone() {
    try {
      return (EnumSet)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new AssertionError(cloneNotSupportedException);
    } 
  }
  
  abstract void complement();
  
  final void typeCheck(E paramE) {
    Class clazz = paramE.getClass();
    if (clazz != this.elementType && clazz.getSuperclass() != this.elementType)
      throw new ClassCastException(clazz + " != " + this.elementType); 
  }
  
  private static <E extends Enum<E>> E[] getUniverse(Class<E> paramClass) { return (E[])SharedSecrets.getJavaLangAccess().getEnumConstantsShared(paramClass); }
  
  Object writeReplace() { return new SerializationProxy(this); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws InvalidObjectException { throw new InvalidObjectException("Proxy required"); }
  
  private static class SerializationProxy<E extends Enum<E>> extends Object implements Serializable {
    private final Class<E> elementType;
    
    private final Enum<?>[] elements;
    
    private static final long serialVersionUID = 362491234563181265L;
    
    SerializationProxy(EnumSet<E> param1EnumSet) {
      this.elementType = param1EnumSet.elementType;
      this.elements = (Enum[])param1EnumSet.toArray(ZERO_LENGTH_ENUM_ARRAY);
    }
    
    private Object readResolve() {
      EnumSet enumSet = EnumSet.noneOf(this.elementType);
      for (Enum enum : this.elements)
        enumSet.add(enum); 
      return enumSet;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\EnumSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
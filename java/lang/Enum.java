package java.lang;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public abstract class Enum<E extends Enum<E>> extends Object implements Comparable<E>, Serializable {
  private final String name;
  
  private final int ordinal;
  
  public final String name() { return this.name; }
  
  public final int ordinal() { return this.ordinal; }
  
  protected Enum(String paramString, int paramInt) {
    this.name = paramString;
    this.ordinal = paramInt;
  }
  
  public String toString() { return this.name; }
  
  public final boolean equals(Object paramObject) { return (this == paramObject); }
  
  public final int hashCode() { return super.hashCode(); }
  
  protected final Object clone() throws CloneNotSupportedException { throw new CloneNotSupportedException(); }
  
  public final int compareTo(E paramE) {
    E e = paramE;
    Enum enum = this;
    if (enum.getClass() != e.getClass() && enum.getDeclaringClass() != e.getDeclaringClass())
      throw new ClassCastException(); 
    return enum.ordinal - e.ordinal;
  }
  
  public final Class<E> getDeclaringClass() {
    Class clazz1 = getClass();
    Class clazz2 = clazz1.getSuperclass();
    return (clazz2 == Enum.class) ? clazz1 : clazz2;
  }
  
  public static <T extends Enum<T>> T valueOf(Class<T> paramClass, String paramString) {
    Enum enum = (Enum)paramClass.enumConstantDirectory().get(paramString);
    if (enum != null)
      return (T)enum; 
    if (paramString == null)
      throw new NullPointerException("Name is null"); 
    throw new IllegalArgumentException("No enum constant " + paramClass.getCanonicalName() + "." + paramString);
  }
  
  protected final void finalize() {}
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException { throw new InvalidObjectException("can't deserialize enum"); }
  
  private void readObjectNoData() { throw new InvalidObjectException("can't deserialize enum"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Enum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
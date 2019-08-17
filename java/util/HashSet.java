package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import sun.misc.SharedSecrets;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
  static final long serialVersionUID = -5024744406713321676L;
  
  private HashMap<E, Object> map = new HashMap();
  
  private static final Object PRESENT = new Object();
  
  public HashSet() {}
  
  public HashSet(Collection<? extends E> paramCollection) { addAll(paramCollection); }
  
  public HashSet(int paramInt, float paramFloat) {}
  
  public HashSet(int paramInt) {}
  
  HashSet(int paramInt, float paramFloat, boolean paramBoolean) {}
  
  public Iterator<E> iterator() { return this.map.keySet().iterator(); }
  
  public int size() { return this.map.size(); }
  
  public boolean isEmpty() { return this.map.isEmpty(); }
  
  public boolean contains(Object paramObject) { return this.map.containsKey(paramObject); }
  
  public boolean add(E paramE) { return (this.map.put(paramE, PRESENT) == null); }
  
  public boolean remove(Object paramObject) { return (this.map.remove(paramObject) == PRESENT); }
  
  public void clear() { this.map.clear(); }
  
  public Object clone() {
    try {
      HashSet hashSet = (HashSet)super.clone();
      hashSet.map = (HashMap)this.map.clone();
      return hashSet;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.map.capacity());
    paramObjectOutputStream.writeFloat(this.map.loadFactor());
    paramObjectOutputStream.writeInt(this.map.size());
    for (Object object : this.map.keySet())
      paramObjectOutputStream.writeObject(object); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i < 0)
      throw new InvalidObjectException("Illegal capacity: " + i); 
    float f = paramObjectInputStream.readFloat();
    if (f <= 0.0F || Float.isNaN(f))
      throw new InvalidObjectException("Illegal load factor: " + f); 
    int j = paramObjectInputStream.readInt();
    if (j < 0)
      throw new InvalidObjectException("Illegal size: " + j); 
    i = (int)Math.min(j * Math.min(1.0F / f, 4.0F), 1.07374182E9F);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Entry[].class, HashMap.tableSizeFor(i));
    this.map = (this instanceof LinkedHashSet) ? new LinkedHashMap(i, f) : new HashMap(i, f);
    for (byte b = 0; b < j; b++) {
      Object object = paramObjectInputStream.readObject();
      this.map.put(object, PRESENT);
    } 
  }
  
  public Spliterator<E> spliterator() { return new HashMap.KeySpliterator(this.map, 0, -1, 0, 0); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\HashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.print.attribute;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class HashAttributeSet implements AttributeSet, Serializable {
  private static final long serialVersionUID = 5311560590283707917L;
  
  private Class myInterface;
  
  private HashMap attrMap = new HashMap();
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    Attribute[] arrayOfAttribute = toArray();
    paramObjectOutputStream.writeInt(arrayOfAttribute.length);
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      paramObjectOutputStream.writeObject(arrayOfAttribute[b]); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    this.attrMap = new HashMap();
    int i = paramObjectInputStream.readInt();
    for (byte b = 0; b < i; b++) {
      Attribute attribute = (Attribute)paramObjectInputStream.readObject();
      add(attribute);
    } 
  }
  
  public HashAttributeSet() { this(Attribute.class); }
  
  public HashAttributeSet(Attribute paramAttribute) { this(paramAttribute, Attribute.class); }
  
  public HashAttributeSet(Attribute[] paramArrayOfAttribute) { this(paramArrayOfAttribute, Attribute.class); }
  
  public HashAttributeSet(AttributeSet paramAttributeSet) { this(paramAttributeSet, Attribute.class); }
  
  protected HashAttributeSet(Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null interface"); 
    this.myInterface = paramClass;
  }
  
  protected HashAttributeSet(Attribute paramAttribute, Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null interface"); 
    this.myInterface = paramClass;
    add(paramAttribute);
  }
  
  protected HashAttributeSet(Attribute[] paramArrayOfAttribute, Class<?> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null interface"); 
    this.myInterface = paramClass;
    boolean bool = (paramArrayOfAttribute == null) ? 0 : paramArrayOfAttribute.length;
    for (byte b = 0; b < bool; b++)
      add(paramArrayOfAttribute[b]); 
  }
  
  protected HashAttributeSet(AttributeSet paramAttributeSet, Class<?> paramClass) {
    this.myInterface = paramClass;
    if (paramAttributeSet != null) {
      Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
      boolean bool = (arrayOfAttribute == null) ? 0 : arrayOfAttribute.length;
      for (byte b = 0; b < bool; b++)
        add(arrayOfAttribute[b]); 
    } 
  }
  
  public Attribute get(Class<?> paramClass) { return (Attribute)this.attrMap.get(AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class)); }
  
  public boolean add(Attribute paramAttribute) {
    Object object = this.attrMap.put(paramAttribute.getCategory(), AttributeSetUtilities.verifyAttributeValue(paramAttribute, this.myInterface));
    return !paramAttribute.equals(object);
  }
  
  public boolean remove(Class<?> paramClass) { return (paramClass != null && AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class) != null && this.attrMap.remove(paramClass) != null); }
  
  public boolean remove(Attribute paramAttribute) { return (paramAttribute != null && this.attrMap.remove(paramAttribute.getCategory()) != null); }
  
  public boolean containsKey(Class<?> paramClass) { return (paramClass != null && AttributeSetUtilities.verifyAttributeCategory(paramClass, Attribute.class) != null && this.attrMap.get(paramClass) != null); }
  
  public boolean containsValue(Attribute paramAttribute) { return (paramAttribute != null && paramAttribute instanceof Attribute && paramAttribute.equals(this.attrMap.get(paramAttribute.getCategory()))); }
  
  public boolean addAll(AttributeSet paramAttributeSet) {
    Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
    boolean bool = false;
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      Attribute attribute = AttributeSetUtilities.verifyAttributeValue(arrayOfAttribute[b], this.myInterface);
      Object object = this.attrMap.put(attribute.getCategory(), attribute);
      bool = (!attribute.equals(object) || bool);
    } 
    return bool;
  }
  
  public int size() { return this.attrMap.size(); }
  
  public Attribute[] toArray() {
    Attribute[] arrayOfAttribute = new Attribute[size()];
    this.attrMap.values().toArray(arrayOfAttribute);
    return arrayOfAttribute;
  }
  
  public void clear() { this.attrMap.clear(); }
  
  public boolean isEmpty() { return this.attrMap.isEmpty(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof AttributeSet))
      return false; 
    AttributeSet attributeSet = (AttributeSet)paramObject;
    if (attributeSet.size() != size())
      return false; 
    Attribute[] arrayOfAttribute = toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      if (!attributeSet.containsValue(arrayOfAttribute[b]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 0;
    Attribute[] arrayOfAttribute = toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++)
      i += arrayOfAttribute[b].hashCode(); 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\HashAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
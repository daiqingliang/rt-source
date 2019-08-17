package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class SimpleAttributeSet implements MutableAttributeSet, Serializable, Cloneable {
  private static final long serialVersionUID = -6631553454711782652L;
  
  public static final AttributeSet EMPTY = new EmptyAttributeSet();
  
  private LinkedHashMap<Object, Object> table = new LinkedHashMap(3);
  
  public SimpleAttributeSet() {}
  
  public SimpleAttributeSet(AttributeSet paramAttributeSet) { addAttributes(paramAttributeSet); }
  
  public boolean isEmpty() { return this.table.isEmpty(); }
  
  public int getAttributeCount() { return this.table.size(); }
  
  public boolean isDefined(Object paramObject) { return this.table.containsKey(paramObject); }
  
  public boolean isEqual(AttributeSet paramAttributeSet) { return (getAttributeCount() == paramAttributeSet.getAttributeCount() && containsAttributes(paramAttributeSet)); }
  
  public AttributeSet copyAttributes() { return (AttributeSet)clone(); }
  
  public Enumeration<?> getAttributeNames() { return Collections.enumeration(this.table.keySet()); }
  
  public Object getAttribute(Object paramObject) {
    Object object = this.table.get(paramObject);
    if (object == null) {
      AttributeSet attributeSet = getResolveParent();
      if (attributeSet != null)
        object = attributeSet.getAttribute(paramObject); 
    } 
    return object;
  }
  
  public boolean containsAttribute(Object paramObject1, Object paramObject2) { return paramObject2.equals(getAttribute(paramObject1)); }
  
  public boolean containsAttributes(AttributeSet paramAttributeSet) {
    boolean bool = true;
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (bool && enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      bool = paramAttributeSet.getAttribute(object).equals(getAttribute(object));
    } 
    return bool;
  }
  
  public void addAttribute(Object paramObject1, Object paramObject2) { this.table.put(paramObject1, paramObject2); }
  
  public void addAttributes(AttributeSet paramAttributeSet) {
    Enumeration enumeration = paramAttributeSet.getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      addAttribute(object, paramAttributeSet.getAttribute(object));
    } 
  }
  
  public void removeAttribute(Object paramObject) { this.table.remove(paramObject); }
  
  public void removeAttributes(Enumeration<?> paramEnumeration) {
    while (paramEnumeration.hasMoreElements())
      removeAttribute(paramEnumeration.nextElement()); 
  }
  
  public void removeAttributes(AttributeSet paramAttributeSet) {
    if (paramAttributeSet == this) {
      this.table.clear();
    } else {
      Enumeration enumeration = paramAttributeSet.getAttributeNames();
      while (enumeration.hasMoreElements()) {
        Object object1 = enumeration.nextElement();
        Object object2 = paramAttributeSet.getAttribute(object1);
        if (object2.equals(getAttribute(object1)))
          removeAttribute(object1); 
      } 
    } 
  }
  
  public AttributeSet getResolveParent() { return (AttributeSet)this.table.get(StyleConstants.ResolveAttribute); }
  
  public void setResolveParent(AttributeSet paramAttributeSet) { addAttribute(StyleConstants.ResolveAttribute, paramAttributeSet); }
  
  public Object clone() {
    Object object;
    try {
      object = (SimpleAttributeSet)super.clone();
      object.table = (LinkedHashMap)this.table.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      object = null;
    } 
    return object;
  }
  
  public int hashCode() { return this.table.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof AttributeSet) {
      AttributeSet attributeSet = (AttributeSet)paramObject;
      return isEqual(attributeSet);
    } 
    return false;
  }
  
  public String toString() {
    String str = "";
    Enumeration enumeration = getAttributeNames();
    while (enumeration.hasMoreElements()) {
      Object object1 = enumeration.nextElement();
      Object object2 = getAttribute(object1);
      if (object2 instanceof AttributeSet) {
        str = str + object1 + "=**AttributeSet** ";
        continue;
      } 
      str = str + object1 + "=" + object2 + " ";
    } 
    return str;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    StyleContext.writeAttributeSet(paramObjectOutputStream, this);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    paramObjectInputStream.defaultReadObject();
    this.table = new LinkedHashMap(3);
    StyleContext.readAttributeSet(paramObjectInputStream, this);
  }
  
  static class EmptyAttributeSet implements AttributeSet, Serializable {
    static final long serialVersionUID = -8714803568785904228L;
    
    public int getAttributeCount() { return 0; }
    
    public boolean isDefined(Object param1Object) { return false; }
    
    public boolean isEqual(AttributeSet param1AttributeSet) { return (param1AttributeSet.getAttributeCount() == 0); }
    
    public AttributeSet copyAttributes() { return this; }
    
    public Object getAttribute(Object param1Object) { return null; }
    
    public Enumeration getAttributeNames() { return Collections.emptyEnumeration(); }
    
    public boolean containsAttribute(Object param1Object1, Object param1Object2) { return false; }
    
    public boolean containsAttributes(AttributeSet param1AttributeSet) { return (param1AttributeSet.getAttributeCount() == 0); }
    
    public AttributeSet getResolveParent() { return null; }
    
    public boolean equals(Object param1Object) { return (this == param1Object) ? true : ((param1Object instanceof AttributeSet && ((AttributeSet)param1Object).getAttributeCount() == 0)); }
    
    public int hashCode() { return 0; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\SimpleAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;

public class BasicAttribute implements Attribute {
  protected String attrID;
  
  protected Vector<Object> values;
  
  protected boolean ordered = false;
  
  private static final long serialVersionUID = 6743528196119291326L;
  
  public Object clone() {
    BasicAttribute basicAttribute;
    try {
      basicAttribute = (BasicAttribute)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      basicAttribute = new BasicAttribute(this.attrID, this.ordered);
    } 
    basicAttribute.values = (Vector)this.values.clone();
    return basicAttribute;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Attribute) {
      Attribute attribute = (Attribute)paramObject;
      if (isOrdered() != attribute.isOrdered())
        return false; 
      int i;
      if (this.attrID.equals(attribute.getID()) && (i = size()) == attribute.size()) {
        try {
          if (isOrdered()) {
            for (byte b = 0; b < i; b++) {
              if (!valueEquals(get(b), attribute.get(b)))
                return false; 
            } 
          } else {
            NamingEnumeration namingEnumeration = attribute.getAll();
            while (namingEnumeration.hasMoreElements()) {
              if (find(namingEnumeration.nextElement()) < 0)
                return false; 
            } 
          } 
        } catch (NamingException namingException) {
          return false;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.attrID.hashCode();
    int j = this.values.size();
    for (byte b = 0; b < j; b++) {
      Object object = this.values.elementAt(b);
      if (object != null)
        if (object.getClass().isArray()) {
          int k = Array.getLength(object);
          for (byte b1 = 0; b1 < k; b1++) {
            Object object1 = Array.get(object, b1);
            if (object1 != null)
              i += object1.hashCode(); 
          } 
        } else {
          i += object.hashCode();
        }  
    } 
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer(this.attrID + ": ");
    if (this.values.size() == 0) {
      stringBuffer.append("No values");
    } else {
      boolean bool = true;
      Enumeration enumeration = this.values.elements();
      while (enumeration.hasMoreElements()) {
        if (!bool)
          stringBuffer.append(", "); 
        stringBuffer.append(enumeration.nextElement());
        bool = false;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public BasicAttribute(String paramString) { this(paramString, false); }
  
  public BasicAttribute(String paramString, Object paramObject) { this(paramString, paramObject, false); }
  
  public BasicAttribute(String paramString, boolean paramBoolean) {
    this.attrID = paramString;
    this.values = new Vector();
    this.ordered = paramBoolean;
  }
  
  public BasicAttribute(String paramString, Object paramObject, boolean paramBoolean) {
    this(paramString, paramBoolean);
    this.values.addElement(paramObject);
  }
  
  public NamingEnumeration<?> getAll() throws NamingException { return new ValuesEnumImpl(); }
  
  public Object get() {
    if (this.values.size() == 0)
      throw new NoSuchElementException("Attribute " + getID() + " has no value"); 
    return this.values.elementAt(0);
  }
  
  public int size() { return this.values.size(); }
  
  public String getID() { return this.attrID; }
  
  public boolean contains(Object paramObject) { return (find(paramObject) >= 0); }
  
  private int find(Object paramObject) {
    if (paramObject == null) {
      int i = this.values.size();
      for (byte b = 0; b < i; b++) {
        if (this.values.elementAt(b) == null)
          return b; 
      } 
    } else {
      Class clazz;
      if ((clazz = paramObject.getClass()).isArray()) {
        int i = this.values.size();
        for (byte b = 0; b < i; b++) {
          Object object = this.values.elementAt(b);
          if (object != null && clazz == object.getClass() && arrayEquals(paramObject, object))
            return b; 
        } 
      } else {
        return this.values.indexOf(paramObject, 0);
      } 
    } 
    return -1;
  }
  
  private static boolean valueEquals(Object paramObject1, Object paramObject2) { return (paramObject1 == paramObject2) ? true : ((paramObject1 == null) ? false : ((paramObject1.getClass().isArray() && paramObject2.getClass().isArray()) ? arrayEquals(paramObject1, paramObject2) : paramObject1.equals(paramObject2))); }
  
  private static boolean arrayEquals(Object paramObject1, Object paramObject2) {
    int i;
    if ((i = Array.getLength(paramObject1)) != Array.getLength(paramObject2))
      return false; 
    for (byte b = 0; b < i; b++) {
      Object object1 = Array.get(paramObject1, b);
      Object object2 = Array.get(paramObject2, b);
      if (object1 == null || object2 == null) {
        if (object1 != object2)
          return false; 
      } else if (!object1.equals(object2)) {
        return false;
      } 
    } 
    return true;
  }
  
  public boolean add(Object paramObject) {
    if (isOrdered() || find(paramObject) < 0) {
      this.values.addElement(paramObject);
      return true;
    } 
    return false;
  }
  
  public boolean remove(Object paramObject) {
    int i = find(paramObject);
    if (i >= 0) {
      this.values.removeElementAt(i);
      return true;
    } 
    return false;
  }
  
  public void clear() { this.values.setSize(0); }
  
  public boolean isOrdered() { return this.ordered; }
  
  public Object get(int paramInt) throws NamingException { return this.values.elementAt(paramInt); }
  
  public Object remove(int paramInt) throws NamingException {
    Object object = this.values.elementAt(paramInt);
    this.values.removeElementAt(paramInt);
    return object;
  }
  
  public void add(int paramInt, Object paramObject) {
    if (!isOrdered() && contains(paramObject))
      throw new IllegalStateException("Cannot add duplicate to unordered attribute"); 
    this.values.insertElementAt(paramObject, paramInt);
  }
  
  public Object set(int paramInt, Object paramObject) {
    if (!isOrdered() && contains(paramObject))
      throw new IllegalStateException("Cannot add duplicate to unordered attribute"); 
    Object object = this.values.elementAt(paramInt);
    this.values.setElementAt(paramObject, paramInt);
    return object;
  }
  
  public DirContext getAttributeSyntaxDefinition() throws NamingException { throw new OperationNotSupportedException("attribute syntax"); }
  
  public DirContext getAttributeDefinition() throws NamingException { throw new OperationNotSupportedException("attribute definition"); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.values.size());
    for (byte b = 0; b < this.values.size(); b++)
      paramObjectOutputStream.writeObject(this.values.elementAt(b)); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    this.values = new Vector(Math.min(1024, i));
    while (--i >= 0)
      this.values.addElement(paramObjectInputStream.readObject()); 
  }
  
  class ValuesEnumImpl extends Object implements NamingEnumeration<Object> {
    Enumeration<Object> list = BasicAttribute.this.values.elements();
    
    public boolean hasMoreElements() { return this.list.hasMoreElements(); }
    
    public Object nextElement() { return this.list.nextElement(); }
    
    public Object next() { return this.list.nextElement(); }
    
    public boolean hasMore() { return this.list.hasMoreElements(); }
    
    public void close() { this.list = null; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\directory\BasicAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
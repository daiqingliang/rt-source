package javax.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AttributeList extends ArrayList<Object> {
  private static final long serialVersionUID = -4077085769279709076L;
  
  public AttributeList() {}
  
  public AttributeList(int paramInt) { super(paramInt); }
  
  public AttributeList(AttributeList paramAttributeList) { super(paramAttributeList); }
  
  public AttributeList(List<Attribute> paramList) {
    if (paramList == null)
      throw new IllegalArgumentException("Null parameter"); 
    adding(paramList);
    super.addAll(paramList);
  }
  
  public List<Attribute> asList() {
    this.typeSafe = true;
    if (this.tainted)
      adding(this); 
    return this;
  }
  
  public void add(Attribute paramAttribute) { super.add(paramAttribute); }
  
  public void add(int paramInt, Attribute paramAttribute) {
    try {
      super.add(paramInt, paramAttribute);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new RuntimeOperationsException(indexOutOfBoundsException, "The specified index is out of range");
    } 
  }
  
  public void set(int paramInt, Attribute paramAttribute) {
    try {
      super.set(paramInt, paramAttribute);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new RuntimeOperationsException(indexOutOfBoundsException, "The specified index is out of range");
    } 
  }
  
  public boolean addAll(AttributeList paramAttributeList) { return super.addAll(paramAttributeList); }
  
  public boolean addAll(int paramInt, AttributeList paramAttributeList) {
    try {
      return super.addAll(paramInt, paramAttributeList);
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new RuntimeOperationsException(indexOutOfBoundsException, "The specified index is out of range");
    } 
  }
  
  public boolean add(Object paramObject) {
    adding(paramObject);
    return super.add(paramObject);
  }
  
  public void add(int paramInt, Object paramObject) {
    adding(paramObject);
    super.add(paramInt, paramObject);
  }
  
  public boolean addAll(Collection<?> paramCollection) {
    adding(paramCollection);
    return super.addAll(paramCollection);
  }
  
  public boolean addAll(int paramInt, Collection<?> paramCollection) {
    adding(paramCollection);
    return super.addAll(paramInt, paramCollection);
  }
  
  public Object set(int paramInt, Object paramObject) {
    adding(paramObject);
    return super.set(paramInt, paramObject);
  }
  
  private void adding(Object paramObject) {
    if (paramObject == null || paramObject instanceof Attribute)
      return; 
    if (this.typeSafe)
      throw new IllegalArgumentException("Not an Attribute: " + paramObject); 
    this.tainted = true;
  }
  
  private void adding(Collection<?> paramCollection) {
    for (Object object : paramCollection)
      adding(object); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\AttributeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
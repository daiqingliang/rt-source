package javax.management;

import java.io.Serializable;

public class Attribute implements Serializable {
  private static final long serialVersionUID = 2484220110589082382L;
  
  private String name;
  
  private Object value = null;
  
  public Attribute(String paramString, Object paramObject) {
    if (paramString == null)
      throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null ")); 
    this.name = paramString;
    this.value = paramObject;
  }
  
  public String getName() { return this.name; }
  
  public Object getValue() { return this.value; }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Attribute))
      return false; 
    Attribute attribute = (Attribute)paramObject;
    return (this.value == null) ? ((attribute.getValue() == null) ? this.name.equals(attribute.getName()) : 0) : ((this.name.equals(attribute.getName()) && this.value.equals(attribute.getValue())) ? 1 : 0);
  }
  
  public int hashCode() { return this.name.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode()); }
  
  public String toString() { return getName() + " = " + getValue(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
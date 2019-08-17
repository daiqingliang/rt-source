package java.text;

import java.io.InvalidObjectException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface AttributedCharacterIterator extends CharacterIterator {
  int getRunStart();
  
  int getRunStart(Attribute paramAttribute);
  
  int getRunStart(Set<? extends Attribute> paramSet);
  
  int getRunLimit();
  
  int getRunLimit(Attribute paramAttribute);
  
  int getRunLimit(Set<? extends Attribute> paramSet);
  
  Map<Attribute, Object> getAttributes();
  
  Object getAttribute(Attribute paramAttribute);
  
  Set<Attribute> getAllAttributeKeys();
  
  public static class Attribute implements Serializable {
    private String name;
    
    private static final Map<String, Attribute> instanceMap = new HashMap(7);
    
    public static final Attribute LANGUAGE = new Attribute("language");
    
    public static final Attribute READING = new Attribute("reading");
    
    public static final Attribute INPUT_METHOD_SEGMENT = new Attribute("input_method_segment");
    
    private static final long serialVersionUID = -9142742483513960612L;
    
    protected Attribute(String param1String) {
      this.name = param1String;
      if (getClass() == Attribute.class)
        instanceMap.put(param1String, this); 
    }
    
    public final boolean equals(Object param1Object) { return super.equals(param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public String toString() { return getClass().getName() + "(" + this.name + ")"; }
    
    protected String getName() { return this.name; }
    
    protected Object readResolve() throws InvalidObjectException {
      if (getClass() != Attribute.class)
        throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
      Attribute attribute = (Attribute)instanceMap.get(getName());
      if (attribute != null)
        return attribute; 
      throw new InvalidObjectException("unknown attribute name");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\AttributedCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
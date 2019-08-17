package java.text;

import java.util.Map;

class AttributeEntry extends Object implements Map.Entry<AttributedCharacterIterator.Attribute, Object> {
  private AttributedCharacterIterator.Attribute key;
  
  private Object value;
  
  AttributeEntry(AttributedCharacterIterator.Attribute paramAttribute, Object paramObject) {
    this.key = paramAttribute;
    this.value = paramObject;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof AttributeEntry))
      return false; 
    AttributeEntry attributeEntry = (AttributeEntry)paramObject;
    return (attributeEntry.key.equals(this.key) && ((this.value == null) ? (attributeEntry.value == null) : attributeEntry.value.equals(this.value)));
  }
  
  public AttributedCharacterIterator.Attribute getKey() { return this.key; }
  
  public Object getValue() { return this.value; }
  
  public Object setValue(Object paramObject) { throw new UnsupportedOperationException(); }
  
  public int hashCode() { return this.key.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode()); }
  
  public String toString() { return this.key.toString() + "=" + this.value.toString(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\AttributeEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
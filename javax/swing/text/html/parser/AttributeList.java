package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class AttributeList implements DTDConstants, Serializable {
  public String name;
  
  public int type;
  
  public Vector<?> values;
  
  public int modifier;
  
  public String value;
  
  public AttributeList next;
  
  static Hashtable<Object, Object> attributeTypes = new Hashtable();
  
  AttributeList() {}
  
  public AttributeList(String paramString) { this.name = paramString; }
  
  public AttributeList(String paramString1, int paramInt1, int paramInt2, String paramString2, Vector<?> paramVector, AttributeList paramAttributeList) {
    this.name = paramString1;
    this.type = paramInt1;
    this.modifier = paramInt2;
    this.value = paramString2;
    this.values = paramVector;
    this.next = paramAttributeList;
  }
  
  public String getName() { return this.name; }
  
  public int getType() { return this.type; }
  
  public int getModifier() { return this.modifier; }
  
  public Enumeration<?> getValues() { return (this.values != null) ? this.values.elements() : null; }
  
  public String getValue() { return this.value; }
  
  public AttributeList getNext() { return this.next; }
  
  public String toString() { return this.name; }
  
  static void defineAttributeType(String paramString, int paramInt) {
    Integer integer = Integer.valueOf(paramInt);
    attributeTypes.put(paramString, integer);
    attributeTypes.put(integer, paramString);
  }
  
  public static int name2type(String paramString) {
    Integer integer = (Integer)attributeTypes.get(paramString);
    return (integer == null) ? 1 : integer.intValue();
  }
  
  public static String type2name(int paramInt) { return (String)attributeTypes.get(Integer.valueOf(paramInt)); }
  
  static  {
    defineAttributeType("CDATA", 1);
    defineAttributeType("ENTITY", 2);
    defineAttributeType("ENTITIES", 3);
    defineAttributeType("ID", 4);
    defineAttributeType("IDREF", 5);
    defineAttributeType("IDREFS", 6);
    defineAttributeType("NAME", 7);
    defineAttributeType("NAMES", 8);
    defineAttributeType("NMTOKEN", 9);
    defineAttributeType("NMTOKENS", 10);
    defineAttributeType("NOTATION", 11);
    defineAttributeType("NUMBER", 12);
    defineAttributeType("NUMBERS", 13);
    defineAttributeType("NUTOKEN", 14);
    defineAttributeType("NUTOKENS", 15);
    attributeTypes.put("fixed", Integer.valueOf(1));
    attributeTypes.put("required", Integer.valueOf(2));
    attributeTypes.put("current", Integer.valueOf(3));
    attributeTypes.put("conref", Integer.valueOf(4));
    attributeTypes.put("implied", Integer.valueOf(5));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\AttributeList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
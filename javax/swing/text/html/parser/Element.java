package javax.swing.text.html.parser;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Hashtable;
import sun.awt.AppContext;

public final class Element implements DTDConstants, Serializable {
  public int index;
  
  public String name;
  
  public boolean oStart;
  
  public boolean oEnd;
  
  public BitSet inclusions;
  
  public BitSet exclusions;
  
  public int type = 19;
  
  public ContentModel content;
  
  public AttributeList atts;
  
  public Object data;
  
  private static final Object MAX_INDEX_KEY = new Object();
  
  static Hashtable<String, Integer> contentTypes = new Hashtable();
  
  Element() {}
  
  Element(String paramString, int paramInt) {
    this.name = paramString;
    this.index = paramInt;
    if (paramInt > getMaxIndex())
      AppContext.getAppContext().put(MAX_INDEX_KEY, Integer.valueOf(paramInt)); 
  }
  
  static int getMaxIndex() {
    Integer integer = (Integer)AppContext.getAppContext().get(MAX_INDEX_KEY);
    return (integer != null) ? integer.intValue() : 0;
  }
  
  public String getName() { return this.name; }
  
  public boolean omitStart() { return this.oStart; }
  
  public boolean omitEnd() { return this.oEnd; }
  
  public int getType() { return this.type; }
  
  public ContentModel getContent() { return this.content; }
  
  public AttributeList getAttributes() { return this.atts; }
  
  public int getIndex() { return this.index; }
  
  public boolean isEmpty() { return (this.type == 17); }
  
  public String toString() { return this.name; }
  
  public AttributeList getAttribute(String paramString) {
    for (AttributeList attributeList = this.atts; attributeList != null; attributeList = attributeList.next) {
      if (attributeList.name.equals(paramString))
        return attributeList; 
    } 
    return null;
  }
  
  public AttributeList getAttributeByValue(String paramString) {
    for (AttributeList attributeList = this.atts; attributeList != null; attributeList = attributeList.next) {
      if (attributeList.values != null && attributeList.values.contains(paramString))
        return attributeList; 
    } 
    return null;
  }
  
  public static int name2type(String paramString) {
    Integer integer = (Integer)contentTypes.get(paramString);
    return (integer != null) ? integer.intValue() : 0;
  }
  
  static  {
    contentTypes.put("CDATA", Integer.valueOf(1));
    contentTypes.put("RCDATA", Integer.valueOf(16));
    contentTypes.put("EMPTY", Integer.valueOf(17));
    contentTypes.put("ANY", Integer.valueOf(19));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\Element.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
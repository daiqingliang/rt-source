package com.sun.org.apache.xerces.internal.impl.dtd;

public class XMLContentSpec {
  public static final short CONTENTSPECNODE_LEAF = 0;
  
  public static final short CONTENTSPECNODE_ZERO_OR_ONE = 1;
  
  public static final short CONTENTSPECNODE_ZERO_OR_MORE = 2;
  
  public static final short CONTENTSPECNODE_ONE_OR_MORE = 3;
  
  public static final short CONTENTSPECNODE_CHOICE = 4;
  
  public static final short CONTENTSPECNODE_SEQ = 5;
  
  public static final short CONTENTSPECNODE_ANY = 6;
  
  public static final short CONTENTSPECNODE_ANY_OTHER = 7;
  
  public static final short CONTENTSPECNODE_ANY_LOCAL = 8;
  
  public static final short CONTENTSPECNODE_ANY_LAX = 22;
  
  public static final short CONTENTSPECNODE_ANY_OTHER_LAX = 23;
  
  public static final short CONTENTSPECNODE_ANY_LOCAL_LAX = 24;
  
  public static final short CONTENTSPECNODE_ANY_SKIP = 38;
  
  public static final short CONTENTSPECNODE_ANY_OTHER_SKIP = 39;
  
  public static final short CONTENTSPECNODE_ANY_LOCAL_SKIP = 40;
  
  public short type;
  
  public Object value;
  
  public Object otherValue;
  
  public XMLContentSpec() { clear(); }
  
  public XMLContentSpec(short paramShort, Object paramObject1, Object paramObject2) { setValues(paramShort, paramObject1, paramObject2); }
  
  public XMLContentSpec(XMLContentSpec paramXMLContentSpec) { setValues(paramXMLContentSpec); }
  
  public XMLContentSpec(Provider paramProvider, int paramInt) { setValues(paramProvider, paramInt); }
  
  public void clear() {
    this.type = -1;
    this.value = null;
    this.otherValue = null;
  }
  
  public void setValues(short paramShort, Object paramObject1, Object paramObject2) {
    this.type = paramShort;
    this.value = paramObject1;
    this.otherValue = paramObject2;
  }
  
  public void setValues(XMLContentSpec paramXMLContentSpec) {
    this.type = paramXMLContentSpec.type;
    this.value = paramXMLContentSpec.value;
    this.otherValue = paramXMLContentSpec.otherValue;
  }
  
  public void setValues(Provider paramProvider, int paramInt) {
    if (!paramProvider.getContentSpec(paramInt, this))
      clear(); 
  }
  
  public int hashCode() { return this.type << 16 | this.value.hashCode() << 8 | this.otherValue.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof XMLContentSpec) {
      XMLContentSpec xMLContentSpec = (XMLContentSpec)paramObject;
      return (this.type == xMLContentSpec.type && this.value == xMLContentSpec.value && this.otherValue == xMLContentSpec.otherValue);
    } 
    return false;
  }
  
  public static interface Provider {
    boolean getContentSpec(int param1Int, XMLContentSpec param1XMLContentSpec);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dtd\XMLContentSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
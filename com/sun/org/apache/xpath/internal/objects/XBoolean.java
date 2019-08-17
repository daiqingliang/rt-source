package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.TransformerException;

public class XBoolean extends XObject {
  static final long serialVersionUID = -2964933058866100881L;
  
  public static final XBoolean S_TRUE = new XBooleanStatic(true);
  
  public static final XBoolean S_FALSE = new XBooleanStatic(false);
  
  private final boolean m_val;
  
  public XBoolean(boolean paramBoolean) { this.m_val = paramBoolean; }
  
  public XBoolean(Boolean paramBoolean) {
    this.m_val = paramBoolean.booleanValue();
    setObject(paramBoolean);
  }
  
  public int getType() { return 1; }
  
  public String getTypeString() { return "#BOOLEAN"; }
  
  public double num() { return this.m_val ? 1.0D : 0.0D; }
  
  public boolean bool() { return this.m_val; }
  
  public String str() { return this.m_val ? "true" : "false"; }
  
  public Object object() {
    if (null == this.m_obj)
      setObject(new Boolean(this.m_val)); 
    return this.m_obj;
  }
  
  public boolean equals(XObject paramXObject) {
    if (paramXObject.getType() == 4)
      return paramXObject.equals(this); 
    try {
      return (this.m_val == paramXObject.bool());
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
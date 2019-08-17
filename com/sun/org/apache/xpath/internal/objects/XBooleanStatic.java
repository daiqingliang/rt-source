package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.TransformerException;

public class XBooleanStatic extends XBoolean {
  static final long serialVersionUID = -8064147275772687409L;
  
  private final boolean m_val;
  
  public XBooleanStatic(boolean paramBoolean) {
    super(paramBoolean);
    this.m_val = paramBoolean;
  }
  
  public boolean equals(XObject paramXObject) {
    try {
      return (this.m_val == paramXObject.bool());
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XBooleanStatic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
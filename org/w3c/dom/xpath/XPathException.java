package org.w3c.dom.xpath;

public class XPathException extends RuntimeException {
  public short code;
  
  public static final short INVALID_EXPRESSION_ERR = 1;
  
  public static final short TYPE_ERR = 2;
  
  public XPathException(short paramShort, String paramString) {
    super(paramString);
    this.code = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\xpath\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package org.w3c.dom.ls;

public class LSException extends RuntimeException {
  public short code;
  
  public static final short PARSE_ERR = 81;
  
  public static final short SERIALIZE_ERR = 82;
  
  public LSException(short paramShort, String paramString) {
    super(paramString);
    this.code = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ls\LSException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
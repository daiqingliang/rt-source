package org.w3c.dom.ranges;

public class RangeException extends RuntimeException {
  public short code;
  
  public static final short BAD_BOUNDARYPOINTS_ERR = 1;
  
  public static final short INVALID_NODE_TYPE_ERR = 2;
  
  public RangeException(short paramShort, String paramString) {
    super(paramString);
    this.code = paramShort;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\w3c\dom\ranges\RangeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
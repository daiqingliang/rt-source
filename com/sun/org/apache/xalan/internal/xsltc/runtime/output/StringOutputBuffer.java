package com.sun.org.apache.xalan.internal.xsltc.runtime.output;

class StringOutputBuffer implements OutputBuffer {
  private StringBuffer _buffer = new StringBuffer();
  
  public String close() { return this._buffer.toString(); }
  
  public OutputBuffer append(String paramString) {
    this._buffer.append(paramString);
    return this;
  }
  
  public OutputBuffer append(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    this._buffer.append(paramArrayOfChar, paramInt1, paramInt2);
    return this;
  }
  
  public OutputBuffer append(char paramChar) {
    this._buffer.append(paramChar);
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\runtime\output\StringOutputBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_2 extends CDRInputStream_1_1 {
  protected boolean headerPadding;
  
  protected boolean restoreHeaderPadding;
  
  void setHeaderPadding(boolean paramBoolean) { this.headerPadding = paramBoolean; }
  
  public void mark(int paramInt) {
    super.mark(paramInt);
    this.restoreHeaderPadding = this.headerPadding;
  }
  
  public void reset() {
    super.reset();
    this.headerPadding = this.restoreHeaderPadding;
    this.restoreHeaderPadding = false;
  }
  
  public CDRInputStreamBase dup() {
    CDRInputStreamBase cDRInputStreamBase = super.dup();
    ((CDRInputStream_1_2)cDRInputStreamBase).headerPadding = this.headerPadding;
    return cDRInputStreamBase;
  }
  
  protected void alignAndCheck(int paramInt1, int paramInt2) {
    if (this.headerPadding == true) {
      this.headerPadding = false;
      alignOnBoundary(8);
    } 
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(this.bbwi.position(), paramInt1);
    this.bbwi.position(this.bbwi.position() + i);
    if (this.bbwi.position() + paramInt2 > this.bbwi.buflen)
      grow(1, paramInt2); 
  }
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_2; }
  
  public char read_wchar() {
    byte b = read_octet();
    char[] arrayOfChar = getConvertedChars(b, getWCharConverter());
    if (getWCharConverter().getNumChars() > 1)
      throw this.wrapper.btcResultMoreThanOneChar(); 
    return arrayOfChar[0];
  }
  
  public String read_wstring() {
    int i = read_long();
    if (i == 0)
      return new String(""); 
    checkForNegativeLength(i);
    return new String(getConvertedChars(i, getWCharConverter()), 0, getWCharConverter().getNumChars());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
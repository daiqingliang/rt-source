package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;

public class CDRInputStream_1_1 extends CDRInputStream_1_0 {
  protected int fragmentOffset = 0;
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_1; }
  
  public CDRInputStreamBase dup() {
    CDRInputStreamBase cDRInputStreamBase = super.dup();
    ((CDRInputStream_1_1)cDRInputStreamBase).fragmentOffset = this.fragmentOffset;
    return cDRInputStreamBase;
  }
  
  protected int get_offset() { return this.bbwi.position() + this.fragmentOffset; }
  
  protected void alignAndCheck(int paramInt1, int paramInt2) {
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(this.bbwi.position(), paramInt1);
    if (this.bbwi.position() + paramInt2 + i > this.bbwi.buflen) {
      if (this.bbwi.position() + i == this.bbwi.buflen)
        this.bbwi.position(this.bbwi.position() + i); 
      grow(paramInt1, paramInt2);
      i = computeAlignment(this.bbwi.position(), paramInt1);
    } 
    this.bbwi.position(this.bbwi.position() + i);
  }
  
  protected void grow(int paramInt1, int paramInt2) {
    this.bbwi.needed = paramInt2;
    int i = this.bbwi.position();
    this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
    if (this.bbwi.fragmented) {
      this.fragmentOffset += i - this.bbwi.position();
      this.markAndResetHandler.fragmentationOccured(this.bbwi);
      this.bbwi.fragmented = false;
    } 
  }
  
  public Object createStreamMemento() { return new FragmentableStreamMemento(); }
  
  public void restoreInternalState(Object paramObject) {
    super.restoreInternalState(paramObject);
    this.fragmentOffset = ((FragmentableStreamMemento)paramObject).fragmentOffset_;
  }
  
  public char read_wchar() {
    alignAndCheck(2, 2);
    char[] arrayOfChar = getConvertedChars(2, getWCharConverter());
    if (getWCharConverter().getNumChars() > 1)
      throw this.wrapper.btcResultMoreThanOneChar(); 
    return arrayOfChar[0];
  }
  
  public String read_wstring() {
    int i = read_long();
    if (i == 0)
      return new String(""); 
    checkForNegativeLength(i);
    char[] arrayOfChar = getConvertedChars(--i * 2, getWCharConverter());
    read_short();
    return new String(arrayOfChar, 0, getWCharConverter().getNumChars());
  }
  
  private class FragmentableStreamMemento extends CDRInputStream_1_0.StreamMemento {
    private int fragmentOffset_ = CDRInputStream_1_1.this.fragmentOffset;
    
    public FragmentableStreamMemento() { super(CDRInputStream_1_1.this); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
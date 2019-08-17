package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;

public class CDROutputStream_1_1 extends CDROutputStream_1_0 {
  protected int fragmentOffset = 0;
  
  protected void alignAndReserve(int paramInt1, int paramInt2) {
    int i = computeAlignment(paramInt1);
    if (this.bbwi.position() + paramInt2 + i > this.bbwi.buflen) {
      grow(paramInt1, paramInt2);
      i = computeAlignment(paramInt1);
    } 
    this.bbwi.position(this.bbwi.position() + i);
  }
  
  protected void grow(int paramInt1, int paramInt2) {
    int i = this.bbwi.position();
    super.grow(paramInt1, paramInt2);
    if (this.bbwi.fragmented) {
      this.bbwi.fragmented = false;
      this.fragmentOffset += i - this.bbwi.position();
    } 
  }
  
  public int get_offset() { return this.bbwi.position() + this.fragmentOffset; }
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_1; }
  
  public void write_wchar(char paramChar) {
    CodeSetConversion.CTBConverter cTBConverter = getWCharConverter();
    cTBConverter.convert(paramChar);
    if (cTBConverter.getNumBytes() != 2)
      throw this.wrapper.badGiop11Ctb(CompletionStatus.COMPLETED_MAYBE); 
    alignAndReserve(cTBConverter.getAlignment(), cTBConverter.getNumBytes());
    this.parent.write_octet_array(cTBConverter.getBytes(), 0, cTBConverter.getNumBytes());
  }
  
  public void write_wstring(String paramString) {
    if (paramString == null)
      throw this.wrapper.nullParam(CompletionStatus.COMPLETED_MAYBE); 
    int i = paramString.length() + 1;
    write_long(i);
    CodeSetConversion.CTBConverter cTBConverter = getWCharConverter();
    cTBConverter.convert(paramString);
    internalWriteOctetArray(cTBConverter.getBytes(), 0, cTBConverter.getNumBytes());
    write_short((short)0);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputStream_1_1.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
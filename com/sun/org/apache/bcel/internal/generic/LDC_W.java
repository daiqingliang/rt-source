package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;

public class LDC_W extends LDC {
  LDC_W() {}
  
  public LDC_W(int paramInt) { super(paramInt); }
  
  protected void initFromFile(ByteSequence paramByteSequence, boolean paramBoolean) throws IOException {
    setIndex(paramByteSequence.readUnsignedShort());
    this.opcode = 19;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\bcel\internal\generic\LDC_W.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
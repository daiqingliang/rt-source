package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import java.io.IOException;

public class IntData extends Pcdata {
  private int data;
  
  private int length;
  
  private static final int[] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE };
  
  public void reset(int paramInt) {
    this.data = paramInt;
    if (paramInt == Integer.MIN_VALUE) {
      this.length = 11;
    } else {
      this.length = (paramInt < 0) ? (stringSizeOfInt(-paramInt) + 1) : stringSizeOfInt(paramInt);
    } 
  }
  
  private static int stringSizeOfInt(int paramInt) {
    for (byte b = 0;; b++) {
      if (paramInt <= sizeTable[b])
        return b + true; 
    } 
  }
  
  public String toString() { return String.valueOf(this.data); }
  
  public int length() { return this.length; }
  
  public char charAt(int paramInt) { return toString().charAt(paramInt); }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) { return toString().substring(paramInt1, paramInt2); }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput) throws IOException { paramUTF8XmlOutput.text(this.data); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\IntData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
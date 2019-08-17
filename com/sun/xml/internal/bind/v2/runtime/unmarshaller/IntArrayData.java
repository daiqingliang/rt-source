package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.output.Pcdata;
import com.sun.xml.internal.bind.v2.runtime.output.UTF8XmlOutput;
import java.io.IOException;

public final class IntArrayData extends Pcdata {
  private int[] data;
  
  private int start;
  
  private int len;
  
  private StringBuilder literal;
  
  public IntArrayData(int[] paramArrayOfInt, int paramInt1, int paramInt2) { set(paramArrayOfInt, paramInt1, paramInt2); }
  
  public IntArrayData() {}
  
  public void set(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    this.data = paramArrayOfInt;
    this.start = paramInt1;
    this.len = paramInt2;
    this.literal = null;
  }
  
  public int length() { return getLiteral().length(); }
  
  public char charAt(int paramInt) { return getLiteral().charAt(paramInt); }
  
  public CharSequence subSequence(int paramInt1, int paramInt2) { return getLiteral().subSequence(paramInt1, paramInt2); }
  
  private StringBuilder getLiteral() {
    if (this.literal != null)
      return this.literal; 
    this.literal = new StringBuilder();
    int i = this.start;
    for (int j = this.len; j > 0; j--) {
      if (this.literal.length() > 0)
        this.literal.append(' '); 
      this.literal.append(this.data[i++]);
    } 
    return this.literal;
  }
  
  public String toString() { return this.literal.toString(); }
  
  public void writeTo(UTF8XmlOutput paramUTF8XmlOutput) throws IOException {
    int i = this.start;
    for (int j = this.len; j > 0; j--) {
      if (j != this.len)
        paramUTF8XmlOutput.write(32); 
      paramUTF8XmlOutput.text(this.data[i++]);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\IntArrayData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
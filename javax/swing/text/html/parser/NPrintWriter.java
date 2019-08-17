package javax.swing.text.html.parser;

import java.io.PrintWriter;

class NPrintWriter extends PrintWriter {
  private int numLines = 5;
  
  private int numPrinted = 0;
  
  public NPrintWriter(int paramInt) {
    super(System.out);
    this.numLines = paramInt;
  }
  
  public void println(char[] paramArrayOfChar) {
    if (this.numPrinted >= this.numLines)
      return; 
    char[] arrayOfChar = null;
    for (byte b = 0; b < paramArrayOfChar.length; b++) {
      if (paramArrayOfChar[b] == '\n')
        this.numPrinted++; 
      if (this.numPrinted == this.numLines)
        System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, b); 
    } 
    if (arrayOfChar != null)
      print(arrayOfChar); 
    if (this.numPrinted == this.numLines)
      return; 
    super.println(paramArrayOfChar);
    this.numPrinted++;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\NPrintWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
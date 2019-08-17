package com.sun.org.apache.regexp.internal;

import java.io.Serializable;

public class REProgram implements Serializable {
  static final int OPT_HASBACKREFS = 1;
  
  char[] instruction;
  
  int lenInstruction;
  
  char[] prefix;
  
  int flags;
  
  int maxParens = -1;
  
  public REProgram(char[] paramArrayOfChar) { this(paramArrayOfChar, paramArrayOfChar.length); }
  
  public REProgram(int paramInt, char[] paramArrayOfChar) {
    this(paramArrayOfChar, paramArrayOfChar.length);
    this.maxParens = paramInt;
  }
  
  public REProgram(char[] paramArrayOfChar, int paramInt) { setInstructions(paramArrayOfChar, paramInt); }
  
  public char[] getInstructions() {
    if (this.lenInstruction != 0) {
      char[] arrayOfChar = new char[this.lenInstruction];
      System.arraycopy(this.instruction, 0, arrayOfChar, 0, this.lenInstruction);
      return arrayOfChar;
    } 
    return null;
  }
  
  public void setInstructions(char[] paramArrayOfChar, int paramInt) {
    this.instruction = paramArrayOfChar;
    this.lenInstruction = paramInt;
    this.flags = 0;
    this.prefix = null;
    if (paramArrayOfChar != null && paramInt != 0) {
      if (paramInt >= 3 && paramArrayOfChar[0] == '|') {
        char c1 = paramArrayOfChar[2];
        if (paramArrayOfChar[c1 + Character.MIN_VALUE] == 'E' && paramInt >= 6 && paramArrayOfChar[3] == 'A') {
          char c2 = paramArrayOfChar[4];
          this.prefix = new char[c2];
          System.arraycopy(paramArrayOfChar, 6, this.prefix, 0, c2);
        } 
      } 
      for (char c = Character.MIN_VALUE; c < paramInt; c += '\003') {
        switch (paramArrayOfChar[c + false]) {
          case '[':
            c += paramArrayOfChar[c + true] * '\002';
            break;
          case 'A':
            c += paramArrayOfChar[c + '\001'];
            break;
          case '#':
            this.flags |= 0x1;
            break;
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\REProgram.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package jdk.internal.util.xml.impl;

import java.io.Reader;

public class Input {
  public String pubid;
  
  public String sysid;
  
  public String xmlenc;
  
  public char xmlver;
  
  public Reader src;
  
  public char[] chars;
  
  public int chLen;
  
  public int chIdx;
  
  public Input next;
  
  public Input(int paramInt) {
    this.chars = new char[paramInt];
    this.chLen = this.chars.length;
  }
  
  public Input(char[] paramArrayOfChar) {
    this.chars = paramArrayOfChar;
    this.chLen = this.chars.length;
  }
  
  public Input() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\Input.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
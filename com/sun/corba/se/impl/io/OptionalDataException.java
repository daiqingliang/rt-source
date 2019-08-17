package com.sun.corba.se.impl.io;

import java.io.IOException;

public class OptionalDataException extends IOException {
  public int length;
  
  public boolean eof;
  
  OptionalDataException(int paramInt) {
    this.eof = false;
    this.length = paramInt;
  }
  
  OptionalDataException(boolean paramBoolean) {
    this.length = 0;
    this.eof = paramBoolean;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\io\OptionalDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
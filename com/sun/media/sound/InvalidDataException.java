package com.sun.media.sound;

import java.io.IOException;

public class InvalidDataException extends IOException {
  private static final long serialVersionUID = 1L;
  
  public InvalidDataException() { super("Invalid Data!"); }
  
  public InvalidDataException(String paramString) { super(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\media\sound\InvalidDataException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
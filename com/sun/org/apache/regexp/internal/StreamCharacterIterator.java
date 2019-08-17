package com.sun.org.apache.regexp.internal;

import java.io.IOException;
import java.io.InputStream;

public final class StreamCharacterIterator implements CharacterIterator {
  private final InputStream is;
  
  private final StringBuffer buff;
  
  private boolean closed;
  
  public StreamCharacterIterator(InputStream paramInputStream) {
    this.is = paramInputStream;
    this.buff = new StringBuffer(512);
    this.closed = false;
  }
  
  public String substring(int paramInt1, int paramInt2) {
    try {
      ensure(paramInt2);
      return this.buff.toString().substring(paramInt1, paramInt2);
    } catch (IOException iOException) {
      throw new StringIndexOutOfBoundsException(iOException.getMessage());
    } 
  }
  
  public String substring(int paramInt) {
    try {
      readAll();
      return this.buff.toString().substring(paramInt);
    } catch (IOException iOException) {
      throw new StringIndexOutOfBoundsException(iOException.getMessage());
    } 
  }
  
  public char charAt(int paramInt) {
    try {
      ensure(paramInt);
      return this.buff.charAt(paramInt);
    } catch (IOException iOException) {
      throw new StringIndexOutOfBoundsException(iOException.getMessage());
    } 
  }
  
  public boolean isEnd(int paramInt) {
    if (this.buff.length() > paramInt)
      return false; 
    try {
      ensure(paramInt);
      return (this.buff.length() <= paramInt);
    } catch (IOException iOException) {
      throw new StringIndexOutOfBoundsException(iOException.getMessage());
    } 
  }
  
  private int read(int paramInt) throws IOException {
    if (this.closed)
      return 0; 
    int i = paramInt;
    while (--i >= 0) {
      int j = this.is.read();
      if (j < 0) {
        this.closed = true;
        break;
      } 
      this.buff.append((char)j);
    } 
    return paramInt - i;
  }
  
  private void readAll() throws IOException {
    while (!this.closed)
      read(1000); 
  }
  
  private void ensure(int paramInt) throws IOException {
    if (this.closed)
      return; 
    if (paramInt < this.buff.length())
      return; 
    read(paramInt + 1 - this.buff.length());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\StreamCharacterIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
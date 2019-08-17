package java.io;

import java.util.Enumeration;
import java.util.Vector;

public class SequenceInputStream extends InputStream {
  Enumeration<? extends InputStream> e;
  
  InputStream in;
  
  public SequenceInputStream(Enumeration<? extends InputStream> paramEnumeration) {
    this.e = paramEnumeration;
    try {
      nextStream();
    } catch (IOException iOException) {
      throw new Error("panic");
    } 
  }
  
  public SequenceInputStream(InputStream paramInputStream1, InputStream paramInputStream2) {
    Vector vector = new Vector(2);
    vector.addElement(paramInputStream1);
    vector.addElement(paramInputStream2);
    this.e = vector.elements();
    try {
      nextStream();
    } catch (IOException iOException) {
      throw new Error("panic");
    } 
  }
  
  final void nextStream() throws IOException {
    if (this.in != null)
      this.in.close(); 
    if (this.e.hasMoreElements()) {
      this.in = (InputStream)this.e.nextElement();
      if (this.in == null)
        throw new NullPointerException(); 
    } else {
      this.in = null;
    } 
  }
  
  public int available() throws IOException { return (this.in == null) ? 0 : this.in.available(); }
  
  public int read() throws IOException {
    while (this.in != null) {
      int i = this.in.read();
      if (i != -1)
        return i; 
      nextStream();
    } 
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException {
    if (this.in == null)
      return -1; 
    if (paramArrayOfByte == null)
      throw new NullPointerException(); 
    if (paramInt1 < 0 || paramInt2 < 0 || paramInt2 > paramArrayOfByte.length - paramInt1)
      throw new IndexOutOfBoundsException(); 
    if (paramInt2 == 0)
      return 0; 
    do {
      int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i > 0)
        return i; 
      nextStream();
    } while (this.in != null);
    return -1;
  }
  
  public void close() throws IOException {
    do {
      nextStream();
    } while (this.in != null);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\SequenceInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
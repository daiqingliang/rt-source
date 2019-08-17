package java.io;

import java.util.Objects;

public class UncheckedIOException extends RuntimeException {
  private static final long serialVersionUID = -8134305061645241065L;
  
  public UncheckedIOException(String paramString, IOException paramIOException) { super(paramString, (Throwable)Objects.requireNonNull(paramIOException)); }
  
  public UncheckedIOException(IOException paramIOException) { super((Throwable)Objects.requireNonNull(paramIOException)); }
  
  public IOException getCause() { return (IOException)super.getCause(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Throwable throwable = super.getCause();
    if (!(throwable instanceof IOException))
      throw new InvalidObjectException("Cause must be an IOException"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\io\UncheckedIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.nio.file;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ConcurrentModificationException;
import java.util.Objects;

public final class DirectoryIteratorException extends ConcurrentModificationException {
  private static final long serialVersionUID = -6012699886086212874L;
  
  public DirectoryIteratorException(IOException paramIOException) { super((Throwable)Objects.requireNonNull(paramIOException)); }
  
  public IOException getCause() { return (IOException)super.getCause(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    Throwable throwable = super.getCause();
    if (!(throwable instanceof IOException))
      throw new InvalidObjectException("Cause must be an IOException"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\nio\file\DirectoryIteratorException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
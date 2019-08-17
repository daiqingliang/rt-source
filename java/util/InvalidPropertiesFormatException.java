package java.util;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InvalidPropertiesFormatException extends IOException {
  private static final long serialVersionUID = 7763056076009360219L;
  
  public InvalidPropertiesFormatException(Throwable paramThrowable) {
    super((paramThrowable == null) ? null : paramThrowable.toString());
    initCause(paramThrowable);
  }
  
  public InvalidPropertiesFormatException(String paramString) { super(paramString); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws NotSerializableException { throw new NotSerializableException("Not serializable."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\InvalidPropertiesFormatException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
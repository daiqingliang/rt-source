package javax.rmi.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;

public interface ValueHandlerMultiFormat extends ValueHandler {
  byte getMaximumStreamFormatVersion();
  
  void writeValue(OutputStream paramOutputStream, Serializable paramSerializable, byte paramByte);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\rmi\CORBA\ValueHandlerMultiFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
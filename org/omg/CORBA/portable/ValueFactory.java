package org.omg.CORBA.portable;

import java.io.Serializable;
import org.omg.CORBA_2_3.portable.InputStream;

public interface ValueFactory {
  Serializable read_value(InputStream paramInputStream);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\ValueFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
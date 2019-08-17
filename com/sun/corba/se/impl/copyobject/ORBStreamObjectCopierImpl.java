package com.sun.corba.se.impl.copyobject;

import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import java.io.Serializable;
import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBStreamObjectCopierImpl implements ObjectCopier {
  private ORB orb;
  
  public ORBStreamObjectCopierImpl(ORB paramORB) { this.orb = paramORB; }
  
  public Object copy(Object paramObject) {
    if (paramObject instanceof java.rmi.Remote)
      return Utility.autoConnect(paramObject, this.orb, true); 
    OutputStream outputStream = (OutputStream)this.orb.create_output_stream();
    outputStream.write_value((Serializable)paramObject);
    InputStream inputStream = (InputStream)outputStream.create_input_stream();
    return inputStream.read_value();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\copyobject\ORBStreamObjectCopierImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
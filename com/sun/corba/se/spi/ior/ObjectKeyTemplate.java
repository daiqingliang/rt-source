package com.sun.corba.se.spi.ior;

import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import org.omg.CORBA_2_3.portable.OutputStream;

public interface ObjectKeyTemplate extends Writeable {
  ORBVersion getORBVersion();
  
  int getSubcontractId();
  
  int getServerId();
  
  String getORBId();
  
  ObjectAdapterId getObjectAdapterId();
  
  byte[] getAdapterId();
  
  void write(ObjectId paramObjectId, OutputStream paramOutputStream);
  
  CorbaServerRequestDispatcher getServerRequestDispatcher(ORB paramORB, ObjectId paramObjectId);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\ObjectKeyTemplate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
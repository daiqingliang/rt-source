package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.INTERNAL;

public class BufferManagerFactory {
  public static final int GROW = 0;
  
  public static final int COLLECT = 1;
  
  public static final int STREAM = 2;
  
  public static BufferManagerRead newBufferManagerRead(GIOPVersion paramGIOPVersion, byte paramByte, ORB paramORB) {
    if (paramByte != 0)
      return new BufferManagerReadGrow(paramORB); 
    switch (paramGIOPVersion.intValue()) {
      case 256:
        return new BufferManagerReadGrow(paramORB);
      case 257:
      case 258:
        return new BufferManagerReadStream(paramORB);
    } 
    throw new INTERNAL("Unknown GIOP version: " + paramGIOPVersion);
  }
  
  public static BufferManagerRead newBufferManagerRead(int paramInt, byte paramByte, ORB paramORB) {
    if (paramByte != 0) {
      if (paramInt != 0) {
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
        throw oRBUtilSystemException.invalidBuffMgrStrategy("newBufferManagerRead");
      } 
      return new BufferManagerReadGrow(paramORB);
    } 
    switch (paramInt) {
      case 0:
        return new BufferManagerReadGrow(paramORB);
      case 1:
        throw new INTERNAL("Collect strategy invalid for reading");
      case 2:
        return new BufferManagerReadStream(paramORB);
    } 
    throw new INTERNAL("Unknown buffer manager read strategy: " + paramInt);
  }
  
  public static BufferManagerWrite newBufferManagerWrite(int paramInt, byte paramByte, ORB paramORB) {
    if (paramByte != 0) {
      if (paramInt != 0) {
        ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
        throw oRBUtilSystemException.invalidBuffMgrStrategy("newBufferManagerWrite");
      } 
      return new BufferManagerWriteGrow(paramORB);
    } 
    switch (paramInt) {
      case 0:
        return new BufferManagerWriteGrow(paramORB);
      case 1:
        return new BufferManagerWriteCollect(paramORB);
      case 2:
        return new BufferManagerWriteStream(paramORB);
    } 
    throw new INTERNAL("Unknown buffer manager write strategy: " + paramInt);
  }
  
  public static BufferManagerWrite newBufferManagerWrite(GIOPVersion paramGIOPVersion, byte paramByte, ORB paramORB) { return (paramByte != 0) ? new BufferManagerWriteGrow(paramORB) : newBufferManagerWrite(paramORB.getORBData().getGIOPBuffMgrStrategy(paramGIOPVersion), paramByte, paramORB); }
  
  public static BufferManagerRead defaultBufferManagerRead(ORB paramORB) { return new BufferManagerReadGrow(paramORB); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\BufferManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
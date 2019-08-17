package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBTypeComponentImpl extends TaggedComponentBase implements ORBTypeComponent {
  private int ORBType;
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof ORBTypeComponentImpl))
      return false; 
    ORBTypeComponentImpl oRBTypeComponentImpl = (ORBTypeComponentImpl)paramObject;
    return (this.ORBType == oRBTypeComponentImpl.ORBType);
  }
  
  public int hashCode() { return this.ORBType; }
  
  public String toString() { return "ORBTypeComponentImpl[ORBType=" + this.ORBType + "]"; }
  
  public ORBTypeComponentImpl(int paramInt) { this.ORBType = paramInt; }
  
  public int getId() { return 0; }
  
  public int getORBType() { return this.ORBType; }
  
  public void writeContents(OutputStream paramOutputStream) { paramOutputStream.write_ulong(this.ORBType); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\ORBTypeComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
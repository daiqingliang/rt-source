package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionComponentImpl extends TaggedComponentBase implements MaxStreamFormatVersionComponent {
  private byte version = ORBUtility.getMaxStreamFormatVersion();
  
  public static final MaxStreamFormatVersionComponentImpl singleton = new MaxStreamFormatVersionComponentImpl();
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MaxStreamFormatVersionComponentImpl))
      return false; 
    MaxStreamFormatVersionComponentImpl maxStreamFormatVersionComponentImpl = (MaxStreamFormatVersionComponentImpl)paramObject;
    return (this.version == maxStreamFormatVersionComponentImpl.version);
  }
  
  public int hashCode() { return this.version; }
  
  public String toString() { return "MaxStreamFormatVersionComponentImpl[version=" + this.version + "]"; }
  
  public MaxStreamFormatVersionComponentImpl() {}
  
  public MaxStreamFormatVersionComponentImpl(byte paramByte) {}
  
  public byte getMaxStreamFormatVersion() { return this.version; }
  
  public void writeContents(OutputStream paramOutputStream) { paramOutputStream.write_octet(this.version); }
  
  public int getId() { return 38; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\ior\iiop\MaxStreamFormatVersionComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
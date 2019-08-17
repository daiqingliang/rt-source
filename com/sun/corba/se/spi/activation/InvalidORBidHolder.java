package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidORBidHolder implements Streamable {
  public InvalidORBid value = null;
  
  public InvalidORBidHolder() {}
  
  public InvalidORBidHolder(InvalidORBid paramInvalidORBid) { this.value = paramInvalidORBid; }
  
  public void _read(InputStream paramInputStream) { this.value = InvalidORBidHelper.read(paramInputStream); }
  
  public void _write(OutputStream paramOutputStream) { InvalidORBidHelper.write(paramOutputStream, this.value); }
  
  public TypeCode _type() { return InvalidORBidHelper.type(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\InvalidORBidHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
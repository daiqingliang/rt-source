package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class InvalidORBid extends UserException {
  public InvalidORBid() { super(InvalidORBidHelper.id()); }
  
  public InvalidORBid(String paramString) { super(InvalidORBidHelper.id() + "  " + paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\activation\InvalidORBid.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
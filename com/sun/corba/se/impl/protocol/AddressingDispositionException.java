package com.sun.corba.se.impl.protocol;

public class AddressingDispositionException extends RuntimeException {
  private short expectedAddrDisp = 0;
  
  public AddressingDispositionException(short paramShort) { this.expectedAddrDisp = paramShort; }
  
  public short expectedAddrDisp() { return this.expectedAddrDisp; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\protocol\AddressingDispositionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
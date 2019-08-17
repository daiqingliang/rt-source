package com.sun.corba.se.impl.encoding;

interface MarkAndResetHandler {
  void mark(RestorableInputStream paramRestorableInputStream);
  
  void fragmentationOccured(ByteBufferWithInfo paramByteBufferWithInfo);
  
  void reset();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\MarkAndResetHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
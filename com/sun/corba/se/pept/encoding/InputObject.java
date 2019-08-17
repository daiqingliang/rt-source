package com.sun.corba.se.pept.encoding;

import com.sun.corba.se.pept.protocol.MessageMediator;
import java.io.IOException;

public interface InputObject {
  void setMessageMediator(MessageMediator paramMessageMediator);
  
  MessageMediator getMessageMediator();
  
  void close() throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\pept\encoding\InputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
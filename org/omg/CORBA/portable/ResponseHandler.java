package org.omg.CORBA.portable;

public interface ResponseHandler {
  OutputStream createReply();
  
  OutputStream createExceptionReply();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\CORBA\portable\ResponseHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
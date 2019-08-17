package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.bind.api.Bridge;

public interface CheckedException {
  SEIModel getOwner();
  
  JavaMethod getParent();
  
  Class getExceptionClass();
  
  Class getDetailBean();
  
  Bridge getBridge();
  
  ExceptionType getExceptionType();
  
  String getMessageName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\CheckedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
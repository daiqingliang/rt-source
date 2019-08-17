package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.bind.api.Bridge;
import javax.jws.WebParam;
import javax.xml.namespace.QName;

public interface Parameter {
  SEIModel getOwner();
  
  JavaMethod getParent();
  
  QName getName();
  
  Bridge getBridge();
  
  WebParam.Mode getMode();
  
  int getIndex();
  
  boolean isWrapperStyle();
  
  boolean isReturnValue();
  
  ParameterBinding getBinding();
  
  ParameterBinding getInBinding();
  
  ParameterBinding getOutBinding();
  
  boolean isIN();
  
  boolean isOUT();
  
  boolean isINOUT();
  
  boolean isResponse();
  
  Object getHolderValue(Object paramObject);
  
  String getPartName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\Parameter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
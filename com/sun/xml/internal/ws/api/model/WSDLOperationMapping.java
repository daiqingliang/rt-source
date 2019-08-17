package com.sun.xml.internal.ws.api.model;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import javax.xml.namespace.QName;

public interface WSDLOperationMapping {
  WSDLBoundOperation getWSDLBoundOperation();
  
  JavaMethod getJavaMethod();
  
  QName getOperationName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\WSDLOperationMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
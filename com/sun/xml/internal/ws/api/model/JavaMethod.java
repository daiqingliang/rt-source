package com.sun.xml.internal.ws.api.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.soap.SOAPBinding;
import java.lang.reflect.Method;
import javax.xml.namespace.QName;

public interface JavaMethod {
  SEIModel getOwner();
  
  @NotNull
  Method getMethod();
  
  @NotNull
  Method getSEIMethod();
  
  MEP getMEP();
  
  SOAPBinding getBinding();
  
  @NotNull
  String getOperationName();
  
  @NotNull
  String getRequestMessageName();
  
  @Nullable
  String getResponseMessageName();
  
  @Nullable
  QName getRequestPayloadName();
  
  @Nullable
  QName getResponsePayloadName();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\model\JavaMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
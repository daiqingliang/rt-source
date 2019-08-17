package com.sun.xml.internal.ws.binding;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.http.HTTPBinding;

public class HTTPBindingImpl extends BindingImpl implements HTTPBinding {
  HTTPBindingImpl() { this(EMPTY_FEATURES); }
  
  HTTPBindingImpl(WebServiceFeature... paramVarArgs) { super(BindingID.XML_HTTP, paramVarArgs); }
  
  public void setHandlerChain(List<Handler> paramList) {
    for (Handler handler : paramList) {
      if (!(handler instanceof javax.xml.ws.handler.LogicalHandler))
        throw new WebServiceException(ClientMessages.NON_LOGICAL_HANDLER_SET(handler.getClass())); 
    } 
    setHandlerConfig(new HandlerConfiguration(Collections.emptySet(), paramList));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\binding\HTTPBindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
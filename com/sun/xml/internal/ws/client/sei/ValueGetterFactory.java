package com.sun.xml.internal.ws.client.sei;

import com.sun.xml.internal.ws.model.ParameterImpl;
import javax.jws.WebParam;

abstract class ValueGetterFactory {
  static final ValueGetterFactory SYNC = new ValueGetterFactory() {
      ValueGetter get(ParameterImpl param1ParameterImpl) { return (param1ParameterImpl.getMode() == WebParam.Mode.IN || param1ParameterImpl.getIndex() == -1) ? ValueGetter.PLAIN : ValueGetter.HOLDER; }
    };
  
  static final ValueGetterFactory ASYNC = new ValueGetterFactory() {
      ValueGetter get(ParameterImpl param1ParameterImpl) { return ValueGetter.PLAIN; }
    };
  
  abstract ValueGetter get(ParameterImpl paramParameterImpl);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\client\sei\ValueGetterFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
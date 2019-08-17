package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Intercepter {
  Object intercept(UnmarshallingContext.State paramState, Object paramObject) throws SAXException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Intercepter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
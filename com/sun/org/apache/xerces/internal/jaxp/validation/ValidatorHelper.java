package com.sun.org.apache.xerces.internal.jaxp.validation;

import java.io.IOException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

interface ValidatorHelper {
  void validate(Source paramSource, Result paramResult) throws SAXException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ValidatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
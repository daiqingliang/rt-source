package com.sun.xml.internal.ws.util;

import com.sun.xml.internal.ws.streaming.XMLReaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamReader;

public class FastInfosetUtil {
  public static XMLStreamReader createFIStreamReader(InputStream paramInputStream) {
    if (FastInfosetReflection.fiStAXDocumentParser_new == null)
      throw new XMLReaderException("fastinfoset.noImplementation", new Object[0]); 
    try {
      Object object = FastInfosetReflection.fiStAXDocumentParser_new.newInstance(new Object[0]);
      FastInfosetReflection.fiStAXDocumentParser_setStringInterning.invoke(object, new Object[] { Boolean.TRUE });
      FastInfosetReflection.fiStAXDocumentParser_setInputStream.invoke(object, new Object[] { paramInputStream });
      return (XMLStreamReader)object;
    } catch (Exception exception) {
      throw new XMLStreamReaderException(exception);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\FastInfosetUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
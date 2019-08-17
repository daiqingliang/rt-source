package com.sun.xml.internal.ws.streaming;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.util.FastInfosetUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

public class SourceReaderFactory {
  static Class fastInfosetSourceClass;
  
  static Method fastInfosetSource_getInputStream;
  
  public static XMLStreamReader createSourceReader(Source paramSource, boolean paramBoolean) { return createSourceReader(paramSource, paramBoolean, null); }
  
  public static XMLStreamReader createSourceReader(Source paramSource, boolean paramBoolean, String paramString) {
    try {
      if (paramSource instanceof StreamSource) {
        StreamSource streamSource = (StreamSource)paramSource;
        InputStream inputStream = streamSource.getInputStream();
        if (inputStream != null)
          return (paramString != null) ? XMLStreamReaderFactory.create(paramSource.getSystemId(), new InputStreamReader(inputStream, paramString), paramBoolean) : XMLStreamReaderFactory.create(paramSource.getSystemId(), inputStream, paramBoolean); 
        Reader reader = streamSource.getReader();
        return (reader != null) ? XMLStreamReaderFactory.create(paramSource.getSystemId(), reader, paramBoolean) : XMLStreamReaderFactory.create(paramSource.getSystemId(), (new URL(paramSource.getSystemId())).openStream(), paramBoolean);
      } 
      if (paramSource.getClass() == fastInfosetSourceClass)
        return FastInfosetUtil.createFIStreamReader((InputStream)fastInfosetSource_getInputStream.invoke(paramSource, new Object[0])); 
      if (paramSource instanceof DOMSource) {
        DOMStreamReader dOMStreamReader = new DOMStreamReader();
        dOMStreamReader.setCurrentNode(((DOMSource)paramSource).getNode());
        return dOMStreamReader;
      } 
      if (paramSource instanceof javax.xml.transform.sax.SAXSource) {
        Transformer transformer = XmlUtil.newTransformer();
        DOMResult dOMResult = new DOMResult();
        transformer.transform(paramSource, dOMResult);
        return createSourceReader(new DOMSource(dOMResult.getNode()), paramBoolean);
      } 
      throw new XMLReaderException("sourceReader.invalidSource", new Object[] { paramSource.getClass().getName() });
    } catch (Exception exception) {
      throw new XMLReaderException(exception);
    } 
  }
  
  static  {
    try {
      fastInfosetSourceClass = Class.forName("com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetSource");
      fastInfosetSource_getInputStream = fastInfosetSourceClass.getMethod("getInputStream", new Class[0]);
    } catch (Exception exception) {
      fastInfosetSourceClass = null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\streaming\SourceReaderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
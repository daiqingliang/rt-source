package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentParser;
import java.io.InputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class FastInfosetSource extends SAXSource {
  public FastInfosetSource(InputStream paramInputStream) { super(new InputSource(paramInputStream)); }
  
  public XMLReader getXMLReader() {
    XMLReader xMLReader = super.getXMLReader();
    if (xMLReader == null) {
      xMLReader = new SAXDocumentParser();
      setXMLReader(xMLReader);
    } 
    ((SAXDocumentParser)xMLReader).setInputStream(getInputStream());
    return xMLReader;
  }
  
  public InputStream getInputStream() { return getInputSource().getByteStream(); }
  
  public void setInputStream(InputStream paramInputStream) { setInputSource(new InputSource(paramInputStream)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
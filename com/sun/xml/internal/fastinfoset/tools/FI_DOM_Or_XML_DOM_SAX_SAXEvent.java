package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.Decoder;
import com.sun.xml.internal.fastinfoset.dom.DOMDocumentParser;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.w3c.dom.Document;

public class FI_DOM_Or_XML_DOM_SAX_SAXEvent extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    Document document;
    if (!paramInputStream.markSupported())
      paramInputStream = new BufferedInputStream(paramInputStream); 
    paramInputStream.mark(4);
    boolean bool = Decoder.isFastInfosetDocument(paramInputStream);
    paramInputStream.reset();
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    if (bool) {
      document = documentBuilder.newDocument();
      DOMDocumentParser dOMDocumentParser = new DOMDocumentParser();
      dOMDocumentParser.parse(document, paramInputStream);
    } else {
      if (paramString != null)
        documentBuilder.setEntityResolver(createRelativePathResolver(paramString)); 
      document = documentBuilder.parse(paramInputStream);
    } 
    SAXEventSerializer sAXEventSerializer = new SAXEventSerializer(paramOutputStream);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.transform(new DOMSource(document), new SAXResult(sAXEventSerializer));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    FI_DOM_Or_XML_DOM_SAX_SAXEvent fI_DOM_Or_XML_DOM_SAX_SAXEvent = new FI_DOM_Or_XML_DOM_SAX_SAXEvent();
    fI_DOM_Or_XML_DOM_SAX_SAXEvent.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\FI_DOM_Or_XML_DOM_SAX_SAXEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
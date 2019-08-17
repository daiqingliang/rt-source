package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetResult;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;

public class XML_DOM_SAX_FI extends TransformInputOutput {
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream, String paramString) throws Exception {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    if (paramString != null)
      documentBuilder.setEntityResolver(createRelativePathResolver(paramString)); 
    Document document = documentBuilder.parse(paramInputStream);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.transform(new DOMSource(document), new FastInfosetResult(paramOutputStream));
  }
  
  public void parse(InputStream paramInputStream, OutputStream paramOutputStream) throws Exception { parse(paramInputStream, paramOutputStream, null); }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    XML_DOM_SAX_FI xML_DOM_SAX_FI = new XML_DOM_SAX_FI();
    xML_DOM_SAX_FI.parse(paramArrayOfString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\XML_DOM_SAX_FI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
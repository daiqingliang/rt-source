package com.sun.xml.internal.ws.message.source;

import com.sun.xml.internal.ws.message.RootElementSniffer;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

final class SourceUtils {
  int srcType;
  
  private static final int domSource = 1;
  
  private static final int streamSource = 2;
  
  private static final int saxSource = 4;
  
  public SourceUtils(Source paramSource) {
    if (paramSource instanceof javax.xml.transform.stream.StreamSource) {
      this.srcType = 2;
    } else if (paramSource instanceof DOMSource) {
      this.srcType = 1;
    } else if (paramSource instanceof SAXSource) {
      this.srcType = 4;
    } 
  }
  
  public boolean isDOMSource() { return ((this.srcType & true) == 1); }
  
  public boolean isStreamSource() { return ((this.srcType & 0x2) == 2); }
  
  public boolean isSaxSource() { return ((this.srcType & 0x4) == 4); }
  
  public QName sniff(Source paramSource) { return sniff(paramSource, new RootElementSniffer()); }
  
  public QName sniff(Source paramSource, RootElementSniffer paramRootElementSniffer) {
    String str1 = null;
    String str2 = null;
    if (isDOMSource()) {
      DOMSource dOMSource = (DOMSource)paramSource;
      Node node = dOMSource.getNode();
      if (node.getNodeType() == 9)
        node = ((Document)node).getDocumentElement(); 
      str1 = node.getLocalName();
      str2 = node.getNamespaceURI();
    } else if (isSaxSource()) {
      SAXSource sAXSource = (SAXSource)paramSource;
      SAXResult sAXResult = new SAXResult(paramRootElementSniffer);
      try {
        Transformer transformer = XmlUtil.newTransformer();
        transformer.transform(sAXSource, sAXResult);
      } catch (TransformerConfigurationException transformerConfigurationException) {
        throw new WebServiceException(transformerConfigurationException);
      } catch (TransformerException transformerException) {
        str1 = paramRootElementSniffer.getLocalName();
        str2 = paramRootElementSniffer.getNsUri();
      } 
    } 
    return new QName(str2, str1);
  }
  
  public static void serializeSource(Source paramSource, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    int i;
    XMLStreamReader xMLStreamReader = SourceReaderFactory.createSourceReader(paramSource, true);
    do {
      byte b;
      int j;
      String str3;
      String str2;
      String str1;
      i = xMLStreamReader.next();
      switch (i) {
        case 1:
          str1 = xMLStreamReader.getNamespaceURI();
          str2 = xMLStreamReader.getPrefix();
          str3 = xMLStreamReader.getLocalName();
          if (str2 == null) {
            if (str1 == null) {
              paramXMLStreamWriter.writeStartElement(str3);
            } else {
              paramXMLStreamWriter.writeStartElement(str1, str3);
            } 
          } else if (str2.length() > 0) {
            String str4 = null;
            if (paramXMLStreamWriter.getNamespaceContext() != null)
              str4 = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(str2); 
            String str5 = paramXMLStreamWriter.getPrefix(str1);
            if (declarePrefix(str2, str1, str5, str4)) {
              paramXMLStreamWriter.writeStartElement(str2, str3, str1);
              paramXMLStreamWriter.setPrefix(str2, (str1 != null) ? str1 : "");
              paramXMLStreamWriter.writeNamespace(str2, str1);
            } else {
              paramXMLStreamWriter.writeStartElement(str2, str3, str1);
            } 
          } else {
            paramXMLStreamWriter.writeStartElement(str2, str3, str1);
          } 
          j = xMLStreamReader.getNamespaceCount();
          for (b = 0; b < j; b++) {
            String str4 = xMLStreamReader.getNamespacePrefix(b);
            if (str4 == null)
              str4 = ""; 
            String str5 = null;
            if (paramXMLStreamWriter.getNamespaceContext() != null)
              str5 = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(str4); 
            String str6 = xMLStreamReader.getNamespaceURI(b);
            if (str5 == null || str4.length() == 0 || str2.length() == 0 || (!str4.equals(str2) && !str5.equals(str6))) {
              paramXMLStreamWriter.setPrefix(str4, (str6 != null) ? str6 : "");
              paramXMLStreamWriter.writeNamespace(str4, (str6 != null) ? str6 : "");
            } 
          } 
          j = xMLStreamReader.getAttributeCount();
          for (b = 0; b < j; b++) {
            String str4 = xMLStreamReader.getAttributePrefix(b);
            String str5 = xMLStreamReader.getAttributeNamespace(b);
            paramXMLStreamWriter.writeAttribute((str4 != null) ? str4 : "", (str5 != null) ? str5 : "", xMLStreamReader.getAttributeLocalName(b), xMLStreamReader.getAttributeValue(b));
            setUndeclaredPrefix(str4, str5, paramXMLStreamWriter);
          } 
          break;
        case 2:
          paramXMLStreamWriter.writeEndElement();
          break;
        case 4:
          paramXMLStreamWriter.writeCharacters(xMLStreamReader.getText());
          break;
      } 
    } while (i != 8);
    xMLStreamReader.close();
  }
  
  private static void setUndeclaredPrefix(String paramString1, String paramString2, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    String str = null;
    if (paramXMLStreamWriter.getNamespaceContext() != null)
      str = paramXMLStreamWriter.getNamespaceContext().getNamespaceURI(paramString1); 
    if (str == null) {
      paramXMLStreamWriter.setPrefix(paramString1, (paramString2 != null) ? paramString2 : "");
      paramXMLStreamWriter.writeNamespace(paramString1, (paramString2 != null) ? paramString2 : "");
    } 
  }
  
  private static boolean declarePrefix(String paramString1, String paramString2, String paramString3, String paramString4) { return (paramString4 == null || (paramString3 != null && !paramString1.equals(paramString3)) || (paramString2 != null && !paramString4.equals(paramString2))); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\message\source\SourceUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SourceTreeManager {
  private Vector m_sourceTree = new Vector();
  
  URIResolver m_uriResolver;
  
  public void reset() { this.m_sourceTree = new Vector(); }
  
  public void setURIResolver(URIResolver paramURIResolver) { this.m_uriResolver = paramURIResolver; }
  
  public URIResolver getURIResolver() { return this.m_uriResolver; }
  
  public String findURIFromDoc(int paramInt) {
    int i = this.m_sourceTree.size();
    for (byte b = 0; b < i; b++) {
      SourceTree sourceTree = (SourceTree)this.m_sourceTree.elementAt(b);
      if (paramInt == sourceTree.m_root)
        return sourceTree.m_url; 
    } 
    return null;
  }
  
  public Source resolveURI(String paramString1, String paramString2, SourceLocator paramSourceLocator) throws TransformerException, IOException {
    Source source = null;
    if (null != this.m_uriResolver)
      source = this.m_uriResolver.resolve(paramString2, paramString1); 
    if (null == source) {
      String str = SystemIDResolver.getAbsoluteURI(paramString2, paramString1);
      source = new StreamSource(str);
    } 
    return source;
  }
  
  public void removeDocumentFromCache(int paramInt) {
    if (-1 == paramInt)
      return; 
    for (int i = this.m_sourceTree.size() - 1; i >= 0; i--) {
      SourceTree sourceTree = (SourceTree)this.m_sourceTree.elementAt(i);
      if (sourceTree != null && sourceTree.m_root == paramInt) {
        this.m_sourceTree.removeElementAt(i);
        return;
      } 
    } 
  }
  
  public void putDocumentInCache(int paramInt, Source paramSource) {
    int i = getNode(paramSource);
    if (-1 != i) {
      if (i != paramInt)
        throw new RuntimeException("Programmer's Error!  putDocumentInCache found reparse of doc: " + paramSource.getSystemId()); 
      return;
    } 
    if (null != paramSource.getSystemId())
      this.m_sourceTree.addElement(new SourceTree(paramInt, paramSource.getSystemId())); 
  }
  
  public int getNode(Source paramSource) {
    String str = paramSource.getSystemId();
    if (null == str)
      return -1; 
    int i = this.m_sourceTree.size();
    for (byte b = 0; b < i; b++) {
      SourceTree sourceTree = (SourceTree)this.m_sourceTree.elementAt(b);
      if (str.equals(sourceTree.m_url))
        return sourceTree.m_root; 
    } 
    return -1;
  }
  
  public int getSourceTree(String paramString1, String paramString2, SourceLocator paramSourceLocator, XPathContext paramXPathContext) throws TransformerException {
    try {
      Source source = resolveURI(paramString1, paramString2, paramSourceLocator);
      return getSourceTree(source, paramSourceLocator, paramXPathContext);
    } catch (IOException iOException) {
      throw new TransformerException(iOException.getMessage(), paramSourceLocator, iOException);
    } 
  }
  
  public int getSourceTree(Source paramSource, SourceLocator paramSourceLocator, XPathContext paramXPathContext) throws TransformerException {
    int i = getNode(paramSource);
    if (-1 != i)
      return i; 
    i = parseToNode(paramSource, paramSourceLocator, paramXPathContext);
    if (-1 != i)
      putDocumentInCache(i, paramSource); 
    return i;
  }
  
  public int parseToNode(Source paramSource, SourceLocator paramSourceLocator, XPathContext paramXPathContext) throws TransformerException {
    try {
      DTM dTM;
      Object object = paramXPathContext.getOwnerObject();
      if (null != object && object instanceof DTMWSFilter) {
        dTM = paramXPathContext.getDTM(paramSource, false, (DTMWSFilter)object, false, true);
      } else {
        dTM = paramXPathContext.getDTM(paramSource, false, null, false, true);
      } 
      return dTM.getDocument();
    } catch (Exception exception) {
      throw new TransformerException(exception.getMessage(), paramSourceLocator, exception);
    } 
  }
  
  public static XMLReader getXMLReader(Source paramSource, SourceLocator paramSourceLocator) throws TransformerException {
    try {
      XMLReader xMLReader = (paramSource instanceof SAXSource) ? ((SAXSource)paramSource).getXMLReader() : null;
      if (null == xMLReader) {
        try {
          SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
          sAXParserFactory.setNamespaceAware(true);
          SAXParser sAXParser = sAXParserFactory.newSAXParser();
          xMLReader = sAXParser.getXMLReader();
        } catch (ParserConfigurationException parserConfigurationException) {
          throw new SAXException(parserConfigurationException);
        } catch (FactoryConfigurationError factoryConfigurationError) {
          throw new SAXException(factoryConfigurationError.toString());
        } catch (NoSuchMethodError noSuchMethodError) {
        
        } catch (AbstractMethodError abstractMethodError) {}
        if (null == xMLReader)
          xMLReader = XMLReaderFactory.createXMLReader(); 
      } 
      try {
        xMLReader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      } catch (SAXException sAXException) {}
      return xMLReader;
    } catch (SAXException sAXException) {
      throw new TransformerException(sAXException.getMessage(), paramSourceLocator, sAXException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\SourceTreeManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
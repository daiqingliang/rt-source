package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class DocumentCache implements DOMCache {
  private int _size;
  
  private Map<String, CachedDocument> _references;
  
  private String[] _URIs;
  
  private int _count;
  
  private int _current;
  
  private SAXParser _parser;
  
  private XMLReader _reader;
  
  private XSLTCDTMManager _dtmManager;
  
  private static final int REFRESH_INTERVAL = 1000;
  
  public DocumentCache(int paramInt) throws SAXException {
    this(paramInt, null);
    try {
      this._dtmManager = XSLTCDTMManager.createNewDTMManagerInstance();
    } catch (Exception exception) {
      throw new SAXException(exception);
    } 
  }
  
  public DocumentCache(int paramInt, XSLTCDTMManager paramXSLTCDTMManager) throws SAXException {
    this._dtmManager = paramXSLTCDTMManager;
    this._count = 0;
    this._current = 0;
    this._size = paramInt;
    this._references = new HashMap(this._size + 2);
    this._URIs = new String[this._size];
    try {
      SAXParserFactory sAXParserFactory = SAXParserFactory.newInstance();
      try {
        sAXParserFactory.setFeature("http://xml.org/sax/features/namespaces", true);
      } catch (Exception exception) {
        sAXParserFactory.setNamespaceAware(true);
      } 
      this._parser = sAXParserFactory.newSAXParser();
      this._reader = this._parser.getXMLReader();
    } catch (ParserConfigurationException parserConfigurationException) {
      BasisLibrary.runTimeError("NAMESPACES_SUPPORT_ERR");
    } 
  }
  
  private final long getLastModified(String paramString) {
    try {
      URL uRL = new URL(paramString);
      URLConnection uRLConnection = uRL.openConnection();
      long l = uRLConnection.getLastModified();
      if (l == 0L && "file".equals(uRL.getProtocol())) {
        File file = new File(URLDecoder.decode(uRL.getFile()));
        l = file.lastModified();
      } 
      return l;
    } catch (Exception exception) {
      return System.currentTimeMillis();
    } 
  }
  
  private CachedDocument lookupDocument(String paramString) { return (CachedDocument)this._references.get(paramString); }
  
  private void insertDocument(String paramString, CachedDocument paramCachedDocument) {
    if (this._count < this._size) {
      this._URIs[this._count++] = paramString;
      this._current = 0;
    } else {
      this._references.remove(this._URIs[this._current]);
      this._URIs[this._current] = paramString;
      if (++this._current >= this._size)
        this._current = 0; 
    } 
    this._references.put(paramString, paramCachedDocument);
  }
  
  private void replaceDocument(String paramString, CachedDocument paramCachedDocument) {
    if (paramCachedDocument == null) {
      insertDocument(paramString, paramCachedDocument);
    } else {
      this._references.put(paramString, paramCachedDocument);
    } 
  }
  
  public DOM retrieveDocument(String paramString1, String paramString2, Translet paramTranslet) {
    String str = paramString2;
    if (paramString1 != null && !paramString1.equals(""))
      try {
        str = SystemIDResolver.getAbsoluteURI(str, paramString1);
      } catch (TransformerException transformerException) {} 
    CachedDocument cachedDocument;
    if ((cachedDocument = lookupDocument(str)) == null) {
      cachedDocument = new CachedDocument(str);
      if (cachedDocument == null)
        return null; 
      cachedDocument.setLastModified(getLastModified(str));
      insertDocument(str, cachedDocument);
    } else {
      long l1 = System.currentTimeMillis();
      long l2 = cachedDocument.getLastChecked();
      cachedDocument.setLastChecked(l1);
      if (l1 > l2 + 1000L) {
        cachedDocument.setLastChecked(l1);
        long l = getLastModified(str);
        if (l > cachedDocument.getLastModified()) {
          cachedDocument = new CachedDocument(str);
          if (cachedDocument == null)
            return null; 
          cachedDocument.setLastModified(getLastModified(str));
          replaceDocument(str, cachedDocument);
        } 
      } 
    } 
    DOM dOM = cachedDocument.getDocument();
    if (dOM == null)
      return null; 
    cachedDocument.incAccessCount();
    AbstractTranslet abstractTranslet = (AbstractTranslet)paramTranslet;
    abstractTranslet.prepassDocument(dOM);
    return cachedDocument.getDocument();
  }
  
  public void getStatistics(PrintWriter paramPrintWriter) {
    paramPrintWriter.println("<h2>DOM cache statistics</h2><center><table border=\"2\"><tr><td><b>Document URI</b></td><td><center><b>Build time</b></center></td><td><center><b>Access count</b></center></td><td><center><b>Last accessed</b></center></td><td><center><b>Last modified</b></center></td></tr>");
    for (byte b = 0; b < this._count; b++) {
      CachedDocument cachedDocument = (CachedDocument)this._references.get(this._URIs[b]);
      paramPrintWriter.print("<tr><td><a href=\"" + this._URIs[b] + "\"><font size=-1>" + this._URIs[b] + "</font></a></td>");
      paramPrintWriter.print("<td><center>" + cachedDocument.getLatency() + "ms</center></td>");
      paramPrintWriter.print("<td><center>" + cachedDocument.getAccessCount() + "</center></td>");
      paramPrintWriter.print("<td><center>" + new Date(cachedDocument.getLastReferenced()) + "</center></td>");
      paramPrintWriter.print("<td><center>" + new Date(cachedDocument.getLastModified()) + "</center></td>");
      paramPrintWriter.println("</tr>");
    } 
    paramPrintWriter.println("</table></center>");
  }
  
  public final class CachedDocument {
    private long _firstReferenced;
    
    private long _lastReferenced;
    
    private long _accessCount;
    
    private long _lastModified;
    
    private long _lastChecked;
    
    private long _buildTime;
    
    private DOMEnhancedForDTM _dom = null;
    
    public CachedDocument(String param1String) {
      long l = System.currentTimeMillis();
      this._firstReferenced = l;
      this._lastReferenced = l;
      this._accessCount = 0L;
      loadDocument(param1String);
      this._buildTime = System.currentTimeMillis() - l;
    }
    
    public void loadDocument(String param1String) {
      try {
        long l1 = System.currentTimeMillis();
        this._dom = (DOMEnhancedForDTM)DocumentCache.this._dtmManager.getDTM(new SAXSource(DocumentCache.this._reader, new InputSource(param1String)), false, null, true, false);
        this._dom.setDocumentURI(param1String);
        long l2 = System.currentTimeMillis() - l1;
        if (this._buildTime > 0L) {
          this._buildTime = this._buildTime + l2 >>> true;
        } else {
          this._buildTime = l2;
        } 
      } catch (Exception exception) {
        this._dom = null;
      } 
    }
    
    public DOM getDocument() { return this._dom; }
    
    public long getFirstReferenced() { return this._firstReferenced; }
    
    public long getLastReferenced() { return this._lastReferenced; }
    
    public long getAccessCount() { return this._accessCount; }
    
    public void incAccessCount() { this._accessCount++; }
    
    public long getLastModified() { return this._lastModified; }
    
    public void setLastModified(long param1Long) { this._lastModified = param1Long; }
    
    public long getLatency() { return this._buildTime; }
    
    public long getLastChecked() { return this._lastChecked; }
    
    public void setLastChecked(long param1Long) { this._lastChecked = param1Long; }
    
    public long getEstimatedSize() { return (this._dom != null) ? (this._dom.getSize() << 5) : 0L; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DocumentCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
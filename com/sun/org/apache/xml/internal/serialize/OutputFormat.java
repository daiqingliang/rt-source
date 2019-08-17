package com.sun.org.apache.xml.internal.serialize;

import java.io.UnsupportedEncodingException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

public class OutputFormat {
  private String _method;
  
  private String _version;
  
  private int _indent = 0;
  
  private String _encoding = "UTF-8";
  
  private EncodingInfo _encodingInfo = null;
  
  private boolean _allowJavaNames = false;
  
  private String _mediaType;
  
  private String _doctypeSystem;
  
  private String _doctypePublic;
  
  private boolean _omitXmlDeclaration = false;
  
  private boolean _omitDoctype = false;
  
  private boolean _omitComments = false;
  
  private boolean _stripComments = false;
  
  private boolean _standalone = false;
  
  private String[] _cdataElements;
  
  private String[] _nonEscapingElements;
  
  private String _lineSeparator = "\n";
  
  private int _lineWidth = 72;
  
  private boolean _preserve = false;
  
  private boolean _preserveEmptyAttributes = false;
  
  public OutputFormat() {}
  
  public OutputFormat(String paramString1, String paramString2, boolean paramBoolean) {
    setMethod(paramString1);
    setEncoding(paramString2);
    setIndenting(paramBoolean);
  }
  
  public OutputFormat(Document paramDocument) {
    setMethod(whichMethod(paramDocument));
    setDoctype(whichDoctypePublic(paramDocument), whichDoctypeSystem(paramDocument));
    setMediaType(whichMediaType(getMethod()));
  }
  
  public OutputFormat(Document paramDocument, String paramString, boolean paramBoolean) {
    this(paramDocument);
    setEncoding(paramString);
    setIndenting(paramBoolean);
  }
  
  public String getMethod() { return this._method; }
  
  public void setMethod(String paramString) { this._method = paramString; }
  
  public String getVersion() { return this._version; }
  
  public void setVersion(String paramString) { this._version = paramString; }
  
  public int getIndent() { return this._indent; }
  
  public boolean getIndenting() { return (this._indent > 0); }
  
  public void setIndent(int paramInt) {
    if (paramInt < 0) {
      this._indent = 0;
    } else {
      this._indent = paramInt;
    } 
  }
  
  public void setIndenting(boolean paramBoolean) {
    if (paramBoolean) {
      this._indent = 4;
      this._lineWidth = 72;
    } else {
      this._indent = 0;
      this._lineWidth = 0;
    } 
  }
  
  public String getEncoding() { return this._encoding; }
  
  public void setEncoding(String paramString) {
    this._encoding = paramString;
    this._encodingInfo = null;
  }
  
  public void setEncoding(EncodingInfo paramEncodingInfo) {
    this._encoding = paramEncodingInfo.getIANAName();
    this._encodingInfo = paramEncodingInfo;
  }
  
  public EncodingInfo getEncodingInfo() throws UnsupportedEncodingException {
    if (this._encodingInfo == null)
      this._encodingInfo = Encodings.getEncodingInfo(this._encoding, this._allowJavaNames); 
    return this._encodingInfo;
  }
  
  public void setAllowJavaNames(boolean paramBoolean) { this._allowJavaNames = paramBoolean; }
  
  public boolean setAllowJavaNames() { return this._allowJavaNames; }
  
  public String getMediaType() { return this._mediaType; }
  
  public void setMediaType(String paramString) { this._mediaType = paramString; }
  
  public void setDoctype(String paramString1, String paramString2) {
    this._doctypePublic = paramString1;
    this._doctypeSystem = paramString2;
  }
  
  public String getDoctypePublic() { return this._doctypePublic; }
  
  public String getDoctypeSystem() { return this._doctypeSystem; }
  
  public boolean getOmitComments() { return this._omitComments; }
  
  public void setOmitComments(boolean paramBoolean) { this._omitComments = paramBoolean; }
  
  public boolean getOmitDocumentType() { return this._omitDoctype; }
  
  public void setOmitDocumentType(boolean paramBoolean) { this._omitDoctype = paramBoolean; }
  
  public boolean getOmitXMLDeclaration() { return this._omitXmlDeclaration; }
  
  public void setOmitXMLDeclaration(boolean paramBoolean) { this._omitXmlDeclaration = paramBoolean; }
  
  public boolean getStandalone() { return this._standalone; }
  
  public void setStandalone(boolean paramBoolean) { this._standalone = paramBoolean; }
  
  public String[] getCDataElements() { return this._cdataElements; }
  
  public boolean isCDataElement(String paramString) {
    if (this._cdataElements == null)
      return false; 
    for (byte b = 0; b < this._cdataElements.length; b++) {
      if (this._cdataElements[b].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public void setCDataElements(String[] paramArrayOfString) { this._cdataElements = paramArrayOfString; }
  
  public String[] getNonEscapingElements() { return this._nonEscapingElements; }
  
  public boolean isNonEscapingElement(String paramString) {
    if (this._nonEscapingElements == null)
      return false; 
    for (byte b = 0; b < this._nonEscapingElements.length; b++) {
      if (this._nonEscapingElements[b].equals(paramString))
        return true; 
    } 
    return false;
  }
  
  public void setNonEscapingElements(String[] paramArrayOfString) { this._nonEscapingElements = paramArrayOfString; }
  
  public String getLineSeparator() { return this._lineSeparator; }
  
  public void setLineSeparator(String paramString) {
    if (paramString == null) {
      this._lineSeparator = "\n";
    } else {
      this._lineSeparator = paramString;
    } 
  }
  
  public boolean getPreserveSpace() { return this._preserve; }
  
  public void setPreserveSpace(boolean paramBoolean) { this._preserve = paramBoolean; }
  
  public int getLineWidth() { return this._lineWidth; }
  
  public void setLineWidth(int paramInt) {
    if (paramInt <= 0) {
      this._lineWidth = 0;
    } else {
      this._lineWidth = paramInt;
    } 
  }
  
  public boolean getPreserveEmptyAttributes() { return this._preserveEmptyAttributes; }
  
  public void setPreserveEmptyAttributes(boolean paramBoolean) { this._preserveEmptyAttributes = paramBoolean; }
  
  public char getLastPrintable() { return (getEncoding() != null && getEncoding().equalsIgnoreCase("ASCII")) ? 'ÿ' : Character.MAX_VALUE; }
  
  public static String whichMethod(Document paramDocument) {
    if (paramDocument instanceof org.w3c.dom.html.HTMLDocument)
      return "html"; 
    for (Node node = paramDocument.getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeType() == 1)
        return node.getNodeName().equalsIgnoreCase("html") ? "html" : (node.getNodeName().equalsIgnoreCase("root") ? "fop" : "xml"); 
      if (node.getNodeType() == 3) {
        String str = node.getNodeValue();
        for (byte b = 0; b < str.length(); b++) {
          if (str.charAt(b) != ' ' && str.charAt(b) != '\n' && str.charAt(b) != '\t' && str.charAt(b) != '\r')
            return "xml"; 
        } 
      } 
    } 
    return "xml";
  }
  
  public static String whichDoctypePublic(Document paramDocument) {
    DocumentType documentType = paramDocument.getDoctype();
    if (documentType != null)
      try {
        return documentType.getPublicId();
      } catch (Error error) {} 
    return (paramDocument instanceof org.w3c.dom.html.HTMLDocument) ? "-//W3C//DTD XHTML 1.0 Strict//EN" : null;
  }
  
  public static String whichDoctypeSystem(Document paramDocument) {
    DocumentType documentType = paramDocument.getDoctype();
    if (documentType != null)
      try {
        return documentType.getSystemId();
      } catch (Error error) {} 
    return (paramDocument instanceof org.w3c.dom.html.HTMLDocument) ? "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" : null;
  }
  
  public static String whichMediaType(String paramString) { return paramString.equalsIgnoreCase("xml") ? "text/xml" : (paramString.equalsIgnoreCase("html") ? "text/html" : (paramString.equalsIgnoreCase("xhtml") ? "text/html" : (paramString.equalsIgnoreCase("text") ? "text/plain" : (paramString.equalsIgnoreCase("fop") ? "application/pdf" : null)))); }
  
  public static class DTD {
    public static final String HTMLPublicId = "-//W3C//DTD HTML 4.01//EN";
    
    public static final String HTMLSystemId = "http://www.w3.org/TR/html4/strict.dtd";
    
    public static final String XHTMLPublicId = "-//W3C//DTD XHTML 1.0 Strict//EN";
    
    public static final String XHTMLSystemId = "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd";
  }
  
  public static class Defaults {
    public static final int Indent = 4;
    
    public static final String Encoding = "UTF-8";
    
    public static final int LineWidth = 72;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\OutputFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
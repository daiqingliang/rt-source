package com.sun.org.apache.xml.internal.serializer;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

public final class ToXMLStream extends ToStream {
  boolean m_cdataTagOpen = false;
  
  private static CharInfo m_xmlcharInfo = CharInfo.getCharInfoInternal("com.sun.org.apache.xml.internal.serializer.XMLEntities", "xml");
  
  public ToXMLStream() {
    initCDATA();
    this.m_prefixMap = new NamespaceMappings();
  }
  
  public void CopyFrom(ToXMLStream paramToXMLStream) {
    this.m_writer = paramToXMLStream.m_writer;
    String str = paramToXMLStream.getEncoding();
    setEncoding(str);
    setOmitXMLDeclaration(paramToXMLStream.getOmitXMLDeclaration());
    this.m_ispreserve = paramToXMLStream.m_ispreserve;
    this.m_preserves = paramToXMLStream.m_preserves;
    this.m_isprevtext = paramToXMLStream.m_isprevtext;
    this.m_doIndent = paramToXMLStream.m_doIndent;
    setIndentAmount(paramToXMLStream.getIndentAmount());
    this.m_startNewLine = paramToXMLStream.m_startNewLine;
    this.m_needToOutputDocTypeDecl = paramToXMLStream.m_needToOutputDocTypeDecl;
    setDoctypeSystem(paramToXMLStream.getDoctypeSystem());
    setDoctypePublic(paramToXMLStream.getDoctypePublic());
    setStandalone(paramToXMLStream.getStandalone());
    setMediaType(paramToXMLStream.getMediaType());
    this.m_maxCharacter = paramToXMLStream.m_maxCharacter;
    this.m_encodingInfo = paramToXMLStream.m_encodingInfo;
    this.m_spaceBeforeClose = paramToXMLStream.m_spaceBeforeClose;
    this.m_cdataStartCalled = paramToXMLStream.m_cdataStartCalled;
  }
  
  public void startDocumentInternal() {
    if (this.m_needToCallStartDocument) {
      super.startDocumentInternal();
      this.m_needToCallStartDocument = false;
      if (this.m_inEntityRef)
        return; 
      this.m_needToOutputDocTypeDecl = true;
      this.m_startNewLine = false;
      if (!getOmitXMLDeclaration()) {
        String str3;
        String str1 = Encodings.getMimeEncoding(getEncoding());
        String str2 = getVersion();
        if (str2 == null)
          str2 = "1.0"; 
        if (this.m_standaloneWasSpecified) {
          str3 = " standalone=\"" + getStandalone() + "\"";
        } else {
          str3 = "";
        } 
        try {
          Writer writer = this.m_writer;
          writer.write("<?xml version=\"");
          writer.write(str2);
          writer.write("\" encoding=\"");
          writer.write(str1);
          writer.write(34);
          writer.write(str3);
          writer.write("?>");
          if (this.m_doIndent && (this.m_standaloneWasSpecified || getDoctypePublic() != null || getDoctypeSystem() != null || this.m_isStandalone))
            writer.write(this.m_lineSep, 0, this.m_lineSepLen); 
        } catch (IOException iOException) {
          throw new SAXException(iOException);
        } 
      } 
    } 
  }
  
  public void endDocument() {
    flushPending();
    if (this.m_doIndent && !this.m_isprevtext)
      try {
        outputLineSep();
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      }  
    flushWriter();
    if (this.m_tracer != null)
      fireEndDoc(); 
  }
  
  public void startPreserving() {
    this.m_preserves.push(true);
    this.m_ispreserve = true;
  }
  
  public void endPreserving() { this.m_ispreserve = this.m_preserves.isEmpty() ? false : this.m_preserves.pop(); }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (this.m_inEntityRef)
      return; 
    flushPending();
    if (paramString1.equals("javax.xml.transform.disable-output-escaping")) {
      startNonEscaping();
    } else if (paramString1.equals("javax.xml.transform.enable-output-escaping")) {
      endNonEscaping();
    } else {
      try {
        if (this.m_elemContext.m_startTagOpen) {
          closeStartTag();
          this.m_elemContext.m_startTagOpen = false;
        } else if (this.m_needToCallStartDocument) {
          startDocumentInternal();
        } 
        if (shouldIndent())
          indent(); 
        Writer writer = this.m_writer;
        writer.write("<?");
        writer.write(paramString1);
        if (paramString2.length() > 0 && !Character.isSpaceChar(paramString2.charAt(0)))
          writer.write(32); 
        int i = paramString2.indexOf("?>");
        if (i >= 0) {
          if (i > 0)
            writer.write(paramString2.substring(0, i)); 
          writer.write("? >");
          if (i + 2 < paramString2.length())
            writer.write(paramString2.substring(i + 2)); 
        } else {
          writer.write(paramString2);
        } 
        writer.write(63);
        writer.write(62);
        if (this.m_elemContext.m_currentElemDepth <= 0 && this.m_isStandalone)
          writer.write(this.m_lineSep, 0, this.m_lineSepLen); 
        this.m_startNewLine = true;
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      } 
    } 
    if (this.m_tracer != null)
      fireEscapingEvent(paramString1, paramString2); 
  }
  
  public void entityReference(String paramString) throws SAXException {
    if (this.m_elemContext.m_startTagOpen) {
      closeStartTag();
      this.m_elemContext.m_startTagOpen = false;
    } 
    try {
      if (shouldIndent())
        indent(); 
      Writer writer = this.m_writer;
      writer.write(38);
      writer.write(paramString);
      writer.write(59);
    } catch (IOException iOException) {
      throw new SAXException(iOException);
    } 
    if (this.m_tracer != null)
      fireEntityReference(paramString); 
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt) throws SAXException {
    if (this.m_elemContext.m_startTagOpen)
      try {
        String str = patchName(paramString1);
        Writer writer = this.m_writer;
        if ((paramInt & true) > 0 && m_xmlcharInfo.onlyQuotAmpLtGt) {
          writer.write(32);
          writer.write(str);
          writer.write("=\"");
          writer.write(paramString2);
          writer.write(34);
        } else {
          writer.write(32);
          writer.write(str);
          writer.write("=\"");
          writeAttrString(writer, paramString2, getEncoding());
          writer.write(34);
        } 
      } catch (IOException iOException) {
        throw new SAXException(iOException);
      }  
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) throws SAXException {
    if (this.m_elemContext.m_startTagOpen) {
      boolean bool = addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
      if (bool && !paramBoolean && !paramString3.startsWith("xmlns")) {
        String str = ensureAttributesNamespaceIsDeclared(paramString1, paramString2, paramString3);
        if (str != null && paramString3 != null && !paramString3.startsWith(str))
          paramString3 = str + ":" + paramString2; 
      } 
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, paramBoolean);
    } else {
      String str = Utils.messages.createMessage("ER_ILLEGAL_ATTRIBUTE_POSITION", new Object[] { paramString2 });
      try {
        Transformer transformer = getTransformer();
        ErrorListener errorListener = transformer.getErrorListener();
        if (null != errorListener && this.m_sourceLocator != null) {
          errorListener.warning(new TransformerException(str, this.m_sourceLocator));
        } else {
          System.out.println(str);
        } 
      } catch (Exception exception) {}
    } 
  }
  
  public void endElement(String paramString) throws SAXException { endElement(null, null, paramString); }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2) throws SAXException {
    if (this.m_elemContext.m_elementURI == null) {
      String str = getPrefixPart(this.m_elemContext.m_elementName);
      if (str == null && "".equals(paramString1))
        this.m_elemContext.m_elementURI = paramString2; 
    } 
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  protected boolean pushNamespace(String paramString1, String paramString2) {
    try {
      if (this.m_prefixMap.pushNamespace(paramString1, paramString2, this.m_elemContext.m_currentElemDepth)) {
        startPrefixMapping(paramString1, paramString2);
        return true;
      } 
    } catch (SAXException sAXException) {}
    return false;
  }
  
  public boolean reset() {
    boolean bool = false;
    if (super.reset()) {
      resetToXMLStream();
      bool = true;
    } 
    return bool;
  }
  
  private void resetToXMLStream() { this.m_cdataTagOpen = false; }
  
  private String getXMLVersion() {
    String str = getVersion();
    if (str == null || str.equals("1.0")) {
      str = "1.0";
    } else if (str.equals("1.1")) {
      str = "1.1";
    } else {
      String str1 = Utils.messages.createMessage("ER_XML_VERSION_NOT_SUPPORTED", new Object[] { str });
      try {
        Transformer transformer = getTransformer();
        ErrorListener errorListener = transformer.getErrorListener();
        if (null != errorListener && this.m_sourceLocator != null) {
          errorListener.warning(new TransformerException(str1, this.m_sourceLocator));
        } else {
          System.out.println(str1);
        } 
      } catch (Exception exception) {}
      str = "1.0";
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToXMLStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
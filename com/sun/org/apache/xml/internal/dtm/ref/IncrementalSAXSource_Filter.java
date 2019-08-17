package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.ThreadControllerWrapper;
import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

final class IncrementalSAXSource_Filter implements IncrementalSAXSource, ContentHandler, DTDHandler, LexicalHandler, ErrorHandler, Runnable {
  boolean DEBUG = false;
  
  private CoroutineManager fCoroutineManager = null;
  
  private int fControllerCoroutineID = -1;
  
  private int fSourceCoroutineID = -1;
  
  private ContentHandler clientContentHandler = null;
  
  private LexicalHandler clientLexicalHandler = null;
  
  private DTDHandler clientDTDHandler = null;
  
  private ErrorHandler clientErrorHandler = null;
  
  private int eventcounter;
  
  private int frequency = 5;
  
  private boolean fNoMoreEvents = false;
  
  private XMLReader fXMLReader = null;
  
  private InputSource fXMLReaderInputSource = null;
  
  public IncrementalSAXSource_Filter() { init(new CoroutineManager(), -1, -1); }
  
  public IncrementalSAXSource_Filter(CoroutineManager paramCoroutineManager, int paramInt) { init(paramCoroutineManager, paramInt, -1); }
  
  public static IncrementalSAXSource createIncrementalSAXSource(CoroutineManager paramCoroutineManager, int paramInt) { return new IncrementalSAXSource_Filter(paramCoroutineManager, paramInt); }
  
  public void init(CoroutineManager paramCoroutineManager, int paramInt1, int paramInt2) {
    if (paramCoroutineManager == null)
      paramCoroutineManager = new CoroutineManager(); 
    this.fCoroutineManager = paramCoroutineManager;
    this.fControllerCoroutineID = paramCoroutineManager.co_joinCoroutineSet(paramInt1);
    this.fSourceCoroutineID = paramCoroutineManager.co_joinCoroutineSet(paramInt2);
    if (this.fControllerCoroutineID == -1 || this.fSourceCoroutineID == -1)
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_COJOINROUTINESET_FAILED", null)); 
    this.fNoMoreEvents = false;
    this.eventcounter = this.frequency;
  }
  
  public void setXMLReader(XMLReader paramXMLReader) {
    this.fXMLReader = paramXMLReader;
    paramXMLReader.setContentHandler(this);
    paramXMLReader.setDTDHandler(this);
    paramXMLReader.setErrorHandler(this);
    try {
      paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
    } catch (SAXNotRecognizedException sAXNotRecognizedException) {
    
    } catch (SAXNotSupportedException sAXNotSupportedException) {}
  }
  
  public void setContentHandler(ContentHandler paramContentHandler) { this.clientContentHandler = paramContentHandler; }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) { this.clientDTDHandler = paramDTDHandler; }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this.clientLexicalHandler = paramLexicalHandler; }
  
  public void setErrHandler(ErrorHandler paramErrorHandler) { this.clientErrorHandler = paramErrorHandler; }
  
  public void setReturnFrequency(int paramInt) {
    if (paramInt < 1)
      paramInt = 1; 
    this.frequency = this.eventcounter = paramInt;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.characters(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endDocument() {
    if (this.clientContentHandler != null)
      this.clientContentHandler.endDocument(); 
    this.eventcounter = 0;
    co_yield(false);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.endElement(paramString1, paramString2, paramString3); 
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.endPrefixMapping(paramString); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.processingInstruction(paramString1, paramString2); 
  }
  
  public void setDocumentLocator(Locator paramLocator) {
    if (--this.eventcounter <= 0)
      this.eventcounter = this.frequency; 
    if (this.clientContentHandler != null)
      this.clientContentHandler.setDocumentLocator(paramLocator); 
  }
  
  public void skippedEntity(String paramString) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.skippedEntity(paramString); 
  }
  
  public void startDocument() {
    co_entry_pause();
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.startDocument(); 
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.startElement(paramString1, paramString2, paramString3, paramAttributes); 
  }
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
    if (this.clientContentHandler != null)
      this.clientContentHandler.startPrefixMapping(paramString1, paramString2); 
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.comment(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void endCDATA() {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.endCDATA(); 
  }
  
  public void endDTD() {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.endDTD(); 
  }
  
  public void endEntity(String paramString) throws SAXException {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.endEntity(paramString); 
  }
  
  public void startCDATA() {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.startCDATA(); 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.startDTD(paramString1, paramString2, paramString3); 
  }
  
  public void startEntity(String paramString) throws SAXException {
    if (null != this.clientLexicalHandler)
      this.clientLexicalHandler.startEntity(paramString); 
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (null != this.clientDTDHandler)
      this.clientDTDHandler.notationDecl(paramString1, paramString2, paramString3); 
  }
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException {
    if (null != this.clientDTDHandler)
      this.clientDTDHandler.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4); 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXException {
    if (null != this.clientErrorHandler)
      this.clientErrorHandler.error(paramSAXParseException); 
  }
  
  public void fatalError(SAXParseException paramSAXParseException) throws SAXException {
    if (null != this.clientErrorHandler)
      this.clientErrorHandler.error(paramSAXParseException); 
    this.eventcounter = 0;
    co_yield(false);
  }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXException {
    if (null != this.clientErrorHandler)
      this.clientErrorHandler.error(paramSAXParseException); 
  }
  
  public int getSourceCoroutineID() { return this.fSourceCoroutineID; }
  
  public int getControllerCoroutineID() { return this.fControllerCoroutineID; }
  
  public CoroutineManager getCoroutineManager() { return this.fCoroutineManager; }
  
  protected void count_and_yield(boolean paramBoolean) throws SAXException {
    if (!paramBoolean)
      this.eventcounter = 0; 
    if (--this.eventcounter <= 0) {
      co_yield(true);
      this.eventcounter = this.frequency;
    } 
  }
  
  private void co_entry_pause() {
    if (this.fCoroutineManager == null)
      init(null, -1, -1); 
    try {
      Object object = this.fCoroutineManager.co_entry_pause(this.fSourceCoroutineID);
      if (object == Boolean.FALSE)
        co_yield(false); 
    } catch (NoSuchMethodException noSuchMethodException) {
      if (this.DEBUG)
        noSuchMethodException.printStackTrace(); 
      throw new SAXException(noSuchMethodException);
    } 
  }
  
  private void co_yield(boolean paramBoolean) throws SAXException {
    if (this.fNoMoreEvents)
      return; 
    try {
      Object object = Boolean.FALSE;
      if (paramBoolean)
        object = this.fCoroutineManager.co_resume(Boolean.TRUE, this.fSourceCoroutineID, this.fControllerCoroutineID); 
      if (object == Boolean.FALSE) {
        this.fNoMoreEvents = true;
        if (this.fXMLReader != null)
          throw new StopException(); 
        this.fCoroutineManager.co_exit_to(Boolean.FALSE, this.fSourceCoroutineID, this.fControllerCoroutineID);
      } 
    } catch (NoSuchMethodException noSuchMethodException) {
      this.fNoMoreEvents = true;
      this.fCoroutineManager.co_exit(this.fSourceCoroutineID);
      throw new SAXException(noSuchMethodException);
    } 
  }
  
  public void startParse(InputSource paramInputSource) throws SAXException {
    if (this.fNoMoreEvents)
      throw new SAXException(XMLMessages.createXMLMessage("ER_INCRSAXSRCFILTER_NOT_RESTARTABLE", null)); 
    if (this.fXMLReader == null)
      throw new SAXException(XMLMessages.createXMLMessage("ER_XMLRDR_NOT_BEFORE_STARTPARSE", null)); 
    this.fXMLReaderInputSource = paramInputSource;
    ThreadControllerWrapper.runThread(this, -1);
  }
  
  public void run() {
    if (this.fXMLReader == null)
      return; 
    if (this.DEBUG)
      System.out.println("IncrementalSAXSource_Filter parse thread launched"); 
    SAXException sAXException = Boolean.FALSE;
    try {
      this.fXMLReader.parse(this.fXMLReaderInputSource);
    } catch (IOException iOException2) {
      IOException iOException1 = iOException2;
    } catch (StopException stopException) {
      if (this.DEBUG)
        System.out.println("Active IncrementalSAXSource_Filter normal stop exception"); 
    } catch (SAXException sAXException1) {
      Exception exception = sAXException1.getException();
      if (exception instanceof StopException) {
        if (this.DEBUG)
          System.out.println("Active IncrementalSAXSource_Filter normal stop exception"); 
      } else {
        if (this.DEBUG) {
          System.out.println("Active IncrementalSAXSource_Filter UNEXPECTED SAX exception: " + exception);
          exception.printStackTrace();
        } 
        sAXException = sAXException1;
      } 
    } 
    this.fXMLReader = null;
    try {
      this.fNoMoreEvents = true;
      this.fCoroutineManager.co_exit_to(sAXException, this.fSourceCoroutineID, this.fControllerCoroutineID);
    } catch (NoSuchMethodException noSuchMethodException) {
      noSuchMethodException.printStackTrace(System.err);
      this.fCoroutineManager.co_exit(this.fSourceCoroutineID);
    } 
  }
  
  public Object deliverMoreNodes(boolean paramBoolean) {
    if (this.fNoMoreEvents)
      return Boolean.FALSE; 
    try {
      Object object = this.fCoroutineManager.co_resume(paramBoolean ? Boolean.TRUE : Boolean.FALSE, this.fControllerCoroutineID, this.fSourceCoroutineID);
      if (object == Boolean.FALSE)
        this.fCoroutineManager.co_exit(this.fControllerCoroutineID); 
      return object;
    } catch (NoSuchMethodException null) {
      return null;
    } 
  }
  
  class StopException extends RuntimeException {
    static final long serialVersionUID = -1129245796185754956L;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\IncrementalSAXSource_Filter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
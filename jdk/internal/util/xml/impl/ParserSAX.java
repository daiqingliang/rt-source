package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.ContentHandler;
import jdk.internal.org.xml.sax.DTDHandler;
import jdk.internal.org.xml.sax.EntityResolver;
import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.Locator;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;

final class ParserSAX extends Parser implements XMLReader, Locator {
  public static final String FEATURE_NS = "http://xml.org/sax/features/namespaces";
  
  public static final String FEATURE_PREF = "http://xml.org/sax/features/namespace-prefixes";
  
  private boolean mFNamespaces = true;
  
  private boolean mFPrefixes = false;
  
  private DefaultHandler mHand = new DefaultHandler();
  
  private ContentHandler mHandCont = this.mHand;
  
  private DTDHandler mHandDtd = this.mHand;
  
  private ErrorHandler mHandErr = this.mHand;
  
  private EntityResolver mHandEnt = this.mHand;
  
  public ContentHandler getContentHandler() { return (this.mHandCont != this.mHand) ? this.mHandCont : null; }
  
  public void setContentHandler(ContentHandler paramContentHandler) {
    if (paramContentHandler == null)
      throw new NullPointerException(); 
    this.mHandCont = paramContentHandler;
  }
  
  public DTDHandler getDTDHandler() { return (this.mHandDtd != this.mHand) ? this.mHandDtd : null; }
  
  public void setDTDHandler(DTDHandler paramDTDHandler) {
    if (paramDTDHandler == null)
      throw new NullPointerException(); 
    this.mHandDtd = paramDTDHandler;
  }
  
  public ErrorHandler getErrorHandler() { return (this.mHandErr != this.mHand) ? this.mHandErr : null; }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler) {
    if (paramErrorHandler == null)
      throw new NullPointerException(); 
    this.mHandErr = paramErrorHandler;
  }
  
  public EntityResolver getEntityResolver() { return (this.mHandEnt != this.mHand) ? this.mHandEnt : null; }
  
  public void setEntityResolver(EntityResolver paramEntityResolver) {
    if (paramEntityResolver == null)
      throw new NullPointerException(); 
    this.mHandEnt = paramEntityResolver;
  }
  
  public String getPublicId() { return (this.mInp != null) ? this.mInp.pubid : null; }
  
  public String getSystemId() { return (this.mInp != null) ? this.mInp.sysid : null; }
  
  public int getLineNumber() { return -1; }
  
  public int getColumnNumber() { return -1; }
  
  public void parse(String paramString) throws IOException, SAXException { parse(new InputSource(paramString)); }
  
  public void parse(InputSource paramInputSource) throws IOException, SAXException {
    if (paramInputSource == null)
      throw new IllegalArgumentException(""); 
    this.mInp = new Input(512);
    this.mPh = -1;
    try {
      setinp(paramInputSource);
    } catch (SAXException sAXException) {
      throw sAXException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      panic(exception.toString());
    } 
    parse();
  }
  
  public void parse(InputStream paramInputStream, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputStream == null || paramDefaultHandler == null)
      throw new IllegalArgumentException(""); 
    parse(new InputSource(paramInputStream), paramDefaultHandler);
  }
  
  public void parse(InputSource paramInputSource, DefaultHandler paramDefaultHandler) throws SAXException, IOException {
    if (paramInputSource == null || paramDefaultHandler == null)
      throw new IllegalArgumentException(""); 
    this.mHandCont = paramDefaultHandler;
    this.mHandDtd = paramDefaultHandler;
    this.mHandErr = paramDefaultHandler;
    this.mHandEnt = paramDefaultHandler;
    this.mInp = new Input(512);
    this.mPh = -1;
    try {
      setinp(paramInputSource);
    } catch (SAXException|IOException|RuntimeException sAXException) {
      throw sAXException;
    } catch (Exception exception) {
      panic(exception.toString());
    } 
    parse();
  }
  
  private void parse() {
    init();
    try {
      this.mHandCont.setDocumentLocator(this);
      this.mHandCont.startDocument();
      if (this.mPh != 1)
        this.mPh = 1; 
      int i = 0;
      do {
        wsskip();
        switch (i = step()) {
          case 1:
          case 2:
            this.mPh = 4;
            break;
          case 6:
          case 8:
            break;
          case 9:
            if (this.mPh >= 3)
              panic(""); 
            this.mPh = 3;
            break;
          default:
            panic("");
            break;
        } 
      } while (this.mPh < 4);
      do {
        switch (i) {
          case 1:
          case 2:
            if (this.mIsNSAware == true) {
              this.mHandCont.startElement(this.mElm.value, this.mElm.name, "", this.mAttrs);
            } else {
              this.mHandCont.startElement("", "", this.mElm.name, this.mAttrs);
            } 
            if (i == 2) {
              i = step();
              break;
            } 
          case 3:
            if (this.mIsNSAware == true) {
              this.mHandCont.endElement(this.mElm.value, this.mElm.name, "");
            } else {
              this.mHandCont.endElement("", "", this.mElm.name);
            } 
            while (this.mPref.list == this.mElm) {
              this.mHandCont.endPrefixMapping(this.mPref.name);
              this.mPref = del(this.mPref);
            } 
            this.mElm = del(this.mElm);
            if (this.mElm == null) {
              this.mPh = 5;
              break;
            } 
            i = step();
            break;
          case 4:
          case 5:
          case 6:
          case 7:
          case 8:
          case 10:
            i = step();
            break;
          default:
            panic("");
            break;
        } 
      } while (this.mPh == 4);
      while (wsskip() != Character.MAX_VALUE) {
        switch (step()) {
          case 6:
          case 8:
            break;
          default:
            panic("");
            break;
        } 
        if (this.mPh != 5)
          break; 
      } 
      this.mPh = 6;
    } catch (SAXException sAXException) {
      throw sAXException;
    } catch (IOException iOException) {
      throw iOException;
    } catch (RuntimeException runtimeException) {
      throw runtimeException;
    } catch (Exception exception) {
      panic(exception.toString());
    } finally {
      this.mHandCont.endDocument();
      cleanup();
    } 
  }
  
  protected void docType(String paramString1, String paramString2, String paramString3) throws SAXException { this.mHandDtd.notationDecl(paramString1, paramString2, paramString3); }
  
  protected void comm(char[] paramArrayOfChar, int paramInt) {}
  
  protected void pi(String paramString1, String paramString2) throws SAXException { this.mHandCont.processingInstruction(paramString1, paramString2); }
  
  protected void newPrefix() { this.mHandCont.startPrefixMapping(this.mPref.name, this.mPref.value); }
  
  protected void skippedEnt(String paramString) throws IOException, SAXException { this.mHandCont.skippedEntity(paramString); }
  
  protected InputSource resolveEnt(String paramString1, String paramString2, String paramString3) throws SAXException, IOException { return this.mHandEnt.resolveEntity(paramString2, paramString3); }
  
  protected void notDecl(String paramString1, String paramString2, String paramString3) throws SAXException { this.mHandDtd.notationDecl(paramString1, paramString2, paramString3); }
  
  protected void unparsedEntDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws SAXException { this.mHandDtd.unparsedEntityDecl(paramString1, paramString2, paramString3, paramString4); }
  
  protected void panic(String paramString) throws IOException, SAXException {
    SAXParseException sAXParseException = new SAXParseException(paramString, this);
    this.mHandErr.fatalError(sAXParseException);
    throw sAXParseException;
  }
  
  protected void bflash() {
    if (this.mBuffIdx >= 0) {
      this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
      this.mBuffIdx = -1;
    } 
  }
  
  protected void bflash_ws() {
    if (this.mBuffIdx >= 0) {
      this.mHandCont.characters(this.mBuff, 0, this.mBuffIdx + 1);
      this.mBuffIdx = -1;
    } 
  }
  
  public boolean getFeature(String paramString) { throw new UnsupportedOperationException("Not supported yet."); }
  
  public void setFeature(String paramString, boolean paramBoolean) { throw new UnsupportedOperationException("Not supported yet."); }
  
  public Object getProperty(String paramString) { throw new UnsupportedOperationException("Not supported yet."); }
  
  public void setProperty(String paramString, Object paramObject) { throw new UnsupportedOperationException("Not supported yet."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\ParserSAX.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
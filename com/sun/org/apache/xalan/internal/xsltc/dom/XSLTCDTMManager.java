package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.trax.DOM2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXEvent2SAX;
import com.sun.org.apache.xalan.internal.xsltc.trax.StAXStream2SAX;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stax.StAXSource;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XSLTCDTMManager extends DTMManagerDefault {
  private static final boolean DUMPTREE = false;
  
  private static final boolean DEBUG = false;
  
  public static XSLTCDTMManager newInstance() { return new XSLTCDTMManager(); }
  
  public static XSLTCDTMManager createNewDTMManagerInstance() { return newInstance(); }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3) { return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, true, false); }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) { return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, paramBoolean4, false); }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5) { return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, false, 0, paramBoolean4, paramBoolean5); }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, boolean paramBoolean5) { return getDTM(paramSource, paramBoolean1, paramDTMWSFilter, paramBoolean2, paramBoolean3, paramBoolean4, paramInt, paramBoolean5, false); }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt, boolean paramBoolean5, boolean paramBoolean6) {
    int i = getFirstFreeDTMID();
    int j = i << 16;
    if (null != paramSource && paramSource instanceof StAXSource) {
      SAXImpl sAXImpl;
      StAXSource stAXSource = (StAXSource)paramSource;
      StAXEvent2SAX stAXEvent2SAX = null;
      StAXStream2SAX stAXStream2SAX = null;
      if (stAXSource.getXMLEventReader() != null) {
        sAXImpl = stAXSource.getXMLEventReader();
        stAXEvent2SAX = new StAXEvent2SAX(sAXImpl);
      } else if (stAXSource.getXMLStreamReader() != null) {
        sAXImpl = stAXSource.getXMLStreamReader();
        stAXStream2SAX = new StAXStream2SAX(sAXImpl);
      } 
      if (paramInt <= 0) {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      } 
      sAXImpl.setDocumentURI(paramSource.getSystemId());
      addDTM(sAXImpl, i, 0);
      try {
        if (stAXEvent2SAX != null) {
          stAXEvent2SAX.setContentHandler(sAXImpl);
          stAXEvent2SAX.parse();
        } else if (stAXStream2SAX != null) {
          stAXStream2SAX.setContentHandler(sAXImpl);
          stAXStream2SAX.parse();
        } 
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      } 
      return sAXImpl;
    } 
    if (null != paramSource && paramSource instanceof DOMSource) {
      SAXImpl sAXImpl;
      DOMSource dOMSource = (DOMSource)paramSource;
      Node node = dOMSource.getNode();
      DOM2SAX dOM2SAX = new DOM2SAX(node);
      if (paramInt <= 0) {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      } 
      sAXImpl.setDocumentURI(paramSource.getSystemId());
      addDTM(sAXImpl, i, 0);
      dOM2SAX.setContentHandler(sAXImpl);
      try {
        dOM2SAX.parse();
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      } 
      return sAXImpl;
    } 
    boolean bool1 = (null != paramSource) ? (paramSource instanceof SAXSource) : 1;
    boolean bool2 = (null != paramSource) ? (paramSource instanceof javax.xml.transform.stream.StreamSource) : 0;
    if (bool1 || bool2) {
      SAXImpl sAXImpl;
      InputSource inputSource;
      if (null == paramSource) {
        inputSource = null;
        xMLReader = null;
        paramBoolean4 = false;
      } else {
        xMLReader = getXMLReader(paramSource);
        inputSource = SAXSource.sourceToInputSource(paramSource);
        sAXImpl = inputSource.getSystemId();
        if (null != sAXImpl) {
          try {
            sAXImpl = SystemIDResolver.getAbsoluteURI(sAXImpl);
          } catch (Exception exception) {
            System.err.println("Can not absolutize URL: " + sAXImpl);
          } 
          inputSource.setSystemId(sAXImpl);
        } 
      } 
      if (paramInt <= 0) {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, 512, paramBoolean5, paramBoolean6);
      } else {
        sAXImpl = new SAXImpl(this, paramSource, j, paramDTMWSFilter, null, paramBoolean3, paramInt, paramBoolean5, paramBoolean6);
      } 
      addDTM(sAXImpl, i, 0);
      if (null == xMLReader)
        return sAXImpl; 
      xMLReader.setContentHandler(sAXImpl.getBuilder());
      if (!paramBoolean4 || null == xMLReader.getDTDHandler())
        xMLReader.setDTDHandler(sAXImpl); 
      if (!paramBoolean4 || null == xMLReader.getErrorHandler())
        xMLReader.setErrorHandler(sAXImpl); 
      try {
        xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAXImpl);
      } catch (SAXNotRecognizedException sAXNotRecognizedException) {
      
      } catch (SAXNotSupportedException sAXNotSupportedException) {}
      try {
        xMLReader.parse(inputSource);
      } catch (RuntimeException runtimeException) {
        throw runtimeException;
      } catch (Exception exception) {
        throw new WrappedRuntimeException(exception);
      } finally {
        if (!paramBoolean4)
          releaseXMLReader(xMLReader); 
      } 
      return sAXImpl;
    } 
    throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[] { paramSource }));
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\XSLTCDTMManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
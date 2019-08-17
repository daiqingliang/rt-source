package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTM;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2DTM;
import com.sun.org.apache.xml.internal.dtm.ref.sax2dtm.SAX2RTFDTM;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLReaderManager;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import jdk.xml.internal.JdkXmlUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DTMManagerDefault extends DTMManager {
  private static final boolean DUMPTREE = false;
  
  private static final boolean DEBUG = false;
  
  protected DTM[] m_dtms = new DTM[256];
  
  int[] m_dtm_offsets = new int[256];
  
  protected XMLReaderManager m_readerManager = null;
  
  protected DefaultHandler m_defaultHandler = new DefaultHandler();
  
  private ExpandedNameTable m_expandedNameTable = new ExpandedNameTable();
  
  public void addDTM(DTM paramDTM, int paramInt) { addDTM(paramDTM, paramInt, 0); }
  
  public void addDTM(DTM paramDTM, int paramInt1, int paramInt2) {
    if (paramInt1 >= 65536)
      throw new DTMException(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null)); 
    int i = this.m_dtms.length;
    if (i <= paramInt1) {
      int j = Math.min(paramInt1 + 256, 65536);
      DTM[] arrayOfDTM = new DTM[j];
      System.arraycopy(this.m_dtms, 0, arrayOfDTM, 0, i);
      this.m_dtms = arrayOfDTM;
      int[] arrayOfInt = new int[j];
      System.arraycopy(this.m_dtm_offsets, 0, arrayOfInt, 0, i);
      this.m_dtm_offsets = arrayOfInt;
    } 
    this.m_dtms[paramInt1] = paramDTM;
    this.m_dtm_offsets[paramInt1] = paramInt2;
    paramDTM.documentRegistration();
  }
  
  public int getFirstFreeDTMID() {
    int i = this.m_dtms.length;
    for (byte b = 1; b < i; b++) {
      if (null == this.m_dtms[b])
        return b; 
    } 
    return i;
  }
  
  public DTM getDTM(Source paramSource, boolean paramBoolean1, DTMWSFilter paramDTMWSFilter, boolean paramBoolean2, boolean paramBoolean3) {
    XMLStringFactory xMLStringFactory = this.m_xsf;
    int i = getFirstFreeDTMID();
    int j = i << 16;
    if (null != paramSource && paramSource instanceof DOMSource) {
      DOM2DTM dOM2DTM = new DOM2DTM(this, (DOMSource)paramSource, j, paramDTMWSFilter, xMLStringFactory, paramBoolean3);
      addDTM(dOM2DTM, i, 0);
      return dOM2DTM;
    } 
    boolean bool1 = (null != paramSource) ? (paramSource instanceof SAXSource) : 1;
    boolean bool2 = (null != paramSource) ? (paramSource instanceof javax.xml.transform.stream.StreamSource) : 0;
    if (bool1 || bool2) {
      xMLReader = null;
      try {
        InputSource inputSource;
        SAX2DTM sAX2DTM;
        if (null == paramSource) {
          inputSource = null;
        } else {
          xMLReader = getXMLReader(paramSource);
          inputSource = SAXSource.sourceToInputSource(paramSource);
          String str = inputSource.getSystemId();
          if (null != str) {
            try {
              str = SystemIDResolver.getAbsoluteURI(str);
            } catch (Exception exception) {
              System.err.println("Can not absolutize URL: " + str);
            } 
            inputSource.setSystemId(str);
          } 
        } 
        if (paramSource == null && paramBoolean1 && !paramBoolean2 && !paramBoolean3) {
          sAX2DTM = new SAX2RTFDTM(this, paramSource, j, paramDTMWSFilter, xMLStringFactory, paramBoolean3);
        } else {
          sAX2DTM = new SAX2DTM(this, paramSource, j, paramDTMWSFilter, xMLStringFactory, paramBoolean3);
        } 
        addDTM(sAX2DTM, i, 0);
        boolean bool = (null != xMLReader && xMLReader.getClass().getName().equals("com.sun.org.apache.xerces.internal.parsers.SAXParser")) ? 1 : 0;
        if (bool)
          paramBoolean2 = true; 
        if (this.m_incremental && paramBoolean2) {
          IncrementalSAXSource incrementalSAXSource = null;
          if (bool)
            try {
              incrementalSAXSource = (IncrementalSAXSource)Class.forName("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Xerces").newInstance();
            } catch (Exception exception) {
              exception.printStackTrace();
              incrementalSAXSource = null;
            }  
          if (incrementalSAXSource == null)
            if (null == xMLReader) {
              incrementalSAXSource = new IncrementalSAXSource_Filter();
            } else {
              IncrementalSAXSource_Filter incrementalSAXSource_Filter = new IncrementalSAXSource_Filter();
              incrementalSAXSource_Filter.setXMLReader(xMLReader);
              incrementalSAXSource = incrementalSAXSource_Filter;
            }  
          sAX2DTM.setIncrementalSAXSource(incrementalSAXSource);
          if (null == inputSource)
            return sAX2DTM; 
          if (null == xMLReader.getErrorHandler())
            xMLReader.setErrorHandler(sAX2DTM); 
          xMLReader.setDTDHandler(sAX2DTM);
          try {
            incrementalSAXSource.startParse(inputSource);
          } catch (RuntimeException runtimeException) {
            sAX2DTM.clearCoRoutine();
            throw runtimeException;
          } catch (Exception exception) {
            sAX2DTM.clearCoRoutine();
            throw new WrappedRuntimeException(exception);
          } 
        } else {
          if (null == xMLReader)
            return sAX2DTM; 
          xMLReader.setContentHandler(sAX2DTM);
          xMLReader.setDTDHandler(sAX2DTM);
          if (null == xMLReader.getErrorHandler())
            xMLReader.setErrorHandler(sAX2DTM); 
          try {
            xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAX2DTM);
          } catch (SAXNotRecognizedException sAXNotRecognizedException) {
          
          } catch (SAXNotSupportedException sAXNotSupportedException) {}
          try {
            xMLReader.parse(inputSource);
          } catch (RuntimeException runtimeException) {
            sAX2DTM.clearCoRoutine();
            throw runtimeException;
          } catch (Exception exception) {
            sAX2DTM.clearCoRoutine();
            throw new WrappedRuntimeException(exception);
          } 
        } 
        return sAX2DTM;
      } finally {
        if (xMLReader != null && (!this.m_incremental || !paramBoolean2)) {
          xMLReader.setContentHandler(this.m_defaultHandler);
          xMLReader.setDTDHandler(this.m_defaultHandler);
          xMLReader.setErrorHandler(this.m_defaultHandler);
          try {
            xMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", null);
          } catch (Exception exception) {}
        } 
        releaseXMLReader(xMLReader);
      } 
    } 
    throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[] { paramSource }));
  }
  
  public int getDTMHandleFromNode(Node paramNode) {
    int j;
    if (null == paramNode)
      throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_NODE_NON_NULL", null)); 
    if (paramNode instanceof DTMNodeProxy)
      return ((DTMNodeProxy)paramNode).getDTMNodeNumber(); 
    int i = this.m_dtms.length;
    for (byte b = 0; b < i; b++) {
      DTM dTM = this.m_dtms[b];
      if (null != dTM && dTM instanceof DOM2DTM) {
        int k = ((DOM2DTM)dTM).getHandleOfNode(paramNode);
        if (k != -1)
          return k; 
      } 
    } 
    Node node = paramNode;
    Element element = (node.getNodeType() == 2) ? ((Attr)node).getOwnerElement() : node.getParentNode();
    while (element != null) {
      node = element;
      Node node1 = element.getParentNode();
    } 
    DOM2DTM dOM2DTM = (DOM2DTM)getDTM(new DOMSource(node), false, null, true, true);
    if (paramNode instanceof com.sun.org.apache.xml.internal.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode) {
      j = dOM2DTM.getHandleOfNode(((Attr)paramNode).getOwnerElement());
      j = dOM2DTM.getAttributeNode(j, paramNode.getNamespaceURI(), paramNode.getLocalName());
    } else {
      j = dOM2DTM.getHandleOfNode(paramNode);
    } 
    if (-1 == j)
      throw new RuntimeException(XMLMessages.createXMLMessage("ER_COULD_NOT_RESOLVE_NODE", null)); 
    return j;
  }
  
  public XMLReader getXMLReader(Source paramSource) {
    try {
      XMLReader xMLReader = (paramSource instanceof SAXSource) ? ((SAXSource)paramSource).getXMLReader() : null;
      if (null == xMLReader) {
        if (this.m_readerManager == null)
          this.m_readerManager = XMLReaderManager.getInstance(overrideDefaultParser()); 
        xMLReader = this.m_readerManager.getXMLReader();
      } 
      return xMLReader;
    } catch (SAXException sAXException) {
      throw new DTMException(sAXException.getMessage(), sAXException);
    } 
  }
  
  public void releaseXMLReader(XMLReader paramXMLReader) {
    if (this.m_readerManager != null)
      this.m_readerManager.releaseXMLReader(paramXMLReader); 
  }
  
  public DTM getDTM(int paramInt) {
    try {
      return this.m_dtms[paramInt >>> 16];
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      if (paramInt == -1)
        return null; 
      throw arrayIndexOutOfBoundsException;
    } 
  }
  
  public int getDTMIdentity(DTM paramDTM) {
    if (paramDTM instanceof DTMDefaultBase) {
      DTMDefaultBase dTMDefaultBase = (DTMDefaultBase)paramDTM;
      return (dTMDefaultBase.getManager() == this) ? dTMDefaultBase.getDTMIDs().elementAt(0) : -1;
    } 
    int i = this.m_dtms.length;
    for (byte b = 0; b < i; b++) {
      DTM dTM = this.m_dtms[b];
      if (dTM == paramDTM && this.m_dtm_offsets[b] == 0)
        return b << 16; 
    } 
    return -1;
  }
  
  public boolean release(DTM paramDTM, boolean paramBoolean) {
    if (paramDTM instanceof SAX2DTM)
      ((SAX2DTM)paramDTM).clearCoRoutine(); 
    if (paramDTM instanceof DTMDefaultBase) {
      SuballocatedIntVector suballocatedIntVector = ((DTMDefaultBase)paramDTM).getDTMIDs();
      for (int i = suballocatedIntVector.size() - 1; i >= 0; i--)
        this.m_dtms[suballocatedIntVector.elementAt(i) >>> 16] = null; 
    } else {
      int i = getDTMIdentity(paramDTM);
      if (i >= 0)
        this.m_dtms[i >>> 16] = null; 
    } 
    paramDTM.documentRelease();
    return true;
  }
  
  public DTM createDocumentFragment() {
    try {
      DocumentBuilderFactory documentBuilderFactory = JdkXmlUtils.getDOMFactory(overrideDefaultParser());
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();
      DocumentFragment documentFragment = document.createDocumentFragment();
      return getDTM(new DOMSource(documentFragment), true, null, false, false);
    } catch (Exception exception) {
      throw new DTMException(exception);
    } 
  }
  
  public DTMIterator createDTMIterator(int paramInt, DTMFilter paramDTMFilter, boolean paramBoolean) { return null; }
  
  public DTMIterator createDTMIterator(String paramString, PrefixResolver paramPrefixResolver) { return null; }
  
  public DTMIterator createDTMIterator(int paramInt) { return null; }
  
  public DTMIterator createDTMIterator(Object paramObject, int paramInt) { return null; }
  
  public ExpandedNameTable getExpandedNameTable(DTM paramDTM) { return this.m_expandedNameTable; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMManagerDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
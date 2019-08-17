package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMCache;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.EmptyIterator;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import java.io.FileNotFoundException;
import javax.xml.transform.stream.StreamSource;

public final class LoadDocument {
  private static final String NAMESPACE_FEATURE = "http://xml.org/sax/features/namespaces";
  
  public static DTMAxisIterator documentF(Object paramObject, DTMAxisIterator paramDTMAxisIterator, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM) throws TransletException {
    String str = null;
    int i = paramDTMAxisIterator.next();
    if (i == -1)
      return EmptyIterator.getInstance(); 
    str = paramDOM.getDocumentURI(i);
    if (!SystemIDResolver.isAbsoluteURI(str))
      str = SystemIDResolver.getAbsoluteURIFromRelative(str); 
    try {
      if (paramObject instanceof String)
        return (((String)paramObject).length() == 0) ? document(paramString, "", paramAbstractTranslet, paramDOM) : document((String)paramObject, str, paramAbstractTranslet, paramDOM); 
      if (paramObject instanceof DTMAxisIterator)
        return document((DTMAxisIterator)paramObject, str, paramAbstractTranslet, paramDOM); 
      String str1 = "document(" + paramObject.toString() + ")";
      throw new IllegalArgumentException(str1);
    } catch (Exception exception) {
      throw new TransletException(exception);
    } 
  }
  
  public static DTMAxisIterator documentF(Object paramObject, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM) throws TransletException {
    try {
      if (paramObject instanceof String) {
        if (paramString == null)
          paramString = ""; 
        String str1 = paramString;
        if (!SystemIDResolver.isAbsoluteURI(paramString))
          str1 = SystemIDResolver.getAbsoluteURIFromRelative(paramString); 
        String str2 = (String)paramObject;
        if (str2.length() == 0) {
          str2 = "";
          TemplatesImpl templatesImpl = (TemplatesImpl)paramAbstractTranslet.getTemplates();
          DOM dOM = null;
          if (templatesImpl != null)
            dOM = templatesImpl.getStylesheetDOM(); 
          return (dOM != null) ? document(dOM, paramAbstractTranslet, paramDOM) : document(str2, str1, paramAbstractTranslet, paramDOM, true);
        } 
        return document(str2, str1, paramAbstractTranslet, paramDOM);
      } 
      if (paramObject instanceof DTMAxisIterator)
        return document((DTMAxisIterator)paramObject, null, paramAbstractTranslet, paramDOM); 
      String str = "document(" + paramObject.toString() + ")";
      throw new IllegalArgumentException(str);
    } catch (Exception exception) {
      throw new TransletException(exception);
    } 
  }
  
  private static DTMAxisIterator document(String paramString1, String paramString2, AbstractTranslet paramAbstractTranslet, DOM paramDOM) throws Exception { return document(paramString1, paramString2, paramAbstractTranslet, paramDOM, false); }
  
  private static DTMAxisIterator document(String paramString1, String paramString2, AbstractTranslet paramAbstractTranslet, DOM paramDOM, boolean paramBoolean) throws Exception {
    try {
      DOMEnhancedForDTM dOMEnhancedForDTM;
      String str = paramString1;
      MultiDOM multiDOM = (MultiDOM)paramDOM;
      if (paramString2 != null && !paramString2.equals(""))
        paramString1 = SystemIDResolver.getAbsoluteURI(paramString1, paramString2); 
      if (paramString1 == null || paramString1.equals(""))
        return EmptyIterator.getInstance(); 
      int i = multiDOM.getDocumentMask(paramString1);
      if (i != -1) {
        DOM dOM = ((DOMAdapter)multiDOM.getDOMAdapter(paramString1)).getDOMImpl();
        if (dOM instanceof DOMEnhancedForDTM)
          return new SingletonIterator(((DOMEnhancedForDTM)dOM).getDocument(), true); 
      } 
      DOMCache dOMCache = paramAbstractTranslet.getDOMCache();
      i = multiDOM.nextMask();
      if (dOMCache != null) {
        dOMEnhancedForDTM = dOMCache.retrieveDocument(paramString2, str, paramAbstractTranslet);
        if (dOMEnhancedForDTM == null) {
          FileNotFoundException fileNotFoundException = new FileNotFoundException(str);
          throw new TransletException(fileNotFoundException);
        } 
      } else {
        String str1 = SecuritySupport.checkAccess(paramString1, paramAbstractTranslet.getAllowedProtocols(), "all");
        if (str1 != null) {
          ErrorMsg errorMsg = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(paramString1), str1);
          throw new Exception(errorMsg.toString());
        } 
        XSLTCDTMManager xSLTCDTMManager = (XSLTCDTMManager)multiDOM.getDTMManager();
        DOMEnhancedForDTM dOMEnhancedForDTM1 = (DOMEnhancedForDTM)xSLTCDTMManager.getDTM(new StreamSource(paramString1), false, null, true, false, paramAbstractTranslet.hasIdCall(), paramBoolean);
        dOMEnhancedForDTM = dOMEnhancedForDTM1;
        if (paramBoolean) {
          TemplatesImpl templatesImpl = (TemplatesImpl)paramAbstractTranslet.getTemplates();
          if (templatesImpl != null)
            templatesImpl.setStylesheetDOM(dOMEnhancedForDTM1); 
        } 
        paramAbstractTranslet.prepassDocument(dOMEnhancedForDTM1);
        dOMEnhancedForDTM1.setDocumentURI(paramString1);
      } 
      DOMAdapter dOMAdapter = paramAbstractTranslet.makeDOMAdapter(dOMEnhancedForDTM);
      multiDOM.addDOMAdapter(dOMAdapter);
      paramAbstractTranslet.buildKeys(dOMAdapter, null, null, dOMEnhancedForDTM.getDocument());
      return new SingletonIterator(dOMEnhancedForDTM.getDocument(), true);
    } catch (Exception exception) {
      throw exception;
    } 
  }
  
  private static DTMAxisIterator document(DTMAxisIterator paramDTMAxisIterator, String paramString, AbstractTranslet paramAbstractTranslet, DOM paramDOM) throws Exception {
    UnionIterator unionIterator = new UnionIterator(paramDOM);
    int i = -1;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      String str = paramDOM.getStringValueX(i);
      if (paramString == null) {
        paramString = paramDOM.getDocumentURI(i);
        if (!SystemIDResolver.isAbsoluteURI(paramString))
          paramString = SystemIDResolver.getAbsoluteURIFromRelative(paramString); 
      } 
      unionIterator.addIterator(document(str, paramString, paramAbstractTranslet, paramDOM));
    } 
    return unionIterator;
  }
  
  private static DTMAxisIterator document(DOM paramDOM1, AbstractTranslet paramAbstractTranslet, DOM paramDOM2) throws Exception {
    DTMManager dTMManager = ((MultiDOM)paramDOM2).getDTMManager();
    if (dTMManager != null && paramDOM1 instanceof DTM)
      ((DTM)paramDOM1).migrateTo(dTMManager); 
    paramAbstractTranslet.prepassDocument(paramDOM1);
    DOMAdapter dOMAdapter = paramAbstractTranslet.makeDOMAdapter(paramDOM1);
    ((MultiDOM)paramDOM2).addDOMAdapter(dOMAdapter);
    paramAbstractTranslet.buildKeys(dOMAdapter, null, null, paramDOM1.getDocument());
    return new SingletonIterator(paramDOM1.getDocument(), true);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\LoadDocument.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
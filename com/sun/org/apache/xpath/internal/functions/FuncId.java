package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;

public class FuncId extends FunctionOneArg {
  static final long serialVersionUID = 8930573966143567310L;
  
  private StringVector getNodesByID(XPathContext paramXPathContext, int paramInt, String paramString, StringVector paramStringVector, NodeSetDTM paramNodeSetDTM, boolean paramBoolean) {
    if (null != paramString) {
      String str = null;
      StringTokenizer stringTokenizer = new StringTokenizer(paramString);
      boolean bool = stringTokenizer.hasMoreTokens();
      DTM dTM = paramXPathContext.getDTM(paramInt);
      while (bool) {
        str = stringTokenizer.nextToken();
        bool = stringTokenizer.hasMoreTokens();
        if (null != paramStringVector && paramStringVector.contains(str)) {
          str = null;
          continue;
        } 
        int i = dTM.getElementById(str);
        if (-1 != i)
          paramNodeSetDTM.addNodeInDocOrder(i, paramXPathContext); 
        if (null != str && (bool || paramBoolean)) {
          if (null == paramStringVector)
            paramStringVector = new StringVector(); 
          paramStringVector.addElement(str);
        } 
      } 
    } 
    return paramStringVector;
  }
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    int i = paramXPathContext.getCurrentNode();
    DTM dTM = paramXPathContext.getDTM(i);
    int j = dTM.getDocument();
    if (-1 == j)
      error(paramXPathContext, "ER_CONTEXT_HAS_NO_OWNERDOC", null); 
    XObject xObject = this.m_arg0.execute(paramXPathContext);
    int k = xObject.getType();
    XNodeSet xNodeSet = new XNodeSet(paramXPathContext.getDTMManager());
    NodeSetDTM nodeSetDTM = xNodeSet.mutableNodeset();
    if (4 == k) {
      DTMIterator dTMIterator = xObject.iter();
      StringVector stringVector = null;
      int m = dTMIterator.nextNode();
      while (-1 != m) {
        DTM dTM1 = dTMIterator.getDTM(m);
        String str = dTM1.getStringValue(m).toString();
        m = dTMIterator.nextNode();
        stringVector = getNodesByID(paramXPathContext, j, str, stringVector, nodeSetDTM, (-1 != m));
      } 
    } else {
      if (-1 == k)
        return xNodeSet; 
      String str = xObject.str();
      getNodesByID(paramXPathContext, j, str, null, nodeSetDTM, false);
    } 
    return xNodeSet;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMAdapter implements DOM {
  private DOMEnhancedForDTM _enhancedDOM;
  
  private DOM _dom;
  
  private String[] _namesArray;
  
  private String[] _urisArray;
  
  private int[] _typesArray;
  
  private String[] _namespaceArray;
  
  private short[] _mapping = null;
  
  private int[] _reverse = null;
  
  private short[] _NSmapping = null;
  
  private short[] _NSreverse = null;
  
  private int _multiDOMMask;
  
  public DOMAdapter(DOM paramDOM, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {
    if (paramDOM instanceof DOMEnhancedForDTM)
      this._enhancedDOM = (DOMEnhancedForDTM)paramDOM; 
    this._dom = paramDOM;
    this._namesArray = paramArrayOfString1;
    this._urisArray = paramArrayOfString2;
    this._typesArray = paramArrayOfInt;
    this._namespaceArray = paramArrayOfString3;
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {
    this._namesArray = paramArrayOfString1;
    this._urisArray = paramArrayOfString2;
    this._typesArray = paramArrayOfInt;
    this._namespaceArray = paramArrayOfString3;
  }
  
  public String[] getNamesArray() { return this._namesArray; }
  
  public String[] getUrisArray() { return this._urisArray; }
  
  public int[] getTypesArray() { return this._typesArray; }
  
  public String[] getNamespaceArray() { return this._namespaceArray; }
  
  public DOM getDOMImpl() { return this._dom; }
  
  private short[] getMapping() {
    if (this._mapping == null && this._enhancedDOM != null)
      this._mapping = this._enhancedDOM.getMapping(this._namesArray, this._urisArray, this._typesArray); 
    return this._mapping;
  }
  
  private int[] getReverse() {
    if (this._reverse == null && this._enhancedDOM != null)
      this._reverse = this._enhancedDOM.getReverseMapping(this._namesArray, this._urisArray, this._typesArray); 
    return this._reverse;
  }
  
  private short[] getNSMapping() {
    if (this._NSmapping == null && this._enhancedDOM != null)
      this._NSmapping = this._enhancedDOM.getNamespaceMapping(this._namespaceArray); 
    return this._NSmapping;
  }
  
  private short[] getNSReverse() {
    if (this._NSreverse == null && this._enhancedDOM != null)
      this._NSreverse = this._enhancedDOM.getReverseNamespaceMapping(this._namespaceArray); 
    return this._NSreverse;
  }
  
  public DTMAxisIterator getIterator() { return this._dom.getIterator(); }
  
  public String getStringValue() { return this._dom.getStringValue(); }
  
  public DTMAxisIterator getChildren(int paramInt) {
    if (this._enhancedDOM != null)
      return this._enhancedDOM.getChildren(paramInt); 
    DTMAxisIterator dTMAxisIterator = this._dom.getChildren(paramInt);
    return dTMAxisIterator.setStartNode(paramInt);
  }
  
  public void setFilter(StripFilter paramStripFilter) {}
  
  public DTMAxisIterator getTypedChildren(int paramInt) {
    int[] arrayOfInt = getReverse();
    return (this._enhancedDOM != null) ? this._enhancedDOM.getTypedChildren(arrayOfInt[paramInt]) : this._dom.getTypedChildren(paramInt);
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2) { return this._dom.getNamespaceAxisIterator(paramInt1, getNSReverse()[paramInt2]); }
  
  public DTMAxisIterator getAxisIterator(int paramInt) { return (this._enhancedDOM != null) ? this._enhancedDOM.getAxisIterator(paramInt) : this._dom.getAxisIterator(paramInt); }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) {
    int[] arrayOfInt = getReverse();
    return (this._enhancedDOM != null) ? this._enhancedDOM.getTypedAxisIterator(paramInt1, arrayOfInt[paramInt2]) : this._dom.getTypedAxisIterator(paramInt1, paramInt2);
  }
  
  public int getMultiDOMMask() { return this._multiDOMMask; }
  
  public void setMultiDOMMask(int paramInt) { this._multiDOMMask = paramInt; }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean) { return this._dom.getNthDescendant(getReverse()[paramInt1], paramInt2, paramBoolean); }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean) { return this._dom.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean); }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt) { return this._dom.orderNodes(paramDTMAxisIterator, paramInt); }
  
  public int getExpandedTypeID(int paramInt) {
    int i;
    short[] arrayOfShort = getMapping();
    if (this._enhancedDOM != null) {
      i = arrayOfShort[this._enhancedDOM.getExpandedTypeID2(paramInt)];
    } else if (null != arrayOfShort) {
      i = arrayOfShort[this._dom.getExpandedTypeID(paramInt)];
    } else {
      i = this._dom.getExpandedTypeID(paramInt);
    } 
    return i;
  }
  
  public int getNamespaceType(int paramInt) { return getNSMapping()[this._dom.getNSType(paramInt)]; }
  
  public int getNSType(int paramInt) { return this._dom.getNSType(paramInt); }
  
  public int getParent(int paramInt) { return this._dom.getParent(paramInt); }
  
  public int getAttributeNode(int paramInt1, int paramInt2) { return this._dom.getAttributeNode(getReverse()[paramInt1], paramInt2); }
  
  public String getNodeName(int paramInt) { return (paramInt == -1) ? "" : this._dom.getNodeName(paramInt); }
  
  public String getNodeNameX(int paramInt) { return (paramInt == -1) ? "" : this._dom.getNodeNameX(paramInt); }
  
  public String getNamespaceName(int paramInt) { return (paramInt == -1) ? "" : this._dom.getNamespaceName(paramInt); }
  
  public String getStringValueX(int paramInt) { return (this._enhancedDOM != null) ? this._enhancedDOM.getStringValueX(paramInt) : ((paramInt == -1) ? "" : this._dom.getStringValueX(paramInt)); }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { this._dom.copy(paramInt, paramSerializationHandler); }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler) throws TransletException { this._dom.copy(paramDTMAxisIterator, paramSerializationHandler); }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { return (this._enhancedDOM != null) ? this._enhancedDOM.shallowCopy(paramInt, paramSerializationHandler) : this._dom.shallowCopy(paramInt, paramSerializationHandler); }
  
  public boolean lessThan(int paramInt1, int paramInt2) { return this._dom.lessThan(paramInt1, paramInt2); }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (this._enhancedDOM != null) {
      this._enhancedDOM.characters(paramInt, paramSerializationHandler);
    } else {
      this._dom.characters(paramInt, paramSerializationHandler);
    } 
  }
  
  public Node makeNode(int paramInt) { return this._dom.makeNode(paramInt); }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator) { return this._dom.makeNode(paramDTMAxisIterator); }
  
  public NodeList makeNodeList(int paramInt) { return this._dom.makeNodeList(paramInt); }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator) { return this._dom.makeNodeList(paramDTMAxisIterator); }
  
  public String getLanguage(int paramInt) { return this._dom.getLanguage(paramInt); }
  
  public int getSize() { return this._dom.getSize(); }
  
  public void setDocumentURI(String paramString) {
    if (this._enhancedDOM != null)
      this._enhancedDOM.setDocumentURI(paramString); 
  }
  
  public String getDocumentURI() { return (this._enhancedDOM != null) ? this._enhancedDOM.getDocumentURI() : ""; }
  
  public String getDocumentURI(int paramInt) { return this._dom.getDocumentURI(paramInt); }
  
  public int getDocument() { return this._dom.getDocument(); }
  
  public boolean isElement(int paramInt) { return this._dom.isElement(paramInt); }
  
  public boolean isAttribute(int paramInt) { return this._dom.isAttribute(paramInt); }
  
  public int getNodeIdent(int paramInt) { return this._dom.getNodeIdent(paramInt); }
  
  public int getNodeHandle(int paramInt) { return this._dom.getNodeHandle(paramInt); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2) { return (this._enhancedDOM != null) ? this._enhancedDOM.getResultTreeFrag(paramInt1, paramInt2) : this._dom.getResultTreeFrag(paramInt1, paramInt2); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean) { return (this._enhancedDOM != null) ? this._enhancedDOM.getResultTreeFrag(paramInt1, paramInt2, paramBoolean) : this._dom.getResultTreeFrag(paramInt1, paramInt2, paramBoolean); }
  
  public SerializationHandler getOutputDomBuilder() { return this._dom.getOutputDomBuilder(); }
  
  public String lookupNamespace(int paramInt, String paramString) throws TransletException { return this._dom.lookupNamespace(paramInt, paramString); }
  
  public String getUnparsedEntityURI(String paramString) { return this._dom.getUnparsedEntityURI(paramString); }
  
  public Map<String, Integer> getElementsWithIDs() { return this._dom.getElementsWithIDs(); }
  
  public void release() { this._dom.release(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DOMAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class MultiDOM implements DOM {
  private static final int NO_TYPE = -2;
  
  private static final int INITIAL_SIZE = 4;
  
  private DOM[] _adapters = new DOM[4];
  
  private DOMAdapter _main;
  
  private DTMManager _dtmManager;
  
  private int _free = 1;
  
  private int _size = 4;
  
  private Map<String, Integer> _documents = new HashMap();
  
  public MultiDOM(DOM paramDOM) {
    DOMAdapter dOMAdapter = (DOMAdapter)paramDOM;
    this._adapters[0] = dOMAdapter;
    this._main = dOMAdapter;
    DOM dOM = dOMAdapter.getDOMImpl();
    if (dOM instanceof DTMDefaultBase)
      this._dtmManager = ((DTMDefaultBase)dOM).getManager(); 
    addDOMAdapter(dOMAdapter, false);
  }
  
  public int nextMask() { return this._free; }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {}
  
  public int addDOMAdapter(DOMAdapter paramDOMAdapter) { return addDOMAdapter(paramDOMAdapter, true); }
  
  private int addDOMAdapter(DOMAdapter paramDOMAdapter, boolean paramBoolean) {
    DOM dOM = paramDOMAdapter.getDOMImpl();
    int i = 1;
    int j = 1;
    SuballocatedIntVector suballocatedIntVector = null;
    if (dOM instanceof DTMDefaultBase) {
      DTMDefaultBase dTMDefaultBase = (DTMDefaultBase)dOM;
      suballocatedIntVector = dTMDefaultBase.getDTMIDs();
      j = suballocatedIntVector.size();
      i = suballocatedIntVector.elementAt(j - 1) >>> 16;
    } else if (dOM instanceof SimpleResultTreeImpl) {
      SimpleResultTreeImpl simpleResultTreeImpl = (SimpleResultTreeImpl)dOM;
      i = simpleResultTreeImpl.getDocument() >>> 16;
    } 
    if (i >= this._size) {
      int k = this._size;
      do {
        this._size *= 2;
      } while (this._size <= i);
      DOMAdapter[] arrayOfDOMAdapter = new DOMAdapter[this._size];
      System.arraycopy(this._adapters, 0, arrayOfDOMAdapter, 0, k);
      this._adapters = arrayOfDOMAdapter;
    } 
    this._free = i + 1;
    if (j == 1) {
      this._adapters[i] = paramDOMAdapter;
    } else if (suballocatedIntVector != null) {
      int k = 0;
      for (int m = j - 1; m >= 0; m--) {
        k = suballocatedIntVector.elementAt(m) >>> 16;
        this._adapters[k] = paramDOMAdapter;
      } 
      i = k;
    } 
    if (paramBoolean) {
      String str = paramDOMAdapter.getDocumentURI(0);
      this._documents.put(str, Integer.valueOf(i));
    } 
    if (dOM instanceof AdaptiveResultTreeImpl) {
      AdaptiveResultTreeImpl adaptiveResultTreeImpl = (AdaptiveResultTreeImpl)dOM;
      DOM dOM1 = adaptiveResultTreeImpl.getNestedDOM();
      if (dOM1 != null) {
        DOMAdapter dOMAdapter = new DOMAdapter(dOM1, paramDOMAdapter.getNamesArray(), paramDOMAdapter.getUrisArray(), paramDOMAdapter.getTypesArray(), paramDOMAdapter.getNamespaceArray());
        addDOMAdapter(dOMAdapter);
      } 
    } 
    return i;
  }
  
  public int getDocumentMask(String paramString) {
    Integer integer = (Integer)this._documents.get(paramString);
    return (integer == null) ? -1 : integer.intValue();
  }
  
  public DOM getDOMAdapter(String paramString) {
    Integer integer = (Integer)this._documents.get(paramString);
    return (integer == null) ? null : this._adapters[integer.intValue()];
  }
  
  public int getDocument() { return this._main.getDocument(); }
  
  public DTMManager getDTMManager() { return this._dtmManager; }
  
  public DTMAxisIterator getIterator() { return this._main.getIterator(); }
  
  public String getStringValue() { return this._main.getStringValue(); }
  
  public DTMAxisIterator getChildren(int paramInt) { return this._adapters[getDTMId(paramInt)].getChildren(paramInt); }
  
  public DTMAxisIterator getTypedChildren(int paramInt) { return new AxisIterator(3, paramInt); }
  
  public DTMAxisIterator getAxisIterator(int paramInt) { return new AxisIterator(paramInt, -2); }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2) { return new AxisIterator(paramInt1, paramInt2); }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean) { return this._adapters[getDTMId(paramInt1)].getNthDescendant(paramInt1, paramInt2, paramBoolean); }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean) { return new NodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean); }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2) { return this._main.getNamespaceAxisIterator(paramInt1, paramInt2); }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt) { return this._adapters[getDTMId(paramInt)].orderNodes(paramDTMAxisIterator, paramInt); }
  
  public int getExpandedTypeID(int paramInt) { return (paramInt != -1) ? this._adapters[paramInt >>> 16].getExpandedTypeID(paramInt) : -1; }
  
  public int getNamespaceType(int paramInt) { return this._adapters[getDTMId(paramInt)].getNamespaceType(paramInt); }
  
  public int getNSType(int paramInt) { return this._adapters[getDTMId(paramInt)].getNSType(paramInt); }
  
  public int getParent(int paramInt) { return (paramInt == -1) ? -1 : this._adapters[paramInt >>> 16].getParent(paramInt); }
  
  public int getAttributeNode(int paramInt1, int paramInt2) { return (paramInt2 == -1) ? -1 : this._adapters[paramInt2 >>> 16].getAttributeNode(paramInt1, paramInt2); }
  
  public String getNodeName(int paramInt) { return (paramInt == -1) ? "" : this._adapters[paramInt >>> 16].getNodeName(paramInt); }
  
  public String getNodeNameX(int paramInt) { return (paramInt == -1) ? "" : this._adapters[paramInt >>> 16].getNodeNameX(paramInt); }
  
  public String getNamespaceName(int paramInt) { return (paramInt == -1) ? "" : this._adapters[paramInt >>> 16].getNamespaceName(paramInt); }
  
  public String getStringValueX(int paramInt) { return (paramInt == -1) ? "" : this._adapters[paramInt >>> 16].getStringValueX(paramInt); }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (paramInt != -1)
      this._adapters[paramInt >>> 16].copy(paramInt, paramSerializationHandler); 
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler) throws TransletException {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1)
      this._adapters[i >>> 16].copy(i, paramSerializationHandler); 
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException { return (paramInt == -1) ? "" : this._adapters[paramInt >>> 16].shallowCopy(paramInt, paramSerializationHandler); }
  
  public boolean lessThan(int paramInt1, int paramInt2) {
    if (paramInt1 == -1)
      return true; 
    if (paramInt2 == -1)
      return false; 
    int i = getDTMId(paramInt1);
    int j = getDTMId(paramInt2);
    return (i == j) ? this._adapters[i].lessThan(paramInt1, paramInt2) : ((i < j) ? 1 : 0);
  }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler) throws TransletException {
    if (paramInt != -1)
      this._adapters[paramInt >>> 16].characters(paramInt, paramSerializationHandler); 
  }
  
  public void setFilter(StripFilter paramStripFilter) {
    for (byte b = 0; b < this._free; b++) {
      if (this._adapters[b] != null)
        this._adapters[b].setFilter(paramStripFilter); 
    } 
  }
  
  public Node makeNode(int paramInt) { return (paramInt == -1) ? null : this._adapters[getDTMId(paramInt)].makeNode(paramInt); }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator) { return this._main.makeNode(paramDTMAxisIterator); }
  
  public NodeList makeNodeList(int paramInt) { return (paramInt == -1) ? null : this._adapters[getDTMId(paramInt)].makeNodeList(paramInt); }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator) {
    int i = paramDTMAxisIterator.next();
    if (i == -1)
      return new DTMAxisIterNodeList(null, null); 
    paramDTMAxisIterator.reset();
    return this._adapters[getDTMId(i)].makeNodeList(paramDTMAxisIterator);
  }
  
  public String getLanguage(int paramInt) { return this._adapters[getDTMId(paramInt)].getLanguage(paramInt); }
  
  public int getSize() {
    int i = 0;
    for (byte b = 0; b < this._size; b++)
      i += this._adapters[b].getSize(); 
    return i;
  }
  
  public String getDocumentURI(int paramInt) {
    if (paramInt == -1)
      paramInt = 0; 
    return this._adapters[paramInt >>> 16].getDocumentURI(0);
  }
  
  public boolean isElement(int paramInt) { return (paramInt == -1) ? false : this._adapters[paramInt >>> 16].isElement(paramInt); }
  
  public boolean isAttribute(int paramInt) { return (paramInt == -1) ? false : this._adapters[paramInt >>> 16].isAttribute(paramInt); }
  
  public int getDTMId(int paramInt) {
    if (paramInt == -1)
      return 0; 
    int i;
    for (i = paramInt >>> 16; i >= 2 && this._adapters[i] == this._adapters[i - true]; i--);
    return i;
  }
  
  public DOM getDTM(int paramInt) { return this._adapters[getDTMId(paramInt)]; }
  
  public int getNodeIdent(int paramInt) { return this._adapters[paramInt >>> 16].getNodeIdent(paramInt); }
  
  public int getNodeHandle(int paramInt) { return this._main.getNodeHandle(paramInt); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2) { return this._main.getResultTreeFrag(paramInt1, paramInt2); }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean) { return this._main.getResultTreeFrag(paramInt1, paramInt2, paramBoolean); }
  
  public DOM getMain() { return this._main; }
  
  public SerializationHandler getOutputDomBuilder() { return this._main.getOutputDomBuilder(); }
  
  public String lookupNamespace(int paramInt, String paramString) throws TransletException { return this._main.lookupNamespace(paramInt, paramString); }
  
  public String getUnparsedEntityURI(String paramString) { return this._main.getUnparsedEntityURI(paramString); }
  
  public Map<String, Integer> getElementsWithIDs() { return this._main.getElementsWithIDs(); }
  
  public void release() { this._main.release(); }
  
  private boolean isMatchingAdapterEntry(DOM paramDOM, DOMAdapter paramDOMAdapter) {
    DOM dOM = paramDOMAdapter.getDOMImpl();
    return (paramDOM == paramDOMAdapter || (dOM instanceof AdaptiveResultTreeImpl && paramDOM instanceof DOMAdapter && ((AdaptiveResultTreeImpl)dOM).getNestedDOM() == ((DOMAdapter)paramDOM).getDOMImpl()));
  }
  
  public void removeDOMAdapter(DOMAdapter paramDOMAdapter) {
    this._documents.remove(paramDOMAdapter.getDocumentURI(0));
    DOM dOM = paramDOMAdapter.getDOMImpl();
    if (dOM instanceof DTMDefaultBase) {
      SuballocatedIntVector suballocatedIntVector = ((DTMDefaultBase)dOM).getDTMIDs();
      int i = suballocatedIntVector.size();
      for (byte b = 0; b < i; b++)
        this._adapters[suballocatedIntVector.elementAt(b) >>> 16] = null; 
    } else {
      int i = dOM.getDocument() >>> 16;
      if (i > 0 && i < this._adapters.length && isMatchingAdapterEntry(this._adapters[i], paramDOMAdapter)) {
        this._adapters[i] = null;
      } else {
        boolean bool = false;
        for (byte b = 0; b < this._adapters.length; b++) {
          if (isMatchingAdapterEntry(this._adapters[i], paramDOMAdapter)) {
            this._adapters[b] = null;
            bool = true;
            break;
          } 
        } 
      } 
    } 
  }
  
  private final class AxisIterator extends DTMAxisIteratorBase {
    private final int _axis;
    
    private final int _type;
    
    private DTMAxisIterator _source;
    
    private int _dtmId = -1;
    
    public AxisIterator(int param1Int1, int param1Int2) {
      this._axis = param1Int1;
      this._type = param1Int2;
    }
    
    public int next() { return (this._source == null) ? -1 : this._source.next(); }
    
    public void setRestartable(boolean param1Boolean) {
      if (this._source != null)
        this._source.setRestartable(param1Boolean); 
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (param1Int == -1)
        return this; 
      int i = param1Int >>> 16;
      if (this._source == null || this._dtmId != i)
        if (this._type == -2) {
          this._source = MultiDOM.this._adapters[i].getAxisIterator(this._axis);
        } else if (this._axis == 3) {
          this._source = MultiDOM.this._adapters[i].getTypedChildren(this._type);
        } else {
          this._source = MultiDOM.this._adapters[i].getTypedAxisIterator(this._axis, this._type);
        }  
      this._dtmId = i;
      this._source.setStartNode(param1Int);
      return this;
    }
    
    public DTMAxisIterator reset() {
      if (this._source != null)
        this._source.reset(); 
      return this;
    }
    
    public int getLast() { return (this._source != null) ? this._source.getLast() : -1; }
    
    public int getPosition() { return (this._source != null) ? this._source.getPosition() : -1; }
    
    public boolean isReverse() { return Axis.isReverse(this._axis); }
    
    public void setMark() {
      if (this._source != null)
        this._source.setMark(); 
    }
    
    public void gotoMark() {
      if (this._source != null)
        this._source.gotoMark(); 
    }
    
    public DTMAxisIterator cloneIterator() {
      AxisIterator axisIterator = new AxisIterator(MultiDOM.this, this._axis, this._type);
      if (this._source != null)
        axisIterator._source = this._source.cloneIterator(); 
      axisIterator._dtmId = this._dtmId;
      return axisIterator;
    }
  }
  
  private final class NodeValueIterator extends DTMAxisIteratorBase {
    private DTMAxisIterator _source;
    
    private String _value;
    
    private boolean _op;
    
    private final boolean _isReverse;
    
    private int _returnType = 1;
    
    public NodeValueIterator(DTMAxisIterator param1DTMAxisIterator, int param1Int, String param1String, boolean param1Boolean) {
      this._source = param1DTMAxisIterator;
      this._returnType = param1Int;
      this._value = param1String;
      this._op = param1Boolean;
      this._isReverse = param1DTMAxisIterator.isReverse();
    }
    
    public boolean isReverse() { return this._isReverse; }
    
    public DTMAxisIterator cloneIterator() {
      try {
        NodeValueIterator nodeValueIterator = (NodeValueIterator)clone();
        nodeValueIterator._source = this._source.cloneIterator();
        nodeValueIterator.setRestartable(false);
        return nodeValueIterator.reset();
      } catch (CloneNotSupportedException cloneNotSupportedException) {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", cloneNotSupportedException.toString());
        return null;
      } 
    }
    
    public void setRestartable(boolean param1Boolean) {
      this._isRestartable = param1Boolean;
      this._source.setRestartable(param1Boolean);
    }
    
    public DTMAxisIterator reset() {
      this._source.reset();
      return resetPosition();
    }
    
    public int next() {
      int i;
      while ((i = this._source.next()) != -1) {
        String str = MultiDOM.this.getStringValueX(i);
        if (this._value.equals(str) == this._op)
          return (this._returnType == 0) ? returnNode(i) : returnNode(MultiDOM.this.getParent(i)); 
      } 
      return -1;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      if (this._isRestartable) {
        this._source.setStartNode(this._startNode = param1Int);
        return resetPosition();
      } 
      return this;
    }
    
    public void setMark() { this._source.setMark(); }
    
    public void gotoMark() { this._source.gotoMark(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultiDOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeyIndex extends DTMAxisIteratorBase {
  private Map<String, IntegerArray> _index;
  
  private int _currentDocumentNode = -1;
  
  private Map<Integer, Map> _rootToIndexMap = new HashMap();
  
  private IntegerArray _nodes = null;
  
  private DOM _dom;
  
  private DOMEnhancedForDTM _enhancedDOM;
  
  private int _markedPosition = 0;
  
  private static final IntegerArray EMPTY_NODES = new IntegerArray(0);
  
  public KeyIndex(int paramInt) {}
  
  public void setRestartable(boolean paramBoolean) {}
  
  public void add(String paramString, int paramInt1, int paramInt2) {
    if (this._currentDocumentNode != paramInt2) {
      this._currentDocumentNode = paramInt2;
      this._index = new HashMap();
      this._rootToIndexMap.put(Integer.valueOf(paramInt2), this._index);
    } 
    IntegerArray integerArray = (IntegerArray)this._index.get(paramString);
    if (integerArray == null) {
      integerArray = new IntegerArray();
      this._index.put(paramString, integerArray);
      integerArray.add(paramInt1);
    } else if (paramInt1 != integerArray.at(integerArray.cardinality() - 1)) {
      integerArray.add(paramInt1);
    } 
  }
  
  public void merge(KeyIndex paramKeyIndex) {
    if (paramKeyIndex == null)
      return; 
    if (paramKeyIndex._nodes != null)
      if (this._nodes == null) {
        this._nodes = (IntegerArray)paramKeyIndex._nodes.clone();
      } else {
        this._nodes.merge(paramKeyIndex._nodes);
      }  
  }
  
  public void lookupId(Object paramObject) {
    this._nodes = null;
    StringTokenizer stringTokenizer = new StringTokenizer((String)paramObject, " \n\t");
    while (stringTokenizer.hasMoreElements()) {
      String str = (String)stringTokenizer.nextElement();
      IntegerArray integerArray = (IntegerArray)this._index.get(str);
      if (integerArray == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource())
        integerArray = getDOMNodeById(str); 
      if (integerArray == null)
        continue; 
      if (this._nodes == null) {
        integerArray = (IntegerArray)integerArray.clone();
        this._nodes = integerArray;
        continue;
      } 
      this._nodes.merge(integerArray);
    } 
  }
  
  public IntegerArray getDOMNodeById(String paramString) {
    IntegerArray integerArray = null;
    if (this._enhancedDOM != null) {
      int i = this._enhancedDOM.getElementById(paramString);
      if (i != -1) {
        Integer integer = new Integer(this._enhancedDOM.getDocument());
        Map map = (Map)this._rootToIndexMap.get(integer);
        if (map == null) {
          map = new HashMap();
          this._rootToIndexMap.put(integer, map);
        } else {
          integerArray = (IntegerArray)map.get(paramString);
        } 
        if (integerArray == null) {
          integerArray = new IntegerArray();
          map.put(paramString, integerArray);
        } 
        integerArray.add(this._enhancedDOM.getNodeHandle(i));
      } 
    } 
    return integerArray;
  }
  
  public void lookupKey(Object paramObject) {
    IntegerArray integerArray = (IntegerArray)this._index.get(paramObject);
    this._nodes = (integerArray != null) ? (IntegerArray)integerArray.clone() : null;
    this._position = 0;
  }
  
  public int next() { return (this._nodes == null) ? -1 : ((this._position < this._nodes.cardinality()) ? this._dom.getNodeHandle(this._nodes.at(this._position++)) : -1); }
  
  public int containsID(int paramInt, Object paramObject) {
    String str = (String)paramObject;
    int i = this._dom.getAxisIterator(19).setStartNode(paramInt).next();
    Map map = (Map)this._rootToIndexMap.get(Integer.valueOf(i));
    StringTokenizer stringTokenizer = new StringTokenizer(str, " \n\t");
    while (stringTokenizer.hasMoreElements()) {
      String str1 = (String)stringTokenizer.nextElement();
      IntegerArray integerArray = null;
      if (map != null)
        integerArray = (IntegerArray)map.get(str1); 
      if (integerArray == null && this._enhancedDOM != null && this._enhancedDOM.hasDOMSource())
        integerArray = getDOMNodeById(str1); 
      if (integerArray != null && integerArray.indexOf(paramInt) >= 0)
        return 1; 
    } 
    return 0;
  }
  
  public int containsKey(int paramInt, Object paramObject) {
    int i = this._dom.getAxisIterator(19).setStartNode(paramInt).next();
    Map map = (Map)this._rootToIndexMap.get(new Integer(i));
    if (map != null) {
      IntegerArray integerArray = (IntegerArray)map.get(paramObject);
      return (integerArray != null && integerArray.indexOf(paramInt) >= 0) ? 1 : 0;
    } 
    return 0;
  }
  
  public DTMAxisIterator reset() {
    this._position = 0;
    return this;
  }
  
  public int getLast() { return (this._nodes == null) ? 0 : this._nodes.cardinality(); }
  
  public int getPosition() { return this._position; }
  
  public void setMark() { this._markedPosition = this._position; }
  
  public void gotoMark() { this._position = this._markedPosition; }
  
  public DTMAxisIterator setStartNode(int paramInt) {
    if (paramInt == -1) {
      this._nodes = null;
    } else if (this._nodes != null) {
      this._position = 0;
    } 
    return this;
  }
  
  public int getStartNode() { return 0; }
  
  public boolean isReverse() { return false; }
  
  public DTMAxisIterator cloneIterator() {
    KeyIndex keyIndex = new KeyIndex(0);
    keyIndex._index = this._index;
    keyIndex._rootToIndexMap = this._rootToIndexMap;
    keyIndex._nodes = this._nodes;
    keyIndex._position = this._position;
    return keyIndex;
  }
  
  public void setDom(DOM paramDOM, int paramInt) {
    this._dom = paramDOM;
    if (paramDOM instanceof MultiDOM)
      paramDOM = ((MultiDOM)paramDOM).getDTM(paramInt); 
    if (paramDOM instanceof DOMEnhancedForDTM) {
      this._enhancedDOM = (DOMEnhancedForDTM)paramDOM;
    } else if (paramDOM instanceof DOMAdapter) {
      DOM dOM = ((DOMAdapter)paramDOM).getDOMImpl();
      if (dOM instanceof DOMEnhancedForDTM)
        this._enhancedDOM = (DOMEnhancedForDTM)dOM; 
    } 
  }
  
  public KeyIndexIterator getKeyIndexIterator(Object paramObject, boolean paramBoolean) { return (paramObject instanceof DTMAxisIterator) ? getKeyIndexIterator((DTMAxisIterator)paramObject, paramBoolean) : getKeyIndexIterator(BasisLibrary.stringF(paramObject, this._dom), paramBoolean); }
  
  public KeyIndexIterator getKeyIndexIterator(String paramString, boolean paramBoolean) { return new KeyIndexIterator(paramString, paramBoolean); }
  
  public KeyIndexIterator getKeyIndexIterator(DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean) { return new KeyIndexIterator(paramDTMAxisIterator, paramBoolean); }
  
  public class KeyIndexIterator extends MultiValuedNodeHeapIterator {
    private IntegerArray _nodes;
    
    private DTMAxisIterator _keyValueIterator;
    
    private String _keyValue;
    
    private boolean _isKeyIterator;
    
    KeyIndexIterator(String param1String, boolean param1Boolean) {
      this._isKeyIterator = param1Boolean;
      this._keyValue = param1String;
    }
    
    KeyIndexIterator(DTMAxisIterator param1DTMAxisIterator, boolean param1Boolean) {
      this._keyValueIterator = param1DTMAxisIterator;
      this._isKeyIterator = param1Boolean;
    }
    
    protected IntegerArray lookupNodes(int param1Int, String param1String) {
      IntegerArray integerArray = null;
      Map map = (Map)KeyIndex.this._rootToIndexMap.get(Integer.valueOf(param1Int));
      if (!this._isKeyIterator) {
        StringTokenizer stringTokenizer = new StringTokenizer(param1String, " \n\t");
        while (stringTokenizer.hasMoreElements()) {
          String str = (String)stringTokenizer.nextElement();
          IntegerArray integerArray1 = null;
          if (map != null)
            integerArray1 = (IntegerArray)map.get(str); 
          if (integerArray1 == null && KeyIndex.this._enhancedDOM != null && KeyIndex.this._enhancedDOM.hasDOMSource())
            integerArray1 = KeyIndex.this.getDOMNodeById(str); 
          if (integerArray1 != null) {
            if (integerArray == null) {
              integerArray = (IntegerArray)integerArray1.clone();
              continue;
            } 
            integerArray.merge(integerArray1);
          } 
        } 
      } else if (map != null) {
        integerArray = (IntegerArray)map.get(param1String);
      } 
      return integerArray;
    }
    
    public DTMAxisIterator setStartNode(int param1Int) {
      this._startNode = param1Int;
      if (this._keyValueIterator != null)
        this._keyValueIterator = this._keyValueIterator.setStartNode(param1Int); 
      init();
      return super.setStartNode(param1Int);
    }
    
    public int next() {
      int i;
      if (this._nodes != null) {
        if (this._position < this._nodes.cardinality()) {
          i = returnNode(this._nodes.at(this._position));
        } else {
          i = -1;
        } 
      } else {
        i = super.next();
      } 
      return i;
    }
    
    public DTMAxisIterator reset() {
      if (this._nodes == null) {
        init();
      } else {
        super.reset();
      } 
      return resetPosition();
    }
    
    protected void init() {
      super.init();
      this._position = 0;
      int i = KeyIndex.this._dom.getAxisIterator(19).setStartNode(this._startNode).next();
      if (this._keyValueIterator == null) {
        this._nodes = lookupNodes(i, this._keyValue);
        if (this._nodes == null)
          this._nodes = EMPTY_NODES; 
      } else {
        DTMAxisIterator dTMAxisIterator = this._keyValueIterator.reset();
        boolean bool1 = false;
        boolean bool2 = false;
        this._nodes = null;
        int j;
        for (j = dTMAxisIterator.next(); j != -1; j = dTMAxisIterator.next()) {
          String str = BasisLibrary.stringF(j, KeyIndex.this._dom);
          IntegerArray integerArray = lookupNodes(i, str);
          if (integerArray != null)
            if (!bool2) {
              this._nodes = integerArray;
              bool2 = true;
            } else {
              if (this._nodes != null) {
                addHeapNode(new KeyIndexHeapNode(this._nodes));
                this._nodes = null;
              } 
              addHeapNode(new KeyIndexHeapNode(integerArray));
            }  
        } 
        if (!bool2)
          this._nodes = EMPTY_NODES; 
      } 
    }
    
    public int getLast() { return (this._nodes != null) ? this._nodes.cardinality() : super.getLast(); }
    
    public int getNodeByPosition(int param1Int) {
      int i = -1;
      if (this._nodes != null) {
        if (param1Int > 0)
          if (param1Int <= this._nodes.cardinality()) {
            this._position = param1Int;
            i = this._nodes.at(param1Int - 1);
          } else {
            this._position = this._nodes.cardinality();
          }  
      } else {
        i = super.getNodeByPosition(param1Int);
      } 
      return i;
    }
    
    protected class KeyIndexHeapNode extends MultiValuedNodeHeapIterator.HeapNode {
      private IntegerArray _nodes;
      
      private int _position = 0;
      
      private int _markPosition = -1;
      
      KeyIndexHeapNode(IntegerArray param2IntegerArray) {
        super(KeyIndex.KeyIndexIterator.this);
        this._nodes = param2IntegerArray;
      }
      
      public int step() {
        if (this._position < this._nodes.cardinality()) {
          this._node = this._nodes.at(this._position);
          this._position++;
        } else {
          this._node = -1;
        } 
        return this._node;
      }
      
      public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode() {
        KeyIndexHeapNode keyIndexHeapNode = (KeyIndexHeapNode)super.cloneHeapNode();
        keyIndexHeapNode._nodes = this._nodes;
        keyIndexHeapNode._position = this._position;
        keyIndexHeapNode._markPosition = this._markPosition;
        return keyIndexHeapNode;
      }
      
      public void setMark() { this._markPosition = this._position; }
      
      public void gotoMark() { this._position = this._markPosition; }
      
      public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode param2HeapNode) { return (this._node < param2HeapNode._node); }
      
      public MultiValuedNodeHeapIterator.HeapNode setStartNode(int param2Int) { return this; }
      
      public MultiValuedNodeHeapIterator.HeapNode reset() {
        this._position = 0;
        return this;
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\KeyIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
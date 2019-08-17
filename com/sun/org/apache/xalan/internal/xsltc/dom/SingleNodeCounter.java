package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class SingleNodeCounter extends NodeCounter {
  private static final int[] EmptyArray = new int[0];
  
  DTMAxisIterator _countSiblings = null;
  
  public SingleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { super(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  public SingleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean) { super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean); }
  
  public NodeCounter setStartNode(int paramInt) {
    this._node = paramInt;
    this._nodeType = this._document.getExpandedTypeID(paramInt);
    this._countSiblings = this._document.getAxisIterator(12);
    return this;
  }
  
  public String getCounter() {
    byte b;
    if (this._value != -2.147483648E9D) {
      if (this._value == 0.0D)
        return "0"; 
      if (Double.isNaN(this._value))
        return "NaN"; 
      if (this._value < 0.0D && Double.isInfinite(this._value))
        return "-Infinity"; 
      if (Double.isInfinite(this._value))
        return "Infinity"; 
      b = (int)this._value;
    } else {
      int i = this._node;
      b = 0;
      boolean bool = matchesCount(i);
      if (!bool)
        while ((i = this._document.getParent(i)) > -1 && !matchesCount(i)) {
          if (matchesFrom(i)) {
            i = -1;
            break;
          } 
        }  
      if (i != -1) {
        int j = i;
        if (!bool && this._hasFrom)
          do {
          
          } while ((j = this._document.getParent(j)) > -1 && !matchesFrom(j)); 
        if (j != -1) {
          this._countSiblings.setStartNode(i);
          do {
            if (!matchesCount(i))
              continue; 
            b++;
          } while ((i = this._countSiblings.next()) != -1);
          return formatNumbers(b);
        } 
      } 
      return formatNumbers(EmptyArray);
    } 
    return formatNumbers(b);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { return new DefaultSingleNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  static class DefaultSingleNodeCounter extends SingleNodeCounter {
    public DefaultSingleNodeCounter(Translet param1Translet, DOM param1DOM, DTMAxisIterator param1DTMAxisIterator) { super(param1Translet, param1DOM, param1DTMAxisIterator); }
    
    public NodeCounter setStartNode(int param1Int) {
      this._node = param1Int;
      this._nodeType = this._document.getExpandedTypeID(param1Int);
      this._countSiblings = this._document.getTypedAxisIterator(12, this._document.getExpandedTypeID(param1Int));
      return this;
    }
    
    public String getCounter() {
      byte b;
      if (this._value != -2.147483648E9D) {
        if (this._value == 0.0D)
          return "0"; 
        if (Double.isNaN(this._value))
          return "NaN"; 
        if (this._value < 0.0D && Double.isInfinite(this._value))
          return "-Infinity"; 
        if (Double.isInfinite(this._value))
          return "Infinity"; 
        b = (int)this._value;
      } else {
        b = 1;
        this._countSiblings.setStartNode(this._node);
        int i;
        while ((i = this._countSiblings.next()) != -1)
          b++; 
      } 
      return formatNumbers(b);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SingleNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
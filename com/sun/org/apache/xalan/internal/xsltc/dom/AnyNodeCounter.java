package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class AnyNodeCounter extends NodeCounter {
  public AnyNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { super(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  public AnyNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean) { super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean); }
  
  public NodeCounter setStartNode(int paramInt) {
    this._node = paramInt;
    this._nodeType = this._document.getExpandedTypeID(paramInt);
    return this;
  }
  
  public String getCounter() {
    if (this._value != -2.147483648E9D)
      return (this._value == 0.0D) ? "0" : (Double.isNaN(this._value) ? "NaN" : ((this._value < 0.0D && Double.isInfinite(this._value)) ? "-Infinity" : (Double.isInfinite(this._value) ? "Infinity" : formatNumbers((int)this._value)))); 
    int i = this._node;
    int j = this._document.getDocument();
    byte b = 0;
    while (i >= j && !matchesFrom(i)) {
      if (matchesCount(i))
        b++; 
      i--;
    } 
    return formatNumbers(b);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { return new DefaultAnyNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  static class DefaultAnyNodeCounter extends AnyNodeCounter {
    public DefaultAnyNodeCounter(Translet param1Translet, DOM param1DOM, DTMAxisIterator param1DTMAxisIterator) { super(param1Translet, param1DOM, param1DTMAxisIterator); }
    
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
        int j = this._document.getExpandedTypeID(this._node);
        int k = this._document.getDocument();
        while (i >= 0) {
          if (j == this._document.getExpandedTypeID(i))
            b++; 
          if (i == k)
            break; 
          i--;
        } 
      } 
      return formatNumbers(b);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AnyNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
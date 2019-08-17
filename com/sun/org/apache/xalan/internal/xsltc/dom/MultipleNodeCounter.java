package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class MultipleNodeCounter extends NodeCounter {
  private DTMAxisIterator _precSiblings = null;
  
  public MultipleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { super(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  public MultipleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean) { super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean); }
  
  public NodeCounter setStartNode(int paramInt) {
    this._node = paramInt;
    this._nodeType = this._document.getExpandedTypeID(paramInt);
    this._precSiblings = this._document.getAxisIterator(12);
    return this;
  }
  
  public String getCounter() {
    if (this._value != -2.147483648E9D)
      return (this._value == 0.0D) ? "0" : (Double.isNaN(this._value) ? "NaN" : ((this._value < 0.0D && Double.isInfinite(this._value)) ? "-Infinity" : (Double.isInfinite(this._value) ? "Infinity" : formatNumbers((int)this._value)))); 
    IntegerArray integerArray = new IntegerArray();
    int i = this._node;
    integerArray.add(i);
    while ((i = this._document.getParent(i)) > -1 && !matchesFrom(i))
      integerArray.add(i); 
    int j = integerArray.cardinality();
    int[] arrayOfInt = new int[j];
    byte b;
    for (b = 0; b < j; b++)
      arrayOfInt[b] = Integer.MIN_VALUE; 
    b = 0;
    int k = j - 1;
    while (k >= 0) {
      int m = arrayOfInt[b];
      int n = integerArray.at(k);
      if (matchesCount(n)) {
        this._precSiblings.setStartNode(n);
        while ((i = this._precSiblings.next()) != -1) {
          if (matchesCount(i))
            arrayOfInt[b] = (arrayOfInt[b] == Integer.MIN_VALUE) ? 1 : (arrayOfInt[b] + 1); 
        } 
        arrayOfInt[b] = (arrayOfInt[b] == Integer.MIN_VALUE) ? 1 : (arrayOfInt[b] + 1);
      } 
      k--;
      b++;
    } 
    return formatNumbers(arrayOfInt);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator) { return new DefaultMultipleNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator); }
  
  static class DefaultMultipleNodeCounter extends MultipleNodeCounter {
    public DefaultMultipleNodeCounter(Translet param1Translet, DOM param1DOM, DTMAxisIterator param1DTMAxisIterator) { super(param1Translet, param1DOM, param1DTMAxisIterator); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultipleNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
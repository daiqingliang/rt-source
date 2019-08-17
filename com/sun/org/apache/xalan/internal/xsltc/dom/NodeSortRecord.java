package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xalan.internal.xsltc.CollatorFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.StringComparable;
import java.text.Collator;
import java.util.Locale;

public abstract class NodeSortRecord {
  public static final int COMPARE_STRING = 0;
  
  public static final int COMPARE_NUMERIC = 1;
  
  public static final int COMPARE_ASCENDING = 0;
  
  public static final int COMPARE_DESCENDING = 1;
  
  private static final Collator DEFAULT_COLLATOR = Collator.getInstance();
  
  protected Collator _collator = DEFAULT_COLLATOR;
  
  protected Collator[] _collators;
  
  protected Locale _locale;
  
  protected CollatorFactory _collatorFactory;
  
  protected SortSettings _settings;
  
  private DOM _dom = null;
  
  private int _node;
  
  private int _last = 0;
  
  private int _scanned = 0;
  
  private Object[] _values;
  
  public NodeSortRecord(int paramInt) { this._node = paramInt; }
  
  public NodeSortRecord() { this(0); }
  
  public final void initialize(int paramInt1, int paramInt2, DOM paramDOM, SortSettings paramSortSettings) throws TransletException {
    this._dom = paramDOM;
    this._node = paramInt1;
    this._last = paramInt2;
    this._settings = paramSortSettings;
    int i = paramSortSettings.getSortOrders().length;
    this._values = new Object[i];
    String str = null;
    try {
      str = SecuritySupport.getSystemProperty("com.sun.org.apache.xalan.internal.xsltc.COLLATOR_FACTORY");
    } catch (SecurityException securityException) {}
    if (str != null) {
      try {
        Class clazz = ObjectFactory.findProviderClass(str, true);
        this._collatorFactory = (CollatorFactory)clazz;
      } catch (ClassNotFoundException classNotFoundException) {
        throw new TransletException(classNotFoundException);
      } 
      Locale[] arrayOfLocale = paramSortSettings.getLocales();
      this._collators = new Collator[i];
      for (byte b = 0; b < i; b++)
        this._collators[b] = this._collatorFactory.getCollator(arrayOfLocale[b]); 
      this._collator = this._collators[0];
    } else {
      this._collators = paramSortSettings.getCollators();
      this._collator = this._collators[0];
    } 
  }
  
  public final int getNode() { return this._node; }
  
  public final int compareDocOrder(NodeSortRecord paramNodeSortRecord) { return this._node - paramNodeSortRecord._node; }
  
  private final Comparable stringValue(int paramInt) {
    if (this._scanned <= paramInt) {
      AbstractTranslet abstractTranslet = this._settings.getTranslet();
      Locale[] arrayOfLocale = this._settings.getLocales();
      String[] arrayOfString = this._settings.getCaseOrders();
      String str = extractValueFromDOM(this._dom, this._node, paramInt, abstractTranslet, this._last);
      Comparable comparable = StringComparable.getComparator(str, arrayOfLocale[paramInt], this._collators[paramInt], arrayOfString[paramInt]);
      this._values[this._scanned++] = comparable;
      return comparable;
    } 
    return (Comparable)this._values[paramInt];
  }
  
  private final Double numericValue(int paramInt) {
    if (this._scanned <= paramInt) {
      Double double;
      AbstractTranslet abstractTranslet = this._settings.getTranslet();
      String str = extractValueFromDOM(this._dom, this._node, paramInt, abstractTranslet, this._last);
      try {
        double = new Double(str);
      } catch (NumberFormatException numberFormatException) {
        double = new Double(Double.NEGATIVE_INFINITY);
      } 
      this._values[this._scanned++] = double;
      return double;
    } 
    return (Double)this._values[paramInt];
  }
  
  public int compareTo(NodeSortRecord paramNodeSortRecord) {
    int[] arrayOfInt1 = this._settings.getSortOrders();
    int i = this._settings.getSortOrders().length;
    int[] arrayOfInt2 = this._settings.getTypes();
    for (byte b = 0; b < i; b++) {
      int j;
      if (arrayOfInt2[b] == 1) {
        Double double1 = numericValue(b);
        Double double2 = paramNodeSortRecord.numericValue(b);
        j = double1.compareTo(double2);
      } else {
        Comparable comparable1 = stringValue(b);
        Comparable comparable2 = paramNodeSortRecord.stringValue(b);
        j = comparable1.compareTo(comparable2);
      } 
      if (j != 0)
        return (arrayOfInt1[b] == 1) ? (0 - j) : j; 
    } 
    return this._node - paramNodeSortRecord._node;
  }
  
  public Collator[] getCollator() { return this._collators; }
  
  public abstract String extractValueFromDOM(DOM paramDOM, int paramInt1, int paramInt2, AbstractTranslet paramAbstractTranslet, int paramInt3);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeSortRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
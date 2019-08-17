package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import java.text.Collator;
import java.util.Locale;

final class SortSettings {
  private AbstractTranslet _translet;
  
  private int[] _sortOrders;
  
  private int[] _types;
  
  private Locale[] _locales;
  
  private Collator[] _collators;
  
  private String[] _caseOrders;
  
  SortSettings(AbstractTranslet paramAbstractTranslet, int[] paramArrayOfInt1, int[] paramArrayOfInt2, Locale[] paramArrayOfLocale, Collator[] paramArrayOfCollator, String[] paramArrayOfString) {
    this._translet = paramAbstractTranslet;
    this._sortOrders = paramArrayOfInt1;
    this._types = paramArrayOfInt2;
    this._locales = paramArrayOfLocale;
    this._collators = paramArrayOfCollator;
    this._caseOrders = paramArrayOfString;
  }
  
  AbstractTranslet getTranslet() { return this._translet; }
  
  int[] getSortOrders() { return this._sortOrders; }
  
  int[] getTypes() { return this._types; }
  
  Locale[] getLocales() { return this._locales; }
  
  Collator[] getCollators() { return this._collators; }
  
  String[] getCaseOrders() { return this._caseOrders; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SortSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.utils.LocaleUtility;
import java.text.Collator;
import java.util.Locale;

public class NodeSortRecordFactory {
  private static int DESCENDING = "descending".length();
  
  private static int NUMBER = "number".length();
  
  private final DOM _dom;
  
  private final String _className;
  
  private Class _class;
  
  private SortSettings _sortSettings;
  
  protected Collator _collator;
  
  public NodeSortRecordFactory(DOM paramDOM, String paramString, Translet paramTranslet, String[] paramArrayOfString1, String[] paramArrayOfString2) throws TransletException { this(paramDOM, paramString, paramTranslet, paramArrayOfString1, paramArrayOfString2, null, null); }
  
  public NodeSortRecordFactory(DOM paramDOM, String paramString, Translet paramTranslet, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3, String[] paramArrayOfString4) throws TransletException {
    try {
      this._dom = paramDOM;
      this._className = paramString;
      this._class = paramTranslet.getAuxiliaryClass(paramString);
      if (this._class == null)
        this._class = ObjectFactory.findProviderClass(paramString, true); 
      int i = paramArrayOfString1.length;
      int[] arrayOfInt1 = new int[i];
      int[] arrayOfInt2 = new int[i];
      for (byte b1 = 0; b1 < i; b1++) {
        if (paramArrayOfString1[b1].length() == DESCENDING)
          arrayOfInt1[b1] = 1; 
        if (paramArrayOfString2[b1].length() == NUMBER)
          arrayOfInt2[b1] = 1; 
      } 
      String[] arrayOfString = null;
      if (paramArrayOfString3 == null || paramArrayOfString4 == null) {
        int k = paramArrayOfString1.length;
        arrayOfString = new String[k];
        for (byte b = 0; b < k; b++)
          arrayOfString[b] = ""; 
      } 
      if (paramArrayOfString3 == null)
        paramArrayOfString3 = arrayOfString; 
      if (paramArrayOfString4 == null)
        paramArrayOfString4 = arrayOfString; 
      int j = paramArrayOfString3.length;
      Locale[] arrayOfLocale = new Locale[j];
      Collator[] arrayOfCollator = new Collator[j];
      for (byte b2 = 0; b2 < j; b2++) {
        arrayOfLocale[b2] = LocaleUtility.langToLocale(paramArrayOfString3[b2]);
        arrayOfCollator[b2] = Collator.getInstance(arrayOfLocale[b2]);
      } 
      this._sortSettings = new SortSettings((AbstractTranslet)paramTranslet, arrayOfInt1, arrayOfInt2, arrayOfLocale, arrayOfCollator, paramArrayOfString4);
    } catch (ClassNotFoundException classNotFoundException) {
      throw new TransletException(classNotFoundException);
    } 
  }
  
  public NodeSortRecord makeNodeSortRecord(int paramInt1, int paramInt2) throws ExceptionInInitializerError, LinkageError, IllegalAccessException, InstantiationException, SecurityException, TransletException {
    NodeSortRecord nodeSortRecord = (NodeSortRecord)this._class.newInstance();
    nodeSortRecord.initialize(paramInt1, paramInt2, this._dom, this._sortSettings);
    return nodeSortRecord;
  }
  
  public String getClassName() { return this._className; }
  
  private final void setLang(String[] paramArrayOfString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\NodeSortRecordFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
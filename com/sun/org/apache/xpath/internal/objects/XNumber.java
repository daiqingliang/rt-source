package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import javax.xml.transform.TransformerException;

public class XNumber extends XObject {
  static final long serialVersionUID = -2720400709619020193L;
  
  double m_val;
  
  public XNumber(double paramDouble) { this.m_val = paramDouble; }
  
  public XNumber(Number paramNumber) {
    this.m_val = paramNumber.doubleValue();
    setObject(paramNumber);
  }
  
  public int getType() { return 2; }
  
  public String getTypeString() { return "#NUMBER"; }
  
  public double num() { return this.m_val; }
  
  public double num(XPathContext paramXPathContext) throws TransformerException { return this.m_val; }
  
  public boolean bool() { return !(Double.isNaN(this.m_val) || this.m_val == 0.0D); }
  
  public String str() {
    String str2;
    if (Double.isNaN(this.m_val))
      return "NaN"; 
    if (Double.isInfinite(this.m_val))
      return (this.m_val > 0.0D) ? "Infinity" : "-Infinity"; 
    double d = this.m_val;
    String str1 = Double.toString(d);
    int i = str1.length();
    if (str1.charAt(i - 2) == '.' && str1.charAt(i - 1) == '0') {
      str1 = str1.substring(0, i - 2);
      return str1.equals("-0") ? "0" : str1;
    } 
    int j = str1.indexOf('E');
    if (j < 0)
      return (str1.charAt(i - 1) == '0') ? str1.substring(0, i - 1) : str1; 
    int k = Integer.parseInt(str1.substring(j + 1));
    if (str1.charAt(0) == '-') {
      str2 = "-";
      str1 = str1.substring(1);
      j--;
    } else {
      str2 = "";
    } 
    int m = j - 2;
    if (k >= m)
      return str2 + str1.substring(0, 1) + str1.substring(2, j) + zeros(k - m); 
    while (str1.charAt(j - 1) == '0')
      j--; 
    return (k > 0) ? (str2 + str1.substring(0, 1) + str1.substring(2, 2 + k) + "." + str1.substring(2 + k, j)) : (str2 + "0." + zeros(-1 - k) + str1.substring(0, 1) + str1.substring(2, j));
  }
  
  private static String zeros(int paramInt) {
    if (paramInt < 1)
      return ""; 
    char[] arrayOfChar = new char[paramInt];
    for (byte b = 0; b < paramInt; b++)
      arrayOfChar[b] = '0'; 
    return new String(arrayOfChar);
  }
  
  public Object object() {
    if (null == this.m_obj)
      setObject(new Double(this.m_val)); 
    return this.m_obj;
  }
  
  public boolean equals(XObject paramXObject) {
    int i = paramXObject.getType();
    try {
      return (i == 4) ? paramXObject.equals(this) : ((i == 1) ? ((paramXObject.bool() == bool()) ? 1 : 0) : ((this.m_val == paramXObject.num()) ? 1 : 0));
    } catch (TransformerException transformerException) {
      throw new WrappedRuntimeException(transformerException);
    } 
  }
  
  public boolean isStableNumber() { return true; }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor) { paramXPathVisitor.visitNumberLiteral(paramExpressionOwner, this); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\objects\XNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
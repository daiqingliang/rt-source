package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Objects;

public class Arg {
  private QName m_qname = new QName("");
  
  private XObject m_val = null;
  
  private String m_expression;
  
  private boolean m_isFromWithParam;
  
  private boolean m_isVisible;
  
  public final QName getQName() { return this.m_qname; }
  
  public final void setQName(QName paramQName) { this.m_qname = paramQName; }
  
  public final XObject getVal() { return this.m_val; }
  
  public final void setVal(XObject paramXObject) { this.m_val = paramXObject; }
  
  public void detach() {
    if (null != this.m_val) {
      this.m_val.allowDetachToRelease(true);
      this.m_val.detach();
    } 
  }
  
  public String getExpression() { return this.m_expression; }
  
  public void setExpression(String paramString) { this.m_expression = paramString; }
  
  public boolean isFromWithParam() { return this.m_isFromWithParam; }
  
  public boolean isVisible() { return this.m_isVisible; }
  
  public void setIsVisible(boolean paramBoolean) { this.m_isVisible = paramBoolean; }
  
  public Arg() {
    this.m_expression = null;
    this.m_isVisible = true;
    this.m_isFromWithParam = false;
  }
  
  public Arg(QName paramQName, String paramString, boolean paramBoolean) {
    this.m_expression = paramString;
    this.m_isFromWithParam = paramBoolean;
    this.m_isVisible = !paramBoolean;
  }
  
  public Arg(QName paramQName, XObject paramXObject) {
    this.m_isVisible = true;
    this.m_isFromWithParam = false;
    this.m_expression = null;
  }
  
  public int hashCode() { return Objects.hashCode(this.m_qname); }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof QName) ? this.m_qname.equals(paramObject) : super.equals(paramObject); }
  
  public Arg(QName paramQName, XObject paramXObject, boolean paramBoolean) {
    this.m_isFromWithParam = paramBoolean;
    this.m_isVisible = !paramBoolean;
    this.m_expression = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\Arg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
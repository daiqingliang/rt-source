package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class VariableStack implements Cloneable {
  public static final int CLEARLIMITATION = 1024;
  
  XObject[] _stackFrames = new XObject[8192];
  
  int _frameTop;
  
  private int _currentFrameBottom;
  
  int[] _links = new int[4096];
  
  int _linksTop;
  
  private static XObject[] m_nulls = new XObject[1024];
  
  public VariableStack() { reset(); }
  
  public Object clone() throws CloneNotSupportedException {
    VariableStack variableStack = (VariableStack)super.clone();
    variableStack._stackFrames = (XObject[])this._stackFrames.clone();
    variableStack._links = (int[])this._links.clone();
    return variableStack;
  }
  
  public XObject elementAt(int paramInt) { return this._stackFrames[paramInt]; }
  
  public int size() { return this._frameTop; }
  
  public void reset() {
    this._frameTop = 0;
    this._linksTop = 0;
    this._links[this._linksTop++] = 0;
    this._stackFrames = new XObject[this._stackFrames.length];
  }
  
  public void setStackFrame(int paramInt) { this._currentFrameBottom = paramInt; }
  
  public int getStackFrame() { return this._currentFrameBottom; }
  
  public int link(int paramInt) {
    this._currentFrameBottom = this._frameTop;
    this._frameTop += paramInt;
    if (this._frameTop >= this._stackFrames.length) {
      XObject[] arrayOfXObject = new XObject[this._stackFrames.length + 4096 + paramInt];
      System.arraycopy(this._stackFrames, 0, arrayOfXObject, 0, this._stackFrames.length);
      this._stackFrames = arrayOfXObject;
    } 
    if (this._linksTop + 1 >= this._links.length) {
      int[] arrayOfInt = new int[this._links.length + 2048];
      System.arraycopy(this._links, 0, arrayOfInt, 0, this._links.length);
      this._links = arrayOfInt;
    } 
    this._links[this._linksTop++] = this._currentFrameBottom;
    return this._currentFrameBottom;
  }
  
  public void unlink() {
    this._frameTop = this._links[--this._linksTop];
    this._currentFrameBottom = this._links[this._linksTop - 1];
  }
  
  public void unlink(int paramInt) {
    this._frameTop = this._links[--this._linksTop];
    this._currentFrameBottom = paramInt;
  }
  
  public void setLocalVariable(int paramInt, XObject paramXObject) { this._stackFrames[paramInt + this._currentFrameBottom] = paramXObject; }
  
  public void setLocalVariable(int paramInt1, XObject paramXObject, int paramInt2) { this._stackFrames[paramInt1 + paramInt2] = paramXObject; }
  
  public XObject getLocalVariable(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    paramInt += this._currentFrameBottom;
    XObject xObject = this._stackFrames[paramInt];
    if (null == xObject)
      throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), paramXPathContext.getSAXLocator()); 
    if (xObject.getType() == 600) {
      this._stackFrames[paramInt] = xObject.execute(paramXPathContext);
      return xObject.execute(paramXPathContext);
    } 
    return xObject;
  }
  
  public XObject getLocalVariable(int paramInt1, int paramInt2) throws TransformerException {
    paramInt1 += paramInt2;
    return this._stackFrames[paramInt1];
  }
  
  public XObject getLocalVariable(XPathContext paramXPathContext, int paramInt, boolean paramBoolean) throws TransformerException {
    paramInt += this._currentFrameBottom;
    XObject xObject = this._stackFrames[paramInt];
    if (null == xObject)
      throw new TransformerException(XSLMessages.createXPATHMessage("ER_VARIABLE_ACCESSED_BEFORE_BIND", null), paramXPathContext.getSAXLocator()); 
    if (xObject.getType() == 600) {
      this._stackFrames[paramInt] = xObject.execute(paramXPathContext);
      return xObject.execute(paramXPathContext);
    } 
    return paramBoolean ? xObject : xObject.getFresh();
  }
  
  public boolean isLocalSet(int paramInt) throws TransformerException { return (this._stackFrames[paramInt + this._currentFrameBottom] != null); }
  
  public void clearLocalSlots(int paramInt1, int paramInt2) {
    paramInt1 += this._currentFrameBottom;
    System.arraycopy(m_nulls, 0, this._stackFrames, paramInt1, paramInt2);
  }
  
  public void setGlobalVariable(int paramInt, XObject paramXObject) { this._stackFrames[paramInt] = paramXObject; }
  
  public XObject getGlobalVariable(XPathContext paramXPathContext, int paramInt) throws TransformerException {
    XObject xObject = this._stackFrames[paramInt];
    if (xObject.getType() == 600) {
      this._stackFrames[paramInt] = xObject.execute(paramXPathContext);
      return xObject.execute(paramXPathContext);
    } 
    return xObject;
  }
  
  public XObject getGlobalVariable(XPathContext paramXPathContext, int paramInt, boolean paramBoolean) throws TransformerException {
    XObject xObject = this._stackFrames[paramInt];
    if (xObject.getType() == 600) {
      this._stackFrames[paramInt] = xObject.execute(paramXPathContext);
      return xObject.execute(paramXPathContext);
    } 
    return paramBoolean ? xObject : xObject.getFresh();
  }
  
  public XObject getVariableOrParam(XPathContext paramXPathContext, QName paramQName) throws TransformerException { throw new TransformerException(XSLMessages.createXPATHMessage("ER_VAR_NOT_RESOLVABLE", new Object[] { paramQName.toString() })); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\VariableStack.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
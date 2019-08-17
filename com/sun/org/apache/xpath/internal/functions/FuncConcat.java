package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.TransformerException;

public class FuncConcat extends FunctionMultiArgs {
  static final long serialVersionUID = 1737228885202314413L;
  
  public XObject execute(XPathContext paramXPathContext) throws TransformerException {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.m_arg0.execute(paramXPathContext).str());
    stringBuffer.append(this.m_arg1.execute(paramXPathContext).str());
    if (null != this.m_arg2)
      stringBuffer.append(this.m_arg2.execute(paramXPathContext).str()); 
    if (null != this.m_args)
      for (byte b = 0; b < this.m_args.length; b++)
        stringBuffer.append(this.m_args[b].execute(paramXPathContext).str());  
    return new XString(stringBuffer.toString());
  }
  
  public void checkNumberArgs(int paramInt) throws WrongNumberArgsException {
    if (paramInt < 2)
      reportWrongNumberArgs(); 
  }
  
  protected void reportWrongNumberArgs() { throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("gtone", null)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncConcat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
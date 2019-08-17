package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.utils.ConfigurationError;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import com.sun.org.apache.xpath.internal.functions.Function;
import javax.xml.transform.TransformerException;

public class FuncLoader {
  private int m_funcID;
  
  private String m_funcName;
  
  public String getName() { return this.m_funcName; }
  
  public FuncLoader(String paramString, int paramInt) {
    this.m_funcID = paramInt;
    this.m_funcName = paramString;
  }
  
  Function getFunction() throws TransformerException {
    try {
      String str1 = this.m_funcName;
      if (str1.indexOf(".") < 0)
        str1 = "com.sun.org.apache.xpath.internal.functions." + str1; 
      String str2 = str1.substring(0, str1.lastIndexOf('.'));
      if (!str2.equals("com.sun.org.apache.xalan.internal.templates") && !str2.equals("com.sun.org.apache.xpath.internal.functions"))
        throw new TransformerException("Application can't install his own xpath function."); 
      return (Function)ObjectFactory.newInstance(str1, true);
    } catch (ConfigurationError configurationError) {
      throw new TransformerException(configurationError.getException());
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\FuncLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
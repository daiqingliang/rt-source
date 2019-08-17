package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Attribute extends Instruction {
  private QName _name;
  
  public void display(int paramInt) {
    indent(paramInt);
    Util.println("Attribute " + this._name);
    displayContents(paramInt + 4);
  }
  
  public void parseContents(Parser paramParser) {
    this._name = paramParser.getQName(getAttribute("name"));
    parseChildren(paramParser);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Attribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
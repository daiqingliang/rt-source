package com.sun.org.apache.xalan.internal.xsltc.compiler;

abstract class AttributeValue extends Expression {
  public static final AttributeValue create(SyntaxTreeNode paramSyntaxTreeNode, String paramString, Parser paramParser) {
    SimpleAttributeValue simpleAttributeValue;
    if (paramString.indexOf('{') != -1) {
      simpleAttributeValue = new AttributeValueTemplate(paramString, paramParser, paramSyntaxTreeNode);
    } else if (paramString.indexOf('}') != -1) {
      simpleAttributeValue = new AttributeValueTemplate(paramString, paramParser, paramSyntaxTreeNode);
    } else {
      simpleAttributeValue = new SimpleAttributeValue(paramString);
      simpleAttributeValue.setParser(paramParser);
      simpleAttributeValue.setParent(paramSyntaxTreeNode);
    } 
    return simpleAttributeValue;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\AttributeValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
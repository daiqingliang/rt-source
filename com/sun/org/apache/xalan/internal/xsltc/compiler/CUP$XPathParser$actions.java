package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.java_cup.internal.runtime.lr_parser;
import java.util.Stack;
import java.util.Vector;

class CUP$XPathParser$actions {
  private final XPathParser parser;
  
  CUP$XPathParser$actions(XPathParser paramXPathParser) { this.parser = paramXPathParser; }
  
  public final Symbol CUP$XPathParser$do_action(int paramInt1, lr_parser paramlr_parser, Stack paramStack, int paramInt2) throws Exception {
    Vector vector6;
    Vector vector4;
    int i8;
    Vector vector5;
    FilterParentPath filterParentPath;
    int i7;
    int i6;
    Step step4;
    Step step3;
    int i4;
    int i5;
    Object object3;
    Vector vector3;
    int i3;
    int i2;
    EqualityExpr equalityExpr;
    Step step2;
    int i1;
    Vector vector2;
    SyntaxTreeNode syntaxTreeNode;
    long l;
    int k;
    int n;
    Vector vector1;
    String str3;
    int m;
    QName qName3;
    String str1;
    String str2;
    QName qName2;
    Object object2;
    int j;
    int i;
    Step step1;
    Integer integer1;
    Integer integer2;
    Object object1;
    QName qName1;
    Symbol symbol;
    switch (paramInt1) {
      case 140:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("id");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 139:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("self");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 138:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("preceding-sibling");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 137:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("preceding");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 136:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("parent");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 135:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("namespace");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 134:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("following-sibling");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 133:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("following");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 132:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("decendant-or-self");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 131:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("decendant");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 130:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("child");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 129:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("attribute");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 128:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("ancestor-or-self");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 127:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("child");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 126:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("key");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 125:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("mod");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 124:
        qName1 = null;
        qName1 = this.parser.getQNameIgnoreDefaultNs("div");
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 123:
        qName1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        str2 = (String)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        qName1 = this.parser.getQNameIgnoreDefaultNs(str2);
        return new Symbol(37, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 122:
        qName1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        qName2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        qName1 = qName2;
        return new Symbol(26, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 121:
        qName1 = null;
        qName1 = null;
        return new Symbol(26, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, qName1);
      case 120:
        qName1 = null;
        integer2 = new Integer(7);
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, integer2);
      case 119:
        integer2 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        str1 = (String)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        qName3 = this.parser.getQNameIgnoreDefaultNs("name");
        equalityExpr = new EqualityExpr(0, new NameCall(qName3), new LiteralExpr(str1));
        vector3 = new Vector();
        vector3.addElement(new Predicate(equalityExpr));
        step1 = new Step(3, 7, vector3);
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 3)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, step1);
      case 118:
        step1 = null;
        integer1 = new Integer(8);
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, integer1);
      case 117:
        integer1 = null;
        integer1 = new Integer(3);
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, integer1);
      case 116:
        integer1 = null;
        integer1 = new Integer(-1);
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, integer1);
      case 115:
        integer1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(25, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 114:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(3, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 113:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(39, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 112:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(38, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 111:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        i1 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        vector3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        vector3.insertElementAt(object2, 0);
        object1 = vector3;
        return new Symbol(36, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 110:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        vector2 = new Vector();
        vector2.addElement(object2);
        object1 = vector2;
        return new Symbol(36, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 109:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 3)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 3)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 3)).value;
        n = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        vector3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        if (object2 == this.parser.getQNameIgnoreDefaultNs("concat")) {
          object1 = new ConcatCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("number")) {
          object1 = new NumberCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("document")) {
          this.parser.setMultiDocument(true);
          object1 = new DocumentCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("string")) {
          object1 = new StringCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("boolean")) {
          object1 = new BooleanCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("name")) {
          object1 = new NameCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("generate-id")) {
          object1 = new GenerateIdCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("not")) {
          object1 = new NotCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("format-number")) {
          object1 = new FormatNumberCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("unparsed-entity-uri")) {
          object1 = new UnparsedEntityUriCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("key")) {
          object1 = new KeyCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("id")) {
          object1 = new KeyCall(object2, vector3);
          this.parser.setHasIdCall(true);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("ceiling")) {
          object1 = new CeilingCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("round")) {
          object1 = new RoundCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("floor")) {
          object1 = new FloorCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("contains")) {
          object1 = new ContainsCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("string-length")) {
          object1 = new StringLengthCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("starts-with")) {
          object1 = new StartsWithCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("function-available")) {
          object1 = new FunctionAvailableCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("element-available")) {
          object1 = new ElementAvailableCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("local-name")) {
          object1 = new LocalNameCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("lang")) {
          object1 = new LangCall(object2, vector3);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("namespace-uri")) {
          object1 = new NamespaceUriCall(object2, vector3);
        } else if (object2 == this.parser.getQName("http://xml.apache.org/xalan/xsltc", "xsltc", "cast")) {
          object1 = new CastCall(object2, vector3);
        } else if (object2.getLocalPart().equals("nodeset") || object2.getLocalPart().equals("node-set")) {
          this.parser.setCallsNodeset(true);
          object1 = new FunctionCall(object2, vector3);
        } else {
          object1 = new FunctionCall(object2, vector3);
        } 
        return new Symbol(16, ((Symbol)paramStack.elementAt(paramInt2 - 3)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 108:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        if (object2 == this.parser.getQNameIgnoreDefaultNs("current")) {
          object1 = new CurrentCall(object2);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("number")) {
          this.parser;
          object1 = new NumberCall(object2, XPathParser.EmptyArgs);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("string")) {
          this.parser;
          object1 = new StringCall(object2, XPathParser.EmptyArgs);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("concat")) {
          this.parser;
          object1 = new ConcatCall(object2, XPathParser.EmptyArgs);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("true")) {
          object1 = new BooleanExpr(true);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("false")) {
          object1 = new BooleanExpr(false);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("name")) {
          object1 = new NameCall(object2);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("generate-id")) {
          this.parser;
          object1 = new GenerateIdCall(object2, XPathParser.EmptyArgs);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("string-length")) {
          this.parser;
          object1 = new StringLengthCall(object2, XPathParser.EmptyArgs);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("position")) {
          object1 = new PositionCall(object2);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("last")) {
          object1 = new LastCall(object2);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("local-name")) {
          object1 = new LocalNameCall(object2);
        } else if (object2 == this.parser.getQNameIgnoreDefaultNs("namespace-uri")) {
          object1 = new NamespaceUriCall(object2);
        } else {
          this.parser;
          object1 = new FunctionCall(object2, XPathParser.EmptyArgs);
        } 
        return new Symbol(16, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 107:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        syntaxTreeNode = this.parser.lookupName(object2);
        if (syntaxTreeNode != null)
          if (syntaxTreeNode instanceof Variable) {
            object1 = new VariableRef((Variable)syntaxTreeNode);
          } else if (syntaxTreeNode instanceof Param) {
            object1 = new ParameterRef((Param)syntaxTreeNode);
          } else {
            object1 = new UnresolvedRef(object2);
          }  
        if (syntaxTreeNode == null)
          object1 = new UnresolvedRef(object2); 
        return new Symbol(15, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 106:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 105:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Double)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new RealExpr(object2.doubleValue());
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 104:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Long)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        l = object2.longValue();
        if (l < -2147483648L || l > 2147483647L) {
          object1 = new RealExpr(l);
        } else if (object2.doubleValue() == 0.0D) {
          object1 = new RealExpr(object2.doubleValue());
        } else if (object2.intValue() == 0) {
          object1 = new IntExpr(object2.intValue());
        } else if (object2.doubleValue() == 0.0D) {
          object1 = new RealExpr(object2.doubleValue());
        } else {
          object1 = new IntExpr(object2.intValue());
        } 
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 103:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (String)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        str3 = null;
        i3 = object2.lastIndexOf(':');
        if (i3 > 0) {
          String str = object2.substring(0, i3);
          str3 = this.parser._symbolTable.lookupNamespace(str);
        } 
        object1 = (str3 == null) ? new LiteralExpr(object2) : new LiteralExpr(object2, str3);
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 102:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = object2;
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 101:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(17, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 100:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        vector3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new FilterExpr(object2, vector3);
        return new Symbol(6, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 99:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(6, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 98:
        object1 = null;
        object1 = new Step(10, -1, null);
        return new Symbol(20, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 97:
        object1 = null;
        object1 = new Step(13, -1, null);
        return new Symbol(20, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 96:
        object1 = null;
        object1 = new Integer(13);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 95:
        object1 = null;
        object1 = new Integer(12);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 94:
        object1 = null;
        object1 = new Integer(11);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 93:
        object1 = null;
        object1 = new Integer(10);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 92:
        object1 = null;
        object1 = new Integer(9);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 91:
        object1 = null;
        object1 = new Integer(7);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 90:
        object1 = null;
        object1 = new Integer(6);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 89:
        object1 = null;
        object1 = new Integer(5);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 88:
        object1 = null;
        object1 = new Integer(4);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 87:
        object1 = null;
        object1 = new Integer(3);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 86:
        object1 = null;
        object1 = new Integer(2);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 85:
        object1 = null;
        object1 = new Integer(1);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 84:
        object1 = null;
        object1 = new Integer(0);
        return new Symbol(40, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 83:
        object1 = null;
        object1 = new Integer(2);
        return new Symbol(41, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 82:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = object2;
        return new Symbol(41, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 81:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(7, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 80:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new Step(object2.intValue(), this.parser.findNodeType(object2.intValue(), object3), null);
        return new Symbol(7, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 79:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object3 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        i5 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i7 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        vector5 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new Step(object2.intValue(), this.parser.findNodeType(object2.intValue(), object3), vector5);
        return new Symbol(7, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 78:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        if (object2 instanceof Step) {
          Step step = (Step)object2;
          step.addPredicates(object3);
          object1 = (Step)object2;
        } else {
          object1 = new Step(3, this.parser.findNodeType(3, object2), object3);
        } 
        return new Symbol(7, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 77:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        if (object2 instanceof Step) {
          object1 = (Step)object2;
        } else {
          object1 = new Step(3, this.parser.findNodeType(3, object2), null);
        } 
        return new Symbol(7, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 76:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        m = -1;
        if (object2 instanceof Step && this.parser.isElementAxis(((Step)object2).getAxis()))
          m = 1; 
        step2 = new Step(5, m, null);
        object1 = new AbsoluteLocationPath(this.parser.insertStep(step2, (RelativeLocationPath)object2));
        return new Symbol(24, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 75:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        step3 = (Step)object3;
        i7 = step3.getAxis();
        i8 = step3.getNodeType();
        vector6 = step3.getPredicates();
        if (i7 == 3 && i8 != 2) {
          if (vector6 == null) {
            step3.setAxis(4);
            if (object2 instanceof Step && ((Step)object2).isAbbreviatedDot()) {
              object1 = step3;
            } else {
              RelativeLocationPath relativeLocationPath = (RelativeLocationPath)object2;
              object1 = new ParentLocationPath(relativeLocationPath, step3);
            } 
          } else if (object2 instanceof Step && ((Step)object2).isAbbreviatedDot()) {
            Step step = new Step(5, 1, null);
            object1 = new ParentLocationPath(step, step3);
          } else {
            RelativeLocationPath relativeLocationPath = (RelativeLocationPath)object2;
            Step step = new Step(5, 1, null);
            ParentLocationPath parentLocationPath = new ParentLocationPath(step, step3);
            object1 = new ParentLocationPath(relativeLocationPath, parentLocationPath);
          } 
        } else if (i7 == 2 || i8 == 2) {
          RelativeLocationPath relativeLocationPath = (RelativeLocationPath)object2;
          Step step = new Step(5, 1, null);
          ParentLocationPath parentLocationPath = new ParentLocationPath(step, step3);
          object1 = new ParentLocationPath(relativeLocationPath, parentLocationPath);
        } else {
          RelativeLocationPath relativeLocationPath = (RelativeLocationPath)object2;
          Step step = new Step(5, -1, null);
          ParentLocationPath parentLocationPath = new ParentLocationPath(step, step3);
          object1 = new ParentLocationPath(relativeLocationPath, parentLocationPath);
        } 
        return new Symbol(22, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 74:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(23, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 73:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AbsoluteLocationPath(object2);
        return new Symbol(23, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 72:
        object1 = null;
        object1 = new AbsoluteLocationPath();
        return new Symbol(23, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 71:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(21, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 70:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        if (object2 instanceof Step && ((Step)object2).isAbbreviatedDot()) {
          object1 = object3;
        } else if (((Step)object3).isAbbreviatedDot()) {
          object1 = object2;
        } else {
          object1 = new ParentLocationPath((RelativeLocationPath)object2, object3);
        } 
        return new Symbol(21, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 69:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(21, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 68:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(4, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 67:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(4, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 66:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        i4 = -1;
        if (object3 instanceof Step && this.parser.isElementAxis(((Step)object3).getAxis()))
          i4 = 1; 
        step4 = new Step(5, i4, null);
        filterParentPath = new FilterParentPath(object2, step4);
        filterParentPath = new FilterParentPath(filterParentPath, object3);
        if (!(object2 instanceof KeyCall))
          filterParentPath.setDescendantAxis(); 
        object1 = filterParentPath;
        return new Symbol(19, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 65:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new FilterParentPath(object2, object3);
        return new Symbol(19, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 64:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(19, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 63:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(19, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 62:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new UnionPathExpr(object2, object3);
        return new Symbol(18, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 61:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(18, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 60:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new UnaryOpExpr(object2);
        return new Symbol(14, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 59:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(14, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 58:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new BinOpExpr(4, object2, object3);
        return new Symbol(13, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 57:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new BinOpExpr(3, object2, object3);
        return new Symbol(13, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 56:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new BinOpExpr(2, object2, object3);
        return new Symbol(13, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 55:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(13, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 54:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new BinOpExpr(1, object2, object3);
        return new Symbol(12, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 53:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new BinOpExpr(0, object2, object3);
        return new Symbol(12, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 52:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(12, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 51:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new RelationalExpr(4, object2, object3);
        return new Symbol(11, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 50:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new RelationalExpr(5, object2, object3);
        return new Symbol(11, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 49:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new RelationalExpr(2, object2, object3);
        return new Symbol(11, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 48:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new RelationalExpr(3, object2, object3);
        return new Symbol(11, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 47:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(11, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 46:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new EqualityExpr(1, object2, object3);
        return new Symbol(10, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 45:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new EqualityExpr(0, object2, object3);
        return new Symbol(10, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 44:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(10, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 43:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new LogicalExpr(1, object2, object3);
        return new Symbol(9, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 42:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(9, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 41:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new LogicalExpr(0, object2, object3);
        return new Symbol(8, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 40:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(8, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 39:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(2, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 38:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = new Predicate(object2);
        return new Symbol(5, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 37:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        m = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object3.insertElementAt(object2, 0);
        object1 = object3;
        return new Symbol(35, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 36:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        vector1 = new Vector();
        vector1.addElement(object2);
        object1 = vector1;
        return new Symbol(35, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 35:
        object1 = null;
        object1 = new Integer(2);
        return new Symbol(42, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 34:
        object1 = null;
        object1 = new Integer(3);
        return new Symbol(42, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 33:
        object1 = null;
        object1 = new Integer(2);
        return new Symbol(42, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 32:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (QName)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(34, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 31:
        object1 = null;
        object1 = null;
        return new Symbol(34, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 30:
        object1 = null;
        object1 = new Integer(7);
        return new Symbol(33, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 29:
        object1 = null;
        object1 = new Integer(8);
        return new Symbol(33, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 28:
        object1 = null;
        object1 = new Integer(3);
        return new Symbol(33, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 27:
        object1 = null;
        object1 = new Integer(-1);
        return new Symbol(33, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 26:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(33, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 25:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object3 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        i4 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i6 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        vector4 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = (ProcessingInstructionPattern)object3.setPredicates(vector4);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 24:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object3;
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 23:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object3 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        i4 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i6 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        vector4 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = this.parser.createStepPattern(object2.intValue(), object3, vector4);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 22:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (Integer)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = this.parser.createStepPattern(object2.intValue(), object3, null);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 21:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = (ProcessingInstructionPattern)object2.setPredicates(object3);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 20:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 19:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Vector)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = this.parser.createStepPattern(3, object2, object3);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 18:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = this.parser.createStepPattern(3, object2, null);
        return new Symbol(32, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 17:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AncestorPattern(object2, object3);
        return new Symbol(31, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 16:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new ParentPattern(object2, object3);
        return new Symbol(31, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 15:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (StepPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(31, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 14:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (String)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = new ProcessingInstructionPattern(object2);
        return new Symbol(30, ((Symbol)paramStack.elementAt(paramInt2 - 3)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 13:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 3)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 3)).right;
        object2 = (String)((Symbol)paramStack.elementAt(paramInt2 - 3)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object3 = (String)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = new KeyPattern(object2, object3);
        return new Symbol(27, ((Symbol)paramStack.elementAt(paramInt2 - 5)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 12:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (String)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = new IdPattern(object2);
        this.parser.setHasIdCall(true);
        return new Symbol(27, ((Symbol)paramStack.elementAt(paramInt2 - 3)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 11:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 10:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AncestorPattern(object2);
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 9:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (IdKeyPattern)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AncestorPattern(object2, object3);
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 8:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (IdKeyPattern)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new ParentPattern(object2, object3);
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 7:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (IdKeyPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 6:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (RelativePathPattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AbsolutePathPattern(object2);
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 5:
        object1 = null;
        object1 = new AbsolutePathPattern(null);
        return new Symbol(29, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 4:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 2)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 2)).right;
        object2 = (Pattern)((Symbol)paramStack.elementAt(paramInt2 - 2)).value;
        k = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        i2 = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object3 = (Pattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = new AlternativePattern(object2, object3);
        return new Symbol(28, ((Symbol)paramStack.elementAt(paramInt2 - 2)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 3:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Pattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(28, ((Symbol)paramStack.elementAt(paramInt2 - 0)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 2:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Expression)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(1, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 1:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 0)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 0)).right;
        object2 = (Pattern)((Symbol)paramStack.elementAt(paramInt2 - 0)).value;
        object1 = object2;
        return new Symbol(1, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
      case 0:
        object1 = null;
        i = ((Symbol)paramStack.elementAt(paramInt2 - 1)).left;
        j = ((Symbol)paramStack.elementAt(paramInt2 - 1)).right;
        object2 = (SyntaxTreeNode)((Symbol)paramStack.elementAt(paramInt2 - 1)).value;
        object1 = object2;
        symbol = new Symbol(0, ((Symbol)paramStack.elementAt(paramInt2 - 1)).left, ((Symbol)paramStack.elementAt(paramInt2 - 0)).right, object1);
        paramlr_parser.done_parsing();
        return symbol;
    } 
    throw new Exception("Invalid action number found in internal parse table");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\CUP$XPathParser$actions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
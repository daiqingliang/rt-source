package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.IF_ICMPEQ;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.StringTokenizer;
import java.util.Vector;

final class Whitespace extends TopLevelElement {
  public static final int USE_PREDICATE = 0;
  
  public static final int STRIP_SPACE = 1;
  
  public static final int PRESERVE_SPACE = 2;
  
  public static final int RULE_NONE = 0;
  
  public static final int RULE_ELEMENT = 1;
  
  public static final int RULE_NAMESPACE = 2;
  
  public static final int RULE_ALL = 3;
  
  private String _elementList;
  
  private int _action;
  
  private int _importPrecedence;
  
  public void parseContents(Parser paramParser) {
    this._action = this._qname.getLocalPart().endsWith("strip-space") ? 1 : 2;
    this._importPrecedence = paramParser.getCurrentImportPrecedence();
    this._elementList = getAttribute("elements");
    if (this._elementList == null || this._elementList.length() == 0) {
      reportError(this, paramParser, "REQUIRED_ATTR_ERR", "elements");
      return;
    } 
    SymbolTable symbolTable = paramParser.getSymbolTable();
    StringTokenizer stringTokenizer = new StringTokenizer(this._elementList);
    StringBuffer stringBuffer = new StringBuffer("");
    while (stringTokenizer.hasMoreElements()) {
      String str = stringTokenizer.nextToken();
      int i = str.indexOf(':');
      if (i != -1) {
        String str1 = lookupNamespace(str.substring(0, i));
        if (str1 != null) {
          stringBuffer.append(str1).append(':').append(str.substring(i + 1));
        } else {
          stringBuffer.append(str);
        } 
      } else {
        stringBuffer.append(str);
      } 
      if (stringTokenizer.hasMoreElements())
        stringBuffer.append(" "); 
    } 
    this._elementList = stringBuffer.toString();
  }
  
  public Vector getRules() {
    Vector vector = new Vector();
    StringTokenizer stringTokenizer = new StringTokenizer(this._elementList);
    while (stringTokenizer.hasMoreElements())
      vector.add(new WhitespaceRule(this._action, stringTokenizer.nextToken(), this._importPrecedence)); 
    return vector;
  }
  
  private static WhitespaceRule findContradictingRule(Vector paramVector, WhitespaceRule paramWhitespaceRule) {
    for (byte b = 0; b < paramVector.size(); b++) {
      WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.elementAt(b);
      if (whitespaceRule == paramWhitespaceRule)
        return null; 
      switch (whitespaceRule.getStrength()) {
        case 3:
          return whitespaceRule;
        case 1:
          if (!paramWhitespaceRule.getElement().equals(whitespaceRule.getElement()))
            break; 
        case 2:
          if (paramWhitespaceRule.getNamespace().equals(whitespaceRule.getNamespace()))
            return whitespaceRule; 
          break;
      } 
    } 
    return null;
  }
  
  private static int prioritizeRules(Vector paramVector) {
    int i = 2;
    quicksort(paramVector, 0, paramVector.size() - 1);
    boolean bool = false;
    byte b;
    for (b = 0; b < paramVector.size(); b++) {
      WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.elementAt(b);
      if (whitespaceRule.getAction() == 1)
        bool = true; 
    } 
    if (!bool) {
      paramVector.removeAllElements();
      return 2;
    } 
    for (b = 0; b < paramVector.size(); b++) {
      WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.elementAt(b);
      if (findContradictingRule(paramVector, whitespaceRule) != null) {
        paramVector.remove(b);
        continue;
      } 
      if (whitespaceRule.getStrength() == 3) {
        i = whitespaceRule.getAction();
        for (byte b1 = b; b1 < paramVector.size(); b1++)
          paramVector.removeElementAt(b1); 
      } 
    } 
    if (paramVector.size() == 0)
      return i; 
    while (true) {
      WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.lastElement();
      if (whitespaceRule.getAction() == i) {
        paramVector.removeElementAt(paramVector.size() - 1);
        if (paramVector.size() <= 0)
          break; 
        continue;
      } 
      break;
    } 
    return i;
  }
  
  public static void compileStripSpace(BranchHandle[] paramArrayOfBranchHandle, int paramInt, InstructionList paramInstructionList) {
    InstructionHandle instructionHandle = paramInstructionList.append(ICONST_1);
    paramInstructionList.append(IRETURN);
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfBranchHandle[b].setTarget(instructionHandle); 
  }
  
  public static void compilePreserveSpace(BranchHandle[] paramArrayOfBranchHandle, int paramInt, InstructionList paramInstructionList) {
    InstructionHandle instructionHandle = paramInstructionList.append(ICONST_0);
    paramInstructionList.append(IRETURN);
    for (byte b = 0; b < paramInt; b++)
      paramArrayOfBranchHandle[b].setTarget(instructionHandle); 
  }
  
  private static void compilePredicate(Vector paramVector, int paramInt, ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    MethodGenerator methodGenerator = new MethodGenerator(17, Type.BOOLEAN, new Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Type.INT, Type.INT }, new String[] { "dom", "node", "type" }, "stripSpace", paramClassGenerator.getClassName(), instructionList, constantPoolGen);
    paramClassGenerator.addInterface("com/sun/org/apache/xalan/internal/xsltc/StripFilter");
    int i = methodGenerator.getLocalIndex("dom");
    int j = methodGenerator.getLocalIndex("node");
    int k = methodGenerator.getLocalIndex("type");
    BranchHandle[] arrayOfBranchHandle1 = new BranchHandle[paramVector.size()];
    BranchHandle[] arrayOfBranchHandle2 = new BranchHandle[paramVector.size()];
    byte b1 = 0;
    byte b2 = 0;
    for (byte b3 = 0; b3 < paramVector.size(); b3++) {
      WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.elementAt(b3);
      int m = constantPoolGen.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNamespaceName", "(I)Ljava/lang/String;");
      int n = constantPoolGen.addMethodref("java/lang/String", "compareTo", "(Ljava/lang/String;)I");
      if (whitespaceRule.getStrength() == 2) {
        instructionList.append(new ALOAD(i));
        instructionList.append(new ILOAD(j));
        instructionList.append(new INVOKEINTERFACE(m, 2));
        instructionList.append(new PUSH(constantPoolGen, whitespaceRule.getNamespace()));
        instructionList.append(new INVOKEVIRTUAL(n));
        instructionList.append(ICONST_0);
        if (whitespaceRule.getAction() == 1) {
          arrayOfBranchHandle1[b1++] = instructionList.append(new IF_ICMPEQ(null));
        } else {
          arrayOfBranchHandle2[b2++] = instructionList.append(new IF_ICMPEQ(null));
        } 
      } else if (whitespaceRule.getStrength() == 1) {
        QName qName;
        Parser parser = paramClassGenerator.getParser();
        if (whitespaceRule.getNamespace() != "") {
          qName = parser.getQName(whitespaceRule.getNamespace(), null, whitespaceRule.getElement());
        } else {
          qName = parser.getQName(whitespaceRule.getElement());
        } 
        int i1 = xSLTC.registerElement(qName);
        instructionList.append(new ILOAD(k));
        instructionList.append(new PUSH(constantPoolGen, i1));
        if (whitespaceRule.getAction() == 1) {
          arrayOfBranchHandle1[b1++] = instructionList.append(new IF_ICMPEQ(null));
        } else {
          arrayOfBranchHandle2[b2++] = instructionList.append(new IF_ICMPEQ(null));
        } 
      } 
    } 
    if (paramInt == 1) {
      compileStripSpace(arrayOfBranchHandle1, b1, instructionList);
      compilePreserveSpace(arrayOfBranchHandle2, b2, instructionList);
    } else {
      compilePreserveSpace(arrayOfBranchHandle2, b2, instructionList);
      compileStripSpace(arrayOfBranchHandle1, b1, instructionList);
    } 
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  private static void compileDefault(int paramInt, ClassGenerator paramClassGenerator) {
    ConstantPoolGen constantPoolGen = paramClassGenerator.getConstantPool();
    InstructionList instructionList = new InstructionList();
    XSLTC xSLTC = paramClassGenerator.getParser().getXSLTC();
    MethodGenerator methodGenerator = new MethodGenerator(17, Type.BOOLEAN, new Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Type.INT, Type.INT }, new String[] { "dom", "node", "type" }, "stripSpace", paramClassGenerator.getClassName(), instructionList, constantPoolGen);
    paramClassGenerator.addInterface("com/sun/org/apache/xalan/internal/xsltc/StripFilter");
    if (paramInt == 1) {
      instructionList.append(ICONST_1);
    } else {
      instructionList.append(ICONST_0);
    } 
    instructionList.append(IRETURN);
    paramClassGenerator.addMethod(methodGenerator);
  }
  
  public static int translateRules(Vector paramVector, ClassGenerator paramClassGenerator) {
    int i = prioritizeRules(paramVector);
    if (paramVector.size() == 0) {
      compileDefault(i, paramClassGenerator);
      return i;
    } 
    compilePredicate(paramVector, i, paramClassGenerator);
    return 0;
  }
  
  private static void quicksort(Vector paramVector, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      int i = partition(paramVector, paramInt1, paramInt2);
      quicksort(paramVector, paramInt1, i);
      paramInt1 = i + 1;
    } 
  }
  
  private static int partition(Vector paramVector, int paramInt1, int paramInt2) {
    WhitespaceRule whitespaceRule = (WhitespaceRule)paramVector.elementAt(paramInt1 + paramInt2 >>> 1);
    int i = paramInt1 - 1;
    int j = paramInt2 + 1;
    while (true) {
      if (whitespaceRule.compareTo((WhitespaceRule)paramVector.elementAt(--j)) < 0)
        continue; 
      while (whitespaceRule.compareTo((WhitespaceRule)paramVector.elementAt(++i)) > 0);
      if (i < j) {
        WhitespaceRule whitespaceRule1 = (WhitespaceRule)paramVector.elementAt(i);
        paramVector.setElementAt(paramVector.elementAt(j), i);
        paramVector.setElementAt(whitespaceRule1, j);
        continue;
      } 
      break;
    } 
    return j;
  }
  
  public Type typeCheck(SymbolTable paramSymbolTable) throws TypeCheckError { return Type.Void; }
  
  public void translate(ClassGenerator paramClassGenerator, MethodGenerator paramMethodGenerator) {}
  
  private static final class WhitespaceRule {
    private final int _action;
    
    private String _namespace;
    
    private String _element;
    
    private int _type;
    
    private int _priority;
    
    public WhitespaceRule(int param1Int1, String param1String, int param1Int2) {
      this._action = param1Int1;
      int i = param1String.lastIndexOf(':');
      if (i >= 0) {
        this._namespace = param1String.substring(0, i);
        this._element = param1String.substring(i + 1, param1String.length());
      } else {
        this._namespace = "";
        this._element = param1String;
      } 
      this._priority = param1Int2 << 2;
      if (this._element.equals("*")) {
        if (this._namespace == "") {
          this._type = 3;
          this._priority += 2;
        } else {
          this._type = 2;
          this._priority++;
        } 
      } else {
        this._type = 1;
      } 
    }
    
    public int compareTo(WhitespaceRule param1WhitespaceRule) { return (this._priority < param1WhitespaceRule._priority) ? -1 : ((this._priority > param1WhitespaceRule._priority) ? 1 : 0); }
    
    public int getAction() { return this._action; }
    
    public int getStrength() { return this._type; }
    
    public int getPriority() { return this._priority; }
    
    public String getElement() { return this._element; }
    
    public String getNamespace() { return this._namespace; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\compiler\Whitespace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
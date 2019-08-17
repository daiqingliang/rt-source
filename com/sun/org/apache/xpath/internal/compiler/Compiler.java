package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.SAXSourceLocator;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.axes.UnionPathIterator;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunction;
import com.sun.org.apache.xpath.internal.functions.FuncExtFunctionAvailable;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.functions.WrongNumberArgsException;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.org.apache.xpath.internal.operations.Div;
import com.sun.org.apache.xpath.internal.operations.Equals;
import com.sun.org.apache.xpath.internal.operations.Gt;
import com.sun.org.apache.xpath.internal.operations.Gte;
import com.sun.org.apache.xpath.internal.operations.Lt;
import com.sun.org.apache.xpath.internal.operations.Lte;
import com.sun.org.apache.xpath.internal.operations.Minus;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.sun.org.apache.xpath.internal.operations.Mult;
import com.sun.org.apache.xpath.internal.operations.Neg;
import com.sun.org.apache.xpath.internal.operations.NotEquals;
import com.sun.org.apache.xpath.internal.operations.Number;
import com.sun.org.apache.xpath.internal.operations.Operation;
import com.sun.org.apache.xpath.internal.operations.Or;
import com.sun.org.apache.xpath.internal.operations.Plus;
import com.sun.org.apache.xpath.internal.operations.String;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import com.sun.org.apache.xpath.internal.operations.Variable;
import com.sun.org.apache.xpath.internal.patterns.FunctionPattern;
import com.sun.org.apache.xpath.internal.patterns.StepPattern;
import com.sun.org.apache.xpath.internal.patterns.UnionPattern;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public class Compiler extends OpMap {
  private int locPathDepth = -1;
  
  private static final boolean DEBUG = false;
  
  private static long s_nextMethodId = 0L;
  
  private PrefixResolver m_currentPrefixResolver = null;
  
  ErrorListener m_errorHandler;
  
  SourceLocator m_locator;
  
  private FunctionTable m_functionTable;
  
  public Compiler(ErrorListener paramErrorListener, SourceLocator paramSourceLocator, FunctionTable paramFunctionTable) {
    this.m_errorHandler = paramErrorListener;
    this.m_locator = paramSourceLocator;
    this.m_functionTable = paramFunctionTable;
  }
  
  public Compiler() {
    this.m_errorHandler = null;
    this.m_locator = null;
  }
  
  public Expression compile(int paramInt) throws TransformerException {
    int i = getOp(paramInt);
    null = null;
    switch (i) {
      case 1:
        return compile(paramInt + 2);
      case 2:
        return or(paramInt);
      case 3:
        return and(paramInt);
      case 4:
        return notequals(paramInt);
      case 5:
        return equals(paramInt);
      case 6:
        return lte(paramInt);
      case 7:
        return lt(paramInt);
      case 8:
        return gte(paramInt);
      case 9:
        return gt(paramInt);
      case 10:
        return plus(paramInt);
      case 11:
        return minus(paramInt);
      case 12:
        return mult(paramInt);
      case 13:
        return div(paramInt);
      case 14:
        return mod(paramInt);
      case 16:
        return neg(paramInt);
      case 17:
        return string(paramInt);
      case 18:
        return bool(paramInt);
      case 19:
        return number(paramInt);
      case 20:
        return union(paramInt);
      case 21:
        return literal(paramInt);
      case 22:
        return variable(paramInt);
      case 23:
        return group(paramInt);
      case 27:
        return numberlit(paramInt);
      case 26:
        return arg(paramInt);
      case 24:
        return compileExtension(paramInt);
      case 25:
        return compileFunction(paramInt);
      case 28:
        return locationPath(paramInt);
      case 29:
        return null;
      case 30:
        return matchPattern(paramInt + 2);
      case 31:
        return locationPathPattern(paramInt);
      case 15:
        error("ER_UNKNOWN_OPCODE", new Object[] { "quo" });
        return SYNTHETIC_LOCAL_VARIABLE_3;
    } 
    error("ER_UNKNOWN_OPCODE", new Object[] { Integer.toString(getOp(paramInt)) });
    return SYNTHETIC_LOCAL_VARIABLE_3;
  }
  
  private Expression compileOperation(Operation paramOperation, int paramInt) throws TransformerException {
    int i = getFirstChildPos(paramInt);
    int j = getNextOpPos(i);
    paramOperation.setLeftRight(compile(i), compile(j));
    return paramOperation;
  }
  
  private Expression compileUnary(UnaryOperation paramUnaryOperation, int paramInt) throws TransformerException {
    int i = getFirstChildPos(paramInt);
    paramUnaryOperation.setRight(compile(i));
    return paramUnaryOperation;
  }
  
  protected Expression or(int paramInt) throws TransformerException { return compileOperation(new Or(), paramInt); }
  
  protected Expression and(int paramInt) throws TransformerException { return compileOperation(new And(), paramInt); }
  
  protected Expression notequals(int paramInt) throws TransformerException { return compileOperation(new NotEquals(), paramInt); }
  
  protected Expression equals(int paramInt) throws TransformerException { return compileOperation(new Equals(), paramInt); }
  
  protected Expression lte(int paramInt) throws TransformerException { return compileOperation(new Lte(), paramInt); }
  
  protected Expression lt(int paramInt) throws TransformerException { return compileOperation(new Lt(), paramInt); }
  
  protected Expression gte(int paramInt) throws TransformerException { return compileOperation(new Gte(), paramInt); }
  
  protected Expression gt(int paramInt) throws TransformerException { return compileOperation(new Gt(), paramInt); }
  
  protected Expression plus(int paramInt) throws TransformerException { return compileOperation(new Plus(), paramInt); }
  
  protected Expression minus(int paramInt) throws TransformerException { return compileOperation(new Minus(), paramInt); }
  
  protected Expression mult(int paramInt) throws TransformerException { return compileOperation(new Mult(), paramInt); }
  
  protected Expression div(int paramInt) throws TransformerException { return compileOperation(new Div(), paramInt); }
  
  protected Expression mod(int paramInt) throws TransformerException { return compileOperation(new Mod(), paramInt); }
  
  protected Expression neg(int paramInt) throws TransformerException { return compileUnary(new Neg(), paramInt); }
  
  protected Expression string(int paramInt) throws TransformerException { return compileUnary(new String(), paramInt); }
  
  protected Expression bool(int paramInt) throws TransformerException { return compileUnary(new Bool(), paramInt); }
  
  protected Expression number(int paramInt) throws TransformerException { return compileUnary(new Number(), paramInt); }
  
  protected Expression literal(int paramInt) throws TransformerException {
    paramInt = getFirstChildPos(paramInt);
    return (XString)getTokenQueue().elementAt(getOp(paramInt));
  }
  
  protected Expression numberlit(int paramInt) throws TransformerException {
    paramInt = getFirstChildPos(paramInt);
    return (XNumber)getTokenQueue().elementAt(getOp(paramInt));
  }
  
  protected Expression variable(int paramInt) throws TransformerException {
    Variable variable = new Variable();
    paramInt = getFirstChildPos(paramInt);
    int i = getOp(paramInt);
    String str1 = (-2 == i) ? null : (String)getTokenQueue().elementAt(i);
    String str2 = (String)getTokenQueue().elementAt(getOp(paramInt + 1));
    QName qName = new QName(str1, str2);
    variable.setQName(qName);
    return variable;
  }
  
  protected Expression group(int paramInt) throws TransformerException { return compile(paramInt + 2); }
  
  protected Expression arg(int paramInt) throws TransformerException { return compile(paramInt + 2); }
  
  protected Expression union(int paramInt) throws TransformerException {
    this.locPathDepth++;
    try {
      return UnionPathIterator.createUnionIterator(this, paramInt);
    } finally {
      this.locPathDepth--;
    } 
  }
  
  public int getLocationPathDepth() { return this.locPathDepth; }
  
  FunctionTable getFunctionTable() { return this.m_functionTable; }
  
  public Expression locationPath(int paramInt) throws TransformerException {
    this.locPathDepth++;
    try {
      DTMIterator dTMIterator = WalkerFactory.newDTMIterator(this, paramInt, (this.locPathDepth == 0));
      return (Expression)dTMIterator;
    } finally {
      this.locPathDepth--;
    } 
  }
  
  public Expression predicate(int paramInt) throws TransformerException { return compile(paramInt + 2); }
  
  protected Expression matchPattern(int paramInt) throws TransformerException {
    this.locPathDepth++;
    try {
      int i = paramInt;
      byte b;
      for (b = 0; getOp(i) == 31; b++)
        i = getNextOpPos(i); 
      if (b == 1)
        return compile(paramInt); 
      UnionPattern unionPattern = new UnionPattern();
      StepPattern[] arrayOfStepPattern = new StepPattern[b];
      for (b = 0; getOp(paramInt) == 31; b++) {
        i = getNextOpPos(paramInt);
        arrayOfStepPattern[b] = (StepPattern)compile(paramInt);
        paramInt = i;
      } 
      unionPattern.setPatterns(arrayOfStepPattern);
      return unionPattern;
    } finally {
      this.locPathDepth--;
    } 
  }
  
  public Expression locationPathPattern(int paramInt) throws TransformerException {
    paramInt = getFirstChildPos(paramInt);
    return stepPattern(paramInt, 0, null);
  }
  
  public int getWhatToShow(int paramInt) {
    int i = getOp(paramInt);
    int j = getOp(paramInt + 3);
    switch (j) {
      case 1030:
        return 128;
      case 1031:
        return 12;
      case 1032:
        return 64;
      case 1033:
        switch (i) {
          case 49:
            return 4096;
          case 39:
          case 51:
            return 2;
          case 38:
          case 42:
          case 48:
            return -1;
        } 
        return (getOp(0) == 30) ? -1283 : -3;
      case 35:
        return 1280;
      case 1034:
        return 65536;
      case 34:
        switch (i) {
          case 49:
            return 4096;
          case 39:
          case 51:
            return 2;
          case 52:
          case 53:
            return 1;
        } 
        return 1;
    } 
    return -1;
  }
  
  protected StepPattern stepPattern(int paramInt1, int paramInt2, StepPattern paramStepPattern) throws TransformerException {
    int n;
    int m;
    StepPattern stepPattern1;
    FunctionPattern functionPattern;
    int i = paramInt1;
    int j = getOp(paramInt1);
    if (-1 == j)
      return null; 
    boolean bool = true;
    int k = getNextOpPos(paramInt1);
    switch (j) {
      case 25:
        bool = false;
        m = getOp(paramInt1 + 1);
        functionPattern = new FunctionPattern(compileFunction(paramInt1), 10, 3);
        break;
      case 50:
        bool = false;
        m = getArgLengthOfStep(paramInt1);
        paramInt1 = getFirstChildPosOfStep(paramInt1);
        stepPattern1 = new StepPattern(1280, 10, 3);
        break;
      case 51:
        m = getArgLengthOfStep(paramInt1);
        paramInt1 = getFirstChildPosOfStep(paramInt1);
        stepPattern1 = new StepPattern(2, getStepNS(i), getStepLocalName(i), 10, 2);
        break;
      case 52:
        m = getArgLengthOfStep(paramInt1);
        paramInt1 = getFirstChildPosOfStep(paramInt1);
        n = getWhatToShow(i);
        if (1280 == n)
          bool = false; 
        stepPattern1 = new StepPattern(getWhatToShow(i), getStepNS(i), getStepLocalName(i), 0, 3);
        break;
      case 53:
        m = getArgLengthOfStep(paramInt1);
        paramInt1 = getFirstChildPosOfStep(paramInt1);
        stepPattern1 = new StepPattern(getWhatToShow(i), getStepNS(i), getStepLocalName(i), 10, 3);
        break;
      default:
        error("ER_UNKNOWN_MATCH_OPERATION", null);
        return null;
    } 
    stepPattern1.setPredicates(getCompiledPredicates(paramInt1 + m));
    if (null != paramStepPattern)
      stepPattern1.setRelativePathPattern(paramStepPattern); 
    StepPattern stepPattern2 = stepPattern(k, paramInt2 + 1, stepPattern1);
    return (null != stepPattern2) ? stepPattern2 : stepPattern1;
  }
  
  public Expression[] getCompiledPredicates(int paramInt) throws TransformerException {
    int i = countPredicates(paramInt);
    if (i > 0) {
      Expression[] arrayOfExpression = new Expression[i];
      compilePredicates(paramInt, arrayOfExpression);
      return arrayOfExpression;
    } 
    return null;
  }
  
  public int countPredicates(int paramInt) {
    byte b = 0;
    while (29 == getOp(paramInt)) {
      b++;
      paramInt = getNextOpPos(paramInt);
    } 
    return b;
  }
  
  private void compilePredicates(int paramInt, Expression[] paramArrayOfExpression) throws TransformerException {
    for (byte b = 0; 29 == getOp(paramInt); b++) {
      paramArrayOfExpression[b] = predicate(paramInt);
      paramInt = getNextOpPos(paramInt);
    } 
  }
  
  Expression compileFunction(int paramInt) throws TransformerException {
    int i = paramInt + getOp(paramInt + 1) - 1;
    paramInt = getFirstChildPos(paramInt);
    int j = getOp(paramInt);
    paramInt++;
    if (-1 != j) {
      Function function = this.m_functionTable.getFunction(j);
      if (function instanceof FuncExtFunctionAvailable)
        ((FuncExtFunctionAvailable)function).setFunctionTable(this.m_functionTable); 
      function.postCompileStep(this);
      try {
        byte b = 0;
        int k = paramInt;
        while (k < i) {
          function.setArg(compile(k), b);
          k = getNextOpPos(k);
          b++;
        } 
        function.checkNumberArgs(b);
      } catch (WrongNumberArgsException wrongNumberArgsException) {
        String str = this.m_functionTable.getFunctionName(j);
        this.m_errorHandler.fatalError(new TransformerException(XSLMessages.createXPATHMessage("ER_ONLY_ALLOWS", new Object[] { str, wrongNumberArgsException.getMessage() }), this.m_locator));
      } 
      return function;
    } 
    error("ER_FUNCTION_TOKEN_NOT_FOUND", null);
    return null;
  }
  
  private long getNextMethodId() {
    if (s_nextMethodId == Float.MAX_VALUE)
      s_nextMethodId = 0L; 
    return s_nextMethodId++;
  }
  
  private Expression compileExtension(int paramInt) throws TransformerException {
    int i = paramInt + getOp(paramInt + 1) - 1;
    paramInt = getFirstChildPos(paramInt);
    String str1 = (String)getTokenQueue().elementAt(getOp(paramInt));
    String str2 = (String)getTokenQueue().elementAt(getOp(++paramInt));
    paramInt++;
    FuncExtFunction funcExtFunction = new FuncExtFunction(str1, str2, String.valueOf(getNextMethodId()));
    try {
      for (byte b = 0; paramInt < i; b++) {
        int j = getNextOpPos(paramInt);
        funcExtFunction.setArg(compile(paramInt), b);
        paramInt = j;
      } 
    } catch (WrongNumberArgsException wrongNumberArgsException) {}
    return funcExtFunction;
  }
  
  public void warn(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    if (null != this.m_errorHandler) {
      this.m_errorHandler.warning(new TransformerException(str, this.m_locator));
    } else {
      System.out.println(str + "; file " + this.m_locator.getSystemId() + "; line " + this.m_locator.getLineNumber() + "; column " + this.m_locator.getColumnNumber());
    } 
  }
  
  public void assertion(boolean paramBoolean, String paramString) {
    if (!paramBoolean) {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    } 
  }
  
  public void error(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    if (null != this.m_errorHandler) {
      this.m_errorHandler.fatalError(new TransformerException(str, this.m_locator));
    } else {
      throw new TransformerException(str, (SAXSourceLocator)this.m_locator);
    } 
  }
  
  public PrefixResolver getNamespaceContext() { return this.m_currentPrefixResolver; }
  
  public void setNamespaceContext(PrefixResolver paramPrefixResolver) { this.m_currentPrefixResolver = paramPrefixResolver; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\Compiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
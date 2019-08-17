package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.XPathProcessorException;
import com.sun.org.apache.xpath.internal.domapi.XPathStylesheetDOM3Exception;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XString;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

public class XPathParser {
  public static final String CONTINUE_AFTER_FATAL_ERROR = "CONTINUE_AFTER_FATAL_ERROR";
  
  private OpMap m_ops;
  
  String m_token;
  
  char m_tokenChar = Character.MIN_VALUE;
  
  int m_queueMark = 0;
  
  protected static final int FILTER_MATCH_FAILED = 0;
  
  protected static final int FILTER_MATCH_PRIMARY = 1;
  
  protected static final int FILTER_MATCH_PREDICATES = 2;
  
  PrefixResolver m_namespaceContext;
  
  private ErrorListener m_errorListener;
  
  SourceLocator m_sourceLocator;
  
  private FunctionTable m_functionTable;
  
  public XPathParser(ErrorListener paramErrorListener, SourceLocator paramSourceLocator) {
    this.m_errorListener = paramErrorListener;
    this.m_sourceLocator = paramSourceLocator;
  }
  
  public void initXPath(Compiler paramCompiler, String paramString, PrefixResolver paramPrefixResolver) throws TransformerException {
    this.m_ops = paramCompiler;
    this.m_namespaceContext = paramPrefixResolver;
    this.m_functionTable = paramCompiler.getFunctionTable();
    Lexer lexer = new Lexer(paramCompiler, paramPrefixResolver, this);
    lexer.tokenize(paramString);
    this.m_ops.setOp(0, 1);
    this.m_ops.setOp(1, 2);
    try {
      nextToken();
      Expr();
      if (null != this.m_token) {
        String str = "";
        while (null != this.m_token) {
          str = str + "'" + this.m_token + "'";
          nextToken();
          if (null != this.m_token)
            str = str + ", "; 
        } 
        error("ER_EXTRA_ILLEGAL_TOKENS", new Object[] { str });
      } 
    } catch (XPathProcessorException xPathProcessorException) {
      if ("CONTINUE_AFTER_FATAL_ERROR".equals(xPathProcessorException.getMessage())) {
        initXPath(paramCompiler, "/..", paramPrefixResolver);
      } else {
        throw xPathProcessorException;
      } 
    } 
    paramCompiler.shrink();
  }
  
  public void initMatchPattern(Compiler paramCompiler, String paramString, PrefixResolver paramPrefixResolver) throws TransformerException {
    this.m_ops = paramCompiler;
    this.m_namespaceContext = paramPrefixResolver;
    this.m_functionTable = paramCompiler.getFunctionTable();
    Lexer lexer = new Lexer(paramCompiler, paramPrefixResolver, this);
    lexer.tokenize(paramString);
    this.m_ops.setOp(0, 30);
    this.m_ops.setOp(1, 2);
    nextToken();
    Pattern();
    if (null != this.m_token) {
      String str = "";
      while (null != this.m_token) {
        str = str + "'" + this.m_token + "'";
        nextToken();
        if (null != this.m_token)
          str = str + ", "; 
      } 
      error("ER_EXTRA_ILLEGAL_TOKENS", new Object[] { str });
    } 
    this.m_ops.setOp(this.m_ops.getOp(1), -1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    this.m_ops.shrink();
  }
  
  public void setErrorHandler(ErrorListener paramErrorListener) { this.m_errorListener = paramErrorListener; }
  
  public ErrorListener getErrorListener() { return this.m_errorListener; }
  
  final boolean tokenIs(String paramString) { return (this.m_token != null) ? this.m_token.equals(paramString) : ((paramString == null) ? 1 : 0); }
  
  final boolean tokenIs(char paramChar) { return (this.m_token != null) ? ((this.m_tokenChar == paramChar)) : false; }
  
  final boolean lookahead(char paramChar, int paramInt) {
    boolean bool;
    int i = this.m_queueMark + paramInt;
    if (i <= this.m_ops.getTokenQueueSize() && i > 0 && this.m_ops.getTokenQueueSize() != 0) {
      String str = (String)this.m_ops.m_tokenQueue.elementAt(i - 1);
      bool = (str.length() == 1) ? ((str.charAt(0) == paramChar)) : false;
    } else {
      bool = false;
    } 
    return bool;
  }
  
  private final boolean lookbehind(char paramChar, int paramInt) {
    boolean bool;
    int i = this.m_queueMark - paramInt + 1;
    if (i >= 0) {
      String str = (String)this.m_ops.m_tokenQueue.elementAt(i);
      if (str.length() == 1) {
        byte b = (str == null) ? 124 : str.charAt(0);
        bool = (b == 124) ? false : ((b == paramChar));
      } else {
        bool = false;
      } 
    } else {
      bool = false;
    } 
    return bool;
  }
  
  private final boolean lookbehindHasToken(int paramInt) {
    boolean bool;
    if (this.m_queueMark - paramInt > 0) {
      String str = (String)this.m_ops.m_tokenQueue.elementAt(this.m_queueMark - paramInt - 1);
      byte b = (str == null) ? 124 : str.charAt(0);
      bool = !(b == 124);
    } else {
      bool = false;
    } 
    return bool;
  }
  
  private final boolean lookahead(String paramString, int paramInt) {
    boolean bool;
    if (this.m_queueMark + paramInt <= this.m_ops.getTokenQueueSize()) {
      String str = (String)this.m_ops.m_tokenQueue.elementAt(this.m_queueMark + paramInt - 1);
      bool = (str != null) ? str.equals(paramString) : ((paramString == null) ? 1 : 0);
    } else {
      bool = (null == paramString);
    } 
    return bool;
  }
  
  private final void nextToken() {
    if (this.m_queueMark < this.m_ops.getTokenQueueSize()) {
      this.m_token = (String)this.m_ops.m_tokenQueue.elementAt(this.m_queueMark++);
      this.m_tokenChar = this.m_token.charAt(0);
    } else {
      this.m_token = null;
      this.m_tokenChar = Character.MIN_VALUE;
    } 
  }
  
  private final String getTokenRelative(int paramInt) {
    String str;
    int i = this.m_queueMark + paramInt;
    if (i > 0 && i < this.m_ops.getTokenQueueSize()) {
      str = (String)this.m_ops.m_tokenQueue.elementAt(i);
    } else {
      str = null;
    } 
    return str;
  }
  
  private final void prevToken() {
    if (this.m_queueMark > 0) {
      this.m_queueMark--;
      this.m_token = (String)this.m_ops.m_tokenQueue.elementAt(this.m_queueMark);
      this.m_tokenChar = this.m_token.charAt(0);
    } else {
      this.m_token = null;
      this.m_tokenChar = Character.MIN_VALUE;
    } 
  }
  
  private final void consumeExpected(String paramString) throws TransformerException {
    if (tokenIs(paramString)) {
      nextToken();
    } else {
      error("ER_EXPECTED_BUT_FOUND", new Object[] { paramString, this.m_token });
      throw new XPathProcessorException("CONTINUE_AFTER_FATAL_ERROR");
    } 
  }
  
  private final void consumeExpected(char paramChar) throws TransformerException {
    if (tokenIs(paramChar)) {
      nextToken();
    } else {
      error("ER_EXPECTED_BUT_FOUND", new Object[] { String.valueOf(paramChar), this.m_token });
      throw new XPathProcessorException("CONTINUE_AFTER_FATAL_ERROR");
    } 
  }
  
  void warn(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    ErrorListener errorListener = getErrorListener();
    if (null != errorListener) {
      errorListener.warning(new TransformerException(str, this.m_sourceLocator));
    } else {
      System.err.println(str);
    } 
  }
  
  private void assertion(boolean paramBoolean, String paramString) {
    if (!paramBoolean) {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    } 
  }
  
  void error(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    ErrorListener errorListener = getErrorListener();
    TransformerException transformerException = new TransformerException(str, this.m_sourceLocator);
    if (null != errorListener) {
      errorListener.fatalError(transformerException);
    } else {
      throw transformerException;
    } 
  }
  
  void errorForDOM3(String paramString, Object[] paramArrayOfObject) throws TransformerException {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    ErrorListener errorListener = getErrorListener();
    XPathStylesheetDOM3Exception xPathStylesheetDOM3Exception = new XPathStylesheetDOM3Exception(str, this.m_sourceLocator);
    if (null != errorListener) {
      errorListener.fatalError(xPathStylesheetDOM3Exception);
    } else {
      throw xPathStylesheetDOM3Exception;
    } 
  }
  
  protected String dumpRemainingTokenQueue() {
    String str;
    int i = this.m_queueMark;
    if (i < this.m_ops.getTokenQueueSize()) {
      String str1;
      for (str1 = "\n Remaining tokens: ("; i < this.m_ops.getTokenQueueSize(); str1 = str1 + " '" + str2 + "'")
        String str2 = (String)this.m_ops.m_tokenQueue.elementAt(i++); 
      str = str1 + ")";
    } else {
      str = "";
    } 
    return str;
  }
  
  final int getFunctionToken(String paramString) {
    byte b;
    try {
      Object object = Keywords.lookupNodeTest(paramString);
      if (null == object)
        object = this.m_functionTable.getFunctionID(paramString); 
      b = ((Integer)object).intValue();
    } catch (NullPointerException nullPointerException) {
      b = -1;
    } catch (ClassCastException classCastException) {
      b = -1;
    } 
    return b;
  }
  
  void insertOp(int paramInt1, int paramInt2, int paramInt3) {
    int i = this.m_ops.getOp(1);
    for (int j = i - 1; j >= paramInt1; j--)
      this.m_ops.setOp(j + paramInt2, this.m_ops.getOp(j)); 
    this.m_ops.setOp(paramInt1, paramInt3);
    this.m_ops.setOp(1, i + paramInt2);
  }
  
  void appendOp(int paramInt1, int paramInt2) {
    int i = this.m_ops.getOp(1);
    this.m_ops.setOp(i, paramInt2);
    this.m_ops.setOp(i + 1, paramInt1);
    this.m_ops.setOp(1, i + paramInt1);
  }
  
  protected void Expr() { OrExpr(); }
  
  protected void OrExpr() {
    int i = this.m_ops.getOp(1);
    AndExpr();
    if (null != this.m_token && tokenIs("or")) {
      nextToken();
      insertOp(i, 2, 2);
      OrExpr();
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
    } 
  }
  
  protected void AndExpr() {
    int i = this.m_ops.getOp(1);
    EqualityExpr(-1);
    if (null != this.m_token && tokenIs("and")) {
      nextToken();
      insertOp(i, 2, 3);
      AndExpr();
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
    } 
  }
  
  protected int EqualityExpr(int paramInt) throws TransformerException {
    int i = this.m_ops.getOp(1);
    if (-1 == paramInt)
      paramInt = i; 
    RelationalExpr(-1);
    if (null != this.m_token)
      if (tokenIs('!') && lookahead('=', 1)) {
        nextToken();
        nextToken();
        insertOp(paramInt, 2, 4);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = EqualityExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs('=')) {
        nextToken();
        insertOp(paramInt, 2, 5);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = EqualityExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }  
    return paramInt;
  }
  
  protected int RelationalExpr(int paramInt) throws TransformerException {
    int i = this.m_ops.getOp(1);
    if (-1 == paramInt)
      paramInt = i; 
    AdditiveExpr(-1);
    if (null != this.m_token)
      if (tokenIs('<')) {
        nextToken();
        if (tokenIs('=')) {
          nextToken();
          insertOp(paramInt, 2, 6);
        } else {
          insertOp(paramInt, 2, 7);
        } 
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = RelationalExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs('>')) {
        nextToken();
        if (tokenIs('=')) {
          nextToken();
          insertOp(paramInt, 2, 8);
        } else {
          insertOp(paramInt, 2, 9);
        } 
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = RelationalExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }  
    return paramInt;
  }
  
  protected int AdditiveExpr(int paramInt) throws TransformerException {
    int i = this.m_ops.getOp(1);
    if (-1 == paramInt)
      paramInt = i; 
    MultiplicativeExpr(-1);
    if (null != this.m_token)
      if (tokenIs('+')) {
        nextToken();
        insertOp(paramInt, 2, 10);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = AdditiveExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs('-')) {
        nextToken();
        insertOp(paramInt, 2, 11);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = AdditiveExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }  
    return paramInt;
  }
  
  protected int MultiplicativeExpr(int paramInt) throws TransformerException {
    int i = this.m_ops.getOp(1);
    if (-1 == paramInt)
      paramInt = i; 
    UnaryExpr();
    if (null != this.m_token)
      if (tokenIs('*')) {
        nextToken();
        insertOp(paramInt, 2, 12);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs("div")) {
        nextToken();
        insertOp(paramInt, 2, 13);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs("mod")) {
        nextToken();
        insertOp(paramInt, 2, 14);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      } else if (tokenIs("quo")) {
        nextToken();
        insertOp(paramInt, 2, 15);
        int j = this.m_ops.getOp(1) - paramInt;
        paramInt = MultiplicativeExpr(paramInt);
        this.m_ops.setOp(paramInt + 1, this.m_ops.getOp(paramInt + j + 1) + j);
        paramInt += 2;
      }  
    return paramInt;
  }
  
  protected void UnaryExpr() {
    int i = this.m_ops.getOp(1);
    boolean bool = false;
    if (this.m_tokenChar == '-') {
      nextToken();
      appendOp(2, 16);
      bool = true;
    } 
    UnionExpr();
    if (bool)
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i); 
  }
  
  protected void StringExpr() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 17);
    Expr();
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected void BooleanExpr() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 18);
    Expr();
    int j = this.m_ops.getOp(1) - i;
    if (j == 2)
      error("ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL", null); 
    this.m_ops.setOp(i + 1, j);
  }
  
  protected void NumberExpr() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 19);
    Expr();
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected void UnionExpr() {
    int i = this.m_ops.getOp(1);
    boolean bool1 = true;
    boolean bool2 = false;
    while (true) {
      PathExpr();
      if (tokenIs('|')) {
        if (false == bool2) {
          bool2 = true;
          insertOp(i, 2, 20);
        } 
        nextToken();
        if (!bool1)
          break; 
        continue;
      } 
      break;
    } 
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected void PathExpr() {
    int i = this.m_ops.getOp(1);
    int j = FilterExpr();
    if (j != 0) {
      boolean bool = (j == 2) ? 1 : 0;
      if (tokenIs('/')) {
        nextToken();
        if (!bool) {
          insertOp(i, 2, 28);
          bool = true;
        } 
        if (!RelativeLocationPath())
          error("ER_EXPECTED_REL_LOC_PATH", null); 
      } 
      if (bool) {
        this.m_ops.setOp(this.m_ops.getOp(1), -1);
        this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
        this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      } 
    } else {
      LocationPath();
    } 
  }
  
  protected int FilterExpr() throws TransformerException {
    byte b;
    int i = this.m_ops.getOp(1);
    if (PrimaryExpr()) {
      if (tokenIs('[')) {
        insertOp(i, 2, 28);
        while (tokenIs('['))
          Predicate(); 
        b = 2;
      } else {
        b = 1;
      } 
    } else {
      b = 0;
    } 
    return b;
  }
  
  protected boolean PrimaryExpr() throws TransformerException {
    boolean bool;
    int i = this.m_ops.getOp(1);
    if (this.m_tokenChar == '\'' || this.m_tokenChar == '"') {
      appendOp(2, 21);
      Literal();
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      bool = true;
    } else if (this.m_tokenChar == '$') {
      nextToken();
      appendOp(2, 22);
      QName();
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      bool = true;
    } else if (this.m_tokenChar == '(') {
      nextToken();
      appendOp(2, 23);
      Expr();
      consumeExpected(')');
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      bool = true;
    } else if (null != this.m_token && (('.' == this.m_tokenChar && this.m_token.length() > 1 && Character.isDigit(this.m_token.charAt(1))) || Character.isDigit(this.m_tokenChar))) {
      appendOp(2, 27);
      Number();
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      bool = true;
    } else if (lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3))) {
      bool = FunctionCall();
    } else {
      bool = false;
    } 
    return bool;
  }
  
  protected void Argument() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 26);
    Expr();
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected boolean FunctionCall() throws TransformerException {
    int i = this.m_ops.getOp(1);
    if (lookahead(':', 1)) {
      appendOp(4, 24);
      this.m_ops.setOp(i + 1 + 1, this.m_queueMark - 1);
      nextToken();
      consumeExpected(':');
      this.m_ops.setOp(i + 1 + 2, this.m_queueMark - 1);
      nextToken();
    } else {
      int j = getFunctionToken(this.m_token);
      if (-1 == j)
        error("ER_COULDNOT_FIND_FUNCTION", new Object[] { this.m_token }); 
      switch (j) {
        case 1030:
        case 1031:
        case 1032:
        case 1033:
          return false;
      } 
      appendOp(3, 25);
      this.m_ops.setOp(i + 1 + 1, j);
      nextToken();
    } 
    consumeExpected('(');
    while (!tokenIs(')') && this.m_token != null) {
      if (tokenIs(','))
        error("ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG", null); 
      Argument();
      if (!tokenIs(')')) {
        consumeExpected(',');
        if (tokenIs(')'))
          error("ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG", null); 
      } 
    } 
    consumeExpected(')');
    this.m_ops.setOp(this.m_ops.getOp(1), -1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
    return true;
  }
  
  protected void LocationPath() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 28);
    boolean bool = tokenIs('/');
    if (bool) {
      appendOp(4, 50);
      this.m_ops.setOp(this.m_ops.getOp(1) - 2, 4);
      this.m_ops.setOp(this.m_ops.getOp(1) - 1, 35);
      nextToken();
    } else if (this.m_token == null) {
      error("ER_EXPECTED_LOC_PATH_AT_END_EXPR", null);
    } 
    if (this.m_token != null && !RelativeLocationPath() && !bool)
      error("ER_EXPECTED_LOC_PATH", new Object[] { this.m_token }); 
    this.m_ops.setOp(this.m_ops.getOp(1), -1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected boolean RelativeLocationPath() throws TransformerException {
    if (!Step())
      return false; 
    while (tokenIs('/')) {
      nextToken();
      if (!Step())
        error("ER_EXPECTED_LOC_STEP", null); 
    } 
    return true;
  }
  
  protected boolean Step() throws TransformerException {
    int i = this.m_ops.getOp(1);
    boolean bool = tokenIs('/');
    if (bool) {
      nextToken();
      appendOp(2, 42);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      this.m_ops.setOp(this.m_ops.getOp(1), 1033);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      this.m_ops.setOp(i + 1 + 1, this.m_ops.getOp(1) - i);
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
      i = this.m_ops.getOp(1);
    } 
    if (tokenIs(".")) {
      nextToken();
      if (tokenIs('['))
        error("ER_PREDICATE_ILLEGAL_SYNTAX", null); 
      appendOp(4, 48);
      this.m_ops.setOp(this.m_ops.getOp(1) - 2, 4);
      this.m_ops.setOp(this.m_ops.getOp(1) - 1, 1033);
    } else if (tokenIs("..")) {
      nextToken();
      appendOp(4, 45);
      this.m_ops.setOp(this.m_ops.getOp(1) - 2, 4);
      this.m_ops.setOp(this.m_ops.getOp(1) - 1, 1033);
    } else if (tokenIs('*') || tokenIs('@') || tokenIs('_') || (this.m_token != null && Character.isLetter(this.m_token.charAt(0)))) {
      Basis();
      while (tokenIs('['))
        Predicate(); 
      this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
    } else {
      if (bool)
        error("ER_EXPECTED_LOC_STEP", null); 
      return false;
    } 
    return true;
  }
  
  protected void Basis() {
    byte b;
    int i = this.m_ops.getOp(1);
    if (lookahead("::", 1)) {
      b = AxisName();
      nextToken();
      nextToken();
    } else if (tokenIs('@')) {
      b = 39;
      appendOp(2, b);
      nextToken();
    } else {
      b = 40;
      appendOp(2, b);
    } 
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    NodeTest(b);
    this.m_ops.setOp(i + 1 + 1, this.m_ops.getOp(1) - i);
  }
  
  protected int AxisName() throws TransformerException {
    Integer integer = Keywords.getAxisName(this.m_token);
    if (null == integer)
      error("ER_ILLEGAL_AXIS_NAME", new Object[] { this.m_token }); 
    int i = ((Integer)integer).intValue();
    appendOp(2, i);
    return i;
  }
  
  protected void NodeTest(int paramInt) throws TransformerException {
    if (lookahead('(', 1)) {
      Integer integer = Keywords.getNodeType(this.m_token);
      if (null == integer) {
        error("ER_UNKNOWN_NODETYPE", new Object[] { this.m_token });
      } else {
        nextToken();
        int i = ((Integer)integer).intValue();
        this.m_ops.setOp(this.m_ops.getOp(1), i);
        this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
        consumeExpected('(');
        if (1032 == i && !tokenIs(')'))
          Literal(); 
        consumeExpected(')');
      } 
    } else {
      this.m_ops.setOp(this.m_ops.getOp(1), 34);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      if (lookahead(':', 1)) {
        if (tokenIs('*')) {
          this.m_ops.setOp(this.m_ops.getOp(1), -3);
        } else {
          this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
          if (!Character.isLetter(this.m_tokenChar) && !tokenIs('_'))
            error("ER_EXPECTED_NODE_TEST", null); 
        } 
        nextToken();
        consumeExpected(':');
      } else {
        this.m_ops.setOp(this.m_ops.getOp(1), -2);
      } 
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      if (tokenIs('*')) {
        this.m_ops.setOp(this.m_ops.getOp(1), -3);
      } else {
        this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
        if (!Character.isLetter(this.m_tokenChar) && !tokenIs('_'))
          error("ER_EXPECTED_NODE_TEST", null); 
      } 
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      nextToken();
    } 
  }
  
  protected void Predicate() {
    if (tokenIs('[')) {
      nextToken();
      PredicateExpr();
      consumeExpected(']');
    } 
  }
  
  protected void PredicateExpr() {
    int i = this.m_ops.getOp(1);
    appendOp(2, 29);
    Expr();
    this.m_ops.setOp(this.m_ops.getOp(1), -1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected void QName() {
    if (lookahead(':', 1)) {
      this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      nextToken();
      consumeExpected(':');
    } else {
      this.m_ops.setOp(this.m_ops.getOp(1), -2);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    } 
    this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    nextToken();
  }
  
  protected void NCName() {
    this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    nextToken();
  }
  
  protected void Literal() {
    int i = this.m_token.length() - 1;
    char c1 = this.m_tokenChar;
    char c2 = this.m_token.charAt(i);
    if ((c1 == '"' && c2 == '"') || (c1 == '\'' && c2 == '\'')) {
      int j = this.m_queueMark - 1;
      this.m_ops.m_tokenQueue.setElementAt(null, j);
      XString xString = new XString(this.m_token.substring(1, i));
      this.m_ops.m_tokenQueue.setElementAt(xString, j);
      this.m_ops.setOp(this.m_ops.getOp(1), j);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      nextToken();
    } else {
      error("ER_PATTERN_LITERAL_NEEDS_BE_QUOTED", new Object[] { this.m_token });
    } 
  }
  
  protected void Number() {
    if (null != this.m_token) {
      double d;
      try {
        if (this.m_token.indexOf('e') > -1 || this.m_token.indexOf('E') > -1)
          throw new NumberFormatException(); 
        d = Double.valueOf(this.m_token).doubleValue();
      } catch (NumberFormatException numberFormatException) {
        d = 0.0D;
        error("ER_COULDNOT_BE_FORMATTED_TO_NUMBER", new Object[] { this.m_token });
      } 
      this.m_ops.m_tokenQueue.setElementAt(new XNumber(d), this.m_queueMark - 1);
      this.m_ops.setOp(this.m_ops.getOp(1), this.m_queueMark - 1);
      this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
      nextToken();
    } 
  }
  
  protected void Pattern() {
    while (true) {
      LocationPathPattern();
      if (tokenIs('|')) {
        nextToken();
        continue;
      } 
      break;
    } 
  }
  
  protected void LocationPathPattern() {
    int i = this.m_ops.getOp(1);
    boolean bool1 = false;
    boolean bool2 = true;
    byte b1 = 2;
    byte b2 = 0;
    appendOp(2, 31);
    if (lookahead('(', 1) && (tokenIs("id") || tokenIs("key"))) {
      IdKeyPattern();
      if (tokenIs('/')) {
        nextToken();
        if (tokenIs('/')) {
          appendOp(4, 52);
          nextToken();
        } else {
          appendOp(4, 53);
        } 
        this.m_ops.setOp(this.m_ops.getOp(1) - 2, 4);
        this.m_ops.setOp(this.m_ops.getOp(1) - 1, 1034);
        b2 = 2;
      } 
    } else if (tokenIs('/')) {
      if (lookahead('/', 1)) {
        appendOp(4, 52);
        nextToken();
        b2 = 2;
      } else {
        appendOp(4, 50);
        b2 = 1;
      } 
      this.m_ops.setOp(this.m_ops.getOp(1) - 2, 4);
      this.m_ops.setOp(this.m_ops.getOp(1) - 1, 35);
      nextToken();
    } else {
      b2 = 2;
    } 
    if (b2 != 0)
      if (!tokenIs('|') && null != this.m_token) {
        RelativePathPattern();
      } else if (b2 == 2) {
        error("ER_EXPECTED_REL_PATH_PATTERN", null);
      }  
    this.m_ops.setOp(this.m_ops.getOp(1), -1);
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
  }
  
  protected void IdKeyPattern() { FunctionCall(); }
  
  protected void RelativePathPattern() {
    for (boolean bool = StepPattern(false); tokenIs('/'); bool = StepPattern(!bool))
      nextToken(); 
  }
  
  protected boolean StepPattern(boolean paramBoolean) throws TransformerException { return AbbreviatedNodeTestStep(paramBoolean); }
  
  protected boolean AbbreviatedNodeTestStep(boolean paramBoolean) throws TransformerException {
    boolean bool;
    byte b;
    int i = this.m_ops.getOp(1);
    int j = -1;
    if (tokenIs('@')) {
      b = 51;
      appendOp(2, b);
      nextToken();
    } else if (lookahead("::", 1)) {
      if (tokenIs("attribute")) {
        b = 51;
        appendOp(2, b);
      } else if (tokenIs("child")) {
        j = this.m_ops.getOp(1);
        b = 53;
        appendOp(2, b);
      } else {
        b = -1;
        error("ER_AXES_NOT_ALLOWED", new Object[] { this.m_token });
      } 
      nextToken();
      nextToken();
    } else if (tokenIs('/')) {
      if (!paramBoolean)
        error("ER_EXPECTED_STEP_PATTERN", null); 
      b = 52;
      appendOp(2, b);
      nextToken();
    } else {
      j = this.m_ops.getOp(1);
      b = 53;
      appendOp(2, b);
    } 
    this.m_ops.setOp(1, this.m_ops.getOp(1) + 1);
    NodeTest(b);
    this.m_ops.setOp(i + 1 + 1, this.m_ops.getOp(1) - i);
    while (tokenIs('['))
      Predicate(); 
    if (j > -1 && tokenIs('/') && lookahead('/', 1)) {
      this.m_ops.setOp(j, 52);
      nextToken();
      bool = true;
    } else {
      bool = false;
    } 
    this.m_ops.setOp(i + 1, this.m_ops.getOp(1) - i);
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\XPathParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
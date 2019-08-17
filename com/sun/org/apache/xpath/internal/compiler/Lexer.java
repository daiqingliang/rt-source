package com.sun.org.apache.xpath.internal.compiler;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import java.util.Vector;
import javax.xml.transform.TransformerException;

class Lexer {
  private Compiler m_compiler;
  
  PrefixResolver m_namespaceContext;
  
  XPathParser m_processor;
  
  static final int TARGETEXTRA = 10000;
  
  private int[] m_patternMap = new int[100];
  
  private int m_patternMapSize;
  
  Lexer(Compiler paramCompiler, PrefixResolver paramPrefixResolver, XPathParser paramXPathParser) {
    this.m_compiler = paramCompiler;
    this.m_namespaceContext = paramPrefixResolver;
    this.m_processor = paramXPathParser;
  }
  
  void tokenize(String paramString) throws TransformerException { tokenize(paramString, null); }
  
  void tokenize(String paramString, Vector paramVector) throws TransformerException {
    this.m_compiler.m_currentPattern = paramString;
    this.m_patternMapSize = 0;
    this.m_compiler.m_opMap = new OpMapVector(2500, 2500, 1);
    int i = paramString.length();
    int j = -1;
    int k = -1;
    boolean bool1 = true;
    boolean bool2 = false;
    boolean bool3 = false;
    byte b = 0;
    for (int m = 0; m < i; m++) {
      char c = paramString.charAt(m);
      switch (c) {
        case '"':
          if (j != -1) {
            bool3 = false;
            bool1 = mapPatternElemPos(b, bool1, bool2);
            bool2 = false;
            if (-1 != k) {
              k = mapNSTokens(paramString, j, k, m);
            } else {
              addToTokenQueue(paramString.substring(j, m));
            } 
          } 
          j = m;
          while (++m < i && (c = paramString.charAt(m)) != '"')
            m++; 
          if (c == '"' && m < i) {
            addToTokenQueue(paramString.substring(j, m + 1));
            j = -1;
            break;
          } 
          this.m_processor.error("ER_EXPECTED_DOUBLE_QUOTE", null);
          break;
        case '\'':
          if (j != -1) {
            bool3 = false;
            bool1 = mapPatternElemPos(b, bool1, bool2);
            bool2 = false;
            if (-1 != k) {
              k = mapNSTokens(paramString, j, k, m);
            } else {
              addToTokenQueue(paramString.substring(j, m));
            } 
          } 
          j = m;
          while (++m < i && (c = paramString.charAt(m)) != '\'')
            m++; 
          if (c == '\'' && m < i) {
            addToTokenQueue(paramString.substring(j, m + 1));
            j = -1;
            break;
          } 
          this.m_processor.error("ER_EXPECTED_SINGLE_QUOTE", null);
          break;
        case '\t':
        case '\n':
        case '\r':
        case ' ':
          if (j != -1) {
            bool3 = false;
            bool1 = mapPatternElemPos(b, bool1, bool2);
            bool2 = false;
            if (-1 != k) {
              k = mapNSTokens(paramString, j, k, m);
            } else {
              addToTokenQueue(paramString.substring(j, m));
            } 
            j = -1;
          } 
          break;
        case '@':
          bool2 = true;
        case '-':
          if ('-' == c) {
            if (!bool3 && j != -1)
              break; 
            bool3 = false;
          } 
        case '!':
        case '$':
        case '(':
        case ')':
        case '*':
        case '+':
        case ',':
        case '/':
        case '<':
        case '=':
        case '>':
        case '[':
        case '\\':
        case ']':
        case '^':
        case '|':
          if (j != -1) {
            bool3 = false;
            bool1 = mapPatternElemPos(b, bool1, bool2);
            bool2 = false;
            if (-1 != k) {
              k = mapNSTokens(paramString, j, k, m);
            } else {
              addToTokenQueue(paramString.substring(j, m));
            } 
            j = -1;
          } else if ('/' == c && bool1) {
            bool1 = mapPatternElemPos(b, bool1, bool2);
          } else if ('*' == c) {
            bool1 = mapPatternElemPos(b, bool1, bool2);
            bool2 = false;
          } 
          if (0 == b && '|' == c) {
            if (null != paramVector)
              recordTokenString(paramVector); 
            bool1 = true;
          } 
          if (')' == c || ']' == c) {
            b--;
          } else if ('(' == c || '[' == c) {
            b++;
          } 
          addToTokenQueue(paramString.substring(m, m + 1));
          break;
        case ':':
          if (m > 0) {
            if (k == m - 1) {
              if (j != -1 && j < m - 1)
                addToTokenQueue(paramString.substring(j, m - 1)); 
              bool3 = false;
              bool2 = false;
              j = -1;
              k = -1;
              addToTokenQueue(paramString.substring(m - 1, m + 1));
              break;
            } 
            k = m;
          } 
        default:
          if (-1 == j) {
            j = m;
            bool3 = Character.isDigit(c);
            break;
          } 
          if (bool3)
            bool3 = Character.isDigit(c); 
          break;
      } 
    } 
    if (j != -1) {
      bool3 = false;
      bool1 = mapPatternElemPos(b, bool1, bool2);
      if (-1 != k || (this.m_namespaceContext != null && this.m_namespaceContext.handlesNullPrefixes())) {
        k = mapNSTokens(paramString, j, k, i);
      } else {
        addToTokenQueue(paramString.substring(j, i));
      } 
    } 
    if (0 == this.m_compiler.getTokenQueueSize()) {
      this.m_processor.error("ER_EMPTY_EXPRESSION", null);
    } else if (null != paramVector) {
      recordTokenString(paramVector);
    } 
    this.m_processor.m_queueMark = 0;
  }
  
  private boolean mapPatternElemPos(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    if (0 == paramInt) {
      if (this.m_patternMapSize >= this.m_patternMap.length) {
        int[] arrayOfInt = this.m_patternMap;
        int i = this.m_patternMap.length;
        this.m_patternMap = new int[this.m_patternMapSize + 100];
        System.arraycopy(arrayOfInt, 0, this.m_patternMap, 0, i);
      } 
      if (!paramBoolean1)
        this.m_patternMap[this.m_patternMapSize - 1] = this.m_patternMap[this.m_patternMapSize - 1] - 10000; 
      this.m_patternMap[this.m_patternMapSize] = this.m_compiler.getTokenQueueSize() - (paramBoolean2 ? 1 : 0) + 10000;
      this.m_patternMapSize++;
      paramBoolean1 = false;
    } 
    return paramBoolean1;
  }
  
  private int getTokenQueuePosFromMap(int paramInt) {
    int i = this.m_patternMap[paramInt];
    return (i >= 10000) ? (i - 10000) : i;
  }
  
  private final void resetTokenMark(int paramInt) {
    int i = this.m_compiler.getTokenQueueSize();
    this.m_processor.m_queueMark = (paramInt > 0) ? ((paramInt <= i) ? (paramInt - 1) : paramInt) : 0;
    if (this.m_processor.m_queueMark < i) {
      this.m_processor.m_token = (String)this.m_compiler.getTokenQueue().elementAt(this.m_processor.m_queueMark++);
      this.m_processor.m_tokenChar = this.m_processor.m_token.charAt(0);
    } else {
      this.m_processor.m_token = null;
      this.m_processor.m_tokenChar = Character.MIN_VALUE;
    } 
  }
  
  final int getKeywordToken(String paramString) {
    byte b;
    try {
      Integer integer = Keywords.getKeyWord(paramString);
      b = (null != integer) ? integer.intValue() : 0;
    } catch (NullPointerException nullPointerException) {
      b = 0;
    } catch (ClassCastException classCastException) {
      b = 0;
    } 
    return b;
  }
  
  private void recordTokenString(Vector paramVector) {
    int i = getTokenQueuePosFromMap(this.m_patternMapSize - 1);
    resetTokenMark(i + 1);
    if (this.m_processor.lookahead('(', 1)) {
      int j = getKeywordToken(this.m_processor.m_token);
      switch (j) {
        case 1030:
          paramVector.addElement("#comment");
          return;
        case 1031:
          paramVector.addElement("#text");
          return;
        case 1033:
          paramVector.addElement("*");
          return;
        case 35:
          paramVector.addElement("/");
          return;
        case 36:
          paramVector.addElement("*");
          return;
        case 1032:
          paramVector.addElement("*");
          return;
      } 
      paramVector.addElement("*");
    } else {
      if (this.m_processor.tokenIs('@'))
        resetTokenMark(++i + 1); 
      if (this.m_processor.lookahead(':', 1))
        i += 2; 
      paramVector.addElement(this.m_compiler.getTokenQueue().elementAt(i));
    } 
  }
  
  private final void addToTokenQueue(String paramString) throws TransformerException { this.m_compiler.getTokenQueue().addElement(paramString); }
  
  private int mapNSTokens(String paramString, int paramInt1, int paramInt2, int paramInt3) throws TransformerException {
    String str2;
    String str1 = "";
    if (paramInt1 >= 0 && paramInt2 >= 0)
      str1 = paramString.substring(paramInt1, paramInt2); 
    if (null != this.m_namespaceContext && !str1.equals("*") && !str1.equals("xmlns")) {
      try {
        if (str1.length() > 0) {
          str2 = this.m_namespaceContext.getNamespaceForPrefix(str1);
        } else {
          str2 = this.m_namespaceContext.getNamespaceForPrefix(str1);
        } 
      } catch (ClassCastException classCastException) {
        str2 = this.m_namespaceContext.getNamespaceForPrefix(str1);
      } 
    } else {
      str2 = str1;
    } 
    if (null != str2 && str2.length() > 0) {
      addToTokenQueue(str2);
      addToTokenQueue(":");
      String str = paramString.substring(paramInt2 + 1, paramInt3);
      if (str.length() > 0)
        addToTokenQueue(str); 
    } else {
      this.m_processor.errorForDOM3("ER_PREFIX_MUST_RESOLVE", new String[] { str1 });
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xpath\internal\compiler\Lexer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
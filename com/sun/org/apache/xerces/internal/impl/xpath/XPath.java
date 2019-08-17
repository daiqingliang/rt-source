package com.sun.org.apache.xerces.internal.impl.xpath;

import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.util.XMLSymbols;
import com.sun.org.apache.xerces.internal.xni.NamespaceContext;
import com.sun.org.apache.xerces.internal.xni.QName;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class XPath {
  private static final boolean DEBUG_ALL = false;
  
  private static final boolean DEBUG_XPATH_PARSE = false;
  
  private static final boolean DEBUG_ANY = false;
  
  protected String fExpression;
  
  protected SymbolTable fSymbolTable;
  
  protected LocationPath[] fLocationPaths;
  
  public XPath(String paramString, SymbolTable paramSymbolTable, NamespaceContext paramNamespaceContext) throws XPathException {
    this.fExpression = paramString;
    this.fSymbolTable = paramSymbolTable;
    parseExpression(paramNamespaceContext);
  }
  
  public LocationPath[] getLocationPaths() {
    LocationPath[] arrayOfLocationPath = new LocationPath[this.fLocationPaths.length];
    for (byte b = 0; b < this.fLocationPaths.length; b++)
      arrayOfLocationPath[b] = (LocationPath)this.fLocationPaths[b].clone(); 
    return arrayOfLocationPath;
  }
  
  public LocationPath getLocationPath() { return (LocationPath)this.fLocationPaths[0].clone(); }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.fLocationPaths.length; b++) {
      if (b)
        stringBuffer.append("|"); 
      stringBuffer.append(this.fLocationPaths[b].toString());
    } 
    return stringBuffer.toString();
  }
  
  private static void check(boolean paramBoolean) throws XPathException {
    if (!paramBoolean)
      throw new XPathException("c-general-xpath"); 
  }
  
  private LocationPath buildLocationPath(Vector paramVector) throws XPathException {
    int i = paramVector.size();
    check((i != 0));
    Step[] arrayOfStep = new Step[i];
    paramVector.copyInto(arrayOfStep);
    paramVector.removeAllElements();
    return new LocationPath(arrayOfStep);
  }
  
  private void parseExpression(NamespaceContext paramNamespaceContext) throws XPathException {
    Tokens tokens = new Tokens(this.fSymbolTable);
    Scanner scanner = new Scanner(this.fSymbolTable) {
        protected void addToken(XPath.Tokens param1Tokens, int param1Int) throws XPathException {
          if (param1Int == 6 || param1Int == 35 || param1Int == 11 || param1Int == 21 || param1Int == 4 || param1Int == 9 || param1Int == 10 || param1Int == 22 || param1Int == 23 || param1Int == 36 || param1Int == 8) {
            super.addToken(param1Tokens, param1Int);
            return;
          } 
          throw new XPathException("c-general-xpath");
        }
      };
    int i = this.fExpression.length();
    boolean bool1 = scanner.scanExpr(this.fSymbolTable, tokens, this.fExpression, 0, i);
    if (!bool1)
      throw new XPathException("c-general-xpath"); 
    Vector vector1 = new Vector();
    Vector vector2 = new Vector();
    boolean bool2 = true;
    boolean bool3 = false;
    while (tokens.hasMore()) {
      Step step;
      int j = tokens.nextToken();
      switch (j) {
        case 23:
          check(!bool2);
          vector2.addElement(buildLocationPath(vector1));
          bool2 = true;
          continue;
        case 6:
          check(bool2);
          step = new Step(new Axis((short)2), parseNodeTest(tokens.nextToken(), tokens, paramNamespaceContext));
          vector1.addElement(step);
          bool2 = false;
          continue;
        case 9:
        case 10:
        case 11:
          check(bool2);
          step = new Step(new Axis((short)1), parseNodeTest(j, tokens, paramNamespaceContext));
          vector1.addElement(step);
          bool2 = false;
          continue;
        case 4:
          check(bool2);
          bool2 = false;
          if (vector1.size() == 0) {
            Axis axis = new Axis((short)3);
            NodeTest nodeTest = new NodeTest((short)3);
            Step step1 = new Step(axis, nodeTest);
            vector1.addElement(step1);
            if (tokens.hasMore() && tokens.peekToken() == 22) {
              tokens.nextToken();
              axis = new Axis((short)4);
              nodeTest = new NodeTest((short)3);
              step1 = new Step(axis, nodeTest);
              vector1.addElement(step1);
              bool2 = true;
            } 
          } 
          continue;
        case 22:
          throw new XPathException("c-general-xpath");
        case 21:
          check(!bool2);
          bool2 = true;
          continue;
        case 35:
          check(bool2);
          bool3 = true;
          if (tokens.nextToken() == 8) {
            step = new Step(new Axis((short)2), parseNodeTest(tokens.nextToken(), tokens, paramNamespaceContext));
            vector1.addElement(step);
            bool2 = false;
            bool3 = false;
          } 
          continue;
        case 36:
          check(bool2);
          bool3 = true;
          continue;
        case 8:
          check(bool2);
          check(bool3);
          bool3 = false;
          continue;
      } 
      throw new XPathException("c-general-xpath");
    } 
    check(!bool2);
    vector2.addElement(buildLocationPath(vector1));
    this.fLocationPaths = new LocationPath[vector2.size()];
    vector2.copyInto(this.fLocationPaths);
  }
  
  private NodeTest parseNodeTest(int paramInt, Tokens paramTokens, NamespaceContext paramNamespaceContext) throws XPathException {
    String str4;
    String str3;
    String str2;
    String str1;
    switch (paramInt) {
      case 9:
        return new NodeTest((short)2);
      case 10:
      case 11:
        str1 = paramTokens.nextTokenAsString();
        str2 = null;
        if (paramNamespaceContext != null && str1 != XMLSymbols.EMPTY_STRING)
          str2 = paramNamespaceContext.getURI(str1); 
        if (str1 != XMLSymbols.EMPTY_STRING && paramNamespaceContext != null && str2 == null)
          throw new XPathException("c-general-xpath-ns"); 
        if (paramInt == 10)
          return new NodeTest(str1, str2); 
        str3 = paramTokens.nextTokenAsString();
        str4 = (str1 != XMLSymbols.EMPTY_STRING) ? this.fSymbolTable.addSymbol(str1 + ':' + str3) : str3;
        return new NodeTest(new QName(str1, str3, str4, str2));
    } 
    throw new XPathException("c-general-xpath");
  }
  
  public static void main(String[] paramArrayOfString) throws Exception {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String str = paramArrayOfString[b];
      System.out.println("# XPath expression: \"" + str + '"');
      try {
        SymbolTable symbolTable = new SymbolTable();
        XPath xPath = new XPath(str, symbolTable, null);
        System.out.println("expanded xpath: \"" + xPath.toString() + '"');
      } catch (XPathException xPathException) {
        System.out.println("error: " + xPathException.getMessage());
      } 
    } 
  }
  
  public static class Axis implements Cloneable {
    public static final short CHILD = 1;
    
    public static final short ATTRIBUTE = 2;
    
    public static final short SELF = 3;
    
    public static final short DESCENDANT = 4;
    
    public short type;
    
    public Axis(short param1Short) { this.type = param1Short; }
    
    protected Axis(Axis param1Axis) { this.type = param1Axis.type; }
    
    public String toString() {
      switch (this.type) {
        case 1:
          return "child";
        case 2:
          return "attribute";
        case 3:
          return "self";
        case 4:
          return "descendant";
      } 
      return "???";
    }
    
    public Object clone() { return new Axis(this); }
  }
  
  public static class LocationPath implements Cloneable {
    public XPath.Step[] steps;
    
    public LocationPath(XPath.Step[] param1ArrayOfStep) { this.steps = param1ArrayOfStep; }
    
    protected LocationPath(LocationPath param1LocationPath) {
      this.steps = new XPath.Step[param1LocationPath.steps.length];
      for (byte b = 0; b < this.steps.length; b++)
        this.steps[b] = (XPath.Step)param1LocationPath.steps[b].clone(); 
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.steps.length; b++) {
        if (b && (this.steps[b - true]).axis.type != 4 && (this.steps[b]).axis.type != 4)
          stringBuffer.append('/'); 
        stringBuffer.append(this.steps[b].toString());
      } 
      return stringBuffer.toString();
    }
    
    public Object clone() { return new LocationPath(this); }
  }
  
  public static class NodeTest implements Cloneable {
    public static final short QNAME = 1;
    
    public static final short WILDCARD = 2;
    
    public static final short NODE = 3;
    
    public static final short NAMESPACE = 4;
    
    public short type;
    
    public final QName name = new QName();
    
    public NodeTest(short param1Short) { this.type = param1Short; }
    
    public NodeTest(QName param1QName) {
      this.type = 1;
      this.name.setValues(param1QName);
    }
    
    public NodeTest(String param1String1, String param1String2) {
      this.type = 4;
      this.name.setValues(param1String1, null, null, param1String2);
    }
    
    public NodeTest(NodeTest param1NodeTest) {
      this.type = param1NodeTest.type;
      this.name.setValues(param1NodeTest.name);
    }
    
    public String toString() {
      switch (this.type) {
        case 1:
          return (this.name.prefix.length() != 0) ? ((this.name.uri != null) ? (this.name.prefix + ':' + this.name.localpart) : ("{" + this.name.uri + '}' + this.name.prefix + ':' + this.name.localpart)) : this.name.localpart;
        case 4:
          return (this.name.prefix.length() != 0) ? ((this.name.uri != null) ? (this.name.prefix + ":*") : ("{" + this.name.uri + '}' + this.name.prefix + ":*")) : "???:*";
        case 2:
          return "*";
        case 3:
          return "node()";
      } 
      return "???";
    }
    
    public Object clone() { return new NodeTest(this); }
  }
  
  private static class Scanner {
    private static final byte CHARTYPE_INVALID = 0;
    
    private static final byte CHARTYPE_OTHER = 1;
    
    private static final byte CHARTYPE_WHITESPACE = 2;
    
    private static final byte CHARTYPE_EXCLAMATION = 3;
    
    private static final byte CHARTYPE_QUOTE = 4;
    
    private static final byte CHARTYPE_DOLLAR = 5;
    
    private static final byte CHARTYPE_OPEN_PAREN = 6;
    
    private static final byte CHARTYPE_CLOSE_PAREN = 7;
    
    private static final byte CHARTYPE_STAR = 8;
    
    private static final byte CHARTYPE_PLUS = 9;
    
    private static final byte CHARTYPE_COMMA = 10;
    
    private static final byte CHARTYPE_MINUS = 11;
    
    private static final byte CHARTYPE_PERIOD = 12;
    
    private static final byte CHARTYPE_SLASH = 13;
    
    private static final byte CHARTYPE_DIGIT = 14;
    
    private static final byte CHARTYPE_COLON = 15;
    
    private static final byte CHARTYPE_LESS = 16;
    
    private static final byte CHARTYPE_EQUAL = 17;
    
    private static final byte CHARTYPE_GREATER = 18;
    
    private static final byte CHARTYPE_ATSIGN = 19;
    
    private static final byte CHARTYPE_LETTER = 20;
    
    private static final byte CHARTYPE_OPEN_BRACKET = 21;
    
    private static final byte CHARTYPE_CLOSE_BRACKET = 22;
    
    private static final byte CHARTYPE_UNDERSCORE = 23;
    
    private static final byte CHARTYPE_UNION = 24;
    
    private static final byte CHARTYPE_NONASCII = 25;
    
    private static final byte[] fASCIICharMap = { 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 
        2, 0, 0, 2, 0, 0, 0, 0, 0, 0, 
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
        0, 0, 2, 3, 4, 1, 5, 1, 1, 4, 
        6, 7, 8, 9, 10, 11, 12, 13, 14, 14, 
        14, 14, 14, 14, 14, 14, 14, 14, 15, 1, 
        16, 17, 18, 1, 19, 20, 20, 20, 20, 20, 
        20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 
        20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 
        20, 21, 1, 22, 1, 23, 1, 20, 20, 20, 
        20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 
        20, 20, 20, 20, 20, 20, 20, 20, 20, 20, 
        20, 20, 20, 1, 24, 1, 1, 1 };
    
    private SymbolTable fSymbolTable;
    
    private static final String fAndSymbol = "and".intern();
    
    private static final String fOrSymbol = "or".intern();
    
    private static final String fModSymbol = "mod".intern();
    
    private static final String fDivSymbol = "div".intern();
    
    private static final String fCommentSymbol = "comment".intern();
    
    private static final String fTextSymbol = "text".intern();
    
    private static final String fPISymbol = "processing-instruction".intern();
    
    private static final String fNodeSymbol = "node".intern();
    
    private static final String fAncestorSymbol = "ancestor".intern();
    
    private static final String fAncestorOrSelfSymbol = "ancestor-or-self".intern();
    
    private static final String fAttributeSymbol = "attribute".intern();
    
    private static final String fChildSymbol = "child".intern();
    
    private static final String fDescendantSymbol = "descendant".intern();
    
    private static final String fDescendantOrSelfSymbol = "descendant-or-self".intern();
    
    private static final String fFollowingSymbol = "following".intern();
    
    private static final String fFollowingSiblingSymbol = "following-sibling".intern();
    
    private static final String fNamespaceSymbol = "namespace".intern();
    
    private static final String fParentSymbol = "parent".intern();
    
    private static final String fPrecedingSymbol = "preceding".intern();
    
    private static final String fPrecedingSiblingSymbol = "preceding-sibling".intern();
    
    private static final String fSelfSymbol = "self".intern();
    
    public Scanner(SymbolTable param1SymbolTable) { this.fSymbolTable = param1SymbolTable; }
    
    public boolean scanExpr(SymbolTable param1SymbolTable, XPath.Tokens param1Tokens, String param1String, int param1Int1, int param1Int2) throws XPathException {
      boolean bool = false;
      while (param1Int1 != param1Int2) {
        boolean bool2;
        boolean bool1;
        int m;
        int k;
        char c;
        String str2;
        String str1;
        int i;
        int j;
        for (j = param1String.charAt(param1Int1); (j == 32 || j == 10 || j == 9 || j == 13) && ++param1Int1 != param1Int2; j = param1String.charAt(param1Int1));
        if (param1Int1 == param1Int2)
          break; 
        byte b = (j >= 128) ? 25 : fASCIICharMap[j];
        switch (b) {
          case 6:
            addToken(param1Tokens, 0);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 7:
            addToken(param1Tokens, 1);
            bool = true;
            if (++param1Int1 == param1Int2);
          case 21:
            addToken(param1Tokens, 2);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 22:
            addToken(param1Tokens, 3);
            bool = true;
            if (++param1Int1 == param1Int2);
          case 12:
            if (param1Int1 + 1 == param1Int2) {
              addToken(param1Tokens, 4);
              bool = true;
              param1Int1++;
              continue;
            } 
            j = param1String.charAt(param1Int1 + 1);
            if (j == 46) {
              addToken(param1Tokens, 5);
              bool = true;
              param1Int1 += 2;
            } else if (j >= 48 && j <= 57) {
              addToken(param1Tokens, 47);
              bool = true;
              param1Int1 = scanNumber(param1Tokens, param1String, param1Int2, param1Int1);
            } else if (j == 47) {
              addToken(param1Tokens, 4);
              bool = true;
              param1Int1++;
            } else {
              if (j == 124) {
                addToken(param1Tokens, 4);
                bool = true;
                param1Int1++;
                continue;
              } 
              if (j == 32 || j == 10 || j == 9 || j == 13) {
                while (++param1Int1 != param1Int2) {
                  j = param1String.charAt(param1Int1);
                  if (j != 32 && j != 10 && j != 9 && j != 13)
                    break; 
                } 
                if (param1Int1 == param1Int2 || j == 124 || j == 47) {
                  addToken(param1Tokens, 4);
                  bool = true;
                  continue;
                } 
                throw new XPathException("c-general-xpath");
              } 
              throw new XPathException("c-general-xpath");
            } 
            if (param1Int1 == param1Int2);
          case 19:
            addToken(param1Tokens, 6);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 10:
            addToken(param1Tokens, 7);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 15:
            if (++param1Int1 == param1Int2)
              return false; 
            j = param1String.charAt(param1Int1);
            if (j != 58)
              return false; 
            addToken(param1Tokens, 8);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 13:
            if (++param1Int1 == param1Int2) {
              addToken(param1Tokens, 21);
              bool = false;
              continue;
            } 
            j = param1String.charAt(param1Int1);
            if (j == 47) {
              addToken(param1Tokens, 22);
              bool = false;
              if (++param1Int1 == param1Int2);
              continue;
            } 
            addToken(param1Tokens, 21);
            bool = false;
          case 24:
            addToken(param1Tokens, 23);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 9:
            addToken(param1Tokens, 24);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 11:
            addToken(param1Tokens, 25);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 17:
            addToken(param1Tokens, 26);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 3:
            if (++param1Int1 == param1Int2)
              return false; 
            j = param1String.charAt(param1Int1);
            if (j != 61)
              return false; 
            addToken(param1Tokens, 27);
            bool = false;
            if (++param1Int1 == param1Int2);
          case 16:
            if (++param1Int1 == param1Int2) {
              addToken(param1Tokens, 28);
              bool = false;
              continue;
            } 
            j = param1String.charAt(param1Int1);
            if (j == 61) {
              addToken(param1Tokens, 29);
              bool = false;
              if (++param1Int1 == param1Int2);
              continue;
            } 
            addToken(param1Tokens, 28);
            bool = false;
          case 18:
            if (++param1Int1 == param1Int2) {
              addToken(param1Tokens, 30);
              bool = false;
              continue;
            } 
            j = param1String.charAt(param1Int1);
            if (j == 61) {
              addToken(param1Tokens, 31);
              bool = false;
              if (++param1Int1 == param1Int2);
              continue;
            } 
            addToken(param1Tokens, 30);
            bool = false;
          case 4:
            c = j;
            if (++param1Int1 == param1Int2)
              return false; 
            j = param1String.charAt(param1Int1);
            k = param1Int1;
            while (j != c) {
              if (++param1Int1 == param1Int2)
                return false; 
              j = param1String.charAt(param1Int1);
            } 
            m = param1Int1 - k;
            addToken(param1Tokens, 46);
            bool = true;
            param1Tokens.addToken(param1SymbolTable.addSymbol(param1String.substring(k, k + m)));
            if (++param1Int1 == param1Int2);
          case 14:
            addToken(param1Tokens, 47);
            bool = true;
            param1Int1 = scanNumber(param1Tokens, param1String, param1Int2, param1Int1);
          case 5:
            if (++param1Int1 == param1Int2)
              return false; 
            i = param1Int1;
            param1Int1 = scanNCName(param1String, param1Int2, param1Int1);
            if (param1Int1 == i)
              return false; 
            if (param1Int1 < param1Int2) {
              j = param1String.charAt(param1Int1);
            } else {
              j = -1;
            } 
            str1 = param1SymbolTable.addSymbol(param1String.substring(i, param1Int1));
            if (j != 58) {
              str2 = XMLSymbols.EMPTY_STRING;
            } else {
              str2 = str1;
              if (++param1Int1 == param1Int2)
                return false; 
              i = param1Int1;
              param1Int1 = scanNCName(param1String, param1Int2, param1Int1);
              if (param1Int1 == i)
                return false; 
              if (param1Int1 < param1Int2) {
                j = param1String.charAt(param1Int1);
              } else {
                j = -1;
              } 
              str1 = param1SymbolTable.addSymbol(param1String.substring(i, param1Int1));
            } 
            addToken(param1Tokens, 48);
            bool = true;
            param1Tokens.addToken(str2);
            param1Tokens.addToken(str1);
          case 8:
            if (bool) {
              addToken(param1Tokens, 20);
              bool = false;
            } else {
              addToken(param1Tokens, 9);
              bool = true;
            } 
            if (++param1Int1 == param1Int2);
          case 20:
          case 23:
          case 25:
            i = param1Int1;
            param1Int1 = scanNCName(param1String, param1Int2, param1Int1);
            if (param1Int1 == i)
              return false; 
            if (param1Int1 < param1Int2) {
              j = param1String.charAt(param1Int1);
            } else {
              j = -1;
            } 
            str1 = param1SymbolTable.addSymbol(param1String.substring(i, param1Int1));
            bool1 = false;
            bool2 = false;
            str2 = XMLSymbols.EMPTY_STRING;
            if (j == 58) {
              if (++param1Int1 == param1Int2)
                return false; 
              j = param1String.charAt(param1Int1);
              if (j == 42) {
                if (++param1Int1 < param1Int2)
                  j = param1String.charAt(param1Int1); 
                bool1 = true;
              } else if (j == 58) {
                if (++param1Int1 < param1Int2)
                  j = param1String.charAt(param1Int1); 
                bool2 = true;
              } else {
                str2 = str1;
                i = param1Int1;
                param1Int1 = scanNCName(param1String, param1Int2, param1Int1);
                if (param1Int1 == i)
                  return false; 
                if (param1Int1 < param1Int2) {
                  j = param1String.charAt(param1Int1);
                } else {
                  j = -1;
                } 
                str1 = param1SymbolTable.addSymbol(param1String.substring(i, param1Int1));
              } 
            } 
            while ((j == 32 || j == 10 || j == 9 || j == 13) && ++param1Int1 != param1Int2)
              j = param1String.charAt(param1Int1); 
            if (bool) {
              if (str1 == fAndSymbol) {
                addToken(param1Tokens, 16);
                bool = false;
              } else if (str1 == fOrSymbol) {
                addToken(param1Tokens, 17);
                bool = false;
              } else if (str1 == fModSymbol) {
                addToken(param1Tokens, 18);
                bool = false;
              } else if (str1 == fDivSymbol) {
                addToken(param1Tokens, 19);
                bool = false;
              } else {
                return false;
              } 
              if (bool1)
                return false; 
              if (bool2)
                return false; 
              continue;
            } 
            if (j == 40 && !bool1 && !bool2) {
              if (str1 == fCommentSymbol) {
                addToken(param1Tokens, 12);
              } else if (str1 == fTextSymbol) {
                addToken(param1Tokens, 13);
              } else if (str1 == fPISymbol) {
                addToken(param1Tokens, 14);
              } else if (str1 == fNodeSymbol) {
                addToken(param1Tokens, 15);
              } else {
                addToken(param1Tokens, 32);
                param1Tokens.addToken(str2);
                param1Tokens.addToken(str1);
              } 
              addToken(param1Tokens, 0);
              bool = false;
              if (++param1Int1 == param1Int2);
              continue;
            } 
            if (bool2 || (j == 58 && param1Int1 + 1 < param1Int2 && param1String.charAt(param1Int1 + 1) == ':')) {
              if (str1 == fAncestorSymbol) {
                addToken(param1Tokens, 33);
              } else if (str1 == fAncestorOrSelfSymbol) {
                addToken(param1Tokens, 34);
              } else if (str1 == fAttributeSymbol) {
                addToken(param1Tokens, 35);
              } else if (str1 == fChildSymbol) {
                addToken(param1Tokens, 36);
              } else if (str1 == fDescendantSymbol) {
                addToken(param1Tokens, 37);
              } else if (str1 == fDescendantOrSelfSymbol) {
                addToken(param1Tokens, 38);
              } else if (str1 == fFollowingSymbol) {
                addToken(param1Tokens, 39);
              } else if (str1 == fFollowingSiblingSymbol) {
                addToken(param1Tokens, 40);
              } else if (str1 == fNamespaceSymbol) {
                addToken(param1Tokens, 41);
              } else if (str1 == fParentSymbol) {
                addToken(param1Tokens, 42);
              } else if (str1 == fPrecedingSymbol) {
                addToken(param1Tokens, 43);
              } else if (str1 == fPrecedingSiblingSymbol) {
                addToken(param1Tokens, 44);
              } else if (str1 == fSelfSymbol) {
                addToken(param1Tokens, 45);
              } else {
                return false;
              } 
              if (bool1)
                return false; 
              addToken(param1Tokens, 8);
              bool = false;
              if (!bool2) {
                param1Int1++;
                if (++param1Int1 == param1Int2);
              } 
              continue;
            } 
            if (bool1) {
              addToken(param1Tokens, 10);
              bool = true;
              param1Tokens.addToken(str1);
              continue;
            } 
            addToken(param1Tokens, 11);
            bool = true;
            param1Tokens.addToken(str2);
            param1Tokens.addToken(str1);
        } 
      } 
      return true;
    }
    
    int scanNCName(String param1String, int param1Int1, int param1Int2) {
      char c = param1String.charAt(param1Int2);
      if (c >= '') {
        if (!XMLChar.isNameStart(c))
          return param1Int2; 
      } else {
        byte b = fASCIICharMap[c];
        if (b != 20 && b != 23)
          return param1Int2; 
      } 
      while (++param1Int2 < param1Int1) {
        c = param1String.charAt(param1Int2);
        if (c >= '') {
          if (!XMLChar.isName(c))
            break; 
          continue;
        } 
        byte b = fASCIICharMap[c];
        if (b != 20 && b != 14 && b != 12 && b != 11 && b != 23)
          break; 
      } 
      return param1Int2;
    }
    
    private int scanNumber(XPath.Tokens param1Tokens, String param1String, int param1Int1, int param1Int2) {
      char c = param1String.charAt(param1Int2);
      char c1 = Character.MIN_VALUE;
      char c2 = Character.MIN_VALUE;
      while (c >= '0' && c <= '9') {
        c1 = c1 * 10 + c - '0';
        if (++param1Int2 == param1Int1)
          break; 
        c = param1String.charAt(param1Int2);
      } 
      if (c == '.' && ++param1Int2 < param1Int1) {
        for (c = param1String.charAt(param1Int2); c >= '0' && c <= '9'; c = param1String.charAt(param1Int2)) {
          c2 = c2 * 10 + c - '0';
          if (++param1Int2 == param1Int1)
            break; 
        } 
        if (c2 != '\000')
          throw new RuntimeException("find a solution!"); 
      } 
      param1Tokens.addToken(c1);
      param1Tokens.addToken(c2);
      return param1Int2;
    }
    
    protected void addToken(XPath.Tokens param1Tokens, int param1Int) throws XPathException { param1Tokens.addToken(param1Int); }
  }
  
  public static class Step implements Cloneable {
    public XPath.Axis axis;
    
    public XPath.NodeTest nodeTest;
    
    public Step(XPath.Axis param1Axis, XPath.NodeTest param1NodeTest) {
      this.axis = param1Axis;
      this.nodeTest = param1NodeTest;
    }
    
    protected Step(Step param1Step) {
      this.axis = (XPath.Axis)param1Step.axis.clone();
      this.nodeTest = (XPath.NodeTest)param1Step.nodeTest.clone();
    }
    
    public String toString() { return (this.axis.type == 3) ? "." : ((this.axis.type == 2) ? ("@" + this.nodeTest.toString()) : ((this.axis.type == 1) ? this.nodeTest.toString() : ((this.axis.type == 4) ? "//" : ("??? (" + this.axis.type + ')')))); }
    
    public Object clone() { return new Step(this); }
  }
  
  private static final class Tokens {
    static final boolean DUMP_TOKENS = false;
    
    public static final int EXPRTOKEN_OPEN_PAREN = 0;
    
    public static final int EXPRTOKEN_CLOSE_PAREN = 1;
    
    public static final int EXPRTOKEN_OPEN_BRACKET = 2;
    
    public static final int EXPRTOKEN_CLOSE_BRACKET = 3;
    
    public static final int EXPRTOKEN_PERIOD = 4;
    
    public static final int EXPRTOKEN_DOUBLE_PERIOD = 5;
    
    public static final int EXPRTOKEN_ATSIGN = 6;
    
    public static final int EXPRTOKEN_COMMA = 7;
    
    public static final int EXPRTOKEN_DOUBLE_COLON = 8;
    
    public static final int EXPRTOKEN_NAMETEST_ANY = 9;
    
    public static final int EXPRTOKEN_NAMETEST_NAMESPACE = 10;
    
    public static final int EXPRTOKEN_NAMETEST_QNAME = 11;
    
    public static final int EXPRTOKEN_NODETYPE_COMMENT = 12;
    
    public static final int EXPRTOKEN_NODETYPE_TEXT = 13;
    
    public static final int EXPRTOKEN_NODETYPE_PI = 14;
    
    public static final int EXPRTOKEN_NODETYPE_NODE = 15;
    
    public static final int EXPRTOKEN_OPERATOR_AND = 16;
    
    public static final int EXPRTOKEN_OPERATOR_OR = 17;
    
    public static final int EXPRTOKEN_OPERATOR_MOD = 18;
    
    public static final int EXPRTOKEN_OPERATOR_DIV = 19;
    
    public static final int EXPRTOKEN_OPERATOR_MULT = 20;
    
    public static final int EXPRTOKEN_OPERATOR_SLASH = 21;
    
    public static final int EXPRTOKEN_OPERATOR_DOUBLE_SLASH = 22;
    
    public static final int EXPRTOKEN_OPERATOR_UNION = 23;
    
    public static final int EXPRTOKEN_OPERATOR_PLUS = 24;
    
    public static final int EXPRTOKEN_OPERATOR_MINUS = 25;
    
    public static final int EXPRTOKEN_OPERATOR_EQUAL = 26;
    
    public static final int EXPRTOKEN_OPERATOR_NOT_EQUAL = 27;
    
    public static final int EXPRTOKEN_OPERATOR_LESS = 28;
    
    public static final int EXPRTOKEN_OPERATOR_LESS_EQUAL = 29;
    
    public static final int EXPRTOKEN_OPERATOR_GREATER = 30;
    
    public static final int EXPRTOKEN_OPERATOR_GREATER_EQUAL = 31;
    
    public static final int EXPRTOKEN_FUNCTION_NAME = 32;
    
    public static final int EXPRTOKEN_AXISNAME_ANCESTOR = 33;
    
    public static final int EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF = 34;
    
    public static final int EXPRTOKEN_AXISNAME_ATTRIBUTE = 35;
    
    public static final int EXPRTOKEN_AXISNAME_CHILD = 36;
    
    public static final int EXPRTOKEN_AXISNAME_DESCENDANT = 37;
    
    public static final int EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF = 38;
    
    public static final int EXPRTOKEN_AXISNAME_FOLLOWING = 39;
    
    public static final int EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING = 40;
    
    public static final int EXPRTOKEN_AXISNAME_NAMESPACE = 41;
    
    public static final int EXPRTOKEN_AXISNAME_PARENT = 42;
    
    public static final int EXPRTOKEN_AXISNAME_PRECEDING = 43;
    
    public static final int EXPRTOKEN_AXISNAME_PRECEDING_SIBLING = 44;
    
    public static final int EXPRTOKEN_AXISNAME_SELF = 45;
    
    public static final int EXPRTOKEN_LITERAL = 46;
    
    public static final int EXPRTOKEN_NUMBER = 47;
    
    public static final int EXPRTOKEN_VARIABLE_REFERENCE = 48;
    
    private static final String[] fgTokenNames = { 
        "EXPRTOKEN_OPEN_PAREN", "EXPRTOKEN_CLOSE_PAREN", "EXPRTOKEN_OPEN_BRACKET", "EXPRTOKEN_CLOSE_BRACKET", "EXPRTOKEN_PERIOD", "EXPRTOKEN_DOUBLE_PERIOD", "EXPRTOKEN_ATSIGN", "EXPRTOKEN_COMMA", "EXPRTOKEN_DOUBLE_COLON", "EXPRTOKEN_NAMETEST_ANY", 
        "EXPRTOKEN_NAMETEST_NAMESPACE", "EXPRTOKEN_NAMETEST_QNAME", "EXPRTOKEN_NODETYPE_COMMENT", "EXPRTOKEN_NODETYPE_TEXT", "EXPRTOKEN_NODETYPE_PI", "EXPRTOKEN_NODETYPE_NODE", "EXPRTOKEN_OPERATOR_AND", "EXPRTOKEN_OPERATOR_OR", "EXPRTOKEN_OPERATOR_MOD", "EXPRTOKEN_OPERATOR_DIV", 
        "EXPRTOKEN_OPERATOR_MULT", "EXPRTOKEN_OPERATOR_SLASH", "EXPRTOKEN_OPERATOR_DOUBLE_SLASH", "EXPRTOKEN_OPERATOR_UNION", "EXPRTOKEN_OPERATOR_PLUS", "EXPRTOKEN_OPERATOR_MINUS", "EXPRTOKEN_OPERATOR_EQUAL", "EXPRTOKEN_OPERATOR_NOT_EQUAL", "EXPRTOKEN_OPERATOR_LESS", "EXPRTOKEN_OPERATOR_LESS_EQUAL", 
        "EXPRTOKEN_OPERATOR_GREATER", "EXPRTOKEN_OPERATOR_GREATER_EQUAL", "EXPRTOKEN_FUNCTION_NAME", "EXPRTOKEN_AXISNAME_ANCESTOR", "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF", "EXPRTOKEN_AXISNAME_ATTRIBUTE", "EXPRTOKEN_AXISNAME_CHILD", "EXPRTOKEN_AXISNAME_DESCENDANT", "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF", "EXPRTOKEN_AXISNAME_FOLLOWING", 
        "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING", "EXPRTOKEN_AXISNAME_NAMESPACE", "EXPRTOKEN_AXISNAME_PARENT", "EXPRTOKEN_AXISNAME_PRECEDING", "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING", "EXPRTOKEN_AXISNAME_SELF", "EXPRTOKEN_LITERAL", "EXPRTOKEN_NUMBER", "EXPRTOKEN_VARIABLE_REFERENCE" };
    
    private static final int INITIAL_TOKEN_COUNT = 256;
    
    private int[] fTokens = new int[256];
    
    private int fTokenCount = 0;
    
    private SymbolTable fSymbolTable;
    
    private Map<String, Integer> fSymbolMapping = new HashMap();
    
    private Map<Integer, String> fTokenNames = new HashMap();
    
    private int fCurrentTokenIndex;
    
    public Tokens(SymbolTable param1SymbolTable) {
      this.fSymbolTable = param1SymbolTable;
      String[] arrayOfString = { 
          "ancestor", "ancestor-or-self", "attribute", "child", "descendant", "descendant-or-self", "following", "following-sibling", "namespace", "parent", 
          "preceding", "preceding-sibling", "self" };
      for (byte b = 0; b < arrayOfString.length; b++)
        this.fSymbolMapping.put(this.fSymbolTable.addSymbol(arrayOfString[b]), Integer.valueOf(b)); 
      this.fTokenNames.put(Integer.valueOf(0), "EXPRTOKEN_OPEN_PAREN");
      this.fTokenNames.put(Integer.valueOf(1), "EXPRTOKEN_CLOSE_PAREN");
      this.fTokenNames.put(Integer.valueOf(2), "EXPRTOKEN_OPEN_BRACKET");
      this.fTokenNames.put(Integer.valueOf(3), "EXPRTOKEN_CLOSE_BRACKET");
      this.fTokenNames.put(Integer.valueOf(4), "EXPRTOKEN_PERIOD");
      this.fTokenNames.put(Integer.valueOf(5), "EXPRTOKEN_DOUBLE_PERIOD");
      this.fTokenNames.put(Integer.valueOf(6), "EXPRTOKEN_ATSIGN");
      this.fTokenNames.put(Integer.valueOf(7), "EXPRTOKEN_COMMA");
      this.fTokenNames.put(Integer.valueOf(8), "EXPRTOKEN_DOUBLE_COLON");
      this.fTokenNames.put(Integer.valueOf(9), "EXPRTOKEN_NAMETEST_ANY");
      this.fTokenNames.put(Integer.valueOf(10), "EXPRTOKEN_NAMETEST_NAMESPACE");
      this.fTokenNames.put(Integer.valueOf(11), "EXPRTOKEN_NAMETEST_QNAME");
      this.fTokenNames.put(Integer.valueOf(12), "EXPRTOKEN_NODETYPE_COMMENT");
      this.fTokenNames.put(Integer.valueOf(13), "EXPRTOKEN_NODETYPE_TEXT");
      this.fTokenNames.put(Integer.valueOf(14), "EXPRTOKEN_NODETYPE_PI");
      this.fTokenNames.put(Integer.valueOf(15), "EXPRTOKEN_NODETYPE_NODE");
      this.fTokenNames.put(Integer.valueOf(16), "EXPRTOKEN_OPERATOR_AND");
      this.fTokenNames.put(Integer.valueOf(17), "EXPRTOKEN_OPERATOR_OR");
      this.fTokenNames.put(Integer.valueOf(18), "EXPRTOKEN_OPERATOR_MOD");
      this.fTokenNames.put(Integer.valueOf(19), "EXPRTOKEN_OPERATOR_DIV");
      this.fTokenNames.put(Integer.valueOf(20), "EXPRTOKEN_OPERATOR_MULT");
      this.fTokenNames.put(Integer.valueOf(21), "EXPRTOKEN_OPERATOR_SLASH");
      this.fTokenNames.put(Integer.valueOf(22), "EXPRTOKEN_OPERATOR_DOUBLE_SLASH");
      this.fTokenNames.put(Integer.valueOf(23), "EXPRTOKEN_OPERATOR_UNION");
      this.fTokenNames.put(Integer.valueOf(24), "EXPRTOKEN_OPERATOR_PLUS");
      this.fTokenNames.put(Integer.valueOf(25), "EXPRTOKEN_OPERATOR_MINUS");
      this.fTokenNames.put(Integer.valueOf(26), "EXPRTOKEN_OPERATOR_EQUAL");
      this.fTokenNames.put(Integer.valueOf(27), "EXPRTOKEN_OPERATOR_NOT_EQUAL");
      this.fTokenNames.put(Integer.valueOf(28), "EXPRTOKEN_OPERATOR_LESS");
      this.fTokenNames.put(Integer.valueOf(29), "EXPRTOKEN_OPERATOR_LESS_EQUAL");
      this.fTokenNames.put(Integer.valueOf(30), "EXPRTOKEN_OPERATOR_GREATER");
      this.fTokenNames.put(Integer.valueOf(31), "EXPRTOKEN_OPERATOR_GREATER_EQUAL");
      this.fTokenNames.put(Integer.valueOf(32), "EXPRTOKEN_FUNCTION_NAME");
      this.fTokenNames.put(Integer.valueOf(33), "EXPRTOKEN_AXISNAME_ANCESTOR");
      this.fTokenNames.put(Integer.valueOf(34), "EXPRTOKEN_AXISNAME_ANCESTOR_OR_SELF");
      this.fTokenNames.put(Integer.valueOf(35), "EXPRTOKEN_AXISNAME_ATTRIBUTE");
      this.fTokenNames.put(Integer.valueOf(36), "EXPRTOKEN_AXISNAME_CHILD");
      this.fTokenNames.put(Integer.valueOf(37), "EXPRTOKEN_AXISNAME_DESCENDANT");
      this.fTokenNames.put(Integer.valueOf(38), "EXPRTOKEN_AXISNAME_DESCENDANT_OR_SELF");
      this.fTokenNames.put(Integer.valueOf(39), "EXPRTOKEN_AXISNAME_FOLLOWING");
      this.fTokenNames.put(Integer.valueOf(40), "EXPRTOKEN_AXISNAME_FOLLOWING_SIBLING");
      this.fTokenNames.put(Integer.valueOf(41), "EXPRTOKEN_AXISNAME_NAMESPACE");
      this.fTokenNames.put(Integer.valueOf(42), "EXPRTOKEN_AXISNAME_PARENT");
      this.fTokenNames.put(Integer.valueOf(43), "EXPRTOKEN_AXISNAME_PRECEDING");
      this.fTokenNames.put(Integer.valueOf(44), "EXPRTOKEN_AXISNAME_PRECEDING_SIBLING");
      this.fTokenNames.put(Integer.valueOf(45), "EXPRTOKEN_AXISNAME_SELF");
      this.fTokenNames.put(Integer.valueOf(46), "EXPRTOKEN_LITERAL");
      this.fTokenNames.put(Integer.valueOf(47), "EXPRTOKEN_NUMBER");
      this.fTokenNames.put(Integer.valueOf(48), "EXPRTOKEN_VARIABLE_REFERENCE");
    }
    
    public String getTokenString(int param1Int) { return (String)this.fTokenNames.get(Integer.valueOf(param1Int)); }
    
    public void addToken(String param1String) {
      Integer integer = null;
      for (Map.Entry entry : this.fTokenNames.entrySet()) {
        if (((String)entry.getValue()).equals(param1String))
          integer = (Integer)entry.getKey(); 
      } 
      if (integer == null) {
        integer = Integer.valueOf(this.fTokenNames.size());
        this.fTokenNames.put(integer, param1String);
      } 
      addToken(integer.intValue());
    }
    
    public void addToken(int param1Int) {
      try {
        this.fTokens[this.fTokenCount] = param1Int;
      } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
        int[] arrayOfInt = this.fTokens;
        this.fTokens = new int[this.fTokenCount << 1];
        System.arraycopy(arrayOfInt, 0, this.fTokens, 0, this.fTokenCount);
        this.fTokens[this.fTokenCount] = param1Int;
      } 
      this.fTokenCount++;
    }
    
    public void rewind() { this.fCurrentTokenIndex = 0; }
    
    public boolean hasMore() { return (this.fCurrentTokenIndex < this.fTokenCount); }
    
    public int nextToken() throws XPathException {
      if (this.fCurrentTokenIndex == this.fTokenCount)
        throw new XPathException("c-general-xpath"); 
      return this.fTokens[this.fCurrentTokenIndex++];
    }
    
    public int peekToken() throws XPathException {
      if (this.fCurrentTokenIndex == this.fTokenCount)
        throw new XPathException("c-general-xpath"); 
      return this.fTokens[this.fCurrentTokenIndex];
    }
    
    public String nextTokenAsString() {
      String str = getTokenString(nextToken());
      if (str == null)
        throw new XPathException("c-general-xpath"); 
      return str;
    }
    
    public void dumpTokens() {
      for (byte b = 0; b < this.fTokenCount; b++) {
        switch (this.fTokens[b]) {
          case 0:
            System.out.print("<OPEN_PAREN/>");
            break;
          case 1:
            System.out.print("<CLOSE_PAREN/>");
            break;
          case 2:
            System.out.print("<OPEN_BRACKET/>");
            break;
          case 3:
            System.out.print("<CLOSE_BRACKET/>");
            break;
          case 4:
            System.out.print("<PERIOD/>");
            break;
          case 5:
            System.out.print("<DOUBLE_PERIOD/>");
            break;
          case 6:
            System.out.print("<ATSIGN/>");
            break;
          case 7:
            System.out.print("<COMMA/>");
            break;
          case 8:
            System.out.print("<DOUBLE_COLON/>");
            break;
          case 9:
            System.out.print("<NAMETEST_ANY/>");
            break;
          case 10:
            System.out.print("<NAMETEST_NAMESPACE");
            System.out.print(" prefix=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          case 11:
            System.out.print("<NAMETEST_QNAME");
            if (this.fTokens[++b] != -1)
              System.out.print(" prefix=\"" + getTokenString(this.fTokens[b]) + "\""); 
            System.out.print(" localpart=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          case 12:
            System.out.print("<NODETYPE_COMMENT/>");
            break;
          case 13:
            System.out.print("<NODETYPE_TEXT/>");
            break;
          case 14:
            System.out.print("<NODETYPE_PI/>");
            break;
          case 15:
            System.out.print("<NODETYPE_NODE/>");
            break;
          case 16:
            System.out.print("<OPERATOR_AND/>");
            break;
          case 17:
            System.out.print("<OPERATOR_OR/>");
            break;
          case 18:
            System.out.print("<OPERATOR_MOD/>");
            break;
          case 19:
            System.out.print("<OPERATOR_DIV/>");
            break;
          case 20:
            System.out.print("<OPERATOR_MULT/>");
            break;
          case 21:
            System.out.print("<OPERATOR_SLASH/>");
            if (b + 1 < this.fTokenCount) {
              System.out.println();
              System.out.print("  ");
            } 
            break;
          case 22:
            System.out.print("<OPERATOR_DOUBLE_SLASH/>");
            break;
          case 23:
            System.out.print("<OPERATOR_UNION/>");
            break;
          case 24:
            System.out.print("<OPERATOR_PLUS/>");
            break;
          case 25:
            System.out.print("<OPERATOR_MINUS/>");
            break;
          case 26:
            System.out.print("<OPERATOR_EQUAL/>");
            break;
          case 27:
            System.out.print("<OPERATOR_NOT_EQUAL/>");
            break;
          case 28:
            System.out.print("<OPERATOR_LESS/>");
            break;
          case 29:
            System.out.print("<OPERATOR_LESS_EQUAL/>");
            break;
          case 30:
            System.out.print("<OPERATOR_GREATER/>");
            break;
          case 31:
            System.out.print("<OPERATOR_GREATER_EQUAL/>");
            break;
          case 32:
            System.out.print("<FUNCTION_NAME");
            if (this.fTokens[++b] != -1)
              System.out.print(" prefix=\"" + getTokenString(this.fTokens[b]) + "\""); 
            System.out.print(" localpart=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          case 33:
            System.out.print("<AXISNAME_ANCESTOR/>");
            break;
          case 34:
            System.out.print("<AXISNAME_ANCESTOR_OR_SELF/>");
            break;
          case 35:
            System.out.print("<AXISNAME_ATTRIBUTE/>");
            break;
          case 36:
            System.out.print("<AXISNAME_CHILD/>");
            break;
          case 37:
            System.out.print("<AXISNAME_DESCENDANT/>");
            break;
          case 38:
            System.out.print("<AXISNAME_DESCENDANT_OR_SELF/>");
            break;
          case 39:
            System.out.print("<AXISNAME_FOLLOWING/>");
            break;
          case 40:
            System.out.print("<AXISNAME_FOLLOWING_SIBLING/>");
            break;
          case 41:
            System.out.print("<AXISNAME_NAMESPACE/>");
            break;
          case 42:
            System.out.print("<AXISNAME_PARENT/>");
            break;
          case 43:
            System.out.print("<AXISNAME_PRECEDING/>");
            break;
          case 44:
            System.out.print("<AXISNAME_PRECEDING_SIBLING/>");
            break;
          case 45:
            System.out.print("<AXISNAME_SELF/>");
            break;
          case 46:
            System.out.print("<LITERAL");
            System.out.print(" value=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          case 47:
            System.out.print("<NUMBER");
            System.out.print(" whole=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print(" part=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          case 48:
            System.out.print("<VARIABLE_REFERENCE");
            if (this.fTokens[++b] != -1)
              System.out.print(" prefix=\"" + getTokenString(this.fTokens[b]) + "\""); 
            System.out.print(" localpart=\"" + getTokenString(this.fTokens[++b]) + "\"");
            System.out.print("/>");
            break;
          default:
            System.out.println("<???/>");
            break;
        } 
      } 
      System.out.println();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\XPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
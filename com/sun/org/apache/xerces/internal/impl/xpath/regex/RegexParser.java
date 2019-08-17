package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

class RegexParser {
  static final int T_CHAR = 0;
  
  static final int T_EOF = 1;
  
  static final int T_OR = 2;
  
  static final int T_STAR = 3;
  
  static final int T_PLUS = 4;
  
  static final int T_QUESTION = 5;
  
  static final int T_LPAREN = 6;
  
  static final int T_RPAREN = 7;
  
  static final int T_DOT = 8;
  
  static final int T_LBRACKET = 9;
  
  static final int T_BACKSOLIDUS = 10;
  
  static final int T_CARET = 11;
  
  static final int T_DOLLAR = 12;
  
  static final int T_LPAREN2 = 13;
  
  static final int T_LOOKAHEAD = 14;
  
  static final int T_NEGATIVELOOKAHEAD = 15;
  
  static final int T_LOOKBEHIND = 16;
  
  static final int T_NEGATIVELOOKBEHIND = 17;
  
  static final int T_INDEPENDENT = 18;
  
  static final int T_SET_OPERATIONS = 19;
  
  static final int T_POSIX_CHARCLASS_START = 20;
  
  static final int T_COMMENT = 21;
  
  static final int T_MODIFIERS = 22;
  
  static final int T_CONDITION = 23;
  
  static final int T_XMLSCHEMA_CC_SUBTRACTION = 24;
  
  int offset;
  
  String regex;
  
  int regexlen;
  
  int options;
  
  ResourceBundle resources;
  
  int chardata;
  
  int nexttoken;
  
  protected static final int S_NORMAL = 0;
  
  protected static final int S_INBRACKETS = 1;
  
  protected static final int S_INXBRACKETS = 2;
  
  int context = 0;
  
  int parenOpened = 1;
  
  int parennumber = 1;
  
  boolean hasBackReferences;
  
  Vector references = null;
  
  int parenCount = 0;
  
  public RegexParser() { setLocale(Locale.getDefault()); }
  
  public RegexParser(Locale paramLocale) { setLocale(paramLocale); }
  
  public void setLocale(Locale paramLocale) {
    try {
      if (paramLocale != null) {
        this.resources = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.xpath.regex.message", paramLocale);
      } else {
        this.resources = SecuritySupport.getResourceBundle("com.sun.org.apache.xerces.internal.impl.xpath.regex.message");
      } 
    } catch (MissingResourceException missingResourceException) {
      throw new RuntimeException("Installation Problem???  Couldn't load messages: " + missingResourceException.getMessage());
    } 
  }
  
  final ParseException ex(String paramString, int paramInt) { return new ParseException(this.resources.getString(paramString), paramInt); }
  
  protected final boolean isSet(int paramInt) { return ((this.options & paramInt) == paramInt); }
  
  Token parse(String paramString, int paramInt) throws ParseException {
    this.options = paramInt;
    this.offset = 0;
    setContext(0);
    this.parennumber = 1;
    this.parenOpened = 1;
    this.hasBackReferences = false;
    this.regex = paramString;
    if (isSet(16))
      this.regex = REUtil.stripExtendedComment(this.regex); 
    this.regexlen = this.regex.length();
    next();
    Token token = parseRegex();
    if (this.offset != this.regexlen)
      throw ex("parser.parse.1", this.offset); 
    if (this.parenCount < 0)
      throw ex("parser.factor.0", this.offset); 
    if (this.references != null) {
      for (byte b = 0; b < this.references.size(); b++) {
        ReferencePosition referencePosition = (ReferencePosition)this.references.elementAt(b);
        if (this.parennumber <= referencePosition.refNumber)
          throw ex("parser.parse.2", referencePosition.position); 
      } 
      this.references.removeAllElements();
    } 
    return token;
  }
  
  protected final void setContext(int paramInt) { this.context = paramInt; }
  
  final int read() { return this.nexttoken; }
  
  final void next() {
    byte b;
    if (this.offset >= this.regexlen) {
      this.chardata = -1;
      this.nexttoken = 1;
      return;
    } 
    char c = this.regex.charAt(this.offset++);
    this.chardata = c;
    if (this.context == 1) {
      byte b1;
      switch (c) {
        case '\\':
          b1 = 10;
          if (this.offset >= this.regexlen)
            throw ex("parser.next.1", this.offset - 1); 
          this.chardata = this.regex.charAt(this.offset++);
          break;
        case '-':
          if (this.offset < this.regexlen && this.regex.charAt(this.offset) == '[') {
            this.offset++;
            b1 = 24;
            break;
          } 
          b1 = 0;
          break;
        case '[':
          if (!isSet(512) && this.offset < this.regexlen && this.regex.charAt(this.offset) == ':') {
            this.offset++;
            b1 = 20;
            break;
          } 
        default:
          if (REUtil.isHighSurrogate(c) && this.offset < this.regexlen) {
            char c1 = this.regex.charAt(this.offset);
            if (REUtil.isLowSurrogate(c1)) {
              this.chardata = REUtil.composeFromSurrogates(c, c1);
              this.offset++;
            } 
          } 
          b1 = 0;
          break;
      } 
      this.nexttoken = b1;
      return;
    } 
    switch (c) {
      case '|':
        b = 2;
        break;
      case '*':
        b = 3;
        break;
      case '+':
        b = 4;
        break;
      case '?':
        b = 5;
        break;
      case ')':
        b = 7;
        break;
      case '.':
        b = 8;
        break;
      case '[':
        b = 9;
        break;
      case '^':
        if (isSet(512)) {
          b = 0;
          break;
        } 
        b = 11;
        break;
      case '$':
        if (isSet(512)) {
          b = 0;
          break;
        } 
        b = 12;
        break;
      case '(':
        b = 6;
        this.parenCount++;
        if (this.offset >= this.regexlen || this.regex.charAt(this.offset) != '?')
          break; 
        if (++this.offset >= this.regexlen)
          throw ex("parser.next.2", this.offset - 1); 
        c = this.regex.charAt(this.offset++);
        switch (c) {
          case ':':
            b = 13;
            break;
          case '=':
            b = 14;
            break;
          case '!':
            b = 15;
            break;
          case '[':
            b = 19;
            break;
          case '>':
            b = 18;
            break;
          case '<':
            if (this.offset >= this.regexlen)
              throw ex("parser.next.2", this.offset - 3); 
            c = this.regex.charAt(this.offset++);
            if (c == '=') {
              b = 16;
              break;
            } 
            if (c == '!') {
              b = 17;
              break;
            } 
            throw ex("parser.next.3", this.offset - 3);
          case '#':
            while (this.offset < this.regexlen) {
              c = this.regex.charAt(this.offset++);
              if (c == ')')
                break; 
            } 
            if (c != ')')
              throw ex("parser.next.4", this.offset - 1); 
            b = 21;
            break;
        } 
        if (c == '-' || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z')) {
          this.offset--;
          b = 22;
          break;
        } 
        if (c == '(') {
          b = 23;
          break;
        } 
        throw ex("parser.next.2", this.offset - 2);
      case '\\':
        b = 10;
        if (this.offset >= this.regexlen)
          throw ex("parser.next.1", this.offset - 1); 
        this.chardata = this.regex.charAt(this.offset++);
        break;
      default:
        b = 0;
        break;
    } 
    this.nexttoken = b;
  }
  
  Token parseRegex() throws ParseException {
    Token token = parseTerm();
    Token.UnionToken unionToken = null;
    while (read() == 2) {
      next();
      if (unionToken == null) {
        unionToken = Token.createUnion();
        unionToken.addChild(token);
        token = unionToken;
      } 
      token.addChild(parseTerm());
    } 
    return token;
  }
  
  Token parseTerm() throws ParseException {
    int i = read();
    Token token = null;
    if (i == 2 || i == 7 || i == 1) {
      token = Token.createEmpty();
    } else {
      token = parseFactor();
      Token.UnionToken unionToken = null;
      while ((i = read()) != 2 && i != 7 && i != 1) {
        if (unionToken == null) {
          unionToken = Token.createConcat();
          unionToken.addChild(token);
          token = unionToken;
        } 
        unionToken.addChild(parseFactor());
      } 
    } 
    if (i == 7)
      this.parenCount--; 
    return token;
  }
  
  Token processCaret() throws ParseException {
    next();
    return Token.token_linebeginning;
  }
  
  Token processDollar() throws ParseException {
    next();
    return Token.token_lineend;
  }
  
  Token processLookahead() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createLook(20, parseRegex());
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processNegativelookahead() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createLook(21, parseRegex());
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processLookbehind() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createLook(22, parseRegex());
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processNegativelookbehind() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createLook(23, parseRegex());
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processBacksolidus_A() throws ParseException {
    next();
    return Token.token_stringbeginning;
  }
  
  Token processBacksolidus_Z() throws ParseException {
    next();
    return Token.token_stringend2;
  }
  
  Token processBacksolidus_z() throws ParseException {
    next();
    return Token.token_stringend;
  }
  
  Token processBacksolidus_b() throws ParseException {
    next();
    return Token.token_wordedge;
  }
  
  Token processBacksolidus_B() throws ParseException {
    next();
    return Token.token_not_wordedge;
  }
  
  Token processBacksolidus_lt() throws ParseException {
    next();
    return Token.token_wordbeginning;
  }
  
  Token processBacksolidus_gt() throws ParseException {
    next();
    return Token.token_wordend;
  }
  
  Token processStar(Token paramToken) throws ParseException {
    next();
    if (read() == 5) {
      next();
      return Token.createNGClosure(paramToken);
    } 
    return Token.createClosure(paramToken);
  }
  
  Token processPlus(Token paramToken) throws ParseException {
    next();
    if (read() == 5) {
      next();
      return Token.createConcat(paramToken, Token.createNGClosure(paramToken));
    } 
    return Token.createConcat(paramToken, Token.createClosure(paramToken));
  }
  
  Token processQuestion(Token paramToken) throws ParseException {
    next();
    Token.UnionToken unionToken = Token.createUnion();
    if (read() == 5) {
      next();
      unionToken.addChild(Token.createEmpty());
      unionToken.addChild(paramToken);
    } else {
      unionToken.addChild(paramToken);
      unionToken.addChild(Token.createEmpty());
    } 
    return unionToken;
  }
  
  boolean checkQuestion(int paramInt) { return (paramInt < this.regexlen && this.regex.charAt(paramInt) == '?'); }
  
  Token processParen() throws ParseException {
    next();
    int i = this.parenOpened++;
    Token.ParenToken parenToken = Token.createParen(parseRegex(), i);
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    this.parennumber++;
    next();
    return parenToken;
  }
  
  Token processParen2() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createParen(parseRegex(), 0);
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processCondition() throws ParseException {
    if (this.offset + 1 >= this.regexlen)
      throw ex("parser.factor.4", this.offset); 
    int i = -1;
    Token token1 = null;
    char c = this.regex.charAt(this.offset);
    if ('1' <= c && c <= '9') {
      i = c - '0';
      int j = i;
      if (this.parennumber <= i)
        throw ex("parser.parse.2", this.offset); 
      while (this.offset + 1 < this.regexlen) {
        c = this.regex.charAt(this.offset + 1);
        if ('1' <= c && c <= '9') {
          i = i * 10 + c - '0';
          if (i < this.parennumber) {
            j = i;
            this.offset++;
          } 
        } 
      } 
      this.hasBackReferences = true;
      if (this.references == null)
        this.references = new Vector(); 
      this.references.addElement(new ReferencePosition(j, this.offset));
      this.offset++;
      if (this.regex.charAt(this.offset) != ')')
        throw ex("parser.factor.1", this.offset); 
      this.offset++;
    } else {
      if (c == '?')
        this.offset--; 
      next();
      token1 = parseFactor();
      switch (token1.type) {
        case 20:
        case 21:
        case 22:
        case 23:
          break;
        case 8:
          if (read() != 7)
            throw ex("parser.factor.1", this.offset - 1); 
          break;
        default:
          throw ex("parser.factor.5", this.offset);
      } 
    } 
    next();
    Token token2 = parseRegex();
    Token token3 = null;
    if (token2.type == 2) {
      if (token2.size() != 2)
        throw ex("parser.factor.6", this.offset); 
      token3 = token2.getChild(1);
      token2 = token2.getChild(0);
    } 
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return Token.createCondition(i, token1, token2, token3);
  }
  
  Token processModifiers() throws ParseException {
    Token.ModifierToken modifierToken;
    int i = 0;
    int j = 0;
    int k = -1;
    while (this.offset < this.regexlen) {
      k = this.regex.charAt(this.offset);
      int m = REUtil.getOptionValue(k);
      if (m == 0)
        break; 
      i |= m;
      this.offset++;
    } 
    if (this.offset >= this.regexlen)
      throw ex("parser.factor.2", this.offset - 1); 
    if (k == 45) {
      this.offset++;
      while (this.offset < this.regexlen) {
        k = this.regex.charAt(this.offset);
        int m = REUtil.getOptionValue(k);
        if (m == 0)
          break; 
        j |= m;
        this.offset++;
      } 
      if (this.offset >= this.regexlen)
        throw ex("parser.factor.2", this.offset - 1); 
    } 
    if (k == 58) {
      this.offset++;
      next();
      modifierToken = Token.createModifierGroup(parseRegex(), i, j);
      if (read() != 7)
        throw ex("parser.factor.1", this.offset - 1); 
      next();
    } else if (k == 41) {
      this.offset++;
      next();
      modifierToken = Token.createModifierGroup(parseRegex(), i, j);
    } else {
      throw ex("parser.factor.3", this.offset);
    } 
    return modifierToken;
  }
  
  Token processIndependent() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createLook(24, parseRegex());
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processBacksolidus_c() throws ParseException {
    char c;
    if (this.offset >= this.regexlen || ((c = this.regex.charAt(this.offset++)) & 0xFFE0) != '@')
      throw ex("parser.atom.1", this.offset - 1); 
    next();
    return Token.createChar(c - '@');
  }
  
  Token processBacksolidus_C() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_i() throws ParseException {
    Token.CharToken charToken = Token.createChar(105);
    next();
    return charToken;
  }
  
  Token processBacksolidus_I() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_g() throws ParseException {
    next();
    return Token.getGraphemePattern();
  }
  
  Token processBacksolidus_X() throws ParseException {
    next();
    return Token.getCombiningCharacterSequence();
  }
  
  Token processBackreference() throws ParseException {
    int i = this.chardata - 48;
    int j = i;
    if (this.parennumber <= i)
      throw ex("parser.parse.2", this.offset - 2); 
    while (this.offset < this.regexlen) {
      char c = this.regex.charAt(this.offset);
      if ('1' <= c && c <= '9') {
        i = i * 10 + c - '0';
        if (i < this.parennumber) {
          this.offset++;
          j = i;
          this.chardata = c;
        } 
      } 
    } 
    Token.StringToken stringToken = Token.createBackReference(j);
    this.hasBackReferences = true;
    if (this.references == null)
      this.references = new Vector(); 
    this.references.addElement(new ReferencePosition(j, this.offset - 2));
    next();
    return stringToken;
  }
  
  Token parseFactor() throws ParseException {
    int i = read();
    switch (i) {
      case 11:
        return processCaret();
      case 12:
        return processDollar();
      case 14:
        return processLookahead();
      case 15:
        return processNegativelookahead();
      case 16:
        return processLookbehind();
      case 17:
        return processNegativelookbehind();
      case 21:
        next();
        return Token.createEmpty();
      case 10:
        switch (this.chardata) {
          case 65:
            return processBacksolidus_A();
          case 90:
            return processBacksolidus_Z();
          case 122:
            return processBacksolidus_z();
          case 98:
            return processBacksolidus_b();
          case 66:
            return processBacksolidus_B();
          case 60:
            return processBacksolidus_lt();
          case 62:
            return processBacksolidus_gt();
        } 
        break;
    } 
    Token token = parseAtom();
    i = read();
    switch (i) {
      case 3:
        return processStar(token);
      case 4:
        return processPlus(token);
      case 5:
        return processQuestion(token);
      case 0:
        if (this.chardata == 123 && this.offset < this.regexlen) {
          int j = this.offset;
          int k = 0;
          int m = -1;
          if ((i = this.regex.charAt(j++)) >= 48 && i <= 57) {
            k = i - 48;
            while (j < this.regexlen && (i = this.regex.charAt(j++)) >= 48 && i <= 57) {
              k = k * 10 + i - 48;
              if (k < 0)
                throw ex("parser.quantifier.5", this.offset); 
            } 
          } else {
            throw ex("parser.quantifier.1", this.offset);
          } 
          m = k;
          if (i == 44) {
            if (j >= this.regexlen)
              throw ex("parser.quantifier.3", this.offset); 
            if ((i = this.regex.charAt(j++)) >= 48 && i <= 57) {
              m = i - 48;
              while (j < this.regexlen && (i = this.regex.charAt(j++)) >= 48 && i <= 57) {
                m = m * 10 + i - 48;
                if (m < 0)
                  throw ex("parser.quantifier.5", this.offset); 
              } 
              if (k > m)
                throw ex("parser.quantifier.4", this.offset); 
            } else {
              m = -1;
            } 
          } 
          if (i != 125)
            throw ex("parser.quantifier.2", this.offset); 
          if (checkQuestion(j)) {
            token = Token.createNGClosure(token);
            this.offset = j + 1;
          } else {
            token = Token.createClosure(token);
            this.offset = j;
          } 
          token.setMin(k);
          token.setMax(m);
          next();
        } 
        break;
    } 
    return token;
  }
  
  Token parseAtom() throws ParseException {
    int j;
    Token token;
    int i = read();
    null = null;
    switch (i) {
      case 6:
        return processParen();
      case 13:
        return processParen2();
      case 23:
        return processCondition();
      case 22:
        return processModifiers();
      case 18:
        return processIndependent();
      case 8:
        next();
        return Token.token_dot;
      case 9:
        return parseCharacterClass(true);
      case 19:
        return parseSetOperations();
      case 10:
        switch (this.chardata) {
          case 68:
          case 83:
          case 87:
          case 100:
          case 115:
          case 119:
            token = getTokenForShorthand(this.chardata);
            next();
            return token;
          case 101:
          case 102:
          case 110:
          case 114:
          case 116:
          case 117:
          case 118:
          case 120:
            j = decodeEscaped();
            if (j < 65536) {
              token = Token.createChar(j);
              break;
            } 
            token = Token.createString(REUtil.decomposeToSurrogates(j));
            break;
          case 99:
            return processBacksolidus_c();
          case 67:
            return processBacksolidus_C();
          case 105:
            return processBacksolidus_i();
          case 73:
            return processBacksolidus_I();
          case 103:
            return processBacksolidus_g();
          case 88:
            return processBacksolidus_X();
          case 49:
          case 50:
          case 51:
          case 52:
          case 53:
          case 54:
          case 55:
          case 56:
          case 57:
            return processBackreference();
          case 80:
          case 112:
            j = this.offset;
            token = processBacksolidus_pP(this.chardata);
            if (token == null)
              throw ex("parser.atom.5", j); 
            break;
          default:
            token = Token.createChar(this.chardata);
            break;
        } 
        next();
        return token;
      case 0:
        if (this.chardata == 93 || this.chardata == 123 || this.chardata == 125)
          throw ex("parser.atom.4", this.offset - 1); 
        token = Token.createChar(this.chardata);
        j = this.chardata;
        next();
        if (REUtil.isHighSurrogate(j) && read() == 0 && REUtil.isLowSurrogate(this.chardata)) {
          char[] arrayOfChar = new char[2];
          arrayOfChar[0] = (char)j;
          arrayOfChar[1] = (char)this.chardata;
          token = Token.createParen(Token.createString(new String(arrayOfChar)), 0);
          next();
        } 
        return token;
    } 
    throw ex("parser.atom.4", this.offset - 1);
  }
  
  protected RangeToken processBacksolidus_pP(int paramInt) throws ParseException {
    next();
    if (read() != 0 || this.chardata != 123)
      throw ex("parser.atom.2", this.offset - 1); 
    boolean bool = (paramInt == 112);
    int i = this.offset;
    int j = this.regex.indexOf('}', i);
    if (j < 0)
      throw ex("parser.atom.3", this.offset); 
    String str = this.regex.substring(i, j);
    this.offset = j + 1;
    return Token.getRange(str, bool, isSet(512));
  }
  
  int processCIinCharacterClass(RangeToken paramRangeToken, int paramInt) { return decodeEscaped(); }
  
  protected RangeToken parseCharacterClass(boolean paramBoolean) throws ParseException {
    RangeToken rangeToken2;
    setContext(1);
    next();
    boolean bool1 = false;
    RangeToken rangeToken1 = null;
    if (read() == 0 && this.chardata == 94) {
      bool1 = true;
      next();
      if (paramBoolean) {
        rangeToken2 = Token.createNRange();
      } else {
        rangeToken1 = Token.createRange();
        rangeToken1.addRange(0, 1114111);
        rangeToken2 = Token.createRange();
      } 
    } else {
      rangeToken2 = Token.createRange();
    } 
    int i;
    boolean bool2;
    for (bool2 = true; (i = read()) != 1 && (i != 0 || this.chardata != 93 || bool2); bool2 = false) {
      int j = this.chardata;
      boolean bool = false;
      if (i == 10) {
        RangeToken rangeToken;
        int k;
        switch (j) {
          case 68:
          case 83:
          case 87:
          case 100:
          case 115:
          case 119:
            rangeToken2.mergeRanges(getTokenForShorthand(j));
            bool = true;
            break;
          case 67:
          case 73:
          case 99:
          case 105:
            j = processCIinCharacterClass(rangeToken2, j);
            if (j < 0)
              bool = true; 
            break;
          case 80:
          case 112:
            k = this.offset;
            rangeToken = processBacksolidus_pP(j);
            if (rangeToken == null)
              throw ex("parser.atom.5", k); 
            rangeToken2.mergeRanges(rangeToken);
            bool = true;
            break;
          default:
            j = decodeEscaped();
            break;
        } 
      } else if (i == 20) {
        int k = this.regex.indexOf(':', this.offset);
        if (k < 0)
          throw ex("parser.cc.1", this.offset); 
        boolean bool3 = true;
        if (this.regex.charAt(this.offset) == '^') {
          this.offset++;
          bool3 = false;
        } 
        String str = this.regex.substring(this.offset, k);
        RangeToken rangeToken = Token.getRange(str, bool3, isSet(512));
        if (rangeToken == null)
          throw ex("parser.cc.3", this.offset); 
        rangeToken2.mergeRanges(rangeToken);
        bool = true;
        if (k + 1 >= this.regexlen || this.regex.charAt(k + 1) != ']')
          throw ex("parser.cc.1", k); 
        this.offset = k + 2;
      } else if (i == 24 && !bool2) {
        if (bool1) {
          bool1 = false;
          if (paramBoolean) {
            rangeToken2 = (RangeToken)Token.complementRanges(rangeToken2);
          } else {
            rangeToken1.subtractRanges(rangeToken2);
            rangeToken2 = rangeToken1;
          } 
        } 
        RangeToken rangeToken = parseCharacterClass(false);
        rangeToken2.subtractRanges(rangeToken);
        if (read() != 0 || this.chardata != 93)
          throw ex("parser.cc.5", this.offset); 
        break;
      } 
      next();
      if (!bool)
        if (read() != 0 || this.chardata != 45) {
          if (!isSet(2) || j > 65535) {
            rangeToken2.addRange(j, j);
          } else {
            addCaseInsensitiveChar(rangeToken2, j);
          } 
        } else {
          if (i == 24)
            throw ex("parser.cc.8", this.offset - 1); 
          next();
          if ((i = read()) == 1)
            throw ex("parser.cc.2", this.offset); 
          if (i == 0 && this.chardata == 93) {
            if (!isSet(2) || j > 65535) {
              rangeToken2.addRange(j, j);
            } else {
              addCaseInsensitiveChar(rangeToken2, j);
            } 
            rangeToken2.addRange(45, 45);
          } else {
            int k = this.chardata;
            if (i == 10)
              k = decodeEscaped(); 
            next();
            if (j > k)
              throw ex("parser.ope.3", this.offset - 1); 
            if (!isSet(2) || (j > 65535 && k > 65535)) {
              rangeToken2.addRange(j, k);
            } else {
              addCaseInsensitiveCharRange(rangeToken2, j, k);
            } 
          } 
        }  
      if (isSet(1024) && read() == 0 && this.chardata == 44)
        next(); 
    } 
    if (read() == 1)
      throw ex("parser.cc.2", this.offset); 
    if (!paramBoolean && bool1) {
      rangeToken1.subtractRanges(rangeToken2);
      rangeToken2 = rangeToken1;
    } 
    rangeToken2.sortRanges();
    rangeToken2.compactRanges();
    setContext(0);
    next();
    return rangeToken2;
  }
  
  protected RangeToken parseSetOperations() throws ParseException {
    RangeToken rangeToken = parseCharacterClass(false);
    int i;
    while ((i = read()) != 7) {
      int j = this.chardata;
      if ((i == 0 && (j == 45 || j == 38)) || i == 4) {
        next();
        if (read() != 9)
          throw ex("parser.ope.1", this.offset - 1); 
        RangeToken rangeToken1 = parseCharacterClass(false);
        if (i == 4) {
          rangeToken.mergeRanges(rangeToken1);
          continue;
        } 
        if (j == 45) {
          rangeToken.subtractRanges(rangeToken1);
          continue;
        } 
        if (j == 38) {
          rangeToken.intersectRanges(rangeToken1);
          continue;
        } 
        throw new RuntimeException("ASSERT");
      } 
      throw ex("parser.ope.2", this.offset - 1);
    } 
    next();
    return rangeToken;
  }
  
  Token getTokenForShorthand(int paramInt) {
    switch (paramInt) {
      case 100:
        return isSet(32) ? Token.getRange("Nd", true) : Token.token_0to9;
      case 68:
        return isSet(32) ? Token.getRange("Nd", false) : Token.token_not_0to9;
      case 119:
        return isSet(32) ? Token.getRange("IsWord", true) : Token.token_wordchars;
      case 87:
        return isSet(32) ? Token.getRange("IsWord", false) : Token.token_not_wordchars;
      case 115:
        return isSet(32) ? Token.getRange("IsSpace", true) : Token.token_spaces;
      case 83:
        return isSet(32) ? Token.getRange("IsSpace", false) : Token.token_not_spaces;
    } 
    throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(paramInt, 16));
  }
  
  int decodeEscaped() {
    int k;
    int j;
    if (read() != 10)
      throw ex("parser.next.1", this.offset - 1); 
    int i = this.chardata;
    switch (i) {
      case 101:
        i = 27;
        break;
      case 102:
        i = 12;
        break;
      case 110:
        i = 10;
        break;
      case 114:
        i = 13;
        break;
      case 116:
        i = 9;
        break;
      case 120:
        next();
        if (read() != 0)
          throw ex("parser.descape.1", this.offset - 1); 
        if (this.chardata == 123) {
          int m = 0;
          int n;
          for (n = 0;; n = n * 16 + m) {
            next();
            if (read() != 0)
              throw ex("parser.descape.1", this.offset - 1); 
            if ((m = hexChar(this.chardata)) < 0)
              break; 
            if (n > n * 16)
              throw ex("parser.descape.2", this.offset - 1); 
          } 
          if (this.chardata != 125)
            throw ex("parser.descape.3", this.offset - 1); 
          if (n > 1114111)
            throw ex("parser.descape.4", this.offset - 1); 
          i = n;
          break;
        } 
        j = 0;
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        i = k;
        break;
      case 117:
        j = 0;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        i = k;
        break;
      case 118:
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        next();
        if (read() != 0 || (j = hexChar(this.chardata)) < 0)
          throw ex("parser.descape.1", this.offset - 1); 
        k = k * 16 + j;
        if (k > 1114111)
          throw ex("parser.descappe.4", this.offset - 1); 
        i = k;
        break;
      case 65:
      case 90:
      case 122:
        throw ex("parser.descape.5", this.offset - 2);
    } 
    return i;
  }
  
  private static final int hexChar(int paramInt) { return (paramInt < 48) ? -1 : ((paramInt > 102) ? -1 : ((paramInt <= 57) ? (paramInt - 48) : ((paramInt < 65) ? -1 : ((paramInt <= 70) ? (paramInt - 65 + 10) : ((paramInt < 97) ? -1 : (paramInt - 97 + 10)))))); }
  
  protected static final void addCaseInsensitiveChar(RangeToken paramRangeToken, int paramInt) {
    int[] arrayOfInt = CaseInsensitiveMap.get(paramInt);
    paramRangeToken.addRange(paramInt, paramInt);
    if (arrayOfInt != null)
      for (boolean bool = false; bool < arrayOfInt.length; bool += true)
        paramRangeToken.addRange(arrayOfInt[bool], arrayOfInt[bool]);  
  }
  
  protected static final void addCaseInsensitiveCharRange(RangeToken paramRangeToken, int paramInt1, int paramInt2) {
    int j;
    int i;
    if (paramInt1 <= paramInt2) {
      i = paramInt1;
      j = paramInt2;
    } else {
      i = paramInt2;
      j = paramInt1;
    } 
    paramRangeToken.addRange(i, j);
    for (int k = i; k <= j; k++) {
      int[] arrayOfInt = CaseInsensitiveMap.get(k);
      if (arrayOfInt != null)
        for (boolean bool = false; bool < arrayOfInt.length; bool += true)
          paramRangeToken.addRange(arrayOfInt[bool], arrayOfInt[bool]);  
    } 
  }
  
  static class ReferencePosition {
    int refNumber;
    
    int position;
    
    ReferencePosition(int param1Int1, int param1Int2) {
      this.refNumber = param1Int1;
      this.position = param1Int2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\RegexParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
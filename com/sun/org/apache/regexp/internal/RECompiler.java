package com.sun.org.apache.regexp.internal;

import java.util.Hashtable;

public class RECompiler {
  char[] instruction = new char[128];
  
  int lenInstruction = 0;
  
  String pattern;
  
  int len;
  
  int idx;
  
  int parens;
  
  static final int NODE_NORMAL = 0;
  
  static final int NODE_NULLABLE = 1;
  
  static final int NODE_TOPLEVEL = 2;
  
  static final int ESC_MASK = 1048560;
  
  static final int ESC_BACKREF = 1048575;
  
  static final int ESC_COMPLEX = 1048574;
  
  static final int ESC_CLASS = 1048573;
  
  int maxBrackets = 10;
  
  static final int bracketUnbounded = -1;
  
  int brackets = 0;
  
  int[] bracketStart = null;
  
  int[] bracketEnd = null;
  
  int[] bracketMin = null;
  
  int[] bracketOpt = null;
  
  static Hashtable hashPOSIX = new Hashtable();
  
  void ensure(int paramInt) {
    int i = this.instruction.length;
    if (this.lenInstruction + paramInt >= i) {
      while (this.lenInstruction + paramInt >= i)
        i *= 2; 
      char[] arrayOfChar = new char[i];
      System.arraycopy(this.instruction, 0, arrayOfChar, 0, this.lenInstruction);
      this.instruction = arrayOfChar;
    } 
  }
  
  void emit(char paramChar) {
    ensure(1);
    this.instruction[this.lenInstruction++] = paramChar;
  }
  
  void nodeInsert(char paramChar, int paramInt1, int paramInt2) {
    ensure(3);
    System.arraycopy(this.instruction, paramInt2, this.instruction, paramInt2 + 3, this.lenInstruction - paramInt2);
    this.instruction[paramInt2 + 0] = paramChar;
    this.instruction[paramInt2 + 1] = (char)paramInt1;
    this.instruction[paramInt2 + 2] = Character.MIN_VALUE;
    this.lenInstruction += 3;
  }
  
  void setNextOfEnd(int paramInt1, int paramInt2) {
    for (char c = this.instruction[paramInt1 + 2]; c != '\000' && paramInt1 < this.lenInstruction; c = this.instruction[paramInt1 + 2]) {
      if (paramInt1 == paramInt2)
        paramInt2 = this.lenInstruction; 
      paramInt1 += c;
    } 
    if (paramInt1 < this.lenInstruction)
      this.instruction[paramInt1 + 2] = (char)(short)(paramInt2 - paramInt1); 
  }
  
  int node(char paramChar, int paramInt) {
    ensure(3);
    this.instruction[this.lenInstruction + 0] = paramChar;
    this.instruction[this.lenInstruction + 1] = (char)paramInt;
    this.instruction[this.lenInstruction + 2] = Character.MIN_VALUE;
    this.lenInstruction += 3;
    return this.lenInstruction - 3;
  }
  
  void internalError() { throw new Error("Internal error!"); }
  
  void syntaxError(String paramString) throws RESyntaxException { throw new RESyntaxException(paramString); }
  
  void allocBrackets() {
    if (this.bracketStart == null) {
      this.bracketStart = new int[this.maxBrackets];
      this.bracketEnd = new int[this.maxBrackets];
      this.bracketMin = new int[this.maxBrackets];
      this.bracketOpt = new int[this.maxBrackets];
      for (byte b = 0; b < this.maxBrackets; b++) {
        this.bracketOpt[b] = -1;
        this.bracketMin[b] = -1;
        this.bracketEnd[b] = -1;
        this.bracketStart[b] = -1;
      } 
    } 
  }
  
  void reallocBrackets() {
    if (this.bracketStart == null)
      allocBrackets(); 
    int i = this.maxBrackets * 2;
    int[] arrayOfInt1 = new int[i];
    int[] arrayOfInt2 = new int[i];
    int[] arrayOfInt3 = new int[i];
    int[] arrayOfInt4 = new int[i];
    for (int j = this.brackets; j < i; j++) {
      arrayOfInt4[j] = -1;
      arrayOfInt3[j] = -1;
      arrayOfInt2[j] = -1;
      arrayOfInt1[j] = -1;
    } 
    System.arraycopy(this.bracketStart, 0, arrayOfInt1, 0, this.brackets);
    System.arraycopy(this.bracketEnd, 0, arrayOfInt2, 0, this.brackets);
    System.arraycopy(this.bracketMin, 0, arrayOfInt3, 0, this.brackets);
    System.arraycopy(this.bracketOpt, 0, arrayOfInt4, 0, this.brackets);
    this.bracketStart = arrayOfInt1;
    this.bracketEnd = arrayOfInt2;
    this.bracketMin = arrayOfInt3;
    this.bracketOpt = arrayOfInt4;
    this.maxBrackets = i;
  }
  
  void bracket() {
    if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '{')
      internalError(); 
    if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx)))
      syntaxError("Expected digit"); 
    StringBuffer stringBuffer = new StringBuffer();
    while (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx)))
      stringBuffer.append(this.pattern.charAt(this.idx++)); 
    try {
      this.bracketMin[this.brackets] = Integer.parseInt(stringBuffer.toString());
    } catch (NumberFormatException numberFormatException) {
      syntaxError("Expected valid number");
    } 
    if (this.idx >= this.len)
      syntaxError("Expected comma or right bracket"); 
    if (this.pattern.charAt(this.idx) == '}') {
      this.idx++;
      this.bracketOpt[this.brackets] = 0;
      return;
    } 
    if (this.idx >= this.len || this.pattern.charAt(this.idx++) != ',')
      syntaxError("Expected comma"); 
    if (this.idx >= this.len)
      syntaxError("Expected comma or right bracket"); 
    if (this.pattern.charAt(this.idx) == '}') {
      this.idx++;
      this.bracketOpt[this.brackets] = -1;
      return;
    } 
    if (this.idx >= this.len || !Character.isDigit(this.pattern.charAt(this.idx)))
      syntaxError("Expected digit"); 
    stringBuffer.setLength(0);
    while (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx)))
      stringBuffer.append(this.pattern.charAt(this.idx++)); 
    try {
      this.bracketOpt[this.brackets] = Integer.parseInt(stringBuffer.toString()) - this.bracketMin[this.brackets];
    } catch (NumberFormatException numberFormatException) {
      syntaxError("Expected valid number");
    } 
    if (this.bracketOpt[this.brackets] < 0)
      syntaxError("Bad range"); 
    if (this.idx >= this.len || this.pattern.charAt(this.idx++) != '}')
      syntaxError("Missing close brace"); 
  }
  
  int escape() throws RESyntaxException {
    char c2;
    char c1;
    if (this.pattern.charAt(this.idx) != '\\')
      internalError(); 
    if (this.idx + 1 == this.len)
      syntaxError("Escape terminates string"); 
    this.idx += 2;
    char c = this.pattern.charAt(this.idx - 1);
    switch (c) {
      case 'B':
      case 'b':
        return 1048574;
      case 'D':
      case 'S':
      case 'W':
      case 'd':
      case 's':
      case 'w':
        return 1048573;
      case 'u':
      case 'x':
        c1 = (c == 'u') ? '\004' : '\002';
        c2 = Character.MIN_VALUE;
        while (this.idx < this.len && c1-- > Character.MIN_VALUE) {
          char c3 = this.pattern.charAt(this.idx);
          if (c3 >= '0' && c3 <= '9') {
            c2 = (c2 << 4) + c3 - '0';
          } else {
            c3 = Character.toLowerCase(c3);
            if (c3 >= 'a' && c3 <= 'f') {
              c2 = (c2 << '\004') + c3 - 'a' + '\n';
            } else {
              syntaxError("Expected " + c1 + " hexadecimal digits after \\" + c);
            } 
          } 
          this.idx++;
        } 
        return c2;
      case 't':
        return 9;
      case 'n':
        return 10;
      case 'r':
        return 13;
      case 'f':
        return 12;
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        if ((this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) || c == '0') {
          c1 = c - '0';
          if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx))) {
            c1 = (c1 << '\003') + this.pattern.charAt(this.idx++) - '0';
            if (this.idx < this.len && Character.isDigit(this.pattern.charAt(this.idx)))
              c1 = (c1 << '\003') + this.pattern.charAt(this.idx++) - '0'; 
          } 
          return c1;
        } 
        return 1048575;
    } 
    return c;
  }
  
  int characterClass() throws RESyntaxException {
    if (this.pattern.charAt(this.idx) != '[')
      internalError(); 
    if (this.idx + 1 >= this.len || this.pattern.charAt(++this.idx) == ']')
      syntaxError("Empty or unterminated class"); 
    if (this.idx < this.len && this.pattern.charAt(this.idx) == ':') {
      int k = ++this.idx;
      while (this.idx < this.len && this.pattern.charAt(this.idx) >= 'a' && this.pattern.charAt(this.idx) <= 'z')
        this.idx++; 
      if (this.idx + 1 < this.len && this.pattern.charAt(this.idx) == ':' && this.pattern.charAt(this.idx + 1) == ']') {
        String str = this.pattern.substring(k, this.idx);
        Character character = (Character)hashPOSIX.get(str);
        if (character != null) {
          this.idx += 2;
          return node('P', character.charValue());
        } 
        syntaxError("Invalid POSIX character class '" + str + "'");
      } 
      syntaxError("Invalid POSIX character class syntax");
    } 
    int i = node('[', 0);
    char c1 = '￿';
    char c2 = c1;
    char c = Character.MIN_VALUE;
    boolean bool = true;
    boolean bool1 = false;
    int j = this.idx;
    byte b1 = 0;
    RERange rERange = new RERange();
    while (this.idx < this.len && this.pattern.charAt(this.idx) != ']') {
      int k;
      switch (this.pattern.charAt(this.idx)) {
        case '^':
          bool = !bool;
          if (this.idx == j)
            rERange.include(0, 65535, true); 
          this.idx++;
          continue;
        case '\\':
          switch (k = escape()) {
            case 1048574:
            case 1048575:
              syntaxError("Bad character class");
            case 1048573:
              if (bool1)
                syntaxError("Bad character class"); 
              switch (this.pattern.charAt(this.idx - 1)) {
                case 'D':
                case 'S':
                case 'W':
                  syntaxError("Bad character class");
                case 's':
                  rERange.include('\t', bool);
                  rERange.include('\r', bool);
                  rERange.include('\f', bool);
                  rERange.include('\n', bool);
                  rERange.include('\b', bool);
                  rERange.include(' ', bool);
                  break;
                case 'w':
                  rERange.include(97, 122, bool);
                  rERange.include(65, 90, bool);
                  rERange.include('_', bool);
                case 'd':
                  rERange.include(48, 57, bool);
                  break;
              } 
              c2 = c1;
              continue;
          } 
          c = (char)k;
          break;
        case '-':
          if (bool1)
            syntaxError("Bad class range"); 
          bool1 = true;
          b1 = (c2 == c1) ? 0 : c2;
          if (this.idx + 1 < this.len && this.pattern.charAt(++this.idx) == ']') {
            c = Character.MAX_VALUE;
            break;
          } 
          continue;
        default:
          c = this.pattern.charAt(this.idx++);
          break;
      } 
      if (bool1) {
        char c3 = c;
        if (b1 >= c3)
          syntaxError("Bad character class"); 
        rERange.include(b1, c3, bool);
        c2 = c1;
        bool1 = false;
        continue;
      } 
      if (this.idx >= this.len || this.pattern.charAt(this.idx) != '-')
        rERange.include(c, bool); 
      c2 = c;
    } 
    if (this.idx == this.len)
      syntaxError("Unterminated character class"); 
    this.idx++;
    this.instruction[i + 1] = (char)rERange.num;
    for (byte b2 = 0; b2 < rERange.num; b2++) {
      emit((char)rERange.minRange[b2]);
      emit((char)rERange.maxRange[b2]);
    } 
    return i;
  }
  
  int atom() throws RESyntaxException {
    int i = node('A', 0);
    byte b;
    for (b = 0; this.idx < this.len; b++) {
      int k;
      int j;
      if (this.idx + 1 < this.len) {
        char c = this.pattern.charAt(this.idx + 1);
        if (this.pattern.charAt(this.idx) == '\\') {
          int m = this.idx;
          escape();
          if (this.idx < this.len)
            c = this.pattern.charAt(this.idx); 
          this.idx = m;
        } 
        switch (c) {
          case '*':
          case '+':
          case '?':
          case '{':
            if (b)
              break; 
            break;
        } 
      } 
      switch (this.pattern.charAt(this.idx)) {
        case '$':
        case '(':
        case ')':
        case '.':
        case '[':
        case ']':
        case '^':
        case '|':
          break;
        case '*':
        case '+':
        case '?':
        case '{':
          if (!b)
            syntaxError("Missing operand to closure"); 
          break;
        case '\\':
          j = this.idx;
          k = escape();
          if ((k & 0xFFFF0) == 1048560) {
            this.idx = j;
            break;
          } 
          emit((char)k);
          b++;
          continue;
      } 
      emit(this.pattern.charAt(this.idx++));
    } 
    if (b == 0)
      internalError(); 
    this.instruction[i + 1] = (char)b;
    return i;
  }
  
  int terminal(int[] paramArrayOfInt) throws RESyntaxException {
    char c;
    int i;
    switch (this.pattern.charAt(this.idx)) {
      case '$':
      case '.':
      case '^':
        return node(this.pattern.charAt(this.idx++), 0);
      case '[':
        return characterClass();
      case '(':
        return expr(paramArrayOfInt);
      case ')':
        syntaxError("Unexpected close paren");
      case '|':
        internalError();
      case ']':
        syntaxError("Mismatched class");
      case '\000':
        syntaxError("Unexpected end of input");
      case '*':
      case '+':
      case '?':
      case '{':
        syntaxError("Missing operand to closure");
      case '\\':
        i = this.idx;
        switch (escape()) {
          case 1048573:
          case 1048574:
            paramArrayOfInt[0] = paramArrayOfInt[0] & 0xFFFFFFFE;
            return node('\\', this.pattern.charAt(this.idx - 1));
          case 1048575:
            c = (char)(this.pattern.charAt(this.idx - 1) - '0');
            if (this.parens <= c)
              syntaxError("Bad backreference"); 
            paramArrayOfInt[0] = paramArrayOfInt[0] | true;
            return node('#', c);
        } 
        this.idx = i;
        paramArrayOfInt[0] = paramArrayOfInt[0] & 0xFFFFFFFE;
        break;
    } 
    paramArrayOfInt[0] = paramArrayOfInt[0] & 0xFFFFFFFE;
    return atom();
  }
  
  int closure(int[] paramArrayOfInt) throws RESyntaxException {
    char c2;
    int i = this.idx;
    int[] arrayOfInt = { 0 };
    int j = terminal(arrayOfInt);
    paramArrayOfInt[0] = paramArrayOfInt[0] | arrayOfInt[0];
    if (this.idx >= this.len)
      return j; 
    boolean bool = true;
    char c1 = this.pattern.charAt(this.idx);
    switch (c1) {
      case '*':
      case '?':
        paramArrayOfInt[0] = paramArrayOfInt[0] | true;
      case '+':
        this.idx++;
      case '{':
        c2 = this.instruction[j + 0];
        if (c2 == '^' || c2 == '$')
          syntaxError("Bad closure operand"); 
        if ((arrayOfInt[0] & true) != 0)
          syntaxError("Closure operand can't be nullable"); 
        break;
    } 
    if (this.idx < this.len && this.pattern.charAt(this.idx) == '?') {
      this.idx++;
      bool = false;
    } 
    if (bool) {
      int m;
      int k;
      switch (c1) {
        case '{':
          c2 = Character.MIN_VALUE;
          allocBrackets();
          for (m = 0; m < this.brackets; m++) {
            if (this.bracketStart[m] == this.idx) {
              c2 = '\001';
              break;
            } 
          } 
          if (c2 == '\000') {
            if (this.brackets >= this.maxBrackets)
              reallocBrackets(); 
            this.bracketStart[this.brackets] = this.idx;
            bracket();
            this.bracketEnd[this.brackets] = this.idx;
            m = this.brackets++;
          } 
          this.bracketMin[m] = this.bracketMin[m] - 1;
          if (this.bracketMin[m] > 0) {
            if (this.bracketMin[m] > 0 || this.bracketOpt[m] != 0) {
              for (byte b = 0; b < this.brackets; b++) {
                if (b != m && this.bracketStart[b] < this.idx && this.bracketStart[b] >= i) {
                  this.brackets--;
                  this.bracketStart[b] = this.bracketStart[this.brackets];
                  this.bracketEnd[b] = this.bracketEnd[this.brackets];
                  this.bracketMin[b] = this.bracketMin[this.brackets];
                  this.bracketOpt[b] = this.bracketOpt[this.brackets];
                } 
              } 
              this.idx = i;
              break;
            } 
            this.idx = this.bracketEnd[m];
            break;
          } 
          if (this.bracketOpt[m] == -1) {
            c1 = '*';
            this.bracketOpt[m] = 0;
            this.idx = this.bracketEnd[m];
          } else {
            this.bracketOpt[m] = this.bracketOpt[m] - 1;
            if (this.bracketOpt[m] > 0) {
              if (this.bracketOpt[m] > 0) {
                this.idx = i;
              } else {
                this.idx = this.bracketEnd[m];
              } 
              c1 = '?';
            } else {
              this.lenInstruction = j;
              node('N', 0);
              this.idx = this.bracketEnd[m];
              break;
            } 
          } 
        case '*':
        case '?':
          if (!bool)
            break; 
          if (c1 == '?') {
            nodeInsert('|', 0, j);
            setNextOfEnd(j, node('|', 0));
            int n = node('N', 0);
            setNextOfEnd(j, n);
            setNextOfEnd(j + 3, n);
          } 
          if (c1 == '*') {
            nodeInsert('|', 0, j);
            setNextOfEnd(j + 3, node('|', 0));
            setNextOfEnd(j + 3, node('G', 0));
            setNextOfEnd(j + 3, j);
            setNextOfEnd(j, node('|', 0));
            setNextOfEnd(j, node('N', 0));
          } 
          break;
        case '+':
          k = node('|', 0);
          setNextOfEnd(j, k);
          setNextOfEnd(node('G', 0), j);
          setNextOfEnd(k, node('|', 0));
          setNextOfEnd(j, node('N', 0));
          break;
      } 
    } else {
      setNextOfEnd(j, node('E', 0));
      switch (c1) {
        case '?':
          nodeInsert('/', 0, j);
          break;
        case '*':
          nodeInsert('8', 0, j);
          break;
        case '+':
          nodeInsert('=', 0, j);
          break;
      } 
      setNextOfEnd(j, this.lenInstruction);
    } 
    return j;
  }
  
  int branch(int[] paramArrayOfInt) throws RESyntaxException {
    int i = node('|', 0);
    int j = -1;
    int[] arrayOfInt = new int[1];
    boolean bool = true;
    while (this.idx < this.len && this.pattern.charAt(this.idx) != '|' && this.pattern.charAt(this.idx) != ')') {
      arrayOfInt[0] = 0;
      int k = closure(arrayOfInt);
      if (arrayOfInt[0] == 0)
        bool = false; 
      if (j != -1)
        setNextOfEnd(j, k); 
      j = k;
    } 
    if (j == -1)
      node('N', 0); 
    if (bool)
      paramArrayOfInt[0] = paramArrayOfInt[0] | true; 
    return i;
  }
  
  int expr(int[] paramArrayOfInt) throws RESyntaxException {
    int m;
    byte b = -1;
    int i = -1;
    int j = this.parens;
    if ((paramArrayOfInt[0] & 0x2) == 0 && this.pattern.charAt(this.idx) == '(')
      if (this.idx + 2 < this.len && this.pattern.charAt(this.idx + 1) == '?' && this.pattern.charAt(this.idx + 2) == ':') {
        b = 2;
        this.idx += 3;
        i = node('<', 0);
      } else {
        b = 1;
        this.idx++;
        i = node('(', this.parens++);
      }  
    paramArrayOfInt[0] = paramArrayOfInt[0] & 0xFFFFFFFD;
    int k = branch(paramArrayOfInt);
    if (i == -1) {
      i = k;
    } else {
      setNextOfEnd(i, k);
    } 
    while (this.idx < this.len && this.pattern.charAt(this.idx) == '|') {
      this.idx++;
      k = branch(paramArrayOfInt);
      setNextOfEnd(i, k);
    } 
    if (b > 0) {
      if (this.idx < this.len && this.pattern.charAt(this.idx) == ')') {
        this.idx++;
      } else {
        syntaxError("Missing close paren");
      } 
      if (b == 1) {
        m = node(')', j);
      } else {
        m = node('>', 0);
      } 
    } else {
      m = node('E', 0);
    } 
    setNextOfEnd(i, m);
    int n = i;
    char c = this.instruction[n + 2];
    while (c != '\000' && n < this.lenInstruction) {
      if (this.instruction[n + 0] == '|')
        setNextOfEnd(n + 3, m); 
      c = this.instruction[n + 2];
      n += c;
    } 
    return i;
  }
  
  public REProgram compile(String paramString) throws RESyntaxException {
    this.pattern = paramString;
    this.len = paramString.length();
    this.idx = 0;
    this.lenInstruction = 0;
    this.parens = 1;
    this.brackets = 0;
    int[] arrayOfInt = { 2 };
    expr(arrayOfInt);
    if (this.idx != this.len) {
      if (paramString.charAt(this.idx) == ')')
        syntaxError("Unmatched close paren"); 
      syntaxError("Unexpected input remains");
    } 
    char[] arrayOfChar = new char[this.lenInstruction];
    System.arraycopy(this.instruction, 0, arrayOfChar, 0, this.lenInstruction);
    return new REProgram(this.parens, arrayOfChar);
  }
  
  static  {
    hashPOSIX.put("alnum", new Character('w'));
    hashPOSIX.put("alpha", new Character('a'));
    hashPOSIX.put("blank", new Character('b'));
    hashPOSIX.put("cntrl", new Character('c'));
    hashPOSIX.put("digit", new Character('d'));
    hashPOSIX.put("graph", new Character('g'));
    hashPOSIX.put("lower", new Character('l'));
    hashPOSIX.put("print", new Character('p'));
    hashPOSIX.put("punct", new Character('!'));
    hashPOSIX.put("space", new Character('s'));
    hashPOSIX.put("upper", new Character('u'));
    hashPOSIX.put("xdigit", new Character('x'));
    hashPOSIX.put("javastart", new Character('j'));
    hashPOSIX.put("javapart", new Character('k'));
  }
  
  class RERange {
    int size = 16;
    
    int[] minRange = new int[this.size];
    
    int[] maxRange = new int[this.size];
    
    int num = 0;
    
    void delete(int param1Int) {
      if (this.num == 0 || param1Int >= this.num)
        return; 
      while (++param1Int < this.num) {
        if (param1Int - 1 >= 0) {
          this.minRange[param1Int - 1] = this.minRange[param1Int];
          this.maxRange[param1Int - 1] = this.maxRange[param1Int];
        } 
      } 
      this.num--;
    }
    
    void merge(int param1Int1, int param1Int2) {
      for (byte b = 0; b < this.num; b++) {
        if (param1Int1 >= this.minRange[b] && param1Int2 <= this.maxRange[b])
          return; 
        if (param1Int1 <= this.minRange[b] && param1Int2 >= this.maxRange[b]) {
          delete(b);
          merge(param1Int1, param1Int2);
          return;
        } 
        if (param1Int1 >= this.minRange[b] && param1Int1 <= this.maxRange[b]) {
          delete(b);
          param1Int1 = this.minRange[b];
          merge(param1Int1, param1Int2);
          return;
        } 
        if (param1Int2 >= this.minRange[b] && param1Int2 <= this.maxRange[b]) {
          delete(b);
          param1Int2 = this.maxRange[b];
          merge(param1Int1, param1Int2);
          return;
        } 
      } 
      if (this.num >= this.size) {
        this.size *= 2;
        int[] arrayOfInt1 = new int[this.size];
        int[] arrayOfInt2 = new int[this.size];
        System.arraycopy(this.minRange, 0, arrayOfInt1, 0, this.num);
        System.arraycopy(this.maxRange, 0, arrayOfInt2, 0, this.num);
        this.minRange = arrayOfInt1;
        this.maxRange = arrayOfInt2;
      } 
      this.minRange[this.num] = param1Int1;
      this.maxRange[this.num] = param1Int2;
      this.num++;
    }
    
    void remove(int param1Int1, int param1Int2) {
      for (byte b = 0; b < this.num; b++) {
        if (this.minRange[b] >= param1Int1 && this.maxRange[b] <= param1Int2) {
          delete(b);
          b--;
          return;
        } 
        if (param1Int1 >= this.minRange[b] && param1Int2 <= this.maxRange[b]) {
          int i = this.minRange[b];
          int j = this.maxRange[b];
          delete(b);
          if (i < param1Int1)
            merge(i, param1Int1 - 1); 
          if (param1Int2 < j)
            merge(param1Int2 + 1, j); 
          return;
        } 
        if (this.minRange[b] >= param1Int1 && this.minRange[b] <= param1Int2) {
          this.minRange[b] = param1Int2 + 1;
          return;
        } 
        if (this.maxRange[b] >= param1Int1 && this.maxRange[b] <= param1Int2) {
          this.maxRange[b] = param1Int1 - 1;
          return;
        } 
      } 
    }
    
    void include(int param1Int1, int param1Int2, boolean param1Boolean) {
      if (param1Boolean) {
        merge(param1Int1, param1Int2);
      } else {
        remove(param1Int1, param1Int2);
      } 
    }
    
    void include(char param1Char, boolean param1Boolean) { include(param1Char, param1Char, param1Boolean); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\regexp\internal\RECompiler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
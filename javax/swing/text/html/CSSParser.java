package javax.swing.text.html;

import java.io.IOException;
import java.io.Reader;

class CSSParser {
  private static final int IDENTIFIER = 1;
  
  private static final int BRACKET_OPEN = 2;
  
  private static final int BRACKET_CLOSE = 3;
  
  private static final int BRACE_OPEN = 4;
  
  private static final int BRACE_CLOSE = 5;
  
  private static final int PAREN_OPEN = 6;
  
  private static final int PAREN_CLOSE = 7;
  
  private static final int END = -1;
  
  private static final char[] charMapping = { Character.MIN_VALUE, Character.MIN_VALUE, '[', ']', '{', '}', '(', ')', Character.MIN_VALUE };
  
  private boolean didPushChar;
  
  private int pushedChar;
  
  private StringBuffer unitBuffer = new StringBuffer();
  
  private int[] unitStack = new int[2];
  
  private int stackCount;
  
  private Reader reader;
  
  private boolean encounteredRuleSet;
  
  private CSSParserCallback callback;
  
  private char[] tokenBuffer = new char[80];
  
  private int tokenBufferLength;
  
  private boolean readWS;
  
  void parse(Reader paramReader, CSSParserCallback paramCSSParserCallback, boolean paramBoolean) throws IOException {
    this.callback = paramCSSParserCallback;
    this.stackCount = this.tokenBufferLength = 0;
    this.reader = paramReader;
    this.encounteredRuleSet = false;
    try {
      if (paramBoolean) {
        parseDeclarationBlock();
      } else {
        while (getNextStatement());
      } 
    } finally {
      paramCSSParserCallback = null;
      paramReader = null;
    } 
  }
  
  private boolean getNextStatement() throws IOException {
    this.unitBuffer.setLength(0);
    int i = nextToken(false);
    switch (i) {
      case 1:
        if (this.tokenBufferLength > 0)
          if (this.tokenBuffer[0] == '@') {
            parseAtRule();
          } else {
            this.encounteredRuleSet = true;
            parseRuleSet();
          }  
        return true;
      case 2:
      case 4:
      case 6:
        parseTillClosed(i);
        return true;
      case 3:
      case 5:
      case 7:
        throw new RuntimeException("Unexpected top level block close");
      case -1:
        return false;
    } 
    return true;
  }
  
  private void parseAtRule() {
    boolean bool1 = false;
    boolean bool2 = (this.tokenBufferLength == 7 && this.tokenBuffer[0] == '@' && this.tokenBuffer[1] == 'i' && this.tokenBuffer[2] == 'm' && this.tokenBuffer[3] == 'p' && this.tokenBuffer[4] == 'o' && this.tokenBuffer[5] == 'r' && this.tokenBuffer[6] == 't') ? 1 : 0;
    this.unitBuffer.setLength(0);
    while (!bool1) {
      int j;
      int i = nextToken(';');
      switch (i) {
        case 1:
          if (this.tokenBufferLength > 0 && this.tokenBuffer[this.tokenBufferLength - 1] == ';') {
            this.tokenBufferLength--;
            bool1 = true;
          } 
          if (this.tokenBufferLength > 0) {
            if (this.unitBuffer.length() > 0 && this.readWS)
              this.unitBuffer.append(' '); 
            this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
          } 
        case 4:
          if (this.unitBuffer.length() > 0 && this.readWS)
            this.unitBuffer.append(' '); 
          this.unitBuffer.append(charMapping[i]);
          parseTillClosed(i);
          bool1 = true;
          j = readWS();
          if (j != -1 && j != 59)
            pushChar(j); 
        case 2:
        case 6:
          this.unitBuffer.append(charMapping[i]);
          parseTillClosed(i);
        case 3:
        case 5:
        case 7:
          throw new RuntimeException("Unexpected close in @ rule");
        case -1:
          bool1 = true;
      } 
    } 
    if (bool2 && !this.encounteredRuleSet)
      this.callback.handleImport(this.unitBuffer.toString()); 
  }
  
  private void parseRuleSet() {
    if (parseSelectors()) {
      this.callback.startRule();
      parseDeclarationBlock();
      this.callback.endRule();
    } 
  }
  
  private boolean parseSelectors() throws IOException {
    if (this.tokenBufferLength > 0)
      this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength)); 
    this.unitBuffer.setLength(0);
    while (true) {
      int i;
      while ((i = nextToken(false)) == 1) {
        if (this.tokenBufferLength > 0)
          this.callback.handleSelector(new String(this.tokenBuffer, 0, this.tokenBufferLength)); 
      } 
      switch (i) {
        case 4:
          return true;
        case 2:
        case 6:
          parseTillClosed(i);
          this.unitBuffer.setLength(0);
        case 3:
        case 5:
        case 7:
          throw new RuntimeException("Unexpected block close in selector");
        case -1:
          break;
      } 
    } 
    return false;
  }
  
  private void parseDeclarationBlock() {
    while (true) {
      int i = parseDeclaration();
      switch (i) {
        case -1:
        case 5:
          return;
        case 3:
        case 7:
          break;
      } 
    } 
    throw new RuntimeException("Unexpected close in declaration block");
  }
  
  private int parseDeclaration() throws IOException {
    int i;
    if ((i = parseIdentifiers(':', false)) != 1)
      return i; 
    for (int j = this.unitBuffer.length() - 1; j >= 0; j--)
      this.unitBuffer.setCharAt(j, Character.toLowerCase(this.unitBuffer.charAt(j))); 
    this.callback.handleProperty(this.unitBuffer.toString());
    i = parseIdentifiers(';', true);
    this.callback.handleValue(this.unitBuffer.toString());
    return i;
  }
  
  private int parseIdentifiers(char paramChar, boolean paramBoolean) throws IOException {
    int i;
    this.unitBuffer.setLength(0);
    while (true) {
      int j;
      i = nextToken(paramChar);
      switch (i) {
        case 1:
          if (this.tokenBufferLength > 0) {
            if (this.tokenBuffer[this.tokenBufferLength - 1] == paramChar) {
              if (--this.tokenBufferLength > 0) {
                if (this.readWS && this.unitBuffer.length() > 0)
                  this.unitBuffer.append(' '); 
                this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
              } 
              return 1;
            } 
            if (this.readWS && this.unitBuffer.length() > 0)
              this.unitBuffer.append(' '); 
            this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength);
          } 
        case 2:
        case 4:
        case 6:
          j = this.unitBuffer.length();
          if (paramBoolean)
            this.unitBuffer.append(charMapping[i]); 
          parseTillClosed(i);
          if (!paramBoolean)
            this.unitBuffer.setLength(j); 
        case -1:
        case 3:
        case 5:
        case 7:
          break;
      } 
    } 
    return i;
  }
  
  private void parseTillClosed(int paramInt) throws IOException {
    boolean bool = false;
    startBlock(paramInt);
    while (!bool) {
      int i = nextToken(false);
      switch (i) {
        case 1:
          if (this.unitBuffer.length() > 0 && this.readWS)
            this.unitBuffer.append(' '); 
          if (this.tokenBufferLength > 0)
            this.unitBuffer.append(this.tokenBuffer, 0, this.tokenBufferLength); 
        case 2:
        case 4:
        case 6:
          if (this.unitBuffer.length() > 0 && this.readWS)
            this.unitBuffer.append(' '); 
          this.unitBuffer.append(charMapping[i]);
          startBlock(i);
        case 3:
        case 5:
        case 7:
          if (this.unitBuffer.length() > 0 && this.readWS)
            this.unitBuffer.append(' '); 
          this.unitBuffer.append(charMapping[i]);
          endBlock(i);
          if (!inBlock())
            bool = true; 
        case -1:
          throw new RuntimeException("Unclosed block");
      } 
    } 
  }
  
  private int nextToken(char paramChar) throws IOException {
    this.readWS = false;
    int i = readWS();
    switch (i) {
      case 39:
        readTill('\'');
        if (this.tokenBufferLength > 0)
          this.tokenBufferLength--; 
        return 1;
      case 34:
        readTill('"');
        if (this.tokenBufferLength > 0)
          this.tokenBufferLength--; 
        return 1;
      case 91:
        return 2;
      case 93:
        return 3;
      case 123:
        return 4;
      case 125:
        return 5;
      case 40:
        return 6;
      case 41:
        return 7;
      case -1:
        return -1;
    } 
    pushChar(i);
    getIdentifier(paramChar);
    return 1;
  }
  
  private boolean getIdentifier(char paramChar) throws IOException {
    boolean bool1 = false;
    boolean bool2 = false;
    byte b = 0;
    int i = 0;
    char c = paramChar;
    int j = 0;
    this.tokenBufferLength = 0;
    while (!bool2) {
      byte b1;
      int k = readChar();
      switch (k) {
        case 92:
          b1 = 1;
          break;
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
          b1 = 2;
          j = k - 48;
          break;
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
          b1 = 2;
          j = k - 97 + 10;
          break;
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
          b1 = 2;
          j = k - 65 + 10;
          break;
        case 9:
        case 10:
        case 13:
        case 32:
        case 34:
        case 39:
        case 40:
        case 41:
        case 91:
        case 93:
        case 123:
        case 125:
          b1 = 3;
          break;
        case 47:
          b1 = 4;
          break;
        case -1:
          bool2 = true;
          b1 = 0;
          break;
        default:
          b1 = 0;
          break;
      } 
      if (bool1) {
        if (b1 == 2) {
          i = i * 16 + j;
          if (++b == 4) {
            bool1 = false;
            append((char)i);
          } 
          continue;
        } 
        bool1 = false;
        if (b > 0) {
          append((char)i);
          pushChar(k);
          continue;
        } 
        if (!bool2)
          append((char)k); 
        continue;
      } 
      if (!bool2) {
        if (b1 == 1) {
          bool1 = true;
          i = b = 0;
          continue;
        } 
        if (b1 == 3) {
          bool2 = true;
          pushChar(k);
          continue;
        } 
        if (b1 == 4) {
          k = readChar();
          if (k == 42) {
            bool2 = true;
            readComment();
            this.readWS = true;
            continue;
          } 
          append('/');
          if (k == -1) {
            bool2 = true;
            continue;
          } 
          pushChar(k);
          continue;
        } 
        append((char)k);
        if (k == c)
          bool2 = true; 
      } 
    } 
    return (this.tokenBufferLength > 0);
  }
  
  private void readTill(char paramChar) throws IOException {
    boolean bool1 = false;
    byte b = 0;
    int i = 0;
    boolean bool2 = false;
    char c = paramChar;
    int j = 0;
    this.tokenBufferLength = 0;
    while (!bool2) {
      byte b1;
      int k = readChar();
      switch (k) {
        case 92:
          b1 = 1;
          break;
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 54:
        case 55:
        case 56:
        case 57:
          b1 = 2;
          j = k - 48;
          break;
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
          b1 = 2;
          j = k - 97 + 10;
          break;
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
          b1 = 2;
          j = k - 65 + 10;
          break;
        case -1:
          throw new RuntimeException("Unclosed " + paramChar);
        default:
          b1 = 0;
          break;
      } 
      if (bool1) {
        if (b1 == 2) {
          i = i * 16 + j;
          if (++b == 4) {
            bool1 = false;
            append((char)i);
          } 
          continue;
        } 
        if (b > 0) {
          append((char)i);
          if (b1 == 1) {
            bool1 = true;
            i = b = 0;
            continue;
          } 
          if (k == c)
            bool2 = true; 
          append((char)k);
          bool1 = false;
          continue;
        } 
        append((char)k);
        bool1 = false;
        continue;
      } 
      if (b1 == 1) {
        bool1 = true;
        i = b = 0;
        continue;
      } 
      if (k == c)
        bool2 = true; 
      append((char)k);
    } 
  }
  
  private void append(char paramChar) throws IOException {
    if (this.tokenBufferLength == this.tokenBuffer.length) {
      char[] arrayOfChar = new char[this.tokenBuffer.length * 2];
      System.arraycopy(this.tokenBuffer, 0, arrayOfChar, 0, this.tokenBuffer.length);
      this.tokenBuffer = arrayOfChar;
    } 
    this.tokenBuffer[this.tokenBufferLength++] = paramChar;
  }
  
  private void readComment() {
    while (true) {
      int i = readChar();
      switch (i) {
        case -1:
          throw new RuntimeException("Unclosed comment");
        case 42:
          i = readChar();
          if (i == 47)
            return; 
          if (i == -1)
            throw new RuntimeException("Unclosed comment"); 
          pushChar(i);
      } 
    } 
  }
  
  private void startBlock(int paramInt) throws IOException {
    if (this.stackCount == this.unitStack.length) {
      int[] arrayOfInt = new int[this.stackCount * 2];
      System.arraycopy(this.unitStack, 0, arrayOfInt, 0, this.stackCount);
      this.unitStack = arrayOfInt;
    } 
    this.unitStack[this.stackCount++] = paramInt;
  }
  
  private void endBlock(int paramInt) throws IOException {
    byte b;
    switch (paramInt) {
      case 3:
        b = 2;
        break;
      case 5:
        b = 4;
        break;
      case 7:
        b = 6;
        break;
      default:
        b = -1;
        break;
    } 
    if (this.stackCount > 0 && this.unitStack[this.stackCount - 1] == b) {
      this.stackCount--;
    } else {
      throw new RuntimeException("Unmatched block");
    } 
  }
  
  private boolean inBlock() throws IOException { return (this.stackCount > 0); }
  
  private int readWS() throws IOException {
    int i;
    while ((i = readChar()) != -1 && Character.isWhitespace((char)i))
      this.readWS = true; 
    return i;
  }
  
  private int readChar() throws IOException {
    if (this.didPushChar) {
      this.didPushChar = false;
      return this.pushedChar;
    } 
    return this.reader.read();
  }
  
  private void pushChar(int paramInt) throws IOException {
    if (this.didPushChar)
      throw new RuntimeException("Can not handle look ahead of more than one character"); 
    this.didPushChar = true;
    this.pushedChar = paramInt;
  }
  
  static interface CSSParserCallback {
    void handleImport(String param1String);
    
    void handleSelector(String param1String);
    
    void startRule();
    
    void handleProperty(String param1String);
    
    void handleValue(String param1String);
    
    void endRule();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\CSSParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
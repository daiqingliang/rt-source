package javax.swing.text.html.parser;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Reader;
import java.util.Vector;
import javax.swing.text.ChangedCharSetException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;

public class Parser implements DTDConstants {
  private char[] text = new char[1024];
  
  private int textpos = 0;
  
  private TagElement last;
  
  private boolean space;
  
  private char[] str = new char[128];
  
  private int strpos = 0;
  
  protected DTD dtd = null;
  
  private int ch;
  
  private int ln;
  
  private Reader in;
  
  private Element recent;
  
  private TagStack stack;
  
  private boolean skipTag = false;
  
  private TagElement lastFormSent = null;
  
  private SimpleAttributeSet attributes = new SimpleAttributeSet();
  
  private boolean seenHtml = false;
  
  private boolean seenHead = false;
  
  private boolean seenBody = false;
  
  private boolean ignoreSpace;
  
  protected boolean strict = false;
  
  private int crlfCount;
  
  private int crCount;
  
  private int lfCount;
  
  private int currentBlockStartPos;
  
  private int lastBlockStartPos;
  
  private static final char[] cp1252Map = { 
      '‚', 'ƒ', '„', '…', '†', '‡', 'ˆ', '‰', 'Š', '‹', 
      'Œ', '', '', '', '', '‘', '’', '“', '”', '•', 
      '–', '—', '˜', '™', 'š', '›', 'œ', '', '', 'Ÿ' };
  
  private static final String START_COMMENT = "<!--";
  
  private static final String END_COMMENT = "-->";
  
  private static final char[] SCRIPT_END_TAG = "</script>".toCharArray();
  
  private static final char[] SCRIPT_END_TAG_UPPER_CASE = "</SCRIPT>".toCharArray();
  
  private char[] buf = new char[1];
  
  private int pos;
  
  private int len;
  
  private int currentPosition;
  
  public Parser(DTD paramDTD) { this.dtd = paramDTD; }
  
  protected int getCurrentLine() { return this.ln; }
  
  int getBlockStartPosition() { return Math.max(0, this.lastBlockStartPos - 1); }
  
  protected TagElement makeTag(Element paramElement, boolean paramBoolean) { return new TagElement(paramElement, paramBoolean); }
  
  protected TagElement makeTag(Element paramElement) { return makeTag(paramElement, false); }
  
  protected SimpleAttributeSet getAttributes() { return this.attributes; }
  
  protected void flushAttributes() { this.attributes.removeAttributes(this.attributes); }
  
  protected void handleText(char[] paramArrayOfChar) {}
  
  protected void handleTitle(char[] paramArrayOfChar) { handleText(paramArrayOfChar); }
  
  protected void handleComment(char[] paramArrayOfChar) {}
  
  protected void handleEOFInComment() {
    int i = strIndexOf('\n');
    if (i >= 0) {
      handleComment(getChars(0, i));
      try {
        this.in.close();
        this.in = new CharArrayReader(getChars(i + 1));
        this.ch = 62;
      } catch (IOException iOException) {
        error("ioexception");
      } 
      resetStrBuffer();
    } else {
      error("eof.comment");
    } 
  }
  
  protected void handleEmptyTag(TagElement paramTagElement) throws ChangedCharSetException {}
  
  protected void handleStartTag(TagElement paramTagElement) throws ChangedCharSetException {}
  
  protected void handleEndTag(TagElement paramTagElement) throws ChangedCharSetException {}
  
  protected void handleError(int paramInt, String paramString) {}
  
  void handleText(TagElement paramTagElement) throws ChangedCharSetException {
    if (paramTagElement.breaksFlow()) {
      this.space = false;
      if (!this.strict)
        this.ignoreSpace = true; 
    } 
    if (this.textpos == 0 && (!this.space || this.stack == null || this.last.breaksFlow() || !this.stack.advance(this.dtd.pcdata))) {
      this.last = paramTagElement;
      this.space = false;
      this.lastBlockStartPos = this.currentBlockStartPos;
      return;
    } 
    if (this.space) {
      if (!this.ignoreSpace) {
        if (this.textpos + 1 > this.text.length) {
          char[] arrayOfChar1 = new char[this.text.length + 200];
          System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
          this.text = arrayOfChar1;
        } 
        this.text[this.textpos++] = ' ';
        if (!this.strict && !paramTagElement.getElement().isEmpty())
          this.ignoreSpace = true; 
      } 
      this.space = false;
    } 
    char[] arrayOfChar = new char[this.textpos];
    System.arraycopy(this.text, 0, arrayOfChar, 0, this.textpos);
    if (paramTagElement.getElement().getName().equals("title")) {
      handleTitle(arrayOfChar);
    } else {
      handleText(arrayOfChar);
    } 
    this.lastBlockStartPos = this.currentBlockStartPos;
    this.textpos = 0;
    this.last = paramTagElement;
    this.space = false;
  }
  
  protected void error(String paramString1, String paramString2, String paramString3, String paramString4) { handleError(this.ln, paramString1 + " " + paramString2 + " " + paramString3 + " " + paramString4); }
  
  protected void error(String paramString1, String paramString2, String paramString3) { error(paramString1, paramString2, paramString3, "?"); }
  
  protected void error(String paramString1, String paramString2) { error(paramString1, paramString2, "?", "?"); }
  
  protected void error(String paramString) { error(paramString, "?", "?", "?"); }
  
  protected void startTag(TagElement paramTagElement) throws ChangedCharSetException {
    Element element = paramTagElement.getElement();
    if (!element.isEmpty() || (this.last != null && !this.last.breaksFlow()) || this.textpos != 0) {
      handleText(paramTagElement);
    } else {
      this.last = paramTagElement;
      this.space = false;
    } 
    this.lastBlockStartPos = this.currentBlockStartPos;
    for (AttributeList attributeList = element.atts; attributeList != null; attributeList = attributeList.next) {
      if (attributeList.modifier == 2 && (this.attributes.isEmpty() || (!this.attributes.isDefined(attributeList.name) && !this.attributes.isDefined(HTML.getAttributeKey(attributeList.name)))))
        error("req.att ", attributeList.getName(), element.getName()); 
    } 
    if (element.isEmpty()) {
      handleEmptyTag(paramTagElement);
    } else {
      this.recent = element;
      this.stack = new TagStack(paramTagElement, this.stack);
      handleStartTag(paramTagElement);
    } 
  }
  
  protected void endTag(boolean paramBoolean) {
    handleText(this.stack.tag);
    if (paramBoolean && !this.stack.elem.omitEnd()) {
      error("end.missing", this.stack.elem.getName());
    } else if (!this.stack.terminate()) {
      error("end.unexpected", this.stack.elem.getName());
    } 
    handleEndTag(this.stack.tag);
    this.stack = this.stack.next;
    this.recent = (this.stack != null) ? this.stack.elem : null;
  }
  
  boolean ignoreElement(Element paramElement) {
    String str1 = this.stack.elem.getName();
    String str2 = paramElement.getName();
    if ((str2.equals("html") && this.seenHtml) || (str2.equals("head") && this.seenHead) || (str2.equals("body") && this.seenBody))
      return true; 
    if (str2.equals("dt") || str2.equals("dd")) {
      TagStack tagStack;
      for (tagStack = this.stack; tagStack != null && !tagStack.elem.getName().equals("dl"); tagStack = tagStack.next);
      if (tagStack == null)
        return true; 
    } 
    return ((str1.equals("table") && !str2.equals("#pcdata") && !str2.equals("input")) || (str2.equals("font") && (str1.equals("ul") || str1.equals("ol"))) || (str2.equals("meta") && this.stack != null) || (str2.equals("style") && this.seenBody) || (str1.equals("table") && str2.equals("a")));
  }
  
  protected void markFirstTime(Element paramElement) {
    String str1 = paramElement.getName();
    if (str1.equals("html")) {
      this.seenHtml = true;
    } else if (str1.equals("head")) {
      this.seenHead = true;
    } else if (str1.equals("body")) {
      if (this.buf.length == 1) {
        char[] arrayOfChar = new char[256];
        arrayOfChar[0] = this.buf[0];
        this.buf = arrayOfChar;
      } 
      this.seenBody = true;
    } 
  }
  
  boolean legalElementContext(Element paramElement) {
    if (this.stack == null) {
      if (paramElement != this.dtd.html) {
        startTag(makeTag(this.dtd.html, true));
        return legalElementContext(paramElement);
      } 
      return true;
    } 
    if (this.stack.advance(paramElement)) {
      markFirstTime(paramElement);
      return true;
    } 
    boolean bool = false;
    String str1 = this.stack.elem.getName();
    String str2 = paramElement.getName();
    if (!this.strict && ((str1.equals("table") && str2.equals("td")) || (str1.equals("table") && str2.equals("th")) || (str1.equals("tr") && !str2.equals("tr"))))
      bool = true; 
    if (!this.strict && !bool && (this.stack.elem.getName() != paramElement.getName() || paramElement.getName().equals("body")) && (this.skipTag = ignoreElement(paramElement))) {
      error("tag.ignore", paramElement.getName());
      return this.skipTag;
    } 
    if (!this.strict && str1.equals("table") && !str2.equals("tr") && !str2.equals("td") && !str2.equals("th") && !str2.equals("caption")) {
      Element element1 = this.dtd.getElement("tr");
      TagElement tagElement = makeTag(element1, true);
      legalTagContext(tagElement);
      startTag(tagElement);
      error("start.missing", paramElement.getName());
      return legalElementContext(paramElement);
    } 
    if (!bool && this.stack.terminate() && (!this.strict || this.stack.elem.omitEnd()))
      for (TagStack tagStack = this.stack.next; tagStack != null; tagStack = tagStack.next) {
        if (tagStack.advance(paramElement)) {
          while (this.stack != tagStack)
            endTag(true); 
          return true;
        } 
        if (!tagStack.terminate() || (this.strict && !tagStack.elem.omitEnd()))
          break; 
      }  
    Element element = this.stack.first();
    if (element != null && (!this.strict || element.omitStart()) && (element != this.dtd.head || paramElement != this.dtd.pcdata)) {
      TagElement tagElement = makeTag(element, true);
      legalTagContext(tagElement);
      startTag(tagElement);
      if (!element.omitStart())
        error("start.missing", paramElement.getName()); 
      return legalElementContext(paramElement);
    } 
    if (!this.strict) {
      ContentModel contentModel = this.stack.contentModel();
      Vector vector = new Vector();
      if (contentModel != null) {
        contentModel.getElements(vector);
        for (Element element1 : vector) {
          if (this.stack.excluded(element1.getIndex()))
            continue; 
          boolean bool1 = false;
          for (AttributeList attributeList = element1.getAttributes(); attributeList != null; attributeList = attributeList.next) {
            if (attributeList.modifier == 2) {
              bool1 = true;
              break;
            } 
          } 
          if (bool1)
            continue; 
          ContentModel contentModel1 = element1.getContent();
          if (contentModel1 != null && contentModel1.first(paramElement)) {
            TagElement tagElement = makeTag(element1, true);
            legalTagContext(tagElement);
            startTag(tagElement);
            error("start.missing", element1.getName());
            return legalElementContext(paramElement);
          } 
        } 
      } 
    } 
    if (this.stack.terminate() && this.stack.elem != this.dtd.body && (!this.strict || this.stack.elem.omitEnd())) {
      if (!this.stack.elem.omitEnd())
        error("end.missing", paramElement.getName()); 
      endTag(true);
      return legalElementContext(paramElement);
    } 
    return false;
  }
  
  void legalTagContext(TagElement paramTagElement) throws ChangedCharSetException {
    if (legalElementContext(paramTagElement.getElement())) {
      markFirstTime(paramTagElement.getElement());
      return;
    } 
    if (paramTagElement.breaksFlow() && this.stack != null && !this.stack.tag.breaksFlow()) {
      endTag(true);
      legalTagContext(paramTagElement);
      return;
    } 
    for (TagStack tagStack = this.stack; tagStack != null; tagStack = tagStack.next) {
      if (tagStack.tag.getElement() == this.dtd.head) {
        while (this.stack != tagStack)
          endTag(true); 
        endTag(true);
        legalTagContext(paramTagElement);
        return;
      } 
    } 
    error("tag.unexpected", paramTagElement.getElement().getName());
  }
  
  void errorContext() {
    while (this.stack != null && this.stack.tag.getElement() != this.dtd.body) {
      handleEndTag(this.stack.tag);
      this.stack = this.stack.next;
    } 
    if (this.stack == null) {
      legalElementContext(this.dtd.body);
      startTag(makeTag(this.dtd.body, true));
    } 
  }
  
  void addString(int paramInt) {
    if (this.strpos == this.str.length) {
      char[] arrayOfChar = new char[this.str.length + 128];
      System.arraycopy(this.str, 0, arrayOfChar, 0, this.str.length);
      this.str = arrayOfChar;
    } 
    this.str[this.strpos++] = (char)paramInt;
  }
  
  String getString(int paramInt) {
    char[] arrayOfChar = new char[this.strpos - paramInt];
    System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
    this.strpos = paramInt;
    return new String(arrayOfChar);
  }
  
  char[] getChars(int paramInt) {
    char[] arrayOfChar = new char[this.strpos - paramInt];
    System.arraycopy(this.str, paramInt, arrayOfChar, 0, this.strpos - paramInt);
    this.strpos = paramInt;
    return arrayOfChar;
  }
  
  char[] getChars(int paramInt1, int paramInt2) {
    char[] arrayOfChar = new char[paramInt2 - paramInt1];
    System.arraycopy(this.str, paramInt1, arrayOfChar, 0, paramInt2 - paramInt1);
    return arrayOfChar;
  }
  
  void resetStrBuffer() { this.strpos = 0; }
  
  int strIndexOf(char paramChar) {
    for (byte b = 0; b < this.strpos; b++) {
      if (this.str[b] == paramChar)
        return b; 
    } 
    return -1;
  }
  
  void skipSpace() {
    while (true) {
      switch (this.ch) {
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          continue;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
            continue;
          } 
          this.crCount++;
          continue;
        case 9:
        case 32:
          this.ch = readCh();
          continue;
      } 
      break;
    } 
  }
  
  boolean parseIdentifier(boolean paramBoolean) throws IOException {
    switch (this.ch) {
      case 65:
      case 66:
      case 67:
      case 68:
      case 69:
      case 70:
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
      case 76:
      case 77:
      case 78:
      case 79:
      case 80:
      case 81:
      case 82:
      case 83:
      case 84:
      case 85:
      case 86:
      case 87:
      case 88:
      case 89:
      case 90:
        if (paramBoolean)
          this.ch = 97 + this.ch - 65; 
        break;
      case 97:
      case 98:
      case 99:
      case 100:
      case 101:
      case 102:
      case 103:
      case 104:
      case 105:
      case 106:
      case 107:
      case 108:
      case 109:
      case 110:
      case 111:
      case 112:
      case 113:
      case 114:
      case 115:
      case 116:
      case 117:
      case 118:
      case 119:
      case 120:
      case 121:
      case 122:
        break;
      default:
        return false;
    } 
    while (true) {
      addString(this.ch);
      switch (this.ch = readCh()) {
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
          if (paramBoolean)
            this.ch = 97 + this.ch - 65; 
          continue;
        case 45:
        case 46:
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
        case 95:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 119:
        case 120:
        case 121:
        case 122:
          continue;
      } 
      break;
    } 
    return true;
  }
  
  private char[] parseEntityReference() throws IOException {
    int i = this.strpos;
    if ((this.ch = readCh()) == 35) {
      int j = 0;
      this.ch = readCh();
      if ((this.ch >= 48 && this.ch <= 57) || this.ch == 120 || this.ch == 88) {
        if (this.ch >= 48 && this.ch <= 57) {
          while (this.ch >= 48 && this.ch <= 57) {
            j = j * 10 + this.ch - 48;
            this.ch = readCh();
          } 
        } else {
          this.ch = readCh();
          for (char c = (char)Character.toLowerCase(this.ch); (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'); c = (char)Character.toLowerCase(this.ch)) {
            if (c >= '0' && c <= '9') {
              j = j * 16 + c - 48;
            } else {
              j = j * 16 + c - 97 + 10;
            } 
            this.ch = readCh();
          } 
        } 
        switch (this.ch) {
          case 10:
            this.ln++;
            this.ch = readCh();
            this.lfCount++;
            break;
          case 13:
            this.ln++;
            if ((this.ch = readCh()) == 10) {
              this.ch = readCh();
              this.crlfCount++;
              break;
            } 
            this.crCount++;
            break;
          case 59:
            this.ch = readCh();
            break;
        } 
        return mapNumericReference(j);
      } 
      addString(35);
      if (!parseIdentifier(false)) {
        error("ident.expected");
        this.strpos = i;
        return new char[] { '&', '#' };
      } 
    } else if (!parseIdentifier(false)) {
      return new char[] { '&' };
    } 
    boolean bool = false;
    switch (this.ch) {
      case 10:
        this.ln++;
        this.ch = readCh();
        this.lfCount++;
        break;
      case 13:
        this.ln++;
        if ((this.ch = readCh()) == 10) {
          this.ch = readCh();
          this.crlfCount++;
          break;
        } 
        this.crCount++;
        break;
      case 59:
        bool = true;
        this.ch = readCh();
        break;
    } 
    String str1 = getString(i);
    Entity entity = this.dtd.getEntity(str1);
    if (!this.strict && entity == null)
      entity = this.dtd.getEntity(str1.toLowerCase()); 
    if (entity == null || !entity.isGeneral()) {
      if (str1.length() == 0) {
        error("invalid.entref", str1);
        return new char[0];
      } 
      String str2 = "&" + str1 + (bool ? ";" : "");
      char[] arrayOfChar = new char[str2.length()];
      str2.getChars(0, arrayOfChar.length, arrayOfChar, 0);
      return arrayOfChar;
    } 
    return entity.getData();
  }
  
  private char[] mapNumericReference(int paramInt) {
    char[] arrayOfChar;
    if (paramInt >= 65535) {
      try {
        arrayOfChar = Character.toChars(paramInt);
      } catch (IllegalArgumentException illegalArgumentException) {
        arrayOfChar = new char[0];
      } 
    } else {
      arrayOfChar = new char[1];
      arrayOfChar[0] = (paramInt < 130 || paramInt > 159) ? (char)paramInt : cp1252Map[paramInt - 130];
    } 
    return arrayOfChar;
  }
  
  void parseComment() {
    while (true) {
      int i = this.ch;
      switch (i) {
        case 45:
          if (!this.strict && this.strpos != 0 && this.str[this.strpos - 1] == '-') {
            if ((this.ch = readCh()) == 62)
              return; 
            if (this.ch == 33) {
              if ((this.ch = readCh()) == 62)
                return; 
              addString(45);
              addString(33);
              continue;
            } 
            break;
          } 
          if ((this.ch = readCh()) == 45) {
            this.ch = readCh();
            if (this.strict || this.ch == 62)
              return; 
            if (this.ch == 33) {
              if ((this.ch = readCh()) == 62)
                return; 
              addString(45);
              addString(33);
              continue;
            } 
            addString(45);
          } 
          break;
        case -1:
          handleEOFInComment();
          return;
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          break;
        case 62:
          this.ch = readCh();
          break;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
          } else {
            this.crCount++;
          } 
          i = 10;
          break;
        default:
          this.ch = readCh();
          break;
      } 
      addString(i);
    } 
  }
  
  void parseLiteral(boolean paramBoolean) {
    while (true) {
      char[] arrayOfChar;
      byte b;
      int j;
      int i = this.ch;
      switch (i) {
        case -1:
          error("eof.literal", this.stack.elem.getName());
          endTag(true);
          return;
        case 62:
          this.ch = readCh();
          j = this.textpos - this.stack.elem.name.length() + 2;
          b = 0;
          if (j >= 0 && this.text[j++] == '<' && this.text[j] == '/') {
            while (++j < this.textpos && Character.toLowerCase(this.text[j]) == this.stack.elem.name.charAt(b++));
            if (j == this.textpos) {
              this.textpos -= this.stack.elem.name.length() + 2;
              if (this.textpos > 0 && this.text[this.textpos - 1] == '\n')
                this.textpos--; 
              endTag(false);
              return;
            } 
          } 
          break;
        case 38:
          arrayOfChar = parseEntityReference();
          if (this.textpos + arrayOfChar.length > this.text.length) {
            char[] arrayOfChar1 = new char[Math.max(this.textpos + arrayOfChar.length + 128, this.text.length * 2)];
            System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
            this.text = arrayOfChar1;
          } 
          System.arraycopy(arrayOfChar, 0, this.text, this.textpos, arrayOfChar.length);
          this.textpos += arrayOfChar.length;
          continue;
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          break;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
          } else {
            this.crCount++;
          } 
          i = 10;
          break;
        default:
          this.ch = readCh();
          break;
      } 
      if (this.textpos == this.text.length) {
        char[] arrayOfChar1 = new char[this.text.length + 128];
        System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
        this.text = arrayOfChar1;
      } 
      this.text[this.textpos++] = (char)i;
    } 
  }
  
  String parseAttributeValue(boolean paramBoolean) throws IOException {
    int i = -1;
    switch (this.ch) {
      case 34:
      case 39:
        i = this.ch;
        this.ch = readCh();
        break;
    } 
    while (true) {
      byte b;
      char[] arrayOfChar;
      int j = this.ch;
      switch (j) {
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          if (i < 0)
            return getString(0); 
          break;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
          } else {
            this.crCount++;
          } 
          if (i < 0)
            return getString(0); 
          break;
        case 9:
          if (i < 0)
            j = 32; 
        case 32:
          this.ch = readCh();
          if (i < 0)
            return getString(0); 
          break;
        case 60:
        case 62:
          if (i < 0)
            return getString(0); 
          this.ch = readCh();
          break;
        case 34:
        case 39:
          this.ch = readCh();
          if (j == i)
            return getString(0); 
          if (i == -1) {
            error("attvalerr");
            if (this.strict || this.ch == 32)
              return getString(0); 
            continue;
          } 
          break;
        case 61:
          if (i < 0) {
            error("attvalerr");
            if (this.strict)
              return getString(0); 
          } 
          this.ch = readCh();
          break;
        case 38:
          if (this.strict && i < 0) {
            this.ch = readCh();
            break;
          } 
          arrayOfChar = parseEntityReference();
          for (b = 0; b < arrayOfChar.length; b++) {
            j = arrayOfChar[b];
            addString((paramBoolean && j >= 65 && j <= 90) ? (97 + j - 65) : j);
          } 
          continue;
        case -1:
          return getString(0);
        default:
          if (paramBoolean && j >= 65 && j <= 90)
            j = 97 + j - 65; 
          this.ch = readCh();
          break;
      } 
      addString(j);
    } 
  }
  
  void parseAttributeSpecificationList(Element paramElement) {
    while (true) {
      String str2;
      String str1;
      AttributeList attributeList;
      skipSpace();
      switch (this.ch) {
        case -1:
        case 47:
        case 60:
        case 62:
          return;
        case 45:
          if ((this.ch = readCh()) == 45) {
            this.ch = readCh();
            parseComment();
            this.strpos = 0;
            continue;
          } 
          error("invalid.tagchar", "-", paramElement.getName());
          this.ch = readCh();
          continue;
      } 
      if (parseIdentifier(true)) {
        str1 = getString(0);
        skipSpace();
        if (this.ch == 61) {
          this.ch = readCh();
          skipSpace();
          attributeList = paramElement.getAttribute(str1);
          str2 = parseAttributeValue((attributeList != null && attributeList.type != 1 && attributeList.type != 11 && attributeList.type != 7));
        } else {
          str2 = str1;
          attributeList = paramElement.getAttributeByValue(str2);
          if (attributeList == null) {
            attributeList = paramElement.getAttribute(str1);
            if (attributeList != null) {
              str2 = attributeList.getValue();
            } else {
              str2 = null;
            } 
          } 
        } 
      } else {
        if (!this.strict && this.ch == 44) {
          this.ch = readCh();
          continue;
        } 
        if (!this.strict && this.ch == 34) {
          this.ch = readCh();
          skipSpace();
          if (parseIdentifier(true)) {
            str1 = getString(0);
            if (this.ch == 34)
              this.ch = readCh(); 
            skipSpace();
            if (this.ch == 61) {
              this.ch = readCh();
              skipSpace();
              attributeList = paramElement.getAttribute(str1);
              str2 = parseAttributeValue((attributeList != null && attributeList.type != 1 && attributeList.type != 11));
            } else {
              str2 = str1;
              attributeList = paramElement.getAttributeByValue(str2);
              if (attributeList == null) {
                attributeList = paramElement.getAttribute(str1);
                if (attributeList != null)
                  str2 = attributeList.getValue(); 
              } 
            } 
          } else {
            char[] arrayOfChar = { (char)this.ch };
            error("invalid.tagchar", new String(arrayOfChar), paramElement.getName());
            this.ch = readCh();
            continue;
          } 
        } else if (!this.strict && this.attributes.isEmpty() && this.ch == 61) {
          this.ch = readCh();
          skipSpace();
          str1 = paramElement.getName();
          attributeList = paramElement.getAttribute(str1);
          str2 = parseAttributeValue((attributeList != null && attributeList.type != 1 && attributeList.type != 11));
        } else {
          if (!this.strict && this.ch == 61) {
            this.ch = readCh();
            skipSpace();
            str2 = parseAttributeValue(true);
            error("attvalerr");
            return;
          } 
          char[] arrayOfChar = { (char)this.ch };
          error("invalid.tagchar", new String(arrayOfChar), paramElement.getName());
          if (!this.strict) {
            this.ch = readCh();
            continue;
          } 
          return;
        } 
      } 
      if (attributeList != null) {
        str1 = attributeList.getName();
      } else {
        error("invalid.tagatt", str1, paramElement.getName());
      } 
      if (this.attributes.isDefined(str1))
        error("multi.tagatt", str1, paramElement.getName()); 
      if (str2 == null) {
        str2 = (attributeList != null && attributeList.value != null) ? attributeList.value : "#DEFAULT";
      } else if (attributeList != null && attributeList.values != null && !attributeList.values.contains(str2)) {
        error("invalid.tagattval", str1, paramElement.getName());
      } 
      HTML.Attribute attribute = HTML.getAttributeKey(str1);
      if (attribute == null) {
        this.attributes.addAttribute(str1, str2);
        continue;
      } 
      this.attributes.addAttribute(attribute, str2);
    } 
  }
  
  public String parseDTDMarkup() throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    this.ch = readCh();
    while (true) {
      switch (this.ch) {
        case 62:
          this.ch = readCh();
          return stringBuilder.toString();
        case -1:
          error("invalid.markup");
          return stringBuilder.toString();
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          continue;
        case 34:
          this.ch = readCh();
          continue;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
            continue;
          } 
          this.crCount++;
          continue;
      } 
      stringBuilder.append((char)(this.ch & 0xFF));
      this.ch = readCh();
    } 
  }
  
  protected boolean parseMarkupDeclarations(StringBuffer paramStringBuffer) throws IOException {
    if (paramStringBuffer.length() == "DOCTYPE".length() && paramStringBuffer.toString().toUpperCase().equals("DOCTYPE")) {
      parseDTDMarkup();
      return true;
    } 
    return false;
  }
  
  void parseInvalidTag() {
    while (true) {
      skipSpace();
      switch (this.ch) {
        case -1:
        case 62:
          this.ch = readCh();
          return;
        case 60:
          return;
      } 
      this.ch = readCh();
    } 
  }
  
  void parseTag() {
    String str2;
    StringBuffer stringBuffer;
    TagStack tagStack;
    String str1;
    Element element;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    switch (this.ch = readCh()) {
      case 33:
        switch (this.ch = readCh()) {
          case 45:
            while (true) {
              if (this.ch == 45) {
                if (!this.strict || (this.ch = readCh()) == 45) {
                  this.ch = readCh();
                  if (!this.strict && this.ch == 45)
                    this.ch = readCh(); 
                  if (this.textpos != 0) {
                    char[] arrayOfChar = new char[this.textpos];
                    System.arraycopy(this.text, 0, arrayOfChar, 0, this.textpos);
                    handleText(arrayOfChar);
                    this.lastBlockStartPos = this.currentBlockStartPos;
                    this.textpos = 0;
                  } 
                  parseComment();
                  this.last = makeTag(this.dtd.getElement("comment"), true);
                  handleComment(getChars(0));
                  continue;
                } 
                if (!bool2) {
                  bool2 = true;
                  error("invalid.commentchar", "-");
                } 
              } 
              skipSpace();
              switch (this.ch) {
                case 45:
                  continue;
                case 62:
                  this.ch = readCh();
                case -1:
                  return;
              } 
              this.ch = readCh();
              if (!bool2) {
                bool2 = true;
                error("invalid.commentchar", String.valueOf((char)this.ch));
              } 
            } 
        } 
        stringBuffer = new StringBuffer();
        while (true) {
          stringBuffer.append((char)this.ch);
          if (parseMarkupDeclarations(stringBuffer))
            return; 
          switch (this.ch) {
            case 62:
              this.ch = readCh();
            case -1:
              error("invalid.markup");
              return;
            case 10:
              this.ln++;
              this.ch = readCh();
              this.lfCount++;
              continue;
            case 13:
              this.ln++;
              if ((this.ch = readCh()) == 10) {
                this.ch = readCh();
                this.crlfCount++;
                continue;
              } 
              this.crCount++;
              continue;
          } 
          this.ch = readCh();
        } 
      case 47:
        switch (this.ch = readCh()) {
          case 62:
            this.ch = readCh();
          case 60:
            if (this.recent == null) {
              error("invalid.shortend");
              return;
            } 
            element = this.recent;
            break;
          default:
            if (!parseIdentifier(true)) {
              error("expected.endtagname");
              return;
            } 
            skipSpace();
            switch (this.ch) {
              case 62:
                this.ch = readCh();
                break;
              case 60:
                break;
              default:
                error("expected", "'>'");
                while (this.ch != -1 && this.ch != 10 && this.ch != 62)
                  this.ch = readCh(); 
                if (this.ch == 62)
                  this.ch = readCh(); 
                break;
            } 
            str1 = getString(0);
            if (!this.dtd.elementExists(str1)) {
              error("end.unrecognized", str1);
              if (this.textpos > 0 && this.text[this.textpos - 1] == '\n')
                this.textpos--; 
              element = this.dtd.getElement("unknown");
              element.name = str1;
              bool3 = true;
              break;
            } 
            element = this.dtd.getElement(str1);
            break;
        } 
        if (this.stack == null) {
          error("end.extra.tag", element.getName());
          return;
        } 
        if (this.textpos > 0 && this.text[this.textpos - 1] == '\n')
          if (this.stack.pre) {
            if (this.textpos > 1 && this.text[this.textpos - 2] != '\n')
              this.textpos--; 
          } else {
            this.textpos--;
          }  
        if (bool3) {
          TagElement tagElement1 = makeTag(element);
          handleText(tagElement1);
          this.attributes.addAttribute(HTML.Attribute.ENDTAG, "true");
          handleEmptyTag(makeTag(element));
          bool3 = false;
          return;
        } 
        if (!this.strict) {
          str1 = this.stack.elem.getName();
          if (str1.equals("table") && !element.getName().equals(str1)) {
            error("tag.ignore", element.getName());
            return;
          } 
          if ((str1.equals("tr") || str1.equals("td")) && !element.getName().equals("table") && !element.getName().equals(str1)) {
            error("tag.ignore", element.getName());
            return;
          } 
        } 
        for (tagStack = this.stack; tagStack != null && element != tagStack.elem; tagStack = tagStack.next);
        if (tagStack == null) {
          error("unmatched.endtag", element.getName());
          return;
        } 
        str2 = element.getName();
        if (this.stack != tagStack && (str2.equals("font") || str2.equals("center"))) {
          if (str2.equals("center")) {
            while (this.stack.elem.omitEnd() && this.stack != tagStack)
              endTag(true); 
            if (this.stack.elem == element)
              endTag(false); 
          } 
          return;
        } 
        while (this.stack != tagStack)
          endTag(true); 
        endTag(false);
        return;
      case -1:
        error("eof");
        return;
    } 
    if (!parseIdentifier(true)) {
      element = this.recent;
      if (this.ch != 62 || element == null) {
        error("expected.tagname");
        return;
      } 
    } else {
      String str3 = getString(0);
      if (str3.equals("image"))
        str3 = "img"; 
      if (!this.dtd.elementExists(str3)) {
        error("tag.unrecognized ", str3);
        element = this.dtd.getElement("unknown");
        element.name = str3;
        bool3 = true;
      } else {
        element = this.dtd.getElement(str3);
      } 
    } 
    parseAttributeSpecificationList(element);
    switch (this.ch) {
      case 47:
        bool1 = true;
      case 62:
        this.ch = readCh();
        if (this.ch == 62 && bool1)
          this.ch = readCh(); 
        break;
      case 60:
        break;
      default:
        error("expected", "'>'");
        break;
    } 
    if (!this.strict && element.getName().equals("script"))
      error("javascript.unsupported"); 
    if (!element.isEmpty())
      if (this.ch == 10) {
        this.ln++;
        this.lfCount++;
        this.ch = readCh();
      } else if (this.ch == 13) {
        this.ln++;
        if ((this.ch = readCh()) == 10) {
          this.ch = readCh();
          this.crlfCount++;
        } else {
          this.crCount++;
        } 
      }  
    TagElement tagElement = makeTag(element, false);
    if (!bool3) {
      legalTagContext(tagElement);
      if (!this.strict && this.skipTag) {
        this.skipTag = false;
        return;
      } 
    } 
    startTag(tagElement);
    if (!element.isEmpty()) {
      switch (element.getType()) {
        case 1:
          parseLiteral(false);
          return;
        case 16:
          parseLiteral(true);
          return;
      } 
      if (this.stack != null)
        this.stack.net = bool1; 
    } 
  }
  
  void parseScript() {
    char[] arrayOfChar = new char[SCRIPT_END_TAG.length];
    boolean bool = false;
    while (true) {
      byte b;
      for (b = 0; !bool && b < SCRIPT_END_TAG.length && (SCRIPT_END_TAG[b] == this.ch || SCRIPT_END_TAG_UPPER_CASE[b] == this.ch); b++) {
        arrayOfChar[b] = (char)this.ch;
        this.ch = readCh();
      } 
      if (b == SCRIPT_END_TAG.length)
        return; 
      if (!bool && b == 1 && arrayOfChar[0] == "<!--".charAt(0)) {
        while (b < "<!--".length() && "<!--".charAt(b) == this.ch) {
          arrayOfChar[b] = (char)this.ch;
          this.ch = readCh();
          b++;
        } 
        if (b == "<!--".length())
          bool = true; 
      } 
      if (bool) {
        while (b < "-->".length() && "-->".charAt(b) == this.ch) {
          arrayOfChar[b] = (char)this.ch;
          this.ch = readCh();
          b++;
        } 
        if (b == "-->".length())
          bool = false; 
      } 
      if (b > 0) {
        for (byte b1 = 0; b1 < b; b1++)
          addString(arrayOfChar[b1]); 
        continue;
      } 
      switch (this.ch) {
        case -1:
          error("eof.script");
          return;
        case 10:
          this.ln++;
          this.ch = readCh();
          this.lfCount++;
          addString(10);
          continue;
        case 13:
          this.ln++;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
          } else {
            this.crCount++;
          } 
          addString(10);
          continue;
      } 
      addString(this.ch);
      this.ch = readCh();
    } 
  }
  
  void parseContent() {
    Thread thread = Thread.currentThread();
    while (true) {
      char[] arrayOfChar;
      if (thread.isInterrupted()) {
        thread.interrupt();
        break;
      } 
      int i = this.ch;
      this.currentBlockStartPos = this.currentPosition;
      if (this.recent == this.dtd.script) {
        parseScript();
        this.last = makeTag(this.dtd.getElement("comment"), true);
        String str1 = (new String(getChars(0))).trim();
        int j = "<!--".length() + "-->".length();
        if (str1.startsWith("<!--") && str1.endsWith("-->") && str1.length() >= j)
          str1 = str1.substring("<!--".length(), str1.length() - "-->".length()); 
        handleComment(str1.toCharArray());
        endTag(false);
        this.lastBlockStartPos = this.currentPosition;
        continue;
      } 
      switch (i) {
        case 60:
          parseTag();
          this.lastBlockStartPos = this.currentPosition;
          continue;
        case 47:
          this.ch = readCh();
          if (this.stack != null && this.stack.net) {
            endTag(false);
            continue;
          } 
          if (this.textpos == 0) {
            if (!legalElementContext(this.dtd.pcdata))
              error("unexpected.pcdata"); 
            if (this.last.breaksFlow())
              this.space = false; 
          } 
          break;
        case -1:
          return;
        case 38:
          if (this.textpos == 0) {
            if (!legalElementContext(this.dtd.pcdata))
              error("unexpected.pcdata"); 
            if (this.last.breaksFlow())
              this.space = false; 
          } 
          arrayOfChar = parseEntityReference();
          if (this.textpos + arrayOfChar.length + 1 > this.text.length) {
            char[] arrayOfChar1 = new char[Math.max(this.textpos + arrayOfChar.length + 128, this.text.length * 2)];
            System.arraycopy(this.text, 0, arrayOfChar1, 0, this.text.length);
            this.text = arrayOfChar1;
          } 
          if (this.space) {
            this.space = false;
            this.text[this.textpos++] = ' ';
          } 
          System.arraycopy(arrayOfChar, 0, this.text, this.textpos, arrayOfChar.length);
          this.textpos += arrayOfChar.length;
          this.ignoreSpace = false;
          continue;
        case 10:
          this.ln++;
          this.lfCount++;
          this.ch = readCh();
          if (this.stack != null && this.stack.pre)
            break; 
          if (this.textpos == 0)
            this.lastBlockStartPos = this.currentPosition; 
          if (!this.ignoreSpace)
            this.space = true; 
          continue;
        case 13:
          this.ln++;
          i = 10;
          if ((this.ch = readCh()) == 10) {
            this.ch = readCh();
            this.crlfCount++;
          } else {
            this.crCount++;
          } 
          if (this.stack != null && this.stack.pre)
            break; 
          if (this.textpos == 0)
            this.lastBlockStartPos = this.currentPosition; 
          if (!this.ignoreSpace)
            this.space = true; 
          continue;
        case 9:
        case 32:
          this.ch = readCh();
          if (this.stack != null && this.stack.pre)
            break; 
          if (this.textpos == 0)
            this.lastBlockStartPos = this.currentPosition; 
          if (!this.ignoreSpace)
            this.space = true; 
          continue;
        default:
          if (this.textpos == 0) {
            if (!legalElementContext(this.dtd.pcdata))
              error("unexpected.pcdata"); 
            if (this.last.breaksFlow())
              this.space = false; 
          } 
          this.ch = readCh();
          break;
      } 
      if (this.textpos + 2 > this.text.length) {
        arrayOfChar = new char[this.text.length + 128];
        System.arraycopy(this.text, 0, arrayOfChar, 0, this.text.length);
        this.text = arrayOfChar;
      } 
      if (this.space) {
        if (this.textpos == 0)
          this.lastBlockStartPos--; 
        this.text[this.textpos++] = ' ';
        this.space = false;
      } 
      this.text[this.textpos++] = (char)i;
      this.ignoreSpace = false;
    } 
  }
  
  String getEndOfLineString() throws IOException { return (this.crlfCount >= this.crCount) ? ((this.lfCount >= this.crlfCount) ? "\n" : "\r\n") : ((this.crCount > this.lfCount) ? "\r" : "\n"); }
  
  public void parse(Reader paramReader) throws IOException {
    this.in = paramReader;
    this.ln = 1;
    this.seenHtml = false;
    this.seenHead = false;
    this.seenBody = false;
    this.crCount = this.lfCount = this.crlfCount = 0;
    try {
      this.ch = readCh();
      this.text = new char[1024];
      this.str = new char[128];
      parseContent();
      while (this.stack != null)
        endTag(true); 
      paramReader.close();
    } catch (IOException iOException) {
      errorContext();
      error("ioexception");
      throw iOException;
    } catch (Exception exception) {
      errorContext();
      error("exception", exception.getClass().getName(), exception.getMessage());
      exception.printStackTrace();
    } catch (ThreadDeath threadDeath) {
      errorContext();
      error("terminated");
      threadDeath.printStackTrace();
      throw threadDeath;
    } finally {
      while (this.stack != null) {
        handleEndTag(this.stack.tag);
        this.stack = this.stack.next;
      } 
      this.text = null;
      this.str = null;
    } 
  }
  
  private final int readCh() {
    if (this.pos >= this.len) {
      try {
        this.len = this.in.read(this.buf);
      } catch (InterruptedIOException interruptedIOException) {
        throw interruptedIOException;
      } 
      if (this.len <= 0)
        return -1; 
      this.pos = 0;
    } 
    this.currentPosition++;
    return this.buf[this.pos++];
  }
  
  protected int getCurrentPos() { return this.currentPosition; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\parser\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
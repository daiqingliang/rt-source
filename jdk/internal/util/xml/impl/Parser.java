package jdk.internal.util.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;

public abstract class Parser {
  public static final String FAULT = "";
  
  protected static final int BUFFSIZE_READER = 512;
  
  protected static final int BUFFSIZE_PARSER = 128;
  
  public static final char EOS = '￿';
  
  private Pair mNoNS;
  
  private Pair mXml;
  
  private Map<String, Input> mEnt;
  
  private Map<String, Input> mPEnt;
  
  protected boolean mIsSAlone;
  
  protected boolean mIsSAloneSet;
  
  protected boolean mIsNSAware;
  
  protected int mPh = -1;
  
  protected static final int PH_BEFORE_DOC = -1;
  
  protected static final int PH_DOC_START = 0;
  
  protected static final int PH_MISC_DTD = 1;
  
  protected static final int PH_DTD = 2;
  
  protected static final int PH_DTD_MISC = 3;
  
  protected static final int PH_DOCELM = 4;
  
  protected static final int PH_DOCELM_MISC = 5;
  
  protected static final int PH_AFTER_DOC = 6;
  
  protected int mEvt;
  
  protected static final int EV_NULL = 0;
  
  protected static final int EV_ELM = 1;
  
  protected static final int EV_ELMS = 2;
  
  protected static final int EV_ELME = 3;
  
  protected static final int EV_TEXT = 4;
  
  protected static final int EV_WSPC = 5;
  
  protected static final int EV_PI = 6;
  
  protected static final int EV_CDAT = 7;
  
  protected static final int EV_COMM = 8;
  
  protected static final int EV_DTD = 9;
  
  protected static final int EV_ENT = 10;
  
  private char mESt;
  
  protected char[] mBuff = new char[128];
  
  protected int mBuffIdx;
  
  protected Pair mPref = pair(this.mPref);
  
  protected Pair mElm;
  
  protected Pair mAttL;
  
  protected Input mDoc;
  
  protected Input mInp;
  
  private char[] mChars;
  
  private int mChLen;
  
  private int mChIdx;
  
  protected Attrs mAttrs = new Attrs();
  
  private String[] mItems;
  
  private char mAttrIdx;
  
  private String mUnent;
  
  private Pair mDltd;
  
  private static final char[] NONS = new char[1];
  
  private static final char[] XML;
  
  private static final char[] XMLNS;
  
  private static final byte[] asctyp;
  
  private static final byte[] nmttyp;
  
  protected Parser() {
    this.mPref.name = "";
    this.mPref.value = "";
    this.mPref.chars = NONS;
    this.mNoNS = this.mPref;
    this.mPref = pair(this.mPref);
    this.mPref.name = "xml";
    this.mPref.value = "http://www.w3.org/XML/1998/namespace";
    this.mPref.chars = XML;
    this.mXml = this.mPref;
  }
  
  protected void init() {
    this.mUnent = null;
    this.mElm = null;
    this.mPref = this.mXml;
    this.mAttL = null;
    this.mPEnt = new HashMap();
    this.mEnt = new HashMap();
    this.mDoc = this.mInp;
    this.mChars = this.mInp.chars;
    this.mPh = 0;
  }
  
  protected void cleanup() {
    while (this.mAttL != null) {
      while (this.mAttL.list != null) {
        if (this.mAttL.list.list != null)
          del(this.mAttL.list.list); 
        this.mAttL.list = del(this.mAttL.list);
      } 
      this.mAttL = del(this.mAttL);
    } 
    while (this.mElm != null)
      this.mElm = del(this.mElm); 
    while (this.mPref != this.mXml)
      this.mPref = del(this.mPref); 
    while (this.mInp != null)
      pop(); 
    if (this.mDoc != null && this.mDoc.src != null)
      try {
        this.mDoc.src.close();
      } catch (IOException iOException) {} 
    this.mPEnt = null;
    this.mEnt = null;
    this.mDoc = null;
    this.mPh = 6;
  }
  
  protected int step() throws Exception {
    this.mEvt = 0;
    byte b = 0;
    while (this.mEvt == 0) {
      Pair pair2;
      Pair pair1;
      char[] arrayOfChar;
      char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : getch();
      switch (b) {
        case false:
          if (c != '<') {
            bkch();
            this.mBuffIdx = -1;
            b = 1;
            continue;
          } 
          switch (getch()) {
            case '/':
              this.mEvt = 3;
              if (this.mElm == null)
                panic(""); 
              this.mBuffIdx = -1;
              bname(this.mIsNSAware);
              arrayOfChar = this.mElm.chars;
              if (arrayOfChar.length == this.mBuffIdx + 1) {
                char c1;
                for (c1 = '\001'; c1 <= this.mBuffIdx; c1 = (char)(c1 + true)) {
                  if (arrayOfChar[c1] != this.mBuff[c1])
                    panic(""); 
                } 
              } else {
                panic("");
              } 
              if (wsskip() != '>')
                panic(""); 
              getch();
              continue;
            case '!':
              c = getch();
              bkch();
              switch (c) {
                case '-':
                  this.mEvt = 8;
                  comm();
                  continue;
                case '[':
                  this.mEvt = 7;
                  cdat();
                  continue;
              } 
              this.mEvt = 9;
              dtd();
              continue;
            case '?':
              this.mEvt = 6;
              pi();
              continue;
          } 
          bkch();
          this.mElm = pair(this.mElm);
          this.mElm.chars = qname(this.mIsNSAware);
          this.mElm.name = this.mElm.local();
          this.mElm.id = (this.mElm.next != null) ? this.mElm.next.id : 0;
          this.mElm.num = 0;
          pair1 = find(this.mAttL, this.mElm.chars);
          this.mElm.list = (pair1 != null) ? pair1.list : null;
          this.mAttrIdx = Character.MIN_VALUE;
          pair2 = pair(null);
          pair2.num = 0;
          attr(pair2);
          del(pair2);
          this.mElm.value = this.mIsNSAware ? rslv(this.mElm.chars) : null;
          switch (wsskip()) {
            case '>':
              getch();
              this.mEvt = 2;
              continue;
            case '/':
              getch();
              if (getch() != '>')
                panic(""); 
              this.mEvt = 1;
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case '\t':
            case '\n':
            case ' ':
              bappend(c);
              continue;
            case '\r':
              if (getch() != '\n')
                bkch(); 
              bappend('\n');
              continue;
            case '<':
              this.mEvt = 5;
              bkch();
              bflash_ws();
              continue;
          } 
          bkch();
          b = 2;
          continue;
        case true:
          switch (c) {
            case '&':
              if (this.mUnent == null) {
                if ((this.mUnent = ent(120)) != null) {
                  this.mEvt = 4;
                  bkch();
                  setch('&');
                  bflash();
                } 
                continue;
              } 
              this.mEvt = 10;
              skippedEnt(this.mUnent);
              this.mUnent = null;
              continue;
            case '<':
              this.mEvt = 4;
              bkch();
              bflash();
              continue;
            case '\r':
              if (getch() != '\n')
                bkch(); 
              bappend('\n');
              continue;
            case '￿':
              panic("");
              break;
          } 
          bappend(c);
          continue;
      } 
      panic("");
    } 
    return this.mEvt;
  }
  
  private void dtd() {
    Object object = null;
    String str = null;
    Pair pair = null;
    if ("DOCTYPE".equals(name(false)) != true)
      panic(""); 
    this.mPh = 2;
    byte b = 0;
    while (b) {
      char c = getch();
      switch (b) {
        case false:
          if (chtyp(c) != ' ') {
            bkch();
            str = name(this.mIsNSAware);
            wsskip();
            b = 1;
          } 
          continue;
        case true:
          switch (chtyp(c)) {
            case 'A':
              bkch();
              pair = pubsys(' ');
              b = 2;
              docType(str, pair.name, pair.value);
              continue;
            case '[':
              bkch();
              b = 2;
              docType(str, null, null);
              continue;
            case '>':
              bkch();
              b = 3;
              docType(str, null, null);
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case '[':
              dtdsub();
              b = 3;
              continue;
            case '>':
              bkch();
              b = 3;
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case '>':
              if (pair != null) {
                InputSource inputSource = resolveEnt(str, pair.name, pair.value);
                if (inputSource != null) {
                  if (!this.mIsSAlone) {
                    bkch();
                    setch(']');
                    push(new Input(512));
                    setinp(inputSource);
                    this.mInp.pubid = pair.name;
                    this.mInp.sysid = pair.value;
                    dtdsub();
                  } else {
                    skippedEnt("[dtd]");
                    if (inputSource.getCharacterStream() != null)
                      try {
                        inputSource.getCharacterStream().close();
                      } catch (IOException iOException) {} 
                    if (inputSource.getByteStream() != null)
                      try {
                        inputSource.getByteStream().close();
                      } catch (IOException iOException) {} 
                  } 
                } else {
                  skippedEnt("[dtd]");
                } 
                del(pair);
              } 
              b = -1;
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
  }
  
  private void dtdsub() {
    byte b = 0;
    while (b) {
      char c = getch();
      switch (b) {
        case false:
          switch (chtyp(c)) {
            case '<':
              c = getch();
              switch (c) {
                case '?':
                  pi();
                  continue;
                case '!':
                  c = getch();
                  bkch();
                  if (c == '-') {
                    comm();
                    continue;
                  } 
                  bntok();
                  switch (bkeyword()) {
                    case 'n':
                      dtdent();
                      break;
                    case 'a':
                      dtdattl();
                      break;
                    case 'e':
                      dtdelm();
                      break;
                    case 'o':
                      dtdnot();
                      break;
                    default:
                      panic("");
                      break;
                  } 
                  b = 1;
                  continue;
              } 
              panic("");
              continue;
            case '%':
              pent(' ');
              continue;
            case ']':
              b = -1;
              continue;
            case ' ':
              continue;
            case 'Z':
              if (getch() != ']')
                panic(""); 
              b = -1;
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case '>':
              b = 0;
              continue;
            case '\t':
            case '\n':
            case '\r':
            case ' ':
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
  }
  
  private void dtdent() {
    String str = null;
    char[] arrayOfChar = null;
    Input input = null;
    Pair pair = null;
    byte b = 0;
    while (b) {
      char c = getch();
      switch (b) {
        case false:
          switch (chtyp(c)) {
            case ' ':
              continue;
            case '%':
              c = getch();
              bkch();
              if (chtyp(c) == ' ') {
                wsskip();
                str = name(false);
                switch (chtyp(wsskip())) {
                  case 'A':
                    pair = pubsys(' ');
                    if (wsskip() == '>') {
                      if (!this.mPEnt.containsKey(str)) {
                        input = new Input();
                        input.pubid = pair.name;
                        input.sysid = pair.value;
                        this.mPEnt.put(str, input);
                      } 
                    } else {
                      panic("");
                    } 
                    del(pair);
                    b = -1;
                    continue;
                  case '"':
                  case '\'':
                    bqstr('d');
                    arrayOfChar = new char[this.mBuffIdx + 1];
                    System.arraycopy(this.mBuff, 1, arrayOfChar, 1, arrayOfChar.length - 1);
                    arrayOfChar[0] = ' ';
                    if (!this.mPEnt.containsKey(str)) {
                      input = new Input(arrayOfChar);
                      input.pubid = this.mInp.pubid;
                      input.sysid = this.mInp.sysid;
                      input.xmlenc = this.mInp.xmlenc;
                      input.xmlver = this.mInp.xmlver;
                      this.mPEnt.put(str, input);
                    } 
                    b = -1;
                    continue;
                } 
                panic("");
                continue;
              } 
              pent(' ');
              continue;
          } 
          bkch();
          str = name(false);
          b = 1;
          continue;
        case true:
          switch (chtyp(c)) {
            case '"':
            case '\'':
              bkch();
              bqstr('d');
              if (this.mEnt.get(str) == null) {
                arrayOfChar = new char[this.mBuffIdx];
                System.arraycopy(this.mBuff, 1, arrayOfChar, 0, arrayOfChar.length);
                if (!this.mEnt.containsKey(str)) {
                  input = new Input(arrayOfChar);
                  input.pubid = this.mInp.pubid;
                  input.sysid = this.mInp.sysid;
                  input.xmlenc = this.mInp.xmlenc;
                  input.xmlver = this.mInp.xmlver;
                  this.mEnt.put(str, input);
                } 
              } 
              b = -1;
              continue;
            case 'A':
              bkch();
              pair = pubsys(' ');
              switch (wsskip()) {
                case '>':
                  if (!this.mEnt.containsKey(str)) {
                    input = new Input();
                    input.pubid = pair.name;
                    input.sysid = pair.value;
                    this.mEnt.put(str, input);
                  } 
                  break;
                case 'N':
                  if ("NDATA".equals(name(false)) == true) {
                    wsskip();
                    unparsedEntDecl(str, pair.name, pair.value, name(false));
                    break;
                  } 
                default:
                  panic("");
                  break;
              } 
              del(pair);
              b = -1;
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
  }
  
  private void dtdelm() {
    wsskip();
    name(this.mIsNSAware);
    while (true) {
      char c = getch();
      switch (c) {
        case '>':
          bkch();
          return;
        case '￿':
          panic("");
      } 
    } 
  }
  
  private void dtdattl() {
    char[] arrayOfChar = null;
    Pair pair = null;
    boolean bool = false;
    while (bool) {
      char c = getch();
      switch (bool) {
        case false:
          switch (chtyp(c)) {
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
              bkch();
              arrayOfChar = qname(this.mIsNSAware);
              pair = find(this.mAttL, arrayOfChar);
              if (pair == null) {
                pair = pair(this.mAttL);
                pair.chars = arrayOfChar;
                this.mAttL = pair;
              } 
              bool = true;
              continue;
            case ' ':
              continue;
            case '%':
              pent(' ');
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
              bkch();
              dtdatt(pair);
              if (wsskip() == '>')
                return; 
              continue;
            case ' ':
              continue;
            case '%':
              pent(' ');
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
  }
  
  private void dtdatt(Pair paramPair) throws Exception {
    char[] arrayOfChar = null;
    Pair pair = null;
    byte b = 0;
    while (b) {
      char c = getch();
      switch (b) {
        case false:
          switch (chtyp(c)) {
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
              bkch();
              arrayOfChar = qname(this.mIsNSAware);
              pair = find(paramPair.list, arrayOfChar);
              if (pair == null) {
                pair = pair(paramPair.list);
                pair.chars = arrayOfChar;
                paramPair.list = pair;
              } else {
                pair = pair(null);
                pair.chars = arrayOfChar;
                pair.id = 99;
              } 
              wsskip();
              b = 1;
              continue;
            case '%':
              pent(' ');
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case '(':
              pair.id = 117;
              b = 2;
              continue;
            case '%':
              pent(' ');
              continue;
            case ' ':
              continue;
          } 
          bkch();
          bntok();
          pair.id = bkeyword();
          switch (pair.id) {
            case 111:
              if (wsskip() != '(')
                panic(""); 
              c = getch();
              b = 2;
              continue;
            case 78:
            case 82:
            case 84:
            case 99:
            case 105:
            case 110:
            case 114:
            case 116:
              wsskip();
              b = 4;
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case '-':
            case '.':
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
            case 'd':
              bkch();
              switch (pair.id) {
                case 117:
                  bntok();
                  break;
                case 111:
                  this.mBuffIdx = -1;
                  bname(false);
                  break;
                default:
                  panic("");
                  break;
              } 
              wsskip();
              b = 3;
              continue;
            case '%':
              pent(' ');
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case ')':
              wsskip();
              b = 4;
              continue;
            case '|':
              wsskip();
              switch (pair.id) {
                case 117:
                  bntok();
                  break;
                case 111:
                  this.mBuffIdx = -1;
                  bname(false);
                  break;
                default:
                  panic("");
                  break;
              } 
              wsskip();
              continue;
            case '%':
              pent(' ');
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case '#':
              bntok();
              switch (bkeyword()) {
                case 'F':
                  switch (wsskip()) {
                    case '"':
                    case '\'':
                      b = 5;
                      continue;
                    case '￿':
                      panic("");
                      break;
                  } 
                  b = -1;
                  continue;
                case 'I':
                case 'Q':
                  b = -1;
                  continue;
              } 
              panic("");
              continue;
            case '"':
            case '\'':
              bkch();
              b = 5;
              continue;
            case '\t':
            case '\n':
            case '\r':
            case ' ':
              continue;
            case '%':
              pent(' ');
              continue;
          } 
          bkch();
          b = -1;
          continue;
        case true:
          switch (c) {
            case '"':
            case '\'':
              bkch();
              bqstr('d');
              pair.list = pair(null);
              pair.list.chars = new char[pair.chars.length + this.mBuffIdx + 3];
              System.arraycopy(pair.chars, 1, pair.list.chars, 0, pair.chars.length - 1);
              pair.list.chars[pair.chars.length - 1] = '=';
              pair.list.chars[pair.chars.length] = c;
              System.arraycopy(this.mBuff, 1, pair.list.chars, pair.chars.length + 1, this.mBuffIdx);
              pair.list.chars[pair.chars.length + this.mBuffIdx + 1] = c;
              pair.list.chars[pair.chars.length + this.mBuffIdx + 2] = ' ';
              b = -1;
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
  }
  
  private void dtdnot() {
    wsskip();
    String str = name(false);
    wsskip();
    Pair pair = pubsys('N');
    notDecl(str, pair.name, pair.value);
    del(pair);
  }
  
  private void attr(Pair paramPair) throws Exception {
    switch (wsskip()) {
      case '/':
      case '>':
        if ((paramPair.num & 0x2) == 0) {
          paramPair.num |= 0x2;
          Input input = this.mInp;
          for (Pair pair1 = this.mElm.list; pair1 != null; pair1 = pair1.next) {
            if (pair1.list != null) {
              Pair pair2 = find(paramPair.next, pair1.chars);
              if (pair2 == null)
                push(new Input(pair1.list.chars)); 
            } 
          } 
          if (this.mInp != input) {
            attr(paramPair);
            return;
          } 
        } 
        this.mAttrs.setLength(this.mAttrIdx);
        this.mItems = this.mAttrs.mItems;
        return;
      case '￿':
        panic("");
        break;
    } 
    paramPair.chars = qname(this.mIsNSAware);
    paramPair.name = paramPair.local();
    String str1 = atype(paramPair);
    wsskip();
    if (getch() != '=')
      panic(""); 
    bqstr((char)paramPair.id);
    String str2 = new String(this.mBuff, 1, this.mBuffIdx);
    Pair pair = pair(paramPair);
    paramPair.num &= 0xFFFFFFFE;
    if (!this.mIsNSAware || !isdecl(paramPair, str2)) {
      this.mAttrIdx = (char)(this.mAttrIdx + '\001');
      attr(pair);
      this.mAttrIdx = (char)(this.mAttrIdx - '\001');
      char c = (char)(this.mAttrIdx << '\003');
      this.mItems[c + '\001'] = paramPair.qname();
      this.mItems[c + '\002'] = this.mIsNSAware ? paramPair.name : "";
      this.mItems[c + '\003'] = str2;
      this.mItems[c + '\004'] = str1;
      switch (paramPair.num & 0x3) {
        case 0:
          this.mItems[c + '\005'] = null;
          break;
        case 1:
          this.mItems[c + '\005'] = "d";
          break;
        default:
          this.mItems[c + '\005'] = "D";
          break;
      } 
      this.mItems[c + Character.MIN_VALUE] = (paramPair.chars[0] != '\000') ? rslv(paramPair.chars) : "";
    } else {
      newPrefix();
      attr(pair);
    } 
    del(pair);
  }
  
  private String atype(Pair paramPair) throws Exception {
    paramPair.id = 99;
    Pair pair;
    if (this.mElm.list == null || (pair = find(this.mElm.list, paramPair.chars)) == null)
      return "CDATA"; 
    paramPair.num |= 0x1;
    paramPair.id = 105;
    switch (pair.id) {
      case 105:
        return "ID";
      case 114:
        return "IDREF";
      case 82:
        return "IDREFS";
      case 110:
        return "ENTITY";
      case 78:
        return "ENTITIES";
      case 116:
        return "NMTOKEN";
      case 84:
        return "NMTOKENS";
      case 117:
        return "NMTOKEN";
      case 111:
        return "NOTATION";
      case 99:
        paramPair.id = 99;
        return "CDATA";
    } 
    panic("");
    return null;
  }
  
  private void comm() {
    if (this.mPh == 0)
      this.mPh = 1; 
    this.mBuffIdx = -1;
    byte b = 0;
    while (b) {
      char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : getch();
      if (c == Character.MAX_VALUE)
        panic(""); 
      switch (b) {
        case false:
          if (c == '-') {
            b = 1;
            continue;
          } 
          panic("");
          continue;
        case true:
          if (c == '-') {
            b = 2;
            continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case '-':
              b = 3;
              continue;
          } 
          bappend(c);
          continue;
        case true:
          switch (c) {
            case '-':
              b = 4;
              continue;
          } 
          bappend('-');
          bappend(c);
          b = 2;
          continue;
        case true:
          if (c == '>') {
            comm(this.mBuff, this.mBuffIdx + 1);
            b = -1;
            continue;
          } 
          break;
      } 
      panic("");
    } 
  }
  
  private void pi() {
    String str = null;
    this.mBuffIdx = -1;
    byte b = 0;
    while (b) {
      char c = getch();
      if (c == Character.MAX_VALUE)
        panic(""); 
      switch (b) {
        case false:
          switch (chtyp(c)) {
            case ':':
            case 'A':
            case 'X':
            case '_':
            case 'a':
              bkch();
              str = name(false);
              if (str.length() == 0 || this.mXml.name.equals(str.toLowerCase()) == true)
                panic(""); 
              if (this.mPh == 0)
                this.mPh = 1; 
              wsskip();
              b = 1;
              this.mBuffIdx = -1;
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (c) {
            case '?':
              b = 2;
              continue;
          } 
          bappend(c);
          continue;
        case true:
          switch (c) {
            case '>':
              pi(str, new String(this.mBuff, 0, this.mBuffIdx + 1));
              b = -1;
              continue;
            case '?':
              bappend('?');
              continue;
          } 
          bappend('?');
          bappend(c);
          b = 1;
          continue;
      } 
      panic("");
    } 
  }
  
  private void cdat() {
    this.mBuffIdx = -1;
    byte b = 0;
    while (b) {
      char c = getch();
      switch (b) {
        case false:
          if (c == '[') {
            b = 1;
            continue;
          } 
          panic("");
          continue;
        case true:
          if (chtyp(c) == 'A') {
            bappend(c);
            continue;
          } 
          if ("CDATA".equals(new String(this.mBuff, 0, this.mBuffIdx + 1)) != true)
            panic(""); 
          bkch();
          b = 2;
          continue;
        case true:
          if (c != '[')
            panic(""); 
          this.mBuffIdx = -1;
          b = 3;
          continue;
        case true:
          if (c != ']') {
            bappend(c);
            continue;
          } 
          b = 4;
          continue;
        case true:
          if (c != ']') {
            bappend(']');
            bappend(c);
            b = 3;
            continue;
          } 
          b = 5;
          continue;
        case true:
          switch (c) {
            case ']':
              bappend(']');
              continue;
            case '>':
              bflash();
              b = -1;
              continue;
          } 
          bappend(']');
          bappend(']');
          bappend(c);
          b = 3;
          continue;
      } 
      panic("");
    } 
  }
  
  protected String name(boolean paramBoolean) throws Exception {
    this.mBuffIdx = -1;
    bname(paramBoolean);
    return new String(this.mBuff, 1, this.mBuffIdx);
  }
  
  protected char[] qname(boolean paramBoolean) throws Exception {
    this.mBuffIdx = -1;
    bname(paramBoolean);
    char[] arrayOfChar = new char[this.mBuffIdx + 1];
    System.arraycopy(this.mBuff, 0, arrayOfChar, 0, this.mBuffIdx + 1);
    return arrayOfChar;
  }
  
  private void pubsys(Input paramInput) throws Exception {
    Pair pair = pubsys(' ');
    paramInput.pubid = pair.name;
    paramInput.sysid = pair.value;
    del(pair);
  }
  
  private Pair pubsys(char paramChar) throws Exception {
    Pair pair = pair(null);
    String str = name(false);
    if ("PUBLIC".equals(str) == true) {
      bqstr('i');
      pair.name = new String(this.mBuff, 1, this.mBuffIdx);
      switch (wsskip()) {
        case '"':
        case '\'':
          bqstr(' ');
          pair.value = new String(this.mBuff, 1, this.mBuffIdx);
          return pair;
        case '￿':
          panic("");
          break;
      } 
      if (paramChar != 'N')
        panic(""); 
      pair.value = null;
      return pair;
    } 
    if ("SYSTEM".equals(str) == true) {
      pair.name = null;
      bqstr(' ');
      pair.value = new String(this.mBuff, 1, this.mBuffIdx);
      return pair;
    } 
    panic("");
    return null;
  }
  
  protected String eqstr(char paramChar) throws Exception {
    if (paramChar == '=') {
      wsskip();
      if (getch() != '=')
        panic(""); 
    } 
    bqstr((paramChar == '=') ? 45 : paramChar);
    return new String(this.mBuff, 1, this.mBuffIdx);
  }
  
  private String ent(char paramChar) throws Exception {
    int i = this.mBuffIdx + 1;
    Input input = null;
    String str = null;
    this.mESt = 'Ā';
    bappend('&');
    byte b = 0;
    while (b) {
      char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : getch();
      switch (b) {
        case false:
        case true:
          switch (chtyp(c)) {
            case '-':
            case '.':
            case 'd':
              if (b != 1)
                panic(""); 
            case 'A':
            case 'X':
            case '_':
            case 'a':
              bappend(c);
              eappend(c);
              b = 1;
              continue;
            case ':':
              if (this.mIsNSAware)
                panic(""); 
              bappend(c);
              eappend(c);
              b = 1;
              continue;
            case ';':
              if (this.mESt < 'Ā') {
                this.mBuffIdx = i - 1;
                bappend(this.mESt);
                b = -1;
                continue;
              } 
              if (this.mPh == 2) {
                bappend(';');
                b = -1;
                continue;
              } 
              str = new String(this.mBuff, i + 1, this.mBuffIdx - i);
              input = (Input)this.mEnt.get(str);
              this.mBuffIdx = i - 1;
              if (input != null) {
                if (input.chars == null) {
                  InputSource inputSource = resolveEnt(str, input.pubid, input.sysid);
                  if (inputSource != null) {
                    push(new Input(512));
                    setinp(inputSource);
                    this.mInp.pubid = input.pubid;
                    this.mInp.sysid = input.sysid;
                    str = null;
                  } else if (paramChar != 'x') {
                    panic("");
                  } 
                } else {
                  push(input);
                  str = null;
                } 
              } else if (paramChar != 'x') {
                panic("");
              } 
              b = -1;
              continue;
            case '#':
              if (b != 0)
                panic(""); 
              b = 2;
              continue;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case 'd':
              bappend(c);
              continue;
            case ';':
              try {
                int j = Integer.parseInt(new String(this.mBuff, i + 1, this.mBuffIdx - i), 10);
                if (j >= 65535)
                  panic(""); 
                c = (char)j;
              } catch (NumberFormatException numberFormatException) {
                panic("");
              } 
              this.mBuffIdx = i - 1;
              if (c == ' ' || this.mInp.next != null) {
                bappend(c, paramChar);
              } else {
                bappend(c);
              } 
              b = -1;
              continue;
            case 'a':
              if (this.mBuffIdx == i && c == 'x') {
                b = 3;
                continue;
              } 
              break;
          } 
          panic("");
          continue;
        case true:
          switch (chtyp(c)) {
            case 'A':
            case 'a':
            case 'd':
              bappend(c);
              continue;
            case ';':
              try {
                int j = Integer.parseInt(new String(this.mBuff, i + 1, this.mBuffIdx - i), 16);
                if (j >= 65535)
                  panic(""); 
                c = (char)j;
              } catch (NumberFormatException numberFormatException) {
                panic("");
              } 
              this.mBuffIdx = i - 1;
              if (c == ' ' || this.mInp.next != null) {
                bappend(c, paramChar);
              } else {
                bappend(c);
              } 
              b = -1;
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
    return str;
  }
  
  private void pent(char paramChar) throws Exception {
    int i = this.mBuffIdx + 1;
    Input input = null;
    String str = null;
    bappend('%');
    if (this.mPh != 2)
      return; 
    bname(false);
    str = new String(this.mBuff, i + 2, this.mBuffIdx - i - 1);
    if (getch() != ';')
      panic(""); 
    input = (Input)this.mPEnt.get(str);
    this.mBuffIdx = i - 1;
    if (input != null) {
      if (input.chars == null) {
        InputSource inputSource = resolveEnt(str, input.pubid, input.sysid);
        if (inputSource != null) {
          if (paramChar != '-')
            bappend(' '); 
          push(new Input(512));
          setinp(inputSource);
          this.mInp.pubid = input.pubid;
          this.mInp.sysid = input.sysid;
        } else {
          skippedEnt("%" + str);
        } 
      } else {
        if (paramChar == '-') {
          input.chIdx = 1;
        } else {
          bappend(' ');
          input.chIdx = 0;
        } 
        push(input);
      } 
    } else {
      skippedEnt("%" + str);
    } 
  }
  
  private boolean isdecl(Pair paramPair, String paramString) {
    if (paramPair.chars[0] == '\000') {
      if ("xmlns".equals(paramPair.name) == true) {
        this.mPref = pair(this.mPref);
        this.mPref.list = this.mElm;
        this.mPref.value = paramString;
        this.mPref.name = "";
        this.mPref.chars = NONS;
        this.mElm.num++;
        return true;
      } 
    } else if (paramPair.eqpref(XMLNS) == true) {
      int i = paramPair.name.length();
      this.mPref = pair(this.mPref);
      this.mPref.list = this.mElm;
      this.mPref.value = paramString;
      this.mPref.name = paramPair.name;
      this.mPref.chars = new char[i + 1];
      this.mPref.chars[0] = (char)(i + 1);
      paramPair.name.getChars(0, i, this.mPref.chars, 1);
      this.mElm.num++;
      return true;
    } 
    return false;
  }
  
  private String rslv(char[] paramArrayOfChar) throws Exception {
    Pair pair;
    for (pair = this.mPref; pair != null; pair = pair.next) {
      if (pair.eqpref(paramArrayOfChar) == true)
        return pair.value; 
    } 
    if (paramArrayOfChar[0] == '\001')
      for (pair = this.mPref; pair != null; pair = pair.next) {
        if (pair.chars[0] == '\000')
          return pair.value; 
      }  
    panic("");
    return null;
  }
  
  protected char wsskip() throws IOException {
    char c;
    do {
      c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : getch();
    } while (c < '' && nmttyp[c] == 3);
    this.mChIdx--;
    return c;
  }
  
  protected abstract void docType(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  protected abstract void comm(char[] paramArrayOfChar, int paramInt);
  
  protected abstract void pi(String paramString1, String paramString2) throws Exception;
  
  protected abstract void newPrefix();
  
  protected abstract void skippedEnt(String paramString) throws Exception;
  
  protected abstract InputSource resolveEnt(String paramString1, String paramString2, String paramString3) throws Exception;
  
  protected abstract void notDecl(String paramString1, String paramString2, String paramString3) throws SAXException;
  
  protected abstract void unparsedEntDecl(String paramString1, String paramString2, String paramString3, String paramString4) throws Exception;
  
  protected abstract void panic(String paramString) throws Exception;
  
  private void bname(boolean paramBoolean) throws Exception {
    int i = ++this.mBuffIdx;
    int j = i;
    int k = i + 1;
    int m = k;
    int n = this.mChIdx;
    short s = (short)((paramBoolean == true) ? 0 : 2);
    while (true) {
      if (this.mChIdx >= this.mChLen) {
        bcopy(n, m);
        getch();
        n = --this.mChIdx;
        m = k;
      } 
      char c = this.mChars[this.mChIdx++];
      char c1 = Character.MIN_VALUE;
      if (c < '') {
        c1 = (char)nmttyp[c];
      } else if (c == Character.MAX_VALUE) {
        panic("");
      } 
      switch (s) {
        case 0:
        case 2:
          switch (c1) {
            case '\000':
              k++;
              s = (short)(s + 1);
              continue;
            case '\001':
              this.mChIdx--;
              s = (short)(s + 1);
              continue;
          } 
          panic("");
          continue;
        case 1:
        case 3:
          switch (c1) {
            case '\000':
            case '\002':
              k++;
              continue;
            case '\001':
              k++;
              if (paramBoolean == true) {
                if (j != i)
                  panic(""); 
                j = k - 1;
                if (s == 1)
                  s = 2; 
              } 
              continue;
          } 
          this.mChIdx--;
          bcopy(n, m);
          this.mBuff[i] = (char)(j - i);
          return;
      } 
      panic("");
    } 
  }
  
  private void bntok() {
    this.mBuffIdx = -1;
    bappend(false);
    while (true) {
      char c = getch();
      switch (chtyp(c)) {
        case '-':
        case '.':
        case ':':
        case 'A':
        case 'X':
        case '_':
        case 'a':
        case 'd':
          bappend(c);
          continue;
        case 'Z':
          panic("");
          break;
        default:
          break;
      } 
      bkch();
      return;
    } 
    bkch();
  }
  
  private char bkeyword() throws IOException {
    String str = new String(this.mBuff, 1, this.mBuffIdx);
    switch (str.length()) {
      case 2:
        return ("ID".equals(str) == true) ? 'i' : '?';
      case 5:
        switch (this.mBuff[1]) {
          case 'I':
            return ("IDREF".equals(str) == true) ? 'r' : '?';
          case 'C':
            return ("CDATA".equals(str) == true) ? 'c' : '?';
          case 'F':
            return ("FIXED".equals(str) == true) ? 'F' : '?';
        } 
        break;
      case 6:
        switch (this.mBuff[1]) {
          case 'I':
            return ("IDREFS".equals(str) == true) ? 'R' : '?';
          case 'E':
            return ("ENTITY".equals(str) == true) ? 'n' : '?';
        } 
        break;
      case 7:
        switch (this.mBuff[1]) {
          case 'I':
            return ("IMPLIED".equals(str) == true) ? 'I' : '?';
          case 'N':
            return ("NMTOKEN".equals(str) == true) ? 't' : '?';
          case 'A':
            return ("ATTLIST".equals(str) == true) ? 'a' : '?';
          case 'E':
            return ("ELEMENT".equals(str) == true) ? 'e' : '?';
        } 
        break;
      case 8:
        switch (this.mBuff[2]) {
          case 'N':
            return ("ENTITIES".equals(str) == true) ? 'N' : '?';
          case 'M':
            return ("NMTOKENS".equals(str) == true) ? 'T' : '?';
          case 'O':
            return ("NOTATION".equals(str) == true) ? 'o' : '?';
          case 'E':
            return ("REQUIRED".equals(str) == true) ? 'Q' : '?';
        } 
        break;
    } 
    return '?';
  }
  
  private void bqstr(char paramChar) throws Exception {
    Input input = this.mInp;
    this.mBuffIdx = -1;
    bappend(false);
    byte b = 0;
    while (b) {
      char c = (this.mChIdx < this.mChLen) ? this.mChars[this.mChIdx++] : getch();
      switch (b) {
        case false:
          switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
              continue;
            case '\'':
              b = 2;
              continue;
            case '"':
              b = 3;
              continue;
          } 
          panic("");
          continue;
        case true:
        case true:
          switch (c) {
            case '\'':
              if (b == 2 && this.mInp == input) {
                b = -1;
                continue;
              } 
              bappend(c);
              continue;
            case '"':
              if (b == 3 && this.mInp == input) {
                b = -1;
                continue;
              } 
              bappend(c);
              continue;
            case '&':
              if (paramChar != 'd') {
                ent(paramChar);
                continue;
              } 
              bappend(c);
              continue;
            case '%':
              if (paramChar == 'd') {
                pent('-');
                continue;
              } 
              bappend(c);
              continue;
            case '<':
              if (paramChar == '-' || paramChar == 'd') {
                bappend(c);
                continue;
              } 
              panic("");
              continue;
            case '￿':
              panic("");
            case '\r':
              if (paramChar != ' ' && this.mInp.next == null) {
                if (getch() != '\n')
                  bkch(); 
                c = '\n';
              } 
              break;
          } 
          bappend(c, paramChar);
          continue;
      } 
      panic("");
    } 
    if (paramChar == 'i' && this.mBuff[this.mBuffIdx] == ' ')
      this.mBuffIdx--; 
  }
  
  protected abstract void bflash();
  
  protected abstract void bflash_ws();
  
  private void bappend(char paramChar1, char paramChar2) {
    switch (paramChar2) {
      case 'i':
        switch (paramChar1) {
          case '\t':
          case '\n':
          case '\r':
          case ' ':
            if (this.mBuffIdx > 0 && this.mBuff[this.mBuffIdx] != ' ')
              bappend(' '); 
            return;
        } 
        break;
      case 'c':
        switch (paramChar1) {
          case '\t':
          case '\n':
          case '\r':
            paramChar1 = ' ';
            break;
        } 
        break;
    } 
    this.mBuffIdx++;
    if (this.mBuffIdx < this.mBuff.length) {
      this.mBuff[this.mBuffIdx] = paramChar1;
    } else {
      this.mBuffIdx--;
      bappend(paramChar1);
    } 
  }
  
  private void bappend(char paramChar) throws Exception {
    try {
      this.mBuff[++this.mBuffIdx] = paramChar;
    } catch (Exception exception) {
      char[] arrayOfChar = new char[this.mBuff.length << 1];
      System.arraycopy(this.mBuff, 0, arrayOfChar, 0, this.mBuff.length);
      this.mBuff = arrayOfChar;
      this.mBuff[this.mBuffIdx] = paramChar;
    } 
  }
  
  private void bcopy(int paramInt1, int paramInt2) {
    int i = this.mChIdx - paramInt1;
    if (paramInt2 + i + 1 >= this.mBuff.length) {
      char[] arrayOfChar = new char[this.mBuff.length + i];
      System.arraycopy(this.mBuff, 0, arrayOfChar, 0, this.mBuff.length);
      this.mBuff = arrayOfChar;
    } 
    System.arraycopy(this.mChars, paramInt1, this.mBuff, paramInt2, i);
    this.mBuffIdx += i;
  }
  
  private void eappend(char paramChar) throws Exception {
    switch (this.mESt) {
      case 'Ā':
        switch (paramChar) {
          case 'l':
            this.mESt = 'ā';
            break;
          case 'g':
            this.mESt = 'Ă';
            break;
          case 'a':
            this.mESt = 'ă';
            break;
          case 'q':
            this.mESt = 'ć';
            break;
        } 
        this.mESt = 'Ȁ';
        break;
      case 'ā':
        this.mESt = (paramChar == 't') ? '<' : 'Ȁ';
        break;
      case 'Ă':
        this.mESt = (paramChar == 't') ? '>' : 'Ȁ';
        break;
      case 'ă':
        switch (paramChar) {
          case 'm':
            this.mESt = 'Ą';
            break;
          case 'p':
            this.mESt = 'ą';
            break;
        } 
        this.mESt = 'Ȁ';
        break;
      case 'Ą':
        this.mESt = (paramChar == 'p') ? '&' : 'Ȁ';
        break;
      case 'ą':
        this.mESt = (paramChar == 'o') ? 'Ć' : 'Ȁ';
        break;
      case 'Ć':
        this.mESt = (paramChar == 's') ? '\'' : 'Ȁ';
        break;
      case 'ć':
        this.mESt = (paramChar == 'u') ? 'Ĉ' : 'Ȁ';
        break;
      case 'Ĉ':
        this.mESt = (paramChar == 'o') ? 'ĉ' : 'Ȁ';
        break;
      case 'ĉ':
        this.mESt = (paramChar == 't') ? '"' : 'Ȁ';
        break;
      case '"':
      case '&':
      case '\'':
      case '<':
      case '>':
        this.mESt = 'Ȁ';
        break;
    } 
  }
  
  protected void setinp(InputSource paramInputSource) throws Exception {
    Reader reader = null;
    this.mChIdx = 0;
    this.mChLen = 0;
    this.mChars = this.mInp.chars;
    this.mInp.src = null;
    if (this.mPh < 0)
      this.mIsSAlone = false; 
    this.mIsSAloneSet = false;
    if (paramInputSource.getCharacterStream() != null) {
      reader = paramInputSource.getCharacterStream();
      xml(reader);
    } else if (paramInputSource.getByteStream() != null) {
      if (paramInputSource.getEncoding() != null) {
        String str = paramInputSource.getEncoding().toUpperCase();
        if (str.equals("UTF-16")) {
          reader = bom(paramInputSource.getByteStream(), 'U');
        } else {
          reader = enc(str, paramInputSource.getByteStream());
        } 
        xml(reader);
      } else {
        reader = bom(paramInputSource.getByteStream(), ' ');
        if (reader == null) {
          reader = enc("UTF-8", paramInputSource.getByteStream());
          String str = xml(reader);
          if (str.startsWith("UTF-16"))
            panic(""); 
          reader = enc(str, paramInputSource.getByteStream());
        } else {
          xml(reader);
        } 
      } 
    } else {
      panic("");
    } 
    this.mInp.src = reader;
    this.mInp.pubid = paramInputSource.getPublicId();
    this.mInp.sysid = paramInputSource.getSystemId();
  }
  
  private Reader bom(InputStream paramInputStream, char paramChar) throws Exception {
    int i = paramInputStream.read();
    switch (i) {
      case 239:
        if (paramChar == 'U')
          panic(""); 
        if (paramInputStream.read() != 187)
          panic(""); 
        if (paramInputStream.read() != 191)
          panic(""); 
        return new ReaderUTF8(paramInputStream);
      case 254:
        if (paramInputStream.read() != 255)
          panic(""); 
        return new ReaderUTF16(paramInputStream, 'b');
      case 255:
        if (paramInputStream.read() != 254)
          panic(""); 
        return new ReaderUTF16(paramInputStream, 'l');
      case -1:
        this.mChars[this.mChIdx++] = Character.MAX_VALUE;
        return new ReaderUTF8(paramInputStream);
    } 
    if (paramChar == 'U')
      panic(""); 
    switch (i & 0xF0) {
      case 192:
      case 208:
        this.mChars[this.mChIdx++] = (char)((i & 0x1F) << 6 | paramInputStream.read() & 0x3F);
        return null;
      case 224:
        this.mChars[this.mChIdx++] = (char)((i & 0xF) << 12 | (paramInputStream.read() & 0x3F) << 6 | paramInputStream.read() & 0x3F);
        return null;
      case 240:
        throw new UnsupportedEncodingException();
    } 
    this.mChars[this.mChIdx++] = (char)i;
    return null;
  }
  
  private String xml(Reader paramReader) throws Exception {
    String str1 = null;
    String str2 = "UTF-8";
    if (this.mChIdx != 0) {
      s = (short)((this.mChars[0] == '<') ? 1 : -1);
    } else {
      s = 0;
    } 
    while (s && this.mChIdx < this.mChars.length) {
      int i;
      char c = ((i = paramReader.read()) >= 0) ? (char)i : 65535;
      this.mChars[this.mChIdx++] = c;
      switch (s) {
        case false:
          switch (c) {
            case '<':
              s = 1;
              continue;
            case '﻿':
              c = ((i = paramReader.read()) >= 0) ? (char)i : 65535;
              this.mChars[this.mChIdx - 1] = c;
              s = (short)((c == '<') ? 1 : -1);
              continue;
          } 
          s = -1;
          continue;
        case true:
          s = (short)((c == '?') ? 2 : -1);
          continue;
        case true:
          s = (short)((c == 'x') ? 3 : -1);
          continue;
        case true:
          s = (short)((c == 'm') ? 4 : -1);
          continue;
        case true:
          s = (short)((c == 'l') ? 5 : -1);
          continue;
        case true:
          switch (c) {
            case '\t':
            case '\n':
            case '\r':
            case ' ':
              s = 6;
              continue;
          } 
          s = -1;
          continue;
        case true:
          switch (c) {
            case '?':
              s = 7;
              continue;
            case '￿':
              s = -2;
              continue;
          } 
          continue;
        case true:
          switch (c) {
            case '>':
            case '￿':
              s = -2;
              continue;
          } 
          s = 6;
          continue;
      } 
      panic("");
    } 
    this.mChLen = this.mChIdx;
    this.mChIdx = 0;
    if (s == -1)
      return str2; 
    this.mChIdx = 5;
    short s = 0;
    while (s >= 0) {
      char c = getch();
      switch (s) {
        case 0:
          if (chtyp(c) != ' ') {
            bkch();
            s = 1;
          } 
          continue;
        case 1:
        case 2:
        case 3:
          switch (chtyp(c)) {
            case 'A':
            case '_':
            case 'a':
              bkch();
              str1 = name(false).toLowerCase();
              if ("version".equals(str1) == true) {
                if (s != 1)
                  panic(""); 
                if ("1.0".equals(eqstr('=')) != true)
                  panic(""); 
                this.mInp.xmlver = 'Ā';
                s = 2;
                continue;
              } 
              if ("encoding".equals(str1) == true) {
                if (s != 2)
                  panic(""); 
                this.mInp.xmlenc = eqstr('=').toUpperCase();
                str2 = this.mInp.xmlenc;
                s = 3;
                continue;
              } 
              if ("standalone".equals(str1) == true) {
                if (s == 1 || this.mPh >= 0)
                  panic(""); 
                str1 = eqstr('=').toLowerCase();
                if (str1.equals("yes") == true) {
                  this.mIsSAlone = true;
                } else if (str1.equals("no") == true) {
                  this.mIsSAlone = false;
                } else {
                  panic("");
                } 
                this.mIsSAloneSet = true;
                s = 4;
                continue;
              } 
              panic("");
              continue;
            case ' ':
              continue;
            case '?':
              if (s == 1)
                panic(""); 
              bkch();
              s = 4;
              continue;
          } 
          panic("");
          continue;
        case 4:
          switch (chtyp(c)) {
            case '?':
              if (getch() != '>')
                panic(""); 
              if (this.mPh <= 0)
                this.mPh = 1; 
              s = -1;
              continue;
            case ' ':
              continue;
          } 
          panic("");
          continue;
      } 
      panic("");
    } 
    return str2;
  }
  
  private Reader enc(String paramString, InputStream paramInputStream) throws UnsupportedEncodingException { return paramString.equals("UTF-8") ? new ReaderUTF8(paramInputStream) : (paramString.equals("UTF-16LE") ? new ReaderUTF16(paramInputStream, 'l') : (paramString.equals("UTF-16BE") ? new ReaderUTF16(paramInputStream, 'b') : new InputStreamReader(paramInputStream, paramString))); }
  
  protected void push(Input paramInput) throws Exception {
    this.mInp.chLen = this.mChLen;
    this.mInp.chIdx = this.mChIdx;
    paramInput.next = this.mInp;
    this.mInp = paramInput;
    this.mChars = paramInput.chars;
    this.mChLen = paramInput.chLen;
    this.mChIdx = paramInput.chIdx;
  }
  
  protected void pop() {
    if (this.mInp.src != null) {
      try {
        this.mInp.src.close();
      } catch (IOException iOException) {}
      this.mInp.src = null;
    } 
    this.mInp = this.mInp.next;
    if (this.mInp != null) {
      this.mChars = this.mInp.chars;
      this.mChLen = this.mInp.chLen;
      this.mChIdx = this.mInp.chIdx;
    } else {
      this.mChars = null;
      this.mChLen = 0;
      this.mChIdx = 0;
    } 
  }
  
  protected char chtyp(char paramChar) { return (paramChar < '') ? (char)asctyp[paramChar] : ((paramChar != Character.MAX_VALUE) ? 88 : 90); }
  
  protected char getch() throws IOException {
    if (this.mChIdx >= this.mChLen) {
      if (this.mInp.src == null) {
        pop();
        return getch();
      } 
      int i = this.mInp.src.read(this.mChars, 0, this.mChars.length);
      if (i < 0) {
        if (this.mInp != this.mDoc) {
          pop();
          return getch();
        } 
        this.mChars[0] = Character.MAX_VALUE;
        this.mChLen = 1;
      } else {
        this.mChLen = i;
      } 
      this.mChIdx = 0;
    } 
    return this.mChars[this.mChIdx++];
  }
  
  protected void bkch() {
    if (this.mChIdx <= 0)
      panic(""); 
    this.mChIdx--;
  }
  
  protected void setch(char paramChar) throws Exception { this.mChars[this.mChIdx] = paramChar; }
  
  protected Pair find(Pair paramPair, char[] paramArrayOfChar) {
    for (Pair pair = paramPair; pair != null; pair = pair.next) {
      if (pair.eqname(paramArrayOfChar) == true)
        return pair; 
    } 
    return null;
  }
  
  protected Pair pair(Pair paramPair) {
    Pair pair;
    if (this.mDltd != null) {
      pair = this.mDltd;
      this.mDltd = pair.next;
    } else {
      pair = new Pair();
    } 
    pair.next = paramPair;
    return pair;
  }
  
  protected Pair del(Pair paramPair) {
    Pair pair = paramPair.next;
    paramPair.name = null;
    paramPair.value = null;
    paramPair.chars = null;
    paramPair.list = null;
    paramPair.next = this.mDltd;
    this.mDltd = paramPair;
    return pair;
  }
  
  static  {
    NONS[0] = Character.MIN_VALUE;
    XML = new char[4];
    XML[0] = '\004';
    XML[1] = 'x';
    XML[2] = 'm';
    XML[3] = 'l';
    XMLNS = new char[6];
    XMLNS[0] = '\006';
    XMLNS[1] = 'x';
    XMLNS[2] = 'm';
    XMLNS[3] = 'l';
    XMLNS[4] = 'n';
    XMLNS[5] = 's';
    short s = 0;
    asctyp = new byte[128];
    while (s < 32) {
      s = (short)(s + true);
      asctyp[s] = 122;
    } 
    asctyp[9] = 32;
    asctyp[13] = 32;
    asctyp[10] = 32;
    while (s < 48) {
      s = (short)(s + 1);
      asctyp[s] = (byte)s;
    } 
    while (s <= 57) {
      s = (short)(s + 1);
      asctyp[s] = 100;
    } 
    while (s < 65) {
      s = (short)(s + 1);
      asctyp[s] = (byte)s;
    } 
    while (s <= 90) {
      s = (short)(s + 1);
      asctyp[s] = 65;
    } 
    while (s < 97) {
      s = (short)(s + 1);
      asctyp[s] = (byte)s;
    } 
    while (s <= 122) {
      s = (short)(s + 1);
      asctyp[s] = 97;
    } 
    while (s < 128) {
      s = (short)(s + 1);
      asctyp[s] = (byte)s;
    } 
    nmttyp = new byte[128];
    for (s = 0; s < 48; s = (short)(s + 1))
      nmttyp[s] = -1; 
    while (s <= 57) {
      s = (short)(s + 1);
      nmttyp[s] = 2;
    } 
    while (s < 65) {
      s = (short)(s + 1);
      nmttyp[s] = -1;
    } 
    for (s = 91; s < 97; s = (short)(s + 1))
      nmttyp[s] = -1; 
    for (s = 123; s < 128; s = (short)(s + 1))
      nmttyp[s] = -1; 
    nmttyp[95] = 0;
    nmttyp[58] = 1;
    nmttyp[46] = 2;
    nmttyp[45] = 2;
    nmttyp[32] = 3;
    nmttyp[9] = 3;
    nmttyp[13] = 3;
    nmttyp[10] = 3;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\interna\\util\xml\impl\Parser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
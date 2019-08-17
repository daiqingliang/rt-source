package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

class Token implements Serializable {
  private static final long serialVersionUID = 8484976002585487481L;
  
  static final boolean COUNTTOKENS = true;
  
  static int tokens = 0;
  
  static final int CHAR = 0;
  
  static final int DOT = 11;
  
  static final int CONCAT = 1;
  
  static final int UNION = 2;
  
  static final int CLOSURE = 3;
  
  static final int RANGE = 4;
  
  static final int NRANGE = 5;
  
  static final int PAREN = 6;
  
  static final int EMPTY = 7;
  
  static final int ANCHOR = 8;
  
  static final int NONGREEDYCLOSURE = 9;
  
  static final int STRING = 10;
  
  static final int BACKREFERENCE = 12;
  
  static final int LOOKAHEAD = 20;
  
  static final int NEGATIVELOOKAHEAD = 21;
  
  static final int LOOKBEHIND = 22;
  
  static final int NEGATIVELOOKBEHIND = 23;
  
  static final int INDEPENDENT = 24;
  
  static final int MODIFIERGROUP = 25;
  
  static final int CONDITION = 26;
  
  static final int UTF16_MAX = 1114111;
  
  final int type;
  
  static Token token_dot;
  
  static Token token_0to9;
  
  static Token token_wordchars;
  
  static Token token_not_0to9;
  
  static Token token_not_wordchars;
  
  static Token token_spaces;
  
  static Token token_not_spaces;
  
  static Token token_empty;
  
  static Token token_linebeginning;
  
  static Token token_linebeginning2;
  
  static Token token_lineend;
  
  static Token token_stringbeginning;
  
  static Token token_stringend;
  
  static Token token_stringend2;
  
  static Token token_wordedge;
  
  static Token token_not_wordedge;
  
  static Token token_wordbeginning;
  
  static Token token_wordend = (token_wordbeginning = (token_not_wordedge = (token_wordedge = (token_stringend2 = (token_stringend = (token_stringbeginning = (token_lineend = (token_linebeginning2 = (token_linebeginning = (token_empty = new Token(7)).createAnchor(94)).createAnchor(64)).createAnchor(36)).createAnchor(65)).createAnchor(122)).createAnchor(90)).createAnchor(98)).createAnchor(66)).createAnchor(60)).createAnchor(62);
  
  static final int FC_CONTINUE = 0;
  
  static final int FC_TERMINAL = 1;
  
  static final int FC_ANY = 2;
  
  private static final Map<String, Token> categories;
  
  private static final Map<String, Token> categories2;
  
  private static final String[] categoryNames;
  
  static final int CHAR_INIT_QUOTE = 29;
  
  static final int CHAR_FINAL_QUOTE = 30;
  
  static final int CHAR_LETTER = 31;
  
  static final int CHAR_MARK = 32;
  
  static final int CHAR_NUMBER = 33;
  
  static final int CHAR_SEPARATOR = 34;
  
  static final int CHAR_OTHER = 35;
  
  static final int CHAR_PUNCTUATION = 36;
  
  static final int CHAR_SYMBOL = 37;
  
  private static final String[] blockNames;
  
  static final String blockRanges = "\000ÿĀſƀɏɐʯʰ˿̀ͯͰϿЀӿ԰֏֐׿؀ۿ܀ݏހ޿ऀॿঀ৿਀੿઀૿଀୿஀௿ఀ౿ಀ೿ഀൿ඀෿฀๿຀໿ༀ࿿က႟Ⴀჿᄀᇿሀ፿Ꭰ᏿᐀ᙿ ᚟ᚠ᛿ក៿᠀᢯Ḁỿἀ῿ ⁯⁰₟₠⃏⃐⃿℀⅏⅐↏←⇿∀⋿⌀⏿␀␿⑀⑟①⓿─╿▀▟■◿☀⛿✀➿⠀⣿⺀⻿⼀⿟⿰⿿　〿぀ゟ゠ヿ㄀ㄯ㄰㆏㆐㆟ㆠㆿ㈀㋿㌀㏿㐀䶵一鿿ꀀ꒏꒐꓏가힣豈﫿ﬀﭏﭐ﷿︠︯︰﹏﹐﹯ﹰ﻾﻿﻿＀￯";
  
  static final int[] nonBMPBlockRanges;
  
  private static final int NONBMP_BLOCK_START = 84;
  
  static final Set<String> nonxs;
  
  static final String viramaString = "्্੍્୍்్್്ฺ྄";
  
  private static Token token_grapheme;
  
  private static Token token_ccs;
  
  static ParenToken createLook(int paramInt, Token paramToken) {
    tokens++;
    return new ParenToken(paramInt, paramToken, 0);
  }
  
  static ParenToken createParen(Token paramToken, int paramInt) {
    tokens++;
    return new ParenToken(6, paramToken, paramInt);
  }
  
  static ClosureToken createClosure(Token paramToken) {
    tokens++;
    return new ClosureToken(3, paramToken);
  }
  
  static ClosureToken createNGClosure(Token paramToken) {
    tokens++;
    return new ClosureToken(9, paramToken);
  }
  
  static ConcatToken createConcat(Token paramToken1, Token paramToken2) {
    tokens++;
    return new ConcatToken(paramToken1, paramToken2);
  }
  
  static UnionToken createConcat() {
    tokens++;
    return new UnionToken(1);
  }
  
  static UnionToken createUnion() {
    tokens++;
    return new UnionToken(2);
  }
  
  static Token createEmpty() { return token_empty; }
  
  static RangeToken createRange() {
    tokens++;
    return new RangeToken(4);
  }
  
  static RangeToken createNRange() {
    tokens++;
    return new RangeToken(5);
  }
  
  static CharToken createChar(int paramInt) {
    tokens++;
    return new CharToken(0, paramInt);
  }
  
  private static CharToken createAnchor(int paramInt) {
    tokens++;
    return new CharToken(8, paramInt);
  }
  
  static StringToken createBackReference(int paramInt) {
    tokens++;
    return new StringToken(12, null, paramInt);
  }
  
  static StringToken createString(String paramString) {
    tokens++;
    return new StringToken(10, paramString, 0);
  }
  
  static ModifierToken createModifierGroup(Token paramToken, int paramInt1, int paramInt2) {
    tokens++;
    return new ModifierToken(paramToken, paramInt1, paramInt2);
  }
  
  static ConditionToken createCondition(int paramInt, Token paramToken1, Token paramToken2, Token paramToken3) {
    tokens++;
    return new ConditionToken(paramInt, paramToken1, paramToken2, paramToken3);
  }
  
  protected Token(int paramInt) { this.type = paramInt; }
  
  int size() { return 0; }
  
  Token getChild(int paramInt) { return null; }
  
  void addChild(Token paramToken) { throw new RuntimeException("Not supported."); }
  
  protected void addRange(int paramInt1, int paramInt2) { throw new RuntimeException("Not supported."); }
  
  protected void sortRanges() { throw new RuntimeException("Not supported."); }
  
  protected void compactRanges() { throw new RuntimeException("Not supported."); }
  
  protected void mergeRanges(Token paramToken) { throw new RuntimeException("Not supported."); }
  
  protected void subtractRanges(Token paramToken) { throw new RuntimeException("Not supported."); }
  
  protected void intersectRanges(Token paramToken) { throw new RuntimeException("Not supported."); }
  
  static Token complementRanges(Token paramToken) { return RangeToken.complementRanges(paramToken); }
  
  void setMin(int paramInt) {}
  
  void setMax(int paramInt) {}
  
  int getMin() { return -1; }
  
  int getMax() { return -1; }
  
  int getReferenceNumber() { return 0; }
  
  String getString() { return null; }
  
  int getParenNumber() { return 0; }
  
  int getChar() { return -1; }
  
  public String toString() { return toString(0); }
  
  public String toString(int paramInt) { return (this.type == 11) ? "." : ""; }
  
  final int getMinLength() {
    byte b;
    int j;
    int i;
    switch (this.type) {
      case 1:
        i = 0;
        for (j = 0; j < size(); j++)
          i += getChild(j).getMinLength(); 
        return i;
      case 2:
      case 26:
        if (size() == 0)
          return 0; 
        j = getChild(0).getMinLength();
        for (b = 1; b < size(); b++) {
          int k = getChild(b).getMinLength();
          if (k < j)
            j = k; 
        } 
        return j;
      case 3:
      case 9:
        return (getMin() >= 0) ? (getMin() * getChild(0).getMinLength()) : 0;
      case 7:
      case 8:
        return 0;
      case 0:
      case 4:
      case 5:
      case 11:
        return 1;
      case 6:
      case 24:
      case 25:
        return getChild(0).getMinLength();
      case 12:
        return 0;
      case 10:
        return getString().length();
      case 20:
      case 21:
      case 22:
      case 23:
        return 0;
    } 
    throw new RuntimeException("Token#getMinLength(): Invalid Type: " + this.type);
  }
  
  final int getMaxLength() {
    byte b;
    int j;
    int i;
    switch (this.type) {
      case 1:
        i = 0;
        for (j = 0; j < size(); j++) {
          int k = getChild(j).getMaxLength();
          if (k < 0)
            return -1; 
          i += k;
        } 
        return i;
      case 2:
      case 26:
        if (size() == 0)
          return 0; 
        j = getChild(0).getMaxLength();
        for (b = 1; j >= 0 && b < size(); b++) {
          int k = getChild(b).getMaxLength();
          if (k < 0) {
            j = -1;
            break;
          } 
          if (k > j)
            j = k; 
        } 
        return j;
      case 3:
      case 9:
        return (getMax() >= 0) ? (getMax() * getChild(0).getMaxLength()) : -1;
      case 7:
      case 8:
        return 0;
      case 0:
        return 1;
      case 4:
      case 5:
      case 11:
        return 2;
      case 6:
      case 24:
      case 25:
        return getChild(0).getMaxLength();
      case 12:
        return -1;
      case 10:
        return getString().length();
      case 20:
      case 21:
      case 22:
      case 23:
        return 0;
    } 
    throw new RuntimeException("Token#getMaxLength(): Invalid Type: " + this.type);
  }
  
  private static final boolean isSet(int paramInt1, int paramInt2) { return ((paramInt1 & paramInt2) == paramInt2); }
  
  final int analyzeFirstCharacter(RangeToken paramRangeToken, int paramInt) {
    char c;
    int i1;
    int n;
    int m;
    int k;
    boolean bool;
    int j;
    int i;
    switch (this.type) {
      case 1:
        i = 0;
        for (j = 0; j < size() && (i = getChild(j).analyzeFirstCharacter(paramRangeToken, paramInt)) == 0; j++);
        return i;
      case 2:
        if (size() == 0)
          return 0; 
        j = 0;
        bool = false;
        for (k = 0; k < size(); k++) {
          j = getChild(k).analyzeFirstCharacter(paramRangeToken, paramInt);
          if (j == 2)
            break; 
          if (j == 0)
            bool = true; 
        } 
        return bool ? 0 : j;
      case 26:
        k = getChild(0).analyzeFirstCharacter(paramRangeToken, paramInt);
        if (size() == 1)
          return 0; 
        if (k == 2)
          return k; 
        m = getChild(1).analyzeFirstCharacter(paramRangeToken, paramInt);
        return (m == 2) ? m : ((k == 0 || m == 0) ? 0 : 1);
      case 3:
      case 9:
        getChild(0).analyzeFirstCharacter(paramRangeToken, paramInt);
        return 0;
      case 7:
      case 8:
        return 0;
      case 0:
        n = getChar();
        paramRangeToken.addRange(n, n);
        if (n < 65536 && isSet(paramInt, 2)) {
          n = Character.toUpperCase((char)n);
          paramRangeToken.addRange(n, n);
          n = Character.toLowerCase((char)n);
          paramRangeToken.addRange(n, n);
        } 
        return 1;
      case 11:
        return 2;
      case 4:
        paramRangeToken.mergeRanges(this);
        return 1;
      case 5:
        paramRangeToken.mergeRanges(complementRanges(this));
        return 1;
      case 6:
      case 24:
        return getChild(0).analyzeFirstCharacter(paramRangeToken, paramInt);
      case 25:
        paramInt |= ((ModifierToken)this).getOptions();
        paramInt &= (((ModifierToken)this).getOptionsMask() ^ 0xFFFFFFFF);
        return getChild(0).analyzeFirstCharacter(paramRangeToken, paramInt);
      case 12:
        paramRangeToken.addRange(0, 1114111);
        return 2;
      case 10:
        i1 = getString().charAt(0);
        if (REUtil.isHighSurrogate(i1) && getString().length() >= 2 && REUtil.isLowSurrogate(c = getString().charAt(1)))
          i1 = REUtil.composeFromSurrogates(i1, c); 
        paramRangeToken.addRange(i1, i1);
        if (i1 < 65536 && isSet(paramInt, 2)) {
          i1 = Character.toUpperCase((char)i1);
          paramRangeToken.addRange(i1, i1);
          i1 = Character.toLowerCase((char)i1);
          paramRangeToken.addRange(i1, i1);
        } 
        return 1;
      case 20:
      case 21:
      case 22:
      case 23:
        return 0;
    } 
    throw new RuntimeException("Token#analyzeHeadCharacter(): Invalid Type: " + this.type);
  }
  
  private final boolean isShorterThan(Token paramToken) {
    int j;
    int i;
    if (paramToken == null)
      return false; 
    if (this.type == 10) {
      i = getString().length();
    } else {
      throw new RuntimeException("Internal Error: Illegal type: " + this.type);
    } 
    if (paramToken.type == 10) {
      j = paramToken.getString().length();
    } else {
      throw new RuntimeException("Internal Error: Illegal type: " + paramToken.type);
    } 
    return (i < j);
  }
  
  final void findFixedString(FixedStringContainer paramFixedStringContainer, int paramInt) {
    byte b;
    int i;
    Token token;
    switch (this.type) {
      case 1:
        token = null;
        i = 0;
        for (b = 0; b < size(); b++) {
          getChild(b).findFixedString(paramFixedStringContainer, paramInt);
          if (token == null || token.isShorterThan(paramFixedStringContainer.token)) {
            token = paramFixedStringContainer.token;
            i = paramFixedStringContainer.options;
          } 
        } 
        paramFixedStringContainer.token = token;
        paramFixedStringContainer.options = i;
        return;
      case 2:
      case 3:
      case 4:
      case 5:
      case 7:
      case 8:
      case 9:
      case 11:
      case 12:
      case 20:
      case 21:
      case 22:
      case 23:
      case 26:
        paramFixedStringContainer.token = null;
        return;
      case 0:
        paramFixedStringContainer.token = null;
        return;
      case 10:
        paramFixedStringContainer.token = this;
        paramFixedStringContainer.options = paramInt;
        return;
      case 6:
      case 24:
        getChild(0).findFixedString(paramFixedStringContainer, paramInt);
        return;
      case 25:
        paramInt |= ((ModifierToken)this).getOptions();
        paramInt &= (((ModifierToken)this).getOptionsMask() ^ 0xFFFFFFFF);
        getChild(0).findFixedString(paramFixedStringContainer, paramInt);
        return;
    } 
    throw new RuntimeException("Token#findFixedString(): Invalid Type: " + this.type);
  }
  
  boolean match(int paramInt) { throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type); }
  
  protected static RangeToken getRange(String paramString, boolean paramBoolean) {
    if (categories.size() == 0)
      synchronized (categories) {
        Token[] arrayOfToken = new Token[categoryNames.length];
        int i;
        for (i = 0; i < arrayOfToken.length; i++)
          arrayOfToken[i] = createRange(); 
        byte b1;
        for (b1 = 0; b1 < 65536; b1++) {
          i = Character.getType((char)b1);
          if (i == 21 || i == 22) {
            if (b1 == '«' || b1 == '‘' || b1 == '‛' || b1 == '“' || b1 == '‟' || b1 == '‹')
              i = 29; 
            if (b1 == '»' || b1 == '’' || b1 == '”' || b1 == '›')
              i = 30; 
          } 
          arrayOfToken[i].addRange(b1, b1);
          switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
              i = 31;
              break;
            case 6:
            case 7:
            case 8:
              i = 32;
              break;
            case 9:
            case 10:
            case 11:
              i = 33;
              break;
            case 12:
            case 13:
            case 14:
              i = 34;
              break;
            case 0:
            case 15:
            case 16:
            case 18:
            case 19:
              i = 35;
              break;
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 29:
            case 30:
              i = 36;
              break;
            case 25:
            case 26:
            case 27:
            case 28:
              i = 37;
              break;
            default:
              throw new RuntimeException("org.apache.xerces.utils.regex.Token#getRange(): Unknown Unicode category: " + i);
          } 
          arrayOfToken[i].addRange(b1, b1);
        } 
        arrayOfToken[0].addRange(65536, 1114111);
        for (b1 = 0; b1 < arrayOfToken.length; b1++) {
          if (categoryNames[b1] != null) {
            if (b1 == 0)
              arrayOfToken[b1].addRange(65536, 1114111); 
            categories.put(categoryNames[b1], arrayOfToken[b1]);
            categories2.put(categoryNames[b1], complementRanges(arrayOfToken[b1]));
          } 
        } 
        StringBuilder stringBuilder = new StringBuilder(50);
        for (byte b2 = 0; b2 < blockNames.length; b2++) {
          RangeToken rangeToken = createRange();
          if (b2 < 84) {
            byte b = b2 * 2;
            char c1 = "\000ÿĀſƀɏɐʯʰ˿̀ͯͰϿЀӿ԰֏֐׿؀ۿ܀ݏހ޿ऀॿঀ৿਀੿઀૿଀୿஀௿ఀ౿ಀ೿ഀൿ඀෿฀๿຀໿ༀ࿿က႟Ⴀჿᄀᇿሀ፿Ꭰ᏿᐀ᙿ ᚟ᚠ᛿ក៿᠀᢯Ḁỿἀ῿ ⁯⁰₟₠⃏⃐⃿℀⅏⅐↏←⇿∀⋿⌀⏿␀␿⑀⑟①⓿─╿▀▟■◿☀⛿✀➿⠀⣿⺀⻿⼀⿟⿰⿿　〿぀ゟ゠ヿ㄀ㄯ㄰㆏㆐㆟ㆠㆿ㈀㋿㌀㏿㐀䶵一鿿ꀀ꒏꒐꓏가힣豈﫿ﬀﭏﭐ﷿︠︯︰﹏﹐﹯ﹰ﻾﻿﻿＀￯".charAt(b);
            char c2 = "\000ÿĀſƀɏɐʯʰ˿̀ͯͰϿЀӿ԰֏֐׿؀ۿ܀ݏހ޿ऀॿঀ৿਀੿઀૿଀୿஀௿ఀ౿ಀ೿ഀൿ඀෿฀๿຀໿ༀ࿿က႟Ⴀჿᄀᇿሀ፿Ꭰ᏿᐀ᙿ ᚟ᚠ᛿ក៿᠀᢯Ḁỿἀ῿ ⁯⁰₟₠⃏⃐⃿℀⅏⅐↏←⇿∀⋿⌀⏿␀␿⑀⑟①⓿─╿▀▟■◿☀⛿✀➿⠀⣿⺀⻿⼀⿟⿰⿿　〿぀ゟ゠ヿ㄀ㄯ㄰㆏㆐㆟ㆠㆿ㈀㋿㌀㏿㐀䶵一鿿ꀀ꒏꒐꓏가힣豈﫿ﬀﭏﭐ﷿︠︯︰﹏﹐﹯ﹰ﻾﻿﻿＀￯".charAt(b + 1);
            rangeToken.addRange(c1, c2);
          } else {
            byte b = (b2 - 84) * 2;
            rangeToken.addRange(nonBMPBlockRanges[b], nonBMPBlockRanges[b + 1]);
          } 
          String str = blockNames[b2];
          if (str.equals("Specials"))
            rangeToken.addRange(65520, 65533); 
          if (str.equals("Private Use")) {
            rangeToken.addRange(983040, 1048573);
            rangeToken.addRange(1048576, 1114109);
          } 
          categories.put(str, rangeToken);
          categories2.put(str, complementRanges(rangeToken));
          stringBuilder.setLength(0);
          stringBuilder.append("Is");
          if (str.indexOf(' ') >= 0) {
            for (byte b = 0; b < str.length(); b++) {
              if (str.charAt(b) != ' ')
                stringBuilder.append(str.charAt(b)); 
            } 
          } else {
            stringBuilder.append(str);
          } 
          setAlias(stringBuilder.toString(), str, true);
        } 
        setAlias("ASSIGNED", "Cn", false);
        setAlias("UNASSIGNED", "Cn", true);
        RangeToken rangeToken1 = createRange();
        rangeToken1.addRange(0, 1114111);
        categories.put("ALL", rangeToken1);
        categories2.put("ALL", complementRanges(rangeToken1));
        registerNonXS("ASSIGNED");
        registerNonXS("UNASSIGNED");
        registerNonXS("ALL");
        RangeToken rangeToken2 = createRange();
        rangeToken2.mergeRanges(arrayOfToken[1]);
        rangeToken2.mergeRanges(arrayOfToken[2]);
        rangeToken2.mergeRanges(arrayOfToken[5]);
        categories.put("IsAlpha", rangeToken2);
        categories2.put("IsAlpha", complementRanges(rangeToken2));
        registerNonXS("IsAlpha");
        RangeToken rangeToken3 = createRange();
        rangeToken3.mergeRanges(rangeToken2);
        rangeToken3.mergeRanges(arrayOfToken[9]);
        categories.put("IsAlnum", rangeToken3);
        categories2.put("IsAlnum", complementRanges(rangeToken3));
        registerNonXS("IsAlnum");
        RangeToken rangeToken4 = createRange();
        rangeToken4.mergeRanges(token_spaces);
        rangeToken4.mergeRanges(arrayOfToken[34]);
        categories.put("IsSpace", rangeToken4);
        categories2.put("IsSpace", complementRanges(rangeToken4));
        registerNonXS("IsSpace");
        RangeToken rangeToken5 = createRange();
        rangeToken5.mergeRanges(rangeToken3);
        rangeToken5.addRange(95, 95);
        categories.put("IsWord", rangeToken5);
        categories2.put("IsWord", complementRanges(rangeToken5));
        registerNonXS("IsWord");
        RangeToken rangeToken6 = createRange();
        rangeToken6.addRange(0, 127);
        categories.put("IsASCII", rangeToken6);
        categories2.put("IsASCII", complementRanges(rangeToken6));
        registerNonXS("IsASCII");
        RangeToken rangeToken7 = createRange();
        rangeToken7.mergeRanges(arrayOfToken[35]);
        rangeToken7.addRange(32, 32);
        categories.put("IsGraph", complementRanges(rangeToken7));
        categories2.put("IsGraph", rangeToken7);
        registerNonXS("IsGraph");
        RangeToken rangeToken8 = createRange();
        rangeToken8.addRange(48, 57);
        rangeToken8.addRange(65, 70);
        rangeToken8.addRange(97, 102);
        categories.put("IsXDigit", complementRanges(rangeToken8));
        categories2.put("IsXDigit", rangeToken8);
        registerNonXS("IsXDigit");
        setAlias("IsDigit", "Nd", true);
        setAlias("IsUpper", "Lu", true);
        setAlias("IsLower", "Ll", true);
        setAlias("IsCntrl", "C", true);
        setAlias("IsPrint", "C", false);
        setAlias("IsPunct", "P", true);
        registerNonXS("IsDigit");
        registerNonXS("IsUpper");
        registerNonXS("IsLower");
        registerNonXS("IsCntrl");
        registerNonXS("IsPrint");
        registerNonXS("IsPunct");
        setAlias("alpha", "IsAlpha", true);
        setAlias("alnum", "IsAlnum", true);
        setAlias("ascii", "IsASCII", true);
        setAlias("cntrl", "IsCntrl", true);
        setAlias("digit", "IsDigit", true);
        setAlias("graph", "IsGraph", true);
        setAlias("lower", "IsLower", true);
        setAlias("print", "IsPrint", true);
        setAlias("punct", "IsPunct", true);
        setAlias("space", "IsSpace", true);
        setAlias("upper", "IsUpper", true);
        setAlias("word", "IsWord", true);
        setAlias("xdigit", "IsXDigit", true);
        registerNonXS("alpha");
        registerNonXS("alnum");
        registerNonXS("ascii");
        registerNonXS("cntrl");
        registerNonXS("digit");
        registerNonXS("graph");
        registerNonXS("lower");
        registerNonXS("print");
        registerNonXS("punct");
        registerNonXS("space");
        registerNonXS("upper");
        registerNonXS("word");
        registerNonXS("xdigit");
      }  
    return paramBoolean ? (RangeToken)categories.get(paramString) : (RangeToken)categories2.get(paramString);
  }
  
  protected static RangeToken getRange(String paramString, boolean paramBoolean1, boolean paramBoolean2) {
    RangeToken rangeToken = getRange(paramString, paramBoolean1);
    if (paramBoolean2 && rangeToken != null && isRegisterNonXS(paramString))
      rangeToken = null; 
    return rangeToken;
  }
  
  protected static void registerNonXS(String paramString) { nonxs.add(paramString); }
  
  protected static boolean isRegisterNonXS(String paramString) { return nonxs.contains(paramString); }
  
  private static void setAlias(String paramString1, String paramString2, boolean paramBoolean) {
    Token token1 = (Token)categories.get(paramString2);
    Token token2 = (Token)categories2.get(paramString2);
    if (paramBoolean) {
      categories.put(paramString1, token1);
      categories2.put(paramString1, token2);
    } else {
      categories2.put(paramString1, token1);
      categories.put(paramString1, token2);
    } 
  }
  
  static Token getGraphemePattern() {
    if (token_grapheme != null)
      return token_grapheme; 
    RangeToken rangeToken1 = createRange();
    rangeToken1.mergeRanges(getRange("ASSIGNED", true));
    rangeToken1.subtractRanges(getRange("M", true));
    rangeToken1.subtractRanges(getRange("C", true));
    RangeToken rangeToken2 = createRange();
    for (byte b = 0; b < "्্੍્୍்్್്ฺ྄".length(); b++)
      rangeToken2.addRange(b, b); 
    RangeToken rangeToken3 = createRange();
    rangeToken3.mergeRanges(getRange("M", true));
    rangeToken3.addRange(4448, 4607);
    rangeToken3.addRange(65438, 65439);
    UnionToken unionToken1 = createUnion();
    unionToken1.addChild(rangeToken1);
    unionToken1.addChild(token_empty);
    UnionToken unionToken2 = createUnion();
    unionToken2.addChild(createConcat(rangeToken2, getRange("L", true)));
    unionToken2.addChild(rangeToken3);
    ClosureToken closureToken = createClosure(unionToken2);
    ConcatToken concatToken = createConcat(unionToken1, closureToken);
    token_grapheme = concatToken;
    return token_grapheme;
  }
  
  static Token getCombiningCharacterSequence() {
    if (token_ccs != null)
      return token_ccs; 
    ClosureToken closureToken = createClosure(getRange("M", true));
    ConcatToken concatToken = createConcat(getRange("M", false), closureToken);
    token_ccs = concatToken;
    return token_ccs;
  }
  
  static  {
    token_0to9 = (token_dot = new Token(11)).createRange();
    token_0to9.addRange(48, 57);
    token_wordchars = createRange();
    token_wordchars.addRange(48, 57);
    token_wordchars.addRange(65, 90);
    token_wordchars.addRange(95, 95);
    token_wordchars.addRange(97, 122);
    token_spaces = createRange();
    token_spaces.addRange(9, 9);
    token_spaces.addRange(10, 10);
    token_spaces.addRange(12, 12);
    token_spaces.addRange(13, 13);
    token_spaces.addRange(32, 32);
    token_not_spaces = (token_not_wordchars = (token_not_0to9 = complementRanges(token_0to9)).complementRanges(token_wordchars)).complementRanges(token_spaces);
    categories = new HashMap();
    categories2 = new HashMap();
    categoryNames = new String[] { 
        "Cn", "Lu", "Ll", "Lt", "Lm", "Lo", "Mn", "Me", "Mc", "Nd", 
        "Nl", "No", "Zs", "Zl", "Zp", "Cc", "Cf", null, "Co", "Cs", 
        "Pd", "Ps", "Pe", "Pc", "Po", "Sm", "Sc", "Sk", "So", "Pi", 
        "Pf", "L", "M", "N", "Z", "C", "P", "S" };
    blockNames = new String[] { 
        "Basic Latin", "Latin-1 Supplement", "Latin Extended-A", "Latin Extended-B", "IPA Extensions", "Spacing Modifier Letters", "Combining Diacritical Marks", "Greek", "Cyrillic", "Armenian", 
        "Hebrew", "Arabic", "Syriac", "Thaana", "Devanagari", "Bengali", "Gurmukhi", "Gujarati", "Oriya", "Tamil", 
        "Telugu", "Kannada", "Malayalam", "Sinhala", "Thai", "Lao", "Tibetan", "Myanmar", "Georgian", "Hangul Jamo", 
        "Ethiopic", "Cherokee", "Unified Canadian Aboriginal Syllabics", "Ogham", "Runic", "Khmer", "Mongolian", "Latin Extended Additional", "Greek Extended", "General Punctuation", 
        "Superscripts and Subscripts", "Currency Symbols", "Combining Marks for Symbols", "Letterlike Symbols", "Number Forms", "Arrows", "Mathematical Operators", "Miscellaneous Technical", "Control Pictures", "Optical Character Recognition", 
        "Enclosed Alphanumerics", "Box Drawing", "Block Elements", "Geometric Shapes", "Miscellaneous Symbols", "Dingbats", "Braille Patterns", "CJK Radicals Supplement", "Kangxi Radicals", "Ideographic Description Characters", 
        "CJK Symbols and Punctuation", "Hiragana", "Katakana", "Bopomofo", "Hangul Compatibility Jamo", "Kanbun", "Bopomofo Extended", "Enclosed CJK Letters and Months", "CJK Compatibility", "CJK Unified Ideographs Extension A", 
        "CJK Unified Ideographs", "Yi Syllables", "Yi Radicals", "Hangul Syllables", "Private Use", "CJK Compatibility Ideographs", "Alphabetic Presentation Forms", "Arabic Presentation Forms-A", "Combining Half Marks", "CJK Compatibility Forms", 
        "Small Form Variants", "Arabic Presentation Forms-B", "Specials", "Halfwidth and Fullwidth Forms", "Old Italic", "Gothic", "Deseret", "Byzantine Musical Symbols", "Musical Symbols", "Mathematical Alphanumeric Symbols", 
        "CJK Unified Ideographs Extension B", "CJK Compatibility Ideographs Supplement", "Tags" };
    nonBMPBlockRanges = new int[] { 
        66304, 66351, 66352, 66383, 66560, 66639, 118784, 119039, 119040, 119295, 
        119808, 120831, 131072, 173782, 194560, 195103, 917504, 917631 };
    nonxs = Collections.synchronizedSet(new HashSet());
    token_grapheme = null;
    token_ccs = null;
  }
  
  static class CharToken extends Token implements Serializable {
    private static final long serialVersionUID = -4394272816279496989L;
    
    final int chardata;
    
    CharToken(int param1Int1, int param1Int2) {
      super(param1Int1);
      this.chardata = param1Int2;
    }
    
    int getChar() { return this.chardata; }
    
    public String toString(int param1Int) {
      switch (this.type) {
        case 0:
          switch (this.chardata) {
            case 40:
            case 41:
            case 42:
            case 43:
            case 46:
            case 63:
            case 91:
            case 92:
            case 123:
            case 124:
              return "\\" + (char)this.chardata;
            case 12:
              return "\\f";
            case 10:
              return "\\n";
            case 13:
              return "\\r";
            case 9:
              return "\\t";
            case 27:
              return "\\e";
          } 
          if (this.chardata >= 65536) {
            String str = "0" + Integer.toHexString(this.chardata);
            null = "\\v" + str.substring(str.length() - 6, str.length());
          } else {
            null = "" + (char)this.chardata;
          } 
          return null;
        case 8:
          if (this == Token.token_linebeginning || this == Token.token_lineend) {
            null = "" + (char)this.chardata;
          } else {
            null = "\\" + (char)this.chardata;
          } 
          return null;
      } 
      return null;
    }
    
    boolean match(int param1Int) {
      if (this.type == 0)
        return (param1Int == this.chardata); 
      throw new RuntimeException("NFAArrow#match(): Internal error: " + this.type);
    }
  }
  
  static class ClosureToken extends Token implements Serializable {
    private static final long serialVersionUID = 1308971930673997452L;
    
    int min;
    
    int max;
    
    final Token child;
    
    ClosureToken(int param1Int, Token param1Token) {
      super(param1Int);
      this.child = param1Token;
      setMin(-1);
      setMax(-1);
    }
    
    int size() { return 1; }
    
    Token getChild(int param1Int) { return this.child; }
    
    final void setMin(int param1Int) { this.min = param1Int; }
    
    final void setMax(int param1Int) { this.max = param1Int; }
    
    final int getMin() { return this.min; }
    
    final int getMax() { return this.max; }
    
    public String toString(int param1Int) {
      String str;
      if (this.type == 3) {
        if (getMin() < 0 && getMax() < 0) {
          str = this.child.toString(param1Int) + "*";
        } else if (getMin() == getMax()) {
          str = this.child.toString(param1Int) + "{" + getMin() + "}";
        } else if (getMin() >= 0 && getMax() >= 0) {
          str = this.child.toString(param1Int) + "{" + getMin() + "," + getMax() + "}";
        } else if (getMin() >= 0 && getMax() < 0) {
          str = this.child.toString(param1Int) + "{" + getMin() + ",}";
        } else {
          throw new RuntimeException("Token#toString(): CLOSURE " + getMin() + ", " + getMax());
        } 
      } else if (getMin() < 0 && getMax() < 0) {
        str = this.child.toString(param1Int) + "*?";
      } else if (getMin() == getMax()) {
        str = this.child.toString(param1Int) + "{" + getMin() + "}?";
      } else if (getMin() >= 0 && getMax() >= 0) {
        str = this.child.toString(param1Int) + "{" + getMin() + "," + getMax() + "}?";
      } else if (getMin() >= 0 && getMax() < 0) {
        str = this.child.toString(param1Int) + "{" + getMin() + ",}?";
      } else {
        throw new RuntimeException("Token#toString(): NONGREEDYCLOSURE " + getMin() + ", " + getMax());
      } 
      return str;
    }
  }
  
  static class ConcatToken extends Token implements Serializable {
    private static final long serialVersionUID = 8717321425541346381L;
    
    final Token child;
    
    final Token child2;
    
    ConcatToken(Token param1Token1, Token param1Token2) {
      super(1);
      this.child = param1Token1;
      this.child2 = param1Token2;
    }
    
    int size() { return 2; }
    
    Token getChild(int param1Int) { return (param1Int == 0) ? this.child : this.child2; }
    
    public String toString(int param1Int) {
      String str;
      if (this.child2.type == 3 && this.child2.getChild(false) == this.child) {
        str = this.child.toString(param1Int) + "+";
      } else if (this.child2.type == 9 && this.child2.getChild(false) == this.child) {
        str = this.child.toString(param1Int) + "+?";
      } else {
        str = this.child.toString(param1Int) + this.child2.toString(param1Int);
      } 
      return str;
    }
  }
  
  static class ConditionToken extends Token implements Serializable {
    private static final long serialVersionUID = 4353765277910594411L;
    
    final int refNumber;
    
    final Token condition;
    
    final Token yes;
    
    final Token no;
    
    ConditionToken(int param1Int, Token param1Token1, Token param1Token2, Token param1Token3) {
      super(26);
      this.refNumber = param1Int;
      this.condition = param1Token1;
      this.yes = param1Token2;
      this.no = param1Token3;
    }
    
    int size() { return (this.no == null) ? 1 : 2; }
    
    Token getChild(int param1Int) {
      if (param1Int == 0)
        return this.yes; 
      if (param1Int == 1)
        return this.no; 
      throw new RuntimeException("Internal Error: " + param1Int);
    }
    
    public String toString(int param1Int) {
      String str;
      if (this.refNumber > 0) {
        str = "(?(" + this.refNumber + ")";
      } else if (this.condition.type == 8) {
        str = "(?(" + this.condition + ")";
      } else {
        str = "(?" + this.condition;
      } 
      if (this.no == null) {
        str = str + this.yes + ")";
      } else {
        str = str + this.yes + "|" + this.no + ")";
      } 
      return str;
    }
  }
  
  static class FixedStringContainer {
    Token token = null;
    
    int options = 0;
  }
  
  static class ModifierToken extends Token implements Serializable {
    private static final long serialVersionUID = -9114536559696480356L;
    
    final Token child;
    
    final int add;
    
    final int mask;
    
    ModifierToken(Token param1Token, int param1Int1, int param1Int2) {
      super(25);
      this.child = param1Token;
      this.add = param1Int1;
      this.mask = param1Int2;
    }
    
    int size() { return 1; }
    
    Token getChild(int param1Int) { return this.child; }
    
    int getOptions() { return this.add; }
    
    int getOptionsMask() { return this.mask; }
    
    public String toString(int param1Int) { return "(?" + ((this.add == 0) ? "" : REUtil.createOptionString(this.add)) + ((this.mask == 0) ? "" : REUtil.createOptionString(this.mask)) + ":" + this.child.toString(param1Int) + ")"; }
  }
  
  static class ParenToken extends Token implements Serializable {
    private static final long serialVersionUID = -5938014719827987704L;
    
    final Token child;
    
    final int parennumber;
    
    ParenToken(int param1Int1, Token param1Token, int param1Int2) {
      super(param1Int1);
      this.child = param1Token;
      this.parennumber = param1Int2;
    }
    
    int size() { return 1; }
    
    Token getChild(int param1Int) { return this.child; }
    
    int getParenNumber() { return this.parennumber; }
    
    public String toString(int param1Int) {
      String str = null;
      switch (this.type) {
        case 6:
          if (this.parennumber == 0) {
            str = "(?:" + this.child.toString(param1Int) + ")";
            break;
          } 
          str = "(" + this.child.toString(param1Int) + ")";
          break;
        case 20:
          str = "(?=" + this.child.toString(param1Int) + ")";
          break;
        case 21:
          str = "(?!" + this.child.toString(param1Int) + ")";
          break;
        case 22:
          str = "(?<=" + this.child.toString(param1Int) + ")";
          break;
        case 23:
          str = "(?<!" + this.child.toString(param1Int) + ")";
          break;
        case 24:
          str = "(?>" + this.child.toString(param1Int) + ")";
          break;
      } 
      return str;
    }
  }
  
  static class StringToken extends Token implements Serializable {
    private static final long serialVersionUID = -4614366944218504172L;
    
    String string;
    
    final int refNumber;
    
    StringToken(int param1Int1, String param1String, int param1Int2) {
      super(param1Int1);
      this.string = param1String;
      this.refNumber = param1Int2;
    }
    
    int getReferenceNumber() { return this.refNumber; }
    
    String getString() { return this.string; }
    
    public String toString(int param1Int) { return (this.type == 12) ? ("\\" + this.refNumber) : REUtil.quoteMeta(this.string); }
  }
  
  static class UnionToken extends Token implements Serializable {
    private static final long serialVersionUID = -2568843945989489861L;
    
    List<Token> children;
    
    private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("children", Vector.class) };
    
    UnionToken(int param1Int) { super(param1Int); }
    
    void addChild(Token param1Token) {
      StringBuilder stringBuilder;
      if (param1Token == null)
        return; 
      if (this.children == null)
        this.children = new ArrayList(); 
      if (this.type == 2) {
        this.children.add(param1Token);
        return;
      } 
      if (param1Token.type == 1) {
        for (byte b = 0; b < param1Token.size(); b++)
          addChild(param1Token.getChild(b)); 
        return;
      } 
      int i = this.children.size();
      if (i == 0) {
        this.children.add(param1Token);
        return;
      } 
      Token token = (Token)this.children.get(i - 1);
      if ((token.type != 0 && token.type != 10) || (param1Token.type != 0 && param1Token.type != 10)) {
        this.children.add(param1Token);
        return;
      } 
      int j = (param1Token.type == 0) ? 2 : param1Token.getString().length();
      if (token.type == 0) {
        stringBuilder = new StringBuilder(2 + j);
        int k = token.getChar();
        if (k >= 65536) {
          stringBuilder.append(REUtil.decomposeToSurrogates(k));
        } else {
          stringBuilder.append((char)k);
        } 
        token = Token.createString(null);
        this.children.set(i - 1, token);
      } else {
        stringBuilder = new StringBuilder(token.getString().length() + j);
        stringBuilder.append(token.getString());
      } 
      if (param1Token.type == 0) {
        int k = param1Token.getChar();
        if (k >= 65536) {
          stringBuilder.append(REUtil.decomposeToSurrogates(k));
        } else {
          stringBuilder.append((char)k);
        } 
      } else {
        stringBuilder.append(param1Token.getString());
      } 
      ((Token.StringToken)token).string = new String(stringBuilder);
    }
    
    int size() { return (this.children == null) ? 0 : this.children.size(); }
    
    Token getChild(int param1Int) { return (Token)this.children.get(param1Int); }
    
    public String toString(int param1Int) {
      String str;
      if (this.type == 1) {
        if (this.children.size() == 2) {
          Token token1 = getChild(0);
          Token token2 = getChild(1);
          if (token2.type == 3 && token2.getChild(false) == token1) {
            str = token1.toString(param1Int) + "+";
          } else if (token2.type == 9 && token2.getChild(false) == token1) {
            str = token1.toString(param1Int) + "+?";
          } else {
            str = token1.toString(param1Int) + token2.toString(param1Int);
          } 
        } else {
          StringBuilder stringBuilder = new StringBuilder();
          for (byte b = 0; b < this.children.size(); b++)
            stringBuilder.append(((Token)this.children.get(b)).toString(param1Int)); 
          str = new String(stringBuilder);
        } 
        return str;
      } 
      if (this.children.size() == 2 && (getChild(1)).type == 7) {
        str = getChild(0).toString(param1Int) + "?";
      } else if (this.children.size() == 2 && (getChild(0)).type == 7) {
        str = getChild(1).toString(param1Int) + "??";
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(((Token)this.children.get(0)).toString(param1Int));
        for (byte b = 1; b < this.children.size(); b++) {
          stringBuilder.append('|');
          stringBuilder.append(((Token)this.children.get(b)).toString(param1Int));
        } 
        str = new String(stringBuilder);
      } 
      return str;
    }
    
    private void writeObject(ObjectOutputStream param1ObjectOutputStream) throws IOException {
      Vector vector = (this.children == null) ? null : new Vector(this.children);
      ObjectOutputStream.PutField putField = param1ObjectOutputStream.putFields();
      putField.put("children", vector);
      param1ObjectOutputStream.writeFields();
    }
    
    private void readObject(ObjectInputStream param1ObjectInputStream) throws IOException, ClassNotFoundException {
      ObjectInputStream.GetField getField = param1ObjectInputStream.readFields();
      Vector vector = (Vector)getField.get("children", null);
      if (vector != null)
        this.children = new ArrayList(vector); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\Token.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
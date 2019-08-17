package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class ParserForXMLSchema extends RegexParser {
  private static Map<String, Token> ranges = null;
  
  private static Map<String, Token> ranges2 = null;
  
  private static final String SPACES = "\t\n\r\r  ";
  
  private static final String NAMECHARS = "-.0:AZ__az··ÀÖØöøıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁːˑ̀͠͡ͅΆΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁ҃҆ҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆֹֻֽֿֿׁׂ֑֣֡ׄׄאתװײءغـْ٠٩ٰڷںھۀێېۓە۪ۭۨ۰۹ँःअह़्॑॔क़ॣ०९ঁঃঅঌএঐওনপরললশহ়়াৄেৈো্ৗৗড়ঢ়য়ৣ০ৱਂਂਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹ਼਼ਾੂੇੈੋ੍ਖ਼ੜਫ਼ਫ਼੦ੴઁઃઅઋઍઍએઑઓનપરલળવહ઼ૅેૉો્ૠૠ૦૯ଁଃଅଌଏଐଓନପରଲଳଶହ଼ୃେୈୋ୍ୖୗଡ଼ଢ଼ୟୡ୦୯ஂஃஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹாூெைொ்ௗௗ௧௯ఁఃఅఌఎఐఒనపళవహాౄెైొ్ౕౖౠౡ౦౯ಂಃಅಌಎಐಒನಪಳವಹಾೄೆೈೊ್ೕೖೞೞೠೡ೦೯ംഃഅഌഎഐഒനപഹാൃെൈൊ്ൗൗൠൡ൦൯กฮะฺเ๎๐๙ກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະູົຽເໄໆໆ່ໍ໐໙༘༙༠༩༹༹༵༵༷༷༾ཇཉཀྵ྄ཱ྆ྋྐྕྗྗྙྭྱྷྐྵྐྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼ⃐⃜⃡⃡ΩΩKÅ℮℮ↀↂ々々〇〇〡〯〱〵ぁゔ゙゚ゝゞァヺーヾㄅㄬ一龥가힣";
  
  private static final String LETTERS = "AZazÀÖØöøıĴľŁňŊžƀǰǴǵǺȗɐʨʻˁʰˑΆΆΈΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆאתװײءغفيٱڷںھۀێېۓەەۥۦअहऽऽक़ॡঅঌএঐওনপরললশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜਫ਼ਫ਼ੲੴઅઋઍઍએઑઓનપરલળવહઽઽૠૠଅଌଏଐଓନପରଲଳଶହଽଽଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೞೞೠೡഅഌഎഐഒനപഹൠൡกฮะะาำเๅກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະະາຳຽຽເໄཀཇཉཀྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼΩΩKÅ℮℮ↀↂ〇〇〡〩ぁゔァヺㄅㄬ一龥가힣ｦﾟ";
  
  private static final int[] LETTERS_INT = { 120720, 120744, 120746, 120777, 195099, 195101 };
  
  private static final String DIGITS = "09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩၀၉፩፱០៩᠐᠙０９";
  
  private static final int[] DIGITS_INT = { 120782, 120831 };
  
  public ParserForXMLSchema() {}
  
  public ParserForXMLSchema(Locale paramLocale) { super(paramLocale); }
  
  Token processCaret() throws ParseException {
    next();
    return Token.createChar(94);
  }
  
  Token processDollar() throws ParseException {
    next();
    return Token.createChar(36);
  }
  
  Token processLookahead() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processNegativelookahead() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processLookbehind() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processNegativelookbehind() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_A() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_Z() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_z() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_b() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_B() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_lt() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_gt() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processStar(Token paramToken) throws ParseException {
    next();
    return Token.createClosure(paramToken);
  }
  
  Token processPlus(Token paramToken) throws ParseException {
    next();
    return Token.createConcat(paramToken, Token.createClosure(paramToken));
  }
  
  Token processQuestion(Token paramToken) throws ParseException {
    next();
    Token.UnionToken unionToken = Token.createUnion();
    unionToken.addChild(paramToken);
    unionToken.addChild(Token.createEmpty());
    return unionToken;
  }
  
  boolean checkQuestion(int paramInt) { return false; }
  
  Token processParen() throws ParseException {
    next();
    Token.ParenToken parenToken = Token.createParen(parseRegex(), 0);
    if (read() != 7)
      throw ex("parser.factor.1", this.offset - 1); 
    next();
    return parenToken;
  }
  
  Token processParen2() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processCondition() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processModifiers() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processIndependent() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token processBacksolidus_c() throws ParseException {
    next();
    return getTokenForShorthand(99);
  }
  
  Token processBacksolidus_C() throws ParseException {
    next();
    return getTokenForShorthand(67);
  }
  
  Token processBacksolidus_i() throws ParseException {
    next();
    return getTokenForShorthand(105);
  }
  
  Token processBacksolidus_I() throws ParseException {
    next();
    return getTokenForShorthand(73);
  }
  
  Token processBacksolidus_g() throws ParseException { throw ex("parser.process.1", this.offset - 2); }
  
  Token processBacksolidus_X() throws ParseException { throw ex("parser.process.1", this.offset - 2); }
  
  Token processBackreference() throws ParseException { throw ex("parser.process.1", this.offset - 4); }
  
  int processCIinCharacterClass(RangeToken paramRangeToken, int paramInt) {
    paramRangeToken.mergeRanges(getTokenForShorthand(paramInt));
    return -1;
  }
  
  protected RangeToken parseCharacterClass(boolean paramBoolean) throws ParseException {
    RangeToken rangeToken2;
    setContext(1);
    next();
    boolean bool1 = false;
    boolean bool2 = false;
    RangeToken rangeToken1 = null;
    if (read() == 0 && this.chardata == 94) {
      bool1 = true;
      next();
      rangeToken1 = Token.createRange();
      rangeToken1.addRange(0, 1114111);
      rangeToken2 = Token.createRange();
    } else {
      rangeToken2 = Token.createRange();
    } 
    int i;
    boolean bool3;
    for (bool3 = true; (i = read()) != 1; bool3 = false) {
      bool2 = false;
      if (i == 0 && this.chardata == 93 && !bool3) {
        if (bool1) {
          rangeToken1.subtractRanges(rangeToken2);
          rangeToken2 = rangeToken1;
        } 
        break;
      } 
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
          case 45:
            j = decodeEscaped();
            bool2 = true;
            break;
          default:
            j = decodeEscaped();
            break;
        } 
      } else if (i == 24 && !bool3) {
        if (bool1) {
          rangeToken1.subtractRanges(rangeToken2);
          rangeToken2 = rangeToken1;
        } 
        RangeToken rangeToken = parseCharacterClass(false);
        rangeToken2.subtractRanges(rangeToken);
        if (read() != 0 || this.chardata != 93)
          throw ex("parser.cc.5", this.offset); 
        break;
      } 
      next();
      if (!bool) {
        if (i == 0) {
          if (j == 91)
            throw ex("parser.cc.6", this.offset - 2); 
          if (j == 93)
            throw ex("parser.cc.7", this.offset - 2); 
          if (j == 45 && this.chardata != 93 && !bool3)
            throw ex("parser.cc.8", this.offset - 2); 
        } 
        if (read() != 0 || this.chardata != 45 || (j == 45 && bool3)) {
          if (!isSet(2) || j > 65535) {
            rangeToken2.addRange(j, j);
          } else {
            addCaseInsensitiveChar(rangeToken2, j);
          } 
        } else {
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
            if (i == 24)
              throw ex("parser.cc.8", this.offset - 1); 
            int k = this.chardata;
            if (i == 0) {
              if (k == 91)
                throw ex("parser.cc.6", this.offset - 1); 
              if (k == 93)
                throw ex("parser.cc.7", this.offset - 1); 
              if (k == 45)
                throw ex("parser.cc.8", this.offset - 2); 
            } else if (i == 10) {
              k = decodeEscaped();
            } 
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
      } 
    } 
    if (read() == 1)
      throw ex("parser.cc.2", this.offset); 
    rangeToken2.sortRanges();
    rangeToken2.compactRanges();
    setContext(0);
    next();
    return rangeToken2;
  }
  
  protected RangeToken parseSetOperations() throws ParseException { throw ex("parser.process.1", this.offset); }
  
  Token getTokenForShorthand(int paramInt) {
    switch (paramInt) {
      case 100:
        return getRange("xml:isDigit", true);
      case 68:
        return getRange("xml:isDigit", false);
      case 119:
        return getRange("xml:isWord", true);
      case 87:
        return getRange("xml:isWord", false);
      case 115:
        return getRange("xml:isSpace", true);
      case 83:
        return getRange("xml:isSpace", false);
      case 99:
        return getRange("xml:isNameChar", true);
      case 67:
        return getRange("xml:isNameChar", false);
      case 105:
        return getRange("xml:isInitialNameChar", true);
      case 73:
        return getRange("xml:isInitialNameChar", false);
    } 
    throw new RuntimeException("Internal Error: shorthands: \\u" + Integer.toString(paramInt, 16));
  }
  
  int decodeEscaped() throws ParseException {
    if (read() != 10)
      throw ex("parser.next.1", this.offset - 1); 
    int i = this.chardata;
    switch (i) {
      case 110:
        i = 10;
      case 114:
        i = 13;
      case 116:
        i = 9;
      case 40:
      case 41:
      case 42:
      case 43:
      case 45:
      case 46:
      case 63:
      case 91:
      case 92:
      case 93:
      case 94:
      case 123:
      case 124:
      case 125:
        return i;
    } 
    throw ex("parser.process.1", this.offset - 2);
  }
  
  protected static RangeToken getRange(String paramString, boolean paramBoolean) {
    if (ranges == null) {
      ranges = new HashMap();
      ranges2 = new HashMap();
      RangeToken rangeToken = Token.createRange();
      setupRange(rangeToken, "\t\n\r\r  ");
      ranges.put("xml:isSpace", rangeToken);
      ranges2.put("xml:isSpace", Token.complementRanges(rangeToken));
      rangeToken = Token.createRange();
      setupRange(rangeToken, "09٠٩۰۹०९০৯੦੯૦૯୦୯௧௯౦౯೦೯൦൯๐๙໐໙༠༩၀၉፩፱០៩᠐᠙０９");
      setupRange(rangeToken, DIGITS_INT);
      ranges.put("xml:isDigit", rangeToken);
      ranges2.put("xml:isDigit", Token.complementRanges(rangeToken));
      rangeToken = Token.createRange();
      setupRange(rangeToken, "AZazÀÖØöøıĴľŁňŊžƀǰǴǵǺȗɐʨʻˁʰˑΆΆΈΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆאתװײءغفيٱڷںھۀێېۓەەۥۦअहऽऽक़ॡঅঌএঐওনপরললশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜਫ਼ਫ਼ੲੴઅઋઍઍએઑઓનપરલળવહઽઽૠૠଅଌଏଐଓନପରଲଳଶହଽଽଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೞೞೠೡഅഌഎഐഒനപഹൠൡกฮะะาำเๅກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະະາຳຽຽເໄཀཇཉཀྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼΩΩKÅ℮℮ↀↂ〇〇〡〩ぁゔァヺㄅㄬ一龥가힣ｦﾟ");
      setupRange(rangeToken, LETTERS_INT);
      rangeToken.mergeRanges((Token)ranges.get("xml:isDigit"));
      ranges.put("xml:isWord", rangeToken);
      ranges2.put("xml:isWord", Token.complementRanges(rangeToken));
      rangeToken = Token.createRange();
      setupRange(rangeToken, "-.0:AZ__az··ÀÖØöøıĴľŁňŊžƀǃǍǰǴǵǺȗɐʨʻˁːˑ̀͠͡ͅΆΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁ҃҆ҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆֹֻֽֿֿׁׂ֑֣֡ׄׄאתװײءغـْ٠٩ٰڷںھۀێېۓە۪ۭۨ۰۹ँःअह़्॑॔क़ॣ०९ঁঃঅঌএঐওনপরললশহ়়াৄেৈো্ৗৗড়ঢ়য়ৣ০ৱਂਂਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹ਼਼ਾੂੇੈੋ੍ਖ਼ੜਫ਼ਫ਼੦ੴઁઃઅઋઍઍએઑઓનપરલળવહ઼ૅેૉો્ૠૠ૦૯ଁଃଅଌଏଐଓନପରଲଳଶହ଼ୃେୈୋ୍ୖୗଡ଼ଢ଼ୟୡ୦୯ஂஃஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹாூெைொ்ௗௗ௧௯ఁఃఅఌఎఐఒనపళవహాౄెైొ్ౕౖౠౡ౦౯ಂಃಅಌಎಐಒನಪಳವಹಾೄೆೈೊ್ೕೖೞೞೠೡ೦೯ംഃഅഌഎഐഒനപഹാൃെൈൊ്ൗൗൠൡ൦൯กฮะฺเ๎๐๙ກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະູົຽເໄໆໆ່ໍ໐໙༘༙༠༩༹༹༵༵༷༷༾ཇཉཀྵ྄ཱ྆ྋྐྕྗྗྙྭྱྷྐྵྐྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼ⃐⃜⃡⃡ΩΩKÅ℮℮ↀↂ々々〇〇〡〯〱〵ぁゔ゙゚ゝゞァヺーヾㄅㄬ一龥가힣");
      ranges.put("xml:isNameChar", rangeToken);
      ranges2.put("xml:isNameChar", Token.complementRanges(rangeToken));
      rangeToken = Token.createRange();
      setupRange(rangeToken, "AZazÀÖØöøıĴľŁňŊžƀǰǴǵǺȗɐʨʻˁʰˑΆΆΈΊΌΌΎΡΣώϐϖϚϚϜϜϞϞϠϠϢϳЁЌЎяёќўҁҐӄӇӈӋӌӐӫӮӵӸӹԱՖՙՙաֆאתװײءغفيٱڷںھۀێېۓەەۥۦअहऽऽक़ॡঅঌএঐওনপরললশহড়ঢ়য়ৡৰৱਅਊਏਐਓਨਪਰਲਲ਼ਵਸ਼ਸਹਖ਼ੜਫ਼ਫ਼ੲੴઅઋઍઍએઑઓનપરલળવહઽઽૠૠଅଌଏଐଓନପରଲଳଶହଽଽଡ଼ଢ଼ୟୡஅஊஎஐஒகஙசஜஜஞடணதநபமவஷஹఅఌఎఐఒనపళవహౠౡಅಌಎಐಒನಪಳವಹೞೞೠೡഅഌഎഐഒനപഹൠൡกฮะะาำเๅກຂຄຄງຈຊຊຍຍດທນຟມຣລລວວສຫອຮະະາຳຽຽເໄཀཇཉཀྵႠჅაჶᄀᄀᄂᄃᄅᄇᄉᄉᄋᄌᄎᄒᄼᄼᄾᄾᅀᅀᅌᅌᅎᅎᅐᅐᅔᅕᅙᅙᅟᅡᅣᅣᅥᅥᅧᅧᅩᅩᅭᅮᅲᅳᅵᅵᆞᆞᆨᆨᆫᆫᆮᆯᆷᆸᆺᆺᆼᇂᇫᇫᇰᇰᇹᇹḀẛẠỹἀἕἘἝἠὅὈὍὐὗὙὙὛὛὝὝὟώᾀᾴᾶᾼιιῂῄῆῌῐΐῖΊῠῬῲῴῶῼΩΩKÅ℮℮ↀↂ〇〇〡〩ぁゔァヺㄅㄬ一龥가힣ｦﾟ");
      rangeToken.addRange(95, 95);
      rangeToken.addRange(58, 58);
      ranges.put("xml:isInitialNameChar", rangeToken);
      ranges2.put("xml:isInitialNameChar", Token.complementRanges(rangeToken));
    } 
    return paramBoolean ? (RangeToken)ranges.get(paramString) : (RangeToken)ranges2.get(paramString);
  }
  
  static void setupRange(Token paramToken, String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b += 2)
      paramToken.addRange(paramString.charAt(b), paramString.charAt(b + 1)); 
  }
  
  static void setupRange(Token paramToken, int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    for (boolean bool = false; bool < i; bool += true)
      paramToken.addRange(paramArrayOfInt[bool], paramArrayOfInt[bool + true]); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\ParserForXMLSchema.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
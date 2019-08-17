package java.lang;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Character extends Object implements Serializable, Comparable<Character> {
  public static final int MIN_RADIX = 2;
  
  public static final int MAX_RADIX = 36;
  
  public static final char MIN_VALUE = '\000';
  
  public static final char MAX_VALUE = '￿';
  
  public static final Class<Character> TYPE = Class.getPrimitiveClass("char");
  
  public static final byte UNASSIGNED = 0;
  
  public static final byte UPPERCASE_LETTER = 1;
  
  public static final byte LOWERCASE_LETTER = 2;
  
  public static final byte TITLECASE_LETTER = 3;
  
  public static final byte MODIFIER_LETTER = 4;
  
  public static final byte OTHER_LETTER = 5;
  
  public static final byte NON_SPACING_MARK = 6;
  
  public static final byte ENCLOSING_MARK = 7;
  
  public static final byte COMBINING_SPACING_MARK = 8;
  
  public static final byte DECIMAL_DIGIT_NUMBER = 9;
  
  public static final byte LETTER_NUMBER = 10;
  
  public static final byte OTHER_NUMBER = 11;
  
  public static final byte SPACE_SEPARATOR = 12;
  
  public static final byte LINE_SEPARATOR = 13;
  
  public static final byte PARAGRAPH_SEPARATOR = 14;
  
  public static final byte CONTROL = 15;
  
  public static final byte FORMAT = 16;
  
  public static final byte PRIVATE_USE = 18;
  
  public static final byte SURROGATE = 19;
  
  public static final byte DASH_PUNCTUATION = 20;
  
  public static final byte START_PUNCTUATION = 21;
  
  public static final byte END_PUNCTUATION = 22;
  
  public static final byte CONNECTOR_PUNCTUATION = 23;
  
  public static final byte OTHER_PUNCTUATION = 24;
  
  public static final byte MATH_SYMBOL = 25;
  
  public static final byte CURRENCY_SYMBOL = 26;
  
  public static final byte MODIFIER_SYMBOL = 27;
  
  public static final byte OTHER_SYMBOL = 28;
  
  public static final byte INITIAL_QUOTE_PUNCTUATION = 29;
  
  public static final byte FINAL_QUOTE_PUNCTUATION = 30;
  
  static final int ERROR = -1;
  
  public static final byte DIRECTIONALITY_UNDEFINED = -1;
  
  public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;
  
  public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;
  
  public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;
  
  public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;
  
  public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;
  
  public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;
  
  public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;
  
  public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;
  
  public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;
  
  public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;
  
  public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;
  
  public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;
  
  public static final byte DIRECTIONALITY_WHITESPACE = 12;
  
  public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;
  
  public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;
  
  public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;
  
  public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;
  
  public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;
  
  public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;
  
  public static final char MIN_HIGH_SURROGATE = '?';
  
  public static final char MAX_HIGH_SURROGATE = '?';
  
  public static final char MIN_LOW_SURROGATE = '?';
  
  public static final char MAX_LOW_SURROGATE = '?';
  
  public static final char MIN_SURROGATE = '?';
  
  public static final char MAX_SURROGATE = '?';
  
  public static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;
  
  public static final int MIN_CODE_POINT = 0;
  
  public static final int MAX_CODE_POINT = 1114111;
  
  private final char value;
  
  private static final long serialVersionUID = 3786198910865385080L;
  
  public static final int SIZE = 16;
  
  public static final int BYTES = 2;
  
  public Character(char paramChar) { this.value = paramChar; }
  
  public static Character valueOf(char paramChar) { return (paramChar <= '') ? CharacterCache.cache[paramChar] : new Character(paramChar); }
  
  public char charValue() { return this.value; }
  
  public int hashCode() { return hashCode(this.value); }
  
  public static int hashCode(char paramChar) { return paramChar; }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof Character) ? ((this.value == ((Character)paramObject).charValue())) : false; }
  
  public String toString() {
    char[] arrayOfChar = { this.value };
    return String.valueOf(arrayOfChar);
  }
  
  public static String toString(char paramChar) { return String.valueOf(paramChar); }
  
  public static boolean isValidCodePoint(int paramInt) {
    int i = paramInt >>> 16;
    return (i < 17);
  }
  
  public static boolean isBmpCodePoint(int paramInt) { return (paramInt >>> 16 == 0); }
  
  public static boolean isSupplementaryCodePoint(int paramInt) { return (paramInt >= 65536 && paramInt < 1114112); }
  
  public static boolean isHighSurrogate(char paramChar) { return (paramChar >= '?' && paramChar < '?'); }
  
  public static boolean isLowSurrogate(char paramChar) { return (paramChar >= '?' && paramChar < ''); }
  
  public static boolean isSurrogate(char paramChar) { return (paramChar >= '?' && paramChar < ''); }
  
  public static boolean isSurrogatePair(char paramChar1, char paramChar2) { return (isHighSurrogate(paramChar1) && isLowSurrogate(paramChar2)); }
  
  public static int charCount(int paramInt) { return (paramInt >= 65536) ? 2 : 1; }
  
  public static int toCodePoint(char paramChar1, char paramChar2) { return (paramChar1 << '\n') + paramChar2 + -56613888; }
  
  public static int codePointAt(CharSequence paramCharSequence, int paramInt) {
    char c = paramCharSequence.charAt(paramInt);
    if (isHighSurrogate(c) && ++paramInt < paramCharSequence.length()) {
      char c1 = paramCharSequence.charAt(paramInt);
      if (isLowSurrogate(c1))
        return toCodePoint(c, c1); 
    } 
    return c;
  }
  
  public static int codePointAt(char[] paramArrayOfChar, int paramInt) { return codePointAtImpl(paramArrayOfChar, paramInt, paramArrayOfChar.length); }
  
  public static int codePointAt(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt1 >= paramInt2 || paramInt2 < 0 || paramInt2 > paramArrayOfChar.length)
      throw new IndexOutOfBoundsException(); 
    return codePointAtImpl(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  static int codePointAtImpl(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    char c = paramArrayOfChar[paramInt1];
    if (isHighSurrogate(c) && ++paramInt1 < paramInt2) {
      char c1 = paramArrayOfChar[paramInt1];
      if (isLowSurrogate(c1))
        return toCodePoint(c, c1); 
    } 
    return c;
  }
  
  public static int codePointBefore(CharSequence paramCharSequence, int paramInt) {
    char c = paramCharSequence.charAt(--paramInt);
    if (isLowSurrogate(c) && paramInt > 0) {
      char c1 = paramCharSequence.charAt(--paramInt);
      if (isHighSurrogate(c1))
        return toCodePoint(c1, c); 
    } 
    return c;
  }
  
  public static int codePointBefore(char[] paramArrayOfChar, int paramInt) { return codePointBeforeImpl(paramArrayOfChar, paramInt, 0); }
  
  public static int codePointBefore(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt1 <= paramInt2 || paramInt2 < 0 || paramInt2 >= paramArrayOfChar.length)
      throw new IndexOutOfBoundsException(); 
    return codePointBeforeImpl(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  static int codePointBeforeImpl(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    char c = paramArrayOfChar[--paramInt1];
    if (isLowSurrogate(c) && paramInt1 > paramInt2) {
      char c1 = paramArrayOfChar[--paramInt1];
      if (isHighSurrogate(c1))
        return toCodePoint(c1, c); 
    } 
    return c;
  }
  
  public static char highSurrogate(int paramInt) { return (char)((paramInt >>> 10) + 55232); }
  
  public static char lowSurrogate(int paramInt) { return (char)((paramInt & 0x3FF) + 56320); }
  
  public static int toChars(int paramInt1, char[] paramArrayOfChar, int paramInt2) {
    if (isBmpCodePoint(paramInt1)) {
      paramArrayOfChar[paramInt2] = (char)paramInt1;
      return 1;
    } 
    if (isValidCodePoint(paramInt1)) {
      toSurrogates(paramInt1, paramArrayOfChar, paramInt2);
      return 2;
    } 
    throw new IllegalArgumentException();
  }
  
  public static char[] toChars(int paramInt) {
    if (isBmpCodePoint(paramInt))
      return new char[] { (char)paramInt }; 
    if (isValidCodePoint(paramInt)) {
      char[] arrayOfChar = new char[2];
      toSurrogates(paramInt, arrayOfChar, 0);
      return arrayOfChar;
    } 
    throw new IllegalArgumentException();
  }
  
  static void toSurrogates(int paramInt1, char[] paramArrayOfChar, int paramInt2) {
    paramArrayOfChar[paramInt2 + 1] = lowSurrogate(paramInt1);
    paramArrayOfChar[paramInt2] = highSurrogate(paramInt1);
  }
  
  public static int codePointCount(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    int i = paramCharSequence.length();
    if (paramInt1 < 0 || paramInt2 > i || paramInt1 > paramInt2)
      throw new IndexOutOfBoundsException(); 
    int j = paramInt2 - paramInt1;
    int k = paramInt1;
    while (k < paramInt2) {
      if (isHighSurrogate(paramCharSequence.charAt(k++)) && k < paramInt2 && isLowSurrogate(paramCharSequence.charAt(k))) {
        j--;
        k++;
      } 
    } 
    return j;
  }
  
  public static int codePointCount(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (paramInt2 > paramArrayOfChar.length - paramInt1 || paramInt1 < 0 || paramInt2 < 0)
      throw new IndexOutOfBoundsException(); 
    return codePointCountImpl(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  static int codePointCountImpl(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = paramInt1 + paramInt2;
    int j = paramInt2;
    int k = paramInt1;
    while (k < i) {
      if (isHighSurrogate(paramArrayOfChar[k++]) && k < i && isLowSurrogate(paramArrayOfChar[k])) {
        j--;
        k++;
      } 
    } 
    return j;
  }
  
  public static int offsetByCodePoints(CharSequence paramCharSequence, int paramInt1, int paramInt2) {
    int i = paramCharSequence.length();
    if (paramInt1 < 0 || paramInt1 > i)
      throw new IndexOutOfBoundsException(); 
    int j = paramInt1;
    if (paramInt2 >= 0) {
      byte b;
      for (b = 0; j < i && b < paramInt2; b++) {
        if (isHighSurrogate(paramCharSequence.charAt(j++)) && j < i && isLowSurrogate(paramCharSequence.charAt(j)))
          j++; 
      } 
      if (b < paramInt2)
        throw new IndexOutOfBoundsException(); 
    } else {
      int k;
      for (k = paramInt2; j > 0 && k < 0; k++) {
        if (isLowSurrogate(paramCharSequence.charAt(--j)) && j > 0 && isHighSurrogate(paramCharSequence.charAt(j - 1)))
          j--; 
      } 
      if (k < 0)
        throw new IndexOutOfBoundsException(); 
    } 
    return j;
  }
  
  public static int offsetByCodePoints(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (paramInt2 > paramArrayOfChar.length - paramInt1 || paramInt1 < 0 || paramInt2 < 0 || paramInt3 < paramInt1 || paramInt3 > paramInt1 + paramInt2)
      throw new IndexOutOfBoundsException(); 
    return offsetByCodePointsImpl(paramArrayOfChar, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  static int offsetByCodePointsImpl(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3;
    if (paramInt4 >= 0) {
      int j = paramInt1 + paramInt2;
      byte b;
      for (b = 0; i < j && b < paramInt4; b++) {
        if (isHighSurrogate(paramArrayOfChar[i++]) && i < j && isLowSurrogate(paramArrayOfChar[i]))
          i++; 
      } 
      if (b < paramInt4)
        throw new IndexOutOfBoundsException(); 
    } else {
      int j;
      for (j = paramInt4; i > paramInt1 && j < 0; j++) {
        if (isLowSurrogate(paramArrayOfChar[--i]) && i > paramInt1 && isHighSurrogate(paramArrayOfChar[i - 1]))
          i--; 
      } 
      if (j < 0)
        throw new IndexOutOfBoundsException(); 
    } 
    return i;
  }
  
  public static boolean isLowerCase(char paramChar) { return isLowerCase(paramChar); }
  
  public static boolean isLowerCase(int paramInt) { return (getType(paramInt) == 2 || CharacterData.of(paramInt).isOtherLowercase(paramInt)); }
  
  public static boolean isUpperCase(char paramChar) { return isUpperCase(paramChar); }
  
  public static boolean isUpperCase(int paramInt) { return (getType(paramInt) == 1 || CharacterData.of(paramInt).isOtherUppercase(paramInt)); }
  
  public static boolean isTitleCase(char paramChar) { return isTitleCase(paramChar); }
  
  public static boolean isTitleCase(int paramInt) { return (getType(paramInt) == 3); }
  
  public static boolean isDigit(char paramChar) { return isDigit(paramChar); }
  
  public static boolean isDigit(int paramInt) { return (getType(paramInt) == 9); }
  
  public static boolean isDefined(char paramChar) { return isDefined(paramChar); }
  
  public static boolean isDefined(int paramInt) { return (getType(paramInt) != 0); }
  
  public static boolean isLetter(char paramChar) { return isLetter(paramChar); }
  
  public static boolean isLetter(int paramInt) { return ((62 >> getType(paramInt) & true) != 0); }
  
  public static boolean isLetterOrDigit(char paramChar) { return isLetterOrDigit(paramChar); }
  
  public static boolean isLetterOrDigit(int paramInt) { return ((574 >> getType(paramInt) & true) != 0); }
  
  @Deprecated
  public static boolean isJavaLetter(char paramChar) { return isJavaIdentifierStart(paramChar); }
  
  @Deprecated
  public static boolean isJavaLetterOrDigit(char paramChar) { return isJavaIdentifierPart(paramChar); }
  
  public static boolean isAlphabetic(int paramInt) { return ((1086 >> getType(paramInt) & true) != 0 || CharacterData.of(paramInt).isOtherAlphabetic(paramInt)); }
  
  public static boolean isIdeographic(int paramInt) { return CharacterData.of(paramInt).isIdeographic(paramInt); }
  
  public static boolean isJavaIdentifierStart(char paramChar) { return isJavaIdentifierStart(paramChar); }
  
  public static boolean isJavaIdentifierStart(int paramInt) { return CharacterData.of(paramInt).isJavaIdentifierStart(paramInt); }
  
  public static boolean isJavaIdentifierPart(char paramChar) { return isJavaIdentifierPart(paramChar); }
  
  public static boolean isJavaIdentifierPart(int paramInt) { return CharacterData.of(paramInt).isJavaIdentifierPart(paramInt); }
  
  public static boolean isUnicodeIdentifierStart(char paramChar) { return isUnicodeIdentifierStart(paramChar); }
  
  public static boolean isUnicodeIdentifierStart(int paramInt) { return CharacterData.of(paramInt).isUnicodeIdentifierStart(paramInt); }
  
  public static boolean isUnicodeIdentifierPart(char paramChar) { return isUnicodeIdentifierPart(paramChar); }
  
  public static boolean isUnicodeIdentifierPart(int paramInt) { return CharacterData.of(paramInt).isUnicodeIdentifierPart(paramInt); }
  
  public static boolean isIdentifierIgnorable(char paramChar) { return isIdentifierIgnorable(paramChar); }
  
  public static boolean isIdentifierIgnorable(int paramInt) { return CharacterData.of(paramInt).isIdentifierIgnorable(paramInt); }
  
  public static char toLowerCase(char paramChar) { return (char)toLowerCase(paramChar); }
  
  public static int toLowerCase(int paramInt) { return CharacterData.of(paramInt).toLowerCase(paramInt); }
  
  public static char toUpperCase(char paramChar) { return (char)toUpperCase(paramChar); }
  
  public static int toUpperCase(int paramInt) { return CharacterData.of(paramInt).toUpperCase(paramInt); }
  
  public static char toTitleCase(char paramChar) { return (char)toTitleCase(paramChar); }
  
  public static int toTitleCase(int paramInt) { return CharacterData.of(paramInt).toTitleCase(paramInt); }
  
  public static int digit(char paramChar, int paramInt) { return digit(paramChar, paramInt); }
  
  public static int digit(int paramInt1, int paramInt2) { return CharacterData.of(paramInt1).digit(paramInt1, paramInt2); }
  
  public static int getNumericValue(char paramChar) { return getNumericValue(paramChar); }
  
  public static int getNumericValue(int paramInt) { return CharacterData.of(paramInt).getNumericValue(paramInt); }
  
  @Deprecated
  public static boolean isSpace(char paramChar) { return (paramChar <= ' ' && (4294981120L >> paramChar & 0x1L) != 0L); }
  
  public static boolean isSpaceChar(char paramChar) { return isSpaceChar(paramChar); }
  
  public static boolean isSpaceChar(int paramInt) { return ((28672 >> getType(paramInt) & true) != 0); }
  
  public static boolean isWhitespace(char paramChar) { return isWhitespace(paramChar); }
  
  public static boolean isWhitespace(int paramInt) { return CharacterData.of(paramInt).isWhitespace(paramInt); }
  
  public static boolean isISOControl(char paramChar) { return isISOControl(paramChar); }
  
  public static boolean isISOControl(int paramInt) { return (paramInt <= 159 && (paramInt >= 127 || paramInt >>> 5 == 0)); }
  
  public static int getType(char paramChar) { return getType(paramChar); }
  
  public static int getType(int paramInt) { return CharacterData.of(paramInt).getType(paramInt); }
  
  public static char forDigit(int paramInt1, int paramInt2) { return (paramInt1 >= paramInt2 || paramInt1 < 0) ? Character.MIN_VALUE : ((paramInt2 < 2 || paramInt2 > 36) ? Character.MIN_VALUE : ((paramInt1 < 10) ? (char)(48 + paramInt1) : (char)(87 + paramInt1))); }
  
  public static byte getDirectionality(char paramChar) { return getDirectionality(paramChar); }
  
  public static byte getDirectionality(int paramInt) { return CharacterData.of(paramInt).getDirectionality(paramInt); }
  
  public static boolean isMirrored(char paramChar) { return isMirrored(paramChar); }
  
  public static boolean isMirrored(int paramInt) { return CharacterData.of(paramInt).isMirrored(paramInt); }
  
  public int compareTo(Character paramCharacter) { return compare(this.value, paramCharacter.value); }
  
  public static int compare(char paramChar1, char paramChar2) { return paramChar1 - paramChar2; }
  
  static int toUpperCaseEx(int paramInt) {
    assert isValidCodePoint(paramInt);
    return CharacterData.of(paramInt).toUpperCaseEx(paramInt);
  }
  
  static char[] toUpperCaseCharArray(int paramInt) {
    assert isBmpCodePoint(paramInt);
    return CharacterData.of(paramInt).toUpperCaseCharArray(paramInt);
  }
  
  public static char reverseBytes(char paramChar) { return (char)((paramChar & 0xFF00) >> '\b' | paramChar << '\b'); }
  
  public static String getName(int paramInt) {
    if (!isValidCodePoint(paramInt))
      throw new IllegalArgumentException(); 
    String str = CharacterName.get(paramInt);
    if (str != null)
      return str; 
    if (getType(paramInt) == 0)
      return null; 
    UnicodeBlock unicodeBlock = UnicodeBlock.of(paramInt);
    return (unicodeBlock != null) ? (unicodeBlock.toString().replace('_', ' ') + " " + Integer.toHexString(paramInt).toUpperCase(Locale.ENGLISH)) : Integer.toHexString(paramInt).toUpperCase(Locale.ENGLISH);
  }
  
  private static class CharacterCache {
    static final Character[] cache = new Character[128];
    
    static  {
      for (byte b = 0; b < cache.length; b++)
        cache[b] = new Character((char)b); 
    }
  }
  
  public static class Subset {
    private String name;
    
    protected Subset(String param1String) {
      if (param1String == null)
        throw new NullPointerException("name"); 
      this.name = param1String;
    }
    
    public final boolean equals(Object param1Object) { return (this == param1Object); }
    
    public final int hashCode() { return super.hashCode(); }
    
    public final String toString() { return this.name; }
  }
  
  public static final class UnicodeBlock extends Subset {
    private static Map<String, UnicodeBlock> map = new HashMap(256);
    
    public static final UnicodeBlock BASIC_LATIN = new UnicodeBlock("BASIC_LATIN", new String[] { "BASIC LATIN", "BASICLATIN" });
    
    public static final UnicodeBlock LATIN_1_SUPPLEMENT = new UnicodeBlock("LATIN_1_SUPPLEMENT", new String[] { "LATIN-1 SUPPLEMENT", "LATIN-1SUPPLEMENT" });
    
    public static final UnicodeBlock LATIN_EXTENDED_A = new UnicodeBlock("LATIN_EXTENDED_A", new String[] { "LATIN EXTENDED-A", "LATINEXTENDED-A" });
    
    public static final UnicodeBlock LATIN_EXTENDED_B = new UnicodeBlock("LATIN_EXTENDED_B", new String[] { "LATIN EXTENDED-B", "LATINEXTENDED-B" });
    
    public static final UnicodeBlock IPA_EXTENSIONS = new UnicodeBlock("IPA_EXTENSIONS", new String[] { "IPA EXTENSIONS", "IPAEXTENSIONS" });
    
    public static final UnicodeBlock SPACING_MODIFIER_LETTERS = new UnicodeBlock("SPACING_MODIFIER_LETTERS", new String[] { "SPACING MODIFIER LETTERS", "SPACINGMODIFIERLETTERS" });
    
    public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS", new String[] { "COMBINING DIACRITICAL MARKS", "COMBININGDIACRITICALMARKS" });
    
    public static final UnicodeBlock GREEK = new UnicodeBlock("GREEK", new String[] { "GREEK AND COPTIC", "GREEKANDCOPTIC" });
    
    public static final UnicodeBlock CYRILLIC = new UnicodeBlock("CYRILLIC");
    
    public static final UnicodeBlock ARMENIAN = new UnicodeBlock("ARMENIAN");
    
    public static final UnicodeBlock HEBREW = new UnicodeBlock("HEBREW");
    
    public static final UnicodeBlock ARABIC = new UnicodeBlock("ARABIC");
    
    public static final UnicodeBlock DEVANAGARI = new UnicodeBlock("DEVANAGARI");
    
    public static final UnicodeBlock BENGALI = new UnicodeBlock("BENGALI");
    
    public static final UnicodeBlock GURMUKHI = new UnicodeBlock("GURMUKHI");
    
    public static final UnicodeBlock GUJARATI = new UnicodeBlock("GUJARATI");
    
    public static final UnicodeBlock ORIYA = new UnicodeBlock("ORIYA");
    
    public static final UnicodeBlock TAMIL = new UnicodeBlock("TAMIL");
    
    public static final UnicodeBlock TELUGU = new UnicodeBlock("TELUGU");
    
    public static final UnicodeBlock KANNADA = new UnicodeBlock("KANNADA");
    
    public static final UnicodeBlock MALAYALAM = new UnicodeBlock("MALAYALAM");
    
    public static final UnicodeBlock THAI = new UnicodeBlock("THAI");
    
    public static final UnicodeBlock LAO = new UnicodeBlock("LAO");
    
    public static final UnicodeBlock TIBETAN = new UnicodeBlock("TIBETAN");
    
    public static final UnicodeBlock GEORGIAN = new UnicodeBlock("GEORGIAN");
    
    public static final UnicodeBlock HANGUL_JAMO = new UnicodeBlock("HANGUL_JAMO", new String[] { "HANGUL JAMO", "HANGULJAMO" });
    
    public static final UnicodeBlock LATIN_EXTENDED_ADDITIONAL = new UnicodeBlock("LATIN_EXTENDED_ADDITIONAL", new String[] { "LATIN EXTENDED ADDITIONAL", "LATINEXTENDEDADDITIONAL" });
    
    public static final UnicodeBlock GREEK_EXTENDED = new UnicodeBlock("GREEK_EXTENDED", new String[] { "GREEK EXTENDED", "GREEKEXTENDED" });
    
    public static final UnicodeBlock GENERAL_PUNCTUATION = new UnicodeBlock("GENERAL_PUNCTUATION", new String[] { "GENERAL PUNCTUATION", "GENERALPUNCTUATION" });
    
    public static final UnicodeBlock SUPERSCRIPTS_AND_SUBSCRIPTS = new UnicodeBlock("SUPERSCRIPTS_AND_SUBSCRIPTS", new String[] { "SUPERSCRIPTS AND SUBSCRIPTS", "SUPERSCRIPTSANDSUBSCRIPTS" });
    
    public static final UnicodeBlock CURRENCY_SYMBOLS = new UnicodeBlock("CURRENCY_SYMBOLS", new String[] { "CURRENCY SYMBOLS", "CURRENCYSYMBOLS" });
    
    public static final UnicodeBlock COMBINING_MARKS_FOR_SYMBOLS = new UnicodeBlock("COMBINING_MARKS_FOR_SYMBOLS", new String[] { "COMBINING DIACRITICAL MARKS FOR SYMBOLS", "COMBININGDIACRITICALMARKSFORSYMBOLS", "COMBINING MARKS FOR SYMBOLS", "COMBININGMARKSFORSYMBOLS" });
    
    public static final UnicodeBlock LETTERLIKE_SYMBOLS = new UnicodeBlock("LETTERLIKE_SYMBOLS", new String[] { "LETTERLIKE SYMBOLS", "LETTERLIKESYMBOLS" });
    
    public static final UnicodeBlock NUMBER_FORMS = new UnicodeBlock("NUMBER_FORMS", new String[] { "NUMBER FORMS", "NUMBERFORMS" });
    
    public static final UnicodeBlock ARROWS = new UnicodeBlock("ARROWS");
    
    public static final UnicodeBlock MATHEMATICAL_OPERATORS = new UnicodeBlock("MATHEMATICAL_OPERATORS", new String[] { "MATHEMATICAL OPERATORS", "MATHEMATICALOPERATORS" });
    
    public static final UnicodeBlock MISCELLANEOUS_TECHNICAL = new UnicodeBlock("MISCELLANEOUS_TECHNICAL", new String[] { "MISCELLANEOUS TECHNICAL", "MISCELLANEOUSTECHNICAL" });
    
    public static final UnicodeBlock CONTROL_PICTURES = new UnicodeBlock("CONTROL_PICTURES", new String[] { "CONTROL PICTURES", "CONTROLPICTURES" });
    
    public static final UnicodeBlock OPTICAL_CHARACTER_RECOGNITION = new UnicodeBlock("OPTICAL_CHARACTER_RECOGNITION", new String[] { "OPTICAL CHARACTER RECOGNITION", "OPTICALCHARACTERRECOGNITION" });
    
    public static final UnicodeBlock ENCLOSED_ALPHANUMERICS = new UnicodeBlock("ENCLOSED_ALPHANUMERICS", new String[] { "ENCLOSED ALPHANUMERICS", "ENCLOSEDALPHANUMERICS" });
    
    public static final UnicodeBlock BOX_DRAWING = new UnicodeBlock("BOX_DRAWING", new String[] { "BOX DRAWING", "BOXDRAWING" });
    
    public static final UnicodeBlock BLOCK_ELEMENTS = new UnicodeBlock("BLOCK_ELEMENTS", new String[] { "BLOCK ELEMENTS", "BLOCKELEMENTS" });
    
    public static final UnicodeBlock GEOMETRIC_SHAPES = new UnicodeBlock("GEOMETRIC_SHAPES", new String[] { "GEOMETRIC SHAPES", "GEOMETRICSHAPES" });
    
    public static final UnicodeBlock MISCELLANEOUS_SYMBOLS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS", new String[] { "MISCELLANEOUS SYMBOLS", "MISCELLANEOUSSYMBOLS" });
    
    public static final UnicodeBlock DINGBATS = new UnicodeBlock("DINGBATS");
    
    public static final UnicodeBlock CJK_SYMBOLS_AND_PUNCTUATION = new UnicodeBlock("CJK_SYMBOLS_AND_PUNCTUATION", new String[] { "CJK SYMBOLS AND PUNCTUATION", "CJKSYMBOLSANDPUNCTUATION" });
    
    public static final UnicodeBlock HIRAGANA = new UnicodeBlock("HIRAGANA");
    
    public static final UnicodeBlock KATAKANA = new UnicodeBlock("KATAKANA");
    
    public static final UnicodeBlock BOPOMOFO = new UnicodeBlock("BOPOMOFO");
    
    public static final UnicodeBlock HANGUL_COMPATIBILITY_JAMO = new UnicodeBlock("HANGUL_COMPATIBILITY_JAMO", new String[] { "HANGUL COMPATIBILITY JAMO", "HANGULCOMPATIBILITYJAMO" });
    
    public static final UnicodeBlock KANBUN = new UnicodeBlock("KANBUN");
    
    public static final UnicodeBlock ENCLOSED_CJK_LETTERS_AND_MONTHS = new UnicodeBlock("ENCLOSED_CJK_LETTERS_AND_MONTHS", new String[] { "ENCLOSED CJK LETTERS AND MONTHS", "ENCLOSEDCJKLETTERSANDMONTHS" });
    
    public static final UnicodeBlock CJK_COMPATIBILITY = new UnicodeBlock("CJK_COMPATIBILITY", new String[] { "CJK COMPATIBILITY", "CJKCOMPATIBILITY" });
    
    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS", new String[] { "CJK UNIFIED IDEOGRAPHS", "CJKUNIFIEDIDEOGRAPHS" });
    
    public static final UnicodeBlock HANGUL_SYLLABLES = new UnicodeBlock("HANGUL_SYLLABLES", new String[] { "HANGUL SYLLABLES", "HANGULSYLLABLES" });
    
    public static final UnicodeBlock PRIVATE_USE_AREA = new UnicodeBlock("PRIVATE_USE_AREA", new String[] { "PRIVATE USE AREA", "PRIVATEUSEAREA" });
    
    public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS", new String[] { "CJK COMPATIBILITY IDEOGRAPHS", "CJKCOMPATIBILITYIDEOGRAPHS" });
    
    public static final UnicodeBlock ALPHABETIC_PRESENTATION_FORMS = new UnicodeBlock("ALPHABETIC_PRESENTATION_FORMS", new String[] { "ALPHABETIC PRESENTATION FORMS", "ALPHABETICPRESENTATIONFORMS" });
    
    public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_A = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_A", new String[] { "ARABIC PRESENTATION FORMS-A", "ARABICPRESENTATIONFORMS-A" });
    
    public static final UnicodeBlock COMBINING_HALF_MARKS = new UnicodeBlock("COMBINING_HALF_MARKS", new String[] { "COMBINING HALF MARKS", "COMBININGHALFMARKS" });
    
    public static final UnicodeBlock CJK_COMPATIBILITY_FORMS = new UnicodeBlock("CJK_COMPATIBILITY_FORMS", new String[] { "CJK COMPATIBILITY FORMS", "CJKCOMPATIBILITYFORMS" });
    
    public static final UnicodeBlock SMALL_FORM_VARIANTS = new UnicodeBlock("SMALL_FORM_VARIANTS", new String[] { "SMALL FORM VARIANTS", "SMALLFORMVARIANTS" });
    
    public static final UnicodeBlock ARABIC_PRESENTATION_FORMS_B = new UnicodeBlock("ARABIC_PRESENTATION_FORMS_B", new String[] { "ARABIC PRESENTATION FORMS-B", "ARABICPRESENTATIONFORMS-B" });
    
    public static final UnicodeBlock HALFWIDTH_AND_FULLWIDTH_FORMS = new UnicodeBlock("HALFWIDTH_AND_FULLWIDTH_FORMS", new String[] { "HALFWIDTH AND FULLWIDTH FORMS", "HALFWIDTHANDFULLWIDTHFORMS" });
    
    public static final UnicodeBlock SPECIALS = new UnicodeBlock("SPECIALS");
    
    @Deprecated
    public static final UnicodeBlock SURROGATES_AREA = new UnicodeBlock("SURROGATES_AREA");
    
    public static final UnicodeBlock SYRIAC = new UnicodeBlock("SYRIAC");
    
    public static final UnicodeBlock THAANA = new UnicodeBlock("THAANA");
    
    public static final UnicodeBlock SINHALA = new UnicodeBlock("SINHALA");
    
    public static final UnicodeBlock MYANMAR = new UnicodeBlock("MYANMAR");
    
    public static final UnicodeBlock ETHIOPIC = new UnicodeBlock("ETHIOPIC");
    
    public static final UnicodeBlock CHEROKEE = new UnicodeBlock("CHEROKEE");
    
    public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS", new String[] { "UNIFIED CANADIAN ABORIGINAL SYLLABICS", "UNIFIEDCANADIANABORIGINALSYLLABICS" });
    
    public static final UnicodeBlock OGHAM = new UnicodeBlock("OGHAM");
    
    public static final UnicodeBlock RUNIC = new UnicodeBlock("RUNIC");
    
    public static final UnicodeBlock KHMER = new UnicodeBlock("KHMER");
    
    public static final UnicodeBlock MONGOLIAN = new UnicodeBlock("MONGOLIAN");
    
    public static final UnicodeBlock BRAILLE_PATTERNS = new UnicodeBlock("BRAILLE_PATTERNS", new String[] { "BRAILLE PATTERNS", "BRAILLEPATTERNS" });
    
    public static final UnicodeBlock CJK_RADICALS_SUPPLEMENT = new UnicodeBlock("CJK_RADICALS_SUPPLEMENT", new String[] { "CJK RADICALS SUPPLEMENT", "CJKRADICALSSUPPLEMENT" });
    
    public static final UnicodeBlock KANGXI_RADICALS = new UnicodeBlock("KANGXI_RADICALS", new String[] { "KANGXI RADICALS", "KANGXIRADICALS" });
    
    public static final UnicodeBlock IDEOGRAPHIC_DESCRIPTION_CHARACTERS = new UnicodeBlock("IDEOGRAPHIC_DESCRIPTION_CHARACTERS", new String[] { "IDEOGRAPHIC DESCRIPTION CHARACTERS", "IDEOGRAPHICDESCRIPTIONCHARACTERS" });
    
    public static final UnicodeBlock BOPOMOFO_EXTENDED = new UnicodeBlock("BOPOMOFO_EXTENDED", new String[] { "BOPOMOFO EXTENDED", "BOPOMOFOEXTENDED" });
    
    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A", new String[] { "CJK UNIFIED IDEOGRAPHS EXTENSION A", "CJKUNIFIEDIDEOGRAPHSEXTENSIONA" });
    
    public static final UnicodeBlock YI_SYLLABLES = new UnicodeBlock("YI_SYLLABLES", new String[] { "YI SYLLABLES", "YISYLLABLES" });
    
    public static final UnicodeBlock YI_RADICALS = new UnicodeBlock("YI_RADICALS", new String[] { "YI RADICALS", "YIRADICALS" });
    
    public static final UnicodeBlock CYRILLIC_SUPPLEMENTARY = new UnicodeBlock("CYRILLIC_SUPPLEMENTARY", new String[] { "CYRILLIC SUPPLEMENTARY", "CYRILLICSUPPLEMENTARY", "CYRILLIC SUPPLEMENT", "CYRILLICSUPPLEMENT" });
    
    public static final UnicodeBlock TAGALOG = new UnicodeBlock("TAGALOG");
    
    public static final UnicodeBlock HANUNOO = new UnicodeBlock("HANUNOO");
    
    public static final UnicodeBlock BUHID = new UnicodeBlock("BUHID");
    
    public static final UnicodeBlock TAGBANWA = new UnicodeBlock("TAGBANWA");
    
    public static final UnicodeBlock LIMBU = new UnicodeBlock("LIMBU");
    
    public static final UnicodeBlock TAI_LE = new UnicodeBlock("TAI_LE", new String[] { "TAI LE", "TAILE" });
    
    public static final UnicodeBlock KHMER_SYMBOLS = new UnicodeBlock("KHMER_SYMBOLS", new String[] { "KHMER SYMBOLS", "KHMERSYMBOLS" });
    
    public static final UnicodeBlock PHONETIC_EXTENSIONS = new UnicodeBlock("PHONETIC_EXTENSIONS", new String[] { "PHONETIC EXTENSIONS", "PHONETICEXTENSIONS" });
    
    public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A", new String[] { "MISCELLANEOUS MATHEMATICAL SYMBOLS-A", "MISCELLANEOUSMATHEMATICALSYMBOLS-A" });
    
    public static final UnicodeBlock SUPPLEMENTAL_ARROWS_A = new UnicodeBlock("SUPPLEMENTAL_ARROWS_A", new String[] { "SUPPLEMENTAL ARROWS-A", "SUPPLEMENTALARROWS-A" });
    
    public static final UnicodeBlock SUPPLEMENTAL_ARROWS_B = new UnicodeBlock("SUPPLEMENTAL_ARROWS_B", new String[] { "SUPPLEMENTAL ARROWS-B", "SUPPLEMENTALARROWS-B" });
    
    public static final UnicodeBlock MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B = new UnicodeBlock("MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B", new String[] { "MISCELLANEOUS MATHEMATICAL SYMBOLS-B", "MISCELLANEOUSMATHEMATICALSYMBOLS-B" });
    
    public static final UnicodeBlock SUPPLEMENTAL_MATHEMATICAL_OPERATORS = new UnicodeBlock("SUPPLEMENTAL_MATHEMATICAL_OPERATORS", new String[] { "SUPPLEMENTAL MATHEMATICAL OPERATORS", "SUPPLEMENTALMATHEMATICALOPERATORS" });
    
    public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_ARROWS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_ARROWS", new String[] { "MISCELLANEOUS SYMBOLS AND ARROWS", "MISCELLANEOUSSYMBOLSANDARROWS" });
    
    public static final UnicodeBlock KATAKANA_PHONETIC_EXTENSIONS = new UnicodeBlock("KATAKANA_PHONETIC_EXTENSIONS", new String[] { "KATAKANA PHONETIC EXTENSIONS", "KATAKANAPHONETICEXTENSIONS" });
    
    public static final UnicodeBlock YIJING_HEXAGRAM_SYMBOLS = new UnicodeBlock("YIJING_HEXAGRAM_SYMBOLS", new String[] { "YIJING HEXAGRAM SYMBOLS", "YIJINGHEXAGRAMSYMBOLS" });
    
    public static final UnicodeBlock VARIATION_SELECTORS = new UnicodeBlock("VARIATION_SELECTORS", new String[] { "VARIATION SELECTORS", "VARIATIONSELECTORS" });
    
    public static final UnicodeBlock LINEAR_B_SYLLABARY = new UnicodeBlock("LINEAR_B_SYLLABARY", new String[] { "LINEAR B SYLLABARY", "LINEARBSYLLABARY" });
    
    public static final UnicodeBlock LINEAR_B_IDEOGRAMS = new UnicodeBlock("LINEAR_B_IDEOGRAMS", new String[] { "LINEAR B IDEOGRAMS", "LINEARBIDEOGRAMS" });
    
    public static final UnicodeBlock AEGEAN_NUMBERS = new UnicodeBlock("AEGEAN_NUMBERS", new String[] { "AEGEAN NUMBERS", "AEGEANNUMBERS" });
    
    public static final UnicodeBlock OLD_ITALIC = new UnicodeBlock("OLD_ITALIC", new String[] { "OLD ITALIC", "OLDITALIC" });
    
    public static final UnicodeBlock GOTHIC = new UnicodeBlock("GOTHIC");
    
    public static final UnicodeBlock UGARITIC = new UnicodeBlock("UGARITIC");
    
    public static final UnicodeBlock DESERET = new UnicodeBlock("DESERET");
    
    public static final UnicodeBlock SHAVIAN = new UnicodeBlock("SHAVIAN");
    
    public static final UnicodeBlock OSMANYA = new UnicodeBlock("OSMANYA");
    
    public static final UnicodeBlock CYPRIOT_SYLLABARY = new UnicodeBlock("CYPRIOT_SYLLABARY", new String[] { "CYPRIOT SYLLABARY", "CYPRIOTSYLLABARY" });
    
    public static final UnicodeBlock BYZANTINE_MUSICAL_SYMBOLS = new UnicodeBlock("BYZANTINE_MUSICAL_SYMBOLS", new String[] { "BYZANTINE MUSICAL SYMBOLS", "BYZANTINEMUSICALSYMBOLS" });
    
    public static final UnicodeBlock MUSICAL_SYMBOLS = new UnicodeBlock("MUSICAL_SYMBOLS", new String[] { "MUSICAL SYMBOLS", "MUSICALSYMBOLS" });
    
    public static final UnicodeBlock TAI_XUAN_JING_SYMBOLS = new UnicodeBlock("TAI_XUAN_JING_SYMBOLS", new String[] { "TAI XUAN JING SYMBOLS", "TAIXUANJINGSYMBOLS" });
    
    public static final UnicodeBlock MATHEMATICAL_ALPHANUMERIC_SYMBOLS = new UnicodeBlock("MATHEMATICAL_ALPHANUMERIC_SYMBOLS", new String[] { "MATHEMATICAL ALPHANUMERIC SYMBOLS", "MATHEMATICALALPHANUMERICSYMBOLS" });
    
    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B", new String[] { "CJK UNIFIED IDEOGRAPHS EXTENSION B", "CJKUNIFIEDIDEOGRAPHSEXTENSIONB" });
    
    public static final UnicodeBlock CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT = new UnicodeBlock("CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT", new String[] { "CJK COMPATIBILITY IDEOGRAPHS SUPPLEMENT", "CJKCOMPATIBILITYIDEOGRAPHSSUPPLEMENT" });
    
    public static final UnicodeBlock TAGS = new UnicodeBlock("TAGS");
    
    public static final UnicodeBlock VARIATION_SELECTORS_SUPPLEMENT = new UnicodeBlock("VARIATION_SELECTORS_SUPPLEMENT", new String[] { "VARIATION SELECTORS SUPPLEMENT", "VARIATIONSELECTORSSUPPLEMENT" });
    
    public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_A = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_A", new String[] { "SUPPLEMENTARY PRIVATE USE AREA-A", "SUPPLEMENTARYPRIVATEUSEAREA-A" });
    
    public static final UnicodeBlock SUPPLEMENTARY_PRIVATE_USE_AREA_B = new UnicodeBlock("SUPPLEMENTARY_PRIVATE_USE_AREA_B", new String[] { "SUPPLEMENTARY PRIVATE USE AREA-B", "SUPPLEMENTARYPRIVATEUSEAREA-B" });
    
    public static final UnicodeBlock HIGH_SURROGATES = new UnicodeBlock("HIGH_SURROGATES", new String[] { "HIGH SURROGATES", "HIGHSURROGATES" });
    
    public static final UnicodeBlock HIGH_PRIVATE_USE_SURROGATES = new UnicodeBlock("HIGH_PRIVATE_USE_SURROGATES", new String[] { "HIGH PRIVATE USE SURROGATES", "HIGHPRIVATEUSESURROGATES" });
    
    public static final UnicodeBlock LOW_SURROGATES = new UnicodeBlock("LOW_SURROGATES", new String[] { "LOW SURROGATES", "LOWSURROGATES" });
    
    public static final UnicodeBlock ARABIC_SUPPLEMENT = new UnicodeBlock("ARABIC_SUPPLEMENT", new String[] { "ARABIC SUPPLEMENT", "ARABICSUPPLEMENT" });
    
    public static final UnicodeBlock NKO = new UnicodeBlock("NKO");
    
    public static final UnicodeBlock SAMARITAN = new UnicodeBlock("SAMARITAN");
    
    public static final UnicodeBlock MANDAIC = new UnicodeBlock("MANDAIC");
    
    public static final UnicodeBlock ETHIOPIC_SUPPLEMENT = new UnicodeBlock("ETHIOPIC_SUPPLEMENT", new String[] { "ETHIOPIC SUPPLEMENT", "ETHIOPICSUPPLEMENT" });
    
    public static final UnicodeBlock UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED = new UnicodeBlock("UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED", new String[] { "UNIFIED CANADIAN ABORIGINAL SYLLABICS EXTENDED", "UNIFIEDCANADIANABORIGINALSYLLABICSEXTENDED" });
    
    public static final UnicodeBlock NEW_TAI_LUE = new UnicodeBlock("NEW_TAI_LUE", new String[] { "NEW TAI LUE", "NEWTAILUE" });
    
    public static final UnicodeBlock BUGINESE = new UnicodeBlock("BUGINESE");
    
    public static final UnicodeBlock TAI_THAM = new UnicodeBlock("TAI_THAM", new String[] { "TAI THAM", "TAITHAM" });
    
    public static final UnicodeBlock BALINESE = new UnicodeBlock("BALINESE");
    
    public static final UnicodeBlock SUNDANESE = new UnicodeBlock("SUNDANESE");
    
    public static final UnicodeBlock BATAK = new UnicodeBlock("BATAK");
    
    public static final UnicodeBlock LEPCHA = new UnicodeBlock("LEPCHA");
    
    public static final UnicodeBlock OL_CHIKI = new UnicodeBlock("OL_CHIKI", new String[] { "OL CHIKI", "OLCHIKI" });
    
    public static final UnicodeBlock VEDIC_EXTENSIONS = new UnicodeBlock("VEDIC_EXTENSIONS", new String[] { "VEDIC EXTENSIONS", "VEDICEXTENSIONS" });
    
    public static final UnicodeBlock PHONETIC_EXTENSIONS_SUPPLEMENT = new UnicodeBlock("PHONETIC_EXTENSIONS_SUPPLEMENT", new String[] { "PHONETIC EXTENSIONS SUPPLEMENT", "PHONETICEXTENSIONSSUPPLEMENT" });
    
    public static final UnicodeBlock COMBINING_DIACRITICAL_MARKS_SUPPLEMENT = new UnicodeBlock("COMBINING_DIACRITICAL_MARKS_SUPPLEMENT", new String[] { "COMBINING DIACRITICAL MARKS SUPPLEMENT", "COMBININGDIACRITICALMARKSSUPPLEMENT" });
    
    public static final UnicodeBlock GLAGOLITIC = new UnicodeBlock("GLAGOLITIC");
    
    public static final UnicodeBlock LATIN_EXTENDED_C = new UnicodeBlock("LATIN_EXTENDED_C", new String[] { "LATIN EXTENDED-C", "LATINEXTENDED-C" });
    
    public static final UnicodeBlock COPTIC = new UnicodeBlock("COPTIC");
    
    public static final UnicodeBlock GEORGIAN_SUPPLEMENT = new UnicodeBlock("GEORGIAN_SUPPLEMENT", new String[] { "GEORGIAN SUPPLEMENT", "GEORGIANSUPPLEMENT" });
    
    public static final UnicodeBlock TIFINAGH = new UnicodeBlock("TIFINAGH");
    
    public static final UnicodeBlock ETHIOPIC_EXTENDED = new UnicodeBlock("ETHIOPIC_EXTENDED", new String[] { "ETHIOPIC EXTENDED", "ETHIOPICEXTENDED" });
    
    public static final UnicodeBlock CYRILLIC_EXTENDED_A = new UnicodeBlock("CYRILLIC_EXTENDED_A", new String[] { "CYRILLIC EXTENDED-A", "CYRILLICEXTENDED-A" });
    
    public static final UnicodeBlock SUPPLEMENTAL_PUNCTUATION = new UnicodeBlock("SUPPLEMENTAL_PUNCTUATION", new String[] { "SUPPLEMENTAL PUNCTUATION", "SUPPLEMENTALPUNCTUATION" });
    
    public static final UnicodeBlock CJK_STROKES = new UnicodeBlock("CJK_STROKES", new String[] { "CJK STROKES", "CJKSTROKES" });
    
    public static final UnicodeBlock LISU = new UnicodeBlock("LISU");
    
    public static final UnicodeBlock VAI = new UnicodeBlock("VAI");
    
    public static final UnicodeBlock CYRILLIC_EXTENDED_B = new UnicodeBlock("CYRILLIC_EXTENDED_B", new String[] { "CYRILLIC EXTENDED-B", "CYRILLICEXTENDED-B" });
    
    public static final UnicodeBlock BAMUM = new UnicodeBlock("BAMUM");
    
    public static final UnicodeBlock MODIFIER_TONE_LETTERS = new UnicodeBlock("MODIFIER_TONE_LETTERS", new String[] { "MODIFIER TONE LETTERS", "MODIFIERTONELETTERS" });
    
    public static final UnicodeBlock LATIN_EXTENDED_D = new UnicodeBlock("LATIN_EXTENDED_D", new String[] { "LATIN EXTENDED-D", "LATINEXTENDED-D" });
    
    public static final UnicodeBlock SYLOTI_NAGRI = new UnicodeBlock("SYLOTI_NAGRI", new String[] { "SYLOTI NAGRI", "SYLOTINAGRI" });
    
    public static final UnicodeBlock COMMON_INDIC_NUMBER_FORMS = new UnicodeBlock("COMMON_INDIC_NUMBER_FORMS", new String[] { "COMMON INDIC NUMBER FORMS", "COMMONINDICNUMBERFORMS" });
    
    public static final UnicodeBlock PHAGS_PA = new UnicodeBlock("PHAGS_PA", "PHAGS-PA");
    
    public static final UnicodeBlock SAURASHTRA = new UnicodeBlock("SAURASHTRA");
    
    public static final UnicodeBlock DEVANAGARI_EXTENDED = new UnicodeBlock("DEVANAGARI_EXTENDED", new String[] { "DEVANAGARI EXTENDED", "DEVANAGARIEXTENDED" });
    
    public static final UnicodeBlock KAYAH_LI = new UnicodeBlock("KAYAH_LI", new String[] { "KAYAH LI", "KAYAHLI" });
    
    public static final UnicodeBlock REJANG = new UnicodeBlock("REJANG");
    
    public static final UnicodeBlock HANGUL_JAMO_EXTENDED_A = new UnicodeBlock("HANGUL_JAMO_EXTENDED_A", new String[] { "HANGUL JAMO EXTENDED-A", "HANGULJAMOEXTENDED-A" });
    
    public static final UnicodeBlock JAVANESE = new UnicodeBlock("JAVANESE");
    
    public static final UnicodeBlock CHAM = new UnicodeBlock("CHAM");
    
    public static final UnicodeBlock MYANMAR_EXTENDED_A = new UnicodeBlock("MYANMAR_EXTENDED_A", new String[] { "MYANMAR EXTENDED-A", "MYANMAREXTENDED-A" });
    
    public static final UnicodeBlock TAI_VIET = new UnicodeBlock("TAI_VIET", new String[] { "TAI VIET", "TAIVIET" });
    
    public static final UnicodeBlock ETHIOPIC_EXTENDED_A = new UnicodeBlock("ETHIOPIC_EXTENDED_A", new String[] { "ETHIOPIC EXTENDED-A", "ETHIOPICEXTENDED-A" });
    
    public static final UnicodeBlock MEETEI_MAYEK = new UnicodeBlock("MEETEI_MAYEK", new String[] { "MEETEI MAYEK", "MEETEIMAYEK" });
    
    public static final UnicodeBlock HANGUL_JAMO_EXTENDED_B = new UnicodeBlock("HANGUL_JAMO_EXTENDED_B", new String[] { "HANGUL JAMO EXTENDED-B", "HANGULJAMOEXTENDED-B" });
    
    public static final UnicodeBlock VERTICAL_FORMS = new UnicodeBlock("VERTICAL_FORMS", new String[] { "VERTICAL FORMS", "VERTICALFORMS" });
    
    public static final UnicodeBlock ANCIENT_GREEK_NUMBERS = new UnicodeBlock("ANCIENT_GREEK_NUMBERS", new String[] { "ANCIENT GREEK NUMBERS", "ANCIENTGREEKNUMBERS" });
    
    public static final UnicodeBlock ANCIENT_SYMBOLS = new UnicodeBlock("ANCIENT_SYMBOLS", new String[] { "ANCIENT SYMBOLS", "ANCIENTSYMBOLS" });
    
    public static final UnicodeBlock PHAISTOS_DISC = new UnicodeBlock("PHAISTOS_DISC", new String[] { "PHAISTOS DISC", "PHAISTOSDISC" });
    
    public static final UnicodeBlock LYCIAN = new UnicodeBlock("LYCIAN");
    
    public static final UnicodeBlock CARIAN = new UnicodeBlock("CARIAN");
    
    public static final UnicodeBlock OLD_PERSIAN = new UnicodeBlock("OLD_PERSIAN", new String[] { "OLD PERSIAN", "OLDPERSIAN" });
    
    public static final UnicodeBlock IMPERIAL_ARAMAIC = new UnicodeBlock("IMPERIAL_ARAMAIC", new String[] { "IMPERIAL ARAMAIC", "IMPERIALARAMAIC" });
    
    public static final UnicodeBlock PHOENICIAN = new UnicodeBlock("PHOENICIAN");
    
    public static final UnicodeBlock LYDIAN = new UnicodeBlock("LYDIAN");
    
    public static final UnicodeBlock KHAROSHTHI = new UnicodeBlock("KHAROSHTHI");
    
    public static final UnicodeBlock OLD_SOUTH_ARABIAN = new UnicodeBlock("OLD_SOUTH_ARABIAN", new String[] { "OLD SOUTH ARABIAN", "OLDSOUTHARABIAN" });
    
    public static final UnicodeBlock AVESTAN = new UnicodeBlock("AVESTAN");
    
    public static final UnicodeBlock INSCRIPTIONAL_PARTHIAN = new UnicodeBlock("INSCRIPTIONAL_PARTHIAN", new String[] { "INSCRIPTIONAL PARTHIAN", "INSCRIPTIONALPARTHIAN" });
    
    public static final UnicodeBlock INSCRIPTIONAL_PAHLAVI = new UnicodeBlock("INSCRIPTIONAL_PAHLAVI", new String[] { "INSCRIPTIONAL PAHLAVI", "INSCRIPTIONALPAHLAVI" });
    
    public static final UnicodeBlock OLD_TURKIC = new UnicodeBlock("OLD_TURKIC", new String[] { "OLD TURKIC", "OLDTURKIC" });
    
    public static final UnicodeBlock RUMI_NUMERAL_SYMBOLS = new UnicodeBlock("RUMI_NUMERAL_SYMBOLS", new String[] { "RUMI NUMERAL SYMBOLS", "RUMINUMERALSYMBOLS" });
    
    public static final UnicodeBlock BRAHMI = new UnicodeBlock("BRAHMI");
    
    public static final UnicodeBlock KAITHI = new UnicodeBlock("KAITHI");
    
    public static final UnicodeBlock CUNEIFORM = new UnicodeBlock("CUNEIFORM");
    
    public static final UnicodeBlock CUNEIFORM_NUMBERS_AND_PUNCTUATION = new UnicodeBlock("CUNEIFORM_NUMBERS_AND_PUNCTUATION", new String[] { "CUNEIFORM NUMBERS AND PUNCTUATION", "CUNEIFORMNUMBERSANDPUNCTUATION" });
    
    public static final UnicodeBlock EGYPTIAN_HIEROGLYPHS = new UnicodeBlock("EGYPTIAN_HIEROGLYPHS", new String[] { "EGYPTIAN HIEROGLYPHS", "EGYPTIANHIEROGLYPHS" });
    
    public static final UnicodeBlock BAMUM_SUPPLEMENT = new UnicodeBlock("BAMUM_SUPPLEMENT", new String[] { "BAMUM SUPPLEMENT", "BAMUMSUPPLEMENT" });
    
    public static final UnicodeBlock KANA_SUPPLEMENT = new UnicodeBlock("KANA_SUPPLEMENT", new String[] { "KANA SUPPLEMENT", "KANASUPPLEMENT" });
    
    public static final UnicodeBlock ANCIENT_GREEK_MUSICAL_NOTATION = new UnicodeBlock("ANCIENT_GREEK_MUSICAL_NOTATION", new String[] { "ANCIENT GREEK MUSICAL NOTATION", "ANCIENTGREEKMUSICALNOTATION" });
    
    public static final UnicodeBlock COUNTING_ROD_NUMERALS = new UnicodeBlock("COUNTING_ROD_NUMERALS", new String[] { "COUNTING ROD NUMERALS", "COUNTINGRODNUMERALS" });
    
    public static final UnicodeBlock MAHJONG_TILES = new UnicodeBlock("MAHJONG_TILES", new String[] { "MAHJONG TILES", "MAHJONGTILES" });
    
    public static final UnicodeBlock DOMINO_TILES = new UnicodeBlock("DOMINO_TILES", new String[] { "DOMINO TILES", "DOMINOTILES" });
    
    public static final UnicodeBlock PLAYING_CARDS = new UnicodeBlock("PLAYING_CARDS", new String[] { "PLAYING CARDS", "PLAYINGCARDS" });
    
    public static final UnicodeBlock ENCLOSED_ALPHANUMERIC_SUPPLEMENT = new UnicodeBlock("ENCLOSED_ALPHANUMERIC_SUPPLEMENT", new String[] { "ENCLOSED ALPHANUMERIC SUPPLEMENT", "ENCLOSEDALPHANUMERICSUPPLEMENT" });
    
    public static final UnicodeBlock ENCLOSED_IDEOGRAPHIC_SUPPLEMENT = new UnicodeBlock("ENCLOSED_IDEOGRAPHIC_SUPPLEMENT", new String[] { "ENCLOSED IDEOGRAPHIC SUPPLEMENT", "ENCLOSEDIDEOGRAPHICSUPPLEMENT" });
    
    public static final UnicodeBlock MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS = new UnicodeBlock("MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS", new String[] { "MISCELLANEOUS SYMBOLS AND PICTOGRAPHS", "MISCELLANEOUSSYMBOLSANDPICTOGRAPHS" });
    
    public static final UnicodeBlock EMOTICONS = new UnicodeBlock("EMOTICONS");
    
    public static final UnicodeBlock TRANSPORT_AND_MAP_SYMBOLS = new UnicodeBlock("TRANSPORT_AND_MAP_SYMBOLS", new String[] { "TRANSPORT AND MAP SYMBOLS", "TRANSPORTANDMAPSYMBOLS" });
    
    public static final UnicodeBlock ALCHEMICAL_SYMBOLS = new UnicodeBlock("ALCHEMICAL_SYMBOLS", new String[] { "ALCHEMICAL SYMBOLS", "ALCHEMICALSYMBOLS" });
    
    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C", new String[] { "CJK UNIFIED IDEOGRAPHS EXTENSION C", "CJKUNIFIEDIDEOGRAPHSEXTENSIONC" });
    
    public static final UnicodeBlock CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D = new UnicodeBlock("CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D", new String[] { "CJK UNIFIED IDEOGRAPHS EXTENSION D", "CJKUNIFIEDIDEOGRAPHSEXTENSIOND" });
    
    public static final UnicodeBlock ARABIC_EXTENDED_A = new UnicodeBlock("ARABIC_EXTENDED_A", new String[] { "ARABIC EXTENDED-A", "ARABICEXTENDED-A" });
    
    public static final UnicodeBlock SUNDANESE_SUPPLEMENT = new UnicodeBlock("SUNDANESE_SUPPLEMENT", new String[] { "SUNDANESE SUPPLEMENT", "SUNDANESESUPPLEMENT" });
    
    public static final UnicodeBlock MEETEI_MAYEK_EXTENSIONS = new UnicodeBlock("MEETEI_MAYEK_EXTENSIONS", new String[] { "MEETEI MAYEK EXTENSIONS", "MEETEIMAYEKEXTENSIONS" });
    
    public static final UnicodeBlock MEROITIC_HIEROGLYPHS = new UnicodeBlock("MEROITIC_HIEROGLYPHS", new String[] { "MEROITIC HIEROGLYPHS", "MEROITICHIEROGLYPHS" });
    
    public static final UnicodeBlock MEROITIC_CURSIVE = new UnicodeBlock("MEROITIC_CURSIVE", new String[] { "MEROITIC CURSIVE", "MEROITICCURSIVE" });
    
    public static final UnicodeBlock SORA_SOMPENG = new UnicodeBlock("SORA_SOMPENG", new String[] { "SORA SOMPENG", "SORASOMPENG" });
    
    public static final UnicodeBlock CHAKMA = new UnicodeBlock("CHAKMA");
    
    public static final UnicodeBlock SHARADA = new UnicodeBlock("SHARADA");
    
    public static final UnicodeBlock TAKRI = new UnicodeBlock("TAKRI");
    
    public static final UnicodeBlock MIAO = new UnicodeBlock("MIAO");
    
    public static final UnicodeBlock ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS = new UnicodeBlock("ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS", new String[] { "ARABIC MATHEMATICAL ALPHABETIC SYMBOLS", "ARABICMATHEMATICALALPHABETICSYMBOLS" });
    
    private static final int[] blockStarts = { 
        0, 128, 256, 384, 592, 688, 768, 880, 1024, 1280, 
        1328, 1424, 1536, 1792, 1872, 1920, 1984, 2048, 2112, 2144, 
        2208, 2304, 2432, 2560, 2688, 2816, 2944, 3072, 3200, 3328, 
        3456, 3584, 3712, 3840, 4096, 4256, 4352, 4608, 4992, 5024, 
        5120, 5760, 5792, 5888, 5920, 5952, 5984, 6016, 6144, 6320, 
        6400, 6480, 6528, 6624, 6656, 6688, 6832, 6912, 7040, 7104, 
        7168, 7248, 7296, 7360, 7376, 7424, 7552, 7616, 7680, 7936, 
        8192, 8304, 8352, 8400, 8448, 8528, 8592, 8704, 8960, 9216, 
        9280, 9312, 9472, 9600, 9632, 9728, 9984, 10176, 10224, 10240, 
        10496, 10624, 10752, 11008, 11264, 11360, 11392, 11520, 11568, 11648, 
        11744, 11776, 11904, 12032, 12256, 12272, 12288, 12352, 12448, 12544, 
        12592, 12688, 12704, 12736, 12784, 12800, 13056, 13312, 19904, 19968, 
        40960, 42128, 42192, 42240, 42560, 42656, 42752, 42784, 43008, 43056, 
        43072, 43136, 43232, 43264, 43312, 43360, 43392, 43488, 43520, 43616, 
        43648, 43744, 43776, 43824, 43968, 44032, 55216, 55296, 56192, 56320, 
        57344, 63744, 64256, 64336, 65024, 65040, 65056, 65072, 65104, 65136, 
        65280, 65520, 65536, 65664, 65792, 65856, 65936, 66000, 66048, 66176, 
        66208, 66272, 66304, 66352, 66384, 66432, 66464, 66528, 66560, 66640, 
        66688, 66736, 67584, 67648, 67680, 67840, 67872, 67904, 67968, 68000, 
        68096, 68192, 68224, 68352, 68416, 68448, 68480, 68608, 68688, 69216, 
        69248, 69632, 69760, 69840, 69888, 69968, 70016, 70112, 71296, 71376, 
        73728, 74752, 74880, 77824, 78896, 92160, 92736, 93952, 94112, 110592, 
        110848, 118784, 119040, 119296, 119376, 119552, 119648, 119680, 119808, 120832, 
        126464, 126720, 126976, 127024, 127136, 127232, 127488, 127744, 128512, 128592, 
        128640, 128768, 128896, 131072, 173792, 173824, 177984, 178208, 194560, 195104, 
        917504, 917632, 917760, 918000, 983040, 1048576 };
    
    private static final UnicodeBlock[] blocks = { 
        BASIC_LATIN, LATIN_1_SUPPLEMENT, LATIN_EXTENDED_A, LATIN_EXTENDED_B, IPA_EXTENSIONS, SPACING_MODIFIER_LETTERS, COMBINING_DIACRITICAL_MARKS, GREEK, CYRILLIC, CYRILLIC_SUPPLEMENTARY, 
        ARMENIAN, HEBREW, ARABIC, SYRIAC, ARABIC_SUPPLEMENT, THAANA, NKO, SAMARITAN, MANDAIC, null, 
        ARABIC_EXTENDED_A, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, 
        SINHALA, THAI, LAO, TIBETAN, MYANMAR, GEORGIAN, HANGUL_JAMO, ETHIOPIC, ETHIOPIC_SUPPLEMENT, CHEROKEE, 
        UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS, OGHAM, RUNIC, TAGALOG, HANUNOO, BUHID, TAGBANWA, KHMER, MONGOLIAN, UNIFIED_CANADIAN_ABORIGINAL_SYLLABICS_EXTENDED, 
        LIMBU, TAI_LE, NEW_TAI_LUE, KHMER_SYMBOLS, BUGINESE, TAI_THAM, null, BALINESE, SUNDANESE, BATAK, 
        LEPCHA, OL_CHIKI, null, SUNDANESE_SUPPLEMENT, VEDIC_EXTENSIONS, PHONETIC_EXTENSIONS, PHONETIC_EXTENSIONS_SUPPLEMENT, COMBINING_DIACRITICAL_MARKS_SUPPLEMENT, LATIN_EXTENDED_ADDITIONAL, GREEK_EXTENDED, 
        GENERAL_PUNCTUATION, SUPERSCRIPTS_AND_SUBSCRIPTS, CURRENCY_SYMBOLS, COMBINING_MARKS_FOR_SYMBOLS, LETTERLIKE_SYMBOLS, NUMBER_FORMS, ARROWS, MATHEMATICAL_OPERATORS, MISCELLANEOUS_TECHNICAL, CONTROL_PICTURES, 
        OPTICAL_CHARACTER_RECOGNITION, ENCLOSED_ALPHANUMERICS, BOX_DRAWING, BLOCK_ELEMENTS, GEOMETRIC_SHAPES, MISCELLANEOUS_SYMBOLS, DINGBATS, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_A, SUPPLEMENTAL_ARROWS_A, BRAILLE_PATTERNS, 
        SUPPLEMENTAL_ARROWS_B, MISCELLANEOUS_MATHEMATICAL_SYMBOLS_B, SUPPLEMENTAL_MATHEMATICAL_OPERATORS, MISCELLANEOUS_SYMBOLS_AND_ARROWS, GLAGOLITIC, LATIN_EXTENDED_C, COPTIC, GEORGIAN_SUPPLEMENT, TIFINAGH, ETHIOPIC_EXTENDED, 
        CYRILLIC_EXTENDED_A, SUPPLEMENTAL_PUNCTUATION, CJK_RADICALS_SUPPLEMENT, KANGXI_RADICALS, null, IDEOGRAPHIC_DESCRIPTION_CHARACTERS, CJK_SYMBOLS_AND_PUNCTUATION, HIRAGANA, KATAKANA, BOPOMOFO, 
        HANGUL_COMPATIBILITY_JAMO, KANBUN, BOPOMOFO_EXTENDED, CJK_STROKES, KATAKANA_PHONETIC_EXTENSIONS, ENCLOSED_CJK_LETTERS_AND_MONTHS, CJK_COMPATIBILITY, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A, YIJING_HEXAGRAM_SYMBOLS, CJK_UNIFIED_IDEOGRAPHS, 
        YI_SYLLABLES, YI_RADICALS, LISU, VAI, CYRILLIC_EXTENDED_B, BAMUM, MODIFIER_TONE_LETTERS, LATIN_EXTENDED_D, SYLOTI_NAGRI, COMMON_INDIC_NUMBER_FORMS, 
        PHAGS_PA, SAURASHTRA, DEVANAGARI_EXTENDED, KAYAH_LI, REJANG, HANGUL_JAMO_EXTENDED_A, JAVANESE, null, CHAM, MYANMAR_EXTENDED_A, 
        TAI_VIET, MEETEI_MAYEK_EXTENSIONS, ETHIOPIC_EXTENDED_A, null, MEETEI_MAYEK, HANGUL_SYLLABLES, HANGUL_JAMO_EXTENDED_B, HIGH_SURROGATES, HIGH_PRIVATE_USE_SURROGATES, LOW_SURROGATES, 
        PRIVATE_USE_AREA, CJK_COMPATIBILITY_IDEOGRAPHS, ALPHABETIC_PRESENTATION_FORMS, ARABIC_PRESENTATION_FORMS_A, VARIATION_SELECTORS, VERTICAL_FORMS, COMBINING_HALF_MARKS, CJK_COMPATIBILITY_FORMS, SMALL_FORM_VARIANTS, ARABIC_PRESENTATION_FORMS_B, 
        HALFWIDTH_AND_FULLWIDTH_FORMS, SPECIALS, LINEAR_B_SYLLABARY, LINEAR_B_IDEOGRAMS, AEGEAN_NUMBERS, ANCIENT_GREEK_NUMBERS, ANCIENT_SYMBOLS, PHAISTOS_DISC, null, LYCIAN, 
        CARIAN, null, OLD_ITALIC, GOTHIC, null, UGARITIC, OLD_PERSIAN, null, DESERET, SHAVIAN, 
        OSMANYA, null, CYPRIOT_SYLLABARY, IMPERIAL_ARAMAIC, null, PHOENICIAN, LYDIAN, null, MEROITIC_HIEROGLYPHS, MEROITIC_CURSIVE, 
        KHAROSHTHI, OLD_SOUTH_ARABIAN, null, AVESTAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, null, OLD_TURKIC, null, RUMI_NUMERAL_SYMBOLS, 
        null, BRAHMI, KAITHI, SORA_SOMPENG, CHAKMA, null, SHARADA, null, TAKRI, null, 
        CUNEIFORM, CUNEIFORM_NUMBERS_AND_PUNCTUATION, null, EGYPTIAN_HIEROGLYPHS, null, BAMUM_SUPPLEMENT, null, MIAO, null, KANA_SUPPLEMENT, 
        null, BYZANTINE_MUSICAL_SYMBOLS, MUSICAL_SYMBOLS, ANCIENT_GREEK_MUSICAL_NOTATION, null, TAI_XUAN_JING_SYMBOLS, COUNTING_ROD_NUMERALS, null, MATHEMATICAL_ALPHANUMERIC_SYMBOLS, null, 
        ARABIC_MATHEMATICAL_ALPHABETIC_SYMBOLS, null, MAHJONG_TILES, DOMINO_TILES, PLAYING_CARDS, ENCLOSED_ALPHANUMERIC_SUPPLEMENT, ENCLOSED_IDEOGRAPHIC_SUPPLEMENT, MISCELLANEOUS_SYMBOLS_AND_PICTOGRAPHS, EMOTICONS, null, 
        TRANSPORT_AND_MAP_SYMBOLS, ALCHEMICAL_SYMBOLS, null, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B, null, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C, CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D, null, CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT, null, 
        TAGS, null, VARIATION_SELECTORS_SUPPLEMENT, null, SUPPLEMENTARY_PRIVATE_USE_AREA_A, SUPPLEMENTARY_PRIVATE_USE_AREA_B };
    
    private UnicodeBlock(String param1String) {
      super(param1String);
      map.put(param1String, this);
    }
    
    private UnicodeBlock(String param1String1, String param1String2) {
      this(param1String1);
      map.put(param1String2, this);
    }
    
    private UnicodeBlock(String param1String, String... param1VarArgs) {
      this(param1String);
      for (String str : param1VarArgs)
        map.put(str, this); 
    }
    
    public static UnicodeBlock of(char param1Char) { return of(param1Char); }
    
    public static UnicodeBlock of(int param1Int) {
      if (!Character.isValidCodePoint(param1Int))
        throw new IllegalArgumentException(); 
      int j = 0;
      int i = blockStarts.length;
      int k;
      for (k = i / 2; i - j > 1; k = (i + j) / 2) {
        if (param1Int >= blockStarts[k]) {
          j = k;
        } else {
          i = k;
        } 
      } 
      return blocks[k];
    }
    
    public static final UnicodeBlock forName(String param1String) {
      UnicodeBlock unicodeBlock = (UnicodeBlock)map.get(param1String.toUpperCase(Locale.US));
      if (unicodeBlock == null)
        throw new IllegalArgumentException(); 
      return unicodeBlock;
    }
  }
  
  public enum UnicodeScript {
    COMMON, LATIN, GREEK, CYRILLIC, ARMENIAN, HEBREW, ARABIC, SYRIAC, THAANA, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, LAO, TIBETAN, MYANMAR, GEORGIAN, HANGUL, ETHIOPIC, CHEROKEE, CANADIAN_ABORIGINAL, OGHAM, RUNIC, KHMER, MONGOLIAN, HIRAGANA, KATAKANA, BOPOMOFO, HAN, YI, OLD_ITALIC, GOTHIC, DESERET, INHERITED, TAGALOG, HANUNOO, BUHID, TAGBANWA, LIMBU, TAI_LE, LINEAR_B, UGARITIC, SHAVIAN, OSMANYA, CYPRIOT, BRAILLE, BUGINESE, COPTIC, NEW_TAI_LUE, GLAGOLITIC, TIFINAGH, SYLOTI_NAGRI, OLD_PERSIAN, KHAROSHTHI, BALINESE, CUNEIFORM, PHOENICIAN, PHAGS_PA, NKO, SUNDANESE, BATAK, LEPCHA, OL_CHIKI, VAI, SAURASHTRA, KAYAH_LI, REJANG, LYCIAN, CARIAN, LYDIAN, CHAM, TAI_THAM, TAI_VIET, AVESTAN, EGYPTIAN_HIEROGLYPHS, SAMARITAN, MANDAIC, LISU, BAMUM, JAVANESE, MEETEI_MAYEK, IMPERIAL_ARAMAIC, OLD_SOUTH_ARABIAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, OLD_TURKIC, BRAHMI, KAITHI, MEROITIC_HIEROGLYPHS, MEROITIC_CURSIVE, SORA_SOMPENG, CHAKMA, SHARADA, TAKRI, MIAO, UNKNOWN;
    
    private static final int[] scriptStarts;
    
    private static final UnicodeScript[] scripts;
    
    private static HashMap<String, UnicodeScript> aliases;
    
    public static UnicodeScript of(int param1Int) {
      if (!Character.isValidCodePoint(param1Int))
        throw new IllegalArgumentException(); 
      int i = Character.getType(param1Int);
      if (i == 0)
        return UNKNOWN; 
      int j = Arrays.binarySearch(scriptStarts, param1Int);
      if (j < 0)
        j = -j - 2; 
      return scripts[j];
    }
    
    public static final UnicodeScript forName(String param1String) {
      param1String = param1String.toUpperCase(Locale.ENGLISH);
      UnicodeScript unicodeScript = (UnicodeScript)aliases.get(param1String);
      return (unicodeScript != null) ? unicodeScript : valueOf(param1String);
    }
    
    static  {
      scriptStarts = new int[] { 
          0, 65, 91, 97, 123, 170, 171, 186, 187, 192, 
          215, 216, 247, 248, 697, 736, 741, 746, 748, 768, 
          880, 884, 885, 894, 900, 901, 902, 903, 904, 994, 
          1008, 1024, 1157, 1159, 1329, 1417, 1418, 1425, 1536, 1548, 
          1549, 1563, 1566, 1567, 1568, 1600, 1601, 1611, 1622, 1632, 
          1642, 1648, 1649, 1757, 1758, 1792, 1872, 1920, 1984, 2048, 
          2112, 2208, 2304, 2385, 2387, 2404, 2406, 2433, 2561, 2689, 
          2817, 2946, 3073, 3202, 3330, 3458, 3585, 3647, 3648, 3713, 
          3840, 4053, 4057, 4096, 4256, 4347, 4348, 4352, 4608, 5024, 
          5120, 5760, 5792, 5867, 5870, 5888, 5920, 5941, 5952, 5984, 
          6016, 6144, 6146, 6148, 6149, 6150, 6320, 6400, 6480, 6528, 
          6624, 6656, 6688, 6912, 7040, 7104, 7168, 7248, 7360, 7376, 
          7379, 7380, 7393, 7394, 7401, 7405, 7406, 7412, 7413, 7424, 
          7462, 7467, 7468, 7517, 7522, 7526, 7531, 7544, 7545, 7615, 
          7616, 7680, 7936, 8192, 8204, 8206, 8305, 8308, 8319, 8320, 
          8336, 8352, 8400, 8448, 8486, 8487, 8490, 8492, 8498, 8499, 
          8526, 8527, 8544, 8585, 10240, 10496, 11264, 11360, 11392, 11520, 
          11568, 11648, 11744, 11776, 11904, 12272, 12293, 12294, 12295, 12296, 
          12321, 12330, 12334, 12336, 12344, 12348, 12353, 12441, 12443, 12445, 
          12448, 12449, 12539, 12541, 12549, 12593, 12688, 12704, 12736, 12784, 
          12800, 12832, 12896, 12927, 13008, 13144, 13312, 19904, 19968, 40960, 
          42192, 42240, 42560, 42656, 42752, 42786, 42888, 42891, 43008, 43056, 
          43072, 43136, 43232, 43264, 43312, 43360, 43392, 43520, 43616, 43648, 
          43744, 43777, 43968, 44032, 55292, 63744, 64256, 64275, 64285, 64336, 
          64830, 64848, 65021, 65024, 65040, 65056, 65072, 65136, 65279, 65313, 
          65339, 65345, 65371, 65382, 65392, 65393, 65438, 65440, 65504, 65536, 
          65792, 65856, 65936, 66045, 66176, 66208, 66304, 66352, 66432, 66464, 
          66560, 66640, 66688, 67584, 67648, 67840, 67872, 67968, 68000, 68096, 
          68192, 68352, 68416, 68448, 68608, 69216, 69632, 69760, 69840, 69888, 
          70016, 71296, 73728, 77824, 92160, 93952, 110592, 110593, 118784, 119143, 
          119146, 119163, 119171, 119173, 119180, 119210, 119214, 119296, 119552, 126464, 
          126976, 127488, 127489, 131072, 917505, 917760, 918000 };
      scripts = new UnicodeScript[] { 
          COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, 
          COMMON, LATIN, COMMON, LATIN, COMMON, LATIN, COMMON, BOPOMOFO, COMMON, INHERITED, 
          GREEK, COMMON, GREEK, COMMON, GREEK, COMMON, GREEK, COMMON, GREEK, COPTIC, 
          GREEK, CYRILLIC, INHERITED, CYRILLIC, ARMENIAN, COMMON, ARMENIAN, HEBREW, ARABIC, COMMON, 
          ARABIC, COMMON, ARABIC, COMMON, ARABIC, COMMON, ARABIC, INHERITED, ARABIC, COMMON, 
          ARABIC, INHERITED, ARABIC, COMMON, ARABIC, SYRIAC, ARABIC, THAANA, NKO, SAMARITAN, 
          MANDAIC, ARABIC, DEVANAGARI, INHERITED, DEVANAGARI, COMMON, DEVANAGARI, BENGALI, GURMUKHI, GUJARATI, 
          ORIYA, TAMIL, TELUGU, KANNADA, MALAYALAM, SINHALA, THAI, COMMON, THAI, LAO, 
          TIBETAN, COMMON, TIBETAN, MYANMAR, GEORGIAN, COMMON, GEORGIAN, HANGUL, ETHIOPIC, CHEROKEE, 
          CANADIAN_ABORIGINAL, OGHAM, RUNIC, COMMON, RUNIC, TAGALOG, HANUNOO, COMMON, BUHID, TAGBANWA, 
          KHMER, MONGOLIAN, COMMON, MONGOLIAN, COMMON, MONGOLIAN, CANADIAN_ABORIGINAL, LIMBU, TAI_LE, NEW_TAI_LUE, 
          KHMER, BUGINESE, TAI_THAM, BALINESE, SUNDANESE, BATAK, LEPCHA, OL_CHIKI, SUNDANESE, INHERITED, 
          COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, LATIN, 
          GREEK, CYRILLIC, LATIN, GREEK, LATIN, GREEK, LATIN, CYRILLIC, LATIN, GREEK, 
          INHERITED, LATIN, GREEK, COMMON, INHERITED, COMMON, LATIN, COMMON, LATIN, COMMON, 
          LATIN, COMMON, INHERITED, COMMON, GREEK, COMMON, LATIN, COMMON, LATIN, COMMON, 
          LATIN, COMMON, LATIN, COMMON, BRAILLE, COMMON, GLAGOLITIC, LATIN, COPTIC, GEORGIAN, 
          TIFINAGH, ETHIOPIC, CYRILLIC, COMMON, HAN, COMMON, HAN, COMMON, HAN, COMMON, 
          HAN, INHERITED, HANGUL, COMMON, HAN, COMMON, HIRAGANA, INHERITED, COMMON, HIRAGANA, 
          COMMON, KATAKANA, COMMON, KATAKANA, BOPOMOFO, HANGUL, COMMON, BOPOMOFO, COMMON, KATAKANA, 
          HANGUL, COMMON, HANGUL, COMMON, KATAKANA, COMMON, HAN, COMMON, HAN, YI, 
          LISU, VAI, CYRILLIC, BAMUM, COMMON, LATIN, COMMON, LATIN, SYLOTI_NAGRI, COMMON, 
          PHAGS_PA, SAURASHTRA, DEVANAGARI, KAYAH_LI, REJANG, HANGUL, JAVANESE, CHAM, MYANMAR, TAI_VIET, 
          MEETEI_MAYEK, ETHIOPIC, MEETEI_MAYEK, HANGUL, UNKNOWN, HAN, LATIN, ARMENIAN, HEBREW, ARABIC, 
          COMMON, ARABIC, COMMON, INHERITED, COMMON, INHERITED, COMMON, ARABIC, COMMON, LATIN, 
          COMMON, LATIN, COMMON, KATAKANA, COMMON, KATAKANA, COMMON, HANGUL, COMMON, LINEAR_B, 
          COMMON, GREEK, COMMON, INHERITED, LYCIAN, CARIAN, OLD_ITALIC, GOTHIC, UGARITIC, OLD_PERSIAN, 
          DESERET, SHAVIAN, OSMANYA, CYPRIOT, IMPERIAL_ARAMAIC, PHOENICIAN, LYDIAN, MEROITIC_HIEROGLYPHS, MEROITIC_CURSIVE, KHAROSHTHI, 
          OLD_SOUTH_ARABIAN, AVESTAN, INSCRIPTIONAL_PARTHIAN, INSCRIPTIONAL_PAHLAVI, OLD_TURKIC, ARABIC, BRAHMI, KAITHI, SORA_SOMPENG, CHAKMA, 
          SHARADA, TAKRI, CUNEIFORM, EGYPTIAN_HIEROGLYPHS, BAMUM, MIAO, KATAKANA, HIRAGANA, COMMON, INHERITED, 
          COMMON, INHERITED, COMMON, INHERITED, COMMON, INHERITED, COMMON, GREEK, COMMON, ARABIC, 
          COMMON, HIRAGANA, COMMON, HAN, COMMON, INHERITED, UNKNOWN };
      aliases = new HashMap(128);
      aliases.put("ARAB", ARABIC);
      aliases.put("ARMI", IMPERIAL_ARAMAIC);
      aliases.put("ARMN", ARMENIAN);
      aliases.put("AVST", AVESTAN);
      aliases.put("BALI", BALINESE);
      aliases.put("BAMU", BAMUM);
      aliases.put("BATK", BATAK);
      aliases.put("BENG", BENGALI);
      aliases.put("BOPO", BOPOMOFO);
      aliases.put("BRAI", BRAILLE);
      aliases.put("BRAH", BRAHMI);
      aliases.put("BUGI", BUGINESE);
      aliases.put("BUHD", BUHID);
      aliases.put("CAKM", CHAKMA);
      aliases.put("CANS", CANADIAN_ABORIGINAL);
      aliases.put("CARI", CARIAN);
      aliases.put("CHAM", CHAM);
      aliases.put("CHER", CHEROKEE);
      aliases.put("COPT", COPTIC);
      aliases.put("CPRT", CYPRIOT);
      aliases.put("CYRL", CYRILLIC);
      aliases.put("DEVA", DEVANAGARI);
      aliases.put("DSRT", DESERET);
      aliases.put("EGYP", EGYPTIAN_HIEROGLYPHS);
      aliases.put("ETHI", ETHIOPIC);
      aliases.put("GEOR", GEORGIAN);
      aliases.put("GLAG", GLAGOLITIC);
      aliases.put("GOTH", GOTHIC);
      aliases.put("GREK", GREEK);
      aliases.put("GUJR", GUJARATI);
      aliases.put("GURU", GURMUKHI);
      aliases.put("HANG", HANGUL);
      aliases.put("HANI", HAN);
      aliases.put("HANO", HANUNOO);
      aliases.put("HEBR", HEBREW);
      aliases.put("HIRA", HIRAGANA);
      aliases.put("ITAL", OLD_ITALIC);
      aliases.put("JAVA", JAVANESE);
      aliases.put("KALI", KAYAH_LI);
      aliases.put("KANA", KATAKANA);
      aliases.put("KHAR", KHAROSHTHI);
      aliases.put("KHMR", KHMER);
      aliases.put("KNDA", KANNADA);
      aliases.put("KTHI", KAITHI);
      aliases.put("LANA", TAI_THAM);
      aliases.put("LAOO", LAO);
      aliases.put("LATN", LATIN);
      aliases.put("LEPC", LEPCHA);
      aliases.put("LIMB", LIMBU);
      aliases.put("LINB", LINEAR_B);
      aliases.put("LISU", LISU);
      aliases.put("LYCI", LYCIAN);
      aliases.put("LYDI", LYDIAN);
      aliases.put("MAND", MANDAIC);
      aliases.put("MERC", MEROITIC_CURSIVE);
      aliases.put("MERO", MEROITIC_HIEROGLYPHS);
      aliases.put("MLYM", MALAYALAM);
      aliases.put("MONG", MONGOLIAN);
      aliases.put("MTEI", MEETEI_MAYEK);
      aliases.put("MYMR", MYANMAR);
      aliases.put("NKOO", NKO);
      aliases.put("OGAM", OGHAM);
      aliases.put("OLCK", OL_CHIKI);
      aliases.put("ORKH", OLD_TURKIC);
      aliases.put("ORYA", ORIYA);
      aliases.put("OSMA", OSMANYA);
      aliases.put("PHAG", PHAGS_PA);
      aliases.put("PLRD", MIAO);
      aliases.put("PHLI", INSCRIPTIONAL_PAHLAVI);
      aliases.put("PHNX", PHOENICIAN);
      aliases.put("PRTI", INSCRIPTIONAL_PARTHIAN);
      aliases.put("RJNG", REJANG);
      aliases.put("RUNR", RUNIC);
      aliases.put("SAMR", SAMARITAN);
      aliases.put("SARB", OLD_SOUTH_ARABIAN);
      aliases.put("SAUR", SAURASHTRA);
      aliases.put("SHAW", SHAVIAN);
      aliases.put("SHRD", SHARADA);
      aliases.put("SINH", SINHALA);
      aliases.put("SORA", SORA_SOMPENG);
      aliases.put("SUND", SUNDANESE);
      aliases.put("SYLO", SYLOTI_NAGRI);
      aliases.put("SYRC", SYRIAC);
      aliases.put("TAGB", TAGBANWA);
      aliases.put("TALE", TAI_LE);
      aliases.put("TAKR", TAKRI);
      aliases.put("TALU", NEW_TAI_LUE);
      aliases.put("TAML", TAMIL);
      aliases.put("TAVT", TAI_VIET);
      aliases.put("TELU", TELUGU);
      aliases.put("TFNG", TIFINAGH);
      aliases.put("TGLG", TAGALOG);
      aliases.put("THAA", THAANA);
      aliases.put("THAI", THAI);
      aliases.put("TIBT", TIBETAN);
      aliases.put("UGAR", UGARITIC);
      aliases.put("VAII", VAI);
      aliases.put("XPEO", OLD_PERSIAN);
      aliases.put("XSUX", CUNEIFORM);
      aliases.put("YIII", YI);
      aliases.put("ZINH", INHERITED);
      aliases.put("ZYYY", COMMON);
      aliases.put("ZZZZ", UNKNOWN);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\Character.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
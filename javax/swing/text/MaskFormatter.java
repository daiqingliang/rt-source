package javax.swing.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import javax.swing.JFormattedTextField;

public class MaskFormatter extends DefaultFormatter {
  private static final char DIGIT_KEY = '#';
  
  private static final char LITERAL_KEY = '\'';
  
  private static final char UPPERCASE_KEY = 'U';
  
  private static final char LOWERCASE_KEY = 'L';
  
  private static final char ALPHA_NUMERIC_KEY = 'A';
  
  private static final char CHARACTER_KEY = '?';
  
  private static final char ANYTHING_KEY = '*';
  
  private static final char HEX_KEY = 'H';
  
  private static final MaskCharacter[] EmptyMaskChars = new MaskCharacter[0];
  
  private String mask;
  
  private MaskCharacter[] maskChars;
  
  private String validCharacters;
  
  private String invalidCharacters;
  
  private String placeholderString;
  
  private char placeholder;
  
  private boolean containsLiteralChars;
  
  public MaskFormatter() {
    setAllowsInvalid(false);
    this.containsLiteralChars = true;
    this.maskChars = EmptyMaskChars;
    this.placeholder = ' ';
  }
  
  public MaskFormatter(String paramString) throws ParseException {
    this();
    setMask(paramString);
  }
  
  public void setMask(String paramString) throws ParseException {
    this.mask = paramString;
    updateInternalMask();
  }
  
  public String getMask() { return this.mask; }
  
  public void setValidCharacters(String paramString) throws ParseException { this.validCharacters = paramString; }
  
  public String getValidCharacters() { return this.validCharacters; }
  
  public void setInvalidCharacters(String paramString) throws ParseException { this.invalidCharacters = paramString; }
  
  public String getInvalidCharacters() { return this.invalidCharacters; }
  
  public void setPlaceholder(String paramString) throws ParseException { this.placeholderString = paramString; }
  
  public String getPlaceholder() { return this.placeholderString; }
  
  public void setPlaceholderCharacter(char paramChar) { this.placeholder = paramChar; }
  
  public char getPlaceholderCharacter() { return this.placeholder; }
  
  public void setValueContainsLiteralCharacters(boolean paramBoolean) { this.containsLiteralChars = paramBoolean; }
  
  public boolean getValueContainsLiteralCharacters() { return this.containsLiteralChars; }
  
  public Object stringToValue(String paramString) throws ParseException { return stringToValue(paramString, true); }
  
  public String valueToString(Object paramObject) throws ParseException {
    String str1 = (paramObject == null) ? "" : paramObject.toString();
    StringBuilder stringBuilder = new StringBuilder();
    String str2 = getPlaceholder();
    int[] arrayOfInt = { 0 };
    append(stringBuilder, str1, arrayOfInt, str2, this.maskChars);
    return stringBuilder.toString();
  }
  
  public void install(JFormattedTextField paramJFormattedTextField) {
    super.install(paramJFormattedTextField);
    if (paramJFormattedTextField != null) {
      Object object = paramJFormattedTextField.getValue();
      try {
        stringToValue(valueToString(object));
      } catch (ParseException parseException) {
        setEditValid(false);
      } 
    } 
  }
  
  private Object stringToValue(String paramString, boolean paramBoolean) throws ParseException {
    int i;
    if ((i = getInvalidOffset(paramString, paramBoolean)) == -1) {
      if (!getValueContainsLiteralCharacters())
        paramString = stripLiteralChars(paramString); 
      return super.stringToValue(paramString);
    } 
    throw new ParseException("stringToValue passed invalid value", i);
  }
  
  private int getInvalidOffset(String paramString, boolean paramBoolean) {
    int i = paramString.length();
    if (i != getMaxLength())
      return i; 
    byte b = 0;
    int j = paramString.length();
    while (b < j) {
      char c = paramString.charAt(b);
      if (!isValidCharacter(b, c) && (paramBoolean || !isPlaceholder(b, c)))
        return b; 
      b++;
    } 
    return -1;
  }
  
  private void append(StringBuilder paramStringBuilder, String paramString1, int[] paramArrayOfInt, String paramString2, MaskCharacter[] paramArrayOfMaskCharacter) throws ParseException {
    byte b = 0;
    int i = paramArrayOfMaskCharacter.length;
    while (b < i) {
      paramArrayOfMaskCharacter[b].append(paramStringBuilder, paramString1, paramArrayOfInt, paramString2);
      b++;
    } 
  }
  
  private void updateInternalMask() {
    String str = getMask();
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = arrayList1;
    if (str != null) {
      byte b = 0;
      int i = str.length();
      while (b < i) {
        char c = str.charAt(b);
        switch (c) {
          case '#':
            arrayList2.add(new DigitMaskCharacter(null));
            break;
          case '\'':
            if (++b < i) {
              c = str.charAt(b);
              arrayList2.add(new LiteralCharacter(c));
            } 
            break;
          case 'U':
            arrayList2.add(new UpperCaseCharacter(null));
            break;
          case 'L':
            arrayList2.add(new LowerCaseCharacter(null));
            break;
          case 'A':
            arrayList2.add(new AlphaNumericCharacter(null));
            break;
          case '?':
            arrayList2.add(new CharCharacter(null));
            break;
          case '*':
            arrayList2.add(new MaskCharacter(null));
            break;
          case 'H':
            arrayList2.add(new HexCharacter(null));
            break;
          default:
            arrayList2.add(new LiteralCharacter(c));
            break;
        } 
        b++;
      } 
    } 
    if (arrayList1.size() == 0) {
      this.maskChars = EmptyMaskChars;
    } else {
      this.maskChars = new MaskCharacter[arrayList1.size()];
      arrayList1.toArray(this.maskChars);
    } 
  }
  
  private MaskCharacter getMaskCharacter(int paramInt) { return (paramInt >= this.maskChars.length) ? null : this.maskChars[paramInt]; }
  
  private boolean isPlaceholder(int paramInt, char paramChar) { return (getPlaceholderCharacter() == paramChar); }
  
  private boolean isValidCharacter(int paramInt, char paramChar) { return getMaskCharacter(paramInt).isValidCharacter(paramChar); }
  
  private boolean isLiteral(int paramInt) { return getMaskCharacter(paramInt).isLiteral(); }
  
  private int getMaxLength() { return this.maskChars.length; }
  
  private char getLiteral(int paramInt) { return getMaskCharacter(paramInt).getChar(false); }
  
  private char getCharacter(int paramInt, char paramChar) { return getMaskCharacter(paramInt).getChar(paramChar); }
  
  private String stripLiteralChars(String paramString) {
    StringBuilder stringBuilder = null;
    byte b1 = 0;
    byte b2 = 0;
    int i = paramString.length();
    while (b2 < i) {
      if (isLiteral(b2)) {
        if (stringBuilder == null) {
          stringBuilder = new StringBuilder();
          if (b2 > 0)
            stringBuilder.append(paramString.substring(0, b2)); 
          b1 = b2 + 1;
        } else if (b1 != b2) {
          stringBuilder.append(paramString.substring(b1, b2));
        } 
        b1 = b2 + 1;
      } 
      b2++;
    } 
    if (stringBuilder == null)
      return paramString; 
    if (b1 != paramString.length()) {
      if (stringBuilder == null)
        return paramString.substring(b1); 
      stringBuilder.append(paramString.substring(b1));
    } 
    return stringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      updateInternalMask();
    } catch (ParseException parseException) {}
  }
  
  boolean isNavigatable(int paramInt) { return !getAllowsInvalid() ? ((paramInt < getMaxLength() && !isLiteral(paramInt))) : true; }
  
  boolean isValidEdit(DefaultFormatter.ReplaceHolder paramReplaceHolder) {
    if (!getAllowsInvalid()) {
      String str = getReplaceString(paramReplaceHolder.offset, paramReplaceHolder.length, paramReplaceHolder.text);
      try {
        paramReplaceHolder.value = stringToValue(str, false);
        return true;
      } catch (ParseException parseException) {
        return false;
      } 
    } 
    return true;
  }
  
  boolean canReplace(DefaultFormatter.ReplaceHolder paramReplaceHolder) {
    if (!getAllowsInvalid()) {
      StringBuilder stringBuilder = null;
      String str = paramReplaceHolder.text;
      int i = (str != null) ? str.length() : 0;
      if (i == 0 && paramReplaceHolder.length == 1 && getFormattedTextField().getSelectionStart() != paramReplaceHolder.offset)
        while (paramReplaceHolder.offset > 0 && isLiteral(paramReplaceHolder.offset))
          paramReplaceHolder.offset--;  
      int j = Math.min(getMaxLength() - paramReplaceHolder.offset, Math.max(i, paramReplaceHolder.length));
      int k = 0;
      byte b = 0;
      while (k < j) {
        if (b < i && isValidCharacter(paramReplaceHolder.offset + k, str.charAt(b))) {
          char c = str.charAt(b);
          if (c != getCharacter(paramReplaceHolder.offset + k, c) && stringBuilder == null) {
            stringBuilder = new StringBuilder();
            if (b > 0)
              stringBuilder.append(str.substring(0, b)); 
          } 
          if (stringBuilder != null)
            stringBuilder.append(getCharacter(paramReplaceHolder.offset + k, c)); 
          b++;
        } else if (isLiteral(paramReplaceHolder.offset + k)) {
          if (stringBuilder != null) {
            stringBuilder.append(getLiteral(paramReplaceHolder.offset + k));
            if (b < i)
              j = Math.min(j + 1, getMaxLength() - paramReplaceHolder.offset); 
          } else if (b > 0) {
            stringBuilder = new StringBuilder(j);
            stringBuilder.append(str.substring(0, b));
            stringBuilder.append(getLiteral(paramReplaceHolder.offset + k));
            if (b < i) {
              j = Math.min(j + 1, getMaxLength() - paramReplaceHolder.offset);
            } else if (paramReplaceHolder.cursorPosition == -1) {
              paramReplaceHolder.cursorPosition = paramReplaceHolder.offset + k;
            } 
          } else {
            paramReplaceHolder.offset++;
            paramReplaceHolder.length--;
            k--;
            j--;
          } 
        } else if (b >= i) {
          if (stringBuilder == null) {
            stringBuilder = new StringBuilder();
            if (str != null)
              stringBuilder.append(str); 
          } 
          stringBuilder.append(getPlaceholderCharacter());
          if (i > 0 && paramReplaceHolder.cursorPosition == -1)
            paramReplaceHolder.cursorPosition = paramReplaceHolder.offset + k; 
        } else {
          return false;
        } 
        k++;
      } 
      if (stringBuilder != null) {
        paramReplaceHolder.text = stringBuilder.toString();
      } else if (str != null && paramReplaceHolder.offset + i > getMaxLength()) {
        paramReplaceHolder.text = str.substring(0, getMaxLength() - paramReplaceHolder.offset);
      } 
      if (getOverwriteMode() && paramReplaceHolder.text != null)
        paramReplaceHolder.length = paramReplaceHolder.text.length(); 
    } 
    return super.canReplace(paramReplaceHolder);
  }
  
  private class AlphaNumericCharacter extends MaskCharacter {
    private AlphaNumericCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return (Character.isLetterOrDigit(param1Char) && super.isValidCharacter(param1Char)); }
  }
  
  private class CharCharacter extends MaskCharacter {
    private CharCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return (Character.isLetter(param1Char) && super.isValidCharacter(param1Char)); }
  }
  
  private class DigitMaskCharacter extends MaskCharacter {
    private DigitMaskCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return (Character.isDigit(param1Char) && super.isValidCharacter(param1Char)); }
  }
  
  private class HexCharacter extends MaskCharacter {
    private HexCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return ((param1Char == '0' || param1Char == '1' || param1Char == '2' || param1Char == '3' || param1Char == '4' || param1Char == '5' || param1Char == '6' || param1Char == '7' || param1Char == '8' || param1Char == '9' || param1Char == 'a' || param1Char == 'A' || param1Char == 'b' || param1Char == 'B' || param1Char == 'c' || param1Char == 'C' || param1Char == 'd' || param1Char == 'D' || param1Char == 'e' || param1Char == 'E' || param1Char == 'f' || param1Char == 'F') && super.isValidCharacter(param1Char)); }
    
    public char getChar(char param1Char) { return Character.isDigit(param1Char) ? param1Char : Character.toUpperCase(param1Char); }
  }
  
  private class LiteralCharacter extends MaskCharacter {
    private char fixedChar;
    
    public LiteralCharacter(char param1Char) {
      super(MaskFormatter.this, null);
      this.fixedChar = param1Char;
    }
    
    public boolean isLiteral() { return true; }
    
    public char getChar(char param1Char) { return this.fixedChar; }
  }
  
  private class LowerCaseCharacter extends MaskCharacter {
    private LowerCaseCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return (Character.isLetter(param1Char) && super.isValidCharacter(param1Char)); }
    
    public char getChar(char param1Char) { return Character.toLowerCase(param1Char); }
  }
  
  private class MaskCharacter {
    private MaskCharacter() {}
    
    public boolean isLiteral() { return false; }
    
    public boolean isValidCharacter(char param1Char) {
      if (isLiteral())
        return (getChar(param1Char) == param1Char); 
      param1Char = getChar(param1Char);
      String str = MaskFormatter.this.getValidCharacters();
      if (str != null && str.indexOf(param1Char) == -1)
        return false; 
      str = MaskFormatter.this.getInvalidCharacters();
      return !(str != null && str.indexOf(param1Char) != -1);
    }
    
    public char getChar(char param1Char) { return param1Char; }
    
    public void append(StringBuilder param1StringBuilder, String param1String1, int[] param1ArrayOfInt, String param1String2) throws ParseException {
      boolean bool = (param1ArrayOfInt[0] < param1String1.length()) ? 1 : 0;
      char c = bool ? param1String1.charAt(param1ArrayOfInt[0]) : 0;
      if (isLiteral()) {
        param1StringBuilder.append(getChar(c));
        if (MaskFormatter.this.getValueContainsLiteralCharacters()) {
          if (bool && c != getChar(c))
            throw new ParseException("Invalid character: " + c, param1ArrayOfInt[0]); 
          param1ArrayOfInt[0] = param1ArrayOfInt[0] + 1;
        } 
      } else if (param1ArrayOfInt[0] >= param1String1.length()) {
        if (param1String2 != null && param1ArrayOfInt[0] < param1String2.length()) {
          param1StringBuilder.append(param1String2.charAt(param1ArrayOfInt[0]));
        } else {
          param1StringBuilder.append(MaskFormatter.this.getPlaceholderCharacter());
        } 
        param1ArrayOfInt[0] = param1ArrayOfInt[0] + 1;
      } else if (isValidCharacter(c)) {
        param1StringBuilder.append(getChar(c));
        param1ArrayOfInt[0] = param1ArrayOfInt[0] + 1;
      } else {
        throw new ParseException("Invalid character: " + c, param1ArrayOfInt[0]);
      } 
    }
  }
  
  private class UpperCaseCharacter extends MaskCharacter {
    private UpperCaseCharacter() { super(MaskFormatter.this, null); }
    
    public boolean isValidCharacter(char param1Char) { return (Character.isLetter(param1Char) && super.isValidCharacter(param1Char)); }
    
    public char getChar(char param1Char) { return Character.toUpperCase(param1Char); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\MaskFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
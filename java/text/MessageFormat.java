package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MessageFormat extends Format {
  private static final long serialVersionUID = 6479157306784022952L;
  
  private Locale locale = Locale.getDefault(Locale.Category.FORMAT);
  
  private String pattern = "";
  
  private static final int INITIAL_FORMATS = 10;
  
  private Format[] formats = new Format[10];
  
  private int[] offsets = new int[10];
  
  private int[] argumentNumbers = new int[10];
  
  private int maxOffset = -1;
  
  private static final int SEG_RAW = 0;
  
  private static final int SEG_INDEX = 1;
  
  private static final int SEG_TYPE = 2;
  
  private static final int SEG_MODIFIER = 3;
  
  private static final int TYPE_NULL = 0;
  
  private static final int TYPE_NUMBER = 1;
  
  private static final int TYPE_DATE = 2;
  
  private static final int TYPE_TIME = 3;
  
  private static final int TYPE_CHOICE = 4;
  
  private static final String[] TYPE_KEYWORDS = { "", "number", "date", "time", "choice" };
  
  private static final int MODIFIER_DEFAULT = 0;
  
  private static final int MODIFIER_CURRENCY = 1;
  
  private static final int MODIFIER_PERCENT = 2;
  
  private static final int MODIFIER_INTEGER = 3;
  
  private static final String[] NUMBER_MODIFIER_KEYWORDS = { "", "currency", "percent", "integer" };
  
  private static final int MODIFIER_SHORT = 1;
  
  private static final int MODIFIER_MEDIUM = 2;
  
  private static final int MODIFIER_LONG = 3;
  
  private static final int MODIFIER_FULL = 4;
  
  private static final String[] DATE_TIME_MODIFIER_KEYWORDS = { "", "short", "medium", "long", "full" };
  
  private static final int[] DATE_TIME_MODIFIERS = { 2, 3, 2, 1, 0 };
  
  public MessageFormat(String paramString) { applyPattern(paramString); }
  
  public MessageFormat(String paramString, Locale paramLocale) { applyPattern(paramString); }
  
  public void setLocale(Locale paramLocale) { this.locale = paramLocale; }
  
  public Locale getLocale() { return this.locale; }
  
  public void applyPattern(String paramString) {
    StringBuilder[] arrayOfStringBuilder = new StringBuilder[4];
    arrayOfStringBuilder[0] = new StringBuilder();
    byte b1 = 0;
    byte b2 = 0;
    boolean bool = false;
    byte b3 = 0;
    this.maxOffset = -1;
    for (byte b4 = 0; b4 < paramString.length(); b4++) {
      char c = paramString.charAt(b4);
      if (!b1) {
        if (c == '\'') {
          if (b4 + 1 < paramString.length() && paramString.charAt(b4 + 1) == '\'') {
            arrayOfStringBuilder[b1].append(c);
            b4++;
          } else {
            bool = !bool ? 1 : 0;
          } 
        } else if (c == '{' && !bool) {
          b1 = 1;
          if (arrayOfStringBuilder[true] == null)
            arrayOfStringBuilder[1] = new StringBuilder(); 
        } else {
          arrayOfStringBuilder[b1].append(c);
        } 
      } else if (bool) {
        arrayOfStringBuilder[b1].append(c);
        if (c == '\'')
          bool = false; 
      } else {
        switch (c) {
          case ',':
            if (b1 < 3) {
              if (arrayOfStringBuilder[++b1] == null)
                arrayOfStringBuilder[b1] = new StringBuilder(); 
              break;
            } 
            arrayOfStringBuilder[b1].append(c);
            break;
          case '{':
            b3++;
            arrayOfStringBuilder[b1].append(c);
            break;
          case '}':
            if (b3 == 0) {
              b1 = 0;
              makeFormat(b4, b2, arrayOfStringBuilder);
              b2++;
              arrayOfStringBuilder[1] = null;
              arrayOfStringBuilder[2] = null;
              arrayOfStringBuilder[3] = null;
              break;
            } 
            b3--;
            arrayOfStringBuilder[b1].append(c);
            break;
          case ' ':
            if (b1 != 2 || arrayOfStringBuilder[2].length() > 0)
              arrayOfStringBuilder[b1].append(c); 
            break;
          case '\'':
            bool = true;
          default:
            arrayOfStringBuilder[b1].append(c);
            break;
        } 
      } 
    } 
    if (b3 == 0 && b1 != 0) {
      this.maxOffset = -1;
      throw new IllegalArgumentException("Unmatched braces in the pattern.");
    } 
    this.pattern = arrayOfStringBuilder[0].toString();
  }
  
  public String toPattern() {
    int i = 0;
    StringBuilder stringBuilder = new StringBuilder();
    for (byte b = 0; b <= this.maxOffset; b++) {
      copyAndFixQuotes(this.pattern, i, this.offsets[b], stringBuilder);
      i = this.offsets[b];
      stringBuilder.append('{').append(this.argumentNumbers[b]);
      Format format = this.formats[b];
      if (format != null)
        if (format instanceof NumberFormat) {
          if (format.equals(NumberFormat.getInstance(this.locale))) {
            stringBuilder.append(",number");
          } else if (format.equals(NumberFormat.getCurrencyInstance(this.locale))) {
            stringBuilder.append(",number,currency");
          } else if (format.equals(NumberFormat.getPercentInstance(this.locale))) {
            stringBuilder.append(",number,percent");
          } else if (format.equals(NumberFormat.getIntegerInstance(this.locale))) {
            stringBuilder.append(",number,integer");
          } else if (format instanceof DecimalFormat) {
            stringBuilder.append(",number,").append(((DecimalFormat)format).toPattern());
          } else if (format instanceof ChoiceFormat) {
            stringBuilder.append(",choice,").append(((ChoiceFormat)format).toPattern());
          } 
        } else if (format instanceof DateFormat) {
          byte b1;
          for (b1 = 0; b1 < DATE_TIME_MODIFIERS.length; b1++) {
            DateFormat dateFormat = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[b1], this.locale);
            if (format.equals(dateFormat)) {
              stringBuilder.append(",date");
              break;
            } 
            dateFormat = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[b1], this.locale);
            if (format.equals(dateFormat)) {
              stringBuilder.append(",time");
              break;
            } 
          } 
          if (b1 >= DATE_TIME_MODIFIERS.length) {
            if (format instanceof SimpleDateFormat)
              stringBuilder.append(",date,").append(((SimpleDateFormat)format).toPattern()); 
          } else if (b1 != 0) {
            stringBuilder.append(',').append(DATE_TIME_MODIFIER_KEYWORDS[b1]);
          } 
        }  
      stringBuilder.append('}');
    } 
    copyAndFixQuotes(this.pattern, i, this.pattern.length(), stringBuilder);
    return stringBuilder.toString();
  }
  
  public void setFormatsByArgumentIndex(Format[] paramArrayOfFormat) {
    for (byte b = 0; b <= this.maxOffset; b++) {
      int i = this.argumentNumbers[b];
      if (i < paramArrayOfFormat.length)
        this.formats[b] = paramArrayOfFormat[i]; 
    } 
  }
  
  public void setFormats(Format[] paramArrayOfFormat) {
    int i = paramArrayOfFormat.length;
    if (i > this.maxOffset + 1)
      i = this.maxOffset + 1; 
    for (byte b = 0; b < i; b++)
      this.formats[b] = paramArrayOfFormat[b]; 
  }
  
  public void setFormatByArgumentIndex(int paramInt, Format paramFormat) {
    for (byte b = 0; b <= this.maxOffset; b++) {
      if (this.argumentNumbers[b] == paramInt)
        this.formats[b] = paramFormat; 
    } 
  }
  
  public void setFormat(int paramInt, Format paramFormat) { this.formats[paramInt] = paramFormat; }
  
  public Format[] getFormatsByArgumentIndex() {
    int i = -1;
    for (byte b1 = 0; b1 <= this.maxOffset; b1++) {
      if (this.argumentNumbers[b1] > i)
        i = this.argumentNumbers[b1]; 
    } 
    Format[] arrayOfFormat = new Format[i + 1];
    for (byte b2 = 0; b2 <= this.maxOffset; b2++)
      arrayOfFormat[this.argumentNumbers[b2]] = this.formats[b2]; 
    return arrayOfFormat;
  }
  
  public Format[] getFormats() {
    Format[] arrayOfFormat = new Format[this.maxOffset + 1];
    System.arraycopy(this.formats, 0, arrayOfFormat, 0, this.maxOffset + 1);
    return arrayOfFormat;
  }
  
  public final StringBuffer format(Object[] paramArrayOfObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) { return subformat(paramArrayOfObject, paramStringBuffer, paramFieldPosition, null); }
  
  public static String format(String paramString, Object... paramVarArgs) {
    MessageFormat messageFormat = new MessageFormat(paramString);
    return messageFormat.format(paramVarArgs);
  }
  
  public final StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) { return subformat((Object[])paramObject, paramStringBuffer, paramFieldPosition, null); }
  
  public AttributedCharacterIterator formatToCharacterIterator(Object paramObject) {
    StringBuffer stringBuffer = new StringBuffer();
    ArrayList arrayList = new ArrayList();
    if (paramObject == null)
      throw new NullPointerException("formatToCharacterIterator must be passed non-null object"); 
    subformat((Object[])paramObject, stringBuffer, null, arrayList);
    return (arrayList.size() == 0) ? createAttributedCharacterIterator("") : createAttributedCharacterIterator((AttributedCharacterIterator[])arrayList.toArray(new AttributedCharacterIterator[arrayList.size()]));
  }
  
  public Object[] parse(String paramString, ParsePosition paramParsePosition) {
    if (paramString == null)
      return new Object[0]; 
    int i = -1;
    for (byte b = 0; b <= this.maxOffset; b++) {
      if (this.argumentNumbers[b] > i)
        i = this.argumentNumbers[b]; 
    } 
    Object[] arrayOfObject = new Object[i + 1];
    int j = 0;
    int k = paramParsePosition.index;
    ParsePosition parsePosition = new ParsePosition(0);
    int m;
    for (m = 0; m <= this.maxOffset; m++) {
      int n = this.offsets[m] - j;
      if (n == 0 || this.pattern.regionMatches(j, paramString, k, n)) {
        k += n;
        j += n;
      } else {
        paramParsePosition.errorIndex = k;
        return null;
      } 
      if (this.formats[m] == null) {
        int i2;
        int i1 = (m != this.maxOffset) ? this.offsets[m + true] : this.pattern.length();
        if (j >= i1) {
          i2 = paramString.length();
        } else {
          i2 = paramString.indexOf(this.pattern.substring(j, i1), k);
        } 
        if (i2 < 0) {
          paramParsePosition.errorIndex = k;
          return null;
        } 
        String str = paramString.substring(k, i2);
        if (!str.equals("{" + this.argumentNumbers[m] + "}"))
          arrayOfObject[this.argumentNumbers[m]] = paramString.substring(k, i2); 
        k = i2;
      } else {
        parsePosition.index = k;
        arrayOfObject[this.argumentNumbers[m]] = this.formats[m].parseObject(paramString, parsePosition);
        if (parsePosition.index == k) {
          paramParsePosition.errorIndex = k;
          return null;
        } 
        k = parsePosition.index;
      } 
    } 
    m = this.pattern.length() - j;
    if (m == 0 || this.pattern.regionMatches(j, paramString, k, m)) {
      paramParsePosition.index = k + m;
    } else {
      paramParsePosition.errorIndex = k;
      return null;
    } 
    return arrayOfObject;
  }
  
  public Object[] parse(String paramString) throws ParseException {
    ParsePosition parsePosition = new ParsePosition(0);
    Object[] arrayOfObject = parse(paramString, parsePosition);
    if (parsePosition.index == 0)
      throw new ParseException("MessageFormat parse error!", parsePosition.errorIndex); 
    return arrayOfObject;
  }
  
  public Object parseObject(String paramString, ParsePosition paramParsePosition) { return parse(paramString, paramParsePosition); }
  
  public Object clone() {
    MessageFormat messageFormat = (MessageFormat)super.clone();
    messageFormat.formats = (Format[])this.formats.clone();
    for (byte b = 0; b < this.formats.length; b++) {
      if (this.formats[b] != null)
        messageFormat.formats[b] = (Format)this.formats[b].clone(); 
    } 
    messageFormat.offsets = (int[])this.offsets.clone();
    messageFormat.argumentNumbers = (int[])this.argumentNumbers.clone();
    return messageFormat;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    MessageFormat messageFormat = (MessageFormat)paramObject;
    return (this.maxOffset == messageFormat.maxOffset && this.pattern.equals(messageFormat.pattern) && ((this.locale != null && this.locale.equals(messageFormat.locale)) || (this.locale == null && messageFormat.locale == null)) && Arrays.equals(this.offsets, messageFormat.offsets) && Arrays.equals(this.argumentNumbers, messageFormat.argumentNumbers) && Arrays.equals(this.formats, messageFormat.formats));
  }
  
  public int hashCode() { return this.pattern.hashCode(); }
  
  private StringBuffer subformat(Object[] paramArrayOfObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition, List<AttributedCharacterIterator> paramList) {
    int i = 0;
    int j = paramStringBuffer.length();
    for (byte b = 0; b <= this.maxOffset; b++) {
      paramStringBuffer.append(this.pattern.substring(i, this.offsets[b]));
      i = this.offsets[b];
      int k = this.argumentNumbers[b];
      if (paramArrayOfObject == null || k >= paramArrayOfObject.length) {
        paramStringBuffer.append('{').append(k).append('}');
      } else {
        Object[] arrayOfObject = paramArrayOfObject[k];
        String str = null;
        Format format = null;
        if (arrayOfObject == null) {
          str = "null";
        } else if (this.formats[b] != null) {
          format = this.formats[b];
          if (format instanceof ChoiceFormat) {
            str = this.formats[b].format(arrayOfObject);
            if (str.indexOf('{') >= 0) {
              format = new MessageFormat(str, this.locale);
              arrayOfObject = paramArrayOfObject;
              str = null;
            } 
          } 
        } else if (arrayOfObject instanceof Number) {
          format = NumberFormat.getInstance(this.locale);
        } else if (arrayOfObject instanceof java.util.Date) {
          format = DateFormat.getDateTimeInstance(3, 3, this.locale);
        } else if (arrayOfObject instanceof String) {
          str = (String)arrayOfObject;
        } else {
          str = arrayOfObject.toString();
          if (str == null)
            str = "null"; 
        } 
        if (paramList != null) {
          if (j != paramStringBuffer.length()) {
            paramList.add(createAttributedCharacterIterator(paramStringBuffer.substring(j)));
            j = paramStringBuffer.length();
          } 
          if (format != null) {
            AttributedCharacterIterator attributedCharacterIterator = format.formatToCharacterIterator(arrayOfObject);
            append(paramStringBuffer, attributedCharacterIterator);
            if (j != paramStringBuffer.length()) {
              paramList.add(createAttributedCharacterIterator(attributedCharacterIterator, Field.ARGUMENT, Integer.valueOf(k)));
              j = paramStringBuffer.length();
            } 
            str = null;
          } 
          if (str != null && str.length() > 0) {
            paramStringBuffer.append(str);
            paramList.add(createAttributedCharacterIterator(str, Field.ARGUMENT, Integer.valueOf(k)));
            j = paramStringBuffer.length();
          } 
        } else {
          if (format != null)
            str = format.format(arrayOfObject); 
          j = paramStringBuffer.length();
          paramStringBuffer.append(str);
          if (!b && paramFieldPosition != null && Field.ARGUMENT.equals(paramFieldPosition.getFieldAttribute())) {
            paramFieldPosition.setBeginIndex(j);
            paramFieldPosition.setEndIndex(paramStringBuffer.length());
          } 
          j = paramStringBuffer.length();
        } 
      } 
    } 
    paramStringBuffer.append(this.pattern.substring(i, this.pattern.length()));
    if (paramList != null && j != paramStringBuffer.length())
      paramList.add(createAttributedCharacterIterator(paramStringBuffer.substring(j))); 
    return paramStringBuffer;
  }
  
  private void append(StringBuffer paramStringBuffer, CharacterIterator paramCharacterIterator) {
    if (paramCharacterIterator.first() != Character.MAX_VALUE) {
      paramStringBuffer.append(paramCharacterIterator.first());
      char c;
      while ((c = paramCharacterIterator.next()) != Character.MAX_VALUE)
        paramStringBuffer.append(c); 
    } 
  }
  
  private void makeFormat(int paramInt1, int paramInt2, StringBuilder[] paramArrayOfStringBuilder) {
    String[] arrayOfString = new String[paramArrayOfStringBuilder.length];
    int i;
    for (i = 0; i < paramArrayOfStringBuilder.length; i++) {
      StringBuilder stringBuilder = paramArrayOfStringBuilder[i];
      arrayOfString[i] = (stringBuilder != null) ? stringBuilder.toString() : "";
    } 
    try {
      i = Integer.parseInt(arrayOfString[1]);
    } catch (NumberFormatException numberFormatException) {
      throw new IllegalArgumentException("can't parse argument number: " + arrayOfString[1], numberFormatException);
    } 
    if (i < 0)
      throw new IllegalArgumentException("negative argument number: " + i); 
    if (paramInt2 >= this.formats.length) {
      int k = this.formats.length * 2;
      Format[] arrayOfFormat = new Format[k];
      int[] arrayOfInt1 = new int[k];
      int[] arrayOfInt2 = new int[k];
      System.arraycopy(this.formats, 0, arrayOfFormat, 0, this.maxOffset + 1);
      System.arraycopy(this.offsets, 0, arrayOfInt1, 0, this.maxOffset + 1);
      System.arraycopy(this.argumentNumbers, 0, arrayOfInt2, 0, this.maxOffset + 1);
      this.formats = arrayOfFormat;
      this.offsets = arrayOfInt1;
      this.argumentNumbers = arrayOfInt2;
    } 
    int j = this.maxOffset;
    this.maxOffset = paramInt2;
    this.offsets[paramInt2] = arrayOfString[0].length();
    this.argumentNumbers[paramInt2] = i;
    NumberFormat numberFormat = null;
    if (arrayOfString[2].length() != 0) {
      int m;
      int k = findKeyword(arrayOfString[2], TYPE_KEYWORDS);
      switch (k) {
        case 0:
          break;
        case 1:
          switch (findKeyword(arrayOfString[3], NUMBER_MODIFIER_KEYWORDS)) {
            case 0:
              numberFormat = NumberFormat.getInstance(this.locale);
              break;
            case 1:
              numberFormat = NumberFormat.getCurrencyInstance(this.locale);
              break;
            case 2:
              numberFormat = NumberFormat.getPercentInstance(this.locale);
              break;
            case 3:
              numberFormat = NumberFormat.getIntegerInstance(this.locale);
              break;
          } 
          try {
            numberFormat = new DecimalFormat(arrayOfString[3], DecimalFormatSymbols.getInstance(this.locale));
          } catch (IllegalArgumentException illegalArgumentException) {
            this.maxOffset = j;
            throw illegalArgumentException;
          } 
          break;
        case 2:
        case 3:
          m = findKeyword(arrayOfString[3], DATE_TIME_MODIFIER_KEYWORDS);
          if (m >= 0 && m < DATE_TIME_MODIFIER_KEYWORDS.length) {
            if (k == 2) {
              DateFormat dateFormat1 = DateFormat.getDateInstance(DATE_TIME_MODIFIERS[m], this.locale);
              break;
            } 
            DateFormat dateFormat = DateFormat.getTimeInstance(DATE_TIME_MODIFIERS[m], this.locale);
            break;
          } 
          try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(arrayOfString[3], this.locale);
          } catch (IllegalArgumentException illegalArgumentException) {
            this.maxOffset = j;
            throw illegalArgumentException;
          } 
          break;
        case 4:
          try {
            numberFormat = new ChoiceFormat(arrayOfString[3]);
          } catch (Exception exception) {
            this.maxOffset = j;
            throw new IllegalArgumentException("Choice Pattern incorrect: " + arrayOfString[3], exception);
          } 
          break;
        default:
          this.maxOffset = j;
          throw new IllegalArgumentException("unknown format type: " + arrayOfString[2]);
      } 
    } 
    this.formats[paramInt2] = numberFormat;
  }
  
  private static final int findKeyword(String paramString, String[] paramArrayOfString) {
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      if (paramString.equals(paramArrayOfString[b]))
        return b; 
    } 
    String str = paramString.trim().toLowerCase(Locale.ROOT);
    if (str != paramString)
      for (byte b1 = 0; b1 < paramArrayOfString.length; b1++) {
        if (str.equals(paramArrayOfString[b1]))
          return b1; 
      }  
    return -1;
  }
  
  private static final void copyAndFixQuotes(String paramString, int paramInt1, int paramInt2, StringBuilder paramStringBuilder) {
    boolean bool = false;
    for (int i = paramInt1; i < paramInt2; i++) {
      char c = paramString.charAt(i);
      if (c == '{') {
        if (!bool) {
          paramStringBuilder.append('\'');
          bool = true;
        } 
        paramStringBuilder.append(c);
      } else if (c == '\'') {
        paramStringBuilder.append("''");
      } else {
        if (bool) {
          paramStringBuilder.append('\'');
          bool = false;
        } 
        paramStringBuilder.append(c);
      } 
    } 
    if (bool)
      paramStringBuilder.append('\''); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    boolean bool = (this.maxOffset >= -1 && this.formats.length > this.maxOffset && this.offsets.length > this.maxOffset && this.argumentNumbers.length > this.maxOffset) ? 1 : 0;
    if (bool) {
      int i = this.pattern.length() + 1;
      for (int j = this.maxOffset; j >= 0; j--) {
        if (this.offsets[j] < 0 || this.offsets[j] > i) {
          bool = false;
          break;
        } 
        i = this.offsets[j];
      } 
    } 
    if (!bool)
      throw new InvalidObjectException("Could not reconstruct MessageFormat from corrupt stream."); 
  }
  
  public static class Field extends Format.Field {
    private static final long serialVersionUID = 7899943957617360810L;
    
    public static final Field ARGUMENT = new Field("message argument field");
    
    protected Field(String param1String) { super(param1String); }
    
    protected Object readResolve() {
      if (getClass() != Field.class)
        throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
      return ARGUMENT;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\MessageFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.swing.text.rtf;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Segment;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabStop;

class RTFGenerator {
  Dictionary<Object, Integer> colorTable = new Hashtable();
  
  int colorCount;
  
  Dictionary<String, Integer> fontTable;
  
  int fontCount;
  
  Dictionary<AttributeSet, Integer> styleTable;
  
  int styleCount;
  
  OutputStream outputStream;
  
  boolean afterKeyword;
  
  MutableAttributeSet outputAttributes;
  
  int unicodeCount;
  
  private Segment workingSegment;
  
  int[] outputConversion;
  
  public static final Color defaultRTFColor = Color.black;
  
  public static final float defaultFontSize = 12.0F;
  
  public static final String defaultFontFamily = "Helvetica";
  
  private static final Object MagicToken = new Object();
  
  protected static CharacterKeywordPair[] textKeywords;
  
  static final char[] hexdigits;
  
  public static void writeDocument(Document paramDocument, OutputStream paramOutputStream) throws IOException {
    RTFGenerator rTFGenerator = new RTFGenerator(paramOutputStream);
    Element element = paramDocument.getDefaultRootElement();
    rTFGenerator.examineElement(element);
    rTFGenerator.writeRTFHeader();
    rTFGenerator.writeDocumentProperties(paramDocument);
    int i = element.getElementCount();
    for (byte b = 0; b < i; b++)
      rTFGenerator.writeParagraphElement(element.getElement(b)); 
    rTFGenerator.writeRTFTrailer();
  }
  
  public RTFGenerator(OutputStream paramOutputStream) {
    this.colorTable.put(defaultRTFColor, Integer.valueOf(0));
    this.colorCount = 1;
    this.fontTable = new Hashtable();
    this.fontCount = 0;
    this.styleTable = new Hashtable();
    this.styleCount = 0;
    this.workingSegment = new Segment();
    this.outputStream = paramOutputStream;
    this.unicodeCount = 1;
  }
  
  public void examineElement(Element paramElement) {
    AttributeSet attributeSet = paramElement.getAttributes();
    tallyStyles(attributeSet);
    if (attributeSet != null) {
      Color color = StyleConstants.getForeground(attributeSet);
      if (color != null && this.colorTable.get(color) == null) {
        this.colorTable.put(color, new Integer(this.colorCount));
        this.colorCount++;
      } 
      Object object = attributeSet.getAttribute(StyleConstants.Background);
      if (object != null && this.colorTable.get(object) == null) {
        this.colorTable.put(object, new Integer(this.colorCount));
        this.colorCount++;
      } 
      String str = StyleConstants.getFontFamily(attributeSet);
      if (str == null)
        str = "Helvetica"; 
      if (str != null && this.fontTable.get(str) == null) {
        this.fontTable.put(str, new Integer(this.fontCount));
        this.fontCount++;
      } 
    } 
    int i = paramElement.getElementCount();
    for (byte b = 0; b < i; b++)
      examineElement(paramElement.getElement(b)); 
  }
  
  private void tallyStyles(AttributeSet paramAttributeSet) {
    while (paramAttributeSet != null) {
      if (paramAttributeSet instanceof Style) {
        Integer integer = (Integer)this.styleTable.get(paramAttributeSet);
        if (integer == null) {
          this.styleCount++;
          integer = new Integer(this.styleCount);
          this.styleTable.put(paramAttributeSet, integer);
        } 
      } 
      paramAttributeSet = paramAttributeSet.getResolveParent();
    } 
  }
  
  private Style findStyle(AttributeSet paramAttributeSet) {
    while (paramAttributeSet != null) {
      if (paramAttributeSet instanceof Style) {
        Object object = this.styleTable.get(paramAttributeSet);
        if (object != null)
          return (Style)paramAttributeSet; 
      } 
      paramAttributeSet = paramAttributeSet.getResolveParent();
    } 
    return null;
  }
  
  private Integer findStyleNumber(AttributeSet paramAttributeSet, String paramString) {
    while (paramAttributeSet != null) {
      if (paramAttributeSet instanceof Style) {
        Integer integer = (Integer)this.styleTable.get(paramAttributeSet);
        if (integer != null && (paramString == null || paramString.equals(paramAttributeSet.getAttribute("style:type"))))
          return integer; 
      } 
      paramAttributeSet = paramAttributeSet.getResolveParent();
    } 
    return null;
  }
  
  private static Object attrDiff(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, Object paramObject1, Object paramObject2) {
    Object object1 = paramMutableAttributeSet.getAttribute(paramObject1);
    Object object2 = paramAttributeSet.getAttribute(paramObject1);
    if (object2 == object1)
      return null; 
    if (object2 == null) {
      paramMutableAttributeSet.removeAttribute(paramObject1);
      return (paramObject2 != null && !paramObject2.equals(object1)) ? paramObject2 : null;
    } 
    if (object1 == null || !equalArraysOK(object1, object2)) {
      paramMutableAttributeSet.addAttribute(paramObject1, object2);
      return object2;
    } 
    return null;
  }
  
  private static boolean equalArraysOK(Object paramObject1, Object paramObject2) {
    if (paramObject1 == paramObject2)
      return true; 
    if (paramObject1 == null || paramObject2 == null)
      return false; 
    if (paramObject1.equals(paramObject2))
      return true; 
    if (!paramObject1.getClass().isArray() || !paramObject2.getClass().isArray())
      return false; 
    Object[] arrayOfObject1 = (Object[])paramObject1;
    Object[] arrayOfObject2 = (Object[])paramObject2;
    if (arrayOfObject1.length != arrayOfObject2.length)
      return false; 
    int i = arrayOfObject1.length;
    for (byte b = 0; b < i; b++) {
      if (!equalArraysOK(arrayOfObject1[b], arrayOfObject2[b]))
        return false; 
    } 
    return true;
  }
  
  public void writeLineBreak() throws IOException {
    writeRawString("\n");
    this.afterKeyword = false;
  }
  
  public void writeRTFHeader() throws IOException {
    writeBegingroup();
    writeControlWord("rtf", 1);
    writeControlWord("ansi");
    this.outputConversion = outputConversionForName("ansi");
    writeLineBreak();
    String[] arrayOfString = new String[this.fontCount];
    Enumeration enumeration = this.fontTable.keys();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      Integer integer = (Integer)this.fontTable.get(str);
      arrayOfString[integer.intValue()] = str;
    } 
    writeBegingroup();
    writeControlWord("fonttbl");
    byte b;
    for (b = 0; b < this.fontCount; b++) {
      writeControlWord("f", b);
      writeControlWord("fnil");
      writeText(arrayOfString[b]);
      writeText(";");
    } 
    writeEndgroup();
    writeLineBreak();
    if (this.colorCount > 1) {
      Color[] arrayOfColor = new Color[this.colorCount];
      Enumeration enumeration1 = this.colorTable.keys();
      while (enumeration1.hasMoreElements()) {
        Color color = (Color)enumeration1.nextElement();
        Integer integer = (Integer)this.colorTable.get(color);
        arrayOfColor[integer.intValue()] = color;
      } 
      writeBegingroup();
      writeControlWord("colortbl");
      for (b = 0; b < this.colorCount; b++) {
        Color color = arrayOfColor[b];
        if (color != null) {
          writeControlWord("red", color.getRed());
          writeControlWord("green", color.getGreen());
          writeControlWord("blue", color.getBlue());
        } 
        writeRawString(";");
      } 
      writeEndgroup();
      writeLineBreak();
    } 
    if (this.styleCount > 1) {
      writeBegingroup();
      writeControlWord("stylesheet");
      Enumeration enumeration1 = this.styleTable.keys();
      while (enumeration1.hasMoreElements()) {
        SimpleAttributeSet simpleAttributeSet;
        Style style1 = (Style)enumeration1.nextElement();
        int i = ((Integer)this.styleTable.get(style1)).intValue();
        writeBegingroup();
        String str = (String)style1.getAttribute("style:type");
        if (str == null)
          str = "paragraph"; 
        if (str.equals("character")) {
          writeControlWord("*");
          writeControlWord("cs", i);
        } else if (str.equals("section")) {
          writeControlWord("*");
          writeControlWord("ds", i);
        } else {
          writeControlWord("s", i);
        } 
        AttributeSet attributeSet = style1.getResolveParent();
        if (attributeSet == null) {
          simpleAttributeSet = new SimpleAttributeSet();
        } else {
          simpleAttributeSet = new SimpleAttributeSet(attributeSet);
        } 
        updateSectionAttributes(simpleAttributeSet, style1, false);
        updateParagraphAttributes(simpleAttributeSet, style1, false);
        updateCharacterAttributes(simpleAttributeSet, style1, false);
        attributeSet = style1.getResolveParent();
        if (attributeSet != null && attributeSet instanceof Style) {
          Integer integer = (Integer)this.styleTable.get(attributeSet);
          if (integer != null)
            writeControlWord("sbasedon", integer.intValue()); 
        } 
        Style style2 = (Style)style1.getAttribute("style:nextStyle");
        if (style2 != null) {
          Integer integer = (Integer)this.styleTable.get(style2);
          if (integer != null)
            writeControlWord("snext", integer.intValue()); 
        } 
        Boolean bool1 = (Boolean)style1.getAttribute("style:hidden");
        if (bool1 != null && bool1.booleanValue())
          writeControlWord("shidden"); 
        Boolean bool2 = (Boolean)style1.getAttribute("style:additive");
        if (bool2 != null && bool2.booleanValue())
          writeControlWord("additive"); 
        writeText(style1.getName());
        writeText(";");
        writeEndgroup();
      } 
      writeEndgroup();
      writeLineBreak();
    } 
    this.outputAttributes = new SimpleAttributeSet();
  }
  
  void writeDocumentProperties(Document paramDocument) throws IOException {
    boolean bool = false;
    for (byte b = 0; b < RTFAttributes.attributes.length; b++) {
      RTFAttribute rTFAttribute = RTFAttributes.attributes[b];
      if (rTFAttribute.domain() == 3) {
        Object object = paramDocument.getProperty(rTFAttribute.swingName());
        boolean bool1 = rTFAttribute.writeValue(object, this, false);
        if (bool1)
          bool = true; 
      } 
    } 
    if (bool)
      writeLineBreak(); 
  }
  
  public void writeRTFTrailer() throws IOException {
    writeEndgroup();
    writeLineBreak();
  }
  
  protected void checkNumericControlWord(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, Object paramObject, String paramString, float paramFloat1, float paramFloat2) throws IOException {
    Object object;
    if ((object = attrDiff(paramMutableAttributeSet, paramAttributeSet, paramObject, MagicToken)) != null) {
      float f;
      if (object == MagicToken) {
        f = paramFloat1;
      } else {
        f = ((Number)object).floatValue();
      } 
      writeControlWord(paramString, Math.round(f * paramFloat2));
    } 
  }
  
  protected void checkControlWord(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, RTFAttribute paramRTFAttribute) throws IOException {
    Object object;
    if ((object = attrDiff(paramMutableAttributeSet, paramAttributeSet, paramRTFAttribute.swingName(), MagicToken)) != null) {
      if (object == MagicToken)
        object = null; 
      paramRTFAttribute.writeValue(object, this, true);
    } 
  }
  
  protected void checkControlWords(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, RTFAttribute[] paramArrayOfRTFAttribute, int paramInt) throws IOException {
    int i = paramArrayOfRTFAttribute.length;
    for (byte b = 0; b < i; b++) {
      RTFAttribute rTFAttribute = paramArrayOfRTFAttribute[b];
      if (rTFAttribute.domain() == paramInt)
        checkControlWord(paramMutableAttributeSet, paramAttributeSet, rTFAttribute); 
    } 
  }
  
  void updateSectionAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      Object object = paramMutableAttributeSet.getAttribute("sectionStyle");
      Integer integer = findStyleNumber(paramAttributeSet, "section");
      if (object != integer) {
        if (object != null)
          resetSectionAttributes(paramMutableAttributeSet); 
        if (integer != null) {
          writeControlWord("ds", ((Integer)integer).intValue());
          paramMutableAttributeSet.addAttribute("sectionStyle", integer);
        } else {
          paramMutableAttributeSet.removeAttribute("sectionStyle");
        } 
      } 
    } 
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 2);
  }
  
  protected void resetSectionAttributes(MutableAttributeSet paramMutableAttributeSet) throws IOException {
    writeControlWord("sectd");
    int i = RTFAttributes.attributes.length;
    for (byte b = 0; b < i; b++) {
      RTFAttribute rTFAttribute = RTFAttributes.attributes[b];
      if (rTFAttribute.domain() == 2)
        rTFAttribute.setDefault(paramMutableAttributeSet); 
    } 
    paramMutableAttributeSet.removeAttribute("sectionStyle");
  }
  
  void updateParagraphAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean) throws IOException {
    Object object2;
    Object object1;
    if (paramBoolean) {
      object1 = paramMutableAttributeSet.getAttribute("paragraphStyle");
      object2 = findStyleNumber(paramAttributeSet, "paragraph");
      if (object1 != object2 && object1 != null) {
        resetParagraphAttributes(paramMutableAttributeSet);
        object1 = null;
      } 
    } else {
      object1 = null;
      object2 = null;
    } 
    Object object3 = paramMutableAttributeSet.getAttribute("tabs");
    Object object4 = paramAttributeSet.getAttribute("tabs");
    if (object3 != object4 && object3 != null) {
      resetParagraphAttributes(paramMutableAttributeSet);
      object3 = null;
      object1 = null;
    } 
    if (object1 != object2 && object2 != null) {
      writeControlWord("s", ((Integer)object2).intValue());
      paramMutableAttributeSet.addAttribute("paragraphStyle", object2);
    } 
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 1);
    if (object3 != object4 && object4 != null) {
      TabStop[] arrayOfTabStop = (TabStop[])object4;
      for (byte b = 0; b < arrayOfTabStop.length; b++) {
        TabStop tabStop = arrayOfTabStop[b];
        switch (tabStop.getAlignment()) {
          case 1:
            writeControlWord("tqr");
            break;
          case 2:
            writeControlWord("tqc");
            break;
          case 4:
            writeControlWord("tqdec");
            break;
        } 
        switch (tabStop.getLeader()) {
          case 1:
            writeControlWord("tldot");
            break;
          case 2:
            writeControlWord("tlhyph");
            break;
          case 3:
            writeControlWord("tlul");
            break;
          case 4:
            writeControlWord("tlth");
            break;
          case 5:
            writeControlWord("tleq");
            break;
        } 
        int i = Math.round(20.0F * tabStop.getPosition());
        if (tabStop.getAlignment() == 5) {
          writeControlWord("tb", i);
        } else {
          writeControlWord("tx", i);
        } 
      } 
      paramMutableAttributeSet.addAttribute("tabs", arrayOfTabStop);
    } 
  }
  
  public void writeParagraphElement(Element paramElement) {
    updateParagraphAttributes(this.outputAttributes, paramElement.getAttributes(), true);
    int i = paramElement.getElementCount();
    for (byte b = 0; b < i; b++)
      writeTextElement(paramElement.getElement(b)); 
    writeControlWord("par");
    writeLineBreak();
  }
  
  protected void resetParagraphAttributes(MutableAttributeSet paramMutableAttributeSet) throws IOException {
    writeControlWord("pard");
    paramMutableAttributeSet.addAttribute(StyleConstants.Alignment, Integer.valueOf(0));
    int i = RTFAttributes.attributes.length;
    for (byte b = 0; b < i; b++) {
      RTFAttribute rTFAttribute = RTFAttributes.attributes[b];
      if (rTFAttribute.domain() == 1)
        rTFAttribute.setDefault(paramMutableAttributeSet); 
    } 
    paramMutableAttributeSet.removeAttribute("paragraphStyle");
    paramMutableAttributeSet.removeAttribute("tabs");
  }
  
  void updateCharacterAttributes(MutableAttributeSet paramMutableAttributeSet, AttributeSet paramAttributeSet, boolean paramBoolean) throws IOException {
    if (paramBoolean) {
      Object object1 = paramMutableAttributeSet.getAttribute("characterStyle");
      Integer integer = findStyleNumber(paramAttributeSet, "character");
      if (object1 != integer) {
        if (object1 != null)
          resetCharacterAttributes(paramMutableAttributeSet); 
        if (integer != null) {
          writeControlWord("cs", ((Integer)integer).intValue());
          paramMutableAttributeSet.addAttribute("characterStyle", integer);
        } else {
          paramMutableAttributeSet.removeAttribute("characterStyle");
        } 
      } 
    } 
    Object object;
    if ((object = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.FontFamily, null)) != null) {
      Integer integer = (Integer)this.fontTable.get(object);
      writeControlWord("f", integer.intValue());
    } 
    checkNumericControlWord(paramMutableAttributeSet, paramAttributeSet, StyleConstants.FontSize, "fs", 12.0F, 2.0F);
    checkControlWords(paramMutableAttributeSet, paramAttributeSet, RTFAttributes.attributes, 0);
    checkNumericControlWord(paramMutableAttributeSet, paramAttributeSet, StyleConstants.LineSpacing, "sl", 0.0F, 20.0F);
    if ((object = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.Background, MagicToken)) != null) {
      int i;
      if (object == MagicToken) {
        i = 0;
      } else {
        i = ((Integer)this.colorTable.get(object)).intValue();
      } 
      writeControlWord("cb", i);
    } 
    if ((object = attrDiff(paramMutableAttributeSet, paramAttributeSet, StyleConstants.Foreground, null)) != null) {
      int i;
      if (object == MagicToken) {
        i = 0;
      } else {
        i = ((Integer)this.colorTable.get(object)).intValue();
      } 
      writeControlWord("cf", i);
    } 
  }
  
  protected void resetCharacterAttributes(MutableAttributeSet paramMutableAttributeSet) throws IOException {
    writeControlWord("plain");
    int i = RTFAttributes.attributes.length;
    for (byte b = 0; b < i; b++) {
      RTFAttribute rTFAttribute = RTFAttributes.attributes[b];
      if (rTFAttribute.domain() == 0)
        rTFAttribute.setDefault(paramMutableAttributeSet); 
    } 
    StyleConstants.setFontFamily(paramMutableAttributeSet, "Helvetica");
    paramMutableAttributeSet.removeAttribute(StyleConstants.FontSize);
    paramMutableAttributeSet.removeAttribute(StyleConstants.Background);
    paramMutableAttributeSet.removeAttribute(StyleConstants.Foreground);
    paramMutableAttributeSet.removeAttribute(StyleConstants.LineSpacing);
    paramMutableAttributeSet.removeAttribute("characterStyle");
  }
  
  public void writeTextElement(Element paramElement) {
    updateCharacterAttributes(this.outputAttributes, paramElement.getAttributes(), true);
    if (paramElement.isLeaf()) {
      try {
        paramElement.getDocument().getText(paramElement.getStartOffset(), paramElement.getEndOffset() - paramElement.getStartOffset(), this.workingSegment);
      } catch (BadLocationException badLocationException) {
        badLocationException.printStackTrace();
        throw new InternalError(badLocationException.getMessage());
      } 
      writeText(this.workingSegment);
    } else {
      int i = paramElement.getElementCount();
      for (byte b = 0; b < i; b++)
        writeTextElement(paramElement.getElement(b)); 
    } 
  }
  
  public void writeText(Segment paramSegment) throws IOException {
    int i = paramSegment.offset;
    int j = i + paramSegment.count;
    char[] arrayOfChar = paramSegment.array;
    while (i < j) {
      writeCharacter(arrayOfChar[i]);
      i++;
    } 
  }
  
  public void writeText(String paramString) throws IOException {
    byte b = 0;
    int i = paramString.length();
    while (b < i) {
      writeCharacter(paramString.charAt(b));
      b++;
    } 
  }
  
  public void writeRawString(String paramString) throws IOException {
    int i = paramString.length();
    for (byte b = 0; b < i; b++)
      this.outputStream.write(paramString.charAt(b)); 
  }
  
  public void writeControlWord(String paramString) throws IOException {
    this.outputStream.write(92);
    writeRawString(paramString);
    this.afterKeyword = true;
  }
  
  public void writeControlWord(String paramString, int paramInt) throws IOException {
    this.outputStream.write(92);
    writeRawString(paramString);
    writeRawString(String.valueOf(paramInt));
    this.afterKeyword = true;
  }
  
  public void writeBegingroup() throws IOException {
    this.outputStream.write(123);
    this.afterKeyword = false;
  }
  
  public void writeEndgroup() throws IOException {
    this.outputStream.write(125);
    this.afterKeyword = false;
  }
  
  public void writeCharacter(char paramChar) throws IOException {
    if (paramChar == ' ') {
      this.outputStream.write(92);
      this.outputStream.write(126);
      this.afterKeyword = false;
      return;
    } 
    if (paramChar == '\t') {
      writeControlWord("tab");
      return;
    } 
    if (paramChar == '\n' || paramChar == '\r')
      return; 
    int i = convertCharacter(this.outputConversion, paramChar);
    if (i == 0) {
      for (byte b = 0; b < textKeywords.length; b++) {
        if ((textKeywords[b]).character == paramChar) {
          writeControlWord((textKeywords[b]).keyword);
          return;
        } 
      } 
      String str = approximationForUnicode(paramChar);
      if (str.length() != this.unicodeCount) {
        this.unicodeCount = str.length();
        writeControlWord("uc", this.unicodeCount);
      } 
      writeControlWord("u", paramChar);
      writeRawString(" ");
      writeRawString(str);
      this.afterKeyword = false;
      return;
    } 
    if (i > 127) {
      this.outputStream.write(92);
      this.outputStream.write(39);
      int j = (i & 0xF0) >>> 4;
      this.outputStream.write(hexdigits[j]);
      j = i & 0xF;
      this.outputStream.write(hexdigits[j]);
      this.afterKeyword = false;
      return;
    } 
    switch (i) {
      case 92:
      case 123:
      case 125:
        this.outputStream.write(92);
        this.afterKeyword = false;
        break;
    } 
    if (this.afterKeyword) {
      this.outputStream.write(32);
      this.afterKeyword = false;
    } 
    this.outputStream.write(i);
  }
  
  String approximationForUnicode(char paramChar) { return "?"; }
  
  static int[] outputConversionFromTranslationTable(char[] paramArrayOfChar) {
    int[] arrayOfInt = new int[2 * paramArrayOfChar.length];
    for (byte b = 0; b < paramArrayOfChar.length; b++) {
      arrayOfInt[b * 2] = paramArrayOfChar[b];
      arrayOfInt[b * 2 + 1] = b;
    } 
    return arrayOfInt;
  }
  
  static int[] outputConversionForName(String paramString) throws IOException {
    char[] arrayOfChar = (char[])RTFReader.getCharacterSet(paramString);
    return outputConversionFromTranslationTable(arrayOfChar);
  }
  
  protected static int convertCharacter(int[] paramArrayOfInt, char paramChar) {
    for (boolean bool = false; bool < paramArrayOfInt.length; bool += true) {
      if (paramArrayOfInt[bool] == paramChar)
        return paramArrayOfInt[bool + true]; 
    } 
    return 0;
  }
  
  static  {
    Dictionary dictionary = RTFReader.textKeywords;
    Enumeration enumeration = dictionary.keys();
    Vector vector = new Vector();
    while (enumeration.hasMoreElements()) {
      CharacterKeywordPair characterKeywordPair = new CharacterKeywordPair();
      characterKeywordPair.keyword = (String)enumeration.nextElement();
      characterKeywordPair.character = ((String)dictionary.get(characterKeywordPair.keyword)).charAt(0);
      vector.addElement(characterKeywordPair);
    } 
    textKeywords = new CharacterKeywordPair[vector.size()];
    vector.copyInto(textKeywords);
    hexdigits = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f' };
  }
  
  static class CharacterKeywordPair {
    public char character;
    
    public String keyword;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\rtf\RTFGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package java.awt;

import java.util.Locale;

public final class PageAttributes implements Cloneable {
  private ColorType color;
  
  private MediaType media;
  
  private OrientationRequestedType orientationRequested;
  
  private OriginType origin;
  
  private PrintQualityType printQuality;
  
  private int[] printerResolution;
  
  public PageAttributes() {
    setColor(ColorType.MONOCHROME);
    setMediaToDefault();
    setOrientationRequestedToDefault();
    setOrigin(OriginType.PHYSICAL);
    setPrintQualityToDefault();
    setPrinterResolutionToDefault();
  }
  
  public PageAttributes(PageAttributes paramPageAttributes) { set(paramPageAttributes); }
  
  public PageAttributes(ColorType paramColorType, MediaType paramMediaType, OrientationRequestedType paramOrientationRequestedType, OriginType paramOriginType, PrintQualityType paramPrintQualityType, int[] paramArrayOfInt) {
    setColor(paramColorType);
    setMedia(paramMediaType);
    setOrientationRequested(paramOrientationRequestedType);
    setOrigin(paramOriginType);
    setPrintQuality(paramPrintQualityType);
    setPrinterResolution(paramArrayOfInt);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public void set(PageAttributes paramPageAttributes) {
    this.color = paramPageAttributes.color;
    this.media = paramPageAttributes.media;
    this.orientationRequested = paramPageAttributes.orientationRequested;
    this.origin = paramPageAttributes.origin;
    this.printQuality = paramPageAttributes.printQuality;
    this.printerResolution = paramPageAttributes.printerResolution;
  }
  
  public ColorType getColor() { return this.color; }
  
  public void setColor(ColorType paramColorType) {
    if (paramColorType == null)
      throw new IllegalArgumentException("Invalid value for attribute color"); 
    this.color = paramColorType;
  }
  
  public MediaType getMedia() { return this.media; }
  
  public void setMedia(MediaType paramMediaType) {
    if (paramMediaType == null)
      throw new IllegalArgumentException("Invalid value for attribute media"); 
    this.media = paramMediaType;
  }
  
  public void setMediaToDefault() {
    String str = Locale.getDefault().getCountry();
    if (str != null && (str.equals(Locale.US.getCountry()) || str.equals(Locale.CANADA.getCountry()))) {
      setMedia(MediaType.NA_LETTER);
    } else {
      setMedia(MediaType.ISO_A4);
    } 
  }
  
  public OrientationRequestedType getOrientationRequested() { return this.orientationRequested; }
  
  public void setOrientationRequested(OrientationRequestedType paramOrientationRequestedType) {
    if (paramOrientationRequestedType == null)
      throw new IllegalArgumentException("Invalid value for attribute orientationRequested"); 
    this.orientationRequested = paramOrientationRequestedType;
  }
  
  public void setOrientationRequested(int paramInt) {
    switch (paramInt) {
      case 3:
        setOrientationRequested(OrientationRequestedType.PORTRAIT);
        return;
      case 4:
        setOrientationRequested(OrientationRequestedType.LANDSCAPE);
        return;
    } 
    setOrientationRequested(null);
  }
  
  public void setOrientationRequestedToDefault() { setOrientationRequested(OrientationRequestedType.PORTRAIT); }
  
  public OriginType getOrigin() { return this.origin; }
  
  public void setOrigin(OriginType paramOriginType) {
    if (paramOriginType == null)
      throw new IllegalArgumentException("Invalid value for attribute origin"); 
    this.origin = paramOriginType;
  }
  
  public PrintQualityType getPrintQuality() { return this.printQuality; }
  
  public void setPrintQuality(PrintQualityType paramPrintQualityType) {
    if (paramPrintQualityType == null)
      throw new IllegalArgumentException("Invalid value for attribute printQuality"); 
    this.printQuality = paramPrintQualityType;
  }
  
  public void setPrintQuality(int paramInt) {
    switch (paramInt) {
      case 3:
        setPrintQuality(PrintQualityType.DRAFT);
        return;
      case 4:
        setPrintQuality(PrintQualityType.NORMAL);
        return;
      case 5:
        setPrintQuality(PrintQualityType.HIGH);
        return;
    } 
    setPrintQuality(null);
  }
  
  public void setPrintQualityToDefault() { setPrintQuality(PrintQualityType.NORMAL); }
  
  public int[] getPrinterResolution() {
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = this.printerResolution[0];
    arrayOfInt[1] = this.printerResolution[1];
    arrayOfInt[2] = this.printerResolution[2];
    return arrayOfInt;
  }
  
  public void setPrinterResolution(int[] paramArrayOfInt) {
    if (paramArrayOfInt == null || paramArrayOfInt.length != 3 || paramArrayOfInt[0] <= 0 || paramArrayOfInt[1] <= 0 || (paramArrayOfInt[2] != 3 && paramArrayOfInt[2] != 4))
      throw new IllegalArgumentException("Invalid value for attribute printerResolution"); 
    int[] arrayOfInt = new int[3];
    arrayOfInt[0] = paramArrayOfInt[0];
    arrayOfInt[1] = paramArrayOfInt[1];
    arrayOfInt[2] = paramArrayOfInt[2];
    this.printerResolution = arrayOfInt;
  }
  
  public void setPrinterResolution(int paramInt) { setPrinterResolution(new int[] { paramInt, paramInt, 3 }); }
  
  public void setPrinterResolutionToDefault() { setPrinterResolution(72); }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PageAttributes))
      return false; 
    PageAttributes pageAttributes = (PageAttributes)paramObject;
    return (this.color == pageAttributes.color && this.media == pageAttributes.media && this.orientationRequested == pageAttributes.orientationRequested && this.origin == pageAttributes.origin && this.printQuality == pageAttributes.printQuality && this.printerResolution[0] == pageAttributes.printerResolution[0] && this.printerResolution[1] == pageAttributes.printerResolution[1] && this.printerResolution[2] == pageAttributes.printerResolution[2]);
  }
  
  public int hashCode() { return this.color.hashCode() << 31 ^ this.media.hashCode() << 24 ^ this.orientationRequested.hashCode() << 23 ^ this.origin.hashCode() << 22 ^ this.printQuality.hashCode() << 20 ^ this.printerResolution[2] >> 2 << 19 ^ this.printerResolution[1] << 10 ^ this.printerResolution[0]; }
  
  public String toString() { return "color=" + getColor() + ",media=" + getMedia() + ",orientation-requested=" + getOrientationRequested() + ",origin=" + getOrigin() + ",print-quality=" + getPrintQuality() + ",printer-resolution=[" + this.printerResolution[0] + "," + this.printerResolution[1] + "," + this.printerResolution[2] + "]"; }
  
  public static final class ColorType extends AttributeValue {
    private static final int I_COLOR = 0;
    
    private static final int I_MONOCHROME = 1;
    
    private static final String[] NAMES = { "color", "monochrome" };
    
    public static final ColorType COLOR = new ColorType(0);
    
    public static final ColorType MONOCHROME = new ColorType(1);
    
    private ColorType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class MediaType extends AttributeValue {
    private static final int I_ISO_4A0 = 0;
    
    private static final int I_ISO_2A0 = 1;
    
    private static final int I_ISO_A0 = 2;
    
    private static final int I_ISO_A1 = 3;
    
    private static final int I_ISO_A2 = 4;
    
    private static final int I_ISO_A3 = 5;
    
    private static final int I_ISO_A4 = 6;
    
    private static final int I_ISO_A5 = 7;
    
    private static final int I_ISO_A6 = 8;
    
    private static final int I_ISO_A7 = 9;
    
    private static final int I_ISO_A8 = 10;
    
    private static final int I_ISO_A9 = 11;
    
    private static final int I_ISO_A10 = 12;
    
    private static final int I_ISO_B0 = 13;
    
    private static final int I_ISO_B1 = 14;
    
    private static final int I_ISO_B2 = 15;
    
    private static final int I_ISO_B3 = 16;
    
    private static final int I_ISO_B4 = 17;
    
    private static final int I_ISO_B5 = 18;
    
    private static final int I_ISO_B6 = 19;
    
    private static final int I_ISO_B7 = 20;
    
    private static final int I_ISO_B8 = 21;
    
    private static final int I_ISO_B9 = 22;
    
    private static final int I_ISO_B10 = 23;
    
    private static final int I_JIS_B0 = 24;
    
    private static final int I_JIS_B1 = 25;
    
    private static final int I_JIS_B2 = 26;
    
    private static final int I_JIS_B3 = 27;
    
    private static final int I_JIS_B4 = 28;
    
    private static final int I_JIS_B5 = 29;
    
    private static final int I_JIS_B6 = 30;
    
    private static final int I_JIS_B7 = 31;
    
    private static final int I_JIS_B8 = 32;
    
    private static final int I_JIS_B9 = 33;
    
    private static final int I_JIS_B10 = 34;
    
    private static final int I_ISO_C0 = 35;
    
    private static final int I_ISO_C1 = 36;
    
    private static final int I_ISO_C2 = 37;
    
    private static final int I_ISO_C3 = 38;
    
    private static final int I_ISO_C4 = 39;
    
    private static final int I_ISO_C5 = 40;
    
    private static final int I_ISO_C6 = 41;
    
    private static final int I_ISO_C7 = 42;
    
    private static final int I_ISO_C8 = 43;
    
    private static final int I_ISO_C9 = 44;
    
    private static final int I_ISO_C10 = 45;
    
    private static final int I_ISO_DESIGNATED_LONG = 46;
    
    private static final int I_EXECUTIVE = 47;
    
    private static final int I_FOLIO = 48;
    
    private static final int I_INVOICE = 49;
    
    private static final int I_LEDGER = 50;
    
    private static final int I_NA_LETTER = 51;
    
    private static final int I_NA_LEGAL = 52;
    
    private static final int I_QUARTO = 53;
    
    private static final int I_A = 54;
    
    private static final int I_B = 55;
    
    private static final int I_C = 56;
    
    private static final int I_D = 57;
    
    private static final int I_E = 58;
    
    private static final int I_NA_10X15_ENVELOPE = 59;
    
    private static final int I_NA_10X14_ENVELOPE = 60;
    
    private static final int I_NA_10X13_ENVELOPE = 61;
    
    private static final int I_NA_9X12_ENVELOPE = 62;
    
    private static final int I_NA_9X11_ENVELOPE = 63;
    
    private static final int I_NA_7X9_ENVELOPE = 64;
    
    private static final int I_NA_6X9_ENVELOPE = 65;
    
    private static final int I_NA_NUMBER_9_ENVELOPE = 66;
    
    private static final int I_NA_NUMBER_10_ENVELOPE = 67;
    
    private static final int I_NA_NUMBER_11_ENVELOPE = 68;
    
    private static final int I_NA_NUMBER_12_ENVELOPE = 69;
    
    private static final int I_NA_NUMBER_14_ENVELOPE = 70;
    
    private static final int I_INVITE_ENVELOPE = 71;
    
    private static final int I_ITALY_ENVELOPE = 72;
    
    private static final int I_MONARCH_ENVELOPE = 73;
    
    private static final int I_PERSONAL_ENVELOPE = 74;
    
    private static final String[] NAMES = { 
        "iso-4a0", "iso-2a0", "iso-a0", "iso-a1", "iso-a2", "iso-a3", "iso-a4", "iso-a5", "iso-a6", "iso-a7", 
        "iso-a8", "iso-a9", "iso-a10", "iso-b0", "iso-b1", "iso-b2", "iso-b3", "iso-b4", "iso-b5", "iso-b6", 
        "iso-b7", "iso-b8", "iso-b9", "iso-b10", "jis-b0", "jis-b1", "jis-b2", "jis-b3", "jis-b4", "jis-b5", 
        "jis-b6", "jis-b7", "jis-b8", "jis-b9", "jis-b10", "iso-c0", "iso-c1", "iso-c2", "iso-c3", "iso-c4", 
        "iso-c5", "iso-c6", "iso-c7", "iso-c8", "iso-c9", "iso-c10", "iso-designated-long", "executive", "folio", "invoice", 
        "ledger", "na-letter", "na-legal", "quarto", "a", "b", "c", "d", "e", "na-10x15-envelope", 
        "na-10x14-envelope", "na-10x13-envelope", "na-9x12-envelope", "na-9x11-envelope", "na-7x9-envelope", "na-6x9-envelope", "na-number-9-envelope", "na-number-10-envelope", "na-number-11-envelope", "na-number-12-envelope", 
        "na-number-14-envelope", "invite-envelope", "italy-envelope", "monarch-envelope", "personal-envelope" };
    
    public static final MediaType ISO_4A0 = new MediaType(0);
    
    public static final MediaType ISO_2A0 = new MediaType(1);
    
    public static final MediaType ISO_A0 = new MediaType(2);
    
    public static final MediaType ISO_A1 = new MediaType(3);
    
    public static final MediaType ISO_A2 = new MediaType(4);
    
    public static final MediaType ISO_A3 = new MediaType(5);
    
    public static final MediaType ISO_A4 = new MediaType(6);
    
    public static final MediaType ISO_A5 = new MediaType(7);
    
    public static final MediaType ISO_A6 = new MediaType(8);
    
    public static final MediaType ISO_A7 = new MediaType(9);
    
    public static final MediaType ISO_A8 = new MediaType(10);
    
    public static final MediaType ISO_A9 = new MediaType(11);
    
    public static final MediaType ISO_A10 = new MediaType(12);
    
    public static final MediaType ISO_B0 = new MediaType(13);
    
    public static final MediaType ISO_B1 = new MediaType(14);
    
    public static final MediaType ISO_B2 = new MediaType(15);
    
    public static final MediaType ISO_B3 = new MediaType(16);
    
    public static final MediaType ISO_B4 = new MediaType(17);
    
    public static final MediaType ISO_B5 = new MediaType(18);
    
    public static final MediaType ISO_B6 = new MediaType(19);
    
    public static final MediaType ISO_B7 = new MediaType(20);
    
    public static final MediaType ISO_B8 = new MediaType(21);
    
    public static final MediaType ISO_B9 = new MediaType(22);
    
    public static final MediaType ISO_B10 = new MediaType(23);
    
    public static final MediaType JIS_B0 = new MediaType(24);
    
    public static final MediaType JIS_B1 = new MediaType(25);
    
    public static final MediaType JIS_B2 = new MediaType(26);
    
    public static final MediaType JIS_B3 = new MediaType(27);
    
    public static final MediaType JIS_B4 = new MediaType(28);
    
    public static final MediaType JIS_B5 = new MediaType(29);
    
    public static final MediaType JIS_B6 = new MediaType(30);
    
    public static final MediaType JIS_B7 = new MediaType(31);
    
    public static final MediaType JIS_B8 = new MediaType(32);
    
    public static final MediaType JIS_B9 = new MediaType(33);
    
    public static final MediaType JIS_B10 = new MediaType(34);
    
    public static final MediaType ISO_C0 = new MediaType(35);
    
    public static final MediaType ISO_C1 = new MediaType(36);
    
    public static final MediaType ISO_C2 = new MediaType(37);
    
    public static final MediaType ISO_C3 = new MediaType(38);
    
    public static final MediaType ISO_C4 = new MediaType(39);
    
    public static final MediaType ISO_C5 = new MediaType(40);
    
    public static final MediaType ISO_C6 = new MediaType(41);
    
    public static final MediaType ISO_C7 = new MediaType(42);
    
    public static final MediaType ISO_C8 = new MediaType(43);
    
    public static final MediaType ISO_C9 = new MediaType(44);
    
    public static final MediaType ISO_C10 = new MediaType(45);
    
    public static final MediaType ISO_DESIGNATED_LONG = new MediaType(46);
    
    public static final MediaType EXECUTIVE = new MediaType(47);
    
    public static final MediaType FOLIO = new MediaType(48);
    
    public static final MediaType INVOICE = new MediaType(49);
    
    public static final MediaType LEDGER = new MediaType(50);
    
    public static final MediaType NA_LETTER = new MediaType(51);
    
    public static final MediaType NA_LEGAL = new MediaType(52);
    
    public static final MediaType QUARTO = new MediaType(53);
    
    public static final MediaType A = new MediaType(54);
    
    public static final MediaType B = new MediaType(55);
    
    public static final MediaType C = new MediaType(56);
    
    public static final MediaType D = new MediaType(57);
    
    public static final MediaType E = new MediaType(58);
    
    public static final MediaType NA_10X15_ENVELOPE = new MediaType(59);
    
    public static final MediaType NA_10X14_ENVELOPE = new MediaType(60);
    
    public static final MediaType NA_10X13_ENVELOPE = new MediaType(61);
    
    public static final MediaType NA_9X12_ENVELOPE = new MediaType(62);
    
    public static final MediaType NA_9X11_ENVELOPE = new MediaType(63);
    
    public static final MediaType NA_7X9_ENVELOPE = new MediaType(64);
    
    public static final MediaType NA_6X9_ENVELOPE = new MediaType(65);
    
    public static final MediaType NA_NUMBER_9_ENVELOPE = new MediaType(66);
    
    public static final MediaType NA_NUMBER_10_ENVELOPE = new MediaType(67);
    
    public static final MediaType NA_NUMBER_11_ENVELOPE = new MediaType(68);
    
    public static final MediaType NA_NUMBER_12_ENVELOPE = new MediaType(69);
    
    public static final MediaType NA_NUMBER_14_ENVELOPE = new MediaType(70);
    
    public static final MediaType INVITE_ENVELOPE = new MediaType(71);
    
    public static final MediaType ITALY_ENVELOPE = new MediaType(72);
    
    public static final MediaType MONARCH_ENVELOPE = new MediaType(73);
    
    public static final MediaType PERSONAL_ENVELOPE = new MediaType(74);
    
    public static final MediaType A0 = ISO_A0;
    
    public static final MediaType A1 = ISO_A1;
    
    public static final MediaType A2 = ISO_A2;
    
    public static final MediaType A3 = ISO_A3;
    
    public static final MediaType A4 = ISO_A4;
    
    public static final MediaType A5 = ISO_A5;
    
    public static final MediaType A6 = ISO_A6;
    
    public static final MediaType A7 = ISO_A7;
    
    public static final MediaType A8 = ISO_A8;
    
    public static final MediaType A9 = ISO_A9;
    
    public static final MediaType A10 = ISO_A10;
    
    public static final MediaType B0 = ISO_B0;
    
    public static final MediaType B1 = ISO_B1;
    
    public static final MediaType B2 = ISO_B2;
    
    public static final MediaType B3 = ISO_B3;
    
    public static final MediaType B4 = ISO_B4;
    
    public static final MediaType ISO_B4_ENVELOPE = ISO_B4;
    
    public static final MediaType B5 = ISO_B5;
    
    public static final MediaType ISO_B5_ENVELOPE = ISO_B5;
    
    public static final MediaType B6 = ISO_B6;
    
    public static final MediaType B7 = ISO_B7;
    
    public static final MediaType B8 = ISO_B8;
    
    public static final MediaType B9 = ISO_B9;
    
    public static final MediaType B10 = ISO_B10;
    
    public static final MediaType C0 = ISO_C0;
    
    public static final MediaType ISO_C0_ENVELOPE = ISO_C0;
    
    public static final MediaType C1 = ISO_C1;
    
    public static final MediaType ISO_C1_ENVELOPE = ISO_C1;
    
    public static final MediaType C2 = ISO_C2;
    
    public static final MediaType ISO_C2_ENVELOPE = ISO_C2;
    
    public static final MediaType C3 = ISO_C3;
    
    public static final MediaType ISO_C3_ENVELOPE = ISO_C3;
    
    public static final MediaType C4 = ISO_C4;
    
    public static final MediaType ISO_C4_ENVELOPE = ISO_C4;
    
    public static final MediaType C5 = ISO_C5;
    
    public static final MediaType ISO_C5_ENVELOPE = ISO_C5;
    
    public static final MediaType C6 = ISO_C6;
    
    public static final MediaType ISO_C6_ENVELOPE = ISO_C6;
    
    public static final MediaType C7 = ISO_C7;
    
    public static final MediaType ISO_C7_ENVELOPE = ISO_C7;
    
    public static final MediaType C8 = ISO_C8;
    
    public static final MediaType ISO_C8_ENVELOPE = ISO_C8;
    
    public static final MediaType C9 = ISO_C9;
    
    public static final MediaType ISO_C9_ENVELOPE = ISO_C9;
    
    public static final MediaType C10 = ISO_C10;
    
    public static final MediaType ISO_C10_ENVELOPE = ISO_C10;
    
    public static final MediaType ISO_DESIGNATED_LONG_ENVELOPE = ISO_DESIGNATED_LONG;
    
    public static final MediaType STATEMENT = INVOICE;
    
    public static final MediaType TABLOID = LEDGER;
    
    public static final MediaType LETTER = NA_LETTER;
    
    public static final MediaType NOTE = NA_LETTER;
    
    public static final MediaType LEGAL = NA_LEGAL;
    
    public static final MediaType ENV_10X15 = NA_10X15_ENVELOPE;
    
    public static final MediaType ENV_10X14 = NA_10X14_ENVELOPE;
    
    public static final MediaType ENV_10X13 = NA_10X13_ENVELOPE;
    
    public static final MediaType ENV_9X12 = NA_9X12_ENVELOPE;
    
    public static final MediaType ENV_9X11 = NA_9X11_ENVELOPE;
    
    public static final MediaType ENV_7X9 = NA_7X9_ENVELOPE;
    
    public static final MediaType ENV_6X9 = NA_6X9_ENVELOPE;
    
    public static final MediaType ENV_9 = NA_NUMBER_9_ENVELOPE;
    
    public static final MediaType ENV_10 = NA_NUMBER_10_ENVELOPE;
    
    public static final MediaType ENV_11 = NA_NUMBER_11_ENVELOPE;
    
    public static final MediaType ENV_12 = NA_NUMBER_12_ENVELOPE;
    
    public static final MediaType ENV_14 = NA_NUMBER_14_ENVELOPE;
    
    public static final MediaType ENV_INVITE = INVITE_ENVELOPE;
    
    public static final MediaType ENV_ITALY = ITALY_ENVELOPE;
    
    public static final MediaType ENV_MONARCH = MONARCH_ENVELOPE;
    
    public static final MediaType ENV_PERSONAL = PERSONAL_ENVELOPE;
    
    public static final MediaType INVITE = INVITE_ENVELOPE;
    
    public static final MediaType ITALY = ITALY_ENVELOPE;
    
    public static final MediaType MONARCH = MONARCH_ENVELOPE;
    
    public static final MediaType PERSONAL = PERSONAL_ENVELOPE;
    
    private MediaType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class OrientationRequestedType extends AttributeValue {
    private static final int I_PORTRAIT = 0;
    
    private static final int I_LANDSCAPE = 1;
    
    private static final String[] NAMES = { "portrait", "landscape" };
    
    public static final OrientationRequestedType PORTRAIT = new OrientationRequestedType(0);
    
    public static final OrientationRequestedType LANDSCAPE = new OrientationRequestedType(1);
    
    private OrientationRequestedType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class OriginType extends AttributeValue {
    private static final int I_PHYSICAL = 0;
    
    private static final int I_PRINTABLE = 1;
    
    private static final String[] NAMES = { "physical", "printable" };
    
    public static final OriginType PHYSICAL = new OriginType(0);
    
    public static final OriginType PRINTABLE = new OriginType(1);
    
    private OriginType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class PrintQualityType extends AttributeValue {
    private static final int I_HIGH = 0;
    
    private static final int I_NORMAL = 1;
    
    private static final int I_DRAFT = 2;
    
    private static final String[] NAMES = { "high", "normal", "draft" };
    
    public static final PrintQualityType HIGH = new PrintQualityType(0);
    
    public static final PrintQualityType NORMAL = new PrintQualityType(1);
    
    public static final PrintQualityType DRAFT = new PrintQualityType(2);
    
    private PrintQualityType(int param1Int) { super(param1Int, NAMES); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\PageAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
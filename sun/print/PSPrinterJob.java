package sun.print;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterIOException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderMalfunctionError;
import java.nio.file.Files;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.StreamPrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Sides;
import sun.awt.CharsetString;
import sun.awt.FontConfiguration;
import sun.awt.PlatformFont;
import sun.awt.SunToolkit;
import sun.font.FontUtilities;

public class PSPrinterJob extends RasterPrinterJob {
  protected static final int FILL_EVEN_ODD = 1;
  
  protected static final int FILL_WINDING = 2;
  
  private static final int MAX_PSSTR = 65535;
  
  private static final int RED_MASK = 16711680;
  
  private static final int GREEN_MASK = 65280;
  
  private static final int BLUE_MASK = 255;
  
  private static final int RED_SHIFT = 16;
  
  private static final int GREEN_SHIFT = 8;
  
  private static final int BLUE_SHIFT = 0;
  
  private static final int LOWNIBBLE_MASK = 15;
  
  private static final int HINIBBLE_MASK = 240;
  
  private static final int HINIBBLE_SHIFT = 4;
  
  private static final byte[] hexDigits = { 
      48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
      65, 66, 67, 68, 69, 70 };
  
  private static final int PS_XRES = 300;
  
  private static final int PS_YRES = 300;
  
  private static final String ADOBE_PS_STR = "%!PS-Adobe-3.0";
  
  private static final String EOF_COMMENT = "%%EOF";
  
  private static final String PAGE_COMMENT = "%%Page: ";
  
  private static final String READIMAGEPROC = "/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def";
  
  private static final String COPIES = "/#copies exch def";
  
  private static final String PAGE_SAVE = "/pgSave save def";
  
  private static final String PAGE_RESTORE = "pgSave restore";
  
  private static final String SHOWPAGE = "showpage";
  
  private static final String IMAGE_SAVE = "/imSave save def";
  
  private static final String IMAGE_STR = " string /imStr exch def";
  
  private static final String IMAGE_RESTORE = "imSave restore";
  
  private static final String COORD_PREP = " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat";
  
  private static final String SetFontName = "F";
  
  private static final String DrawStringName = "S";
  
  private static final String EVEN_ODD_FILL_STR = "EF";
  
  private static final String WINDING_FILL_STR = "WF";
  
  private static final String EVEN_ODD_CLIP_STR = "EC";
  
  private static final String WINDING_CLIP_STR = "WC";
  
  private static final String MOVETO_STR = " M";
  
  private static final String LINETO_STR = " L";
  
  private static final String CURVETO_STR = " C";
  
  private static final String GRESTORE_STR = "R";
  
  private static final String GSAVE_STR = "G";
  
  private static final String NEWPATH_STR = "N";
  
  private static final String CLOSEPATH_STR = "P";
  
  private static final String SETRGBCOLOR_STR = " SC";
  
  private static final String SETGRAY_STR = " SG";
  
  private int mDestType;
  
  private String mDestination = "lp";
  
  private boolean mNoJobSheet = false;
  
  private String mOptions;
  
  private Font mLastFont;
  
  private Color mLastColor;
  
  private Shape mLastClip;
  
  private AffineTransform mLastTransform;
  
  private EPSPrinter epsPrinter = null;
  
  FontMetrics mCurMetrics;
  
  PrintStream mPSStream;
  
  File spoolFile;
  
  private String mFillOpStr = "WF";
  
  private String mClipOpStr = "WC";
  
  ArrayList mGStateStack = new ArrayList();
  
  private float mPenX;
  
  private float mPenY;
  
  private float mStartPathX;
  
  private float mStartPathY;
  
  private static Properties mFontProps = null;
  
  private static boolean isMac;
  
  private static Properties initProps() {
    String str = System.getProperty("java.home");
    if (str != null) {
      String str1 = SunToolkit.getStartupLocale().getLanguage();
      try {
        File file = new File(str + File.separator + "lib" + File.separator + "psfontj2d.properties." + str1);
        if (!file.canRead()) {
          file = new File(str + File.separator + "lib" + File.separator + "psfont.properties." + str1);
          if (!file.canRead()) {
            file = new File(str + File.separator + "lib" + File.separator + "psfontj2d.properties");
            if (!file.canRead()) {
              file = new File(str + File.separator + "lib" + File.separator + "psfont.properties");
              if (!file.canRead())
                return (Properties)null; 
            } 
          } 
        } 
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file.getPath()));
        Properties properties = new Properties();
        properties.load(bufferedInputStream);
        bufferedInputStream.close();
        return properties;
      } catch (Exception exception) {
        return (Properties)null;
      } 
    } 
    return (Properties)null;
  }
  
  public boolean printDialog() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (this.attributes == null)
      this.attributes = new HashPrintRequestAttributeSet(); 
    this.attributes.add(new Copies(getCopies()));
    this.attributes.add(new JobName(getJobName(), null));
    boolean bool = false;
    DialogTypeSelection dialogTypeSelection = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
    if (dialogTypeSelection == DialogTypeSelection.NATIVE) {
      this.attributes.remove(DialogTypeSelection.class);
      bool = printDialog(this.attributes);
      this.attributes.add(DialogTypeSelection.NATIVE);
    } else {
      bool = printDialog(this.attributes);
    } 
    if (bool) {
      JobName jobName = (JobName)this.attributes.get(JobName.class);
      if (jobName != null)
        setJobName(jobName.getValue()); 
      Copies copies = (Copies)this.attributes.get(Copies.class);
      if (copies != null)
        setCopies(copies.getValue()); 
      Destination destination = (Destination)this.attributes.get(Destination.class);
      if (destination != null) {
        try {
          this.mDestType = 1;
          this.mDestination = (new File(destination.getURI())).getPath();
        } catch (Exception exception) {
          this.mDestination = "out.ps";
        } 
      } else {
        this.mDestType = 0;
        PrintService printService = getPrintService();
        if (printService != null) {
          this.mDestination = printService.getName();
          if (isMac) {
            PrintServiceAttributeSet printServiceAttributeSet = printService.getAttributes();
            if (printServiceAttributeSet != null)
              this.mDestination = printServiceAttributeSet.get(javax.print.attribute.standard.PrinterName.class).toString(); 
          } 
        } 
      } 
    } 
    return bool;
  }
  
  protected void startDoc() {
    if (this.epsPrinter == null) {
      OutputStream outputStream;
      if (getPrintService() instanceof PSStreamPrintService) {
        StreamPrintService streamPrintService = (StreamPrintService)getPrintService();
        this.mDestType = 2;
        if (streamPrintService.isDisposed())
          throw new PrinterException("service is disposed"); 
        outputStream = streamPrintService.getOutputStream();
        if (outputStream == null)
          throw new PrinterException("Null output stream"); 
      } else {
        this.mNoJobSheet = this.noJobSheet;
        if (this.destinationAttr != null) {
          this.mDestType = 1;
          this.mDestination = this.destinationAttr;
        } 
        if (this.mDestType == 1) {
          try {
            this.spoolFile = new File(this.mDestination);
            outputStream = new FileOutputStream(this.spoolFile);
          } catch (IOException iOException) {
            throw new PrinterIOException(iOException);
          } 
        } else {
          PrinterOpener printerOpener = new PrinterOpener(null);
          AccessController.doPrivileged(printerOpener);
          if (printerOpener.pex != null)
            throw printerOpener.pex; 
          outputStream = printerOpener.result;
        } 
      } 
      this.mPSStream = new PrintStream(new BufferedOutputStream(outputStream));
      this.mPSStream.println("%!PS-Adobe-3.0");
    } 
    this.mPSStream.println("%%BeginProlog");
    this.mPSStream.println("/imStr 0 def /imageSrc {currentfile /ASCII85Decode filter /RunLengthDecode filter  imStr readstring pop } def");
    this.mPSStream.println("/BD {bind def} bind def");
    this.mPSStream.println("/D {def} BD");
    this.mPSStream.println("/C {curveto} BD");
    this.mPSStream.println("/L {lineto} BD");
    this.mPSStream.println("/M {moveto} BD");
    this.mPSStream.println("/R {grestore} BD");
    this.mPSStream.println("/G {gsave} BD");
    this.mPSStream.println("/N {newpath} BD");
    this.mPSStream.println("/P {closepath} BD");
    this.mPSStream.println("/EC {eoclip} BD");
    this.mPSStream.println("/WC {clip} BD");
    this.mPSStream.println("/EF {eofill} BD");
    this.mPSStream.println("/WF {fill} BD");
    this.mPSStream.println("/SG {setgray} BD");
    this.mPSStream.println("/SC {setrgbcolor} BD");
    this.mPSStream.println("/ISOF {");
    this.mPSStream.println("     dup findfont dup length 1 add dict begin {");
    this.mPSStream.println("             1 index /FID eq {pop pop} {D} ifelse");
    this.mPSStream.println("     } forall /Encoding ISOLatin1Encoding D");
    this.mPSStream.println("     currentdict end definefont");
    this.mPSStream.println("} BD");
    this.mPSStream.println("/NZ {dup 1 lt {pop 1} if} BD");
    this.mPSStream.println("/S {");
    this.mPSStream.println("     moveto 1 index stringwidth pop NZ sub");
    this.mPSStream.println("     1 index length 1 sub NZ div 0");
    this.mPSStream.println("     3 2 roll ashow newpath} BD");
    this.mPSStream.println("/FL [");
    if (mFontProps == null) {
      this.mPSStream.println(" /Helvetica ISOF");
      this.mPSStream.println(" /Helvetica-Bold ISOF");
      this.mPSStream.println(" /Helvetica-Oblique ISOF");
      this.mPSStream.println(" /Helvetica-BoldOblique ISOF");
      this.mPSStream.println(" /Times-Roman ISOF");
      this.mPSStream.println(" /Times-Bold ISOF");
      this.mPSStream.println(" /Times-Italic ISOF");
      this.mPSStream.println(" /Times-BoldItalic ISOF");
      this.mPSStream.println(" /Courier ISOF");
      this.mPSStream.println(" /Courier-Bold ISOF");
      this.mPSStream.println(" /Courier-Oblique ISOF");
      this.mPSStream.println(" /Courier-BoldOblique ISOF");
    } else {
      int i = Integer.parseInt(mFontProps.getProperty("font.num", "9"));
      for (byte b = 0; b < i; b++)
        this.mPSStream.println("    /" + mFontProps.getProperty("font." + String.valueOf(b), "Courier ISOF")); 
    } 
    this.mPSStream.println("] D");
    this.mPSStream.println("/F {");
    this.mPSStream.println("     FL exch get exch scalefont");
    this.mPSStream.println("     [1 0 0 -1 0 0] makefont setfont} BD");
    this.mPSStream.println("%%EndProlog");
    this.mPSStream.println("%%BeginSetup");
    if (this.epsPrinter == null) {
      PageFormat pageFormat = getPageable().getPageFormat(0);
      double d1 = pageFormat.getPaper().getHeight();
      double d2 = pageFormat.getPaper().getWidth();
      this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
      final PrintService pservice = getPrintService();
      Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              try {
                Class clazz = Class.forName("sun.print.IPPPrintService");
                if (clazz.isInstance(pservice)) {
                  Method method = clazz.getMethod("isPostscript", (Class[])null);
                  return (Boolean)method.invoke(pservice, (Object[])null);
                } 
              } catch (Throwable throwable) {}
              return Boolean.TRUE;
            }
          });
      if (bool.booleanValue())
        this.mPSStream.print(" /DeferredMediaSelection true"); 
      this.mPSStream.print(" /ImagingBBox null /ManualFeed false");
      this.mPSStream.print(isCollated() ? " /Collate true" : "");
      this.mPSStream.print(" /NumCopies " + getCopiesInt());
      if (this.sidesAttr != Sides.ONE_SIDED)
        if (this.sidesAttr == Sides.TWO_SIDED_LONG_EDGE) {
          this.mPSStream.print(" /Duplex true ");
        } else if (this.sidesAttr == Sides.TWO_SIDED_SHORT_EDGE) {
          this.mPSStream.print(" /Duplex true /Tumble true ");
        }  
      this.mPSStream.println(" >> setpagedevice ");
    } 
    this.mPSStream.println("%%EndSetup");
  }
  
  protected void abortDoc() {
    if (this.mPSStream != null && this.mDestType != 2)
      this.mPSStream.close(); 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            if (PSPrinterJob.this.spoolFile != null && PSPrinterJob.this.spoolFile.exists())
              PSPrinterJob.this.spoolFile.delete(); 
            return null;
          }
        });
  }
  
  protected void endDoc() {
    if (this.mPSStream != null) {
      this.mPSStream.println("%%EOF");
      this.mPSStream.flush();
      if (this.mDestType != 2)
        this.mPSStream.close(); 
    } 
    if (this.mDestType == 0) {
      PrintService printService = getPrintService();
      if (printService != null) {
        this.mDestination = printService.getName();
        if (isMac) {
          PrintServiceAttributeSet printServiceAttributeSet = printService.getAttributes();
          if (printServiceAttributeSet != null)
            this.mDestination = printServiceAttributeSet.get(javax.print.attribute.standard.PrinterName.class).toString(); 
        } 
      } 
      PrinterSpooler printerSpooler = new PrinterSpooler(null);
      AccessController.doPrivileged(printerSpooler);
      if (printerSpooler.pex != null)
        throw printerSpooler.pex; 
    } 
  }
  
  protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean) throws PrinterException {
    double d1 = paramPageFormat.getPaper().getHeight();
    double d2 = paramPageFormat.getPaper().getWidth();
    int i = paramInt + 1;
    this.mGStateStack = new ArrayList();
    this.mGStateStack.add(new GState());
    this.mPSStream.println("%%Page: " + i + " " + i);
    if (paramInt > 0 && paramBoolean) {
      this.mPSStream.print("<< /PageSize [" + d2 + " " + d1 + "]");
      final PrintService pservice = getPrintService();
      Boolean bool = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              try {
                Class clazz = Class.forName("sun.print.IPPPrintService");
                if (clazz.isInstance(pservice)) {
                  Method method = clazz.getMethod("isPostscript", (Class[])null);
                  return (Boolean)method.invoke(pservice, (Object[])null);
                } 
              } catch (Throwable throwable) {}
              return Boolean.TRUE;
            }
          });
      if (bool.booleanValue())
        this.mPSStream.print(" /DeferredMediaSelection true"); 
      this.mPSStream.println(" >> setpagedevice");
    } 
    this.mPSStream.println("/pgSave save def");
    this.mPSStream.println(d1 + " 0 exch translate 1 -1 scale[72 300 div 0 0 72 300 div 0 0]concat");
  }
  
  protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt) throws PrinterException {
    this.mPSStream.println("pgSave restore");
    this.mPSStream.println("showpage");
  }
  
  protected void drawImageBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt1, int paramInt2) {
    setTransform(new AffineTransform());
    prepDrawing();
    int i = (int)paramFloat7;
    int j = (int)paramFloat8;
    this.mPSStream.println("/imSave save def");
    int k;
    for (k = 3 * i; k > 65535; k /= 2);
    this.mPSStream.println(k + " string /imStr exch def");
    this.mPSStream.println("[" + paramFloat3 + " 0 0 " + paramFloat4 + " " + paramFloat1 + " " + paramFloat2 + "]concat");
    this.mPSStream.println(i + " " + j + " " + '\b' + "[" + i + " 0 0 " + j + " 0 " + Character.MIN_VALUE + "]/imageSrc load false 3 colorimage");
    int m = 0;
    byte[] arrayOfByte = new byte[i * 3];
    try {
      m = (int)paramFloat6 * paramInt1;
      for (byte b = 0; b < j; b++) {
        m += (int)paramFloat5;
        m = swapBGRtoRGB(paramArrayOfByte, m, arrayOfByte);
        byte[] arrayOfByte1 = rlEncode(arrayOfByte);
        byte[] arrayOfByte2 = ascii85Encode(arrayOfByte1);
        this.mPSStream.write(arrayOfByte2);
        this.mPSStream.println("");
      } 
    } catch (IOException iOException) {}
    this.mPSStream.println("imSave restore");
  }
  
  protected void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws PrinterException {
    this.mPSStream.println("/imSave save def");
    int i;
    for (i = 3 * paramInt3; i > 65535; i /= 2);
    this.mPSStream.println(i + " string /imStr exch def");
    this.mPSStream.println("[" + paramInt3 + " 0 0 " + paramInt4 + " " + paramInt1 + " " + paramInt2 + "]concat");
    this.mPSStream.println(paramInt3 + " " + paramInt4 + " " + '\b' + "[" + paramInt3 + " 0 0 " + -paramInt4 + " 0 " + paramInt4 + "]/imageSrc load false 3 colorimage");
    int j = 0;
    byte[] arrayOfByte = new byte[paramInt3 * 3];
    try {
      for (byte b = 0; b < paramInt4; b++) {
        j = swapBGRtoRGB(paramArrayOfByte, j, arrayOfByte);
        byte[] arrayOfByte1 = rlEncode(arrayOfByte);
        byte[] arrayOfByte2 = ascii85Encode(arrayOfByte1);
        this.mPSStream.write(arrayOfByte2);
        this.mPSStream.println("");
      } 
    } catch (IOException iOException) {
      throw new PrinterIOException(iOException);
    } 
    this.mPSStream.println("imSave restore");
  }
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt) {
    PSPathGraphics pSPathGraphics;
    PeekMetrics peekMetrics = paramPeekGraphics.getMetrics();
    if (!forcePDL && (forceRaster == true || peekMetrics.hasNonSolidColors() || peekMetrics.hasCompositing())) {
      pSPathGraphics = null;
    } else {
      BufferedImage bufferedImage = new BufferedImage(8, 8, 1);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
      pSPathGraphics = new PSPathGraphics(graphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
    } 
    return pSPathGraphics;
  }
  
  protected void selectClipPath() { this.mPSStream.println(this.mClipOpStr); }
  
  protected void setClip(Shape paramShape) { this.mLastClip = paramShape; }
  
  protected void setTransform(AffineTransform paramAffineTransform) { this.mLastTransform = paramAffineTransform; }
  
  protected boolean setFont(Font paramFont) {
    this.mLastFont = paramFont;
    return true;
  }
  
  private int[] getPSFontIndexArray(Font paramFont, CharsetString[] paramArrayOfCharsetString) {
    int[] arrayOfInt = null;
    if (mFontProps != null)
      arrayOfInt = new int[paramArrayOfCharsetString.length]; 
    for (byte b = 0; b < paramArrayOfCharsetString.length && arrayOfInt != null; b++) {
      CharsetString charsetString = paramArrayOfCharsetString[b];
      CharsetEncoder charsetEncoder = charsetString.fontDescriptor.encoder;
      String str1 = charsetString.fontDescriptor.getFontCharsetName();
      if ("Symbol".equals(str1)) {
        str1 = "symbol";
      } else if ("WingDings".equals(str1) || "X11Dingbats".equals(str1)) {
        str1 = "dingbats";
      } else {
        str1 = makeCharsetName(str1, charsetString.charsetChars);
      } 
      int i = paramFont.getStyle() | FontUtilities.getFont2D(paramFont).getStyle();
      String str2 = FontConfiguration.getStyleString(i);
      String str3 = paramFont.getFamily().toLowerCase(Locale.ENGLISH);
      str3 = str3.replace(' ', '_');
      String str4 = mFontProps.getProperty(str3, "");
      String str5 = mFontProps.getProperty(str4 + "." + str1 + "." + str2, null);
      if (str5 != null) {
        try {
          arrayOfInt[b] = Integer.parseInt(mFontProps.getProperty(str5));
        } catch (NumberFormatException numberFormatException) {
          arrayOfInt = null;
        } 
      } else {
        arrayOfInt = null;
      } 
    } 
    return arrayOfInt;
  }
  
  private static String escapeParens(String paramString) {
    if (paramString.indexOf('(') == -1 && paramString.indexOf(')') == -1)
      return paramString; 
    int i = 0;
    int j;
    for (j = 0; (j = paramString.indexOf('(', j)) != -1; j++)
      i++; 
    for (j = 0; (j = paramString.indexOf(')', j)) != -1; j++)
      i++; 
    char[] arrayOfChar1 = paramString.toCharArray();
    char[] arrayOfChar2 = new char[arrayOfChar1.length + i];
    j = 0;
    for (byte b = 0; b < arrayOfChar1.length; b++) {
      if (arrayOfChar1[b] == '(' || arrayOfChar1[b] == ')')
        arrayOfChar2[j++] = '\\'; 
      arrayOfChar2[j++] = arrayOfChar1[b];
    } 
    return new String(arrayOfChar2);
  }
  
  protected int platformFontCount(Font paramFont, String paramString) {
    if (mFontProps == null)
      return 0; 
    CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
    if (arrayOfCharsetString == null)
      return 0; 
    int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
    return (arrayOfInt == null) ? 0 : arrayOfInt.length;
  }
  
  protected boolean textOut(Graphics paramGraphics, String paramString, float paramFloat1, float paramFloat2, Font paramFont, FontRenderContext paramFontRenderContext, float paramFloat3) {
    boolean bool = true;
    if (mFontProps == null)
      return false; 
    prepDrawing();
    paramString = removeControlChars(paramString);
    if (paramString.length() == 0)
      return true; 
    CharsetString[] arrayOfCharsetString = ((PlatformFont)paramFont.getPeer()).makeMultiCharsetString(paramString, false);
    if (arrayOfCharsetString == null)
      return false; 
    int[] arrayOfInt = getPSFontIndexArray(paramFont, arrayOfCharsetString);
    if (arrayOfInt != null) {
      for (byte b = 0; b < arrayOfCharsetString.length; b++) {
        float f;
        CharsetString charsetString = arrayOfCharsetString[b];
        CharsetEncoder charsetEncoder = charsetString.fontDescriptor.encoder;
        StringBuffer stringBuffer = new StringBuffer();
        byte[] arrayOfByte = new byte[charsetString.length * 2];
        int i = 0;
        try {
          ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
          charsetEncoder.encode(CharBuffer.wrap(charsetString.charsetChars, charsetString.offset, charsetString.length), byteBuffer, true);
          byteBuffer.flip();
          i = byteBuffer.limit();
        } catch (IllegalStateException illegalStateException) {
        
        } catch (CoderMalfunctionError coderMalfunctionError) {}
        if (arrayOfCharsetString.length == 1 && paramFloat3 != 0.0F) {
          f = paramFloat3;
        } else {
          Rectangle2D rectangle2D = paramFont.getStringBounds(charsetString.charsetChars, charsetString.offset, charsetString.offset + charsetString.length, paramFontRenderContext);
          f = (float)rectangle2D.getWidth();
        } 
        if (f == 0.0F)
          return bool; 
        stringBuffer.append('<');
        for (byte b1 = 0; b1 < i; b1++) {
          byte b2 = arrayOfByte[b1];
          String str = Integer.toHexString(b2);
          int j = str.length();
          if (j > 2) {
            str = str.substring(j - 2, j);
          } else if (j == 1) {
            str = "0" + str;
          } else if (j == 0) {
            str = "00";
          } 
          stringBuffer.append(str);
        } 
        stringBuffer.append('>');
        getGState().emitPSFont(arrayOfInt[b], paramFont.getSize2D());
        this.mPSStream.println(stringBuffer.toString() + " " + f + " " + paramFloat1 + " " + paramFloat2 + " " + "S");
        paramFloat1 += f;
      } 
    } else {
      bool = false;
    } 
    return bool;
  }
  
  protected void setFillMode(int paramInt) {
    switch (paramInt) {
      case 1:
        this.mFillOpStr = "EF";
        this.mClipOpStr = "EC";
        return;
      case 2:
        this.mFillOpStr = "WF";
        this.mClipOpStr = "WC";
        return;
    } 
    throw new IllegalArgumentException();
  }
  
  protected void setColor(Color paramColor) { this.mLastColor = paramColor; }
  
  protected void fillPath() { this.mPSStream.println(this.mFillOpStr); }
  
  protected void beginPath() {
    prepDrawing();
    this.mPSStream.println("N");
    this.mPenX = 0.0F;
    this.mPenY = 0.0F;
  }
  
  protected void closeSubpath() {
    this.mPSStream.println("P");
    this.mPenX = this.mStartPathX;
    this.mPenY = this.mStartPathY;
  }
  
  protected void moveTo(float paramFloat1, float paramFloat2) {
    this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " M");
    this.mStartPathX = paramFloat1;
    this.mStartPathY = paramFloat2;
    this.mPenX = paramFloat1;
    this.mPenY = paramFloat2;
  }
  
  protected void lineTo(float paramFloat1, float paramFloat2) {
    this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " L");
    this.mPenX = paramFloat1;
    this.mPenY = paramFloat2;
  }
  
  protected void bezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) {
    this.mPSStream.println(trunc(paramFloat1) + " " + trunc(paramFloat2) + " " + trunc(paramFloat3) + " " + trunc(paramFloat4) + " " + trunc(paramFloat5) + " " + trunc(paramFloat6) + " C");
    this.mPenX = paramFloat5;
    this.mPenY = paramFloat6;
  }
  
  String trunc(float paramFloat) {
    float f = Math.abs(paramFloat);
    if (f >= 1.0F && f <= 1000.0F)
      paramFloat = Math.round(paramFloat * 1000.0F) / 1000.0F; 
    return Float.toString(paramFloat);
  }
  
  protected float getPenX() { return this.mPenX; }
  
  protected float getPenY() { return this.mPenY; }
  
  protected double getXRes() { return 300.0D; }
  
  protected double getYRes() { return 300.0D; }
  
  protected double getPhysicalPrintableX(Paper paramPaper) { return 0.0D; }
  
  protected double getPhysicalPrintableY(Paper paramPaper) { return 0.0D; }
  
  protected double getPhysicalPrintableWidth(Paper paramPaper) { return paramPaper.getImageableWidth(); }
  
  protected double getPhysicalPrintableHeight(Paper paramPaper) { return paramPaper.getImageableHeight(); }
  
  protected double getPhysicalPageWidth(Paper paramPaper) { return paramPaper.getWidth(); }
  
  protected double getPhysicalPageHeight(Paper paramPaper) { return paramPaper.getHeight(); }
  
  protected int getNoncollatedCopies() { return 1; }
  
  protected int getCollatedCopies() { return 1; }
  
  private String[] printExecCmd(String paramString1, String paramString2, boolean paramBoolean, String paramString3, int paramInt, String paramString4) {
    String[] arrayOfString;
    byte b1 = 1;
    byte b2 = 2;
    byte b3 = 4;
    byte b4 = 8;
    byte b5 = 16;
    byte b6 = 0;
    byte b7 = 2;
    byte b8 = 0;
    if (paramString1 != null && !paramString1.equals("") && !paramString1.equals("lp")) {
      b6 |= b1;
      b7++;
    } 
    if (paramString2 != null && !paramString2.equals("")) {
      b6 |= b2;
      b7++;
    } 
    if (paramString3 != null && !paramString3.equals("")) {
      b6 |= b3;
      b7++;
    } 
    if (paramInt > 1) {
      b6 |= b4;
      b7++;
    } 
    if (paramBoolean) {
      b6 |= b5;
      b7++;
    } 
    String str = System.getProperty("os.name");
    if (str.equals("Linux") || str.contains("OS X")) {
      arrayOfString = new String[b7];
      arrayOfString[b8++] = "/usr/bin/lpr";
      if ((b6 & b1) != 0)
        arrayOfString[b8++] = "-P" + paramString1; 
      if ((b6 & b3) != 0)
        arrayOfString[b8++] = "-J" + paramString3; 
      if ((b6 & b4) != 0)
        arrayOfString[b8++] = "-#" + paramInt; 
      if ((b6 & b5) != 0)
        arrayOfString[b8++] = "-h"; 
      if ((b6 & b2) != 0)
        arrayOfString[b8++] = new String(paramString2); 
    } else {
      arrayOfString = new String[++b7];
      arrayOfString[b8++] = "/usr/bin/lp";
      arrayOfString[b8++] = "-c";
      if ((b6 & b1) != 0)
        arrayOfString[b8++] = "-d" + paramString1; 
      if ((b6 & b3) != 0)
        arrayOfString[b8++] = "-t" + paramString3; 
      if ((b6 & b4) != 0)
        arrayOfString[b8++] = "-n" + paramInt; 
      if ((b6 & b5) != 0)
        arrayOfString[b8++] = "-o nobanner"; 
      if ((b6 & b2) != 0)
        arrayOfString[b8++] = "-o" + paramString2; 
    } 
    arrayOfString[b8++] = paramString4;
    return arrayOfString;
  }
  
  private static int swapBGRtoRGB(byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2) {
    byte b = 0;
    while (paramInt < paramArrayOfByte1.length - 2 && b < paramArrayOfByte2.length - 2) {
      paramArrayOfByte2[b++] = paramArrayOfByte1[paramInt + 2];
      paramArrayOfByte2[b++] = paramArrayOfByte1[paramInt + 1];
      paramArrayOfByte2[b++] = paramArrayOfByte1[paramInt + 0];
      paramInt += 3;
    } 
    return paramInt;
  }
  
  private String makeCharsetName(String paramString, char[] paramArrayOfChar) {
    if (paramString.equals("Cp1252") || paramString.equals("ISO8859_1"))
      return "latin1"; 
    if (paramString.equals("UTF8")) {
      for (byte b = 0; b < paramArrayOfChar.length; b++) {
        if (paramArrayOfChar[b] > 'ÿ')
          return paramString.toLowerCase(); 
      } 
      return "latin1";
    } 
    if (paramString.startsWith("ISO8859")) {
      for (byte b = 0; b < paramArrayOfChar.length; b++) {
        if (paramArrayOfChar[b] > '')
          return paramString.toLowerCase(); 
      } 
      return "latin1";
    } 
    return paramString.toLowerCase();
  }
  
  private void prepDrawing() {
    while (!isOuterGState() && (!getGState().canSetClip(this.mLastClip) || !(getGState()).mTransform.equals(this.mLastTransform)))
      grestore(); 
    getGState().emitPSColor(this.mLastColor);
    if (isOuterGState()) {
      gsave();
      getGState().emitTransform(this.mLastTransform);
      getGState().emitPSClip(this.mLastClip);
    } 
  }
  
  private GState getGState() {
    int i = this.mGStateStack.size();
    return (GState)this.mGStateStack.get(i - 1);
  }
  
  private void gsave() {
    GState gState = getGState();
    this.mGStateStack.add(new GState(gState));
    this.mPSStream.println("G");
  }
  
  private void grestore() {
    int i = this.mGStateStack.size();
    this.mGStateStack.remove(i - 1);
    this.mPSStream.println("R");
  }
  
  private boolean isOuterGState() throws HeadlessException { return (this.mGStateStack.size() == 1); }
  
  void convertToPSPath(PathIterator paramPathIterator) {
    byte b;
    float[] arrayOfFloat = new float[6];
    if (paramPathIterator.getWindingRule() == 0) {
      b = 1;
    } else {
      b = 2;
    } 
    beginPath();
    setFillMode(b);
    while (!paramPathIterator.isDone()) {
      float f6;
      float f5;
      float f4;
      float f3;
      float f2;
      float f1;
      int i = paramPathIterator.currentSegment(arrayOfFloat);
      switch (i) {
        case 0:
          moveTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 1:
          lineTo(arrayOfFloat[0], arrayOfFloat[1]);
          break;
        case 2:
          f1 = getPenX();
          f2 = getPenY();
          f3 = f1 + (arrayOfFloat[0] - f1) * 2.0F / 3.0F;
          f4 = f2 + (arrayOfFloat[1] - f2) * 2.0F / 3.0F;
          f5 = arrayOfFloat[2] - (arrayOfFloat[2] - arrayOfFloat[0]) * 2.0F / 3.0F;
          f6 = arrayOfFloat[3] - (arrayOfFloat[3] - arrayOfFloat[1]) * 2.0F / 3.0F;
          bezierTo(f3, f4, f5, f6, arrayOfFloat[2], arrayOfFloat[3]);
          break;
        case 3:
          bezierTo(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], arrayOfFloat[4], arrayOfFloat[5]);
          break;
        case 4:
          closeSubpath();
          break;
      } 
      paramPathIterator.next();
    } 
  }
  
  protected void deviceFill(PathIterator paramPathIterator, Color paramColor, AffineTransform paramAffineTransform, Shape paramShape) {
    setTransform(paramAffineTransform);
    setClip(paramShape);
    setColor(paramColor);
    convertToPSPath(paramPathIterator);
    this.mPSStream.println("G");
    selectClipPath();
    fillPath();
    this.mPSStream.println("R N");
  }
  
  private byte[] rlEncode(byte[] paramArrayOfByte) {
    byte b1 = 0;
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    byte[] arrayOfByte1 = new byte[paramArrayOfByte.length * 2 + 2];
    while (b1 < paramArrayOfByte.length) {
      if (!b4) {
        b3 = b1++;
        b4 = 1;
      } 
      while (b4 < '' && b1 < paramArrayOfByte.length && paramArrayOfByte[b1] == paramArrayOfByte[b3]) {
        b4++;
        b1++;
      } 
      if (b4 > 1) {
        arrayOfByte1[b2++] = (byte)('ā' - b4);
        arrayOfByte1[b2++] = paramArrayOfByte[b3];
        b4 = 0;
        continue;
      } 
      while (b4 < '' && b1 < paramArrayOfByte.length && paramArrayOfByte[b1] != paramArrayOfByte[b1 - 1]) {
        b4++;
        b1++;
      } 
      arrayOfByte1[b2++] = (byte)(b4 - 1);
      for (byte b = b3; b < b3 + b4; b++)
        arrayOfByte1[b2++] = paramArrayOfByte[b]; 
      b4 = 0;
    } 
    arrayOfByte1[b2++] = Byte.MIN_VALUE;
    byte[] arrayOfByte2 = new byte[b2];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, b2);
    return arrayOfByte2;
  }
  
  private byte[] ascii85Encode(byte[] paramArrayOfByte) {
    byte[] arrayOfByte1 = new byte[(paramArrayOfByte.length + 4) * 5 / 4 + 2];
    long l1 = 85L;
    long l2 = l1 * l1;
    long l3 = l1 * l2;
    long l4 = l1 * l3;
    byte b1 = 33;
    int i = 0;
    byte b2 = 0;
    while (i + 3 < paramArrayOfByte.length) {
      long l5 = ((paramArrayOfByte[i++] & 0xFF) << 24) + ((paramArrayOfByte[i++] & 0xFF) << 16) + ((paramArrayOfByte[i++] & 0xFF) << 8) + (paramArrayOfByte[i++] & 0xFF);
      if (l5 == 0L) {
        arrayOfByte1[b2++] = 122;
        continue;
      } 
      long l6 = l5;
      arrayOfByte1[b2++] = (byte)(int)(l6 / l4 + b1);
      l6 %= l4;
      arrayOfByte1[b2++] = (byte)(int)(l6 / l3 + b1);
      l6 %= l3;
      arrayOfByte1[b2++] = (byte)(int)(l6 / l2 + b1);
      l6 %= l2;
      arrayOfByte1[b2++] = (byte)(int)(l6 / l1 + b1);
      l6 %= l1;
      arrayOfByte1[b2++] = (byte)(int)(l6 + b1);
    } 
    if (i < paramArrayOfByte.length) {
      int j = paramArrayOfByte.length - i;
      long l5;
      for (l5 = 0L; i < paramArrayOfByte.length; l5 = (l5 << 8) + (paramArrayOfByte[i++] & 0xFF));
      int k = 4 - j;
      while (k-- > 0)
        l5 <<= 8; 
      byte[] arrayOfByte = new byte[5];
      long l6 = l5;
      arrayOfByte[0] = (byte)(int)(l6 / l4 + b1);
      l6 %= l4;
      arrayOfByte[1] = (byte)(int)(l6 / l3 + b1);
      l6 %= l3;
      arrayOfByte[2] = (byte)(int)(l6 / l2 + b1);
      l6 %= l2;
      arrayOfByte[3] = (byte)(int)(l6 / l1 + b1);
      l6 %= l1;
      arrayOfByte[4] = (byte)(int)(l6 + b1);
      for (byte b = 0; b < j + 1; b++)
        arrayOfByte1[b2++] = arrayOfByte[b]; 
    } 
    arrayOfByte1[b2++] = 126;
    arrayOfByte1[b2++] = 62;
    byte[] arrayOfByte2 = new byte[b2];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, b2);
    return arrayOfByte2;
  }
  
  static  {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            mFontProps = PSPrinterJob.initProps();
            String str = System.getProperty("os.name");
            isMac = str.startsWith("Mac");
            return null;
          }
        });
  }
  
  public static class EPSPrinter implements Pageable {
    private PageFormat pf;
    
    private PSPrinterJob job;
    
    private int llx;
    
    private int lly;
    
    private int urx;
    
    private int ury;
    
    private Printable printable;
    
    private PrintStream stream;
    
    private String epsTitle;
    
    public EPSPrinter(Printable param1Printable, String param1String, PrintStream param1PrintStream, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.printable = param1Printable;
      this.epsTitle = param1String;
      this.stream = param1PrintStream;
      this.llx = param1Int1;
      this.lly = param1Int2;
      this.urx = this.llx + param1Int3;
      this.ury = this.lly + param1Int4;
      Paper paper = new Paper();
      paper.setSize(param1Int3, param1Int4);
      paper.setImageableArea(0.0D, 0.0D, param1Int3, param1Int4);
      this.pf = new PageFormat();
      this.pf.setPaper(paper);
    }
    
    public void print() {
      this.stream.println("%!PS-Adobe-3.0 EPSF-3.0");
      this.stream.println("%%BoundingBox: " + this.llx + " " + this.lly + " " + this.urx + " " + this.ury);
      this.stream.println("%%Title: " + this.epsTitle);
      this.stream.println("%%Creator: Java Printing");
      this.stream.println("%%CreationDate: " + new Date());
      this.stream.println("%%EndComments");
      this.stream.println("/pluginSave save def");
      this.stream.println("mark");
      this.job.epsPrinter = this;
      this.job.mPSStream = this.stream;
      this.job.mDestType = 2;
      this.job.startDoc();
      try {
        this.job.printPage(this, 0);
      } catch (Throwable throwable) {
        if (throwable instanceof PrinterException)
          throw (PrinterException)throwable; 
        throw new PrinterException(throwable.toString());
      } finally {
        this.stream.println("cleartomark");
        this.stream.println("pluginSave restore");
        this.job.endDoc();
      } 
      this.stream.flush();
    }
    
    public int getNumberOfPages() { return 1; }
    
    public PageFormat getPageFormat(int param1Int) {
      if (param1Int > 0)
        throw new IndexOutOfBoundsException("pgIndex"); 
      return this.pf;
    }
    
    public Printable getPrintable(int param1Int) {
      if (param1Int > 0)
        throw new IndexOutOfBoundsException("pgIndex"); 
      return this.printable;
    }
  }
  
  private class GState {
    Color mColor = Color.black;
    
    Shape mClip = null;
    
    Font mFont = null;
    
    AffineTransform mTransform = new AffineTransform();
    
    GState() {}
    
    GState(GState param1GState) {}
    
    boolean canSetClip(Shape param1Shape) { return (this.mClip == null || this.mClip.equals(param1Shape)); }
    
    void emitPSClip(Shape param1Shape) {
      if (param1Shape != null && (this.mClip == null || !this.mClip.equals(param1Shape))) {
        String str1 = PSPrinterJob.this.mFillOpStr;
        String str2 = PSPrinterJob.this.mClipOpStr;
        PSPrinterJob.this.convertToPSPath(param1Shape.getPathIterator(new AffineTransform()));
        PSPrinterJob.this.selectClipPath();
        this.mClip = param1Shape;
        PSPrinterJob.this.mClipOpStr = str1;
        PSPrinterJob.this.mFillOpStr = str1;
      } 
    }
    
    void emitTransform(AffineTransform param1AffineTransform) {
      if (param1AffineTransform != null && !param1AffineTransform.equals(this.mTransform)) {
        double[] arrayOfDouble = new double[6];
        param1AffineTransform.getMatrix(arrayOfDouble);
        PSPrinterJob.this.mPSStream.println("[" + (float)arrayOfDouble[0] + " " + (float)arrayOfDouble[1] + " " + (float)arrayOfDouble[2] + " " + (float)arrayOfDouble[3] + " " + (float)arrayOfDouble[4] + " " + (float)arrayOfDouble[5] + "] concat");
        this.mTransform = param1AffineTransform;
      } 
    }
    
    void emitPSColor(Color param1Color) {
      if (param1Color != null && !param1Color.equals(this.mColor)) {
        float[] arrayOfFloat = param1Color.getRGBColorComponents(null);
        if (arrayOfFloat[0] == arrayOfFloat[1] && arrayOfFloat[1] == arrayOfFloat[2]) {
          PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " SG");
        } else {
          PSPrinterJob.this.mPSStream.println(arrayOfFloat[0] + " " + arrayOfFloat[1] + " " + arrayOfFloat[2] + " " + " SC");
        } 
        this.mColor = param1Color;
      } 
    }
    
    void emitPSFont(int param1Int, float param1Float) { PSPrinterJob.this.mPSStream.println(param1Float + " " + param1Int + " " + "F"); }
  }
  
  public static class PluginPrinter implements Printable {
    private PSPrinterJob.EPSPrinter epsPrinter;
    
    private Component applet;
    
    private PrintStream stream;
    
    private String epsTitle;
    
    private int bx;
    
    private int by;
    
    private int bw;
    
    private int bh;
    
    private int width;
    
    private int height;
    
    public PluginPrinter(Component param1Component, PrintStream param1PrintStream, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      this.applet = param1Component;
      this.epsTitle = "Java Plugin Applet";
      this.stream = param1PrintStream;
      this.bx = param1Int1;
      this.by = param1Int2;
      this.bw = param1Int3;
      this.bh = param1Int4;
      this.width = (param1Component.size()).width;
      this.height = (param1Component.size()).height;
      this.epsPrinter = new PSPrinterJob.EPSPrinter(this, this.epsTitle, param1PrintStream, 0, 0, this.width, this.height);
    }
    
    public void printPluginPSHeader() { this.stream.println("%%BeginDocument: JavaPluginApplet"); }
    
    public void printPluginApplet() {
      try {
        this.epsPrinter.print();
      } catch (PrinterException printerException) {}
    }
    
    public void printPluginPSTrailer() {
      this.stream.println("%%EndDocument: JavaPluginApplet");
      this.stream.flush();
    }
    
    public void printAll() {
      printPluginPSHeader();
      printPluginApplet();
      printPluginPSTrailer();
    }
    
    public int print(Graphics param1Graphics, PageFormat param1PageFormat, int param1Int) {
      if (param1Int > 0)
        return 1; 
      this.applet.printAll(param1Graphics);
      return 0;
    }
  }
  
  private class PrinterOpener implements PrivilegedAction {
    PrinterException pex;
    
    OutputStream result;
    
    private PrinterOpener() {}
    
    public Object run() {
      try {
        PSPrinterJob.this.spoolFile = Files.createTempFile("javaprint", ".ps", new java.nio.file.attribute.FileAttribute[0]).toFile();
        PSPrinterJob.this.spoolFile.deleteOnExit();
        this.result = new FileOutputStream(PSPrinterJob.this.spoolFile);
        return this.result;
      } catch (IOException iOException) {
        this.pex = new PrinterIOException(iOException);
        return null;
      } 
    }
  }
  
  private class PrinterSpooler implements PrivilegedAction {
    PrinterException pex;
    
    private PrinterSpooler() {}
    
    private void handleProcessFailure(Process param1Process, String[] param1ArrayOfString, int param1Int) throws IOException {
      try(StringWriter null = new StringWriter(); PrintWriter null = new PrintWriter(stringWriter)) {
        printWriter.append("error=").append(Integer.toString(param1Int));
        printWriter.append(" running:");
        for (String str : param1ArrayOfString)
          printWriter.append(" '").append(str).append("'"); 
      } 
    }
    
    public Object run() {
      if (PSPrinterJob.this.spoolFile == null || !PSPrinterJob.this.spoolFile.exists()) {
        this.pex = new PrinterException("No spool file");
        return null;
      } 
      try {
        String str = PSPrinterJob.this.spoolFile.getAbsolutePath();
        String[] arrayOfString = PSPrinterJob.this.printExecCmd(PSPrinterJob.this.mDestination, PSPrinterJob.this.mOptions, PSPrinterJob.this.mNoJobSheet, PSPrinterJob.this.getJobNameInt(), 1, str);
        Process process = Runtime.getRuntime().exec(arrayOfString);
        process.waitFor();
        int i = process.exitValue();
        if (0 != i)
          handleProcessFailure(process, arrayOfString, i); 
      } catch (IOException iOException) {
        this.pex = new PrinterIOException(iOException);
      } catch (InterruptedException interruptedException) {
        this.pex = new PrinterException(interruptedException.toString());
      } finally {
        PSPrinterJob.this.spoolFile.delete();
      } 
      return null;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PSPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
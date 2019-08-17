package sun.awt.windows;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.peer.ComponentPeer;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import sun.awt.Win32FontManager;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.DisposerTarget;
import sun.print.DialogOwner;
import sun.print.PeekGraphics;
import sun.print.PeekMetrics;
import sun.print.RasterPrinterJob;
import sun.print.ServiceDialog;
import sun.print.SunAlternateMedia;
import sun.print.SunPageSelection;
import sun.print.Win32MediaTray;
import sun.print.Win32PrintService;
import sun.print.Win32PrintServiceLookup;

public final class WPrinterJob extends RasterPrinterJob implements DisposerTarget {
  protected static final long PS_ENDCAP_ROUND = 0L;
  
  protected static final long PS_ENDCAP_SQUARE = 256L;
  
  protected static final long PS_ENDCAP_FLAT = 512L;
  
  protected static final long PS_JOIN_ROUND = 0L;
  
  protected static final long PS_JOIN_BEVEL = 4096L;
  
  protected static final long PS_JOIN_MITER = 8192L;
  
  protected static final int POLYFILL_ALTERNATE = 1;
  
  protected static final int POLYFILL_WINDING = 2;
  
  private static final int MAX_WCOLOR = 255;
  
  private static final int SET_DUP_VERTICAL = 16;
  
  private static final int SET_DUP_HORIZONTAL = 32;
  
  private static final int SET_RES_HIGH = 64;
  
  private static final int SET_RES_LOW = 128;
  
  private static final int SET_COLOR = 512;
  
  private static final int SET_ORIENTATION = 16384;
  
  private static final int SET_COLLATED = 32768;
  
  private static final int PD_COLLATE = 16;
  
  private static final int PD_PRINTTOFILE = 32;
  
  private static final int DM_ORIENTATION = 1;
  
  private static final int DM_PAPERSIZE = 2;
  
  private static final int DM_COPIES = 256;
  
  private static final int DM_DEFAULTSOURCE = 512;
  
  private static final int DM_PRINTQUALITY = 1024;
  
  private static final int DM_COLOR = 2048;
  
  private static final int DM_DUPLEX = 4096;
  
  private static final int DM_YRESOLUTION = 8192;
  
  private static final int DM_COLLATE = 32768;
  
  private static final short DMCOLLATE_FALSE = 0;
  
  private static final short DMCOLLATE_TRUE = 1;
  
  private static final short DMORIENT_PORTRAIT = 1;
  
  private static final short DMORIENT_LANDSCAPE = 2;
  
  private static final short DMCOLOR_MONOCHROME = 1;
  
  private static final short DMCOLOR_COLOR = 2;
  
  private static final short DMRES_DRAFT = -1;
  
  private static final short DMRES_LOW = -2;
  
  private static final short DMRES_MEDIUM = -3;
  
  private static final short DMRES_HIGH = -4;
  
  private static final short DMDUP_SIMPLEX = 1;
  
  private static final short DMDUP_VERTICAL = 2;
  
  private static final short DMDUP_HORIZONTAL = 3;
  
  private static final int MAX_UNKNOWN_PAGES = 9999;
  
  private boolean driverDoesMultipleCopies = false;
  
  private boolean driverDoesCollation = false;
  
  private boolean userRequestedCollation = false;
  
  private boolean noDefaultPrinter = false;
  
  private HandleRecord handleRecord = new HandleRecord();
  
  private int mPrintPaperSize;
  
  private int mPrintXRes;
  
  private int mPrintYRes;
  
  private int mPrintPhysX;
  
  private int mPrintPhysY;
  
  private int mPrintWidth;
  
  private int mPrintHeight;
  
  private int mPageWidth;
  
  private int mPageHeight;
  
  private int mAttSides;
  
  private int mAttChromaticity;
  
  private int mAttXRes;
  
  private int mAttYRes;
  
  private int mAttQuality;
  
  private int mAttCollate;
  
  private int mAttCopies;
  
  private int mAttMediaSizeName;
  
  private int mAttMediaTray;
  
  private String mDestination = null;
  
  private Color mLastColor;
  
  private Color mLastTextColor;
  
  private String mLastFontFamily;
  
  private float mLastFontSize;
  
  private int mLastFontStyle;
  
  private int mLastRotation;
  
  private float mLastAwScale;
  
  private PrinterJob pjob;
  
  private ComponentPeer dialogOwnerPeer = null;
  
  private Object disposerReferent = new Object();
  
  private String lastNativeService = null;
  
  private boolean defaultCopies = true;
  
  public WPrinterJob() {
    Disposer.addRecord(this.disposerReferent, this.handleRecord = new HandleRecord());
    initAttributeMembers();
  }
  
  public Object getDisposerReferent() { return this.disposerReferent; }
  
  public PageFormat pageDialog(PageFormat paramPageFormat) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (!(getPrintService() instanceof Win32PrintService))
      return super.pageDialog(paramPageFormat); 
    PageFormat pageFormat = (PageFormat)paramPageFormat.clone();
    boolean bool = false;
    WPageDialog wPageDialog = new WPageDialog((Frame)null, this, pageFormat, null);
    wPageDialog.setRetVal(false);
    wPageDialog.setVisible(true);
    bool = wPageDialog.getRetVal();
    wPageDialog.dispose();
    if (bool && this.myService != null) {
      String str = getNativePrintService();
      if (!this.myService.getName().equals(str))
        try {
          setPrintService(Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(str));
        } catch (PrinterException printerException) {} 
      updatePageAttributes(this.myService, pageFormat);
      return pageFormat;
    } 
    return paramPageFormat;
  }
  
  private boolean displayNativeDialog() {
    if (this.attributes == null)
      return false; 
    DialogOwner dialogOwner = (DialogOwner)this.attributes.get(DialogOwner.class);
    Frame frame = (dialogOwner != null) ? dialogOwner.getOwner() : null;
    WPrintDialog wPrintDialog = new WPrintDialog(frame, this);
    wPrintDialog.setRetVal(false);
    wPrintDialog.setVisible(true);
    boolean bool = wPrintDialog.getRetVal();
    wPrintDialog.dispose();
    Destination destination = (Destination)this.attributes.get(Destination.class);
    if (destination == null || !bool)
      return bool; 
    String str1 = null;
    String str2 = "sun.print.resources.serviceui";
    ResourceBundle resourceBundle = ResourceBundle.getBundle(str2);
    try {
      str1 = resourceBundle.getString("dialog.printtofile");
    } catch (MissingResourceException missingResourceException) {}
    FileDialog fileDialog = new FileDialog(frame, str1, 1);
    URI uRI = destination.getURI();
    String str3 = (uRI != null) ? uRI.getSchemeSpecificPart() : null;
    if (str3 != null) {
      File file3 = new File(str3);
      fileDialog.setFile(file3.getName());
      File file4 = file3.getParentFile();
      if (file4 != null)
        fileDialog.setDirectory(file4.getPath()); 
    } else {
      fileDialog.setFile("out.prn");
    } 
    fileDialog.setVisible(true);
    String str4 = fileDialog.getFile();
    if (str4 == null) {
      fileDialog.dispose();
      return false;
    } 
    String str5 = fileDialog.getDirectory() + str4;
    File file1 = new File(str5);
    for (File file2 = file1.getParentFile(); (file1.exists() && (!file1.isFile() || !file1.canWrite())) || (file2 != null && (!file2.exists() || (file2.exists() && !file2.canWrite()))); file2 = file1.getParentFile()) {
      (new PrintToFileErrorDialog(frame, ServiceDialog.getMsg("dialog.owtitle"), ServiceDialog.getMsg("dialog.writeerror") + " " + str5, ServiceDialog.getMsg("button.ok"))).setVisible(true);
      fileDialog.setVisible(true);
      str4 = fileDialog.getFile();
      if (str4 == null) {
        fileDialog.dispose();
        return false;
      } 
      str5 = fileDialog.getDirectory() + str4;
      file1 = new File(str5);
    } 
    fileDialog.dispose();
    this.attributes.add(new Destination(file1.toURI()));
    return true;
  }
  
  public boolean printDialog() {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (this.attributes == null)
      this.attributes = new HashPrintRequestAttributeSet(); 
    return !(getPrintService() instanceof Win32PrintService) ? printDialog(this.attributes) : ((this.noDefaultPrinter == true) ? 0 : displayNativeDialog());
  }
  
  public void setPrintService(PrintService paramPrintService) throws PrinterException {
    super.setPrintService(paramPrintService);
    if (!(paramPrintService instanceof Win32PrintService))
      return; 
    this.driverDoesMultipleCopies = false;
    this.driverDoesCollation = false;
    setNativePrintServiceIfNeeded(paramPrintService.getName());
  }
  
  private native void setNativePrintService(String paramString) throws PrinterException;
  
  private void setNativePrintServiceIfNeeded(String paramString) throws PrinterException {
    if (paramString != null && !paramString.equals(this.lastNativeService)) {
      setNativePrintService(paramString);
      this.lastNativeService = paramString;
    } 
  }
  
  public PrintService getPrintService() {
    if (this.myService == null) {
      String str = getNativePrintService();
      if (str != null) {
        this.myService = Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(str);
        if (this.myService != null)
          return this.myService; 
      } 
      this.myService = PrintServiceLookup.lookupDefaultPrintService();
      if (this.myService instanceof Win32PrintService)
        try {
          setNativePrintServiceIfNeeded(this.myService.getName());
        } catch (Exception exception) {
          this.myService = null;
        }  
    } 
    return this.myService;
  }
  
  private native String getNativePrintService();
  
  private void initAttributeMembers() {
    this.mAttSides = 0;
    this.mAttChromaticity = 0;
    this.mAttXRes = 0;
    this.mAttYRes = 0;
    this.mAttQuality = 0;
    this.mAttCollate = -1;
    this.mAttCopies = 0;
    this.mAttMediaTray = 0;
    this.mAttMediaSizeName = 0;
    this.mDestination = null;
  }
  
  protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException {
    initAttributeMembers();
    super.setAttributes(paramPrintRequestAttributeSet);
    this.mAttCopies = getCopiesInt();
    this.mDestination = this.destinationAttr;
    if (paramPrintRequestAttributeSet == null)
      return; 
    Attribute[] arrayOfAttribute = paramPrintRequestAttributeSet.toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      Attribute attribute = arrayOfAttribute[b];
      try {
        if (attribute.getCategory() == Sides.class) {
          setSidesAttrib(attribute);
        } else if (attribute.getCategory() == Chromaticity.class) {
          setColorAttrib(attribute);
        } else if (attribute.getCategory() == PrinterResolution.class) {
          setResolutionAttrib(attribute);
        } else if (attribute.getCategory() == PrintQuality.class) {
          setQualityAttrib(attribute);
        } else if (attribute.getCategory() == SheetCollate.class) {
          setCollateAttrib(attribute);
        } else if (attribute.getCategory() == Media.class || attribute.getCategory() == SunAlternateMedia.class) {
          if (attribute.getCategory() == SunAlternateMedia.class) {
            Media media = (Media)paramPrintRequestAttributeSet.get(Media.class);
            if (media == null || !(media instanceof MediaTray))
              attribute = ((SunAlternateMedia)attribute).getMedia(); 
          } 
          if (attribute instanceof MediaSizeName)
            setWin32MediaAttrib(attribute); 
          if (attribute instanceof MediaTray)
            setMediaTrayAttrib(attribute); 
        } 
      } catch (ClassCastException classCastException) {}
    } 
  }
  
  private native void getDefaultPage(PageFormat paramPageFormat);
  
  public PageFormat defaultPage(PageFormat paramPageFormat) throws HeadlessException {
    PageFormat pageFormat = (PageFormat)paramPageFormat.clone();
    getDefaultPage(pageFormat);
    return pageFormat;
  }
  
  protected native void validatePaper(Paper paramPaper1, Paper paramPaper2);
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt) {
    WPathGraphics wPathGraphics;
    PeekMetrics peekMetrics = paramPeekGraphics.getMetrics();
    if (!forcePDL && (forceRaster == true || peekMetrics.hasNonSolidColors() || peekMetrics.hasCompositing())) {
      wPathGraphics = null;
    } else {
      BufferedImage bufferedImage = new BufferedImage(8, 8, 1);
      Graphics2D graphics2D = bufferedImage.createGraphics();
      boolean bool = !paramPeekGraphics.getAWTDrawingOnly();
      wPathGraphics = new WPathGraphics(graphics2D, paramPrinterJob, paramPrintable, paramPageFormat, paramInt, bool);
    } 
    return wPathGraphics;
  }
  
  protected double getXRes() { return (this.mAttXRes != 0) ? this.mAttXRes : this.mPrintXRes; }
  
  protected double getYRes() { return (this.mAttYRes != 0) ? this.mAttYRes : this.mPrintYRes; }
  
  protected double getPhysicalPrintableX(Paper paramPaper) { return this.mPrintPhysX; }
  
  protected double getPhysicalPrintableY(Paper paramPaper) { return this.mPrintPhysY; }
  
  protected double getPhysicalPrintableWidth(Paper paramPaper) { return this.mPrintWidth; }
  
  protected double getPhysicalPrintableHeight(Paper paramPaper) { return this.mPrintHeight; }
  
  protected double getPhysicalPageWidth(Paper paramPaper) { return this.mPageWidth; }
  
  protected double getPhysicalPageHeight(Paper paramPaper) { return this.mPageHeight; }
  
  protected boolean isCollated() { return this.userRequestedCollation; }
  
  protected int getCollatedCopies() {
    debug_println("driverDoesMultipleCopies=" + this.driverDoesMultipleCopies + " driverDoesCollation=" + this.driverDoesCollation);
    if (super.isCollated() && !this.driverDoesCollation) {
      this.mAttCollate = 0;
      this.mAttCopies = 1;
      return getCopies();
    } 
    return 1;
  }
  
  protected int getNoncollatedCopies() { return (this.driverDoesMultipleCopies || super.isCollated()) ? 1 : getCopies(); }
  
  private long getPrintDC() { return this.handleRecord.mPrintDC; }
  
  private void setPrintDC(long paramLong) { this.handleRecord.mPrintDC = paramLong; }
  
  private long getDevMode() { return this.handleRecord.mPrintHDevMode; }
  
  private void setDevMode(long paramLong) { this.handleRecord.mPrintHDevMode = paramLong; }
  
  private long getDevNames() { return this.handleRecord.mPrintHDevNames; }
  
  private void setDevNames(long paramLong) { this.handleRecord.mPrintHDevNames = paramLong; }
  
  protected void beginPath() { beginPath(getPrintDC()); }
  
  protected void endPath() { endPath(getPrintDC()); }
  
  protected void closeFigure() { closeFigure(getPrintDC()); }
  
  protected void fillPath() { fillPath(getPrintDC()); }
  
  protected void moveTo(float paramFloat1, float paramFloat2) { moveTo(getPrintDC(), paramFloat1, paramFloat2); }
  
  protected void lineTo(float paramFloat1, float paramFloat2) { lineTo(getPrintDC(), paramFloat1, paramFloat2); }
  
  protected void polyBezierTo(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6) { polyBezierTo(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6); }
  
  protected void setPolyFillMode(int paramInt) { setPolyFillMode(getPrintDC(), paramInt); }
  
  protected void selectSolidBrush(Color paramColor) {
    if (!paramColor.equals(this.mLastColor)) {
      this.mLastColor = paramColor;
      float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
      selectSolidBrush(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
    } 
  }
  
  protected int getPenX() { return getPenX(getPrintDC()); }
  
  protected int getPenY() { return getPenY(getPrintDC()); }
  
  protected void selectClipPath() { selectClipPath(getPrintDC()); }
  
  protected void frameRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) { frameRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4); }
  
  protected void fillRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, Color paramColor) {
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    fillRect(getPrintDC(), paramFloat1, paramFloat2, paramFloat3, paramFloat4, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected void selectPen(float paramFloat, Color paramColor) {
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    selectPen(getPrintDC(), paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected boolean selectStylePen(int paramInt1, int paramInt2, float paramFloat, Color paramColor) {
    long l1;
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    switch (paramInt1) {
      case 0:
        l1 = 512L;
        break;
      case 1:
        l1 = 0L;
        break;
      default:
        l1 = 256L;
        break;
    } 
    switch (paramInt2) {
      case 2:
        l2 = 4096L;
        return selectStylePen(getPrintDC(), l1, l2, paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
      default:
        l2 = 8192L;
        return selectStylePen(getPrintDC(), l1, l2, paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
      case 1:
        break;
    } 
    long l2 = 0L;
    return selectStylePen(getPrintDC(), l1, l2, paramFloat, (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
  }
  
  protected boolean setFont(String paramString, float paramFloat1, int paramInt1, int paramInt2, float paramFloat2) {
    boolean bool = true;
    if (!paramString.equals(this.mLastFontFamily) || paramFloat1 != this.mLastFontSize || paramInt1 != this.mLastFontStyle || paramInt2 != this.mLastRotation || paramFloat2 != this.mLastAwScale) {
      bool = setFont(getPrintDC(), paramString, paramFloat1, ((paramInt1 & true) != 0), ((paramInt1 & 0x2) != 0), paramInt2, paramFloat2);
      if (bool) {
        this.mLastFontFamily = paramString;
        this.mLastFontSize = paramFloat1;
        this.mLastFontStyle = paramInt1;
        this.mLastRotation = paramInt2;
        this.mLastAwScale = paramFloat2;
      } 
    } 
    return bool;
  }
  
  protected void setTextColor(Color paramColor) {
    if (!paramColor.equals(this.mLastTextColor)) {
      this.mLastTextColor = paramColor;
      float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
      setTextColor(getPrintDC(), (int)(arrayOfFloat[0] * 255.0F), (int)(arrayOfFloat[1] * 255.0F), (int)(arrayOfFloat[2] * 255.0F));
    } 
  }
  
  protected String removeControlChars(String paramString) { return super.removeControlChars(paramString); }
  
  protected void textOut(String paramString, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat) {
    String str = removeControlChars(paramString);
    assert paramArrayOfFloat == null || str.length() == paramString.length();
    if (str.length() == 0)
      return; 
    textOut(getPrintDC(), str, str.length(), false, paramFloat1, paramFloat2, paramArrayOfFloat);
  }
  
  protected void glyphsOut(int[] paramArrayOfInt, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat) {
    char[] arrayOfChar = new char[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      arrayOfChar[b] = (char)(paramArrayOfInt[b] & 0xFFFF); 
    String str = new String(arrayOfChar);
    textOut(getPrintDC(), str, paramArrayOfInt.length, true, paramFloat1, paramFloat2, paramArrayOfFloat);
  }
  
  protected int getGDIAdvance(String paramString) {
    paramString = removeControlChars(paramString);
    return (paramString.length() == 0) ? 0 : getGDIAdvance(getPrintDC(), paramString);
  }
  
  protected void drawImage3ByteBGR(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8) { drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, 24, null); }
  
  protected void drawDIBImage(byte[] paramArrayOfByte, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, IndexColorModel paramIndexColorModel) {
    int i = 24;
    byte[] arrayOfByte = null;
    if (paramIndexColorModel != null) {
      i = paramInt;
      arrayOfByte = new byte[(1 << paramIndexColorModel.getPixelSize()) * 4];
      for (byte b = 0; b < paramIndexColorModel.getMapSize(); b++) {
        arrayOfByte[b * 4 + 0] = (byte)(paramIndexColorModel.getBlue(b) & 0xFF);
        arrayOfByte[b * 4 + 1] = (byte)(paramIndexColorModel.getGreen(b) & 0xFF);
        arrayOfByte[b * 4 + 2] = (byte)(paramIndexColorModel.getRed(b) & 0xFF);
      } 
    } 
    drawDIBImage(getPrintDC(), paramArrayOfByte, paramFloat1, paramFloat2, paramFloat3, paramFloat4, paramFloat5, paramFloat6, paramFloat7, paramFloat8, i, arrayOfByte);
  }
  
  protected void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean) {
    invalidateCachedState();
    deviceStartPage(paramPageFormat, paramPrintable, paramInt, paramBoolean);
  }
  
  protected void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt) { deviceEndPage(paramPageFormat, paramPrintable, paramInt); }
  
  private void invalidateCachedState() {
    this.mLastColor = null;
    this.mLastTextColor = null;
    this.mLastFontFamily = null;
  }
  
  public void setCopies(int paramInt) {
    super.setCopies(paramInt);
    this.defaultCopies = false;
    this.mAttCopies = paramInt;
    setNativeCopies(paramInt);
  }
  
  private native void setNativeCopies(int paramInt);
  
  private native boolean jobSetup(Pageable paramPageable, boolean paramBoolean);
  
  protected native void initPrinter();
  
  private native boolean _startDoc(String paramString1, String paramString2) throws PrinterException;
  
  protected void startDoc() {
    if (!_startDoc(this.mDestination, getJobName()))
      cancel(); 
  }
  
  protected native void endDoc();
  
  protected native void abortDoc();
  
  private static native void deleteDC(long paramLong1, long paramLong2, long paramLong3);
  
  protected native void deviceStartPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean);
  
  protected native void deviceEndPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt);
  
  protected native void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  protected native void beginPath(long paramLong);
  
  protected native void endPath(long paramLong);
  
  protected native void closeFigure(long paramLong);
  
  protected native void fillPath(long paramLong);
  
  protected native void moveTo(long paramLong, float paramFloat1, float paramFloat2);
  
  protected native void lineTo(long paramLong, float paramFloat1, float paramFloat2);
  
  protected native void polyBezierTo(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6);
  
  protected native void setPolyFillMode(long paramLong, int paramInt);
  
  protected native void selectSolidBrush(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  protected native int getPenX(long paramLong);
  
  protected native int getPenY(long paramLong);
  
  protected native void selectClipPath(long paramLong);
  
  protected native void frameRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  protected native void fillRect(long paramLong, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2, int paramInt3);
  
  protected native void selectPen(long paramLong, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
  
  protected native boolean selectStylePen(long paramLong1, long paramLong2, long paramLong3, float paramFloat, int paramInt1, int paramInt2, int paramInt3);
  
  protected native boolean setFont(long paramLong, String paramString, float paramFloat1, boolean paramBoolean1, boolean paramBoolean2, int paramInt, float paramFloat2);
  
  protected native void setTextColor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
  
  protected native void textOut(long paramLong, String paramString, int paramInt, boolean paramBoolean, float paramFloat1, float paramFloat2, float[] paramArrayOfFloat);
  
  private native int getGDIAdvance(long paramLong, String paramString);
  
  private native void drawDIBImage(long paramLong, byte[] paramArrayOfByte1, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8, int paramInt, byte[] paramArrayOfByte2);
  
  private final String getPrinterAttrib() {
    PrintService printService = getPrintService();
    return (printService != null) ? printService.getName() : null;
  }
  
  private final int getCollateAttrib() { return this.mAttCollate; }
  
  private void setCollateAttrib(Attribute paramAttribute) {
    if (paramAttribute == SheetCollate.COLLATED) {
      this.mAttCollate = 1;
    } else {
      this.mAttCollate = 0;
    } 
  }
  
  private void setCollateAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    setCollateAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getOrientAttrib() {
    byte b = 1;
    OrientationRequested orientationRequested = (this.attributes == null) ? null : (OrientationRequested)this.attributes.get(OrientationRequested.class);
    if (orientationRequested == null)
      orientationRequested = (OrientationRequested)this.myService.getDefaultAttributeValue(OrientationRequested.class); 
    if (orientationRequested != null)
      if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
        b = 2;
      } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
        b = 0;
      }  
    return b;
  }
  
  private void setOrientAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    if (paramPrintRequestAttributeSet != null)
      paramPrintRequestAttributeSet.add(paramAttribute); 
  }
  
  private final int getCopiesAttrib() { return this.defaultCopies ? 0 : getCopiesInt(); }
  
  private final void setRangeCopiesAttribute(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3) {
    if (this.attributes != null) {
      if (paramBoolean) {
        this.attributes.add(new PageRanges(paramInt1, paramInt2));
        setPageRange(paramInt1, paramInt2);
      } 
      this.defaultCopies = false;
      this.attributes.add(new Copies(paramInt3));
      super.setCopies(paramInt3);
      this.mAttCopies = paramInt3;
    } 
  }
  
  private final boolean getDestAttrib() { return (this.mDestination != null); }
  
  private final int getQualityAttrib() { return this.mAttQuality; }
  
  private void setQualityAttrib(Attribute paramAttribute) {
    if (paramAttribute == PrintQuality.HIGH) {
      this.mAttQuality = -4;
    } else if (paramAttribute == PrintQuality.NORMAL) {
      this.mAttQuality = -3;
    } else {
      this.mAttQuality = -2;
    } 
  }
  
  private void setQualityAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    setQualityAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getColorAttrib() { return this.mAttChromaticity; }
  
  private void setColorAttrib(Attribute paramAttribute) {
    if (paramAttribute == Chromaticity.COLOR) {
      this.mAttChromaticity = 2;
    } else {
      this.mAttChromaticity = 1;
    } 
  }
  
  private void setColorAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    setColorAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int getSidesAttrib() { return this.mAttSides; }
  
  private void setSidesAttrib(Attribute paramAttribute) {
    if (paramAttribute == Sides.TWO_SIDED_LONG_EDGE) {
      this.mAttSides = 2;
    } else if (paramAttribute == Sides.TWO_SIDED_SHORT_EDGE) {
      this.mAttSides = 3;
    } else {
      this.mAttSides = 1;
    } 
  }
  
  private void setSidesAttrib(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    setSidesAttrib(paramAttribute);
    paramPrintRequestAttributeSet.add(paramAttribute);
  }
  
  private final int[] getWin32MediaAttrib() {
    int[] arrayOfInt = { 0, 0 };
    if (this.attributes != null) {
      Media media = (Media)this.attributes.get(Media.class);
      if (media instanceof MediaSizeName) {
        MediaSizeName mediaSizeName = (MediaSizeName)media;
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        if (mediaSize != null) {
          arrayOfInt[0] = (int)(mediaSize.getX(25400) * 72.0D);
          arrayOfInt[1] = (int)(mediaSize.getY(25400) * 72.0D);
        } 
      } 
    } 
    return arrayOfInt;
  }
  
  private void setWin32MediaAttrib(Attribute paramAttribute) {
    if (!(paramAttribute instanceof MediaSizeName))
      return; 
    MediaSizeName mediaSizeName = (MediaSizeName)paramAttribute;
    this.mAttMediaSizeName = ((Win32PrintService)this.myService).findPaperID(mediaSizeName);
  }
  
  private void addPaperSize(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, int paramInt3) {
    if (paramPrintRequestAttributeSet == null)
      return; 
    MediaSizeName mediaSizeName = ((Win32PrintService)this.myService).findWin32Media(paramInt1);
    if (mediaSizeName == null)
      mediaSizeName = ((Win32PrintService)this.myService).findMatchingMediaSizeNameMM(paramInt2, paramInt3); 
    if (mediaSizeName != null)
      paramPrintRequestAttributeSet.add(mediaSizeName); 
  }
  
  private void setWin32MediaAttrib(int paramInt1, int paramInt2, int paramInt3) {
    addPaperSize(this.attributes, paramInt1, paramInt2, paramInt3);
    this.mAttMediaSizeName = paramInt1;
  }
  
  private void setMediaTrayAttrib(Attribute paramAttribute) {
    if (paramAttribute == MediaTray.BOTTOM) {
      this.mAttMediaTray = 2;
    } else if (paramAttribute == MediaTray.ENVELOPE) {
      this.mAttMediaTray = 5;
    } else if (paramAttribute == MediaTray.LARGE_CAPACITY) {
      this.mAttMediaTray = 11;
    } else if (paramAttribute == MediaTray.MAIN) {
      this.mAttMediaTray = 1;
    } else if (paramAttribute == MediaTray.MANUAL) {
      this.mAttMediaTray = 4;
    } else if (paramAttribute == MediaTray.MIDDLE) {
      this.mAttMediaTray = 3;
    } else if (paramAttribute == MediaTray.SIDE) {
      this.mAttMediaTray = 7;
    } else if (paramAttribute == MediaTray.TOP) {
      this.mAttMediaTray = 1;
    } else if (paramAttribute instanceof Win32MediaTray) {
      this.mAttMediaTray = ((Win32MediaTray)paramAttribute).winID;
    } else {
      this.mAttMediaTray = 1;
    } 
  }
  
  private void setMediaTrayAttrib(int paramInt) {
    this.mAttMediaTray = paramInt;
    MediaTray mediaTray = ((Win32PrintService)this.myService).findMediaTray(paramInt);
  }
  
  private int getMediaTrayAttrib() { return this.mAttMediaTray; }
  
  private final boolean getPrintToFileEnabled() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      FilePermission filePermission = new FilePermission("<<ALL FILES>>", "read,write");
      try {
        securityManager.checkPermission(filePermission);
      } catch (SecurityException securityException) {
        return false;
      } 
    } 
    return true;
  }
  
  private final void setNativeAttributes(int paramInt1, int paramInt2, int paramInt3) {
    if (this.attributes == null)
      return; 
    if ((paramInt1 & 0x20) != 0) {
      Destination destination = (Destination)this.attributes.get(Destination.class);
      if (destination == null)
        try {
          this.attributes.add(new Destination((new File("./out.prn")).toURI()));
        } catch (SecurityException securityException) {
          try {
            this.attributes.add(new Destination(new URI("file:out.prn")));
          } catch (URISyntaxException uRISyntaxException) {}
        }  
    } else {
      this.attributes.remove(Destination.class);
    } 
    if ((paramInt1 & 0x10) != 0) {
      setCollateAttrib(SheetCollate.COLLATED, this.attributes);
    } else {
      setCollateAttrib(SheetCollate.UNCOLLATED, this.attributes);
    } 
    if ((paramInt1 & 0x2) != 0) {
      this.attributes.add(SunPageSelection.RANGE);
    } else if ((paramInt1 & true) != 0) {
      this.attributes.add(SunPageSelection.SELECTION);
    } else {
      this.attributes.add(SunPageSelection.ALL);
    } 
    if ((paramInt2 & true) != 0)
      if ((paramInt3 & 0x4000) != 0) {
        setOrientAttrib(OrientationRequested.LANDSCAPE, this.attributes);
      } else {
        setOrientAttrib(OrientationRequested.PORTRAIT, this.attributes);
      }  
    if ((paramInt2 & 0x800) != 0)
      if ((paramInt3 & 0x200) != 0) {
        setColorAttrib(Chromaticity.COLOR, this.attributes);
      } else {
        setColorAttrib(Chromaticity.MONOCHROME, this.attributes);
      }  
    if ((paramInt2 & 0x400) != 0) {
      PrintQuality printQuality;
      if ((paramInt3 & 0x80) != 0) {
        printQuality = PrintQuality.DRAFT;
      } else if ((paramInt2 & 0x40) != 0) {
        printQuality = PrintQuality.HIGH;
      } else {
        printQuality = PrintQuality.NORMAL;
      } 
      setQualityAttrib(printQuality, this.attributes);
    } 
    if ((paramInt2 & 0x1000) != 0) {
      Sides sides;
      if ((paramInt3 & 0x10) != 0) {
        sides = Sides.TWO_SIDED_LONG_EDGE;
      } else if ((paramInt3 & 0x20) != 0) {
        sides = Sides.TWO_SIDED_SHORT_EDGE;
      } else {
        sides = Sides.ONE_SIDED;
      } 
      setSidesAttrib(sides, this.attributes);
    } 
  }
  
  private void getDevModeValues(PrintRequestAttributeSet paramPrintRequestAttributeSet, DevModeValues paramDevModeValues) {
    Copies copies = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
    if (copies != null) {
      paramDevModeValues.dmFields |= 0x100;
      paramDevModeValues.copies = (short)copies.getValue();
    } 
    SheetCollate sheetCollate = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
    if (sheetCollate != null) {
      paramDevModeValues.dmFields |= 0x8000;
      paramDevModeValues.collate = (sheetCollate == SheetCollate.COLLATED) ? 1 : 0;
    } 
    Chromaticity chromaticity = (Chromaticity)paramPrintRequestAttributeSet.get(Chromaticity.class);
    if (chromaticity != null) {
      paramDevModeValues.dmFields |= 0x800;
      if (chromaticity == Chromaticity.COLOR) {
        paramDevModeValues.color = 2;
      } else {
        paramDevModeValues.color = 1;
      } 
    } 
    Sides sides = (Sides)paramPrintRequestAttributeSet.get(Sides.class);
    if (sides != null) {
      paramDevModeValues.dmFields |= 0x1000;
      if (sides == Sides.TWO_SIDED_LONG_EDGE) {
        paramDevModeValues.duplex = 2;
      } else if (sides == Sides.TWO_SIDED_SHORT_EDGE) {
        paramDevModeValues.duplex = 3;
      } else {
        paramDevModeValues.duplex = 1;
      } 
    } 
    OrientationRequested orientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if (orientationRequested != null) {
      paramDevModeValues.dmFields |= 0x1;
      paramDevModeValues.orient = (orientationRequested == OrientationRequested.LANDSCAPE) ? 2 : 1;
    } 
    Media media = (Media)paramPrintRequestAttributeSet.get(Media.class);
    if (media instanceof MediaSizeName) {
      paramDevModeValues.dmFields |= 0x2;
      MediaSizeName mediaSizeName = (MediaSizeName)media;
      paramDevModeValues.paper = (short)((Win32PrintService)this.myService).findPaperID(mediaSizeName);
    } 
    MediaTray mediaTray = null;
    if (media instanceof MediaTray)
      mediaTray = (MediaTray)media; 
    if (mediaTray == null) {
      SunAlternateMedia sunAlternateMedia = (SunAlternateMedia)paramPrintRequestAttributeSet.get(SunAlternateMedia.class);
      if (sunAlternateMedia != null && sunAlternateMedia.getMedia() instanceof MediaTray)
        mediaTray = (MediaTray)sunAlternateMedia.getMedia(); 
    } 
    if (mediaTray != null) {
      paramDevModeValues.dmFields |= 0x200;
      paramDevModeValues.bin = (short)((Win32PrintService)this.myService).findTrayID(mediaTray);
    } 
    PrintQuality printQuality = (PrintQuality)paramPrintRequestAttributeSet.get(PrintQuality.class);
    if (printQuality != null) {
      paramDevModeValues.dmFields |= 0x400;
      if (printQuality == PrintQuality.DRAFT) {
        paramDevModeValues.xres_quality = -1;
      } else if (printQuality == PrintQuality.HIGH) {
        paramDevModeValues.xres_quality = -4;
      } else {
        paramDevModeValues.xres_quality = -3;
      } 
    } 
    PrinterResolution printerResolution = (PrinterResolution)paramPrintRequestAttributeSet.get(PrinterResolution.class);
    if (printerResolution != null) {
      paramDevModeValues.dmFields |= 0x2400;
      paramDevModeValues.xres_quality = (short)printerResolution.getCrossFeedResolution(100);
      paramDevModeValues.yres = (short)printerResolution.getFeedResolution(100);
    } 
  }
  
  private final void setJobAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt1, int paramInt2, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7) {
    if (paramPrintRequestAttributeSet == null)
      return; 
    if ((paramInt1 & 0x100) != 0)
      paramPrintRequestAttributeSet.add(new Copies(paramShort1)); 
    if ((paramInt1 & 0x8000) != 0)
      if ((paramInt2 & 0x8000) != 0) {
        paramPrintRequestAttributeSet.add(SheetCollate.COLLATED);
      } else {
        paramPrintRequestAttributeSet.add(SheetCollate.UNCOLLATED);
      }  
    if ((paramInt1 & true) != 0)
      if ((paramInt2 & 0x4000) != 0) {
        paramPrintRequestAttributeSet.add(OrientationRequested.LANDSCAPE);
      } else {
        paramPrintRequestAttributeSet.add(OrientationRequested.PORTRAIT);
      }  
    if ((paramInt1 & 0x800) != 0)
      if ((paramInt2 & 0x200) != 0) {
        paramPrintRequestAttributeSet.add(Chromaticity.COLOR);
      } else {
        paramPrintRequestAttributeSet.add(Chromaticity.MONOCHROME);
      }  
    if ((paramInt1 & 0x400) != 0)
      if (paramShort6 < 0) {
        PrintQuality printQuality;
        if ((paramInt2 & 0x80) != 0) {
          printQuality = PrintQuality.DRAFT;
        } else if ((paramInt1 & 0x40) != 0) {
          printQuality = PrintQuality.HIGH;
        } else {
          printQuality = PrintQuality.NORMAL;
        } 
        paramPrintRequestAttributeSet.add(printQuality);
      } else if (paramShort6 > 0 && paramShort7 > 0) {
        paramPrintRequestAttributeSet.add(new PrinterResolution(paramShort6, paramShort7, 100));
      }  
    if ((paramInt1 & 0x1000) != 0) {
      Sides sides;
      if ((paramInt2 & 0x10) != 0) {
        sides = Sides.TWO_SIDED_LONG_EDGE;
      } else if ((paramInt2 & 0x20) != 0) {
        sides = Sides.TWO_SIDED_SHORT_EDGE;
      } else {
        sides = Sides.ONE_SIDED;
      } 
      paramPrintRequestAttributeSet.add(sides);
    } 
    if ((paramInt1 & 0x2) != 0)
      addPaperSize(paramPrintRequestAttributeSet, paramShort2, paramShort3, paramShort4); 
    if ((paramInt1 & 0x200) != 0) {
      MediaTray mediaTray = ((Win32PrintService)this.myService).findMediaTray(paramShort5);
      paramPrintRequestAttributeSet.add(new SunAlternateMedia(mediaTray));
    } 
  }
  
  private native boolean showDocProperties(long paramLong, PrintRequestAttributeSet paramPrintRequestAttributeSet, int paramInt, short paramShort1, short paramShort2, short paramShort3, short paramShort4, short paramShort5, short paramShort6, short paramShort7, short paramShort8, short paramShort9);
  
  public PrintRequestAttributeSet showDocumentProperties(Window paramWindow, PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    try {
      setNativePrintServiceIfNeeded(paramPrintService.getName());
    } catch (PrinterException printerException) {}
    long l = ((WWindowPeer)paramWindow.getPeer()).getHWnd();
    DevModeValues devModeValues = new DevModeValues(null);
    getDevModeValues(paramPrintRequestAttributeSet, devModeValues);
    boolean bool = showDocProperties(l, paramPrintRequestAttributeSet, devModeValues.dmFields, devModeValues.copies, devModeValues.collate, devModeValues.color, devModeValues.duplex, devModeValues.orient, devModeValues.paper, devModeValues.bin, devModeValues.xres_quality, devModeValues.yres);
    return bool ? paramPrintRequestAttributeSet : null;
  }
  
  private final void setResolutionDPI(int paramInt1, int paramInt2) {
    if (this.attributes != null) {
      PrinterResolution printerResolution = new PrinterResolution(paramInt1, paramInt2, 100);
      this.attributes.add(printerResolution);
    } 
    this.mAttXRes = paramInt1;
    this.mAttYRes = paramInt2;
  }
  
  private void setResolutionAttrib(Attribute paramAttribute) {
    PrinterResolution printerResolution = (PrinterResolution)paramAttribute;
    this.mAttXRes = printerResolution.getCrossFeedResolution(100);
    this.mAttYRes = printerResolution.getFeedResolution(100);
  }
  
  private void setPrinterNameAttrib(String paramString) throws PrinterException {
    PrintService printService = getPrintService();
    if (paramString == null)
      return; 
    if (printService != null && paramString.equals(printService.getName()))
      return; 
    PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
    for (byte b = 0; b < arrayOfPrintService.length; b++) {
      if (paramString.equals(arrayOfPrintService[b].getName())) {
        try {
          setPrintService(arrayOfPrintService[b]);
        } catch (PrinterException printerException) {}
        return;
      } 
    } 
  }
  
  private static native void initIDs();
  
  static  {
    Toolkit.getDefaultToolkit();
    initIDs();
    Win32FontManager.registerJREFontsForPrinting();
  }
  
  private static final class DevModeValues {
    int dmFields;
    
    short copies;
    
    short collate;
    
    short color;
    
    short duplex;
    
    short orient;
    
    short paper;
    
    short bin;
    
    short xres_quality;
    
    short yres;
    
    private DevModeValues() {}
  }
  
  static class HandleRecord implements DisposerRecord {
    private long mPrintDC;
    
    private long mPrintHDevMode;
    
    private long mPrintHDevNames;
    
    public void dispose() { WPrinterJob.deleteDC(this.mPrintDC, this.mPrintHDevMode, this.mPrintHDevNames); }
  }
  
  class PrintToFileErrorDialog extends Dialog implements ActionListener {
    public PrintToFileErrorDialog(Frame param1Frame, String param1String1, String param1String2, String param1String3) {
      super(param1Frame, param1String1, true);
      init(param1Frame, param1String1, param1String2, param1String3);
    }
    
    public PrintToFileErrorDialog(Dialog param1Dialog, String param1String1, String param1String2, String param1String3) {
      super(param1Dialog, param1String1, true);
      init(param1Dialog, param1String1, param1String2, param1String3);
    }
    
    private void init(Component param1Component, String param1String1, String param1String2, String param1String3) {
      Panel panel = new Panel();
      add("Center", new Label(param1String2));
      Button button = new Button(param1String3);
      button.addActionListener(this);
      panel.add(button);
      add("South", panel);
      pack();
      Dimension dimension = getSize();
      if (param1Component != null) {
        Rectangle rectangle = param1Component.getBounds();
        setLocation(rectangle.x + (rectangle.width - dimension.width) / 2, rectangle.y + (rectangle.height - dimension.height) / 2);
      } 
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      setVisible(false);
      dispose();
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\windows\WPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
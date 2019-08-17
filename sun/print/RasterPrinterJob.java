package sun.print;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.StreamPrintServiceFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobSheets;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import sun.awt.image.ByteInterleavedRaster;
import sun.security.action.GetPropertyAction;

public abstract class RasterPrinterJob extends PrinterJob {
  protected static final int PRINTER = 0;
  
  protected static final int FILE = 1;
  
  protected static final int STREAM = 2;
  
  protected static final int MAX_UNKNOWN_PAGES = 9999;
  
  protected static final int PD_ALLPAGES = 0;
  
  protected static final int PD_SELECTION = 1;
  
  protected static final int PD_PAGENUMS = 2;
  
  protected static final int PD_NOSELECTION = 4;
  
  private static final int MAX_BAND_SIZE = 4194304;
  
  private static final float DPI = 72.0F;
  
  private static final String FORCE_PIPE_PROP = "sun.java2d.print.pipeline";
  
  private static final String FORCE_RASTER = "raster";
  
  private static final String FORCE_PDL = "pdl";
  
  private static final String SHAPE_TEXT_PROP = "sun.java2d.print.shapetext";
  
  public static boolean forcePDL = false;
  
  public static boolean forceRaster = false;
  
  public static boolean shapeTextProp = false;
  
  private int cachedBandWidth = 0;
  
  private int cachedBandHeight = 0;
  
  private BufferedImage cachedBand = null;
  
  private int mNumCopies = 1;
  
  private boolean mCollate = false;
  
  private int mFirstPage = -1;
  
  private int mLastPage = -1;
  
  private Paper previousPaper;
  
  protected Pageable mDocument = new Book();
  
  private String mDocName = "Java Printing";
  
  protected boolean performingPrinting = false;
  
  protected boolean userCancelled = false;
  
  private FilePermission printToFilePermission;
  
  private ArrayList redrawList = new ArrayList();
  
  private int copiesAttr;
  
  private String jobNameAttr;
  
  private String userNameAttr;
  
  private PageRanges pageRangesAttr;
  
  protected Sides sidesAttr;
  
  protected String destinationAttr;
  
  protected boolean noJobSheet = false;
  
  protected int mDestType = 1;
  
  protected String mDestination = "";
  
  protected boolean collateAttReq = false;
  
  protected boolean landscapeRotates270 = false;
  
  protected PrintRequestAttributeSet attributes = null;
  
  protected PrintService myService;
  
  public static boolean debugPrint;
  
  private int deviceWidth;
  
  private int deviceHeight;
  
  private AffineTransform defaultDeviceTransform;
  
  private PrinterGraphicsConfig pgConfig;
  
  private DialogOnTop onTop = null;
  
  private long parentWindowID = 0L;
  
  protected abstract double getXRes();
  
  protected abstract double getYRes();
  
  protected abstract double getPhysicalPrintableX(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableY(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableWidth(Paper paramPaper);
  
  protected abstract double getPhysicalPrintableHeight(Paper paramPaper);
  
  protected abstract double getPhysicalPageWidth(Paper paramPaper);
  
  protected abstract double getPhysicalPageHeight(Paper paramPaper);
  
  protected abstract void startPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt, boolean paramBoolean) throws PrinterException;
  
  protected abstract void endPage(PageFormat paramPageFormat, Printable paramPrintable, int paramInt) throws PrinterException;
  
  protected abstract void printBand(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws PrinterException;
  
  public void saveState(AffineTransform paramAffineTransform, Shape paramShape, Rectangle2D paramRectangle2D, double paramDouble1, double paramDouble2) {
    GraphicsState graphicsState = new GraphicsState(null);
    graphicsState.theTransform = paramAffineTransform;
    graphicsState.theClip = paramShape;
    graphicsState.region = paramRectangle2D;
    graphicsState.sx = paramDouble1;
    graphicsState.sy = paramDouble2;
    this.redrawList.add(graphicsState);
  }
  
  protected static PrintService lookupDefaultPrintService() {
    PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
    if (printService != null && printService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && printService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
      return printService; 
    PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
    return (arrayOfPrintService.length > 0) ? arrayOfPrintService[0] : null;
  }
  
  public PrintService getPrintService() {
    if (this.myService == null) {
      PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
      if (printService != null && printService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE))
        try {
          setPrintService(printService);
          this.myService = printService;
        } catch (PrinterException printerException) {} 
      if (this.myService == null) {
        PrintService[] arrayOfPrintService = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
        if (arrayOfPrintService.length > 0)
          try {
            setPrintService(arrayOfPrintService[0]);
            this.myService = arrayOfPrintService[0];
          } catch (PrinterException printerException) {} 
      } 
    } 
    return this.myService;
  }
  
  public void setPrintService(PrintService paramPrintService) throws PrinterException {
    if (paramPrintService == null)
      throw new PrinterException("Service cannot be null"); 
    if (!(paramPrintService instanceof javax.print.StreamPrintService) && paramPrintService.getName() == null)
      throw new PrinterException("Null PrintService name."); 
    PrinterState printerState = (PrinterState)paramPrintService.getAttribute(PrinterState.class);
    if (printerState == PrinterState.STOPPED) {
      PrinterStateReasons printerStateReasons = (PrinterStateReasons)paramPrintService.getAttribute(PrinterStateReasons.class);
      if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN))
        throw new PrinterException("PrintService is no longer available."); 
    } 
    if (paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && paramPrintService.isDocFlavorSupported(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
      this.myService = paramPrintService;
    } else {
      throw new PrinterException("Not a 2D print service: " + paramPrintService);
    } 
  }
  
  private PageFormat attributeToPageFormat(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    PageFormat pageFormat = defaultPage();
    if (paramPrintService == null)
      return pageFormat; 
    OrientationRequested orientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    if (orientationRequested == null)
      orientationRequested = (OrientationRequested)paramPrintService.getDefaultAttributeValue(OrientationRequested.class); 
    if (orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
      pageFormat.setOrientation(2);
    } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
      pageFormat.setOrientation(0);
    } else {
      pageFormat.setOrientation(1);
    } 
    Media media = (Media)paramPrintRequestAttributeSet.get(Media.class);
    MediaSize mediaSize = getMediaSize(media, paramPrintService, pageFormat);
    Paper paper = new Paper();
    float[] arrayOfFloat = mediaSize.getSize(1);
    double d1 = Math.rint(arrayOfFloat[0] * 72.0D / 25400.0D);
    double d2 = Math.rint(arrayOfFloat[1] * 72.0D / 25400.0D);
    paper.setSize(d1, d2);
    MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    if (mediaPrintableArea == null)
      mediaPrintableArea = getDefaultPrintableArea(pageFormat, d1, d2); 
    double d3 = Math.rint((mediaPrintableArea.getX(25400) * 72.0F));
    double d5 = Math.rint((mediaPrintableArea.getY(25400) * 72.0F));
    double d4 = Math.rint((mediaPrintableArea.getWidth(25400) * 72.0F));
    double d6 = Math.rint((mediaPrintableArea.getHeight(25400) * 72.0F));
    paper.setImageableArea(d3, d5, d4, d6);
    pageFormat.setPaper(paper);
    return pageFormat;
  }
  
  protected MediaSize getMediaSize(Media paramMedia, PrintService paramPrintService, PageFormat paramPageFormat) {
    if (paramMedia == null)
      paramMedia = (Media)paramPrintService.getDefaultAttributeValue(Media.class); 
    if (!(paramMedia instanceof MediaSizeName))
      paramMedia = MediaSizeName.NA_LETTER; 
    MediaSize mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)paramMedia);
    return (mediaSize != null) ? mediaSize : MediaSize.NA.LETTER;
  }
  
  protected MediaPrintableArea getDefaultPrintableArea(PageFormat paramPageFormat, double paramDouble1, double paramDouble2) {
    double d4;
    double d3;
    double d2;
    double d1;
    if (paramDouble1 >= 432.0D) {
      d1 = 72.0D;
      d2 = paramDouble1 - 144.0D;
    } else {
      d1 = paramDouble1 / 6.0D;
      d2 = paramDouble1 * 0.75D;
    } 
    if (paramDouble2 >= 432.0D) {
      d3 = 72.0D;
      d4 = paramDouble2 - 144.0D;
    } else {
      d3 = paramDouble2 / 6.0D;
      d4 = paramDouble2 * 0.75D;
    } 
    return new MediaPrintableArea((float)(d1 / 72.0D), (float)(d3 / 72.0D), (float)(d2 / 72.0D), (float)(d4 / 72.0D), 25400);
  }
  
  protected void updatePageAttributes(PrintService paramPrintService, PageFormat paramPageFormat) {
    if (this.attributes == null)
      this.attributes = new HashPrintRequestAttributeSet(); 
    updateAttributesWithPageFormat(paramPrintService, paramPageFormat, this.attributes);
  }
  
  protected void updateAttributesWithPageFormat(PrintService paramPrintService, PageFormat paramPageFormat, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    OrientationRequested orientationRequested;
    if (paramPrintService == null || paramPageFormat == null || paramPrintRequestAttributeSet == null)
      return; 
    float f1 = (float)Math.rint(paramPageFormat.getPaper().getWidth() * 25400.0D / 72.0D) / 25400.0F;
    float f2 = (float)Math.rint(paramPageFormat.getPaper().getHeight() * 25400.0D / 72.0D) / 25400.0F;
    Media[] arrayOfMedia = (Media[])paramPrintService.getSupportedAttributeValues(Media.class, null, null);
    Media media = null;
    try {
      media = CustomMediaSizeName.findMedia(arrayOfMedia, f1, f2, 25400);
    } catch (IllegalArgumentException illegalArgumentException) {}
    if (media == null || !paramPrintService.isAttributeValueSupported(media, null, null))
      media = (Media)paramPrintService.getDefaultAttributeValue(Media.class); 
    switch (paramPageFormat.getOrientation()) {
      case 0:
        orientationRequested = OrientationRequested.LANDSCAPE;
        break;
      case 2:
        orientationRequested = OrientationRequested.REVERSE_LANDSCAPE;
        break;
      default:
        orientationRequested = OrientationRequested.PORTRAIT;
        break;
    } 
    if (media != null)
      paramPrintRequestAttributeSet.add(media); 
    paramPrintRequestAttributeSet.add(orientationRequested);
    float f3 = (float)(paramPageFormat.getPaper().getImageableX() / 72.0D);
    float f4 = (float)(paramPageFormat.getPaper().getImageableWidth() / 72.0D);
    float f5 = (float)(paramPageFormat.getPaper().getImageableY() / 72.0D);
    float f6 = (float)(paramPageFormat.getPaper().getImageableHeight() / 72.0D);
    if (f3 < 0.0F)
      f3 = 0.0F; 
    if (f5 < 0.0F)
      f5 = 0.0F; 
    try {
      paramPrintRequestAttributeSet.add(new MediaPrintableArea(f3, f5, f4, f6, 25400));
    } catch (IllegalArgumentException illegalArgumentException) {}
  }
  
  public PageFormat pageDialog(PageFormat paramPageFormat) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    PrintService printService = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            PrintService printService = RasterPrinterJob.this.getPrintService();
            if (printService == null) {
              ServiceDialog.showNoPrintService(gc);
              return null;
            } 
            return printService;
          }
        });
    if (printService == null)
      return paramPageFormat; 
    updatePageAttributes(printService, paramPageFormat);
    PageFormat pageFormat = null;
    DialogTypeSelection dialogTypeSelection = (DialogTypeSelection)this.attributes.get(DialogTypeSelection.class);
    if (dialogTypeSelection == DialogTypeSelection.NATIVE) {
      this.attributes.remove(DialogTypeSelection.class);
      pageFormat = pageDialog(this.attributes);
      this.attributes.add(DialogTypeSelection.NATIVE);
    } else {
      pageFormat = pageDialog(this.attributes);
    } 
    return (pageFormat == null) ? paramPageFormat : pageFormat;
  }
  
  public PageFormat pageDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    DialogTypeSelection dialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
    if (dialogTypeSelection == DialogTypeSelection.NATIVE) {
      PrintService printService1 = getPrintService();
      PageFormat pageFormat1 = attributeToPageFormat(printService1, paramPrintRequestAttributeSet);
      setParentWindowID(paramPrintRequestAttributeSet);
      PageFormat pageFormat2 = pageDialog(pageFormat1);
      clearParentWindowID();
      if (pageFormat2 == pageFormat1)
        return null; 
      updateAttributesWithPageFormat(printService1, pageFormat2, paramPrintRequestAttributeSet);
      return pageFormat2;
    } 
    final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Rectangle rectangle = graphicsConfiguration.getBounds();
    int i = rectangle.x + rectangle.width / 3;
    int j = rectangle.y + rectangle.height / 3;
    PrintService printService = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            PrintService printService = RasterPrinterJob.this.getPrintService();
            if (printService == null) {
              ServiceDialog.showNoPrintService(gc);
              return null;
            } 
            return printService;
          }
        });
    if (printService == null)
      return null; 
    if (this.onTop != null)
      paramPrintRequestAttributeSet.add(this.onTop); 
    ServiceDialog serviceDialog = new ServiceDialog(graphicsConfiguration, i, j, printService, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet, (Frame)null);
    serviceDialog.show();
    if (serviceDialog.getStatus() == 1) {
      PrintRequestAttributeSet printRequestAttributeSet = serviceDialog.getAttributes();
      Class clazz = SunAlternateMedia.class;
      if (paramPrintRequestAttributeSet.containsKey(clazz) && !printRequestAttributeSet.containsKey(clazz))
        paramPrintRequestAttributeSet.remove(clazz); 
      paramPrintRequestAttributeSet.addAll(printRequestAttributeSet);
      return attributeToPageFormat(printService, paramPrintRequestAttributeSet);
    } 
    return null;
  }
  
  protected PageFormat getPageFormatFromAttributes() {
    if (this.attributes == null || this.attributes.isEmpty())
      return null; 
    PageFormat pageFormat1 = attributeToPageFormat(getPrintService(), this.attributes);
    PageFormat pageFormat2 = null;
    Pageable pageable = getPageable();
    if (pageable != null && pageable instanceof OpenBook && (pageFormat2 = pageable.getPageFormat(false)) != null) {
      if (this.attributes.get(OrientationRequested.class) == null)
        pageFormat1.setOrientation(pageFormat2.getOrientation()); 
      Paper paper1 = pageFormat1.getPaper();
      Paper paper2 = pageFormat2.getPaper();
      boolean bool = false;
      if (this.attributes.get(MediaSizeName.class) == null) {
        paper1.setSize(paper2.getWidth(), paper2.getHeight());
        bool = true;
      } 
      if (this.attributes.get(MediaPrintableArea.class) == null) {
        paper1.setImageableArea(paper2.getImageableX(), paper2.getImageableY(), paper2.getImageableWidth(), paper2.getImageableHeight());
        bool = true;
      } 
      if (bool)
        pageFormat1.setPaper(paper1); 
    } 
    return pageFormat1;
  }
  
  public boolean printDialog(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    PrintService printService2;
    PrintService[] arrayOfPrintService;
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    DialogTypeSelection dialogTypeSelection = (DialogTypeSelection)paramPrintRequestAttributeSet.get(DialogTypeSelection.class);
    if (dialogTypeSelection == DialogTypeSelection.NATIVE) {
      this.attributes = paramPrintRequestAttributeSet;
      try {
        debug_println("calling setAttributes in printDialog");
        setAttributes(paramPrintRequestAttributeSet);
      } catch (PrinterException printerException) {}
      setParentWindowID(paramPrintRequestAttributeSet);
      boolean bool = printDialog();
      clearParentWindowID();
      this.attributes = paramPrintRequestAttributeSet;
      return bool;
    } 
    final GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    PrintService printService1 = (PrintService)AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            PrintService printService = RasterPrinterJob.this.getPrintService();
            if (printService == null) {
              ServiceDialog.showNoPrintService(gc);
              return null;
            } 
            return printService;
          }
        });
    if (printService1 == null)
      return false; 
    StreamPrintServiceFactory[] arrayOfStreamPrintServiceFactory = null;
    if (printService1 instanceof javax.print.StreamPrintService) {
      arrayOfStreamPrintServiceFactory = lookupStreamPrintServices(null);
      arrayOfPrintService = new javax.print.StreamPrintService[arrayOfStreamPrintServiceFactory.length];
      for (byte b = 0; b < arrayOfStreamPrintServiceFactory.length; b++)
        arrayOfPrintService[b] = arrayOfStreamPrintServiceFactory[b].getPrintService(null); 
    } else {
      arrayOfPrintService = (PrintService[])AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() { return PrinterJob.lookupPrintServices(); }
          });
      if (arrayOfPrintService == null || arrayOfPrintService.length == 0) {
        arrayOfPrintService = new PrintService[1];
        arrayOfPrintService[0] = printService1;
      } 
    } 
    Rectangle rectangle = graphicsConfiguration.getBounds();
    int i = rectangle.x + rectangle.width / 3;
    int j = rectangle.y + rectangle.height / 3;
    PrinterJobWrapper printerJobWrapper = new PrinterJobWrapper(this);
    paramPrintRequestAttributeSet.add(printerJobWrapper);
    try {
      printService2 = ServiceUI.printDialog(graphicsConfiguration, i, j, arrayOfPrintService, printService1, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
    } catch (IllegalArgumentException illegalArgumentException) {
      printService2 = ServiceUI.printDialog(graphicsConfiguration, i, j, arrayOfPrintService, arrayOfPrintService[0], DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
    } 
    paramPrintRequestAttributeSet.remove(PrinterJobWrapper.class);
    if (printService2 == null)
      return false; 
    if (!printService1.equals(printService2))
      try {
        setPrintService(printService2);
      } catch (PrinterException printerException) {
        this.myService = printService2;
      }  
    return true;
  }
  
  public boolean printDialog() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    HashPrintRequestAttributeSet hashPrintRequestAttributeSet = new HashPrintRequestAttributeSet();
    hashPrintRequestAttributeSet.add(new Copies(getCopies()));
    hashPrintRequestAttributeSet.add(new JobName(getJobName(), null));
    boolean bool = printDialog(hashPrintRequestAttributeSet);
    if (bool) {
      JobName jobName = (JobName)hashPrintRequestAttributeSet.get(JobName.class);
      if (jobName != null)
        setJobName(jobName.getValue()); 
      Copies copies = (Copies)hashPrintRequestAttributeSet.get(Copies.class);
      if (copies != null)
        setCopies(copies.getValue()); 
      Destination destination = (Destination)hashPrintRequestAttributeSet.get(Destination.class);
      if (destination != null) {
        try {
          this.mDestType = 1;
          this.mDestination = (new File(destination.getURI())).getPath();
        } catch (Exception exception) {
          this.mDestination = "out.prn";
          PrintService printService = getPrintService();
          if (printService != null) {
            Destination destination1 = (Destination)printService.getDefaultAttributeValue(Destination.class);
            if (destination1 != null)
              this.mDestination = (new File(destination1.getURI())).getPath(); 
          } 
        } 
      } else {
        this.mDestType = 0;
        PrintService printService = getPrintService();
        if (printService != null)
          this.mDestination = printService.getName(); 
      } 
    } 
    return bool;
  }
  
  public void setPrintable(Printable paramPrintable) { setPageable(new OpenBook(defaultPage(new PageFormat()), paramPrintable)); }
  
  public void setPrintable(Printable paramPrintable, PageFormat paramPageFormat) {
    setPageable(new OpenBook(paramPageFormat, paramPrintable));
    updatePageAttributes(getPrintService(), paramPageFormat);
  }
  
  public void setPageable(Pageable paramPageable) throws NullPointerException {
    if (paramPageable != null) {
      this.mDocument = paramPageable;
    } else {
      throw new NullPointerException();
    } 
  }
  
  protected void initPrinter() {}
  
  protected boolean isSupportedValue(Attribute paramAttribute, PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    PrintService printService = getPrintService();
    return (paramAttribute != null && printService != null && printService.isAttributeValueSupported(paramAttribute, DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet));
  }
  
  protected void setAttributes(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException {
    setCollated(false);
    this.sidesAttr = null;
    this.pageRangesAttr = null;
    this.copiesAttr = 0;
    this.jobNameAttr = null;
    this.userNameAttr = null;
    this.destinationAttr = null;
    this.collateAttReq = false;
    PrintService printService = getPrintService();
    if (paramPrintRequestAttributeSet == null || printService == null)
      return; 
    boolean bool = false;
    Fidelity fidelity = (Fidelity)paramPrintRequestAttributeSet.get(Fidelity.class);
    if (fidelity != null && fidelity == Fidelity.FIDELITY_TRUE)
      bool = true; 
    if (bool == true) {
      AttributeSet attributeSet = printService.getUnsupportedAttributes(DocFlavor.SERVICE_FORMATTED.PAGEABLE, paramPrintRequestAttributeSet);
      if (attributeSet != null)
        throw new PrinterException("Fidelity cannot be satisfied"); 
    } 
    SheetCollate sheetCollate = (SheetCollate)paramPrintRequestAttributeSet.get(SheetCollate.class);
    if (isSupportedValue(sheetCollate, paramPrintRequestAttributeSet))
      setCollated((sheetCollate == SheetCollate.COLLATED)); 
    this.sidesAttr = (Sides)paramPrintRequestAttributeSet.get(Sides.class);
    if (!isSupportedValue(this.sidesAttr, paramPrintRequestAttributeSet))
      this.sidesAttr = Sides.ONE_SIDED; 
    this.pageRangesAttr = (PageRanges)paramPrintRequestAttributeSet.get(PageRanges.class);
    if (!isSupportedValue(this.pageRangesAttr, paramPrintRequestAttributeSet)) {
      this.pageRangesAttr = null;
    } else if ((SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class) == SunPageSelection.RANGE) {
      int[][] arrayOfInt = this.pageRangesAttr.getMembers();
      setPageRange(arrayOfInt[0][0] - 1, arrayOfInt[0][1] - 1);
    } else {
      setPageRange(-1, -1);
    } 
    Copies copies = (Copies)paramPrintRequestAttributeSet.get(Copies.class);
    if (isSupportedValue(copies, paramPrintRequestAttributeSet) || (!bool && copies != null)) {
      this.copiesAttr = copies.getValue();
      setCopies(this.copiesAttr);
    } else {
      this.copiesAttr = getCopies();
    } 
    Destination destination = (Destination)paramPrintRequestAttributeSet.get(Destination.class);
    if (isSupportedValue(destination, paramPrintRequestAttributeSet))
      try {
        this.destinationAttr = "" + new File(destination.getURI().getSchemeSpecificPart());
      } catch (Exception exception) {
        Destination destination1 = (Destination)printService.getDefaultAttributeValue(Destination.class);
        if (destination1 != null)
          this.destinationAttr = "" + new File(destination1.getURI().getSchemeSpecificPart()); 
      }  
    JobSheets jobSheets = (JobSheets)paramPrintRequestAttributeSet.get(JobSheets.class);
    if (jobSheets != null)
      this.noJobSheet = (jobSheets == JobSheets.NONE); 
    JobName jobName = (JobName)paramPrintRequestAttributeSet.get(JobName.class);
    if (isSupportedValue(jobName, paramPrintRequestAttributeSet) || (!bool && jobName != null)) {
      this.jobNameAttr = jobName.getValue();
      setJobName(this.jobNameAttr);
    } else {
      this.jobNameAttr = getJobName();
    } 
    RequestingUserName requestingUserName = (RequestingUserName)paramPrintRequestAttributeSet.get(RequestingUserName.class);
    if (isSupportedValue(requestingUserName, paramPrintRequestAttributeSet) || (!bool && requestingUserName != null)) {
      this.userNameAttr = requestingUserName.getValue();
    } else {
      try {
        this.userNameAttr = getUserName();
      } catch (SecurityException securityException) {
        this.userNameAttr = "";
      } 
    } 
    Media media = (Media)paramPrintRequestAttributeSet.get(Media.class);
    OrientationRequested orientationRequested = (OrientationRequested)paramPrintRequestAttributeSet.get(OrientationRequested.class);
    MediaPrintableArea mediaPrintableArea = (MediaPrintableArea)paramPrintRequestAttributeSet.get(MediaPrintableArea.class);
    if ((orientationRequested != null || media != null || mediaPrintableArea != null) && getPageable() instanceof OpenBook) {
      Pageable pageable = getPageable();
      Printable printable = pageable.getPrintable(0);
      PageFormat pageFormat = (PageFormat)pageable.getPageFormat(0).clone();
      Paper paper = pageFormat.getPaper();
      if (mediaPrintableArea == null && media != null && printService.isAttributeCategorySupported(MediaPrintableArea.class)) {
        Object object = printService.getSupportedAttributeValues(MediaPrintableArea.class, null, paramPrintRequestAttributeSet);
        if (object instanceof MediaPrintableArea[] && (MediaPrintableArea[])object.length > 0)
          mediaPrintableArea = (MediaPrintableArea[])object[0]; 
      } 
      if (isSupportedValue(orientationRequested, paramPrintRequestAttributeSet) || (!bool && orientationRequested != null)) {
        byte b;
        if (orientationRequested.equals(OrientationRequested.REVERSE_LANDSCAPE)) {
          b = 2;
        } else if (orientationRequested.equals(OrientationRequested.LANDSCAPE)) {
          b = 0;
        } else {
          b = 1;
        } 
        pageFormat.setOrientation(b);
      } 
      if ((isSupportedValue(media, paramPrintRequestAttributeSet) || (!bool && media != null)) && media instanceof MediaSizeName) {
        MediaSizeName mediaSizeName = (MediaSizeName)media;
        MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        if (mediaSize != null) {
          float f1 = mediaSize.getX(25400) * 72.0F;
          float f2 = mediaSize.getY(25400) * 72.0F;
          paper.setSize(f1, f2);
          if (mediaPrintableArea == null)
            paper.setImageableArea(72.0D, 72.0D, f1 - 144.0D, f2 - 144.0D); 
        } 
      } 
      if (isSupportedValue(mediaPrintableArea, paramPrintRequestAttributeSet) || (!bool && mediaPrintableArea != null)) {
        float[] arrayOfFloat = mediaPrintableArea.getPrintableArea(25400);
        for (byte b = 0; b < arrayOfFloat.length; b++)
          arrayOfFloat[b] = arrayOfFloat[b] * 72.0F; 
        paper.setImageableArea(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3]);
      } 
      pageFormat.setPaper(paper);
      pageFormat = validatePage(pageFormat);
      setPrintable(printable, pageFormat);
    } else {
      this.attributes = paramPrintRequestAttributeSet;
    } 
  }
  
  protected void spoolToService(PrintService paramPrintService, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException {
    if (paramPrintService == null)
      throw new PrinterException("No print service found."); 
    DocPrintJob docPrintJob = paramPrintService.createPrintJob();
    PageableDoc pageableDoc = new PageableDoc(getPageable());
    if (paramPrintRequestAttributeSet == null)
      paramPrintRequestAttributeSet = new HashPrintRequestAttributeSet(); 
    try {
      docPrintJob.print(pageableDoc, paramPrintRequestAttributeSet);
    } catch (PrintException printException) {
      throw new PrinterException(printException.toString());
    } 
  }
  
  public void print() { print(this.attributes); }
  
  protected void debug_println(String paramString) {
    if (debugPrint)
      System.out.println("RasterPrinterJob " + paramString + " " + this); 
  }
  
  public void print(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException {
    PrintService printService = getPrintService();
    debug_println("psvc = " + printService);
    if (printService == null)
      throw new PrinterException("No print service found."); 
    PrinterState printerState = (PrinterState)printService.getAttribute(PrinterState.class);
    if (printerState == PrinterState.STOPPED) {
      PrinterStateReasons printerStateReasons = (PrinterStateReasons)printService.getAttribute(PrinterStateReasons.class);
      if (printerStateReasons != null && printerStateReasons.containsKey(PrinterStateReason.SHUTDOWN))
        throw new PrinterException("PrintService is no longer available."); 
    } 
    if ((PrinterIsAcceptingJobs)printService.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS)
      throw new PrinterException("Printer is not accepting job."); 
    if (printService instanceof SunPrinterJobService && ((SunPrinterJobService)printService).usesClass(getClass())) {
      setAttributes(paramPrintRequestAttributeSet);
      if (this.destinationAttr != null)
        validateDestination(this.destinationAttr); 
    } else {
      spoolToService(printService, paramPrintRequestAttributeSet);
      return;
    } 
    initPrinter();
    int i = getCollatedCopies();
    int j = getNoncollatedCopies();
    debug_println("getCollatedCopies()  " + i + " getNoncollatedCopies() " + j);
    int k = this.mDocument.getNumberOfPages();
    if (k == 0)
      return; 
    int m = getFirstPage();
    int n = getLastPage();
    if (n == -1) {
      int i1 = this.mDocument.getNumberOfPages();
      if (i1 != -1)
        n = this.mDocument.getNumberOfPages() - 1; 
    } 
    try {
      synchronized (this) {
        this.performingPrinting = true;
        this.userCancelled = false;
      } 
      startDoc();
      if (isCancelled())
        cancelDoc(); 
      boolean bool = true;
      if (paramPrintRequestAttributeSet != null) {
        SunPageSelection sunPageSelection = (SunPageSelection)paramPrintRequestAttributeSet.get(SunPageSelection.class);
        if (sunPageSelection != null && sunPageSelection != SunPageSelection.RANGE)
          bool = false; 
      } 
      debug_println("after startDoc rangeSelected? " + bool + " numNonCollatedCopies " + j);
      for (byte b = 0; b < i; b++) {
        int i1 = m;
        int i2 = 0;
        while ((i1 <= n || n == -1) && !i2) {
          if (this.pageRangesAttr != null && bool) {
            int i3 = this.pageRangesAttr.next(i1);
            if (i3 == -1)
              break; 
            if (i3 != i1 + 1)
              continue; 
          } 
          for (byte b1 = 0; b1 < j && !i2; b1++) {
            if (isCancelled())
              cancelDoc(); 
            debug_println("printPage " + i1);
            i2 = printPage(this.mDocument, i1);
          } 
          continue;
          i1++;
        } 
      } 
      if (isCancelled())
        cancelDoc(); 
    } finally {
      this.previousPaper = null;
      synchronized (this) {
        if (this.performingPrinting)
          endDoc(); 
        this.performingPrinting = false;
        notify();
      } 
    } 
  }
  
  protected void validateDestination(String paramString) {
    if (paramString == null)
      return; 
    File file1 = new File(paramString);
    try {
      if (file1.createNewFile())
        file1.delete(); 
    } catch (IOException iOException) {
      throw new PrinterException("Cannot write to file:" + paramString);
    } catch (SecurityException securityException) {}
    File file2 = file1.getParentFile();
    if ((file1.exists() && (!file1.isFile() || !file1.canWrite())) || (file2 != null && (!file2.exists() || (file2.exists() && !file2.canWrite()))))
      throw new PrinterException("Cannot write to file:" + paramString); 
  }
  
  protected void validatePaper(Paper paramPaper1, Paper paramPaper2) {
    if (paramPaper1 == null || paramPaper2 == null)
      return; 
    double d1 = paramPaper1.getWidth();
    double d2 = paramPaper1.getHeight();
    double d3 = paramPaper1.getImageableX();
    double d4 = paramPaper1.getImageableY();
    double d5 = paramPaper1.getImageableWidth();
    double d6 = paramPaper1.getImageableHeight();
    Paper paper = new Paper();
    d1 = (d1 > 0.0D) ? d1 : paper.getWidth();
    d2 = (d2 > 0.0D) ? d2 : paper.getHeight();
    d3 = (d3 > 0.0D) ? d3 : paper.getImageableX();
    d4 = (d4 > 0.0D) ? d4 : paper.getImageableY();
    d5 = (d5 > 0.0D) ? d5 : paper.getImageableWidth();
    d6 = (d6 > 0.0D) ? d6 : paper.getImageableHeight();
    if (d5 > d1)
      d5 = d1; 
    if (d6 > d2)
      d6 = d2; 
    if (d3 + d5 > d1)
      d3 = d1 - d5; 
    if (d4 + d6 > d2)
      d4 = d2 - d6; 
    paramPaper2.setSize(d1, d2);
    paramPaper2.setImageableArea(d3, d4, d5, d6);
  }
  
  public PageFormat defaultPage(PageFormat paramPageFormat) throws HeadlessException {
    PageFormat pageFormat = (PageFormat)paramPageFormat.clone();
    pageFormat.setOrientation(1);
    Paper paper = new Paper();
    double d = 72.0D;
    Media media = null;
    PrintService printService = getPrintService();
    if (printService != null) {
      media = (Media)printService.getDefaultAttributeValue(Media.class);
      MediaSize mediaSize;
      if (media instanceof MediaSizeName && (mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)media)) != null) {
        double d1 = mediaSize.getX(25400) * d;
        double d2 = mediaSize.getY(25400) * d;
        paper.setSize(d1, d2);
        paper.setImageableArea(d, d, d1 - 2.0D * d, d2 - 2.0D * d);
        pageFormat.setPaper(paper);
        return pageFormat;
      } 
    } 
    String str = Locale.getDefault().getCountry();
    if (!Locale.getDefault().equals(Locale.ENGLISH) && str != null && !str.equals(Locale.US.getCountry()) && !str.equals(Locale.CANADA.getCountry())) {
      double d3 = 25.4D;
      double d1 = Math.rint(210.0D * d / d3);
      double d2 = Math.rint(297.0D * d / d3);
      paper.setSize(d1, d2);
      paper.setImageableArea(d, d, d1 - 2.0D * d, d2 - 2.0D * d);
    } 
    pageFormat.setPaper(paper);
    return pageFormat;
  }
  
  public PageFormat validatePage(PageFormat paramPageFormat) throws HeadlessException {
    PageFormat pageFormat = (PageFormat)paramPageFormat.clone();
    Paper paper = new Paper();
    validatePaper(pageFormat.getPaper(), paper);
    pageFormat.setPaper(paper);
    return pageFormat;
  }
  
  public void setCopies(int paramInt) { this.mNumCopies = paramInt; }
  
  public int getCopies() { return this.mNumCopies; }
  
  protected int getCopiesInt() { return (this.copiesAttr > 0) ? this.copiesAttr : getCopies(); }
  
  public String getUserName() { return System.getProperty("user.name"); }
  
  protected String getUserNameInt() {
    if (this.userNameAttr != null)
      return this.userNameAttr; 
    try {
      return getUserName();
    } catch (SecurityException securityException) {
      return "";
    } 
  }
  
  public void setJobName(String paramString) {
    if (paramString != null) {
      this.mDocName = paramString;
    } else {
      throw new NullPointerException();
    } 
  }
  
  public String getJobName() { return this.mDocName; }
  
  protected String getJobNameInt() { return (this.jobNameAttr != null) ? this.jobNameAttr : getJobName(); }
  
  protected void setPageRange(int paramInt1, int paramInt2) {
    if (paramInt1 >= 0 && paramInt2 >= 0) {
      this.mFirstPage = paramInt1;
      this.mLastPage = paramInt2;
      if (this.mLastPage < this.mFirstPage)
        this.mLastPage = this.mFirstPage; 
    } else {
      this.mFirstPage = -1;
      this.mLastPage = -1;
    } 
  }
  
  protected int getFirstPage() { return (this.mFirstPage == -1) ? 0 : this.mFirstPage; }
  
  protected int getLastPage() { return this.mLastPage; }
  
  protected void setCollated(boolean paramBoolean) {
    this.mCollate = paramBoolean;
    this.collateAttReq = true;
  }
  
  protected boolean isCollated() throws HeadlessException { return this.mCollate; }
  
  protected final int getSelectAttrib() {
    if (this.attributes != null) {
      SunPageSelection sunPageSelection = (SunPageSelection)this.attributes.get(SunPageSelection.class);
      if (sunPageSelection == SunPageSelection.RANGE)
        return 2; 
      if (sunPageSelection == SunPageSelection.SELECTION)
        return 1; 
      if (sunPageSelection == SunPageSelection.ALL)
        return 0; 
    } 
    return 4;
  }
  
  protected final int getFromPageAttrib() {
    if (this.attributes != null) {
      PageRanges pageRanges = (PageRanges)this.attributes.get(PageRanges.class);
      if (pageRanges != null) {
        int[][] arrayOfInt = pageRanges.getMembers();
        return arrayOfInt[0][0];
      } 
    } 
    return getMinPageAttrib();
  }
  
  protected final int getToPageAttrib() {
    if (this.attributes != null) {
      PageRanges pageRanges = (PageRanges)this.attributes.get(PageRanges.class);
      if (pageRanges != null) {
        int[][] arrayOfInt = pageRanges.getMembers();
        return arrayOfInt[arrayOfInt.length - 1][1];
      } 
    } 
    return getMaxPageAttrib();
  }
  
  protected final int getMinPageAttrib() {
    if (this.attributes != null) {
      SunMinMaxPage sunMinMaxPage = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
      if (sunMinMaxPage != null)
        return sunMinMaxPage.getMin(); 
    } 
    return 1;
  }
  
  protected final int getMaxPageAttrib() {
    if (this.attributes != null) {
      SunMinMaxPage sunMinMaxPage = (SunMinMaxPage)this.attributes.get(SunMinMaxPage.class);
      if (sunMinMaxPage != null)
        return sunMinMaxPage.getMax(); 
    } 
    Pageable pageable = getPageable();
    if (pageable != null) {
      int i = pageable.getNumberOfPages();
      if (i <= -1)
        i = 9999; 
      return (i == 0) ? 1 : i;
    } 
    return Integer.MAX_VALUE;
  }
  
  protected abstract void startDoc();
  
  protected abstract void endDoc();
  
  protected abstract void abortDoc();
  
  protected void cancelDoc() {
    abortDoc();
    synchronized (this) {
      this.userCancelled = false;
      this.performingPrinting = false;
      notify();
    } 
    throw new PrinterAbortException();
  }
  
  protected int getCollatedCopies() { return isCollated() ? getCopiesInt() : 1; }
  
  protected int getNoncollatedCopies() { return isCollated() ? 1 : getCopiesInt(); }
  
  void setGraphicsConfigInfo(AffineTransform paramAffineTransform, double paramDouble1, double paramDouble2) {
    Point2D.Double double = new Point2D.Double(paramDouble1, paramDouble2);
    paramAffineTransform.transform(double, double);
    if (this.pgConfig == null || this.defaultDeviceTransform == null || !paramAffineTransform.equals(this.defaultDeviceTransform) || this.deviceWidth != (int)double.getX() || this.deviceHeight != (int)double.getY()) {
      this.deviceWidth = (int)double.getX();
      this.deviceHeight = (int)double.getY();
      this.defaultDeviceTransform = paramAffineTransform;
      this.pgConfig = null;
    } 
  }
  
  PrinterGraphicsConfig getPrinterGraphicsConfig() {
    if (this.pgConfig != null)
      return this.pgConfig; 
    String str = "Printer Device";
    PrintService printService = getPrintService();
    if (printService != null)
      str = printService.toString(); 
    this.pgConfig = new PrinterGraphicsConfig(str, this.defaultDeviceTransform, this.deviceWidth, this.deviceHeight);
    return this.pgConfig;
  }
  
  protected int printPage(Pageable paramPageable, int paramInt) throws PrinterException {
    Printable printable;
    PageFormat pageFormat2;
    PageFormat pageFormat1;
    try {
      pageFormat2 = paramPageable.getPageFormat(paramInt);
      pageFormat1 = (PageFormat)pageFormat2.clone();
      printable = paramPageable.getPrintable(paramInt);
    } catch (Exception exception) {
      PrinterException printerException = new PrinterException("Error getting page or printable.[ " + exception + " ]");
      printerException.initCause(exception);
      throw printerException;
    } 
    Paper paper = pageFormat1.getPaper();
    if (pageFormat1.getOrientation() != 1 && this.landscapeRotates270) {
      double d3 = paper.getImageableX();
      double d4 = paper.getImageableY();
      double d5 = paper.getImageableWidth();
      double d6 = paper.getImageableHeight();
      paper.setImageableArea(paper.getWidth() - d3 - d5, paper.getHeight() - d4 - d6, d5, d6);
      pageFormat1.setPaper(paper);
      if (pageFormat1.getOrientation() == 0) {
        pageFormat1.setOrientation(2);
      } else {
        pageFormat1.setOrientation(0);
      } 
    } 
    double d1 = getXRes() / 72.0D;
    double d2 = getYRes() / 72.0D;
    Rectangle2D.Double double1 = new Rectangle2D.Double(paper.getImageableX() * d1, paper.getImageableY() * d2, paper.getImageableWidth() * d1, paper.getImageableHeight() * d2);
    AffineTransform affineTransform1 = new AffineTransform();
    AffineTransform affineTransform2 = new AffineTransform();
    affineTransform2.scale(d1, d2);
    int i = (int)double1.getWidth();
    if (i % 4 != 0)
      i += 4 - i % 4; 
    if (i <= 0)
      throw new PrinterException("Paper's imageable width is too small."); 
    int j = (int)double1.getHeight();
    if (j <= 0)
      throw new PrinterException("Paper's imageable height is too small."); 
    int k = 4194304 / i / 3;
    int m = (int)Math.rint(paper.getImageableX() * d1);
    int n = (int)Math.rint(paper.getImageableY() * d2);
    AffineTransform affineTransform3 = new AffineTransform();
    affineTransform3.translate(-m, n);
    affineTransform3.translate(0.0D, k);
    affineTransform3.scale(1.0D, -1.0D);
    BufferedImage bufferedImage = new BufferedImage(1, 1, 5);
    PeekGraphics peekGraphics = createPeekGraphics(bufferedImage.createGraphics(), this);
    Rectangle2D.Double double2 = new Rectangle2D.Double(pageFormat1.getImageableX(), pageFormat1.getImageableY(), pageFormat1.getImageableWidth(), pageFormat1.getImageableHeight());
    peekGraphics.transform(affineTransform2);
    peekGraphics.translate(-getPhysicalPrintableX(paper) / d1, -getPhysicalPrintableY(paper) / d2);
    peekGraphics.transform(new AffineTransform(pageFormat1.getMatrix()));
    initPrinterGraphics(peekGraphics, double2);
    AffineTransform affineTransform4 = peekGraphics.getTransform();
    setGraphicsConfigInfo(affineTransform2, paper.getWidth(), paper.getHeight());
    int i1 = printable.print(peekGraphics, pageFormat2, paramInt);
    debug_println("pageResult " + i1);
    if (i1 == 0) {
      debug_println("startPage " + paramInt);
      Paper paper1 = pageFormat1.getPaper();
      boolean bool = (this.previousPaper == null || paper1.getWidth() != this.previousPaper.getWidth() || paper1.getHeight() != this.previousPaper.getHeight());
      this.previousPaper = paper1;
      startPage(pageFormat1, printable, paramInt, bool);
      Graphics2D graphics2D = createPathGraphics(peekGraphics, this, printable, pageFormat1, paramInt);
      if (graphics2D != null) {
        graphics2D.transform(affineTransform2);
        graphics2D.translate(-getPhysicalPrintableX(paper) / d1, -getPhysicalPrintableY(paper) / d2);
        graphics2D.transform(new AffineTransform(pageFormat1.getMatrix()));
        initPrinterGraphics(graphics2D, double2);
        this.redrawList.clear();
        AffineTransform affineTransform = graphics2D.getTransform();
        printable.print(graphics2D, pageFormat2, paramInt);
        for (byte b = 0; b < this.redrawList.size(); b++) {
          GraphicsState graphicsState = (GraphicsState)this.redrawList.get(b);
          graphics2D.setTransform(affineTransform);
          ((PathGraphics)graphics2D).redrawRegion(graphicsState.region, graphicsState.sx, graphicsState.sy, graphicsState.theClip, graphicsState.theTransform);
        } 
      } else {
        BufferedImage bufferedImage1 = this.cachedBand;
        if (this.cachedBand == null || i != this.cachedBandWidth || k != this.cachedBandHeight) {
          bufferedImage1 = new BufferedImage(i, k, 5);
          this.cachedBand = bufferedImage1;
          this.cachedBandWidth = i;
          this.cachedBandHeight = k;
        } 
        Graphics2D graphics2D1 = bufferedImage1.createGraphics();
        Rectangle2D.Double double = new Rectangle2D.Double(0.0D, 0.0D, i, k);
        initPrinterGraphics(graphics2D1, double);
        ProxyGraphics2D proxyGraphics2D = new ProxyGraphics2D(graphics2D1, this);
        Graphics2D graphics2D2 = bufferedImage1.createGraphics();
        graphics2D2.setColor(Color.white);
        ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)bufferedImage1.getRaster();
        byte[] arrayOfByte = byteInterleavedRaster.getDataStorage();
        int i2 = n + j;
        int i3 = (int)getPhysicalPrintableX(paper);
        int i4 = (int)getPhysicalPrintableY(paper);
        int i5;
        for (i5 = 0; i5 <= j; i5 += k) {
          graphics2D2.fillRect(0, 0, i, k);
          graphics2D1.setTransform(affineTransform1);
          graphics2D1.transform(affineTransform3);
          affineTransform3.translate(0.0D, -k);
          graphics2D1.transform(affineTransform2);
          graphics2D1.transform(new AffineTransform(pageFormat1.getMatrix()));
          Rectangle rectangle = graphics2D1.getClipBounds();
          rectangle = affineTransform4.createTransformedShape(rectangle).getBounds();
          if (rectangle == null || (peekGraphics.hitsDrawingArea(rectangle) && i > 0 && k > 0)) {
            int i6 = m - i3;
            if (i6 < 0) {
              graphics2D1.translate(i6 / d1, 0.0D);
              i6 = 0;
            } 
            int i7 = n + i5 - i4;
            if (i7 < 0) {
              graphics2D1.translate(0.0D, i7 / d2);
              i7 = 0;
            } 
            proxyGraphics2D.setDelegate((Graphics2D)graphics2D1.create());
            printable.print(proxyGraphics2D, pageFormat2, paramInt);
            proxyGraphics2D.dispose();
            printBand(arrayOfByte, i6, i7, i, k);
          } 
        } 
        graphics2D2.dispose();
        graphics2D1.dispose();
      } 
      debug_println("calling endPage " + paramInt);
      endPage(pageFormat1, printable, paramInt);
    } 
    return i1;
  }
  
  public void cancel() {
    synchronized (this) {
      if (this.performingPrinting)
        this.userCancelled = true; 
      notify();
    } 
  }
  
  public boolean isCancelled() throws HeadlessException {
    boolean bool = false;
    synchronized (this) {
      bool = (this.performingPrinting && this.userCancelled);
      notify();
    } 
    return bool;
  }
  
  protected Pageable getPageable() { return this.mDocument; }
  
  protected Graphics2D createPathGraphics(PeekGraphics paramPeekGraphics, PrinterJob paramPrinterJob, Printable paramPrintable, PageFormat paramPageFormat, int paramInt) { return null; }
  
  protected PeekGraphics createPeekGraphics(Graphics2D paramGraphics2D, PrinterJob paramPrinterJob) { return new PeekGraphics(paramGraphics2D, paramPrinterJob); }
  
  protected void initPrinterGraphics(Graphics2D paramGraphics2D, Rectangle2D paramRectangle2D) {
    paramGraphics2D.setClip(paramRectangle2D);
    paramGraphics2D.setPaint(Color.black);
  }
  
  public boolean checkAllowedToPrintToFile() throws HeadlessException {
    try {
      throwPrintToFile();
      return true;
    } catch (SecurityException securityException) {
      return false;
    } 
  }
  
  private void throwPrintToFile() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      if (this.printToFilePermission == null)
        this.printToFilePermission = new FilePermission("<<ALL FILES>>", "read,write"); 
      securityManager.checkPermission(this.printToFilePermission);
    } 
  }
  
  protected String removeControlChars(String paramString) {
    char[] arrayOfChar1 = paramString.toCharArray();
    int i = arrayOfChar1.length;
    char[] arrayOfChar2 = new char[i];
    byte b1 = 0;
    for (byte b2 = 0; b2 < i; b2++) {
      char c = arrayOfChar1[b2];
      if (c > '\r' || c < '\t' || c == '\013' || c == '\f')
        arrayOfChar2[b1++] = c; 
    } 
    return (b1 == i) ? paramString : new String(arrayOfChar2, 0, b1);
  }
  
  private long getParentWindowID() { return this.parentWindowID; }
  
  private void clearParentWindowID() {
    this.parentWindowID = 0L;
    this.onTop = null;
  }
  
  private void setParentWindowID(PrintRequestAttributeSet paramPrintRequestAttributeSet) throws PrinterException {
    this.parentWindowID = 0L;
    this.onTop = (DialogOnTop)paramPrintRequestAttributeSet.get(DialogOnTop.class);
    if (this.onTop != null)
      this.parentWindowID = this.onTop.getID(); 
  }
  
  static  {
    String str1 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pipeline"));
    if (str1 != null)
      if (str1.equalsIgnoreCase("pdl")) {
        forcePDL = true;
      } else if (str1.equalsIgnoreCase("raster")) {
        forceRaster = true;
      }  
    String str2 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.shapetext"));
    if (str2 != null)
      shapeTextProp = true; 
    debugPrint = false;
  }
  
  private class GraphicsState {
    Rectangle2D region;
    
    Shape theClip;
    
    AffineTransform theTransform;
    
    double sx;
    
    double sy;
    
    private GraphicsState() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\RasterPrinterJob.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
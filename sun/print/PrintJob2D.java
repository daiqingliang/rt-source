package sun.print;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.JobAttributes;
import java.awt.PageAttributes;
import java.awt.PrintJob;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DialogTypeSelection;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

public class PrintJob2D extends PrintJob implements Printable, Runnable {
  private static final PageAttributes.MediaType[] SIZES = { 
      PageAttributes.MediaType.ISO_4A0, PageAttributes.MediaType.ISO_2A0, PageAttributes.MediaType.ISO_A0, PageAttributes.MediaType.ISO_A1, PageAttributes.MediaType.ISO_A2, PageAttributes.MediaType.ISO_A3, PageAttributes.MediaType.ISO_A4, PageAttributes.MediaType.ISO_A5, PageAttributes.MediaType.ISO_A6, PageAttributes.MediaType.ISO_A7, 
      PageAttributes.MediaType.ISO_A8, PageAttributes.MediaType.ISO_A9, PageAttributes.MediaType.ISO_A10, PageAttributes.MediaType.ISO_B0, PageAttributes.MediaType.ISO_B1, PageAttributes.MediaType.ISO_B2, PageAttributes.MediaType.ISO_B3, PageAttributes.MediaType.ISO_B4, PageAttributes.MediaType.ISO_B5, PageAttributes.MediaType.ISO_B6, 
      PageAttributes.MediaType.ISO_B7, PageAttributes.MediaType.ISO_B8, PageAttributes.MediaType.ISO_B9, PageAttributes.MediaType.ISO_B10, PageAttributes.MediaType.JIS_B0, PageAttributes.MediaType.JIS_B1, PageAttributes.MediaType.JIS_B2, PageAttributes.MediaType.JIS_B3, PageAttributes.MediaType.JIS_B4, PageAttributes.MediaType.JIS_B5, 
      PageAttributes.MediaType.JIS_B6, PageAttributes.MediaType.JIS_B7, PageAttributes.MediaType.JIS_B8, PageAttributes.MediaType.JIS_B9, PageAttributes.MediaType.JIS_B10, PageAttributes.MediaType.ISO_C0, PageAttributes.MediaType.ISO_C1, PageAttributes.MediaType.ISO_C2, PageAttributes.MediaType.ISO_C3, PageAttributes.MediaType.ISO_C4, 
      PageAttributes.MediaType.ISO_C5, PageAttributes.MediaType.ISO_C6, PageAttributes.MediaType.ISO_C7, PageAttributes.MediaType.ISO_C8, PageAttributes.MediaType.ISO_C9, PageAttributes.MediaType.ISO_C10, PageAttributes.MediaType.ISO_DESIGNATED_LONG, PageAttributes.MediaType.EXECUTIVE, PageAttributes.MediaType.FOLIO, PageAttributes.MediaType.INVOICE, 
      PageAttributes.MediaType.LEDGER, PageAttributes.MediaType.NA_LETTER, PageAttributes.MediaType.NA_LEGAL, PageAttributes.MediaType.QUARTO, PageAttributes.MediaType.A, PageAttributes.MediaType.B, PageAttributes.MediaType.C, PageAttributes.MediaType.D, PageAttributes.MediaType.E, PageAttributes.MediaType.NA_10X15_ENVELOPE, 
      PageAttributes.MediaType.NA_10X14_ENVELOPE, PageAttributes.MediaType.NA_10X13_ENVELOPE, PageAttributes.MediaType.NA_9X12_ENVELOPE, PageAttributes.MediaType.NA_9X11_ENVELOPE, PageAttributes.MediaType.NA_7X9_ENVELOPE, PageAttributes.MediaType.NA_6X9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_9_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_10_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_11_ENVELOPE, PageAttributes.MediaType.NA_NUMBER_12_ENVELOPE, 
      PageAttributes.MediaType.NA_NUMBER_14_ENVELOPE, PageAttributes.MediaType.INVITE_ENVELOPE, PageAttributes.MediaType.ITALY_ENVELOPE, PageAttributes.MediaType.MONARCH_ENVELOPE, PageAttributes.MediaType.PERSONAL_ENVELOPE };
  
  private static final MediaSizeName[] JAVAXSIZES = { 
      null, null, MediaSizeName.ISO_A0, MediaSizeName.ISO_A1, MediaSizeName.ISO_A2, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_A6, MediaSizeName.ISO_A7, 
      MediaSizeName.ISO_A8, MediaSizeName.ISO_A9, MediaSizeName.ISO_A10, MediaSizeName.ISO_B0, MediaSizeName.ISO_B1, MediaSizeName.ISO_B2, MediaSizeName.ISO_B3, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, 
      MediaSizeName.ISO_B7, MediaSizeName.ISO_B8, MediaSizeName.ISO_B9, MediaSizeName.ISO_B10, MediaSizeName.JIS_B0, MediaSizeName.JIS_B1, MediaSizeName.JIS_B2, MediaSizeName.JIS_B3, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, 
      MediaSizeName.JIS_B6, MediaSizeName.JIS_B7, MediaSizeName.JIS_B8, MediaSizeName.JIS_B9, MediaSizeName.JIS_B10, MediaSizeName.ISO_C0, MediaSizeName.ISO_C1, MediaSizeName.ISO_C2, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, 
      MediaSizeName.ISO_C5, MediaSizeName.ISO_C6, null, null, null, null, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.EXECUTIVE, MediaSizeName.FOLIO, MediaSizeName.INVOICE, 
      MediaSizeName.LEDGER, MediaSizeName.NA_LETTER, MediaSizeName.NA_LEGAL, MediaSizeName.QUARTO, MediaSizeName.A, MediaSizeName.B, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.NA_10X15_ENVELOPE, 
      MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.NA_10X13_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.NA_9X11_ENVELOPE, MediaSizeName.NA_7X9_ENVELOPE, MediaSizeName.NA_6X9_ENVELOPE, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, 
      MediaSizeName.NA_NUMBER_14_ENVELOPE, null, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE };
  
  private static final int[] WIDTHS = { 
      4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 210, 
      147, 105, 74, 2835, 2004, 1417, 1001, 709, 499, 354, 
      249, 176, 125, 88, 2920, 2064, 1460, 1032, 729, 516, 
      363, 258, 181, 128, 91, 2599, 1837, 1298, 918, 649, 
      459, 323, 230, 162, 113, 79, 312, 522, 612, 396, 
      792, 612, 612, 609, 612, 792, 1224, 1584, 2448, 720, 
      720, 720, 648, 648, 504, 432, 279, 297, 324, 342, 
      360, 624, 312, 279, 261 };
  
  private static final int[] LENGTHS = { 
      6741, 4768, 3370, 2384, 1684, 1191, 842, 595, 420, 298, 
      210, 147, 105, 4008, 2835, 2004, 1417, 1001, 729, 499, 
      354, 249, 176, 125, 4127, 2920, 2064, 1460, 1032, 729, 
      516, 363, 258, 181, 128, 3677, 2599, 1837, 1298, 918, 
      649, 459, 323, 230, 162, 113, 624, 756, 936, 612, 
      1224, 792, 1008, 780, 792, 1224, 1584, 2448, 3168, 1080, 
      1008, 936, 864, 792, 648, 648, 639, 684, 747, 792, 
      828, 624, 652, 540, 468 };
  
  private Frame frame;
  
  private String docTitle = "";
  
  private JobAttributes jobAttributes;
  
  private PageAttributes pageAttributes;
  
  private PrintRequestAttributeSet attributes;
  
  private PrinterJob printerJob;
  
  private PageFormat pageFormat;
  
  private MessageQ graphicsToBeDrawn = new MessageQ("tobedrawn");
  
  private MessageQ graphicsDrawn = new MessageQ("drawn");
  
  private Graphics2D currentGraphics;
  
  private int pageIndex = -1;
  
  private static final String DEST_PROP = "awt.print.destination";
  
  private static final String PRINTER = "printer";
  
  private static final String FILE = "file";
  
  private static final String PRINTER_PROP = "awt.print.printer";
  
  private static final String FILENAME_PROP = "awt.print.fileName";
  
  private static final String NUMCOPIES_PROP = "awt.print.numCopies";
  
  private static final String OPTIONS_PROP = "awt.print.options";
  
  private static final String ORIENT_PROP = "awt.print.orientation";
  
  private static final String PORTRAIT = "portrait";
  
  private static final String LANDSCAPE = "landscape";
  
  private static final String PAPERSIZE_PROP = "awt.print.paperSize";
  
  private static final String LETTER = "letter";
  
  private static final String LEGAL = "legal";
  
  private static final String EXECUTIVE = "executive";
  
  private static final String A4 = "a4";
  
  private Properties props;
  
  private String options = "";
  
  private Thread printerJobThread;
  
  public PrintJob2D(Frame paramFrame, String paramString, Properties paramProperties) {
    this.props = paramProperties;
    this.jobAttributes = new JobAttributes();
    this.pageAttributes = new PageAttributes();
    translateInputProps();
    initPrintJob2D(paramFrame, paramString, this.jobAttributes, this.pageAttributes);
  }
  
  public PrintJob2D(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes) { initPrintJob2D(paramFrame, paramString, paramJobAttributes, paramPageAttributes); }
  
  private void initPrintJob2D(Frame paramFrame, String paramString, JobAttributes paramJobAttributes, PageAttributes paramPageAttributes) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    if (paramFrame == null && (paramJobAttributes == null || paramJobAttributes.getDialog() == JobAttributes.DialogType.NATIVE))
      throw new NullPointerException("Frame must not be null"); 
    this.frame = paramFrame;
    this.docTitle = (paramString == null) ? "" : paramString;
    this.jobAttributes = (paramJobAttributes != null) ? paramJobAttributes : new JobAttributes();
    this.pageAttributes = (paramPageAttributes != null) ? paramPageAttributes : new PageAttributes();
    int[][] arrayOfInt = this.jobAttributes.getPageRanges();
    int i = arrayOfInt[0][0];
    int j = arrayOfInt[arrayOfInt.length - 1][1];
    this.jobAttributes.setPageRanges(new int[][] { { i, j } });
    this.jobAttributes.setToPage(j);
    this.jobAttributes.setFromPage(i);
    int[] arrayOfInt1 = this.pageAttributes.getPrinterResolution();
    if (arrayOfInt1[0] != arrayOfInt1[1])
      throw new IllegalArgumentException("Differing cross feed and feed resolutions not supported."); 
    JobAttributes.DestinationType destinationType = this.jobAttributes.getDestination();
    if (destinationType == JobAttributes.DestinationType.FILE) {
      throwPrintToFile();
      String str = paramJobAttributes.getFileName();
      if (str != null && paramJobAttributes.getDialog() == JobAttributes.DialogType.NONE) {
        File file1 = new File(str);
        try {
          if (file1.createNewFile())
            file1.delete(); 
        } catch (IOException iOException) {
          throw new IllegalArgumentException("Cannot write to file:" + str);
        } catch (SecurityException securityException) {}
        File file2 = file1.getParentFile();
        if ((file1.exists() && (!file1.isFile() || !file1.canWrite())) || (file2 != null && (!file2.exists() || (file2.exists() && !file2.canWrite()))))
          throw new IllegalArgumentException("Cannot write to file:" + str); 
      } 
    } 
  }
  
  public boolean printDialog() {
    boolean bool = false;
    this.printerJob = PrinterJob.getPrinterJob();
    if (this.printerJob == null)
      return false; 
    JobAttributes.DialogType dialogType = this.jobAttributes.getDialog();
    PrintService printService = this.printerJob.getPrintService();
    if (printService == null && dialogType == JobAttributes.DialogType.NONE)
      return false; 
    copyAttributes(printService);
    JobAttributes.DefaultSelectionType defaultSelectionType = this.jobAttributes.getDefaultSelection();
    if (defaultSelectionType == JobAttributes.DefaultSelectionType.RANGE) {
      this.attributes.add(SunPageSelection.RANGE);
    } else if (defaultSelectionType == JobAttributes.DefaultSelectionType.SELECTION) {
      this.attributes.add(SunPageSelection.SELECTION);
    } else {
      this.attributes.add(SunPageSelection.ALL);
    } 
    if (this.frame != null)
      this.attributes.add(new DialogOwner(this.frame)); 
    if (dialogType == JobAttributes.DialogType.NONE) {
      bool = true;
    } else {
      if (dialogType == JobAttributes.DialogType.NATIVE) {
        this.attributes.add(DialogTypeSelection.NATIVE);
      } else {
        this.attributes.add(DialogTypeSelection.COMMON);
      } 
      if (bool = this.printerJob.printDialog(this.attributes)) {
        if (printService == null) {
          printService = this.printerJob.getPrintService();
          if (printService == null)
            return false; 
        } 
        updateAttributes();
        translateOutputProps();
      } 
    } 
    if (bool) {
      JobName jobName = (JobName)this.attributes.get(JobName.class);
      if (jobName != null)
        this.printerJob.setJobName(jobName.toString()); 
      this.pageFormat = new PageFormat();
      Media media = (Media)this.attributes.get(Media.class);
      MediaSize mediaSize = null;
      if (media != null && media instanceof MediaSizeName)
        mediaSize = MediaSize.getMediaSizeForName((MediaSizeName)media); 
      Paper paper = this.pageFormat.getPaper();
      if (mediaSize != null)
        paper.setSize(mediaSize.getX(25400) * 72.0D, mediaSize.getY(25400) * 72.0D); 
      if (this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
        paper.setImageableArea(18.0D, 18.0D, paper.getWidth() - 36.0D, paper.getHeight() - 36.0D);
      } else {
        paper.setImageableArea(0.0D, 0.0D, paper.getWidth(), paper.getHeight());
      } 
      this.pageFormat.setPaper(paper);
      OrientationRequested orientationRequested = (OrientationRequested)this.attributes.get(OrientationRequested.class);
      if (orientationRequested != null && orientationRequested == OrientationRequested.REVERSE_LANDSCAPE) {
        this.pageFormat.setOrientation(2);
      } else if (orientationRequested == OrientationRequested.LANDSCAPE) {
        this.pageFormat.setOrientation(0);
      } else {
        this.pageFormat.setOrientation(1);
      } 
      this.printerJob.setPrintable(this, this.pageFormat);
    } 
    return bool;
  }
  
  private void updateAttributes() {
    Copies copies = (Copies)this.attributes.get(Copies.class);
    this.jobAttributes.setCopies(copies.getValue());
    SunPageSelection sunPageSelection = (SunPageSelection)this.attributes.get(SunPageSelection.class);
    if (sunPageSelection == SunPageSelection.RANGE) {
      this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.RANGE);
    } else if (sunPageSelection == SunPageSelection.SELECTION) {
      this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.SELECTION);
    } else {
      this.jobAttributes.setDefaultSelection(JobAttributes.DefaultSelectionType.ALL);
    } 
    Destination destination = (Destination)this.attributes.get(Destination.class);
    if (destination != null) {
      this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
      this.jobAttributes.setFileName(destination.getURI().getPath());
    } else {
      this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
    } 
    PrintService printService = this.printerJob.getPrintService();
    if (printService != null)
      this.jobAttributes.setPrinter(printService.getName()); 
    PageRanges pageRanges = (PageRanges)this.attributes.get(PageRanges.class);
    int[][] arrayOfInt = pageRanges.getMembers();
    this.jobAttributes.setPageRanges(arrayOfInt);
    SheetCollate sheetCollate = (SheetCollate)this.attributes.get(SheetCollate.class);
    if (sheetCollate == SheetCollate.COLLATED) {
      this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES);
    } else {
      this.jobAttributes.setMultipleDocumentHandling(JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES);
    } 
    Sides sides = (Sides)this.attributes.get(Sides.class);
    if (sides == Sides.TWO_SIDED_LONG_EDGE) {
      this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_LONG_EDGE);
    } else if (sides == Sides.TWO_SIDED_SHORT_EDGE) {
      this.jobAttributes.setSides(JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE);
    } else {
      this.jobAttributes.setSides(JobAttributes.SidesType.ONE_SIDED);
    } 
    Chromaticity chromaticity = (Chromaticity)this.attributes.get(Chromaticity.class);
    if (chromaticity == Chromaticity.COLOR) {
      this.pageAttributes.setColor(PageAttributes.ColorType.COLOR);
    } else {
      this.pageAttributes.setColor(PageAttributes.ColorType.MONOCHROME);
    } 
    OrientationRequested orientationRequested = (OrientationRequested)this.attributes.get(OrientationRequested.class);
    if (orientationRequested == OrientationRequested.LANDSCAPE) {
      this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
    } else {
      this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
    } 
    PrintQuality printQuality = (PrintQuality)this.attributes.get(PrintQuality.class);
    if (printQuality == PrintQuality.DRAFT) {
      this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.DRAFT);
    } else if (printQuality == PrintQuality.HIGH) {
      this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.HIGH);
    } else {
      this.pageAttributes.setPrintQuality(PageAttributes.PrintQualityType.NORMAL);
    } 
    Media media = (Media)this.attributes.get(Media.class);
    if (media != null && media instanceof MediaSizeName) {
      PageAttributes.MediaType mediaType = unMapMedia((MediaSizeName)media);
      if (mediaType != null)
        this.pageAttributes.setMedia(mediaType); 
    } 
    debugPrintAttributes(false, false);
  }
  
  private void debugPrintAttributes(boolean paramBoolean1, boolean paramBoolean2) {
    if (paramBoolean1)
      System.out.println("new Attributes\ncopies = " + this.jobAttributes.getCopies() + "\nselection = " + this.jobAttributes.getDefaultSelection() + "\ndest " + this.jobAttributes.getDestination() + "\nfile " + this.jobAttributes.getFileName() + "\nfromPage " + this.jobAttributes.getFromPage() + "\ntoPage " + this.jobAttributes.getToPage() + "\ncollation " + this.jobAttributes.getMultipleDocumentHandling() + "\nPrinter " + this.jobAttributes.getPrinter() + "\nSides2 " + this.jobAttributes.getSides()); 
    if (paramBoolean2)
      System.out.println("new Attributes\ncolor = " + this.pageAttributes.getColor() + "\norientation = " + this.pageAttributes.getOrientationRequested() + "\nquality " + this.pageAttributes.getPrintQuality() + "\nMedia2 " + this.pageAttributes.getMedia()); 
  }
  
  private void copyAttributes(PrintService paramPrintService) {
    this.attributes = new HashPrintRequestAttributeSet();
    this.attributes.add(new JobName(this.docTitle, null));
    PrintService printService = paramPrintService;
    String str = this.jobAttributes.getPrinter();
    if (str != null && str != "" && !str.equals(printService.getName())) {
      PrintService[] arrayOfPrintService = PrinterJob.lookupPrintServices();
      try {
        for (byte b = 0; b < arrayOfPrintService.length; b++) {
          if (str.equals(arrayOfPrintService[b].getName())) {
            this.printerJob.setPrintService(arrayOfPrintService[b]);
            printService = arrayOfPrintService[b];
            break;
          } 
        } 
      } catch (PrinterException printerException) {}
    } 
    JobAttributes.DestinationType destinationType = this.jobAttributes.getDestination();
    if (destinationType == JobAttributes.DestinationType.FILE && printService.isAttributeCategorySupported(Destination.class)) {
      String str1 = this.jobAttributes.getFileName();
      Destination destination;
      if (str1 == null && (destination = (Destination)printService.getDefaultAttributeValue(Destination.class)) != null) {
        this.attributes.add(destination);
      } else {
        URI uRI = null;
        try {
          if (str1 != null) {
            if (str1.equals(""))
              str1 = "."; 
          } else {
            str1 = "out.prn";
          } 
          uRI = (new File(str1)).toURI();
        } catch (SecurityException securityException) {
          try {
            str1 = str1.replace('\\', '/');
            uRI = new URI("file:" + str1);
          } catch (URISyntaxException uRISyntaxException) {}
        } 
        if (uRI != null)
          this.attributes.add(new Destination(uRI)); 
      } 
    } 
    this.attributes.add(new SunMinMaxPage(this.jobAttributes.getMinPage(), this.jobAttributes.getMaxPage()));
    JobAttributes.SidesType sidesType = this.jobAttributes.getSides();
    if (sidesType == JobAttributes.SidesType.TWO_SIDED_LONG_EDGE) {
      this.attributes.add(Sides.TWO_SIDED_LONG_EDGE);
    } else if (sidesType == JobAttributes.SidesType.TWO_SIDED_SHORT_EDGE) {
      this.attributes.add(Sides.TWO_SIDED_SHORT_EDGE);
    } else if (sidesType == JobAttributes.SidesType.ONE_SIDED) {
      this.attributes.add(Sides.ONE_SIDED);
    } 
    JobAttributes.MultipleDocumentHandlingType multipleDocumentHandlingType = this.jobAttributes.getMultipleDocumentHandling();
    if (multipleDocumentHandlingType == JobAttributes.MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_COLLATED_COPIES) {
      this.attributes.add(SheetCollate.COLLATED);
    } else {
      this.attributes.add(SheetCollate.UNCOLLATED);
    } 
    this.attributes.add(new Copies(this.jobAttributes.getCopies()));
    this.attributes.add(new PageRanges(this.jobAttributes.getFromPage(), this.jobAttributes.getToPage()));
    if (this.pageAttributes.getColor() == PageAttributes.ColorType.COLOR) {
      this.attributes.add(Chromaticity.COLOR);
    } else {
      this.attributes.add(Chromaticity.MONOCHROME);
    } 
    this.pageFormat = this.printerJob.defaultPage();
    if (this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.LANDSCAPE) {
      this.pageFormat.setOrientation(0);
      this.attributes.add(OrientationRequested.LANDSCAPE);
    } else {
      this.pageFormat.setOrientation(1);
      this.attributes.add(OrientationRequested.PORTRAIT);
    } 
    PageAttributes.MediaType mediaType = this.pageAttributes.getMedia();
    MediaSizeName mediaSizeName = mapMedia(mediaType);
    if (mediaSizeName != null)
      this.attributes.add(mediaSizeName); 
    PageAttributes.PrintQualityType printQualityType = this.pageAttributes.getPrintQuality();
    if (printQualityType == PageAttributes.PrintQualityType.DRAFT) {
      this.attributes.add(PrintQuality.DRAFT);
    } else if (printQualityType == PageAttributes.PrintQualityType.NORMAL) {
      this.attributes.add(PrintQuality.NORMAL);
    } else if (printQualityType == PageAttributes.PrintQualityType.HIGH) {
      this.attributes.add(PrintQuality.HIGH);
    } 
  }
  
  public Graphics getGraphics() {
    ProxyPrintGraphics proxyPrintGraphics = null;
    synchronized (this) {
      this.pageIndex++;
      if (this.pageIndex == 0 && !this.graphicsToBeDrawn.isClosed())
        startPrinterJobThread(); 
      notify();
    } 
    if (this.currentGraphics != null) {
      this.graphicsDrawn.append(this.currentGraphics);
      this.currentGraphics = null;
    } 
    this.currentGraphics = this.graphicsToBeDrawn.pop();
    if (this.currentGraphics instanceof PeekGraphics) {
      ((PeekGraphics)this.currentGraphics).setAWTDrawingOnly();
      this.graphicsDrawn.append(this.currentGraphics);
      this.currentGraphics = this.graphicsToBeDrawn.pop();
    } 
    if (this.currentGraphics != null) {
      this.currentGraphics.translate(this.pageFormat.getImageableX(), this.pageFormat.getImageableY());
      double d = 72.0D / getPageResolutionInternal();
      this.currentGraphics.scale(d, d);
      proxyPrintGraphics = new ProxyPrintGraphics(this.currentGraphics.create(), this);
    } 
    return proxyPrintGraphics;
  }
  
  public Dimension getPageDimension() {
    double d2;
    double d1;
    if (this.pageAttributes != null && this.pageAttributes.getOrigin() == PageAttributes.OriginType.PRINTABLE) {
      d1 = this.pageFormat.getImageableWidth();
      d2 = this.pageFormat.getImageableHeight();
    } else {
      d1 = this.pageFormat.getWidth();
      d2 = this.pageFormat.getHeight();
    } 
    double d3 = getPageResolutionInternal() / 72.0D;
    return new Dimension((int)(d1 * d3), (int)(d2 * d3));
  }
  
  private double getPageResolutionInternal() {
    if (this.pageAttributes != null) {
      int[] arrayOfInt = this.pageAttributes.getPrinterResolution();
      return (arrayOfInt[2] == 3) ? arrayOfInt[0] : (arrayOfInt[0] * 2.54D);
    } 
    return 72.0D;
  }
  
  public int getPageResolution() { return (int)getPageResolutionInternal(); }
  
  public boolean lastPageFirst() { return false; }
  
  public void end() {
    this.graphicsToBeDrawn.close();
    if (this.currentGraphics != null)
      this.graphicsDrawn.append(this.currentGraphics); 
    this.graphicsDrawn.closeWhenEmpty();
    if (this.printerJobThread != null && this.printerJobThread.isAlive())
      try {
        this.printerJobThread.join();
      } catch (InterruptedException interruptedException) {} 
  }
  
  public void finalize() { end(); }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) throws PrinterException {
    byte b;
    this.graphicsToBeDrawn.append((Graphics2D)paramGraphics);
    if (this.graphicsDrawn.pop() != null) {
      b = 0;
    } else {
      b = 1;
    } 
    return b;
  }
  
  private void startPrinterJobThread() {
    this.printerJobThread = new Thread(this, "printerJobThread");
    this.printerJobThread.start();
  }
  
  public void run() {
    try {
      this.printerJob.print(this.attributes);
    } catch (PrinterException printerException) {}
    this.graphicsToBeDrawn.closeWhenEmpty();
    this.graphicsDrawn.close();
  }
  
  private static int[] getSize(PageAttributes.MediaType paramMediaType) {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = 612;
    arrayOfInt[1] = 792;
    for (byte b = 0; b < SIZES.length; b++) {
      if (SIZES[b] == paramMediaType) {
        arrayOfInt[0] = WIDTHS[b];
        arrayOfInt[1] = LENGTHS[b];
        break;
      } 
    } 
    return arrayOfInt;
  }
  
  public static MediaSizeName mapMedia(PageAttributes.MediaType paramMediaType) {
    MediaSizeName mediaSizeName = null;
    int i = Math.min(SIZES.length, JAVAXSIZES.length);
    for (byte b = 0; b < i; b++) {
      if (SIZES[b] == paramMediaType) {
        if (JAVAXSIZES[b] != null && MediaSize.getMediaSizeForName(JAVAXSIZES[b]) != null) {
          mediaSizeName = JAVAXSIZES[b];
          break;
        } 
        mediaSizeName = new CustomMediaSizeName(SIZES[b].toString());
        float f1 = (float)Math.rint(WIDTHS[b] / 72.0D);
        float f2 = (float)Math.rint(LENGTHS[b] / 72.0D);
        if (f1 > 0.0D && f2 > 0.0D)
          new MediaSize(f1, f2, 25400, mediaSizeName); 
        break;
      } 
    } 
    return mediaSizeName;
  }
  
  public static PageAttributes.MediaType unMapMedia(MediaSizeName paramMediaSizeName) {
    PageAttributes.MediaType mediaType = null;
    int i = Math.min(SIZES.length, JAVAXSIZES.length);
    for (byte b = 0; b < i; b++) {
      if (JAVAXSIZES[b] == paramMediaSizeName && SIZES[b] != null) {
        mediaType = SIZES[b];
        break;
      } 
    } 
    return mediaType;
  }
  
  private void translateInputProps() {
    if (this.props == null)
      return; 
    String str = this.props.getProperty("awt.print.destination");
    if (str != null)
      if (str.equals("printer")) {
        this.jobAttributes.setDestination(JobAttributes.DestinationType.PRINTER);
      } else if (str.equals("file")) {
        this.jobAttributes.setDestination(JobAttributes.DestinationType.FILE);
      }  
    str = this.props.getProperty("awt.print.printer");
    if (str != null)
      this.jobAttributes.setPrinter(str); 
    str = this.props.getProperty("awt.print.fileName");
    if (str != null)
      this.jobAttributes.setFileName(str); 
    str = this.props.getProperty("awt.print.numCopies");
    if (str != null)
      this.jobAttributes.setCopies(Integer.parseInt(str)); 
    this.options = this.props.getProperty("awt.print.options", "");
    str = this.props.getProperty("awt.print.orientation");
    if (str != null)
      if (str.equals("portrait")) {
        this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.PORTRAIT);
      } else if (str.equals("landscape")) {
        this.pageAttributes.setOrientationRequested(PageAttributes.OrientationRequestedType.LANDSCAPE);
      }  
    str = this.props.getProperty("awt.print.paperSize");
    if (str != null)
      if (str.equals("letter")) {
        this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LETTER.hashCode()]);
      } else if (str.equals("legal")) {
        this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.LEGAL.hashCode()]);
      } else if (str.equals("executive")) {
        this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.EXECUTIVE.hashCode()]);
      } else if (str.equals("a4")) {
        this.pageAttributes.setMedia(SIZES[PageAttributes.MediaType.A4.hashCode()]);
      }  
  }
  
  private void translateOutputProps() {
    if (this.props == null)
      return; 
    this.props.setProperty("awt.print.destination", (this.jobAttributes.getDestination() == JobAttributes.DestinationType.PRINTER) ? "printer" : "file");
    String str = this.jobAttributes.getPrinter();
    if (str != null && !str.equals(""))
      this.props.setProperty("awt.print.printer", str); 
    str = this.jobAttributes.getFileName();
    if (str != null && !str.equals(""))
      this.props.setProperty("awt.print.fileName", str); 
    int i = this.jobAttributes.getCopies();
    if (i > 0)
      this.props.setProperty("awt.print.numCopies", "" + i); 
    str = this.options;
    if (str != null && !str.equals(""))
      this.props.setProperty("awt.print.options", str); 
    this.props.setProperty("awt.print.orientation", (this.pageAttributes.getOrientationRequested() == PageAttributes.OrientationRequestedType.PORTRAIT) ? "portrait" : "landscape");
    PageAttributes.MediaType mediaType = SIZES[this.pageAttributes.getMedia().hashCode()];
    if (mediaType == PageAttributes.MediaType.LETTER) {
      str = "letter";
    } else if (mediaType == PageAttributes.MediaType.LEGAL) {
      str = "legal";
    } else if (mediaType == PageAttributes.MediaType.EXECUTIVE) {
      str = "executive";
    } else if (mediaType == PageAttributes.MediaType.A4) {
      str = "a4";
    } else {
      str = mediaType.toString();
    } 
    this.props.setProperty("awt.print.paperSize", str);
  }
  
  private void throwPrintToFile() {
    SecurityManager securityManager = System.getSecurityManager();
    FilePermission filePermission = null;
    if (securityManager != null) {
      if (filePermission == null)
        filePermission = new FilePermission("<<ALL FILES>>", "read,write"); 
      securityManager.checkPermission(filePermission);
    } 
  }
  
  private class MessageQ {
    private String qid = "noname";
    
    private ArrayList queue = new ArrayList();
    
    MessageQ(String param1String) { this.qid = param1String; }
    
    void closeWhenEmpty() {
      while (this.queue != null && this.queue.size() > 0) {
        try {
          wait(1000L);
        } catch (InterruptedException interruptedException) {}
      } 
      this.queue = null;
      notifyAll();
    }
    
    void close() {
      this.queue = null;
      notifyAll();
    }
    
    boolean append(Graphics2D param1Graphics2D) {
      boolean bool = false;
      if (this.queue != null) {
        this.queue.add(param1Graphics2D);
        bool = true;
        notify();
      } 
      return bool;
    }
    
    Graphics2D pop() {
      Graphics2D graphics2D = null;
      while (graphics2D == null && this.queue != null) {
        if (this.queue.size() > 0) {
          graphics2D = (Graphics2D)this.queue.remove(0);
          notify();
          continue;
        } 
        try {
          wait(2000L);
        } catch (InterruptedException interruptedException) {}
      } 
      return graphics2D;
    }
    
    boolean isClosed() { return (this.queue == null); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PrintJob2D.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
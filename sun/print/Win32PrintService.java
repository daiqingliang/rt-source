package sun.print;

import java.awt.Window;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;
import sun.awt.windows.WPrinterJob;

public class Win32PrintService implements PrintService, AttributeUpdater, SunPrinterJobService {
  public static MediaSize[] predefMedia = Win32MediaSize.getPredefMedia();
  
  private static final DocFlavor[] supportedFlavors = { 
      DocFlavor.BYTE_ARRAY.GIF, DocFlavor.INPUT_STREAM.GIF, DocFlavor.URL.GIF, DocFlavor.BYTE_ARRAY.JPEG, DocFlavor.INPUT_STREAM.JPEG, DocFlavor.URL.JPEG, DocFlavor.BYTE_ARRAY.PNG, DocFlavor.INPUT_STREAM.PNG, DocFlavor.URL.PNG, DocFlavor.SERVICE_FORMATTED.PAGEABLE, 
      DocFlavor.SERVICE_FORMATTED.PRINTABLE, DocFlavor.BYTE_ARRAY.AUTOSENSE, DocFlavor.URL.AUTOSENSE, DocFlavor.INPUT_STREAM.AUTOSENSE };
  
  private static final Class[] serviceAttrCats = { PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class, ColorSupported.class };
  
  private static Class[] otherAttrCats = { 
      JobName.class, RequestingUserName.class, Copies.class, Destination.class, OrientationRequested.class, PageRanges.class, Media.class, MediaPrintableArea.class, Fidelity.class, SheetCollate.class, 
      SunAlternateMedia.class, Chromaticity.class };
  
  public static final MediaSizeName[] dmPaperToPrintService = { 
      MediaSizeName.NA_LETTER, MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.INVOICE, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A4, 
      MediaSizeName.ISO_A5, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.B, MediaSizeName.NA_LETTER, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, 
      MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ISO_C5, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, 
      MediaSizeName.ISO_C6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, 
      MediaSizeName.FOLIO, MediaSizeName.ISO_B4, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.NA_9X11_ENVELOPE };
  
  private static final MediaTray[] dmPaperBinToPrintService = { 
      MediaTray.TOP, MediaTray.BOTTOM, MediaTray.MIDDLE, MediaTray.MANUAL, MediaTray.ENVELOPE, Win32MediaTray.ENVELOPE_MANUAL, Win32MediaTray.AUTO, Win32MediaTray.TRACTOR, Win32MediaTray.SMALL_FORMAT, Win32MediaTray.LARGE_FORMAT, 
      MediaTray.LARGE_CAPACITY, null, null, MediaTray.MAIN, Win32MediaTray.FORMSOURCE };
  
  private static int DM_PAPERSIZE = 2;
  
  private static int DM_PRINTQUALITY = 1024;
  
  private static int DM_YRESOLUTION = 8192;
  
  private static final int DMRES_MEDIUM = -3;
  
  private static final int DMRES_HIGH = -4;
  
  private static final int DMORIENT_LANDSCAPE = 2;
  
  private static final int DMDUP_VERTICAL = 2;
  
  private static final int DMDUP_HORIZONTAL = 3;
  
  private static final int DMCOLLATE_TRUE = 1;
  
  private static final int DMCOLOR_MONOCHROME = 1;
  
  private static final int DMCOLOR_COLOR = 2;
  
  private static final int DMPAPER_A2 = 66;
  
  private static final int DMPAPER_A6 = 70;
  
  private static final int DMPAPER_B6_JIS = 88;
  
  private static final int DEVCAP_COLOR = 1;
  
  private static final int DEVCAP_DUPLEX = 2;
  
  private static final int DEVCAP_COLLATE = 4;
  
  private static final int DEVCAP_QUALITY = 8;
  
  private static final int DEVCAP_POSTSCRIPT = 16;
  
  private String printer;
  
  private PrinterName name;
  
  private String port;
  
  private PrintServiceAttributeSet lastSet;
  
  private ServiceNotifier notifier = null;
  
  private MediaSizeName[] mediaSizeNames;
  
  private MediaPrintableArea[] mediaPrintables;
  
  private MediaTray[] mediaTrays;
  
  private PrinterResolution[] printRes;
  
  private HashMap mpaMap;
  
  private int nCopies;
  
  private int prnCaps;
  
  private int[] defaultSettings;
  
  private boolean gotTrays;
  
  private boolean gotCopies;
  
  private boolean mediaInitialized;
  
  private boolean mpaListInitialized;
  
  private ArrayList idList;
  
  private MediaSize[] mediaSizes;
  
  private boolean isInvalid;
  
  private Win32DocumentPropertiesUI docPropertiesUI = null;
  
  private Win32ServiceUIFactory uiFactory = null;
  
  Win32PrintService(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("null printer name"); 
    this.printer = paramString;
    this.mediaInitialized = false;
    this.gotTrays = false;
    this.gotCopies = false;
    this.isInvalid = false;
    this.printRes = null;
    this.prnCaps = 0;
    this.defaultSettings = null;
    this.port = null;
  }
  
  public void invalidateService() { this.isInvalid = true; }
  
  public String getName() { return this.printer; }
  
  private PrinterName getPrinterName() {
    if (this.name == null)
      this.name = new PrinterName(this.printer, null); 
    return this.name;
  }
  
  public int findPaperID(MediaSizeName paramMediaSizeName) {
    if (paramMediaSizeName instanceof Win32MediaSize) {
      Win32MediaSize win32MediaSize = (Win32MediaSize)paramMediaSizeName;
      return win32MediaSize.getDMPaper();
    } 
    byte b;
    for (b = 0; b < dmPaperToPrintService.length; b++) {
      if (dmPaperToPrintService[b].equals(paramMediaSizeName))
        return b + true; 
    } 
    if (paramMediaSizeName.equals(MediaSizeName.ISO_A2))
      return 66; 
    if (paramMediaSizeName.equals(MediaSizeName.ISO_A6))
      return 70; 
    if (paramMediaSizeName.equals(MediaSizeName.JIS_B6))
      return 88; 
    initMedia();
    if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length)
      for (b = 0; b < this.idList.size(); b++) {
        if (this.mediaSizes[b].getMediaSizeName() == paramMediaSizeName)
          return ((Integer)this.idList.get(b)).intValue(); 
      }  
    return 0;
  }
  
  public int findTrayID(MediaTray paramMediaTray) {
    getMediaTrays();
    if (paramMediaTray instanceof Win32MediaTray) {
      Win32MediaTray win32MediaTray = (Win32MediaTray)paramMediaTray;
      return win32MediaTray.getDMBinID();
    } 
    for (byte b = 0; b < dmPaperBinToPrintService.length; b++) {
      if (paramMediaTray.equals(dmPaperBinToPrintService[b]))
        return b + true; 
    } 
    return 0;
  }
  
  public MediaTray findMediaTray(int paramInt) {
    if (paramInt >= 1 && paramInt <= dmPaperBinToPrintService.length)
      return dmPaperBinToPrintService[paramInt - 1]; 
    MediaTray[] arrayOfMediaTray = getMediaTrays();
    if (arrayOfMediaTray != null)
      for (byte b = 0; b < arrayOfMediaTray.length; b++) {
        if (arrayOfMediaTray[b] instanceof Win32MediaTray) {
          Win32MediaTray win32MediaTray = (Win32MediaTray)arrayOfMediaTray[b];
          if (win32MediaTray.winID == paramInt)
            return win32MediaTray; 
        } 
      }  
    return Win32MediaTray.AUTO;
  }
  
  public MediaSizeName findWin32Media(int paramInt) {
    if (paramInt >= 1 && paramInt <= dmPaperToPrintService.length)
      return dmPaperToPrintService[paramInt - 1]; 
    switch (paramInt) {
      case 66:
        return MediaSizeName.ISO_A2;
      case 70:
        return MediaSizeName.ISO_A6;
      case 88:
        return MediaSizeName.JIS_B6;
    } 
    return null;
  }
  
  private boolean addToUniqueList(ArrayList paramArrayList, MediaSizeName paramMediaSizeName) {
    for (byte b = 0; b < paramArrayList.size(); b++) {
      MediaSizeName mediaSizeName = (MediaSizeName)paramArrayList.get(b);
      if (mediaSizeName == paramMediaSizeName)
        return false; 
    } 
    paramArrayList.add(paramMediaSizeName);
    return true;
  }
  
  private void initMedia() {
    if (this.mediaInitialized == true)
      return; 
    this.mediaInitialized = true;
    int[] arrayOfInt = getAllMediaIDs(this.printer, getPort());
    if (arrayOfInt == null)
      return; 
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    ArrayList arrayList3 = new ArrayList();
    boolean bool = false;
    this.idList = new ArrayList();
    for (byte b1 = 0; b1 < arrayOfInt.length; b1++)
      this.idList.add(Integer.valueOf(arrayOfInt[b1])); 
    ArrayList arrayList4 = new ArrayList();
    this.mediaSizes = getMediaSizes(this.idList, arrayOfInt, arrayList4);
    for (byte b2 = 0; b2 < this.idList.size(); b2++) {
      MediaSizeName mediaSizeName = findWin32Media(((Integer)this.idList.get(b2)).intValue());
      if (mediaSizeName != null && this.idList.size() == this.mediaSizes.length) {
        MediaSize mediaSize1 = MediaSize.getMediaSizeForName(mediaSizeName);
        MediaSize mediaSize2 = this.mediaSizes[b2];
        char c = '৬';
        if (Math.abs(mediaSize1.getX(1) - mediaSize2.getX(1)) > c || Math.abs(mediaSize1.getY(1) - mediaSize2.getY(1)) > c)
          mediaSizeName = null; 
      } 
      boolean bool2 = (mediaSizeName != null) ? 1 : 0;
      if (mediaSizeName == null && this.idList.size() == this.mediaSizes.length)
        mediaSizeName = this.mediaSizes[b2].getMediaSizeName(); 
      boolean bool1 = false;
      if (mediaSizeName != null)
        bool1 = addToUniqueList(arrayList1, mediaSizeName); 
      if ((!bool2 || !bool1) && this.idList.size() == arrayList4.size()) {
        Win32MediaSize win32MediaSize = Win32MediaSize.findMediaName((String)arrayList4.get(b2));
        if (win32MediaSize == null && this.idList.size() == this.mediaSizes.length) {
          win32MediaSize = new Win32MediaSize((String)arrayList4.get(b2), ((Integer)this.idList.get(b2)).intValue());
          this.mediaSizes[b2] = new MediaSize(this.mediaSizes[b2].getX(1000), this.mediaSizes[b2].getY(1000), 1000, win32MediaSize);
        } 
        if (win32MediaSize != null && win32MediaSize != mediaSizeName)
          if (!bool1) {
            bool1 = addToUniqueList(arrayList1, mediaSizeName = win32MediaSize);
          } else {
            arrayList2.add(win32MediaSize);
          }  
      } 
    } 
    for (Win32MediaSize win32MediaSize : arrayList2)
      boolean bool1 = addToUniqueList(arrayList1, win32MediaSize); 
    this.mediaSizeNames = new MediaSizeName[arrayList1.size()];
    arrayList1.toArray(this.mediaSizeNames);
  }
  
  private MediaPrintableArea[] getMediaPrintables(MediaSizeName paramMediaSizeName) {
    MediaSizeName[] arrayOfMediaSizeName;
    if (paramMediaSizeName == null) {
      if (this.mpaListInitialized == true)
        return this.mediaPrintables; 
    } else if (this.mpaMap != null && this.mpaMap.get(paramMediaSizeName) != null) {
      arrayOfMediaSizeName = new MediaPrintableArea[1];
      arrayOfMediaSizeName[0] = (MediaPrintableArea)this.mpaMap.get(paramMediaSizeName);
      return arrayOfMediaSizeName;
    } 
    initMedia();
    if (this.mediaSizeNames == null || this.mediaSizeNames.length == 0)
      return null; 
    if (paramMediaSizeName != null) {
      arrayOfMediaSizeName = new MediaSizeName[1];
      arrayOfMediaSizeName[0] = paramMediaSizeName;
    } else {
      arrayOfMediaSizeName = this.mediaSizeNames;
    } 
    if (this.mpaMap == null)
      this.mpaMap = new HashMap(); 
    for (byte b = 0; b < arrayOfMediaSizeName.length; b++) {
      MediaSizeName mediaSizeName = arrayOfMediaSizeName[b];
      if (this.mpaMap.get(mediaSizeName) == null && mediaSizeName != null) {
        int i = findPaperID(mediaSizeName);
        float[] arrayOfFloat = (i != 0) ? getMediaPrintableArea(this.printer, i) : null;
        MediaPrintableArea mediaPrintableArea = null;
        if (arrayOfFloat != null) {
          try {
            mediaPrintableArea = new MediaPrintableArea(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], 25400);
            this.mpaMap.put(mediaSizeName, mediaPrintableArea);
          } catch (IllegalArgumentException illegalArgumentException) {}
        } else {
          MediaSize mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
          if (mediaSize != null)
            try {
              mediaPrintableArea = new MediaPrintableArea(0.0F, 0.0F, mediaSize.getX(25400), mediaSize.getY(25400), 25400);
              this.mpaMap.put(mediaSizeName, mediaPrintableArea);
            } catch (IllegalArgumentException illegalArgumentException) {} 
        } 
      } 
    } 
    if (this.mpaMap.size() == 0)
      return null; 
    if (paramMediaSizeName != null) {
      if (this.mpaMap.get(paramMediaSizeName) == null)
        return null; 
      MediaPrintableArea[] arrayOfMediaPrintableArea = new MediaPrintableArea[1];
      arrayOfMediaPrintableArea[0] = (MediaPrintableArea)this.mpaMap.get(paramMediaSizeName);
      return arrayOfMediaPrintableArea;
    } 
    this.mediaPrintables = (MediaPrintableArea[])this.mpaMap.values().toArray(new MediaPrintableArea[0]);
    this.mpaListInitialized = true;
    return this.mediaPrintables;
  }
  
  private MediaTray[] getMediaTrays() {
    if (this.gotTrays == true && this.mediaTrays != null)
      return this.mediaTrays; 
    String str = getPort();
    int[] arrayOfInt = getAllMediaTrays(this.printer, str);
    String[] arrayOfString = getAllMediaTrayNames(this.printer, str);
    if (arrayOfInt == null || arrayOfString == null)
      return null; 
    byte b1 = 0;
    for (byte b2 = 0; b2 < arrayOfInt.length; b2++) {
      if (arrayOfInt[b2] > 0)
        b1++; 
    } 
    MediaTray[] arrayOfMediaTray = new MediaTray[b1];
    byte b3 = 0;
    byte b4 = 0;
    while (b3 < Math.min(arrayOfInt.length, arrayOfString.length)) {
      int i = arrayOfInt[b3];
      if (i > 0)
        if (i > dmPaperBinToPrintService.length || dmPaperBinToPrintService[i - true] == null) {
          arrayOfMediaTray[b4++] = new Win32MediaTray(i, arrayOfString[b3]);
        } else {
          arrayOfMediaTray[b4++] = dmPaperBinToPrintService[i - 1];
        }  
      b3++;
    } 
    this.mediaTrays = arrayOfMediaTray;
    this.gotTrays = true;
    return this.mediaTrays;
  }
  
  private boolean isSameSize(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    float f1 = paramFloat1 - paramFloat3;
    float f2 = paramFloat2 - paramFloat4;
    float f3 = paramFloat1 - paramFloat4;
    float f4 = paramFloat2 - paramFloat3;
    return ((Math.abs(f1) <= 1.0F && Math.abs(f2) <= 1.0F) || (Math.abs(f3) <= 1.0F && Math.abs(f4) <= 1.0F));
  }
  
  public MediaSizeName findMatchingMediaSizeNameMM(float paramFloat1, float paramFloat2) {
    if (predefMedia != null)
      for (byte b = 0; b < predefMedia.length; b++) {
        if (predefMedia[b] != null && isSameSize(predefMedia[b].getX(1000), predefMedia[b].getY(1000), paramFloat1, paramFloat2))
          return predefMedia[b].getMediaSizeName(); 
      }  
    return null;
  }
  
  private MediaSize[] getMediaSizes(ArrayList paramArrayList1, int[] paramArrayOfInt, ArrayList<String> paramArrayList2) {
    if (paramArrayList2 == null)
      paramArrayList2 = new ArrayList<String>(); 
    String str = getPort();
    int[] arrayOfInt = getAllMediaSizes(this.printer, str);
    String[] arrayOfString = getAllMediaNames(this.printer, str);
    MediaSizeName mediaSizeName = null;
    MediaSize mediaSize = null;
    if (arrayOfInt == null || arrayOfString == null)
      return null; 
    int i = arrayOfInt.length / 2;
    ArrayList arrayList = new ArrayList();
    byte b = 0;
    while (b < i) {
      float f1 = arrayOfInt[b * 2] / 10.0F;
      float f2 = arrayOfInt[b * 2 + 1] / 10.0F;
      if (f1 <= 0.0F || f2 <= 0.0F) {
        if (i == paramArrayOfInt.length) {
          Integer integer = Integer.valueOf(paramArrayOfInt[b]);
          paramArrayList1.remove(paramArrayList1.indexOf(integer));
        } 
      } else {
        mediaSizeName = findMatchingMediaSizeNameMM(f1, f2);
        if (mediaSizeName != null)
          mediaSize = MediaSize.getMediaSizeForName(mediaSizeName); 
        if (mediaSize != null) {
          arrayList.add(mediaSize);
          paramArrayList2.add(arrayOfString[b]);
        } else {
          Win32MediaSize win32MediaSize = Win32MediaSize.findMediaName(arrayOfString[b]);
          if (win32MediaSize == null)
            win32MediaSize = new Win32MediaSize(arrayOfString[b], paramArrayOfInt[b]); 
          try {
            mediaSize = new MediaSize(f1, f2, 1000, win32MediaSize);
            arrayList.add(mediaSize);
            paramArrayList2.add(arrayOfString[b]);
          } catch (IllegalArgumentException illegalArgumentException) {
            if (i == paramArrayOfInt.length) {
              Integer integer = Integer.valueOf(paramArrayOfInt[b]);
              paramArrayList1.remove(paramArrayList1.indexOf(integer));
            } 
          } 
        } 
      } 
      b++;
      mediaSize = null;
    } 
    MediaSize[] arrayOfMediaSize = new MediaSize[arrayList.size()];
    arrayList.toArray(arrayOfMediaSize);
    return arrayOfMediaSize;
  }
  
  private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs() { return (getJobStatus(this.printer, 2) != 1) ? PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS : PrinterIsAcceptingJobs.ACCEPTING_JOBS; }
  
  private PrinterState getPrinterState() { return this.isInvalid ? PrinterState.STOPPED : null; }
  
  private PrinterStateReasons getPrinterStateReasons() {
    if (this.isInvalid) {
      PrinterStateReasons printerStateReasons = new PrinterStateReasons();
      printerStateReasons.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
      return printerStateReasons;
    } 
    return null;
  }
  
  private QueuedJobCount getQueuedJobCount() {
    int i = getJobStatus(this.printer, 1);
    return (i != -1) ? new QueuedJobCount(i) : new QueuedJobCount(0);
  }
  
  private boolean isSupportedCopies(Copies paramCopies) {
    synchronized (this) {
      if (!this.gotCopies) {
        this.nCopies = getCopiesSupported(this.printer, getPort());
        this.gotCopies = true;
      } 
    } 
    int i = paramCopies.getValue();
    return (i > 0 && i <= this.nCopies);
  }
  
  private boolean isSupportedMedia(MediaSizeName paramMediaSizeName) {
    initMedia();
    if (this.mediaSizeNames != null)
      for (byte b = 0; b < this.mediaSizeNames.length; b++) {
        if (paramMediaSizeName.equals(this.mediaSizeNames[b]))
          return true; 
      }  
    return false;
  }
  
  private boolean isSupportedMediaPrintableArea(MediaPrintableArea paramMediaPrintableArea) {
    getMediaPrintables(null);
    if (this.mediaPrintables != null)
      for (byte b = 0; b < this.mediaPrintables.length; b++) {
        if (paramMediaPrintableArea.equals(this.mediaPrintables[b]))
          return true; 
      }  
    return false;
  }
  
  private boolean isSupportedMediaTray(MediaTray paramMediaTray) {
    MediaTray[] arrayOfMediaTray = getMediaTrays();
    if (arrayOfMediaTray != null)
      for (byte b = 0; b < arrayOfMediaTray.length; b++) {
        if (paramMediaTray.equals(arrayOfMediaTray[b]))
          return true; 
      }  
    return false;
  }
  
  private int getPrinterCapabilities() {
    if (this.prnCaps == 0)
      this.prnCaps = getCapabilities(this.printer, getPort()); 
    return this.prnCaps;
  }
  
  private String getPort() {
    if (this.port == null)
      this.port = getPrinterPort(this.printer); 
    return this.port;
  }
  
  private int[] getDefaultPrinterSettings() {
    if (this.defaultSettings == null)
      this.defaultSettings = getDefaultSettings(this.printer, getPort()); 
    return this.defaultSettings;
  }
  
  private PrinterResolution[] getPrintResolutions() {
    if (this.printRes == null) {
      int[] arrayOfInt = getAllResolutions(this.printer, getPort());
      if (arrayOfInt == null) {
        this.printRes = new PrinterResolution[0];
      } else {
        int i = arrayOfInt.length / 2;
        ArrayList arrayList = new ArrayList();
        for (byte b = 0; b < i; b++) {
          try {
            PrinterResolution printerResolution = new PrinterResolution(arrayOfInt[b * 2], arrayOfInt[b * 2 + 1], 100);
            arrayList.add(printerResolution);
          } catch (IllegalArgumentException illegalArgumentException) {}
        } 
        this.printRes = (PrinterResolution[])arrayList.toArray(new PrinterResolution[arrayList.size()]);
      } 
    } 
    return this.printRes;
  }
  
  private boolean isSupportedResolution(PrinterResolution paramPrinterResolution) {
    PrinterResolution[] arrayOfPrinterResolution = getPrintResolutions();
    if (arrayOfPrinterResolution != null)
      for (byte b = 0; b < arrayOfPrinterResolution.length; b++) {
        if (paramPrinterResolution.equals(arrayOfPrinterResolution[b]))
          return true; 
      }  
    return false;
  }
  
  public DocPrintJob createPrintJob() {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      securityManager.checkPrintJobAccess(); 
    return new Win32PrintJob(this);
  }
  
  private PrintServiceAttributeSet getDynamicAttributes() {
    HashPrintServiceAttributeSet hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    hashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
    hashPrintServiceAttributeSet.add(getQueuedJobCount());
    return hashPrintServiceAttributeSet;
  }
  
  public PrintServiceAttributeSet getUpdatedAttributes() {
    PrintServiceAttributeSet printServiceAttributeSet = getDynamicAttributes();
    if (this.lastSet == null) {
      this.lastSet = printServiceAttributeSet;
      return AttributeSetUtilities.unmodifiableView(printServiceAttributeSet);
    } 
    HashPrintServiceAttributeSet hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    Attribute[] arrayOfAttribute = printServiceAttributeSet.toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      Attribute attribute = arrayOfAttribute[b];
      if (!this.lastSet.containsValue(attribute))
        hashPrintServiceAttributeSet.add(attribute); 
    } 
    this.lastSet = printServiceAttributeSet;
    return AttributeSetUtilities.unmodifiableView(hashPrintServiceAttributeSet);
  }
  
  public void wakeNotifier() {
    synchronized (this) {
      if (this.notifier != null)
        this.notifier.wake(); 
    } 
  }
  
  public void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {
    synchronized (this) {
      if (paramPrintServiceAttributeListener == null)
        return; 
      if (this.notifier == null)
        this.notifier = new ServiceNotifier(this); 
      this.notifier.addListener(paramPrintServiceAttributeListener);
    } 
  }
  
  public void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {
    synchronized (this) {
      if (paramPrintServiceAttributeListener == null || this.notifier == null)
        return; 
      this.notifier.removeListener(paramPrintServiceAttributeListener);
      if (this.notifier.isEmpty()) {
        this.notifier.stopNotifier();
        this.notifier = null;
      } 
    } 
  }
  
  public <T extends javax.print.attribute.PrintServiceAttribute> T getAttribute(Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("category"); 
    if (!javax.print.attribute.PrintServiceAttribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException("Not a PrintServiceAttribute"); 
    if (paramClass == ColorSupported.class) {
      int i = getPrinterCapabilities();
      return ((i & true) != 0) ? (T)ColorSupported.SUPPORTED : (T)ColorSupported.NOT_SUPPORTED;
    } 
    return (paramClass == PrinterName.class) ? (T)getPrinterName() : ((paramClass == PrinterState.class) ? (T)getPrinterState() : ((paramClass == PrinterStateReasons.class) ? (T)getPrinterStateReasons() : ((paramClass == QueuedJobCount.class) ? (T)getQueuedJobCount() : ((paramClass == PrinterIsAcceptingJobs.class) ? (T)getPrinterIsAcceptingJobs() : null))));
  }
  
  public PrintServiceAttributeSet getAttributes() {
    HashPrintServiceAttributeSet hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    hashPrintServiceAttributeSet.add(getPrinterName());
    hashPrintServiceAttributeSet.add(getPrinterIsAcceptingJobs());
    PrinterState printerState = getPrinterState();
    if (printerState != null)
      hashPrintServiceAttributeSet.add(printerState); 
    PrinterStateReasons printerStateReasons = getPrinterStateReasons();
    if (printerStateReasons != null)
      hashPrintServiceAttributeSet.add(printerStateReasons); 
    hashPrintServiceAttributeSet.add(getQueuedJobCount());
    int i = getPrinterCapabilities();
    if ((i & true) != 0) {
      hashPrintServiceAttributeSet.add(ColorSupported.SUPPORTED);
    } else {
      hashPrintServiceAttributeSet.add(ColorSupported.NOT_SUPPORTED);
    } 
    return AttributeSetUtilities.unmodifiableView(hashPrintServiceAttributeSet);
  }
  
  public DocFlavor[] getSupportedDocFlavors() {
    DocFlavor[] arrayOfDocFlavor;
    int i = supportedFlavors.length;
    int j = getPrinterCapabilities();
    if ((j & 0x10) != 0) {
      arrayOfDocFlavor = new DocFlavor[i + 3];
      System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
      arrayOfDocFlavor[i] = DocFlavor.BYTE_ARRAY.POSTSCRIPT;
      arrayOfDocFlavor[i + 1] = DocFlavor.INPUT_STREAM.POSTSCRIPT;
      arrayOfDocFlavor[i + 2] = DocFlavor.URL.POSTSCRIPT;
    } else {
      arrayOfDocFlavor = new DocFlavor[i];
      System.arraycopy(supportedFlavors, 0, arrayOfDocFlavor, 0, i);
    } 
    return arrayOfDocFlavor;
  }
  
  public boolean isDocFlavorSupported(DocFlavor paramDocFlavor) {
    DocFlavor[] arrayOfDocFlavor;
    if (isPostScriptFlavor(paramDocFlavor)) {
      arrayOfDocFlavor = getSupportedDocFlavors();
    } else {
      arrayOfDocFlavor = supportedFlavors;
    } 
    for (byte b = 0; b < arrayOfDocFlavor.length; b++) {
      if (paramDocFlavor.equals(arrayOfDocFlavor[b]))
        return true; 
    } 
    return false;
  }
  
  public Class<?>[] getSupportedAttributeCategories() {
    ArrayList arrayList = new ArrayList(otherAttrCats.length + 3);
    int i;
    for (i = 0; i < otherAttrCats.length; i++)
      arrayList.add(otherAttrCats[i]); 
    i = getPrinterCapabilities();
    if ((i & 0x2) != 0)
      arrayList.add(Sides.class); 
    if ((i & 0x8) != 0) {
      int[] arrayOfInt = getDefaultPrinterSettings();
      if (arrayOfInt[3] >= -4 && arrayOfInt[3] < 0)
        arrayList.add(PrintQuality.class); 
    } 
    PrinterResolution[] arrayOfPrinterResolution = getPrintResolutions();
    if (arrayOfPrinterResolution != null && arrayOfPrinterResolution.length > 0)
      arrayList.add(PrinterResolution.class); 
    return (Class[])arrayList.toArray(new Class[arrayList.size()]);
  }
  
  public boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null category"); 
    if (!Attribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass + " is not an Attribute"); 
    Class[] arrayOfClass = getSupportedAttributeCategories();
    for (byte b = 0; b < arrayOfClass.length; b++) {
      if (paramClass.equals(arrayOfClass[b]))
        return true; 
    } 
    return false;
  }
  
  public Object getDefaultAttributeValue(Class<? extends Attribute> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null category"); 
    if (!Attribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass + " is not an Attribute"); 
    if (!isAttributeCategorySupported(paramClass))
      return null; 
    int[] arrayOfInt = getDefaultPrinterSettings();
    int i = arrayOfInt[0];
    int j = arrayOfInt[2];
    int k = arrayOfInt[3];
    int m = arrayOfInt[4];
    int n = arrayOfInt[5];
    int i1 = arrayOfInt[6];
    int i2 = arrayOfInt[7];
    int i3 = arrayOfInt[8];
    if (paramClass == Copies.class)
      return (m > 0) ? new Copies(m) : new Copies(1); 
    if (paramClass == Chromaticity.class)
      return (i3 == 2) ? Chromaticity.COLOR : Chromaticity.MONOCHROME; 
    if (paramClass == JobName.class)
      return new JobName("Java Printing", null); 
    if (paramClass == OrientationRequested.class)
      return (n == 2) ? OrientationRequested.LANDSCAPE : OrientationRequested.PORTRAIT; 
    if (paramClass == PageRanges.class)
      return new PageRanges(1, 2147483647); 
    if (paramClass == Media.class) {
      MediaSizeName mediaSizeName = findWin32Media(i);
      if (mediaSizeName != null) {
        if (!isSupportedMedia(mediaSizeName) && this.mediaSizeNames != null) {
          mediaSizeName = this.mediaSizeNames[0];
          i = findPaperID(mediaSizeName);
        } 
        return mediaSizeName;
      } 
      initMedia();
      if (this.mediaSizeNames != null && this.mediaSizeNames.length > 0) {
        if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length) {
          Integer integer = Integer.valueOf(i);
          int i4 = this.idList.indexOf(integer);
          if (i4 >= 0 && i4 < this.mediaSizes.length)
            return this.mediaSizes[i4].getMediaSizeName(); 
        } 
        return this.mediaSizeNames[0];
      } 
    } else {
      if (paramClass == MediaPrintableArea.class) {
        MediaSizeName mediaSizeName = findWin32Media(i);
        if (mediaSizeName != null && !isSupportedMedia(mediaSizeName) && this.mediaSizeNames != null)
          i = findPaperID(this.mediaSizeNames[0]); 
        float[] arrayOfFloat = getMediaPrintableArea(this.printer, i);
        if (arrayOfFloat != null) {
          MediaPrintableArea mediaPrintableArea = null;
          try {
            mediaPrintableArea = new MediaPrintableArea(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2], arrayOfFloat[3], 25400);
          } catch (IllegalArgumentException illegalArgumentException) {}
          return mediaPrintableArea;
        } 
        return null;
      } 
      if (paramClass == SunAlternateMedia.class)
        return null; 
      if (paramClass == Destination.class)
        try {
          return new Destination((new File("out.prn")).toURI());
        } catch (SecurityException securityException) {
          try {
            return new Destination(new URI("file:out.prn"));
          } catch (URISyntaxException uRISyntaxException) {
            return null;
          } 
        }  
      if (paramClass == Sides.class) {
        switch (i1) {
          case 2:
            return Sides.TWO_SIDED_LONG_EDGE;
          case 3:
            return Sides.TWO_SIDED_SHORT_EDGE;
        } 
        return Sides.ONE_SIDED;
      } 
      if (paramClass == PrinterResolution.class) {
        int i4 = j;
        int i5 = k;
        if (i5 < 0 || i4 < 0) {
          int i6 = (i4 > i5) ? i4 : i5;
          if (i6 > 0)
            return new PrinterResolution(i6, i6, 100); 
        } else {
          return new PrinterResolution(i5, i4, 100);
        } 
      } else {
        if (paramClass == ColorSupported.class) {
          int i4 = getPrinterCapabilities();
          return ((i4 & true) != 0) ? ColorSupported.SUPPORTED : ColorSupported.NOT_SUPPORTED;
        } 
        if (paramClass == PrintQuality.class) {
          if (k < 0 && k >= -4) {
            switch (k) {
              case -4:
                return PrintQuality.HIGH;
              case -3:
                return PrintQuality.NORMAL;
            } 
            return PrintQuality.DRAFT;
          } 
        } else {
          if (paramClass == RequestingUserName.class) {
            String str = "";
            try {
              str = System.getProperty("user.name", "");
            } catch (SecurityException securityException) {}
            return new RequestingUserName(str, null);
          } 
          if (paramClass == SheetCollate.class)
            return (i2 == 1) ? SheetCollate.COLLATED : SheetCollate.UNCOLLATED; 
          if (paramClass == Fidelity.class)
            return Fidelity.FIDELITY_FALSE; 
        } 
      } 
    } 
    return null;
  }
  
  private boolean isPostScriptFlavor(DocFlavor paramDocFlavor) { return (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.POSTSCRIPT) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.POSTSCRIPT) || paramDocFlavor.equals(DocFlavor.URL.POSTSCRIPT)); }
  
  private boolean isPSDocAttr(Class paramClass) { return (paramClass == OrientationRequested.class || paramClass == Copies.class); }
  
  private boolean isAutoSense(DocFlavor paramDocFlavor) { return (paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.AUTOSENSE) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.AUTOSENSE) || paramDocFlavor.equals(DocFlavor.URL.AUTOSENSE)); }
  
  public Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    if (paramClass == null)
      throw new NullPointerException("null category"); 
    if (!Attribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass + " does not implement Attribute"); 
    if (paramDocFlavor != null) {
      if (!isDocFlavorSupported(paramDocFlavor))
        throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor"); 
      if (isAutoSense(paramDocFlavor) || (isPostScriptFlavor(paramDocFlavor) && isPSDocAttr(paramClass)))
        return null; 
    } 
    if (!isAttributeCategorySupported(paramClass))
      return null; 
    if (paramClass == JobName.class)
      return new JobName("Java Printing", null); 
    if (paramClass == RequestingUserName.class) {
      String str = "";
      try {
        str = System.getProperty("user.name", "");
      } catch (SecurityException securityException) {}
      return new RequestingUserName(str, null);
    } 
    if (paramClass == ColorSupported.class) {
      int i = getPrinterCapabilities();
      return ((i & true) != 0) ? ColorSupported.SUPPORTED : ColorSupported.NOT_SUPPORTED;
    } 
    if (paramClass == Chromaticity.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || paramDocFlavor.equals(DocFlavor.URL.GIF) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || paramDocFlavor.equals(DocFlavor.URL.JPEG) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || paramDocFlavor.equals(DocFlavor.URL.PNG)) {
        int i = getPrinterCapabilities();
        if ((i & true) == 0) {
          Chromaticity[] arrayOfChromaticity1 = new Chromaticity[1];
          arrayOfChromaticity1[0] = Chromaticity.MONOCHROME;
          return arrayOfChromaticity1;
        } 
        Chromaticity[] arrayOfChromaticity = new Chromaticity[2];
        arrayOfChromaticity[0] = Chromaticity.MONOCHROME;
        arrayOfChromaticity[1] = Chromaticity.COLOR;
        return arrayOfChromaticity;
      } 
      return null;
    } 
    if (paramClass == Destination.class)
      try {
        return new Destination((new File("out.prn")).toURI());
      } catch (SecurityException securityException) {
        try {
          return new Destination(new URI("file:out.prn"));
        } catch (URISyntaxException uRISyntaxException) {
          return null;
        } 
      }  
    if (paramClass == OrientationRequested.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) || paramDocFlavor.equals(DocFlavor.URL.GIF) || paramDocFlavor.equals(DocFlavor.URL.JPEG) || paramDocFlavor.equals(DocFlavor.URL.PNG)) {
        OrientationRequested[] arrayOfOrientationRequested = new OrientationRequested[3];
        arrayOfOrientationRequested[0] = OrientationRequested.PORTRAIT;
        arrayOfOrientationRequested[1] = OrientationRequested.LANDSCAPE;
        arrayOfOrientationRequested[2] = OrientationRequested.REVERSE_LANDSCAPE;
        return arrayOfOrientationRequested;
      } 
      return null;
    } 
    if (paramClass == Copies.class || paramClass == CopiesSupported.class) {
      synchronized (this) {
        if (!this.gotCopies) {
          this.nCopies = getCopiesSupported(this.printer, getPort());
          this.gotCopies = true;
        } 
      } 
      return new CopiesSupported(1, this.nCopies);
    } 
    if (paramClass == Media.class) {
      initMedia();
      int i = (this.mediaSizeNames == null) ? 0 : this.mediaSizeNames.length;
      MediaTray[] arrayOfMediaTray = getMediaTrays();
      i += ((arrayOfMediaTray == null) ? 0 : arrayOfMediaTray.length);
      Media[] arrayOfMedia = new Media[i];
      if (this.mediaSizeNames != null)
        System.arraycopy(this.mediaSizeNames, 0, arrayOfMedia, 0, this.mediaSizeNames.length); 
      if (arrayOfMediaTray != null)
        System.arraycopy(arrayOfMediaTray, 0, arrayOfMedia, i - arrayOfMediaTray.length, arrayOfMediaTray.length); 
      return arrayOfMedia;
    } 
    if (paramClass == MediaPrintableArea.class) {
      Media media = null;
      if (paramAttributeSet != null && (media = (Media)paramAttributeSet.get(Media.class)) != null && !(media instanceof MediaSizeName))
        media = null; 
      MediaPrintableArea[] arrayOfMediaPrintableArea = getMediaPrintables((MediaSizeName)media);
      if (arrayOfMediaPrintableArea != null) {
        MediaPrintableArea[] arrayOfMediaPrintableArea1 = new MediaPrintableArea[arrayOfMediaPrintableArea.length];
        System.arraycopy(arrayOfMediaPrintableArea, 0, arrayOfMediaPrintableArea1, 0, arrayOfMediaPrintableArea.length);
        return arrayOfMediaPrintableArea1;
      } 
      return null;
    } 
    if (paramClass == SunAlternateMedia.class)
      return new SunAlternateMedia((Media)getDefaultAttributeValue(Media.class)); 
    if (paramClass == PageRanges.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        PageRanges[] arrayOfPageRanges = new PageRanges[1];
        arrayOfPageRanges[0] = new PageRanges(1, 2147483647);
        return arrayOfPageRanges;
      } 
      return null;
    } 
    if (paramClass == PrinterResolution.class) {
      PrinterResolution[] arrayOfPrinterResolution1 = getPrintResolutions();
      if (arrayOfPrinterResolution1 == null)
        return null; 
      PrinterResolution[] arrayOfPrinterResolution2 = new PrinterResolution[arrayOfPrinterResolution1.length];
      System.arraycopy(arrayOfPrinterResolution1, 0, arrayOfPrinterResolution2, 0, arrayOfPrinterResolution1.length);
      return arrayOfPrinterResolution2;
    } 
    if (paramClass == Sides.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        Sides[] arrayOfSides = new Sides[3];
        arrayOfSides[0] = Sides.ONE_SIDED;
        arrayOfSides[1] = Sides.TWO_SIDED_LONG_EDGE;
        arrayOfSides[2] = Sides.TWO_SIDED_SHORT_EDGE;
        return arrayOfSides;
      } 
      return null;
    } 
    if (paramClass == PrintQuality.class) {
      PrintQuality[] arrayOfPrintQuality = new PrintQuality[3];
      arrayOfPrintQuality[0] = PrintQuality.DRAFT;
      arrayOfPrintQuality[1] = PrintQuality.HIGH;
      arrayOfPrintQuality[2] = PrintQuality.NORMAL;
      return arrayOfPrintQuality;
    } 
    if (paramClass == SheetCollate.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        SheetCollate[] arrayOfSheetCollate = new SheetCollate[2];
        arrayOfSheetCollate[0] = SheetCollate.COLLATED;
        arrayOfSheetCollate[1] = SheetCollate.UNCOLLATED;
        return arrayOfSheetCollate;
      } 
      return null;
    } 
    if (paramClass == Fidelity.class) {
      Fidelity[] arrayOfFidelity = new Fidelity[2];
      arrayOfFidelity[0] = Fidelity.FIDELITY_FALSE;
      arrayOfFidelity[1] = Fidelity.FIDELITY_TRUE;
      return arrayOfFidelity;
    } 
    return null;
  }
  
  public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    if (paramAttribute == null)
      throw new NullPointerException("null attribute"); 
    Class clazz = paramAttribute.getCategory();
    if (paramDocFlavor != null) {
      if (!isDocFlavorSupported(paramDocFlavor))
        throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor"); 
      if (isAutoSense(paramDocFlavor) || (isPostScriptFlavor(paramDocFlavor) && isPSDocAttr(clazz)))
        return false; 
    } 
    if (!isAttributeCategorySupported(clazz))
      return false; 
    if (clazz == Chromaticity.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF) || paramDocFlavor.equals(DocFlavor.URL.GIF) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) || paramDocFlavor.equals(DocFlavor.URL.JPEG) || paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) || paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG) || paramDocFlavor.equals(DocFlavor.URL.PNG)) {
        int i = getPrinterCapabilities();
        return ((i & true) != 0) ? true : ((paramAttribute == Chromaticity.MONOCHROME));
      } 
      return false;
    } 
    if (clazz == Copies.class)
      return isSupportedCopies((Copies)paramAttribute); 
    if (clazz == Destination.class) {
      URI uRI = ((Destination)paramAttribute).getURI();
      return ("file".equals(uRI.getScheme()) && !uRI.getSchemeSpecificPart().equals(""));
    } 
    if (clazz == Media.class) {
      if (paramAttribute instanceof MediaSizeName)
        return isSupportedMedia((MediaSizeName)paramAttribute); 
      if (paramAttribute instanceof MediaTray)
        return isSupportedMediaTray((MediaTray)paramAttribute); 
    } else {
      if (clazz == MediaPrintableArea.class)
        return isSupportedMediaPrintableArea((MediaPrintableArea)paramAttribute); 
      if (clazz == SunAlternateMedia.class) {
        Media media = ((SunAlternateMedia)paramAttribute).getMedia();
        return isAttributeValueSupported(media, paramDocFlavor, paramAttributeSet);
      } 
      if (clazz == PageRanges.class || clazz == SheetCollate.class || clazz == Sides.class) {
        if (paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
          return false; 
      } else if (clazz == PrinterResolution.class) {
        if (paramAttribute instanceof PrinterResolution)
          return isSupportedResolution((PrinterResolution)paramAttribute); 
      } else if (clazz == OrientationRequested.class) {
        if (paramAttribute == OrientationRequested.REVERSE_PORTRAIT || (paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE) && !paramDocFlavor.equals(DocFlavor.INPUT_STREAM.GIF) && !paramDocFlavor.equals(DocFlavor.INPUT_STREAM.JPEG) && !paramDocFlavor.equals(DocFlavor.INPUT_STREAM.PNG) && !paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.GIF) && !paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.JPEG) && !paramDocFlavor.equals(DocFlavor.BYTE_ARRAY.PNG) && !paramDocFlavor.equals(DocFlavor.URL.GIF) && !paramDocFlavor.equals(DocFlavor.URL.JPEG) && !paramDocFlavor.equals(DocFlavor.URL.PNG)))
          return false; 
      } else if (clazz == ColorSupported.class) {
        int i = getPrinterCapabilities();
        boolean bool = ((i & true) != 0) ? 1 : 0;
        if ((!bool && paramAttribute == ColorSupported.SUPPORTED) || (bool && paramAttribute == ColorSupported.NOT_SUPPORTED))
          return false; 
      } 
    } 
    return true;
  }
  
  public AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    if (paramDocFlavor != null && !isDocFlavorSupported(paramDocFlavor))
      throw new IllegalArgumentException("flavor " + paramDocFlavor + "is not supported"); 
    if (paramAttributeSet == null)
      return null; 
    HashAttributeSet hashAttributeSet = new HashAttributeSet();
    Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
    for (byte b = 0; b < arrayOfAttribute.length; b++) {
      try {
        Attribute attribute = arrayOfAttribute[b];
        if (!isAttributeCategorySupported(attribute.getCategory())) {
          hashAttributeSet.add(attribute);
        } else if (!isAttributeValueSupported(attribute, paramDocFlavor, paramAttributeSet)) {
          hashAttributeSet.add(attribute);
        } 
      } catch (ClassCastException classCastException) {}
    } 
    return hashAttributeSet.isEmpty() ? null : hashAttributeSet;
  }
  
  private DocumentPropertiesUI getDocumentPropertiesUI() { return new Win32DocumentPropertiesUI(this, null); }
  
  public ServiceUIFactory getServiceUIFactory() {
    if (this.uiFactory == null)
      this.uiFactory = new Win32ServiceUIFactory(this); 
    return this.uiFactory;
  }
  
  public String toString() { return "Win32 Printer : " + getName(); }
  
  public boolean equals(Object paramObject) { return (paramObject == this || (paramObject instanceof Win32PrintService && ((Win32PrintService)paramObject).getName().equals(getName()))); }
  
  public int hashCode() { return getClass().hashCode() + getName().hashCode(); }
  
  public boolean usesClass(Class paramClass) { return (paramClass == WPrinterJob.class); }
  
  private native int[] getAllMediaIDs(String paramString1, String paramString2);
  
  private native int[] getAllMediaSizes(String paramString1, String paramString2);
  
  private native int[] getAllMediaTrays(String paramString1, String paramString2);
  
  private native float[] getMediaPrintableArea(String paramString, int paramInt);
  
  private native String[] getAllMediaNames(String paramString1, String paramString2);
  
  private native String[] getAllMediaTrayNames(String paramString1, String paramString2);
  
  private native int getCopiesSupported(String paramString1, String paramString2);
  
  private native int[] getAllResolutions(String paramString1, String paramString2);
  
  private native int getCapabilities(String paramString1, String paramString2);
  
  private native int[] getDefaultSettings(String paramString1, String paramString2);
  
  private native int getJobStatus(String paramString, int paramInt);
  
  private native String getPrinterPort(String paramString);
  
  private static class Win32DocumentPropertiesUI extends DocumentPropertiesUI {
    Win32PrintService service;
    
    private Win32DocumentPropertiesUI(Win32PrintService param1Win32PrintService) { this.service = param1Win32PrintService; }
    
    public PrintRequestAttributeSet showDocumentProperties(PrinterJob param1PrinterJob, Window param1Window, PrintService param1PrintService, PrintRequestAttributeSet param1PrintRequestAttributeSet) {
      if (!(param1PrinterJob instanceof WPrinterJob))
        return null; 
      WPrinterJob wPrinterJob = (WPrinterJob)param1PrinterJob;
      return wPrinterJob.showDocumentProperties(param1Window, param1PrintService, param1PrintRequestAttributeSet);
    }
  }
  
  private static class Win32ServiceUIFactory extends ServiceUIFactory {
    Win32PrintService service;
    
    Win32ServiceUIFactory(Win32PrintService param1Win32PrintService) { this.service = param1Win32PrintService; }
    
    public Object getUI(int param1Int, String param1String) {
      if (param1Int <= 3)
        return null; 
      if (param1Int == 199 && DocumentPropertiesUI.DOCPROPERTIESCLASSNAME.equals(param1String))
        return this.service.getDocumentPropertiesUI(); 
      throw new IllegalArgumentException("Unsupported role");
    }
    
    public String[] getUIClassNamesForRole(int param1Int) {
      if (param1Int <= 3)
        return null; 
      if (param1Int == 199) {
        String[] arrayOfString = new String[0];
        arrayOfString[0] = DocumentPropertiesUI.DOCPROPERTIESCLASSNAME;
        return arrayOfString;
      } 
      throw new IllegalArgumentException("Unsupported role");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\Win32PrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
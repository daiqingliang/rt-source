package sun.print;

import java.io.OutputStream;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.ServiceUIFactory;
import javax.print.StreamPrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;

public class PSStreamPrintService extends StreamPrintService implements SunPrinterJobService {
  private static final Class[] suppAttrCats = { 
      Chromaticity.class, Copies.class, Fidelity.class, JobName.class, Media.class, MediaPrintableArea.class, OrientationRequested.class, PageRanges.class, RequestingUserName.class, SheetCollate.class, 
      Sides.class };
  
  private static int MAXCOPIES = 1000;
  
  private static final MediaSizeName[] mediaSizes = { MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5 };
  
  public PSStreamPrintService(OutputStream paramOutputStream) { super(paramOutputStream); }
  
  public String getOutputFormat() { return "application/postscript"; }
  
  public DocFlavor[] getSupportedDocFlavors() { return PSStreamPrinterFactory.getFlavors(); }
  
  public DocPrintJob createPrintJob() { return new PSStreamPrintJob(this); }
  
  public boolean usesClass(Class paramClass) { return (paramClass == PSPrinterJob.class); }
  
  public String getName() { return "Postscript output"; }
  
  public void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {}
  
  public void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener) {}
  
  public <T extends javax.print.attribute.PrintServiceAttribute> T getAttribute(Class<T> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("category"); 
    if (!javax.print.attribute.PrintServiceAttribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException("Not a PrintServiceAttribute"); 
    return (paramClass == ColorSupported.class) ? (T)ColorSupported.SUPPORTED : null;
  }
  
  public PrintServiceAttributeSet getAttributes() {
    HashPrintServiceAttributeSet hashPrintServiceAttributeSet = new HashPrintServiceAttributeSet();
    hashPrintServiceAttributeSet.add(ColorSupported.SUPPORTED);
    return AttributeSetUtilities.unmodifiableView(hashPrintServiceAttributeSet);
  }
  
  public boolean isDocFlavorSupported(DocFlavor paramDocFlavor) {
    DocFlavor[] arrayOfDocFlavor = getSupportedDocFlavors();
    for (byte b = 0; b < arrayOfDocFlavor.length; b++) {
      if (paramDocFlavor.equals(arrayOfDocFlavor[b]))
        return true; 
    } 
    return false;
  }
  
  public Class<?>[] getSupportedAttributeCategories() {
    Class[] arrayOfClass = new Class[suppAttrCats.length];
    System.arraycopy(suppAttrCats, 0, arrayOfClass, 0, arrayOfClass.length);
    return arrayOfClass;
  }
  
  public boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass) {
    if (paramClass == null)
      throw new NullPointerException("null category"); 
    if (!Attribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass + " is not an Attribute"); 
    for (byte b = 0; b < suppAttrCats.length; b++) {
      if (paramClass == suppAttrCats[b])
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
    if (paramClass == Copies.class)
      return new Copies(1); 
    if (paramClass == Chromaticity.class)
      return Chromaticity.COLOR; 
    if (paramClass == Fidelity.class)
      return Fidelity.FIDELITY_FALSE; 
    if (paramClass == Media.class) {
      String str = Locale.getDefault().getCountry();
      return (str != null && (str.equals("") || str.equals(Locale.US.getCountry()) || str.equals(Locale.CANADA.getCountry()))) ? MediaSizeName.NA_LETTER : MediaSizeName.ISO_A4;
    } 
    if (paramClass == MediaPrintableArea.class) {
      float f2;
      float f1;
      String str = Locale.getDefault().getCountry();
      float f3 = 0.5F;
      if (str != null && (str.equals("") || str.equals(Locale.US.getCountry()) || str.equals(Locale.CANADA.getCountry()))) {
        f1 = MediaSize.NA.LETTER.getX(25400) - 2.0F * f3;
        f2 = MediaSize.NA.LETTER.getY(25400) - 2.0F * f3;
      } else {
        f1 = MediaSize.ISO.A4.getX(25400) - 2.0F * f3;
        f2 = MediaSize.ISO.A4.getY(25400) - 2.0F * f3;
      } 
      return new MediaPrintableArea(f3, f3, f1, f2, 25400);
    } 
    return (paramClass == OrientationRequested.class) ? OrientationRequested.PORTRAIT : ((paramClass == PageRanges.class) ? new PageRanges(1, 2147483647) : ((paramClass == SheetCollate.class) ? SheetCollate.UNCOLLATED : ((paramClass == Sides.class) ? Sides.ONE_SIDED : null)));
  }
  
  public Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    if (paramClass == null)
      throw new NullPointerException("null category"); 
    if (!Attribute.class.isAssignableFrom(paramClass))
      throw new IllegalArgumentException(paramClass + " does not implement Attribute"); 
    if (paramDocFlavor != null && !isDocFlavorSupported(paramDocFlavor))
      throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor"); 
    if (!isAttributeCategorySupported(paramClass))
      return null; 
    if (paramClass == Chromaticity.class) {
      Chromaticity[] arrayOfChromaticity = new Chromaticity[1];
      arrayOfChromaticity[0] = Chromaticity.COLOR;
      return arrayOfChromaticity;
    } 
    if (paramClass == JobName.class)
      return new JobName("", null); 
    if (paramClass == RequestingUserName.class)
      return new RequestingUserName("", null); 
    if (paramClass == OrientationRequested.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        OrientationRequested[] arrayOfOrientationRequested = new OrientationRequested[3];
        arrayOfOrientationRequested[0] = OrientationRequested.PORTRAIT;
        arrayOfOrientationRequested[1] = OrientationRequested.LANDSCAPE;
        arrayOfOrientationRequested[2] = OrientationRequested.REVERSE_LANDSCAPE;
        return arrayOfOrientationRequested;
      } 
      return null;
    } 
    if (paramClass == Copies.class || paramClass == CopiesSupported.class)
      return new CopiesSupported(1, MAXCOPIES); 
    if (paramClass == Media.class) {
      Media[] arrayOfMedia = new Media[mediaSizes.length];
      System.arraycopy(mediaSizes, 0, arrayOfMedia, 0, mediaSizes.length);
      return arrayOfMedia;
    } 
    if (paramClass == Fidelity.class) {
      Fidelity[] arrayOfFidelity = new Fidelity[2];
      arrayOfFidelity[0] = Fidelity.FIDELITY_FALSE;
      arrayOfFidelity[1] = Fidelity.FIDELITY_TRUE;
      return arrayOfFidelity;
    } 
    if (paramClass == MediaPrintableArea.class) {
      if (paramAttributeSet == null)
        return null; 
      MediaSize mediaSize = (MediaSize)paramAttributeSet.get(MediaSize.class);
      if (mediaSize == null) {
        Media media = (Media)paramAttributeSet.get(Media.class);
        if (media != null && media instanceof MediaSizeName) {
          MediaSizeName mediaSizeName = (MediaSizeName)media;
          mediaSize = MediaSize.getMediaSizeForName(mediaSizeName);
        } 
      } 
      if (mediaSize == null)
        return null; 
      MediaPrintableArea[] arrayOfMediaPrintableArea = new MediaPrintableArea[1];
      float f1 = mediaSize.getX(25400);
      float f2 = mediaSize.getY(25400);
      float f3 = 0.5F;
      float f4 = 0.5F;
      if (f1 < 5.0F)
        f3 = f1 / 10.0F; 
      if (f2 < 5.0F)
        f4 = f2 / 10.0F; 
      arrayOfMediaPrintableArea[0] = new MediaPrintableArea(f3, f4, f1 - 2.0F * f3, f2 - 2.0F * f4, 25400);
      return arrayOfMediaPrintableArea;
    } 
    if (paramClass == PageRanges.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        PageRanges[] arrayOfPageRanges = new PageRanges[1];
        arrayOfPageRanges[0] = new PageRanges(1, 2147483647);
        return arrayOfPageRanges;
      } 
      return null;
    } 
    if (paramClass == SheetCollate.class) {
      if (paramDocFlavor == null || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) || paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
        SheetCollate[] arrayOfSheetCollate1 = new SheetCollate[2];
        arrayOfSheetCollate1[0] = SheetCollate.UNCOLLATED;
        arrayOfSheetCollate1[1] = SheetCollate.COLLATED;
        return arrayOfSheetCollate1;
      } 
      SheetCollate[] arrayOfSheetCollate = new SheetCollate[1];
      arrayOfSheetCollate[0] = SheetCollate.UNCOLLATED;
      return arrayOfSheetCollate;
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
    return null;
  }
  
  private boolean isSupportedCopies(Copies paramCopies) {
    int i = paramCopies.getValue();
    return (i > 0 && i < MAXCOPIES);
  }
  
  private boolean isSupportedMedia(MediaSizeName paramMediaSizeName) {
    for (byte b = 0; b < mediaSizes.length; b++) {
      if (paramMediaSizeName.equals(mediaSizes[b]))
        return true; 
    } 
    return false;
  }
  
  public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    if (paramAttribute == null)
      throw new NullPointerException("null attribute"); 
    if (paramDocFlavor != null && !isDocFlavorSupported(paramDocFlavor))
      throw new IllegalArgumentException(paramDocFlavor + " is an unsupported flavor"); 
    Class clazz = paramAttribute.getCategory();
    if (!isAttributeCategorySupported(clazz))
      return false; 
    if (paramAttribute.getCategory() == Chromaticity.class)
      return (paramAttribute == Chromaticity.COLOR); 
    if (paramAttribute.getCategory() == Copies.class)
      return isSupportedCopies((Copies)paramAttribute); 
    if (paramAttribute.getCategory() == Media.class && paramAttribute instanceof MediaSizeName)
      return isSupportedMedia((MediaSizeName)paramAttribute); 
    if (paramAttribute.getCategory() == OrientationRequested.class) {
      if (paramAttribute == OrientationRequested.REVERSE_PORTRAIT || (paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)))
        return false; 
    } else if (paramAttribute.getCategory() == PageRanges.class) {
      if (paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
        return false; 
    } else if (paramAttribute.getCategory() == SheetCollate.class) {
      if (paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))
        return false; 
    } else if (paramAttribute.getCategory() == Sides.class && paramDocFlavor != null && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE) && !paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE)) {
      return false;
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
  
  public ServiceUIFactory getServiceUIFactory() { return null; }
  
  public String toString() { return "PSStreamPrintService: " + getName(); }
  
  public boolean equals(Object paramObject) { return (paramObject == this || (paramObject instanceof PSStreamPrintService && ((PSStreamPrintService)paramObject).getName().equals(getName()))); }
  
  public int hashCode() { return getClass().hashCode() + getName().hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\PSStreamPrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
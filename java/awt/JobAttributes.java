package java.awt;

public final class JobAttributes implements Cloneable {
  private int copies;
  
  private DefaultSelectionType defaultSelection;
  
  private DestinationType destination;
  
  private DialogType dialog;
  
  private String fileName;
  
  private int fromPage;
  
  private int maxPage;
  
  private int minPage;
  
  private MultipleDocumentHandlingType multipleDocumentHandling;
  
  private int[][] pageRanges;
  
  private int prFirst;
  
  private int prLast;
  
  private String printer;
  
  private SidesType sides;
  
  private int toPage;
  
  public JobAttributes() {
    setCopiesToDefault();
    setDefaultSelection(DefaultSelectionType.ALL);
    setDestination(DestinationType.PRINTER);
    setDialog(DialogType.NATIVE);
    setMaxPage(2147483647);
    setMinPage(1);
    setMultipleDocumentHandlingToDefault();
    setSidesToDefault();
  }
  
  public JobAttributes(JobAttributes paramJobAttributes) { set(paramJobAttributes); }
  
  public JobAttributes(int paramInt1, DefaultSelectionType paramDefaultSelectionType, DestinationType paramDestinationType, DialogType paramDialogType, String paramString1, int paramInt2, int paramInt3, MultipleDocumentHandlingType paramMultipleDocumentHandlingType, int[][] paramArrayOfInt, String paramString2, SidesType paramSidesType) {
    setCopies(paramInt1);
    setDefaultSelection(paramDefaultSelectionType);
    setDestination(paramDestinationType);
    setDialog(paramDialogType);
    setFileName(paramString1);
    setMaxPage(paramInt2);
    setMinPage(paramInt3);
    setMultipleDocumentHandling(paramMultipleDocumentHandlingType);
    setPageRanges(paramArrayOfInt);
    setPrinter(paramString2);
    setSides(paramSidesType);
  }
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public void set(JobAttributes paramJobAttributes) {
    this.copies = paramJobAttributes.copies;
    this.defaultSelection = paramJobAttributes.defaultSelection;
    this.destination = paramJobAttributes.destination;
    this.dialog = paramJobAttributes.dialog;
    this.fileName = paramJobAttributes.fileName;
    this.fromPage = paramJobAttributes.fromPage;
    this.maxPage = paramJobAttributes.maxPage;
    this.minPage = paramJobAttributes.minPage;
    this.multipleDocumentHandling = paramJobAttributes.multipleDocumentHandling;
    this.pageRanges = paramJobAttributes.pageRanges;
    this.prFirst = paramJobAttributes.prFirst;
    this.prLast = paramJobAttributes.prLast;
    this.printer = paramJobAttributes.printer;
    this.sides = paramJobAttributes.sides;
    this.toPage = paramJobAttributes.toPage;
  }
  
  public int getCopies() { return this.copies; }
  
  public void setCopies(int paramInt) {
    if (paramInt <= 0)
      throw new IllegalArgumentException("Invalid value for attribute copies"); 
    this.copies = paramInt;
  }
  
  public void setCopiesToDefault() { setCopies(1); }
  
  public DefaultSelectionType getDefaultSelection() { return this.defaultSelection; }
  
  public void setDefaultSelection(DefaultSelectionType paramDefaultSelectionType) {
    if (paramDefaultSelectionType == null)
      throw new IllegalArgumentException("Invalid value for attribute defaultSelection"); 
    this.defaultSelection = paramDefaultSelectionType;
  }
  
  public DestinationType getDestination() { return this.destination; }
  
  public void setDestination(DestinationType paramDestinationType) {
    if (paramDestinationType == null)
      throw new IllegalArgumentException("Invalid value for attribute destination"); 
    this.destination = paramDestinationType;
  }
  
  public DialogType getDialog() { return this.dialog; }
  
  public void setDialog(DialogType paramDialogType) {
    if (paramDialogType == null)
      throw new IllegalArgumentException("Invalid value for attribute dialog"); 
    this.dialog = paramDialogType;
  }
  
  public String getFileName() { return this.fileName; }
  
  public void setFileName(String paramString) { this.fileName = paramString; }
  
  public int getFromPage() { return (this.fromPage != 0) ? this.fromPage : ((this.toPage != 0) ? getMinPage() : ((this.pageRanges != null) ? this.prFirst : getMinPage())); }
  
  public void setFromPage(int paramInt) {
    if (paramInt <= 0 || (this.toPage != 0 && paramInt > this.toPage) || paramInt < this.minPage || paramInt > this.maxPage)
      throw new IllegalArgumentException("Invalid value for attribute fromPage"); 
    this.fromPage = paramInt;
  }
  
  public int getMaxPage() { return this.maxPage; }
  
  public void setMaxPage(int paramInt) {
    if (paramInt <= 0 || paramInt < this.minPage)
      throw new IllegalArgumentException("Invalid value for attribute maxPage"); 
    this.maxPage = paramInt;
  }
  
  public int getMinPage() { return this.minPage; }
  
  public void setMinPage(int paramInt) {
    if (paramInt <= 0 || paramInt > this.maxPage)
      throw new IllegalArgumentException("Invalid value for attribute minPage"); 
    this.minPage = paramInt;
  }
  
  public MultipleDocumentHandlingType getMultipleDocumentHandling() { return this.multipleDocumentHandling; }
  
  public void setMultipleDocumentHandling(MultipleDocumentHandlingType paramMultipleDocumentHandlingType) {
    if (paramMultipleDocumentHandlingType == null)
      throw new IllegalArgumentException("Invalid value for attribute multipleDocumentHandling"); 
    this.multipleDocumentHandling = paramMultipleDocumentHandlingType;
  }
  
  public void setMultipleDocumentHandlingToDefault() { setMultipleDocumentHandling(MultipleDocumentHandlingType.SEPARATE_DOCUMENTS_UNCOLLATED_COPIES); }
  
  public int[][] getPageRanges() {
    if (this.pageRanges != null) {
      int[][] arrayOfInt = new int[this.pageRanges.length][2];
      for (byte b = 0; b < this.pageRanges.length; b++) {
        arrayOfInt[b][0] = this.pageRanges[b][0];
        arrayOfInt[b][1] = this.pageRanges[b][1];
      } 
      return arrayOfInt;
    } 
    if (this.fromPage != 0 || this.toPage != 0) {
      int j = getFromPage();
      int k = getToPage();
      return new int[][] { { j, k } };
    } 
    int i = getMinPage();
    return new int[][] { { i, i } };
  }
  
  public void setPageRanges(int[][] paramArrayOfInt) {
    String str = "Invalid value for attribute pageRanges";
    int i = 0;
    int j = 0;
    if (paramArrayOfInt == null)
      throw new IllegalArgumentException(str); 
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++) {
      if (paramArrayOfInt[b1] == null || paramArrayOfInt[b1].length != 2 || paramArrayOfInt[b1][0] <= j || paramArrayOfInt[b1][1] < paramArrayOfInt[b1][0])
        throw new IllegalArgumentException(str); 
      j = paramArrayOfInt[b1][1];
      if (!i)
        i = paramArrayOfInt[b1][0]; 
    } 
    if (i < this.minPage || j > this.maxPage)
      throw new IllegalArgumentException(str); 
    int[][] arrayOfInt = new int[paramArrayOfInt.length][2];
    for (byte b2 = 0; b2 < paramArrayOfInt.length; b2++) {
      arrayOfInt[b2][0] = paramArrayOfInt[b2][0];
      arrayOfInt[b2][1] = paramArrayOfInt[b2][1];
    } 
    this.pageRanges = arrayOfInt;
    this.prFirst = i;
    this.prLast = j;
  }
  
  public String getPrinter() { return this.printer; }
  
  public void setPrinter(String paramString) { this.printer = paramString; }
  
  public SidesType getSides() { return this.sides; }
  
  public void setSides(SidesType paramSidesType) {
    if (paramSidesType == null)
      throw new IllegalArgumentException("Invalid value for attribute sides"); 
    this.sides = paramSidesType;
  }
  
  public void setSidesToDefault() { setSides(SidesType.ONE_SIDED); }
  
  public int getToPage() { return (this.toPage != 0) ? this.toPage : ((this.fromPage != 0) ? this.fromPage : ((this.pageRanges != null) ? this.prLast : getMinPage())); }
  
  public void setToPage(int paramInt) {
    if (paramInt <= 0 || (this.fromPage != 0 && paramInt < this.fromPage) || paramInt < this.minPage || paramInt > this.maxPage)
      throw new IllegalArgumentException("Invalid value for attribute toPage"); 
    this.toPage = paramInt;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof JobAttributes))
      return false; 
    JobAttributes jobAttributes = (JobAttributes)paramObject;
    if (this.fileName == null) {
      if (jobAttributes.fileName != null)
        return false; 
    } else if (!this.fileName.equals(jobAttributes.fileName)) {
      return false;
    } 
    if (this.pageRanges == null) {
      if (jobAttributes.pageRanges != null)
        return false; 
    } else {
      if (jobAttributes.pageRanges == null || this.pageRanges.length != jobAttributes.pageRanges.length)
        return false; 
      for (byte b = 0; b < this.pageRanges.length; b++) {
        if (this.pageRanges[b][0] != jobAttributes.pageRanges[b][0] || this.pageRanges[b][1] != jobAttributes.pageRanges[b][1])
          return false; 
      } 
    } 
    if (this.printer == null) {
      if (jobAttributes.printer != null)
        return false; 
    } else if (!this.printer.equals(jobAttributes.printer)) {
      return false;
    } 
    return (this.copies == jobAttributes.copies && this.defaultSelection == jobAttributes.defaultSelection && this.destination == jobAttributes.destination && this.dialog == jobAttributes.dialog && this.fromPage == jobAttributes.fromPage && this.maxPage == jobAttributes.maxPage && this.minPage == jobAttributes.minPage && this.multipleDocumentHandling == jobAttributes.multipleDocumentHandling && this.prFirst == jobAttributes.prFirst && this.prLast == jobAttributes.prLast && this.sides == jobAttributes.sides && this.toPage == jobAttributes.toPage);
  }
  
  public int hashCode() {
    int i = (this.copies + this.fromPage + this.maxPage + this.minPage + this.prFirst + this.prLast + this.toPage) * 31 << 21;
    if (this.pageRanges != null) {
      int j = 0;
      for (byte b = 0; b < this.pageRanges.length; b++)
        j += this.pageRanges[b][0] + this.pageRanges[b][1]; 
      i ^= j * 31 << 11;
    } 
    if (this.fileName != null)
      i ^= this.fileName.hashCode(); 
    if (this.printer != null)
      i ^= this.printer.hashCode(); 
    return this.defaultSelection.hashCode() << 6 ^ this.destination.hashCode() << 5 ^ this.dialog.hashCode() << 3 ^ this.multipleDocumentHandling.hashCode() << 2 ^ this.sides.hashCode() ^ i;
  }
  
  public String toString() {
    int[][] arrayOfInt = getPageRanges();
    String str = "[";
    boolean bool = true;
    for (byte b = 0; b < arrayOfInt.length; b++) {
      if (bool) {
        bool = false;
      } else {
        str = str + ",";
      } 
      str = str + arrayOfInt[b][0] + ":" + arrayOfInt[b][1];
    } 
    str = str + "]";
    return "copies=" + getCopies() + ",defaultSelection=" + getDefaultSelection() + ",destination=" + getDestination() + ",dialog=" + getDialog() + ",fileName=" + getFileName() + ",fromPage=" + getFromPage() + ",maxPage=" + getMaxPage() + ",minPage=" + getMinPage() + ",multiple-document-handling=" + getMultipleDocumentHandling() + ",page-ranges=" + str + ",printer=" + getPrinter() + ",sides=" + getSides() + ",toPage=" + getToPage();
  }
  
  public static final class DefaultSelectionType extends AttributeValue {
    private static final int I_ALL = 0;
    
    private static final int I_RANGE = 1;
    
    private static final int I_SELECTION = 2;
    
    private static final String[] NAMES = { "all", "range", "selection" };
    
    public static final DefaultSelectionType ALL = new DefaultSelectionType(0);
    
    public static final DefaultSelectionType RANGE = new DefaultSelectionType(1);
    
    public static final DefaultSelectionType SELECTION = new DefaultSelectionType(2);
    
    private DefaultSelectionType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class DestinationType extends AttributeValue {
    private static final int I_FILE = 0;
    
    private static final int I_PRINTER = 1;
    
    private static final String[] NAMES = { "file", "printer" };
    
    public static final DestinationType FILE = new DestinationType(0);
    
    public static final DestinationType PRINTER = new DestinationType(1);
    
    private DestinationType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class DialogType extends AttributeValue {
    private static final int I_COMMON = 0;
    
    private static final int I_NATIVE = 1;
    
    private static final int I_NONE = 2;
    
    private static final String[] NAMES = { "common", "native", "none" };
    
    public static final DialogType COMMON = new DialogType(0);
    
    public static final DialogType NATIVE = new DialogType(1);
    
    public static final DialogType NONE = new DialogType(2);
    
    private DialogType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class MultipleDocumentHandlingType extends AttributeValue {
    private static final int I_SEPARATE_DOCUMENTS_COLLATED_COPIES = 0;
    
    private static final int I_SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = 1;
    
    private static final String[] NAMES = { "separate-documents-collated-copies", "separate-documents-uncollated-copies" };
    
    public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandlingType(0);
    
    public static final MultipleDocumentHandlingType SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandlingType(1);
    
    private MultipleDocumentHandlingType(int param1Int) { super(param1Int, NAMES); }
  }
  
  public static final class SidesType extends AttributeValue {
    private static final int I_ONE_SIDED = 0;
    
    private static final int I_TWO_SIDED_LONG_EDGE = 1;
    
    private static final int I_TWO_SIDED_SHORT_EDGE = 2;
    
    private static final String[] NAMES = { "one-sided", "two-sided-long-edge", "two-sided-short-edge" };
    
    public static final SidesType ONE_SIDED = new SidesType(0);
    
    public static final SidesType TWO_SIDED_LONG_EDGE = new SidesType(1);
    
    public static final SidesType TWO_SIDED_SHORT_EDGE = new SidesType(2);
    
    private SidesType(int param1Int) { super(param1Int, NAMES); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\JobAttributes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
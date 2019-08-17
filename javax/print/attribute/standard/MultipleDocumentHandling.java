package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintRequestAttribute;

public class MultipleDocumentHandling extends EnumSyntax implements PrintRequestAttribute, PrintJobAttribute {
  private static final long serialVersionUID = 8098326460746413466L;
  
  public static final MultipleDocumentHandling SINGLE_DOCUMENT = new MultipleDocumentHandling(0);
  
  public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_UNCOLLATED_COPIES = new MultipleDocumentHandling(1);
  
  public static final MultipleDocumentHandling SEPARATE_DOCUMENTS_COLLATED_COPIES = new MultipleDocumentHandling(2);
  
  public static final MultipleDocumentHandling SINGLE_DOCUMENT_NEW_SHEET = new MultipleDocumentHandling(3);
  
  private static final String[] myStringTable = { "single-document", "separate-documents-uncollated-copies", "separate-documents-collated-copies", "single-document-new-sheet" };
  
  private static final MultipleDocumentHandling[] myEnumValueTable = { SINGLE_DOCUMENT, SEPARATE_DOCUMENTS_UNCOLLATED_COPIES, SEPARATE_DOCUMENTS_COLLATED_COPIES, SINGLE_DOCUMENT_NEW_SHEET };
  
  protected MultipleDocumentHandling(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return (String[])myStringTable.clone(); }
  
  protected EnumSyntax[] getEnumValueTable() { return (EnumSyntax[])myEnumValueTable.clone(); }
  
  public final Class<? extends Attribute> getCategory() { return MultipleDocumentHandling.class; }
  
  public final String getName() { return "multiple-document-handling"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\MultipleDocumentHandling.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
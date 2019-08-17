package javax.print;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.event.PrintServiceAttributeListener;

public interface PrintService {
  String getName();
  
  DocPrintJob createPrintJob();
  
  void addPrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener);
  
  void removePrintServiceAttributeListener(PrintServiceAttributeListener paramPrintServiceAttributeListener);
  
  PrintServiceAttributeSet getAttributes();
  
  <T extends javax.print.attribute.PrintServiceAttribute> T getAttribute(Class<T> paramClass);
  
  DocFlavor[] getSupportedDocFlavors();
  
  boolean isDocFlavorSupported(DocFlavor paramDocFlavor);
  
  Class<?>[] getSupportedAttributeCategories();
  
  boolean isAttributeCategorySupported(Class<? extends Attribute> paramClass);
  
  Object getDefaultAttributeValue(Class<? extends Attribute> paramClass);
  
  Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet);
  
  ServiceUIFactory getServiceUIFactory();
  
  boolean equals(Object paramObject);
  
  int hashCode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\PrintService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
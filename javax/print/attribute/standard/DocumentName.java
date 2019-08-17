package javax.print.attribute.standard;

import java.util.Locale;
import javax.print.attribute.Attribute;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.TextSyntax;

public final class DocumentName extends TextSyntax implements DocAttribute {
  private static final long serialVersionUID = 7883105848533280430L;
  
  public DocumentName(String paramString, Locale paramLocale) { super(paramString, paramLocale); }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof DocumentName); }
  
  public final Class<? extends Attribute> getCategory() { return DocumentName.class; }
  
  public final String getName() { return "document-name"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\DocumentName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
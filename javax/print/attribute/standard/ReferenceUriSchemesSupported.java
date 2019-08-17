package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;

public class ReferenceUriSchemesSupported extends EnumSyntax implements Attribute {
  private static final long serialVersionUID = -8989076942813442805L;
  
  public static final ReferenceUriSchemesSupported FTP = new ReferenceUriSchemesSupported(0);
  
  public static final ReferenceUriSchemesSupported HTTP = new ReferenceUriSchemesSupported(1);
  
  public static final ReferenceUriSchemesSupported HTTPS = new ReferenceUriSchemesSupported(2);
  
  public static final ReferenceUriSchemesSupported GOPHER = new ReferenceUriSchemesSupported(3);
  
  public static final ReferenceUriSchemesSupported NEWS = new ReferenceUriSchemesSupported(4);
  
  public static final ReferenceUriSchemesSupported NNTP = new ReferenceUriSchemesSupported(5);
  
  public static final ReferenceUriSchemesSupported WAIS = new ReferenceUriSchemesSupported(6);
  
  public static final ReferenceUriSchemesSupported FILE = new ReferenceUriSchemesSupported(7);
  
  private static final String[] myStringTable = { "ftp", "http", "https", "gopher", "news", "nntp", "wais", "file" };
  
  private static final ReferenceUriSchemesSupported[] myEnumValueTable = { FTP, HTTP, HTTPS, GOPHER, NEWS, NNTP, WAIS, FILE };
  
  protected ReferenceUriSchemesSupported(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return (String[])myStringTable.clone(); }
  
  protected EnumSyntax[] getEnumValueTable() { return (EnumSyntax[])myEnumValueTable.clone(); }
  
  public final Class<? extends Attribute> getCategory() { return ReferenceUriSchemesSupported.class; }
  
  public final String getName() { return "reference-uri-schemes-supported"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\ReferenceUriSchemesSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
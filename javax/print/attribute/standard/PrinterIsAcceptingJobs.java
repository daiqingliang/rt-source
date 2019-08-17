package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.PrintServiceAttribute;

public final class PrinterIsAcceptingJobs extends EnumSyntax implements PrintServiceAttribute {
  private static final long serialVersionUID = -5052010680537678061L;
  
  public static final PrinterIsAcceptingJobs NOT_ACCEPTING_JOBS = new PrinterIsAcceptingJobs(0);
  
  public static final PrinterIsAcceptingJobs ACCEPTING_JOBS = new PrinterIsAcceptingJobs(1);
  
  private static final String[] myStringTable = { "not-accepting-jobs", "accepting-jobs" };
  
  private static final PrinterIsAcceptingJobs[] myEnumValueTable = { NOT_ACCEPTING_JOBS, ACCEPTING_JOBS };
  
  protected PrinterIsAcceptingJobs(int paramInt) { super(paramInt); }
  
  protected String[] getStringTable() { return myStringTable; }
  
  protected EnumSyntax[] getEnumValueTable() { return myEnumValueTable; }
  
  public final Class<? extends Attribute> getCategory() { return PrinterIsAcceptingJobs.class; }
  
  public final String getName() { return "printer-is-accepting-jobs"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\PrinterIsAcceptingJobs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.SetOfIntegerSyntax;
import javax.print.attribute.SupportedValuesAttribute;

public final class JobKOctetsSupported extends SetOfIntegerSyntax implements SupportedValuesAttribute {
  private static final long serialVersionUID = -2867871140549897443L;
  
  public JobKOctetsSupported(int paramInt1, int paramInt2) {
    super(paramInt1, paramInt2);
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException("Null range specified"); 
    if (paramInt1 < 0)
      throw new IllegalArgumentException("Job K octets value < 0 specified"); 
  }
  
  public boolean equals(Object paramObject) { return (super.equals(paramObject) && paramObject instanceof JobKOctetsSupported); }
  
  public final Class<? extends Attribute> getCategory() { return JobKOctetsSupported.class; }
  
  public final String getName() { return "job-k-octets-supported"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\standard\JobKOctetsSupported.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package sun.print;

import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.standard.Media;

public class SunAlternateMedia implements PrintRequestAttribute {
  private static final long serialVersionUID = -8878868345472850201L;
  
  private Media media;
  
  public SunAlternateMedia(Media paramMedia) { this.media = paramMedia; }
  
  public Media getMedia() { return this.media; }
  
  public final Class getCategory() { return SunAlternateMedia.class; }
  
  public final String getName() { return "sun-alternate-media"; }
  
  public String toString() { return "alternate-media: " + this.media.toString(); }
  
  public int hashCode() { return this.media.hashCode(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\print\SunAlternateMedia.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
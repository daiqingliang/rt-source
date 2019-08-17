package javax.xml.ws.soap;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

public final class MTOMFeature extends WebServiceFeature {
  public static final String ID = "http://www.w3.org/2004/08/soap/features/http-optimization";
  
  protected int threshold;
  
  public MTOMFeature() {
    this.enabled = true;
    this.threshold = 0;
  }
  
  public MTOMFeature(boolean paramBoolean) {
    this.enabled = paramBoolean;
    this.threshold = 0;
  }
  
  public MTOMFeature(int paramInt) {
    if (paramInt < 0)
      throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + paramInt); 
    this.enabled = true;
    this.threshold = paramInt;
  }
  
  public MTOMFeature(boolean paramBoolean, int paramInt) {
    if (paramInt < 0)
      throw new WebServiceException("MTOMFeature.threshold must be >= 0, actual value: " + paramInt); 
    this.enabled = paramBoolean;
    this.threshold = paramInt;
  }
  
  public String getID() { return "http://www.w3.org/2004/08/soap/features/http-optimization"; }
  
  public int getThreshold() { return this.threshold; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\soap\MTOMFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
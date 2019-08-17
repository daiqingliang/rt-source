package com.oracle.webservices.internal.api;

import javax.xml.ws.WebServiceFeature;

public class EnvelopeStyleFeature extends WebServiceFeature {
  private EnvelopeStyle.Style[] styles;
  
  public EnvelopeStyleFeature(EnvelopeStyle.Style... paramVarArgs) { this.styles = paramVarArgs; }
  
  public EnvelopeStyle.Style[] getStyles() { return this.styles; }
  
  public String getID() { return EnvelopeStyleFeature.class.getName(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\EnvelopeStyleFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
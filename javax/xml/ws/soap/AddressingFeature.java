package javax.xml.ws.soap;

import javax.xml.ws.WebServiceFeature;

public final class AddressingFeature extends WebServiceFeature {
  public static final String ID = "http://www.w3.org/2005/08/addressing/module";
  
  protected boolean required;
  
  private final Responses responses;
  
  public AddressingFeature() { this(true, false, Responses.ALL); }
  
  public AddressingFeature(boolean paramBoolean) { this(paramBoolean, false, Responses.ALL); }
  
  public AddressingFeature(boolean paramBoolean1, boolean paramBoolean2) { this(paramBoolean1, paramBoolean2, Responses.ALL); }
  
  public AddressingFeature(boolean paramBoolean1, boolean paramBoolean2, Responses paramResponses) {
    this.enabled = paramBoolean1;
    this.required = paramBoolean2;
    this.responses = paramResponses;
  }
  
  public String getID() { return "http://www.w3.org/2005/08/addressing/module"; }
  
  public boolean isRequired() { return this.required; }
  
  public Responses getResponses() { return this.responses; }
  
  public enum Responses {
    ANONYMOUS, NON_ANONYMOUS, ALL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\soap\AddressingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
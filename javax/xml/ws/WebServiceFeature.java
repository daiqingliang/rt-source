package javax.xml.ws;

public abstract class WebServiceFeature {
  protected boolean enabled = false;
  
  public abstract String getID();
  
  public boolean isEnabled() { return this.enabled; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\ws\WebServiceFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
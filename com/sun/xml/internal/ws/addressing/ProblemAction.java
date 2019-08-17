package com.sun.xml.internal.ws.addressing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ProblemAction", namespace = "http://www.w3.org/2005/08/addressing")
public class ProblemAction {
  @XmlElement(name = "Action", namespace = "http://www.w3.org/2005/08/addressing")
  private String action;
  
  @XmlElement(name = "SoapAction", namespace = "http://www.w3.org/2005/08/addressing")
  private String soapAction;
  
  public ProblemAction() {}
  
  public ProblemAction(String paramString) { this.action = paramString; }
  
  public ProblemAction(String paramString1, String paramString2) {
    this.action = paramString1;
    this.soapAction = paramString2;
  }
  
  public String getAction() { return this.action; }
  
  public String getSoapAction() { return this.soapAction; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\addressing\ProblemAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "soap-binding")
public class XmlSOAPBinding implements SOAPBinding {
  @XmlAttribute(name = "style")
  protected SoapBindingStyle style;
  
  @XmlAttribute(name = "use")
  protected SoapBindingUse use;
  
  @XmlAttribute(name = "parameter-style")
  protected SoapBindingParameterStyle parameterStyle;
  
  public SoapBindingStyle getStyle() { return (this.style == null) ? SoapBindingStyle.DOCUMENT : this.style; }
  
  public void setStyle(SoapBindingStyle paramSoapBindingStyle) { this.style = paramSoapBindingStyle; }
  
  public SoapBindingUse getUse() { return (this.use == null) ? SoapBindingUse.LITERAL : this.use; }
  
  public void setUse(SoapBindingUse paramSoapBindingUse) { this.use = paramSoapBindingUse; }
  
  public SoapBindingParameterStyle getParameterStyle() { return (this.parameterStyle == null) ? SoapBindingParameterStyle.WRAPPED : this.parameterStyle; }
  
  public void setParameterStyle(SoapBindingParameterStyle paramSoapBindingParameterStyle) { this.parameterStyle = paramSoapBindingParameterStyle; }
  
  public SOAPBinding.Style style() { return (SOAPBinding.Style)Util.nullSafe(this.style, SOAPBinding.Style.DOCUMENT); }
  
  public SOAPBinding.Use use() { return (SOAPBinding.Use)Util.nullSafe(this.use, SOAPBinding.Use.LITERAL); }
  
  public SOAPBinding.ParameterStyle parameterStyle() { return (SOAPBinding.ParameterStyle)Util.nullSafe(this.parameterStyle, SOAPBinding.ParameterStyle.WRAPPED); }
  
  public Class<? extends Annotation> annotationType() { return SOAPBinding.class; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlSOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
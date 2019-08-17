package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLFeaturedObject;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceFeature;

abstract class AbstractFeaturedObjectImpl extends AbstractExtensibleImpl implements WSDLFeaturedObject {
  protected WebServiceFeatureList features;
  
  protected AbstractFeaturedObjectImpl(XMLStreamReader paramXMLStreamReader) { super(paramXMLStreamReader); }
  
  protected AbstractFeaturedObjectImpl(String paramString, int paramInt) { super(paramString, paramInt); }
  
  public final void addFeature(WebServiceFeature paramWebServiceFeature) {
    if (this.features == null)
      this.features = new WebServiceFeatureList(); 
    this.features.add(paramWebServiceFeature);
  }
  
  @NotNull
  public WebServiceFeatureList getFeatures() { return (this.features == null) ? new WebServiceFeatureList() : this.features; }
  
  public final WebServiceFeature getFeature(String paramString) {
    if (this.features != null)
      for (WebServiceFeature webServiceFeature : this.features) {
        if (webServiceFeature.getID().equals(paramString))
          return webServiceFeature; 
      }  
    return null;
  }
  
  @Nullable
  public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass) { return (this.features == null) ? null : (F)this.features.get(paramClass); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\AbstractFeaturedObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
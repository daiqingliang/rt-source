package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.internal.ws.resources.UtilMessages;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;

abstract class AbstractExtensibleImpl extends AbstractObjectImpl implements WSDLExtensible {
  protected final Set<WSDLExtension> extensions = new HashSet();
  
  protected List<UnknownWSDLExtension> notUnderstoodExtensions = new ArrayList();
  
  protected AbstractExtensibleImpl(XMLStreamReader paramXMLStreamReader) { super(paramXMLStreamReader); }
  
  protected AbstractExtensibleImpl(String paramString, int paramInt) { super(paramString, paramInt); }
  
  public final Iterable<WSDLExtension> getExtensions() { return this.extensions; }
  
  public final <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> paramClass) {
    ArrayList arrayList = new ArrayList(this.extensions.size());
    for (WSDLExtension wSDLExtension : this.extensions) {
      if (paramClass.isInstance(wSDLExtension))
        arrayList.add(paramClass.cast(wSDLExtension)); 
    } 
    return arrayList;
  }
  
  public <T extends WSDLExtension> T getExtension(Class<T> paramClass) {
    for (WSDLExtension wSDLExtension : this.extensions) {
      if (paramClass.isInstance(wSDLExtension))
        return (T)(WSDLExtension)paramClass.cast(wSDLExtension); 
    } 
    return null;
  }
  
  public void addExtension(WSDLExtension paramWSDLExtension) {
    if (paramWSDLExtension == null)
      throw new IllegalArgumentException(); 
    this.extensions.add(paramWSDLExtension);
  }
  
  public List<? extends UnknownWSDLExtension> getNotUnderstoodExtensions() { return this.notUnderstoodExtensions; }
  
  public void addNotUnderstoodExtension(QName paramQName, Locator paramLocator) { this.notUnderstoodExtensions.add(new UnknownWSDLExtension(paramQName, paramLocator)); }
  
  public boolean areRequiredExtensionsUnderstood() {
    if (this.notUnderstoodExtensions.size() != 0) {
      StringBuilder stringBuilder = new StringBuilder("Unknown WSDL extensibility elements:");
      for (UnknownWSDLExtension unknownWSDLExtension : this.notUnderstoodExtensions)
        stringBuilder.append('\n').append(unknownWSDLExtension.toString()); 
      throw new WebServiceException(stringBuilder.toString());
    } 
    return true;
  }
  
  protected static class UnknownWSDLExtension implements WSDLExtension, WSDLObject {
    private final QName extnEl;
    
    private final Locator locator;
    
    public UnknownWSDLExtension(QName param1QName, Locator param1Locator) {
      this.extnEl = param1QName;
      this.locator = param1Locator;
    }
    
    public QName getName() { return this.extnEl; }
    
    @NotNull
    public Locator getLocation() { return this.locator; }
    
    public String toString() { return this.extnEl + " " + UtilMessages.UTIL_LOCATION(Integer.valueOf(this.locator.getLineNumber()), this.locator.getSystemId()); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\model\wsdl\AbstractExtensibleImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package com.sun.xml.internal.ws.db.glassfish;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.JAXBRIContext;
import com.sun.xml.internal.bind.api.TypeReference;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeInfoSet;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.PropertyAccessor;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

class JAXBRIContextWrapper implements BindingContext {
  private Map<TypeInfo, TypeReference> typeRefs;
  
  private Map<TypeReference, TypeInfo> typeInfos;
  
  private JAXBRIContext context;
  
  JAXBRIContextWrapper(JAXBRIContext paramJAXBRIContext, Map<TypeInfo, TypeReference> paramMap) {
    this.context = paramJAXBRIContext;
    this.typeRefs = paramMap;
    if (paramMap != null) {
      this.typeInfos = new HashMap();
      for (TypeInfo typeInfo : paramMap.keySet())
        this.typeInfos.put(this.typeRefs.get(typeInfo), typeInfo); 
    } 
  }
  
  TypeReference typeReference(TypeInfo paramTypeInfo) { return (this.typeRefs != null) ? (TypeReference)this.typeRefs.get(paramTypeInfo) : null; }
  
  TypeInfo typeInfo(TypeReference paramTypeReference) { return (this.typeInfos != null) ? (TypeInfo)this.typeInfos.get(paramTypeReference) : null; }
  
  public Marshaller createMarshaller() throws JAXBException { return this.context.createMarshaller(); }
  
  public Unmarshaller createUnmarshaller() throws JAXBException { return this.context.createUnmarshaller(); }
  
  public void generateSchema(SchemaOutputResolver paramSchemaOutputResolver) throws IOException { this.context.generateSchema(paramSchemaOutputResolver); }
  
  public String getBuildId() { return this.context.getBuildId(); }
  
  public QName getElementName(Class paramClass) throws JAXBException { return this.context.getElementName(paramClass); }
  
  public QName getElementName(Object paramObject) throws JAXBException { return this.context.getElementName(paramObject); }
  
  public <B, V> PropertyAccessor<B, V> getElementPropertyAccessor(Class<B> paramClass, String paramString1, String paramString2) throws JAXBException { return new RawAccessorWrapper(this.context.getElementPropertyAccessor(paramClass, paramString1, paramString2)); }
  
  public List<String> getKnownNamespaceURIs() { return this.context.getKnownNamespaceURIs(); }
  
  public RuntimeTypeInfoSet getRuntimeTypeInfoSet() { return this.context.getRuntimeTypeInfoSet(); }
  
  public QName getTypeName(TypeReference paramTypeReference) { return this.context.getTypeName(paramTypeReference); }
  
  public int hashCode() { return this.context.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (getClass() != paramObject.getClass())
      return false; 
    JAXBRIContextWrapper jAXBRIContextWrapper = (JAXBRIContextWrapper)paramObject;
    return !(this.context != jAXBRIContextWrapper.context && (this.context == null || !this.context.equals(jAXBRIContextWrapper.context)));
  }
  
  public boolean hasSwaRef() { return this.context.hasSwaRef(); }
  
  public String toString() { return JAXBRIContextWrapper.class.getName() + " : " + this.context.toString(); }
  
  public XMLBridge createBridge(TypeInfo paramTypeInfo) {
    TypeReference typeReference = (TypeReference)this.typeRefs.get(paramTypeInfo);
    Bridge bridge = this.context.createBridge(typeReference);
    return com.sun.xml.internal.ws.spi.db.WrapperComposite.class.equals(paramTypeInfo.type) ? new WrapperBridge(this, bridge) : new BridgeWrapper(this, bridge);
  }
  
  public JAXBContext getJAXBContext() { return this.context; }
  
  public QName getTypeName(TypeInfo paramTypeInfo) {
    TypeReference typeReference = (TypeReference)this.typeRefs.get(paramTypeInfo);
    return this.context.getTypeName(typeReference);
  }
  
  public XMLBridge createFragmentBridge() { return new MarshallerBridge((JAXBContextImpl)this.context); }
  
  public Object newWrapperInstace(Class<?> paramClass) throws InstantiationException, IllegalAccessException { return paramClass.newInstance(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\db\glassfish\JAXBRIContextWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
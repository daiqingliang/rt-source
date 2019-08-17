package com.sun.xml.internal.ws.spi;

import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.transport.http.server.EndpointImpl;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.Invoker;
import javax.xml.ws.spi.Provider;
import javax.xml.ws.spi.ServiceDelegate;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Element;

public class ProviderImpl extends Provider {
  private static final ContextClassloaderLocal<JAXBContext> eprjc = new ContextClassloaderLocal<JAXBContext>() {
      protected JAXBContext initialValue() { return ProviderImpl.getEPRJaxbContext(); }
    };
  
  public static final ProviderImpl INSTANCE = new ProviderImpl();
  
  public Endpoint createEndpoint(String paramString, Object paramObject) { return new EndpointImpl((paramString != null) ? BindingID.parse(paramString) : BindingID.parse(paramObject.getClass()), paramObject, new WebServiceFeature[0]); }
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class paramClass) { return new WSServiceDelegate(paramURL, paramQName, paramClass, new WebServiceFeature[0]); }
  
  public ServiceDelegate createServiceDelegate(URL paramURL, QName paramQName, Class paramClass, WebServiceFeature... paramVarArgs) {
    for (WebServiceFeature webServiceFeature : paramVarArgs) {
      if (!(webServiceFeature instanceof com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker))
        throw new WebServiceException("Doesn't support any Service specific features"); 
    } 
    return new WSServiceDelegate(paramURL, paramQName, paramClass, paramVarArgs);
  }
  
  public ServiceDelegate createServiceDelegate(Source paramSource, QName paramQName, Class paramClass) { return new WSServiceDelegate(paramSource, paramQName, paramClass, new WebServiceFeature[0]); }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject) {
    EndpointImpl endpointImpl = new EndpointImpl(BindingID.parse(paramObject.getClass()), paramObject, new WebServiceFeature[0]);
    endpointImpl.publish(paramString);
    return endpointImpl;
  }
  
  public Endpoint createEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) { return new EndpointImpl((paramString != null) ? BindingID.parse(paramString) : BindingID.parse(paramObject.getClass()), paramObject, paramVarArgs); }
  
  public Endpoint createAndPublishEndpoint(String paramString, Object paramObject, WebServiceFeature... paramVarArgs) {
    EndpointImpl endpointImpl = new EndpointImpl(BindingID.parse(paramObject.getClass()), paramObject, paramVarArgs);
    endpointImpl.publish(paramString);
    return endpointImpl;
  }
  
  public Endpoint createEndpoint(String paramString, Class paramClass, Invoker paramInvoker, WebServiceFeature... paramVarArgs) { return new EndpointImpl((paramString != null) ? BindingID.parse(paramString) : BindingID.parse(paramClass), paramClass, paramInvoker, paramVarArgs); }
  
  public EndpointReference readEndpointReference(Source paramSource) {
    try {
      Unmarshaller unmarshaller = ((JAXBContext)eprjc.get()).createUnmarshaller();
      return (EndpointReference)unmarshaller.unmarshal(paramSource);
    } catch (JAXBException jAXBException) {
      throw new WebServiceException("Error creating Marshaller or marshalling.", jAXBException);
    } 
  }
  
  public <T> T getPort(EndpointReference paramEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs) {
    WSService wSService;
    if (paramEndpointReference == null)
      throw new WebServiceException(ProviderApiMessages.NULL_EPR()); 
    WSEndpointReference wSEndpointReference = new WSEndpointReference(paramEndpointReference);
    WSEndpointReference.Metadata metadata = wSEndpointReference.getMetaData();
    if (metadata.getWsdlSource() != null) {
      wSService = (WSService)createServiceDelegate(metadata.getWsdlSource(), metadata.getServiceName(), javax.xml.ws.Service.class);
    } else {
      throw new WebServiceException("WSDL metadata is missing in EPR");
    } 
    return (T)wSService.getPort(wSEndpointReference, paramClass, paramVarArgs);
  }
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, List<Element> paramList1, String paramString2, List<Element> paramList2) { return createW3CEndpointReference(paramString1, null, paramQName1, paramQName2, paramList1, paramString2, paramList2, null, null); }
  
  public W3CEndpointReference createW3CEndpointReference(String paramString1, QName paramQName1, QName paramQName2, QName paramQName3, List<Element> paramList1, String paramString2, List<Element> paramList2, List<Element> paramList3, Map<QName, String> paramMap) { // Byte code:
    //   0: invokestatic getInstance : ()Lcom/sun/xml/internal/ws/api/server/ContainerResolver;
    //   3: invokevirtual getContainer : ()Lcom/sun/xml/internal/ws/api/server/Container;
    //   6: astore #10
    //   8: aload_1
    //   9: ifnonnull -> 158
    //   12: aload_3
    //   13: ifnull -> 21
    //   16: aload #4
    //   18: ifnonnull -> 32
    //   21: new java/lang/IllegalStateException
    //   24: dup
    //   25: invokestatic NULL_ADDRESS_SERVICE_ENDPOINT : ()Ljava/lang/String;
    //   28: invokespecial <init> : (Ljava/lang/String;)V
    //   31: athrow
    //   32: aload #10
    //   34: ldc com/sun/xml/internal/ws/api/server/Module
    //   36: invokevirtual getSPI : (Ljava/lang/Class;)Ljava/lang/Object;
    //   39: checkcast com/sun/xml/internal/ws/api/server/Module
    //   42: astore #11
    //   44: aload #11
    //   46: ifnull -> 143
    //   49: aload #11
    //   51: invokevirtual getBoundEndpoints : ()Ljava/util/List;
    //   54: astore #12
    //   56: aload #12
    //   58: invokeinterface iterator : ()Ljava/util/Iterator;
    //   63: astore #13
    //   65: aload #13
    //   67: invokeinterface hasNext : ()Z
    //   72: ifeq -> 143
    //   75: aload #13
    //   77: invokeinterface next : ()Ljava/lang/Object;
    //   82: checkcast com/sun/xml/internal/ws/api/server/BoundEndpoint
    //   85: astore #14
    //   87: aload #14
    //   89: invokeinterface getEndpoint : ()Lcom/sun/xml/internal/ws/api/server/WSEndpoint;
    //   94: astore #15
    //   96: aload #15
    //   98: invokevirtual getServiceName : ()Ljavax/xml/namespace/QName;
    //   101: aload_3
    //   102: invokevirtual equals : (Ljava/lang/Object;)Z
    //   105: ifeq -> 140
    //   108: aload #15
    //   110: invokevirtual getPortName : ()Ljavax/xml/namespace/QName;
    //   113: aload #4
    //   115: invokevirtual equals : (Ljava/lang/Object;)Z
    //   118: ifeq -> 140
    //   121: aload #14
    //   123: invokeinterface getAddress : ()Ljava/net/URI;
    //   128: invokevirtual toString : ()Ljava/lang/String;
    //   131: astore_1
    //   132: goto -> 143
    //   135: astore #16
    //   137: goto -> 143
    //   140: goto -> 65
    //   143: aload_1
    //   144: ifnonnull -> 158
    //   147: new java/lang/IllegalStateException
    //   150: dup
    //   151: invokestatic NULL_ADDRESS : ()Ljava/lang/String;
    //   154: invokespecial <init> : (Ljava/lang/String;)V
    //   157: athrow
    //   158: aload_3
    //   159: ifnonnull -> 178
    //   162: aload #4
    //   164: ifnull -> 178
    //   167: new java/lang/IllegalStateException
    //   170: dup
    //   171: invokestatic NULL_SERVICE : ()Ljava/lang/String;
    //   174: invokespecial <init> : (Ljava/lang/String;)V
    //   177: athrow
    //   178: aconst_null
    //   179: astore #11
    //   181: aload #6
    //   183: ifnull -> 352
    //   186: invokestatic createDefaultCatalogResolver : ()Lorg/xml/sax/EntityResolver;
    //   189: astore #12
    //   191: new java/net/URL
    //   194: dup
    //   195: aload #6
    //   197: invokespecial <init> : (Ljava/lang/String;)V
    //   200: astore #13
    //   202: aload #13
    //   204: new javax/xml/transform/stream/StreamSource
    //   207: dup
    //   208: aload #13
    //   210: invokevirtual toExternalForm : ()Ljava/lang/String;
    //   213: invokespecial <init> : (Ljava/lang/String;)V
    //   216: aload #12
    //   218: iconst_1
    //   219: aload #10
    //   221: ldc com/sun/xml/internal/ws/api/wsdl/parser/WSDLParserExtension
    //   223: invokestatic find : (Ljava/lang/Class;)Lcom/sun/xml/internal/ws/util/ServiceFinder;
    //   226: invokevirtual toArray : ()[Ljava/lang/Object;
    //   229: checkcast [Lcom/sun/xml/internal/ws/api/wsdl/parser/WSDLParserExtension;
    //   232: invokestatic parse : (Ljava/net/URL;Ljavax/xml/transform/Source;Lorg/xml/sax/EntityResolver;ZLcom/sun/xml/internal/ws/api/server/Container;[Lcom/sun/xml/internal/ws/api/wsdl/parser/WSDLParserExtension;)Lcom/sun/xml/internal/ws/api/model/wsdl/WSDLModel;
    //   235: astore #14
    //   237: aload_3
    //   238: ifnull -> 316
    //   241: aload #14
    //   243: aload_3
    //   244: invokeinterface getService : (Ljavax/xml/namespace/QName;)Lcom/sun/xml/internal/ws/api/model/wsdl/WSDLService;
    //   249: astore #15
    //   251: aload #15
    //   253: ifnonnull -> 270
    //   256: new java/lang/IllegalStateException
    //   259: dup
    //   260: aload_3
    //   261: aload #6
    //   263: invokestatic NOTFOUND_SERVICE_IN_WSDL : (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;
    //   266: invokespecial <init> : (Ljava/lang/String;)V
    //   269: athrow
    //   270: aload #4
    //   272: ifnull -> 307
    //   275: aload #15
    //   277: aload #4
    //   279: invokeinterface get : (Ljavax/xml/namespace/QName;)Lcom/sun/xml/internal/ws/api/model/wsdl/WSDLPort;
    //   284: astore #16
    //   286: aload #16
    //   288: ifnonnull -> 307
    //   291: new java/lang/IllegalStateException
    //   294: dup
    //   295: aload #4
    //   297: aload_3
    //   298: aload #6
    //   300: invokestatic NOTFOUND_PORT_IN_WSDL : (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/String;
    //   303: invokespecial <init> : (Ljava/lang/String;)V
    //   306: athrow
    //   307: aload_3
    //   308: invokevirtual getNamespaceURI : ()Ljava/lang/String;
    //   311: astore #11
    //   313: goto -> 332
    //   316: aload #14
    //   318: invokeinterface getFirstServiceName : ()Ljavax/xml/namespace/QName;
    //   323: astore #15
    //   325: aload #15
    //   327: invokevirtual getNamespaceURI : ()Ljava/lang/String;
    //   330: astore #11
    //   332: goto -> 352
    //   335: astore #12
    //   337: new java/lang/IllegalStateException
    //   340: dup
    //   341: aload #6
    //   343: invokestatic ERROR_WSDL : (Ljava/lang/Object;)Ljava/lang/String;
    //   346: aload #12
    //   348: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
    //   351: athrow
    //   352: aload #5
    //   354: ifnull -> 370
    //   357: aload #5
    //   359: invokeinterface size : ()I
    //   364: ifne -> 370
    //   367: aconst_null
    //   368: astore #5
    //   370: new com/sun/xml/internal/ws/api/addressing/WSEndpointReference
    //   373: dup
    //   374: ldc javax/xml/ws/wsaddressing/W3CEndpointReference
    //   376: invokestatic fromSpecClass : (Ljava/lang/Class;)Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;
    //   379: aload_1
    //   380: aload_3
    //   381: aload #4
    //   383: aload_2
    //   384: aload #5
    //   386: aload #6
    //   388: aload #11
    //   390: aload #7
    //   392: aload #8
    //   394: aload #9
    //   396: invokespecial <init> : (Lcom/sun/xml/internal/ws/api/addressing/AddressingVersion;Ljava/lang/String;Ljavax/xml/namespace/QName;Ljavax/xml/namespace/QName;Ljavax/xml/namespace/QName;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/Map;)V
    //   399: ldc javax/xml/ws/wsaddressing/W3CEndpointReference
    //   401: invokevirtual toSpec : (Ljava/lang/Class;)Ljavax/xml/ws/EndpointReference;
    //   404: checkcast javax/xml/ws/wsaddressing/W3CEndpointReference
    //   407: areturn
    // Exception table:
    //   from	to	target	type
    //   121	132	135	javax/xml/ws/WebServiceException
    //   186	332	335	java/lang/Exception }
  
  private static JAXBContext getEPRJaxbContext() { return (JAXBContext)AccessController.doPrivileged(new PrivilegedAction<JAXBContext>() {
          public JAXBContext run() {
            try {
              return JAXBContext.newInstance(new Class[] { com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.class, W3CEndpointReference.class });
            } catch (JAXBException jAXBException) {
              throw new WebServiceException("Error creating JAXBContext for W3CEndpointReference. ", jAXBException);
            } 
          }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\spi\ProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
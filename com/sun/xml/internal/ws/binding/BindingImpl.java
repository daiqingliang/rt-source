package com.sun.xml.internal.ws.binding;

import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.developer.BindingTypeFeature;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.activation.CommandInfo;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

public abstract class BindingImpl implements WSBinding {
  protected static final WebServiceFeature[] EMPTY_FEATURES = new WebServiceFeature[0];
  
  private HandlerConfiguration handlerConfig;
  
  private final Set<QName> addedHeaders = new HashSet();
  
  private final Set<QName> knownHeaders = new HashSet();
  
  private final Set<QName> unmodKnownHeaders = Collections.unmodifiableSet(this.knownHeaders);
  
  private final BindingID bindingId;
  
  protected final WebServiceFeatureList features;
  
  protected final Map<QName, WebServiceFeatureList> operationFeatures = new HashMap();
  
  protected final Map<QName, WebServiceFeatureList> inputMessageFeatures = new HashMap();
  
  protected final Map<QName, WebServiceFeatureList> outputMessageFeatures = new HashMap();
  
  protected final Map<MessageKey, WebServiceFeatureList> faultMessageFeatures = new HashMap();
  
  protected Service.Mode serviceMode = Service.Mode.PAYLOAD;
  
  protected MessageContextFactory messageContextFactory;
  
  protected BindingImpl(BindingID paramBindingID, WebServiceFeature... paramVarArgs) {
    this.bindingId = paramBindingID;
    this.handlerConfig = new HandlerConfiguration(Collections.emptySet(), Collections.emptyList());
    if (this.handlerConfig.getHandlerKnownHeaders() != null)
      this.knownHeaders.addAll(this.handlerConfig.getHandlerKnownHeaders()); 
    this.features = new WebServiceFeatureList(paramVarArgs);
    this.features.validate();
  }
  
  @NotNull
  public List<Handler> getHandlerChain() { return this.handlerConfig.getHandlerChain(); }
  
  public HandlerConfiguration getHandlerConfig() { return this.handlerConfig; }
  
  protected void setHandlerConfig(HandlerConfiguration paramHandlerConfiguration) {
    this.handlerConfig = paramHandlerConfiguration;
    this.knownHeaders.clear();
    this.knownHeaders.addAll(this.addedHeaders);
    if (paramHandlerConfiguration != null && paramHandlerConfiguration.getHandlerKnownHeaders() != null)
      this.knownHeaders.addAll(paramHandlerConfiguration.getHandlerKnownHeaders()); 
  }
  
  public void setMode(@NotNull Service.Mode paramMode) { this.serviceMode = paramMode; }
  
  public Set<QName> getKnownHeaders() { return this.unmodKnownHeaders; }
  
  public boolean addKnownHeader(QName paramQName) {
    this.addedHeaders.add(paramQName);
    return this.knownHeaders.add(paramQName);
  }
  
  @NotNull
  public BindingID getBindingId() { return this.bindingId; }
  
  public final SOAPVersion getSOAPVersion() { return this.bindingId.getSOAPVersion(); }
  
  public AddressingVersion getAddressingVersion() {
    AddressingVersion addressingVersion;
    if (this.features.isEnabled(javax.xml.ws.soap.AddressingFeature.class)) {
      addressingVersion = AddressingVersion.W3C;
    } else if (this.features.isEnabled(com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature.class)) {
      addressingVersion = AddressingVersion.MEMBER;
    } else {
      addressingVersion = null;
    } 
    return addressingVersion;
  }
  
  @NotNull
  public final Codec createCodec() {
    initializeJavaActivationHandlers();
    return this.bindingId.createEncoder(this);
  }
  
  public static void initializeJavaActivationHandlers() {
    try {
      CommandMap commandMap = CommandMap.getDefaultCommandMap();
      if (commandMap instanceof MailcapCommandMap) {
        MailcapCommandMap mailcapCommandMap = (MailcapCommandMap)commandMap;
        if (!cmdMapInitialized(mailcapCommandMap)) {
          mailcapCommandMap.addMailcap("text/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          mailcapCommandMap.addMailcap("application/xml;;x-java-content-handler=com.sun.xml.internal.ws.encoding.XmlDataContentHandler");
          mailcapCommandMap.addMailcap("image/*;;x-java-content-handler=com.sun.xml.internal.ws.encoding.ImageDataContentHandler");
          mailcapCommandMap.addMailcap("text/plain;;x-java-content-handler=com.sun.xml.internal.ws.encoding.StringDataContentHandler");
        } 
      } 
    } catch (Throwable throwable) {}
  }
  
  private static boolean cmdMapInitialized(MailcapCommandMap paramMailcapCommandMap) {
    CommandInfo[] arrayOfCommandInfo = paramMailcapCommandMap.getAllCommands("text/xml");
    if (arrayOfCommandInfo == null || arrayOfCommandInfo.length == 0)
      return false; 
    String str1 = "com.sun.xml.internal.messaging.saaj.soap.XmlDataContentHandler";
    String str2 = "com.sun.xml.internal.ws.encoding.XmlDataContentHandler";
    for (CommandInfo commandInfo : arrayOfCommandInfo) {
      String str = commandInfo.getCommandClass();
      if (str1.equals(str) || str2.equals(str))
        return true; 
    } 
    return false;
  }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID) { return paramBindingID.equals(BindingID.XML_HTTP) ? new HTTPBindingImpl() : new SOAPBindingImpl(paramBindingID); }
  
  public static BindingImpl create(@NotNull BindingID paramBindingID, WebServiceFeature[] paramArrayOfWebServiceFeature) {
    for (WebServiceFeature webServiceFeature : paramArrayOfWebServiceFeature) {
      if (webServiceFeature instanceof BindingTypeFeature) {
        BindingTypeFeature bindingTypeFeature = (BindingTypeFeature)webServiceFeature;
        paramBindingID = BindingID.parse(bindingTypeFeature.getBindingId());
      } 
    } 
    return paramBindingID.equals(BindingID.XML_HTTP) ? new HTTPBindingImpl(paramArrayOfWebServiceFeature) : new SOAPBindingImpl(paramBindingID, paramArrayOfWebServiceFeature);
  }
  
  public static WSBinding getDefaultBinding() { return new SOAPBindingImpl(BindingID.SOAP11_HTTP); }
  
  public String getBindingID() { return this.bindingId.toString(); }
  
  @Nullable
  public <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass) { return (F)this.features.get(paramClass); }
  
  @Nullable
  public <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> paramClass, @NotNull QName paramQName) {
    WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return (F)FeatureListUtil.mergeFeature(paramClass, webServiceFeatureList, this.features);
  }
  
  public boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass) { return this.features.isEnabled(paramClass); }
  
  public boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @NotNull QName paramQName) {
    WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return FeatureListUtil.isFeatureEnabled(paramClass, webServiceFeatureList, this.features);
  }
  
  @NotNull
  public WebServiceFeatureList getFeatures() {
    if (!isFeatureEnabled(com.oracle.webservices.internal.api.EnvelopeStyleFeature.class)) {
      WebServiceFeature[] arrayOfWebServiceFeature = { getSOAPVersion().toFeature() };
      this.features.mergeFeatures(arrayOfWebServiceFeature, false);
    } 
    return this.features;
  }
  
  @NotNull
  public WebServiceFeatureList getOperationFeatures(@NotNull QName paramQName) {
    WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { webServiceFeatureList, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getInputMessageFeatures(@NotNull QName paramQName) {
    WebServiceFeatureList webServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    WebServiceFeatureList webServiceFeatureList2 = (WebServiceFeatureList)this.inputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { webServiceFeatureList1, webServiceFeatureList2, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getOutputMessageFeatures(@NotNull QName paramQName) {
    WebServiceFeatureList webServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
    WebServiceFeatureList webServiceFeatureList2 = (WebServiceFeatureList)this.outputMessageFeatures.get(paramQName);
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { webServiceFeatureList1, webServiceFeatureList2, this.features });
  }
  
  @NotNull
  public WebServiceFeatureList getFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2) {
    WebServiceFeatureList webServiceFeatureList1 = (WebServiceFeatureList)this.operationFeatures.get(paramQName1);
    WebServiceFeatureList webServiceFeatureList2 = (WebServiceFeatureList)this.faultMessageFeatures.get(new MessageKey(paramQName1, paramQName2));
    return FeatureListUtil.mergeList(new WebServiceFeatureList[] { webServiceFeatureList1, webServiceFeatureList2, this.features });
  }
  
  public void setOperationFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs) {
    if (paramVarArgs != null) {
      WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.operationFeatures.get(paramQName);
      if (webServiceFeatureList == null)
        webServiceFeatureList = new WebServiceFeatureList(); 
      for (WebServiceFeature webServiceFeature : paramVarArgs)
        webServiceFeatureList.add(webServiceFeature); 
      this.operationFeatures.put(paramQName, webServiceFeatureList);
    } 
  }
  
  public void setInputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs) {
    if (paramVarArgs != null) {
      WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.inputMessageFeatures.get(paramQName);
      if (webServiceFeatureList == null)
        webServiceFeatureList = new WebServiceFeatureList(); 
      for (WebServiceFeature webServiceFeature : paramVarArgs)
        webServiceFeatureList.add(webServiceFeature); 
      this.inputMessageFeatures.put(paramQName, webServiceFeatureList);
    } 
  }
  
  public void setOutputMessageFeatures(@NotNull QName paramQName, WebServiceFeature... paramVarArgs) {
    if (paramVarArgs != null) {
      WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.outputMessageFeatures.get(paramQName);
      if (webServiceFeatureList == null)
        webServiceFeatureList = new WebServiceFeatureList(); 
      for (WebServiceFeature webServiceFeature : paramVarArgs)
        webServiceFeatureList.add(webServiceFeature); 
      this.outputMessageFeatures.put(paramQName, webServiceFeatureList);
    } 
  }
  
  public void setFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2, WebServiceFeature... paramVarArgs) {
    if (paramVarArgs != null) {
      MessageKey messageKey = new MessageKey(paramQName1, paramQName2);
      WebServiceFeatureList webServiceFeatureList = (WebServiceFeatureList)this.faultMessageFeatures.get(messageKey);
      if (webServiceFeatureList == null)
        webServiceFeatureList = new WebServiceFeatureList(); 
      for (WebServiceFeature webServiceFeature : paramVarArgs)
        webServiceFeatureList.add(webServiceFeature); 
      this.faultMessageFeatures.put(messageKey, webServiceFeatureList);
    } 
  }
  
  @NotNull
  public MessageContextFactory getMessageContextFactory() {
    if (this.messageContextFactory == null)
      this.messageContextFactory = MessageContextFactory.createFactory(getFeatures().toArray()); 
    return this.messageContextFactory;
  }
  
  protected static class MessageKey {
    private final QName operationName;
    
    private final QName messageName;
    
    public MessageKey(QName param1QName1, QName param1QName2) {
      this.operationName = param1QName1;
      this.messageName = param1QName2;
    }
    
    public int hashCode() {
      int i = (this.operationName != null) ? this.operationName.hashCode() : 0;
      int j = (this.messageName != null) ? this.messageName.hashCode() : 0;
      return (i + j) * j + i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      MessageKey messageKey = (MessageKey)param1Object;
      return (this.operationName != messageKey.operationName && (this.operationName == null || !this.operationName.equals(messageKey.operationName))) ? false : (!(this.messageName != messageKey.messageName && (this.messageName == null || !this.messageName.equals(messageKey.messageName))));
    }
    
    public String toString() { return "(" + this.operationName + ", " + this.messageName + ")"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\binding\BindingImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
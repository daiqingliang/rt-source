package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.MessageContextFactory;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.Handler;

public interface WSBinding extends Binding {
  SOAPVersion getSOAPVersion();
  
  AddressingVersion getAddressingVersion();
  
  @NotNull
  BindingID getBindingId();
  
  @NotNull
  List<Handler> getHandlerChain();
  
  boolean isFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass);
  
  boolean isOperationFeatureEnabled(@NotNull Class<? extends WebServiceFeature> paramClass, @NotNull QName paramQName);
  
  @Nullable
  <F extends WebServiceFeature> F getFeature(@NotNull Class<F> paramClass);
  
  @Nullable
  <F extends WebServiceFeature> F getOperationFeature(@NotNull Class<F> paramClass, @NotNull QName paramQName);
  
  @NotNull
  WSFeatureList getFeatures();
  
  @NotNull
  WSFeatureList getOperationFeatures(@NotNull QName paramQName);
  
  @NotNull
  WSFeatureList getInputMessageFeatures(@NotNull QName paramQName);
  
  @NotNull
  WSFeatureList getOutputMessageFeatures(@NotNull QName paramQName);
  
  @NotNull
  WSFeatureList getFaultMessageFeatures(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  @NotNull
  Set<QName> getKnownHeaders();
  
  boolean addKnownHeader(QName paramQName);
  
  @NotNull
  MessageContextFactory getMessageContextFactory();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\WSBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
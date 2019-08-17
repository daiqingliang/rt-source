package com.oracle.webservices.internal.api.message;

import com.oracle.webservices.internal.api.EnvelopeStyle;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.MessageContextFactory;
import com.sun.xml.internal.ws.util.ServiceFinder;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.WebServiceFeature;

public abstract class MessageContextFactory {
  private static final MessageContextFactory DEFAULT = new MessageContextFactory(new WebServiceFeature[0]);
  
  protected abstract MessageContextFactory newFactory(WebServiceFeature... paramVarArgs);
  
  public abstract MessageContext createContext();
  
  public abstract MessageContext createContext(SOAPMessage paramSOAPMessage);
  
  public abstract MessageContext createContext(Source paramSource);
  
  public abstract MessageContext createContext(Source paramSource, EnvelopeStyle.Style paramStyle);
  
  public abstract MessageContext createContext(InputStream paramInputStream, String paramString) throws IOException;
  
  @Deprecated
  public abstract MessageContext createContext(InputStream paramInputStream, MimeHeaders paramMimeHeaders) throws IOException;
  
  public static MessageContextFactory createFactory(WebServiceFeature... paramVarArgs) { return createFactory(null, paramVarArgs); }
  
  public static MessageContextFactory createFactory(ClassLoader paramClassLoader, WebServiceFeature... paramVarArgs) {
    for (MessageContextFactory messageContextFactory1 : ServiceFinder.find(MessageContextFactory.class, paramClassLoader)) {
      MessageContextFactory messageContextFactory2 = messageContextFactory1.newFactory(paramVarArgs);
      if (messageContextFactory2 != null)
        return messageContextFactory2; 
    } 
    return new MessageContextFactory(paramVarArgs);
  }
  
  @Deprecated
  public abstract MessageContext doCreate();
  
  @Deprecated
  public abstract MessageContext doCreate(SOAPMessage paramSOAPMessage);
  
  @Deprecated
  public abstract MessageContext doCreate(Source paramSource, SOAPVersion paramSOAPVersion);
  
  @Deprecated
  public static MessageContext create(ClassLoader... paramVarArgs) { return serviceFinder(paramVarArgs, new Creator() {
          public MessageContext create(MessageContextFactory param1MessageContextFactory) { return param1MessageContextFactory.doCreate(); }
        }); }
  
  @Deprecated
  public static MessageContext create(final SOAPMessage m, ClassLoader... paramVarArgs) { return serviceFinder(paramVarArgs, new Creator() {
          public MessageContext create(MessageContextFactory param1MessageContextFactory) { return param1MessageContextFactory.doCreate(m); }
        }); }
  
  @Deprecated
  public static MessageContext create(final Source m, final SOAPVersion v, ClassLoader... paramVarArgs) { return serviceFinder(paramVarArgs, new Creator() {
          public MessageContext create(MessageContextFactory param1MessageContextFactory) { return param1MessageContextFactory.doCreate(m, v); }
        }); }
  
  @Deprecated
  private static MessageContext serviceFinder(ClassLoader[] paramArrayOfClassLoader, Creator paramCreator) {
    ClassLoader classLoader = (paramArrayOfClassLoader.length == 0) ? null : paramArrayOfClassLoader[0];
    for (MessageContextFactory messageContextFactory : ServiceFinder.find(MessageContextFactory.class, classLoader)) {
      MessageContext messageContext = paramCreator.create(messageContextFactory);
      if (messageContext != null)
        return messageContext; 
    } 
    return paramCreator.create(DEFAULT);
  }
  
  @Deprecated
  private static interface Creator {
    MessageContext create(MessageContextFactory param1MessageContextFactory);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\message\MessageContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
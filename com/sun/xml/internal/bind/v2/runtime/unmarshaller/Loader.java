package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JaxBeanInfo;
import java.util.Collection;
import java.util.Collections;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.namespace.QName;
import org.xml.sax.SAXException;

public abstract class Loader {
  protected boolean expectText;
  
  protected Loader(boolean paramBoolean) { this.expectText = paramBoolean; }
  
  protected Loader() {}
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {}
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    reportUnexpectedChildElement(paramTagName, true);
    paramState.setLoader(Discarder.INSTANCE);
    paramState.setReceiver(null);
  }
  
  protected final void reportUnexpectedChildElement(TagName paramTagName, boolean paramBoolean) throws SAXException {
    if (paramBoolean) {
      UnmarshallingContext unmarshallingContext = UnmarshallingContext.getInstance();
      if (!unmarshallingContext.parent.hasEventHandler() || !unmarshallingContext.shouldErrorBeReported())
        return; 
    } 
    if (paramTagName.uri != paramTagName.uri.intern() || paramTagName.local != paramTagName.local.intern()) {
      reportError(Messages.UNINTERNED_STRINGS.format(new Object[0]), paramBoolean);
    } else {
      reportError(Messages.UNEXPECTED_ELEMENT.format(new Object[] { paramTagName.uri, paramTagName.local, computeExpectedElements() }, ), paramBoolean);
    } 
  }
  
  public Collection<QName> getExpectedChildElements() { return Collections.emptyList(); }
  
  public Collection<QName> getExpectedAttributes() { return Collections.emptyList(); }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence) throws SAXException {
    paramCharSequence = paramCharSequence.toString().replace('\r', ' ').replace('\n', ' ').replace('\t', ' ').trim();
    reportError(Messages.UNEXPECTED_TEXT.format(new Object[] { paramCharSequence }, ), true);
  }
  
  public final boolean expectText() { return this.expectText; }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {}
  
  private String computeExpectedElements() {
    StringBuilder stringBuilder = new StringBuilder();
    for (QName qName : getExpectedChildElements()) {
      if (stringBuilder.length() != 0)
        stringBuilder.append(','); 
      stringBuilder.append("<{").append(qName.getNamespaceURI()).append('}').append(qName.getLocalPart()).append('>');
    } 
    return (stringBuilder.length() == 0) ? "(none)" : stringBuilder.toString();
  }
  
  protected final void fireBeforeUnmarshal(JaxBeanInfo paramJaxBeanInfo, Object paramObject, UnmarshallingContext.State paramState) throws SAXException {
    if (paramJaxBeanInfo.lookForLifecycleMethods()) {
      UnmarshallingContext unmarshallingContext = paramState.getContext();
      Unmarshaller.Listener listener = unmarshallingContext.parent.getListener();
      if (paramJaxBeanInfo.hasBeforeUnmarshalMethod())
        paramJaxBeanInfo.invokeBeforeUnmarshalMethod(unmarshallingContext.parent, paramObject, paramState.getPrev().getTarget()); 
      if (listener != null)
        listener.beforeUnmarshal(paramObject, paramState.getPrev().getTarget()); 
    } 
  }
  
  protected final void fireAfterUnmarshal(JaxBeanInfo paramJaxBeanInfo, Object paramObject, UnmarshallingContext.State paramState) throws SAXException {
    if (paramJaxBeanInfo.lookForLifecycleMethods()) {
      UnmarshallingContext unmarshallingContext = paramState.getContext();
      Unmarshaller.Listener listener = unmarshallingContext.parent.getListener();
      if (paramJaxBeanInfo.hasAfterUnmarshalMethod())
        paramJaxBeanInfo.invokeAfterUnmarshalMethod(unmarshallingContext.parent, paramObject, paramState.getTarget()); 
      if (listener != null)
        listener.afterUnmarshal(paramObject, paramState.getTarget()); 
    } 
  }
  
  protected static void handleGenericException(Exception paramException) throws SAXException { handleGenericException(paramException, false); }
  
  public static void handleGenericException(Exception paramException, boolean paramBoolean) throws SAXException { reportError(paramException.getMessage(), paramException, paramBoolean); }
  
  public static void handleGenericError(Error paramError) throws SAXException { reportError(paramError.getMessage(), false); }
  
  protected static void reportError(String paramString, boolean paramBoolean) throws SAXException { reportError(paramString, null, paramBoolean); }
  
  public static void reportError(String paramString, Exception paramException, boolean paramBoolean) throws SAXException {
    UnmarshallingContext unmarshallingContext = UnmarshallingContext.getInstance();
    unmarshallingContext.handleEvent(new ValidationEventImpl(paramBoolean ? 1 : 2, paramString, unmarshallingContext.getLocator().getLocation(), paramException), paramBoolean);
  }
  
  protected static void handleParseConversionException(UnmarshallingContext.State paramState, Exception paramException) throws SAXException { paramState.getContext().handleError(paramException); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\Loader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
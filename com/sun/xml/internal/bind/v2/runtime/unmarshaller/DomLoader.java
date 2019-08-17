package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;

public class DomLoader<ResultT extends Result> extends Loader {
  private final DomHandler<?, ResultT> dom;
  
  public DomLoader(DomHandler<?, ResultT> paramDomHandler) {
    super(true);
    this.dom = paramDomHandler;
  }
  
  public void startElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    if (paramState.getTarget() == null)
      paramState.setTarget(new State(unmarshallingContext)); 
    State state = (State)paramState.getTarget();
    try {
      state.declarePrefixes(unmarshallingContext, unmarshallingContext.getNewlyDeclaredPrefixes());
      state.handler.startElement(paramTagName.uri, paramTagName.local, paramTagName.getQname(), paramTagName.atts);
    } catch (SAXException sAXException) {
      unmarshallingContext.handleError(sAXException);
      throw sAXException;
    } 
  }
  
  public void childElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    paramState.setLoader(this);
    State state = (State)paramState.getPrev().getTarget();
    state.depth++;
    paramState.setTarget(state);
  }
  
  public void text(UnmarshallingContext.State paramState, CharSequence paramCharSequence) throws SAXException {
    if (paramCharSequence.length() == 0)
      return; 
    try {
      State state;
      state.handler.characters(paramCharSequence.toString().toCharArray(), 0, paramCharSequence.length());
    } catch (SAXException sAXException) {
      paramState.getContext().handleError(sAXException);
      throw sAXException;
    } 
  }
  
  public void leaveElement(UnmarshallingContext.State paramState, TagName paramTagName) throws SAXException {
    State state = (State)paramState.getTarget();
    UnmarshallingContext unmarshallingContext = paramState.getContext();
    try {
      state.handler.endElement(paramTagName.uri, paramTagName.local, paramTagName.getQname());
      state.undeclarePrefixes(unmarshallingContext.getNewlyDeclaredPrefixes());
    } catch (SAXException sAXException) {
      unmarshallingContext.handleError(sAXException);
      throw sAXException;
    } 
    if (--state.depth == 0) {
      try {
        state.undeclarePrefixes(unmarshallingContext.getAllDeclaredPrefixes());
        state.handler.endDocument();
      } catch (SAXException sAXException) {
        unmarshallingContext.handleError(sAXException);
        throw sAXException;
      } 
      paramState.setTarget(state.getElement());
    } 
  }
  
  private final class State {
    private TransformerHandler handler = null;
    
    private final ResultT result;
    
    int depth = 1;
    
    public State(UnmarshallingContext param1UnmarshallingContext) throws SAXException {
      this.handler = JAXBContextImpl.createTransformerHandler((param1UnmarshallingContext.getJAXBContext()).disableSecurityProcessing);
      this.result = this$0.dom.createUnmarshaller(param1UnmarshallingContext);
      this.handler.setResult(this.result);
      try {
        this.handler.setDocumentLocator(param1UnmarshallingContext.getLocator());
        this.handler.startDocument();
        declarePrefixes(param1UnmarshallingContext, param1UnmarshallingContext.getAllDeclaredPrefixes());
      } catch (SAXException sAXException) {
        param1UnmarshallingContext.handleError(sAXException);
        throw sAXException;
      } 
    }
    
    public Object getElement() { return DomLoader.this.dom.getElement(this.result); }
    
    private void declarePrefixes(UnmarshallingContext param1UnmarshallingContext, String[] param1ArrayOfString) throws SAXException {
      for (int i = param1ArrayOfString.length - 1; i >= 0; i--) {
        String str = param1UnmarshallingContext.getNamespaceURI(param1ArrayOfString[i]);
        if (str == null)
          throw new IllegalStateException("prefix '" + param1ArrayOfString[i] + "' isn't bound"); 
        this.handler.startPrefixMapping(param1ArrayOfString[i], str);
      } 
    }
    
    private void undeclarePrefixes(String[] param1ArrayOfString) throws SAXException {
      for (int i = param1ArrayOfString.length - 1; i >= 0; i--)
        this.handler.endPrefixMapping(param1ArrayOfString[i]); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\DomLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
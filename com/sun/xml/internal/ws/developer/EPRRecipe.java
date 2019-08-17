package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Header;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.Source;

public final class EPRRecipe {
  private final List<Header> referenceParameters = new ArrayList();
  
  private final List<Source> metadata = new ArrayList();
  
  @NotNull
  public List<Header> getReferenceParameters() { return this.referenceParameters; }
  
  @NotNull
  public List<Source> getMetadata() { return this.metadata; }
  
  public EPRRecipe addReferenceParameter(Header paramHeader) {
    if (paramHeader == null)
      throw new IllegalArgumentException(); 
    this.referenceParameters.add(paramHeader);
    return this;
  }
  
  public EPRRecipe addReferenceParameters(Header... paramVarArgs) {
    for (Header header : paramVarArgs)
      addReferenceParameter(header); 
    return this;
  }
  
  public EPRRecipe addReferenceParameters(Iterable<? extends Header> paramIterable) {
    for (Header header : paramIterable)
      addReferenceParameter(header); 
    return this;
  }
  
  public EPRRecipe addMetadata(Source paramSource) {
    if (paramSource == null)
      throw new IllegalArgumentException(); 
    this.metadata.add(paramSource);
    return this;
  }
  
  public EPRRecipe addMetadata(Source... paramVarArgs) {
    for (Source source : paramVarArgs)
      addMetadata(source); 
    return this;
  }
  
  public EPRRecipe addMetadata(Iterable<? extends Source> paramIterable) {
    for (Source source : paramIterable)
      addMetadata(source); 
    return this;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\EPRRecipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
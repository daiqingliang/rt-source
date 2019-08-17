package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public final class AssertionCreationException extends PolicyException {
  private final AssertionData assertionData;
  
  public AssertionCreationException(AssertionData paramAssertionData, String paramString) {
    super(paramString);
    this.assertionData = paramAssertionData;
  }
  
  public AssertionCreationException(AssertionData paramAssertionData, String paramString, Throwable paramThrowable) {
    super(paramString, paramThrowable);
    this.assertionData = paramAssertionData;
  }
  
  public AssertionCreationException(AssertionData paramAssertionData, Throwable paramThrowable) {
    super(paramThrowable);
    this.assertionData = paramAssertionData;
  }
  
  public AssertionData getAssertionData() { return this.assertionData; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\policy\spi\AssertionCreationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
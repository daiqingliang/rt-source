package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class MemberSubmissionAddressingFeature extends WebServiceFeature {
  public static final String ID = "http://java.sun.com/xml/ns/jaxws/2004/08/addressing";
  
  public static final String IS_REQUIRED = "ADDRESSING_IS_REQUIRED";
  
  private boolean required;
  
  private MemberSubmissionAddressing.Validation validation = MemberSubmissionAddressing.Validation.LAX;
  
  public MemberSubmissionAddressingFeature() {}
  
  public MemberSubmissionAddressingFeature(boolean paramBoolean) {}
  
  public MemberSubmissionAddressingFeature(boolean paramBoolean1, boolean paramBoolean2) { this.required = paramBoolean2; }
  
  @FeatureConstructor({"enabled", "required", "validation"})
  public MemberSubmissionAddressingFeature(boolean paramBoolean1, boolean paramBoolean2, MemberSubmissionAddressing.Validation paramValidation) {
    this.required = paramBoolean2;
    this.validation = paramValidation;
  }
  
  @ManagedAttribute
  public String getID() { return "http://java.sun.com/xml/ns/jaxws/2004/08/addressing"; }
  
  @ManagedAttribute
  public boolean isRequired() { return this.required; }
  
  public void setRequired(boolean paramBoolean) { this.required = paramBoolean; }
  
  public void setValidation(MemberSubmissionAddressing.Validation paramValidation) { this.validation = paramValidation; }
  
  @ManagedAttribute
  public MemberSubmissionAddressing.Validation getValidation() { return this.validation; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\developer\MemberSubmissionAddressingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
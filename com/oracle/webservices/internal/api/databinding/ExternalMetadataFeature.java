package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.model.ExternalMetadataReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ExternalMetadataFeature extends WebServiceFeature {
  private static final String ID = "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature";
  
  private boolean enabled = true;
  
  private List<String> resourceNames;
  
  private List<File> files;
  
  private MetadataReader reader;
  
  public void addResources(String... paramVarArgs) {
    if (this.resourceNames == null)
      this.resourceNames = new ArrayList(); 
    Collections.addAll(this.resourceNames, paramVarArgs);
  }
  
  public List<String> getResourceNames() { return this.resourceNames; }
  
  public void addFiles(File... paramVarArgs) {
    if (this.files == null)
      this.files = new ArrayList(); 
    Collections.addAll(this.files, paramVarArgs);
  }
  
  public List<File> getFiles() { return this.files; }
  
  public boolean isEnabled() { return this.enabled; }
  
  private void setEnabled(boolean paramBoolean) { this.enabled = paramBoolean; }
  
  public String getID() { return "com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature"; }
  
  public MetadataReader getMetadataReader(ClassLoader paramClassLoader, boolean paramBoolean) { return (this.reader != null && this.enabled) ? this.reader : (this.enabled ? new ExternalMetadataReader(this.files, this.resourceNames, paramClassLoader, true, paramBoolean) : null); }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    ExternalMetadataFeature externalMetadataFeature = (ExternalMetadataFeature)paramObject;
    return (this.enabled != externalMetadataFeature.enabled) ? false : (((this.files != null) ? !this.files.equals(externalMetadataFeature.files) : (externalMetadataFeature.files != null)) ? false : (!((this.resourceNames != null) ? !this.resourceNames.equals(externalMetadataFeature.resourceNames) : (externalMetadataFeature.resourceNames != null))));
  }
  
  public int hashCode() {
    null = this.enabled ? 1 : 0;
    null = 31 * null + ((this.resourceNames != null) ? this.resourceNames.hashCode() : 0);
    return 31 * null + ((this.files != null) ? this.files.hashCode() : 0);
  }
  
  public String toString() { return "[" + getID() + ", enabled=" + this.enabled + ", resourceNames=" + this.resourceNames + ", files=" + this.files + ']'; }
  
  public static Builder builder() { return new Builder(new ExternalMetadataFeature()); }
  
  public static final class Builder {
    private final ExternalMetadataFeature o;
    
    Builder(ExternalMetadataFeature param1ExternalMetadataFeature) { this.o = param1ExternalMetadataFeature; }
    
    public ExternalMetadataFeature build() { return this.o; }
    
    public Builder addResources(String... param1VarArgs) {
      this.o.addResources(param1VarArgs);
      return this;
    }
    
    public Builder addFiles(File... param1VarArgs) {
      this.o.addFiles(param1VarArgs);
      return this;
    }
    
    public Builder setEnabled(boolean param1Boolean) {
      this.o.setEnabled(param1Boolean);
      return this;
    }
    
    public Builder setReader(MetadataReader param1MetadataReader) {
      this.o.reader = param1MetadataReader;
      return this;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\databinding\ExternalMetadataFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
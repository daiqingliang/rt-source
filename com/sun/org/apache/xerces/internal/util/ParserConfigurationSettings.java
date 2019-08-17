package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParserConfigurationSettings implements XMLComponentManager {
  protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
  
  protected Set<String> fRecognizedProperties = new HashSet();
  
  protected Map<String, Object> fProperties = new HashMap();
  
  protected Set<String> fRecognizedFeatures = new HashSet();
  
  protected Map<String, Boolean> fFeatures = new HashMap();
  
  protected XMLComponentManager fParentSettings;
  
  public ParserConfigurationSettings() { this(null); }
  
  public ParserConfigurationSettings(XMLComponentManager paramXMLComponentManager) { this.fParentSettings = paramXMLComponentManager; }
  
  public void addRecognizedFeatures(String[] paramArrayOfString) {
    int i = (paramArrayOfString != null) ? paramArrayOfString.length : 0;
    for (byte b = 0; b < i; b++) {
      String str = paramArrayOfString[b];
      if (!this.fRecognizedFeatures.contains(str))
        this.fRecognizedFeatures.add(str); 
    } 
  }
  
  public void setFeature(String paramString, boolean paramBoolean) throws XMLConfigurationException {
    FeatureState featureState = checkFeature(paramString);
    if (featureState.isExceptional())
      throw new XMLConfigurationException(featureState.status, paramString); 
    this.fFeatures.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public void addRecognizedProperties(String[] paramArrayOfString) { this.fRecognizedProperties.addAll(Arrays.asList(paramArrayOfString)); }
  
  public void setProperty(String paramString, Object paramObject) throws XMLConfigurationException {
    PropertyState propertyState = checkProperty(paramString);
    if (propertyState.isExceptional())
      throw new XMLConfigurationException(propertyState.status, paramString); 
    this.fProperties.put(paramString, paramObject);
  }
  
  public final boolean getFeature(String paramString) throws XMLConfigurationException {
    FeatureState featureState = getFeatureState(paramString);
    if (featureState.isExceptional())
      throw new XMLConfigurationException(featureState.status, paramString); 
    return featureState.state;
  }
  
  public final boolean getFeature(String paramString, boolean paramBoolean) {
    FeatureState featureState = getFeatureState(paramString);
    return featureState.isExceptional() ? paramBoolean : featureState.state;
  }
  
  public FeatureState getFeatureState(String paramString) {
    Boolean bool = (Boolean)this.fFeatures.get(paramString);
    if (bool == null) {
      FeatureState featureState = checkFeature(paramString);
      return featureState.isExceptional() ? featureState : FeatureState.is(false);
    } 
    return FeatureState.is(bool.booleanValue());
  }
  
  public final Object getProperty(String paramString) throws XMLConfigurationException {
    PropertyState propertyState = getPropertyState(paramString);
    if (propertyState.isExceptional())
      throw new XMLConfigurationException(propertyState.status, paramString); 
    return propertyState.state;
  }
  
  public final Object getProperty(String paramString, Object paramObject) {
    PropertyState propertyState = getPropertyState(paramString);
    return propertyState.isExceptional() ? paramObject : propertyState.state;
  }
  
  public PropertyState getPropertyState(String paramString) {
    Object object = this.fProperties.get(paramString);
    if (object == null) {
      PropertyState propertyState = checkProperty(paramString);
      if (propertyState.isExceptional())
        return propertyState; 
    } 
    return PropertyState.is(object);
  }
  
  protected FeatureState checkFeature(String paramString) { return !this.fRecognizedFeatures.contains(paramString) ? ((this.fParentSettings != null) ? this.fParentSettings.getFeatureState(paramString) : FeatureState.NOT_RECOGNIZED) : FeatureState.RECOGNIZED; }
  
  protected PropertyState checkProperty(String paramString) {
    if (!this.fRecognizedProperties.contains(paramString))
      if (this.fParentSettings != null) {
        PropertyState propertyState = this.fParentSettings.getPropertyState(paramString);
        if (propertyState.isExceptional())
          return propertyState; 
      } else {
        return PropertyState.NOT_RECOGNIZED;
      }  
    return PropertyState.RECOGNIZED;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\ParserConfigurationSettings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
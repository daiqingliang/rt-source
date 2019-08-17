package javax.xml.transform;

import java.util.Properties;

public interface Templates {
  Transformer newTransformer() throws TransformerConfigurationException;
  
  Properties getOutputProperties();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\transform\Templates.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
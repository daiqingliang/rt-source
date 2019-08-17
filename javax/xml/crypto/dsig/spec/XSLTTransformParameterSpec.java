package javax.xml.crypto.dsig.spec;

import javax.xml.crypto.XMLStructure;

public final class XSLTTransformParameterSpec implements TransformParameterSpec {
  private XMLStructure stylesheet;
  
  public XSLTTransformParameterSpec(XMLStructure paramXMLStructure) {
    if (paramXMLStructure == null)
      throw new NullPointerException(); 
    this.stylesheet = paramXMLStructure;
  }
  
  public XMLStructure getStylesheet() { return this.stylesheet; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\dsig\spec\XSLTTransformParameterSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
package org.jcp.xml.dsig.internal.dom;

import java.security.InvalidAlgorithmParameterException;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

public final class DOMBase64Transform extends ApacheTransform {
  public void init(TransformParameterSpec paramTransformParameterSpec) throws InvalidAlgorithmParameterException {
    if (paramTransformParameterSpec != null)
      throw new InvalidAlgorithmParameterException("params must be null"); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\jcp\xml\dsig\internal\dom\DOMBase64Transform.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
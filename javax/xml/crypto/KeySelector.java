package javax.xml.crypto;

import java.security.Key;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;

public abstract class KeySelector {
  public abstract KeySelectorResult select(KeyInfo paramKeyInfo, Purpose paramPurpose, AlgorithmMethod paramAlgorithmMethod, XMLCryptoContext paramXMLCryptoContext) throws KeySelectorException;
  
  public static KeySelector singletonKeySelector(Key paramKey) { return new SingletonKeySelector(paramKey); }
  
  public static class Purpose {
    private final String name;
    
    public static final Purpose SIGN = new Purpose("sign");
    
    public static final Purpose VERIFY = new Purpose("verify");
    
    public static final Purpose ENCRYPT = new Purpose("encrypt");
    
    public static final Purpose DECRYPT = new Purpose("decrypt");
    
    private Purpose(String param1String) { this.name = param1String; }
    
    public String toString() { return this.name; }
  }
  
  private static class SingletonKeySelector extends KeySelector {
    private final Key key;
    
    SingletonKeySelector(Key param1Key) {
      if (param1Key == null)
        throw new NullPointerException(); 
      this.key = param1Key;
    }
    
    public KeySelectorResult select(KeyInfo param1KeyInfo, KeySelector.Purpose param1Purpose, AlgorithmMethod param1AlgorithmMethod, XMLCryptoContext param1XMLCryptoContext) throws KeySelectorException { return new KeySelectorResult() {
          public Key getKey() { return KeySelector.SingletonKeySelector.this.key; }
        }; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\crypto\KeySelector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
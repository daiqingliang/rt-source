package javax.net.ssl;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class KeyStoreBuilderParameters implements ManagerFactoryParameters {
  private final List<KeyStore.Builder> parameters;
  
  public KeyStoreBuilderParameters(KeyStore.Builder paramBuilder) { this.parameters = Collections.singletonList(Objects.requireNonNull(paramBuilder)); }
  
  public KeyStoreBuilderParameters(List<KeyStore.Builder> paramList) {
    if (paramList.isEmpty())
      throw new IllegalArgumentException(); 
    this.parameters = Collections.unmodifiableList(new ArrayList(paramList));
  }
  
  public List<KeyStore.Builder> getParameters() { return this.parameters; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\net\ssl\KeyStoreBuilderParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
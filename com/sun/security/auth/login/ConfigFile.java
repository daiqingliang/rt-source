package com.sun.security.auth.login;

import java.net.URI;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import jdk.Exported;
import sun.security.provider.ConfigFile;

@Exported
public class ConfigFile extends Configuration {
  private final ConfigFile.Spi spi = new ConfigFile.Spi();
  
  public ConfigFile() {}
  
  public ConfigFile(URI paramURI) {}
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString) { return this.spi.engineGetAppConfigurationEntry(paramString); }
  
  public void refresh() { this.spi.engineRefresh(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\security\auth\login\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
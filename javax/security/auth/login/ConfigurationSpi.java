package javax.security.auth.login;

public abstract class ConfigurationSpi {
  protected abstract AppConfigurationEntry[] engineGetAppConfigurationEntry(String paramString);
  
  protected void engineRefresh() {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\security\auth\login\ConfigurationSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
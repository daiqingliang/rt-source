package sun.security.tools.policytool;

class AudioPerm extends Perm {
  public AudioPerm() { super("AudioPermission", "javax.sound.sampled.AudioPermission", new String[] { "play", "record" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\AudioPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
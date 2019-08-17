package sun.security.tools.policytool;

class AWTPerm extends Perm {
  public AWTPerm() { super("AWTPermission", "java.awt.AWTPermission", new String[] { 
          "accessClipboard", "accessEventQueue", "accessSystemTray", "createRobot", "fullScreenExclusive", "listenToAllAWTEvents", "readDisplayPixels", "replaceKeyboardFocusManager", "setAppletStub", "setWindowAlwaysOnTop", 
          "showWindowWithoutWarningBanner", "toolkitModality", "watchMousePointer" }, null); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\AWTPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
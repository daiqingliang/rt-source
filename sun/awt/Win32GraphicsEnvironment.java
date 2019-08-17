package sun.awt;

import java.awt.AWTError;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;
import sun.awt.windows.WToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;
import sun.java2d.WindowsSurfaceManagerFactory;
import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.windows.WindowsFlags;

public class Win32GraphicsEnvironment extends SunGraphicsEnvironment {
  private static boolean displayInitialized;
  
  private ArrayList<WeakReference<Win32GraphicsDevice>> oldDevices;
  
  private static native void initDisplay();
  
  public static void initDisplayWrapper() {
    if (!displayInitialized) {
      displayInitialized = true;
      initDisplay();
    } 
  }
  
  protected native int getNumScreens();
  
  protected native int getDefaultScreen();
  
  public GraphicsDevice getDefaultScreenDevice() {
    GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
    if (arrayOfGraphicsDevice.length == 0)
      throw new AWTError("no screen devices"); 
    int i = getDefaultScreen();
    return arrayOfGraphicsDevice[(0 < i && i < arrayOfGraphicsDevice.length) ? i : 0];
  }
  
  public native int getXResolution();
  
  public native int getYResolution();
  
  public void displayChanged() {
    GraphicsDevice[] arrayOfGraphicsDevice1 = new GraphicsDevice[getNumScreens()];
    GraphicsDevice[] arrayOfGraphicsDevice2 = this.screens;
    if (arrayOfGraphicsDevice2 != null) {
      for (byte b = 0; b < arrayOfGraphicsDevice2.length; b++) {
        if (!(this.screens[b] instanceof Win32GraphicsDevice)) {
          assert false : arrayOfGraphicsDevice2[b];
        } else {
          Win32GraphicsDevice win32GraphicsDevice = (Win32GraphicsDevice)arrayOfGraphicsDevice2[b];
          if (!win32GraphicsDevice.isValid()) {
            if (this.oldDevices == null)
              this.oldDevices = new ArrayList(); 
            this.oldDevices.add(new WeakReference(win32GraphicsDevice));
          } else if (b < arrayOfGraphicsDevice1.length) {
            arrayOfGraphicsDevice1[b] = win32GraphicsDevice;
          } 
        } 
      } 
      arrayOfGraphicsDevice2 = null;
    } 
    int i;
    for (i = 0; i < arrayOfGraphicsDevice1.length; i++) {
      if (arrayOfGraphicsDevice1[i] == null)
        arrayOfGraphicsDevice1[i] = makeScreenDevice(i); 
    } 
    this.screens = arrayOfGraphicsDevice1;
    for (GraphicsDevice graphicsDevice : this.screens) {
      if (graphicsDevice instanceof DisplayChangedListener)
        ((DisplayChangedListener)graphicsDevice).displayChanged(); 
    } 
    if (this.oldDevices != null) {
      i = getDefaultScreen();
      ListIterator listIterator = this.oldDevices.listIterator();
      while (listIterator.hasNext()) {
        Win32GraphicsDevice win32GraphicsDevice = (Win32GraphicsDevice)((WeakReference)listIterator.next()).get();
        if (win32GraphicsDevice != null) {
          win32GraphicsDevice.invalidate(i);
          win32GraphicsDevice.displayChanged();
          continue;
        } 
        listIterator.remove();
      } 
    } 
    WToolkit.resetGC();
    this.displayChanger.notifyListeners();
  }
  
  protected GraphicsDevice makeScreenDevice(int paramInt) {
    Win32GraphicsDevice win32GraphicsDevice = null;
    if (WindowsFlags.isD3DEnabled())
      win32GraphicsDevice = D3DGraphicsDevice.createDevice(paramInt); 
    if (win32GraphicsDevice == null)
      win32GraphicsDevice = new Win32GraphicsDevice(paramInt); 
    return win32GraphicsDevice;
  }
  
  public boolean isDisplayLocal() { return true; }
  
  public boolean isFlipStrategyPreferred(ComponentPeer paramComponentPeer) {
    GraphicsConfiguration graphicsConfiguration;
    if (paramComponentPeer != null && (graphicsConfiguration = paramComponentPeer.getGraphicsConfiguration()) != null) {
      GraphicsDevice graphicsDevice = graphicsConfiguration.getDevice();
      if (graphicsDevice instanceof D3DGraphicsDevice)
        return ((D3DGraphicsDevice)graphicsDevice).isD3DEnabledOnDevice(); 
    } 
    return false;
  }
  
  public static boolean isDWMCompositionEnabled() { return isDWMCompositionEnabled; }
  
  private static void dwmCompositionChanged(boolean paramBoolean) { isDWMCompositionEnabled = paramBoolean; }
  
  public static native boolean isVistaOS();
  
  static  {
    WToolkit.loadLibraries();
    WindowsFlags.initFlags();
    initDisplayWrapper();
    SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\awt\Win32GraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
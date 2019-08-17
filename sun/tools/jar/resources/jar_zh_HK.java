package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_zh_HK extends ListResourceBundle {
  protected final Object[][] getContents() { return new Object[][] { 
        { "error.bad.cflag", "'c' 旗標要求指定資訊清單或輸入檔案！" }, { "error.bad.eflag", "無法同時指定 'e' 旗標和具有 'Main-Class' 屬性的\n資訊清單！" }, { "error.bad.option", "其中一個選項 -{ctxu} 必須加以指定。" }, { "error.bad.uflag", "'u' 旗標要求指定資訊清單、'e' 旗標或輸入檔案！" }, { "error.cant.open", "無法開啟: {0} " }, { "error.create.dir", "{0} : 無法建立目錄" }, { "error.create.tempfile", "無法建立暫存檔案" }, { "error.illegal.option", "無效的選項: {0}" }, { "error.incorrect.length", "處理 {0} 時長度不正確" }, { "error.nosuch.fileordir", "{0} : 沒有這類檔案或目錄" }, 
        { "error.write.file", "寫入現有的 jar 檔案時發生錯誤" }, { "out.added.manifest", "已新增資訊清單" }, { "out.adding", "新增: {0}" }, { "out.create", "  建立: {0}" }, { "out.deflated", "(壓縮 {0}%)" }, { "out.extracted", "擷取: {0}" }, { "out.ignore.entry", "忽略項目 {0}" }, { "out.inflated", " 擴展: {0}" }, { "out.size", " (讀={0})(寫={1})" }, { "out.stored", "(儲存 0%)" }, 
        { "out.update.manifest", "已更新資訊清單" }, { "usage", "用法: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] 檔案 ...\n選項:\n    -c  建立新的歸檔\n    -t  列出歸檔的目錄\n    -x  從歸檔中擷取指定 (或所有) 檔案\n    -u  更新現有歸檔\n    -v  在標準輸出中產生詳細輸出\n    -f  指定歸檔檔案名稱\n    -m  包含指定資訊清單中的資訊清單資訊\n    -n  在建立新歸檔之後執行 Pack200 正規化\n    -e  為已隨附於可執行 jar 檔案中的獨立應用程式\n        指定應用程式進入點\n    -0  僅儲存; 不使用 ZIP 壓縮方式\n    -P  保留檔案名稱前面的 '/' (絕對路徑) 和 \"..\" (上層目錄) 元件\n    -M  不為項目建立資訊清單檔案\n    -i  為指定的 jar 檔案產生索引資訊\n    -C  變更至指定目錄並包含後面所列的檔案\n如果有任何檔案是目錄，則會對其進行遞迴處理。\n資訊清單檔案名稱、歸檔檔案名稱和進入點名稱\n的指定順序與指定 'm' 旗標、'f' 旗標和 'e' 旗標的順序相同。\n\n範例 1: 將兩個類別檔案歸檔至名為 classes.jar 的歸檔中: \n       jar cvf classes.jar Foo.class Bar.class\n範例 2: 使用現有資訊清單檔案 'mymanifest' 並將\n           foo/ 目錄中的所有檔案歸檔至 'classes.jar' 中: \n       jar cvfm classes.jar mymanifest -C foo/ .\n" } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\resources\jar_zh_HK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
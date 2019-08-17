package sun.security.tools.policytool;

import java.util.ListResourceBundle;

public class Resources_zh_HK extends ListResourceBundle {
  private static final Object[][] contents = { 
      { "NEWLINE", "\n" }, { "Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured.", "警告: 別名 {0} 的公開金鑰不存在。請確定金鑰儲存庫配置正確。" }, { "Warning.Class.not.found.class", "警告: 找不到類別 {0}" }, { "Warning.Invalid.argument.s.for.constructor.arg", "警告: 無效的建構子引數: {0}" }, { "Illegal.Principal.Type.type", "無效的 Principal 類型: {0}" }, { "Illegal.option.option", "無效的選項: {0}" }, { "Usage.policytool.options.", "用法: policytool [options]" }, { ".file.file.policy.file.location", "  [-file <file>]    原則檔案位置" }, { "New", "新增" }, { "Open", "開啟" }, 
      { "Save", "儲存" }, { "Save.As", "另存新檔" }, { "View.Warning.Log", "檢視警告記錄" }, { "Exit", "結束" }, { "Add.Policy.Entry", "新增原則項目" }, { "Edit.Policy.Entry", "編輯原則項目" }, { "Remove.Policy.Entry", "移除原則項目" }, { "Edit", "編輯" }, { "Retain", "保留" }, { "Warning.File.name.may.include.escaped.backslash.characters.It.is.not.necessary.to.escape.backslash.characters.the.tool.escapes", "警告: 檔案名稱包含遁離反斜線字元。不需要遁離反斜線字元 (撰寫原則內容至永久存放區時需要工具遁離字元)。\n\n按一下「保留」以保留輸入的名稱，或按一下「編輯」以編輯名稱。" }, 
      { "Add.Public.Key.Alias", "新增公開金鑰別名" }, { "Remove.Public.Key.Alias", "移除公開金鑰別名" }, { "File", "檔案" }, { "KeyStore", "金鑰儲存庫" }, { "Policy.File.", "原則檔案: " }, { "Could.not.open.policy.file.policyFile.e.toString.", "無法開啟原則檔案: {0}: {1}" }, { "Policy.Tool", "原則工具" }, { "Errors.have.occurred.while.opening.the.policy.configuration.View.the.Warning.Log.for.more.information.", "開啟原則記置時發生錯誤。請檢視警告記錄以取得更多的資訊" }, { "Error", "錯誤" }, { "OK", "確定" }, 
      { "Status", "狀態" }, { "Warning", "警告" }, { "Permission.", "權限:                                                       " }, { "Principal.Type.", "Principal 類型: " }, { "Principal.Name.", "Principal 名稱: " }, { "Target.Name.", "目標名稱:                                                    " }, { "Actions.", "動作:                                                             " }, { "OK.to.overwrite.existing.file.filename.", "確認覆寫現存的檔案 {0}？" }, { "Cancel", "取消" }, { "CodeBase.", "CodeBase:" }, 
      { "SignedBy.", "SignedBy:" }, { "Add.Principal", "新增 Principal" }, { "Edit.Principal", "編輯 Principal" }, { "Remove.Principal", "移除 Principal" }, { "Principals.", "Principal:" }, { ".Add.Permission", "  新增權限" }, { ".Edit.Permission", "  編輯權限" }, { "Remove.Permission", "移除權限" }, { "Done", "完成" }, { "KeyStore.URL.", "金鑰儲存庫 URL: " }, 
      { "KeyStore.Type.", "金鑰儲存庫類型:" }, { "KeyStore.Provider.", "金鑰儲存庫提供者:" }, { "KeyStore.Password.URL.", "金鑰儲存庫密碼 URL: " }, { "Principals", "Principal" }, { ".Edit.Principal.", "  編輯 Principal: " }, { ".Add.New.Principal.", "  新增 Principal: " }, { "Permissions", "權限" }, { ".Edit.Permission.", "  編輯權限:" }, { ".Add.New.Permission.", "  新增權限:" }, { "Signed.By.", "簽署人: " }, 
      { "Cannot.Specify.Principal.with.a.Wildcard.Class.without.a.Wildcard.Name", "沒有萬用字元名稱，無法指定含有萬用字元類別的 Principal" }, { "Cannot.Specify.Principal.without.a.Name", "沒有名稱，無法指定 Principal" }, { "Permission.and.Target.Name.must.have.a.value", "權限及目標名稱必須有一個值。" }, { "Remove.this.Policy.Entry.", "移除這個原則項目？" }, { "Overwrite.File", "覆寫檔案" }, { "Policy.successfully.written.to.filename", "原則成功寫入至 {0}" }, { "null.filename", "空值檔名" }, { "Save.changes.", "儲存變更？" }, { "Yes", "是" }, { "No", "否" }, 
      { "Policy.Entry", "原則項目" }, { "Save.Changes", "儲存變更" }, { "No.Policy.Entry.selected", "沒有選取原則項目" }, { "Unable.to.open.KeyStore.ex.toString.", "無法開啟金鑰儲存庫: {0}" }, { "No.principal.selected", "未選取 Principal" }, { "No.permission.selected", "沒有選取權限" }, { "name", "名稱" }, { "configuration.type", "組態類型" }, { "environment.variable.name", "環境變數名稱" }, { "library.name", "程式庫名稱" }, 
      { "package.name", "套裝程式名稱" }, { "policy.type", "原則類型" }, { "property.name", "屬性名稱" }, { "provider.name", "提供者名稱" }, { "Principal.List", "Principal 清單" }, { "Permission.List", "權限清單" }, { "Code.Base", "代碼基準" }, { "KeyStore.U.R.L.", "金鑰儲存庫 URL:" }, { "KeyStore.Password.U.R.L.", "金鑰儲存庫密碼 URL:" } };
  
  public Object[][] getContents() { return contents; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\security\tools\policytool\Resources_zh_HK.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
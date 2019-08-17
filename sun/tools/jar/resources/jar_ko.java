package sun.tools.jar.resources;

import java.util.ListResourceBundle;

public final class jar_ko extends ListResourceBundle {
  protected final Object[][] getContents() { return new Object[][] { 
        { "error.bad.cflag", "'c' 플래그를 사용하려면 Manifest 또는 입력 파일을 지정해야 합니다!" }, { "error.bad.eflag", "'e' 플래그 및 Manifest를 'Main-Class' 속성과 함께 지정할 수\n없습니다!" }, { "error.bad.option", "옵션 -{ctxu} 중 하나를 지정해야 합니다." }, { "error.bad.uflag", "'u' 플래그를 사용하려면 Manifest, 'e' 플래그 또는 입력 파일을 지정해야 합니다!" }, { "error.cant.open", "열 수 없음: {0} " }, { "error.create.dir", "{0}: 디렉토리를 생성할 수 없습니다." }, { "error.create.tempfile", "임시 파일을 생성할 수 없습니다." }, { "error.illegal.option", "잘못된 옵션: {0}" }, { "error.incorrect.length", "처리 중 올바르지 않은 길이가 발견됨: {0}" }, { "error.nosuch.fileordir", "{0}: 해당 파일 또는 디렉토리가 없습니다." }, 
        { "error.write.file", "기존 jar 파일에 쓰는 중 오류가 발생했습니다." }, { "out.added.manifest", "Manifest를 추가함" }, { "out.adding", "추가하는 중: {0}" }, { "out.create", "  생성됨: {0}" }, { "out.deflated", "({0}%를 감소함)" }, { "out.extracted", "추출됨: {0}" }, { "out.ignore.entry", "{0} 항목을 무시하는 중" }, { "out.inflated", " 증가됨: {0}" }, { "out.size", "(입력 = {0}) (출력 = {1})" }, { "out.stored", "(0%를 저장함)" }, 
        { "out.update.manifest", "Manifest를 업데이트함" }, { "usage", "사용법: jar {ctxui}[vfmn0PMe] [jar-file] [manifest-file] [entry-point] [-C dir] files ...\n옵션:\n    -c  새 아카이브를 생성합니다.\n    -t  아카이브에 대한 목차를 나열합니다.\n    -x  명명된(또는 모든) 파일을 아카이브에서 추출합니다.\n    -u  기존 아카이브를 업데이트합니다.\n    -v  표준 출력에 상세 정보 출력을 생성합니다.\n    -f  아카이브 파일 이름을 지정합니다.\n    -m  지정된 Manifest 파일의 Manifest 정보를 포함합니다.\n    -n  새 아카이브를 생성한 후 Pack200 정규화를 수행합니다.\n    -e  jar 실행 파일에 번들로 제공된 독립형 애플리케이션의 \n        애플리케이션 시작 지점을 지정합니다.\n    -0  저장 전용: ZIP 압축을 사용하지 않습니다.\n    -P  파일 이름에서 선행 '/'(절대 경로) 및 \"..\"(상위 디렉토리) 구성요소를 유지합니다.\n    -M  항목에 대해 Manifest 파일을 생성하지 않습니다.\n    -i  지정된 jar 파일에 대한 인덱스 정보를 생성합니다.\n    -C  지정된 디렉토리로 변경하고 다음 파일을 포함합니다.\n특정 파일이 디렉토리일 경우 순환적으로 처리됩니다.\nManifest 파일 이름, 아카이브 파일 이름 및 시작 지점 이름은\n'm', 'f' 및 'e' 플래그와 동일한 순서로 지정됩니다.\n\n예 1: classes.jar라는 아카이브에 두 클래스 파일을 아카이브하는 방법: \n       jar cvf classes.jar Foo.class Bar.class \n예 2: 기존 Manifest 파일 'mymanifest'를 사용하여\n           foo/ 디렉토리의 모든 파일을 'classes.jar'로 아카이브하는 방법: \n       jar cvfm classes.jar mymanifest -C foo/ ." } }; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tools\jar\resources\jar_ko.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */
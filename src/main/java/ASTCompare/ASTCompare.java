package ASTCompare;
import ASTCompare.Unity.FileDiffAction;
import Graph.ExtractJavaFile;
import Graph.Unity.ProjectInfo;
import Graph.Utils;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lp on 2020/4/27
 * 需要遍历两个项目的文件夹，进行文件的比较。我们首先针对新版本项目种的文件进行遍历。寻找是否
 * 在旧版本中存在相同的文件。
 */
public class ASTCompare {
    ProjectInfo oldVersion;
    ProjectInfo newVersion;
    File[] files;
    List<FileDiffAction> diff = new ArrayList<>();

    public static void main(String[] args) {
        Path newpath = Utils.openDirFileChooser().toPath();
        Path oldpath = Utils.openDirFileChooser().toPath();
        ProjectInfo projectInfo_old = new ProjectInfo(oldpath);
        ProjectInfo projectInfo_new = new ProjectInfo(newpath);
        ExtractJavaFile extractJavaFile = new ExtractJavaFile(projectInfo_new.getPrifxPath());
        extractJavaFile.getFileList(new File(projectInfo_new.getPrifxPath()));
        ASTCompare astCompare = new ASTCompare(projectInfo_old, projectInfo_new, extractJavaFile);
        try {
            astCompare.compare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ASTCompare(ProjectInfo oldVersion, ProjectInfo newVersion, ExtractJavaFile extractJavaFile) {
        /*需要输入项目的地址*/
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.files = extractJavaFile.getFile();

    }

    private void compare() throws Exception {
        for (File newFile : this.files) {
            //TODO 判断旧文件是否存在
            File oldFile = Change2OldFile(newFile);
            if (oldFile == null) {
                /*增加整个文件*/
            } else {
                AstComparator astComparator = new AstComparator();
                Diff result = astComparator.compare(oldFile, newFile);
                List<Operation> operations = result.getRootOperations();
                this.diff.add(new FileDiffAction(oldFile, newFile, operations));
            }


        }

    }

    private File Change2OldFile(File newFile) {
        String relative_path = newFile.getPath().replace(this.newVersion.getPrifxPath(), "");
        File oldFile = new File(this.oldVersion.getPrifxPath() + relative_path);
        if (oldFile.isFile())
            return oldFile;
        else
            /*一定是增加整个文件*/
            return null;
    }


}

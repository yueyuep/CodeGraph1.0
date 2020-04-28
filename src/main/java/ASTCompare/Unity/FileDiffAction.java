package ASTCompare.Unity;
import gumtree.spoon.diff.operations.DeleteOperation;
import gumtree.spoon.diff.operations.InsertOperation;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by lp on 2020/4/27
 * 主要涉及我们新版本的项目哪些文件发生改变，以及变化的类型
 */
public class FileDiffAction {
    private File src;
    private File dist;
    /*每个文件定义如下3种操作类型*/
    List<DeleteOperation> deleteOperations = new ArrayList<>();
    List<InsertOperation> insertOperations = new ArrayList<>();
    List<UpdateOperation> updateOperations = new ArrayList<>();

    public FileDiffAction(File src, File dist, List<Operation> diffOpeation) {
        this.src = src;
        this.dist = dist;
        for (Operation operation : diffOpeation) {
            if (operation instanceof DeleteOperation) {
                this.deleteOperations.add((DeleteOperation) operation);
            } else if (operation instanceof InsertOperation) {
                this.insertOperations.add((InsertOperation) operation);
            } else if (operation instanceof UpdateOperation) {
                this.updateOperations.add((UpdateOperation) operation);

            } else {
                System.out.println("其他行为的变更行文");
            }
        }

    }

    public File getDist() {
        return dist;
    }

    public File getSrc() {
        return src;
    }

    public List<DeleteOperation> getDeleteOperations() {
        return deleteOperations;
    }

    public List<InsertOperation> getInsertOperations() {
        return insertOperations;
    }

    public List<UpdateOperation> getUpdateOperations() {
        return updateOperations;
    }

}

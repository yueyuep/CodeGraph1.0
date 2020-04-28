package GUM;

import com.github.gumtreediff.actions.ActionGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.client.diff.TextDiff;
import com.github.gumtreediff.io.TreeIoUtils;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.Matchers;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.TreeContext;

import java.io.File;
import java.io.FileReader;

import java.util.HashMap;
import java.util.List;

/**
 Gumtree相似性度量：
 gumtree/core/src/main/java/com/github/gumtreediff/matchers/SimilarityMetrics.java
 在新版本的maven项目中，还没有加入当前功能，这个以后进行测试
 */
public class GunTree {
    public static void main(String[] args) throws Exception {
        String file1 = "H:\\CodeGraph1.0\\src\\main\\resources\\data\\A.java";
        String file2 = "H:\\CodeGraph1.0\\src\\main\\resources\\data\\B.java";


        HashMap<String, TreeContext> tc1 = new MethodDeclationVisitor().generateTreeContextList(new FileReader(new File(file1))); // retrieve the default generator for the file
        HashMap<String, TreeContext> tc2 = new MethodDeclationVisitor().generateTreeContextList(new FileReader(new File(file2))); // retrieve the default generator for the file
        ITree src = tc1.get("main").getRoot();
        ITree dst = tc2.get("test").getRoot();

        Matcher m = Matchers.getInstance().getMatcher(src, dst);
        MappingStore mappingStore = m.getMappings();

        // retrieve the default matcher
        m.match();
        ActionGenerator g = new ActionGenerator(src, dst, m.getMappings());
        g.generate();
        List<Action> actions = g.getActions();
        // return the actions
        System.out.println(TreeIoUtils.toJson(tc1.get("main")));
        System.out.println("done");
    }
}

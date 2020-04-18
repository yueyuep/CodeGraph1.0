package Graph;

import Graph.Base.BaseParse;
import com.github.javaparser.ast.Node;
import com.google.common.graph.MutableNetwork;
import lombok.Data;

import java.io.FileNotFoundException;

/**
 * Create by lp on 2020/1/6
 * 将我们的AST解析转成graph
 */
@Data
public class AST2Graph extends BaseParse {
    private MutableNetwork<Object, String> network;

    public AST2Graph(String filePath) throws Exception {
        super(filePath);

    }

    /*解析文件url*/
    public static AST2Graph newInstance(String url) {
        AST2Graph ast2Graph = null;
        try {
            ast2Graph = new AST2Graph(url);
        } catch (Exception e) {
            System.out.println("EXCEPION:AST2Graph newInstance");
        }
        return ast2Graph;
    }

    /*初始化network*/
    public void initNetwork() {

    }

    /*构建java文件的network*/
    public void constructNetwork(Node node) {
        Travel travel = new Travel();
        travel.travelNodeForCFG(node);
        network = travel.getMutableNetwork();

    }


}

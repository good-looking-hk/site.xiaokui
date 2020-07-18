package site.xiaokui.common.hk.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HK
 * @date 2020-02-20 17:44
 */
public class JsonTest {


    static class Node {
        String key;
        Integer value;
        List<Node> list;
        public Node() {
            this.list = new ArrayList<>();
        }
        public Node(String key, Integer value) {
            this.key = key;
            this.value = value;
        }
        public void appendNode(Node node) {
            this.list.add(node);
        }
        @Override
        public String toString() {
            if (this.list == null) {
                return this.key + "=" + this.value;
            }
            StringBuilder str = new StringBuilder();
            for (Node node : this.list) {
                str.append(node.toString());
            }
            return this.key + ":" + str.toString();
        }
    }

    public static List<Node> convert(Node[] nodes) {
        List<Node> list = new ArrayList<>();
        for (Node node : nodes) {
            if (!node.key.contains(".")) {
                list.add(node);
            } else {
                node = convertNode(node);
                list.add(node);
            }
        }
        return list;
    }

    private static Node convertNode(Node node) {
        if (!node.key.contains(".")) {
            return node;
        } else {
            Node newNode = new Node();
            int index = node.key.indexOf(".");
            newNode.key = node.key.substring(0, index);
            node.key = node.key.substring(index + 1);
            newNode.appendNode(convertNode(node));
            return newNode;
        }
    }

    public static void main(String[] args) {
        Node[] nodes = new Node[]{new Node("A", 1), new Node("B.A", 2), new Node("B.B", 3), new Node("CC.D.E", 4), new Node("CC.D.F",  5)};
        List<Node> list = convert(nodes);
        for (Node node : list) {
            System.out.println(node);
        }
    }
}

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimboyoung on 2018. 1. 10..
 */
public class bptree implements Serializable {

    public int nodeSize;
    public Node root;

    public bptree(int ns){
        this.nodeSize = ns - 1;
        root = new Node(ns-1);
    }

    public void insertList(List<String[]> inputs){
        for(int i = 0;i<inputs.size(); i++){
            String s[] = inputs.get(i);
            Node nnode = insert(root, Integer.parseInt(s[0]), Integer.parseInt(s[1]));
            if(nnode != null) root = nnode;
        }
    }

    public Node insert(Node node, int key, int value) {
        if(node.isLeafNode == true) {
            if(node.isFull()) {
                if(node.equals(root)) {
                    Node nnode = node.splitNode(key, value);
                    int [] lkey = new int [1];
                    Node [] pointers = new Node [2];
                    lkey[0] = nnode.leftMostKey();
                    pointers[0] = node;
                    pointers[1] = nnode;
                    Node nroot = new Node(nodeSize, lkey, pointers);
                    return nroot;
                }
                else return node.splitNode(key, value);
            }
            else {
                node.insertToNode(key, value);
                return null;
            }
        }
        else {
            Node nextNode = node.findPath(key);
            Node result = insert(nextNode, key, value);
            if(result == null) return null;
            else {
                if(node.isFull()) {
                    if(node == root) return node.insertAndSplitRoot(result);
                    else return node.insertAndSplit(result);
                }
                else {
                    node.insertToNode(result);
                    return null;
                }
            }
        }
    }

    public void deleteList(List<String[]> inputs) {
        for(int i = 0; i < inputs.size(); i++) {
            String s[] = inputs.get(i);
            deleteInRoot(Integer.parseInt(s[0]));
            //show(root, 0);
        }
    }

    public void deleteInRoot(int key) {
        if (root.isLeafNode == true) {
            root.deleteInLeaf(key);
            return;
        }
        else{
            Node nextNode = root.findPath(key);
            Node nnode = delete(nextNode, key);
            if (nnode.isEmpty()) {
                root.deleteNode(nnode);
                if(root.isEmpty()) {
                    root = root.pointers[0];
                }
            }
            root.checkAllKey();
        }
    }

    public Node delete(Node node, int key) {

        if(node.isLeafNode == true) {
            node.deleteInLeaf(key);
            if(node.isLack()) {
                if (node.sibling[1] != null && node.sibling[1].isEnough() == true) {
                    node.moveLeftOneLeaf(node.sibling[1]);
                    return node.sibling[1];
                }
                else if (node.sibling[0] != null && node.sibling[0].isEnough() == true) {
                    node.moveRightOneLeaf(node.sibling[0]);
                    return node;
                }
                else {
                    if(node.sibling[0] != null) {
                        node.mergeTwoLeafNode2(node.sibling[0]);
                        //return node.sibling[0];
                        return node;
                    } else if(node.sibling[1] != null) {
                        node.mergeTwoLeafNode(node.sibling[1]);
                        return node.sibling[1];
                    }
                }
            } else {
                return node;
            }
        }
        else {
            Node nextNode = node.findPath(key);
            Node nnode = delete(nextNode, key);
            if (nnode.isEmpty()) {
                node.deleteNode(nnode);
                if(node.isLack()) {  //0은왼쪽, 1은 오른쪽
                    if (node.sibling[1] != null && node.sibling[1].isEnough() == true) {
                        node.moveLeftOneNonLeaf(node.sibling[1]);
                        return node.sibling[1];
                    } else if (node.sibling[0] != null && node.sibling[0].isEnough() == true) {
                        node.moveRightOneNonLeaf(node.sibling[0]);
                        return node;
                    }
                    else {
                        if(node.sibling[0] != null) {
                            node.mergeTwoNonLeafNode2(node.sibling[0]);
                            //return node.sibling[0];
                            return node;
                        } else if(node.sibling[1] != null) {
                            node.mergeTwoNonLeafNode(node.sibling[1]);
                            return node.sibling[1]; //비는거리턴
                        }
                    }
                } else {
                    return node;
                }
            }
            else {
                return node;
            }
        }
        return null;
    }

    public void searchSingle(int key) {
        root.searchPath(key);
    }

    public void searchRange(int keyStart, int keyEnd) {
        root.rangeSearch(keyStart, keyEnd);
    }

    public static List<String[]> readCSV(String path) {
        List<String []> data = new ArrayList<>();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "euc-kr"));
            while ((line = br.readLine()) != null) {
                String[] field = line.split(cvsSplitBy);
                data.add(field);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    public static void irregularFormat(String args[]) {
        System.err.println("irregular command");
        System.err.println("args length : " + args.length);
        //print regular format
        for (int i = 0; i < args.length; i++) {
            System.out.format("args[%d] : %s%n",  i, args[i]);
        }
        System.exit(1);
    }


    public static void show(Node n, int j) {
        System.out.println("[ level :" + j + " ]");
        for(int i = 0; i < n.m; i++) {
            System.out.println(j + " : " + n.keys[i]);
        }
        if (n.isLeafNode == false) {
            for(int i = 0; i <= n.m; i++) {
                show(n.pointers[i], j + 1);
            }
        }
    }

    public static void main(String[] args) {

		/*create new data file*/
        if (args.length == 3 && args[0].equals("-c") ) {
            try {
                bptree bpt = new bptree(Integer.parseInt(args[2]));
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[1]));
                oos.writeObject(bpt);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		/*insert*/
        else if(args.length == 3 && args[0].equals("-i") ) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[1]));
                bptree bpt = (bptree)ois.readObject();
                ois.close();
                List<String []> list = readCSV(args[2]);
                bpt.insertList(list);

                //show(bpt.root, 0);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[1]));
                oos.writeObject(bpt);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		/*delete*/
        else if(args.length == 3 && args[0].equals("-d")) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[1]));
                bptree bpt = (bptree)ois.readObject();
                ois.close();
                List<String []> list = readCSV(args[2]);
                bpt.deleteList(list);

                //show(bpt.root, 0);

                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(args[1]));
                oos.writeObject(bpt);
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		/*single search*/
        else if(args.length == 3 && args[0].equals("-s")) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[1]));
                bptree bpt = (bptree)ois.readObject();
                ois.close();
                bpt.searchSingle(Integer.parseInt(args[2]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		/*range search*/
        else if(args.length == 4 && args[0].equals("-r")) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(args[1]));
                bptree bpt = (bptree)ois.readObject();
                ois.close();
                bpt.searchRange(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*another input : error */
        else {
            // irregularFormat
            irregularFormat(args);
        }

    }
}

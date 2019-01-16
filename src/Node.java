import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kimboyoung on 2017. 9. 10..
 */
public class Node implements Serializable {

    public boolean isLeafNode;
    public int [] keys;
    public int [] values;
    public Node [] pointers;
    public Node [] sibling;
    public Node rightSibling;
    public int nodeSize;
    public boolean keyFlag;
    public int upKey;
    public int m; //# of children

    // make initial leaf node
    public Node(int n) {

        isLeafNode = true;
        nodeSize = n;
        m = 0;

        keys = new int[nodeSize + 1];
        values = new int[nodeSize + 1];
        sibling = new Node[2];
        sibling[0] = null; //left
        sibling[1] = null;  //right
        rightSibling = null;
        keyFlag = false;
    }

    // make new leaf node with key and value
    public Node(int n, int [] inputKeys, int [] inputValues) {

        isLeafNode = true;
        nodeSize = n;
        m = inputKeys.length;

        keys = new int[nodeSize + 1];
        values = new int[nodeSize + 1];
        sibling = new Node[2];
        sibling[0] = null;
        sibling[1] = null;
        keyFlag = false;

        for(int i = 0; i < inputKeys.length; i++) {
            keys[i] = inputKeys[i];
            values[i] = inputValues[i];
        }
        rightSibling = null;
    }

    // make non-leaf node
    public Node(int n, int [] inputKeys, Node [] nodes) {

        isLeafNode = false;
        nodeSize = n;
        m = inputKeys.length;

        keys = new int[nodeSize + 1];
        pointers = new Node[nodeSize + 2];
        sibling = new Node[2];
        sibling[0] = null;
        sibling[1] = null;
        keyFlag = false;

        for(int i = 0; i < inputKeys.length; i++) {
            keys[i] = inputKeys[i];
        }
        for(int i = 0; i < nodes.length; i++) {
            pointers[i] = nodes[i];
        }
    }

    public void insertToNode(int key, int value) {  //leaf node
        int i;
        for(i = 0; i < m; i++) {
            if(keys[i] > key) {
                break;
            }
        }
        for(int j = m; j > i; j--) {
            keys[j] = keys[j - 1];
            values[j] = values[j - 1];
        }
        keys[i] = key;
        values[i] = value;
        m++;
    }

    public void insertToNode(Node nNode) {   //non leaf node
        int key = nNode.keys[0];
        int i;
        for(i = 0; i < m; i++) {
            if(keys[i] > key) {
                break;
            }
        }
        for(int j = m; j > i; j--) {
            keys[j] = keys[j - 1];
            pointers[j + 1] = pointers[j];
        }
        keys[i] = deepleftMostKey(nNode);
        pointers[i + 1] = nNode;
        m++;
    }

    public Node insertAndSplit(Node addNode) {
        insertToNode(addNode);
        int [] tempk = new int[(m - 1) / 2];
        Node [] tempN = new Node[((m - 1) / 2) + 1];

        for (int i = 0; i < (m - 1) / 2; i++) {
            tempk[i] = keys[m / 2 + 1 + i];
            tempN[i] = pointers[m / 2 + 1 + i];
        }
        tempN[(m - 1) / 2] = pointers[m];
        m = m / 2;

        Node nnode = new Node(nodeSize, tempk, tempN);

        nnode.sibling[0] = this;
        nnode.sibling[1] = sibling[1]; // can erase
        sibling[1] = nnode;

        return nnode;
    }

    public Node insertAndSplitRoot(Node addNode) {
        insertToNode(addNode);
        int [] tempk = new int[(m - 1) / 2];
        Node [] tempN = new Node[((m - 1) / 2) + 1];

        for (int i = 0; i < (m - 1) / 2; i++) {
            tempk[i] = keys[m / 2 + 1 + i];
            tempN[i] = pointers[m / 2 + 1 + i];
        }
        tempN[(m - 1) / 2] = pointers[m];
        m = m / 2;

        Node nnode = new Node(nodeSize, tempk, tempN);
       // System.out.println("nnode" + nnode.keys[0]);

        nnode.sibling[0] = this;
        nnode.sibling[1] = sibling[1]; // can erase
        sibling[1] = nnode;

        int [] lkey = new int [1];
        Node [] pointers = new Node [2];
        lkey[0] = keys[m];
        pointers[0] = this;
        pointers[1] = nnode;
        Node nNonLeafNode = new Node(nodeSize, lkey, pointers);

        return nNonLeafNode;
    }

    public Node splitNode (int key, int value) {

        int i;
        for (i = 0; i < m; i++) {
            if(keys[i] > key) {
                break;
            }
        }
        for (int j = nodeSize; j > i; j--) {
            keys[j] = keys[j - 1];
            values[j] = values[j - 1];
        }
        keys[i] = key;
        values[i] = value;
        m++;


        int[] tempk = new int[(nodeSize + 2) / 2];
        int[] tempv = new int[(nodeSize + 2) / 2];

        for (int j = 0; j < (nodeSize + 2) / 2; j++) {
            tempk[((nodeSize + 2) / 2) - j - 1] = keys[nodeSize - j];
            tempv[((nodeSize + 2) / 2) - j - 1] = values[nodeSize - j];
        }

        m =  ((nodeSize + 1) / 2);
        rightSibling = new Node(nodeSize, tempk, tempv);
        rightSibling.sibling[0] = this;
        rightSibling.sibling[1] = sibling[1]; // can erase
        sibling[1] = rightSibling;
        return rightSibling;
    }

    public Node findPath (int key) {
        for(int i = 0; i < m; i ++) {
            if(keys[i] > key) return pointers[i];
            if(keys[i] == key) return pointers[i + 1];
        }
        return pointers[m];
    }

    public int findPathInt (int key) {
        int i;
        for(i = 0; i < m; i ++) {
            if(keys[i] > key) return i;
            if(keys[i] == key) return i + 1;
        }
        return i;
    }

    public boolean isthereKeyInNode(int key) {
        for(int i = 0; i < m; i++)
            if(keys[i] == key) return true;
        return false;
    }

    public int leftMostKey () {
        return keys[0];
    }

    public int deleteInLeaf(int key) {
        if (keys[0] == key) {
            for(int i = 1; i < m; i++) {
                keys[i - 1] = keys[i];
                values[i - 1] = values[i];
            }
            m--;
            return keys[0];
        }
        for (int i = 1; i < m; i++) {
            if (keys[i] == key) {
                for(i = i + 1; i < m; i++) {
                    keys[i - 1] = keys[i];
                    values[i - 1] = keys[i];
                }
                m--;
                return keys[0];
            }
        }
        return keys[0];
    }

    public boolean isFull() {
        if(m == nodeSize) return true;
        else return false;
    }

    public boolean isLack() {
        if(m < nodeSize / 2) return true;
        else return false;
    }

    public boolean isEnough() {
        if(m > nodeSize / 2) return true;
        return false;
    }

    public void moveLeftOneLeaf(Node src) {
        keys[m] = src.keys[0];
        values[m] = src.values[0];
        m++;
        for(int i = 1; i < src.m; i++) {
            src.keys[i - 1] = src.keys[i];
            src.values[i - 1] = src.values[i];
        }
        src.m--;
    }

    public void moveRightOneLeaf(Node src) {
        for(int i = 1; i <= m; i++) {
            keys[i] = keys[i - 1];
            values[i] = values[i - 1];
        }
        m++;
        keys[0] = src.keys[src.m];
        values[0] = src.values[src.m];
        src.m--;
    }

    public void mergeTwoLeafNode(Node nnode) {
        for(int i = 0; i < nnode.m; i++) {
            keys[m + i] = nnode.keys[i];
            values[m + i] = nnode.values[i];
        }
        m += nnode.m;
        nnode.m = 0;
    }

    public void mergeTwoLeafNode2(Node nnode) {

        for(int i = 0; i < m; i++) {
            nnode.keys[i + nnode.m] = keys[i];
            nnode.values[i + nnode.m] = values[i];
        }
        /*
        for(int i = m - 1; i >= 0; i--) {
            keys[i + nnode.m] = keys[i];
            values[i + nnode.m] = values[i];
        }
        for(int i = 0; i < nnode.m; i++) {
            keys[i] = nnode.keys[i];
            values[i] = nnode.values[i];
        }
        m += nnode.m;
        nnode.m = 0;
        */
        nnode.m += m;
        m = 0;
    }

    public boolean isEmpty() {
        if(m == 0) return true;
        else return false;
    }

    public void checkAllKey() {
        checkKey(this);
    }

    public void checkKey(Node node) {
        if(node.isLeafNode == true) return;
        checkKey(node.pointers[0]);
        for(int i = 1; i <= node.m; i++) {
            node.keys[i - 1] = deepleftMostKey(node.pointers[i]);
            checkKey(node.pointers[i]);
        }
    }

    public int deepleftMostKey(Node node) {
        if(node.isLeafNode == true) return node.keys[0];
        else return deepleftMostKey(node.pointers[0]);
    }

    public void deleteNode(Node dnode) {
        if(dnode.equals(pointers[0])) {
            for(int j = 0; j < m - 1; j++) {
                pointers[j] = pointers[j + 1];
                keys[j] = keys[j + 1];
            }
            pointers[m - 1] = pointers[m];
            m--;
            return;
        }
        for(int i = 1; i <= m; i++) {
            if(dnode.equals(pointers[i])) {
                for(int j = i; j < m; j++) {
                    pointers[j] = pointers[j + 1];
                    keys[j - 1] = keys[j];
                }
                m--;
                return;
            }
        }
    }

    public void moveLeftOneNonLeaf(Node src) {
        keys[m] = deepleftMostKey(src.pointers[0]);
        pointers[m + 1] = src.pointers[0];
        m++;
        for(int i = 1; i < src.m; i++) {
            src.keys[i - 1] = src.keys[i];
            src.pointers[i - 1] = src.pointers[i];
        }
        src.pointers[src.m - 1] = src.pointers[src.m];
        src.pointers[src.m] = null;
        src.m--;
    }

    public void moveRightOneNonLeaf(Node src) {
        pointers[m + 1] = pointers[m];
        for(int i = m; i >= 1; i--) {
            keys[i] = keys[i - 1];
            pointers[i] = pointers[i - 1];
        }
        m++;
        keys[0] = deepleftMostKey(pointers[1]);
        pointers[0] = src.pointers[src.m];
        src.pointers[src.m] = null;
        src.m--;
    }

    public void mergeTwoNonLeafNode(Node nnode) {
        keys[m] = deepleftMostKey(nnode.pointers[0]);
        for(int i = 0; i < nnode.m; i++) {
            keys[m + i + 1] = nnode.keys[i];
            pointers[m + i + 1] = nnode.pointers[i];
        }
        pointers[m + nnode.m + 1] = nnode.pointers[nnode.m];
        m += (nnode.m + 1);
        nnode.m = 0;
    }

    public void mergeTwoNonLeafNode2(Node nnode) {

        for(int i = 0; i < m; i++) {
            nnode.keys[i + nnode.m + 1] = keys[i];
            nnode.pointers[i + nnode.m + 1] = pointers[i];
        }
        nnode.pointers[nnode.m + m + 1] = pointers[m];

        for(int i = 0; i <= m; i++) {
            pointers[i] = null;
        }

        nnode.m += (m + 1);
        m = 0;

        /*
        pointers[m + nnode.m + 1] = pointers[m];
        for(int i = m - 1; i >= 0; i--) {
            keys[i + nnode.m + 1] = keys[i];
            pointers[i + nnode.m + 1] = pointers[i];
        }
        for(int i = 0; i < nnode.m; i++) {
            keys[i] = nnode.keys[i];
            pointers[i] = nnode.pointers[i];
        }
        pointers[nnode.m] = nnode.pointers[nnode.m];
        keys[nnode.m] = deepleftMostKey(pointers[nnode.m + 1]);
        m += (nnode.m + 1);
        nnode.m = 0;
        */
    }
    public void searchPath(int key) {
        if(isLeafNode == true) {
            if(!isthereKeyInNode(key)) {
                System.out.println("NOT FOUND");
                return;
            }
            System.out.println(values[findPathInt(key) - 1]);
            return;
        }
        Node pathNode = findPath(key);

        System.out.print(keys[0]);
        for(int i = 1; i < m; i++) {
            System.out.print("," + keys[i]);
        }
        System.out.println();
        pathNode.searchPath(key);
    }

    public void rangeSearch(int keyStart,int keyEnd) {
        if(isLeafNode == true) {
            for(int i  = 0; i < m; i++) {
                if(keyStart <= keys[i] && keys[i] <= keyEnd) System.out.println(keys[i] + "," + values[i]);
            }
            return;
        }
        else {
            if(keyStart < keys[0]) pointers[0].rangeSearch(keyStart, keyEnd);
            for(int i = 1; i < m; i++) {
                if(keyStart < keys[i] || keys[i - 1] <= keyEnd) {
                    pointers[i].rangeSearch(keyStart, keyEnd);
                }
            }
            if(keys[m - 1] < keyEnd) pointers[m].rangeSearch(keyStart, keyEnd);
        }
    }
}

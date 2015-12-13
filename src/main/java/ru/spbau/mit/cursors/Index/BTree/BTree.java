package ru.spbau.mit.cursors.Index.BTree;

import java.io.IOException;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class BTree {
    private Node root;

    public BTree() throws IOException {
        // TODO - OK CONSTRUCTOR
        //create leafNode
        root = new LeafNode();
    }

    public BTree(int pageId) throws IOException {
        root = Node.getNode(pageId);
    }

    public Node getRoot(){
        return root;
    }

    public void insert(int key, LeafEntry e) throws IOException {
        LeafNode leaf = find(key);
        leaf.insertKey(key, e);
        if (leaf.isFull()) {
            Node tmp = leaf.resolveOversize();
            if (tmp != null) {
                root = tmp;
            }
        }
    }

    public void delete(int key) throws IOException {
        LeafNode leaf = find(key);
        if (leaf.delete(key) && leaf.isTooEmpty()) {
            Node tmp = leaf.resolveUnderflow();
            if (tmp != null) {
                root = tmp;
            }
        }
    }

    public LeafNode find(int key) throws IOException {
        Node node = root;
        while (!node.isLeaf()) {
            node = ((InnerNode) node).getChild(node.find(key));
        }
        return (LeafNode) node;
    }
}

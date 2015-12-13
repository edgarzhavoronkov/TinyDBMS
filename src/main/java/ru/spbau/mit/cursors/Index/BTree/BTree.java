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

    public void insert(int key, LeafEntry e) throws IOException {
        LeafNode leaf = getPotentialLeaf(key);
        leaf.insertKey(key, e);
        if (leaf.isFull()) {
            Node tmp = leaf.resolveOversize();
            if (tmp != null) {
                root = tmp;
            }
        }
    }

    public LeafNode find(int key) throws IOException {
        LeafNode leaf = getPotentialLeaf(key);
        int index = leaf.find(key);
        if (index == -1) {
            return null;
        }
        return leaf;
    }

    public void delete(int key) throws IOException {
        LeafNode leaf = getPotentialLeaf(key);
        if (leaf.delete(key) && leaf.isTooEmpty()) {
            Node tmp = leaf.resolveUnderflow();
            if (tmp != null) {
                root = tmp;
            }
        }
    }

    private LeafNode getPotentialLeaf(int key) throws IOException {
        Node node = root;
        while (!node.isLeaf()) {
            node = ((InnerNode) node).getChild(node.find(key));
        }
        return (LeafNode) node;
    }
}

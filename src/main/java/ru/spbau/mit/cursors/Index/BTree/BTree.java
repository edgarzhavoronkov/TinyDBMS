package ru.spbau.mit.cursors.Index.BTree;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public class BTree {
    private Node root;

    public BTree(){
        // TODO - OK CONSTRUCTOR
        root  = new LeafNode();
    }

    public void insert(int key, LeafEntry e){
        LeafNode leaf = getPotentialLeaf(key);
        leaf.insertKey(key, e);
        if(leaf.isFull()){
            Node tmp = leaf.resolveOversize();
            if(tmp != null){
                root = tmp;
            }
        }
    }

    public LeafNode find(int key){
        LeafNode leaf = getPotentialLeaf(key);
        int index = leaf.find(key);
        if(index == -1){
            return null;
        }
        return leaf;
    }

    public void delete(int key){
        LeafNode leaf = getPotentialLeaf(key);
        if(leaf.delete(key) && leaf.isTooEmpty()){
            Node tmp = leaf.resolveUnderflow();
            if(tmp != null){
                root = tmp;
            }
        }
    }

    private LeafNode getPotentialLeaf(int key){
        Node node = root;
        while (!node.isLeaf()){
            node = ((InnerNode) node).getChild(node.find(key));
        }
        return (LeafNode)node;
    }
}

package ru.spbau.mit.cursors.Index.BTree;

import com.sun.istack.internal.Nullable;
import ru.spbau.mit.memory.Page;

import java.util.ArrayList;

/**
 * Created by gellm_000 on 06.12.2015.
 */
abstract class Node {
    protected Integer[] keys;
    protected int size;
    protected Integer pageId;
    protected Integer leftNodePageId;
    protected Integer rightNodePageId;
    protected Integer parentNodePageId;

    protected Node(){
        size = 0;
        // TODO Allocate memmory for self
        pageId = null;
        leftNodePageId = null;
        parentNodePageId = null;
        rightNodePageId = null;
    }


    public int getSize(){
        return size;
    }

    public Integer getKeyAt(int index){
        return keys[index];
    }

    public Integer getPageId(){
        return pageId;
    }

    public void setKeyAt(int index, int value){
        keys[index] = value;
    }

    public Integer getParentNodePageID(){
        return parentNodePageId;
    }

    public Node getParentNode(){
        Integer id = getParentNodePageID();
        if(id == null){
            return null;
        }
        // TODO corrent instantiation of id
        Node parent = null;
        return parent;
    }

    public void setParentNodePageId(Integer id){
        parentNodePageId = id;
    }

    public void setParentNode(Node parent){
        setParentNodePageId(parent.getPageId());
    }
    @Nullable
    public Integer getLeftNodePageId(){
        if(leftNodePageId == null){
            return null;
        }
        // TODO correct instantiation getLeftNodePageId()
        Node leftNode = null;
        if(leftNode.getParentNodePageID() != getParentNodePageID()){
            return null;
        }
        return leftNodePageId;
    }

    public Node getLeftNode(){
        Integer id = getLeftNodePageId();
        if(id == null){
            return null;
        }
        //TODO correct instantiation of id
        Node left = null;
        return left;
    }

    public void setLeftNodePageId(Integer id){
        leftNodePageId = id;
    }

    public void setLeftNode(Node left){
        setLeftNodePageId(left.getPageId());
    }

    @Nullable
    public Integer getRightNodePageId(){
        if(rightNodePageId == null){
            return null;
        }
        // TODO correct instantiation getRightNodePageId()
        Node rightNode = null;
        if(rightNode.getParentNodePageID() != getParentNodePageID()){
            return null;
        }
        return rightNodePageId;
    }

    public Node getRightNode(){
        Integer id = getRightNodePageId();
        if(id == null){
            return null;
        }
        //TODO correct id instantiation
        Node right = null;
        return right;
    }

    public void setRightNodePageId(Integer id){
        rightNodePageId = id;
    }

    public void setRightNode(Node right){
        setRightNodePageId(right.getPageId());
    }

    public abstract boolean isLeaf();

    /**
     *
     * @param key
     * @return for
     */
    public abstract int find(int key);

    protected abstract Node split();

    protected abstract Node pushToParent(int key, Node leftChild, Node rightChild);

    public boolean isFull(){
        return getSize() == keys.length;
    }

    public Node resolveOversize(){
        int splitKey = getKeyAt(keys.length / 2);
        Node newRightNode = split();

        if(getParentNodePageID() == null){
            setParentNodePageId((new InnerNode()).pageId);
        }

        newRightNode.setParentNodePageId(getParentNodePageID());
        newRightNode.setRightNodePageId(getRightNodePageId());
        newRightNode.setLeftNodePageId(getPageId());
        if(getRightNodePageId() != null){
            // TODO factory instantiation
            Node oldRightNode = getRightNode();
            oldRightNode.setLeftNode(newRightNode);
        }
        setRightNode(newRightNode);

        Node parentNode = getParentNode();

        return parentNode.pushToParent(splitKey, this, newRightNode);
    }

    public boolean isTooEmpty(){
        return getSize() < keys.length/2;
    }

    public boolean canDonate(){
        return getSize() > keys.length/2;
    }

    protected abstract void transferChildren(Node receiver, Node donor, int donationIndex);
    protected abstract Node FuseChildren(Node leftChild, Node rightChild);
    protected abstract void FuseWithSibling(int separationKey, Node rightNode);
    protected abstract int getKeyFromSibling(int separationKey, Node sibling, int donationIndex);

    @Nullable
    public Node resolveUnderflow(){
        if(getParentNodePageID() == null){
            return null;
        }

        Node leftNode = getLeftNode();
        if(leftNode != null && leftNode.canDonate()){
            getParentNode().transferChildren(this, leftNode, leftNode.getSize()-1);
            return null;
        }

        Node rightNode = getRightNode();
        if(rightNode != null && rightNode.canDonate()){
            getParentNode().transferChildren(this, rightNode, 0);
            return null;
        }

        if(leftNode != null){
            return getParentNode().FuseChildren(leftNode, this);
        }else {
            return getParentNode().FuseChildren(this, rightNode);
        }
    }
}

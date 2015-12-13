package ru.spbau.mit.cursors.Index.BTree;



import ru.spbau.mit.QueryHandler;
import ru.spbau.mit.memory.page.*;

import java.io.IOException;

/**
 * Created by gellm_000 on 06.12.2015.
 */
public abstract class Node {
    protected NodePage nodePage;

    protected Node() {
//        nodePage = QueryHandler.bufferManager.getFirstNodeFreePage();
//        nodePage.setSize(0);
//        nodePage.setLeftNodePageId(null);
//        nodePage.setRightNodePageId(null);
//        nodePage.setParentNodePageId(null);
    }


    public static Node getNode(Integer pageId) throws IOException {
        NodePage nodePage = new NodePageImpl(QueryHandler.bufferManager.getPage(pageId));
        if (nodePage.isLeaf()) {
            LeafNodePage leafNodePage = new LeafNodePageImpl(QueryHandler.bufferManager.getPage(pageId));
            return new LeafNode(leafNodePage);
        } else {
            InnerNodePage innerNodePage = new InnerNodePageImpl(QueryHandler.bufferManager.getPage(pageId));
            return new InnerNode(innerNodePage);
        }
    }

    public int getSize() {
        return nodePage.getSize();
    }

    public void setSize(int size) {
        nodePage.setSize(size);
    }

    public Integer getKeyAt(int index) {
        return nodePage.getKeyAt(index);
    }

    public Integer getPageId() {
        return nodePage.getId();
    }

    public void setKeyAt(int index, Integer value) {
        nodePage.setKeyAt(index, value);
    }

    public Integer getParentNodePageID() {
        return nodePage.getParentNodePageId();
    }

    public Node getParentNode() throws IOException {
        if (getParentNodePageID() == null) {
            return null;
        }
        // TODO corrent instantiation of id
        return getNode(getParentNodePageID());
    }

    public void setParentNodePageId(Integer id) {
        nodePage.setParentNodePageId(id);
    }

    public void setParentNode(Node parent) {
        setParentNodePageId(parent.getPageId());
    }


    public Integer getLeftNodePageId() throws IOException {
        if (nodePage.getLeftNodePageId() == null) {
            return null;
        }
        // TODO correct instantiation getLeftNodePageId()
        Node leftNode = getNode(nodePage.getLeftNodePageId());
        if (!leftNode.getParentNodePageID().equals(getParentNodePageID())) {
            return null;
        }
        return nodePage.getLeftNodePageId();
    }


    public Node getLeftNode() throws IOException {
        if (getLeftNodePageId() == null) {
            return null;
        }
        //TODO correct instantiation of id
        return getNode(getLeftNodePageId());
    }

    public void setLeftNodePageId(Integer id) {
        nodePage.setLeftNodePageId(id);
    }

    public void setLeftNode(Node left) {
        setLeftNodePageId(left.getPageId());
    }


    public Integer getRightNodePageId() throws IOException {
        if (nodePage.getRightNodePageId() == null) {
            return null;
        }
        // TODO correct instantiation getRightNodePageId()
        Node rightNode = getNode(nodePage.getRightNodePageId());
        if (!rightNode.getParentNodePageID().equals(getParentNodePageID())) {
            return null;
        }
        return nodePage.getRightNodePageId();
    }


    public Node getRightNode() throws IOException {
        if (getRightNodePageId() == null) {
            return null;
        }
        //TODO correct id instantiation
        return getNode(getRightNodePageId());
    }

    public void setRightNodePageId(Integer id) {
        nodePage.setRightNodePageId(id);
    }

    public void setRightNode(Node right) {
        setRightNodePageId(right.getPageId());
    }

    public abstract boolean isLeaf();

    /**
     * @param key
     * @return for
     */
    public abstract int find(int key);

    protected abstract Node split() throws IOException;

    protected abstract Node pushToParent(int key, Node leftChild, Node rightChild) throws IOException;

    public boolean isFull() {
        return getSize() == NodePage.KEYS_CAPACITY;
    }

    public Node resolveOversize() throws IOException {
        int splitKey = getKeyAt(NodePage.KEYS_CAPACITY / 2);
        Node newRightNode = split();

        if (getParentNodePageID() == null) {
//            setParentNodePageId((new InnerNode()).pageId);
            setParentNodePageId(new InnerNode().getPageId());
        }

        newRightNode.setParentNodePageId(getParentNodePageID());
        newRightNode.setRightNodePageId(getRightNodePageId());
        newRightNode.setLeftNodePageId(getPageId());
        if (getRightNodePageId() != null) {
            // TODO factory instantiation
            Node oldRightNode = getRightNode();
            oldRightNode.setLeftNode(newRightNode);
        }
        setRightNode(newRightNode);

        Node parentNode = getParentNode();

        return parentNode.pushToParent(splitKey, this, newRightNode);
    }

    public boolean isTooEmpty() {
        return getSize() < NodePage.KEYS_CAPACITY / 2;
    }

    public boolean canDonate() {
        return getSize() > NodePage.KEYS_CAPACITY / 2;
    }

    protected abstract void transferChildren(Node receiver, Node donor, int donationIndex) throws IOException;

    protected abstract Node FuseChildren(Node leftChild, Node rightChild) throws IOException;

    protected abstract void FuseWithSibling(int separationKey, Node rightNode) throws IOException;

    protected abstract Integer getKeyFromSibling(int separationKey, Node sibling, int donationIndex) throws IOException;


    public Node resolveUnderflow() throws IOException {
        if (getParentNodePageID() == null) {
            return null;
        }

        Node leftNode = getLeftNode();
        if (leftNode != null && leftNode.canDonate()) {
            getParentNode().transferChildren(this, leftNode, leftNode.getSize() - 1);
            return null;
        }

        Node rightNode = getRightNode();
        if (rightNode != null && rightNode.canDonate()) {
            getParentNode().transferChildren(this, rightNode, 0);
            return null;
        }

        if (leftNode != null) {
            return getParentNode().FuseChildren(leftNode, this);
        } else {
            return getParentNode().FuseChildren(this, rightNode);
        }
    }

    protected void close() {
    }
}

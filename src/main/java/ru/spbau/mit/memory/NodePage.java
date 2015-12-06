package ru.spbau.mit.memory;

/**
 * Created by John on 12/6/2015.
 */
public interface NodePage extends BasePage {

    boolean isLeaf();

    void setIsLeaf(boolean isLeaf);

    int getSize();

    void setSize(int size);

    Integer getLeftNodePageId();

    void setLeftNodePageId(Integer leftNodePageId);

    Integer getRightNodePageId();

    void setRightNodePageId(Integer rightNodePageId);

    Integer getParentNodePageId();

    void setParentNodePageId(Integer parentNodePageId);

    int[] getKeys();

    void setKeys(int[] keys);
}

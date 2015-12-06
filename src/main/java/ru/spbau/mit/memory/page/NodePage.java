package ru.spbau.mit.memory.page;

/**
 * Created by John on 12/6/2015.
 */
public interface NodePage extends BasePage {
    int KEYS_CAPACITY = 100;

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

    Integer[] getKeys();

    void setKeys(Integer[] keys);
}

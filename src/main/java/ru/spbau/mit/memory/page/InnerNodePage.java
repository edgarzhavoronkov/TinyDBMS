package ru.spbau.mit.memory.page;


/**
 * Created by John on 12/13/2015.
 */
public interface InnerNodePage extends NodePage {
    int CHILDREN_CAPACITY = KEYS_CAPACITY + 1;

    Integer getChildrenAt(int index);

    void setChildrenAt(int index, Integer children);

}

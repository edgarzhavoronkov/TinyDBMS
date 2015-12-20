package ru.spbau.mit.cursors;

import ru.spbau.mit.cursors.Index.BTree.BTree;
import ru.spbau.mit.cursors.Index.BTree.LeafNode;
import ru.spbau.mit.cursors.Index.BTree.Node;
import ru.spbau.mit.memory.BufferManager;
import ru.spbau.mit.memory.Record;
import ru.spbau.mit.memory.page.RecordPage;
import ru.spbau.mit.memory.page.RecordPageImpl;
import ru.spbau.mit.meta.Column;
import ru.spbau.mit.meta.Table;

import java.io.IOException;

/**
 * Created by gellm_000 on 30.10.2015.
 */
public class TreeIndexCursor implements Cursor{
    private BTree bTree;
    private BufferManager bufferManager;
    private Table table;
    private LeafNode currentNode;
    private int position;
    private Record currentRecord;
    private Column indexColumn;
    private Integer rightKey;

    public TreeIndexCursor(BufferManager bufferManager, Table table, Column indexColumn, int leftKey, int rightKey) throws IOException {
        Integer indexPageId = table.getIndexRootPageIdForColumn(indexColumn);
        this.indexColumn = indexColumn;
        if(indexPageId == null){
            throw new UnsupportedOperationException("No index for column: " + indexColumn.getName());
        }
        bTree = new BTree(indexPageId);
        this.bufferManager = bufferManager;
        this.table = table;
        this.rightKey = rightKey;

        currentNode = bTree.find(leftKey);

        position = -1;
        for(int i = 0; i < currentNode.getSize(); i++){
            if(currentNode.getKeyAt(i) >= leftKey){
                position = i;

                break;
            }
        }
    }

    @Override
    public Record getCurrentRecord() {
        return currentRecord;
    }

    @Override
    public BufferManager getBufferManager() {
        return bufferManager;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public Cursor clone() {
        //todo implement
        try {
            return new TreeIndexCursor(bufferManager, table, indexColumn, currentNode.getKeyAt(position), rightKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public Integer getPageId() {
        return currentNode.getEntryAt(position).getPageId();
    }

    @Override
    public Integer getOffset() {
        return currentNode.getEntryAt(position).getOffset();
    }

    @Override
    public void initiateCursor(Integer pageId, Integer offset) throws IOException {}

    @Override
    public boolean hasNext() {
        boolean isEmpty = (position == -1);
        boolean isLast = (position == currentNode.getSize()) && (currentNode.getRightNodePageId() == null);
        boolean rightBounds = !isEmpty && currentNode.getKeyAt(position) > rightKey;
        return !(isEmpty || isLast || rightBounds);
    }

    @Override
    public Object next() {
        if(!hasNext()){
            return null;
        }
        try {
            RecordPage page = new RecordPageImpl(bufferManager.getPage(currentNode.getEntryAt(position).getPageId()), table);
            currentRecord = page.getRecordByAbsolutePosition(currentNode.getEntryAt(position).getOffset());
            position ++;
            if(position == currentNode.getSize() && (currentNode.getRightNode() != null)){
                currentNode = (LeafNode) Node.getNode(currentNode.getRightNodePageId());
                position = 0;
            }
            return currentRecord;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

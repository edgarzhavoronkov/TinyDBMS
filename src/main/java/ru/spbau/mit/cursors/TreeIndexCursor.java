package ru.spbau.mit.cursors;

import ru.spbau.mit.cursors.Index.BTree.BTree;
import ru.spbau.mit.cursors.Index.BTree.LeafNode;
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
    public TreeIndexCursor(BufferManager bufferManager, Table table, Column indexColumn,int leftKey) throws IOException {
        Integer indexPageId = table.getIndexRootPageIdForColumn(indexColumn);
        if(indexPageId == null){
            throw new UnsupportedOperationException("No index for column: " + indexColumn.getName());
        }
        bTree = new BTree(indexPageId);
        this.bufferManager = bufferManager;
        this.table = table;

        currentNode = bTree.find(leftKey);
        for(int i = 0; i < currentNode.getSize(); i++){
            if(currentNode.getKeyAt(i) >= leftKey){
                position = i;

                break;
            }
        }
        position = -1;
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
        try {
            boolean isLast = (position == currentNode.getSize()) && (currentNode.getRightNode() == null);
            return !(isEmpty || isLast);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
                currentNode = (LeafNode) currentNode.getRightNode();
                position = 0;
            }
            return currentRecord;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}

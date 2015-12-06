package ru.spbau.mit.memory;

import ru.spbau.mit.memory.page.*;
import ru.spbau.mit.meta.Table;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class for working with cache
 * <p>
 * Created by John on 9/12/2015.
 */
public class BufferManager {

    private final int CAPACITY = 100;

    private long operationId;

    private FileDataManager fileDataManager;

    /**
     * Store all buffered pages
     */
    private TreeSet<BasePage> pages;

    /**
     * Store all pinned pages
     */
    private TreeSet<BasePage> pinedPages;

    /**
     * Store map page id - page (for fast find in buf)
     */
    private Map<Integer, BasePage> pageMap;

    public BufferManager() throws IOException {
        this.pages = new TreeSet<>((o1, o2) -> (int) (o1.getLastOperationId() - o2.getLastOperationId()));
        pinedPages = new TreeSet<>((o1, o2) -> o1.getId() - o2.getId());
        pageMap = new HashMap<>();
        fileDataManager = new FileDataManager();
    }

    public RecordPage getRecordPage(Integer id, Table table) throws IOException {
        return new RecordPageImpl(getPage(id), table);
    }

    public NodePage getNodePage(Integer id) throws IOException {
        return new NodePageImpl(getPage(id));
    }

    private BasePage getPage(Integer id) throws IOException {
        if (pageMap.containsKey(id)) {
            BasePage page = pageMap.get(id);
            removePageFromBuffer(page);
            addPageToBuffer(page);
            return page;
        }
        //todo (low) remove useless byte[4096]
        BasePage page = new BasePageImpl(new byte[BasePage.SIZE], id);
        if (pinedPages.contains(page)) {
            return pinedPages.floor(page);
        }
        page = fileDataManager.getPageById(id);
        addPageToBuffer(page);
        return page;
    }

    private void removePageFromBuffer(BasePage page) {
        if (pages.contains(page)) {
            pages.remove(page);
            pageMap.remove(page.getId());
        }
    }

    private void addPageToBuffer(BasePage page) throws IOException {
        page.updateOperationId(operationId++);
        if (CAPACITY == (pages.size() + pinedPages.size())) {
            fileDataManager.savePage(pages.pollFirst());
        }
        pageMap.put(page.getId(), page);
        pages.add(page);
    }

    //???????? ???????? ??? ? ??????? ??????
    public void pinPage(BasePage page) throws Exception {
        page.pin();
        if (pages.remove(page)) {
            pinedPages.add(page);
        }
    }

    public void unPinned(BasePage page) throws Exception {
        assert (page.isPin());
        page.unpin();
        if (page.isPin() && !pinedPages.remove(page)) {
            throw new RuntimeException("Pinned exception ");
        }
        addPageToBuffer(page);
    }

    public void close() throws IOException {
        for (BasePage page : pages) {
            fileDataManager.savePage(page);
        }
        for (BasePage page : pinedPages) {
            fileDataManager.savePage(page);
        }
        fileDataManager.close();
    }

    public RecordPage getFirstRecordFreePage(Table table) throws IOException {
        return new RecordPageImpl(getFirstFreePage(), table);
    }

    public NodePage getFirstNodeFreePage(boolean isLeaf) throws IOException {
        if (isLeaf) {
            return new LeafNodePageImpl(getFirstFreePage());
        }
        //todo return InnerNode
        return new NodePageImpl(getFirstFreePage());
    }

    private BasePage getFirstFreePage() throws IOException {
        BasePage page = fileDataManager.getFirstFreePage();
        addPageToBuffer(page);
        return page;
    }
}

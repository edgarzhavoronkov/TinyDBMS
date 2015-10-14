package ru.spbau.mit.memory;

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
    private TreeSet<Page> pages;

    /**
     * Store all pinned pages
     */
    private TreeSet<Page> pinedPages;

    /**
     * Store map page id - page (for fast find in buf)
     */
    private Map<Integer, Page> pageMap;

    public BufferManager() throws IOException {
        this.pages = new TreeSet<>((o1, o2) -> (int) (o1.getLastOperationId() - o2.getLastOperationId()));
        pinedPages = new TreeSet<>((o1, o2) -> o1.getId() - o2.getId());
        pageMap = new HashMap<>();
        fileDataManager = new FileDataManager();
    }

    public Page getPage(Integer id, Table table) throws IOException {
        if (pageMap.containsKey(id)) {
            Page page = pageMap.get(id);
            removePageFromBuffer(page);
            addPageToBuffer(page);
            return page;
        }
        //todo (low) remove useless byte[4096]
        Page page = new PageImpl(new byte[Page.SIZE], id);
        if (pinedPages.contains(page)) {
            return pinedPages.floor(page);
        }
        page = fileDataManager.getPageById(id);
        page.setTable(table);
        addPageToBuffer(page);
        return page;
    }

    private void removePageFromBuffer(Page page) {
        if (pages.contains(page)) {
            pages.remove(page);
            pageMap.remove(page.getId());
        }
    }

    private void addPageToBuffer(Page page) throws IOException {
        page.updateOperationId(operationId++);
        if (CAPACITY == (pages.size() + pinedPages.size())) {
            fileDataManager.savePage(pages.pollFirst());
        }
        pageMap.put(page.getId(), page);
        pages.add(page);
    }

    //???????? ???????? ??? ? ??????? ??????
    public void pinPage(Page page) throws Exception {
        page.pin();
        if (pages.remove(page)) {
            pinedPages.add(page);
        }
    }

    public void unPinned(Page page) throws Exception {
        assert (page.isPin());
        page.unpin();
        if (page.isPin() && !pinedPages.remove(page)) {
            throw new RuntimeException("Pinned exception ");
        }
        addPageToBuffer(page);
    }

    public void close() throws IOException {
        for (Page page : pages) {
            fileDataManager.savePage(page);
        }
        for (Page page : pinedPages) {
            fileDataManager.savePage(page);
        }
        fileDataManager.close();
    }

    public Page getFirstFreePage() throws IOException {
        return fileDataManager.getFirstFreePage();
    }
}

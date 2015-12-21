package ru.spbau.mit.memory;

import ru.spbau.mit.memory.page.BasePage;
import ru.spbau.mit.memory.page.BasePageImpl;

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

    public BasePage getPage(Integer id) throws IOException {
        if (pageMap.containsKey(id) && !pinedPages.contains(new BasePageImpl(new byte[0], id))) {
            BasePage page = pageMap.get(id);
            removePageFromBuffer(page);
            addPageToBuffer(page);
            page.flush(); //save wrapper data
            return page;
        }
        //todo (low) remove useless byte[4096]
        BasePage page = new BasePageImpl(new byte[BasePage.SIZE], id);
        if (pinedPages.contains(page)) {
            BasePage floor = pinedPages.floor(page);
            floor.flush(); //save wrapper data
            return floor;
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
            BasePage pageToRemove = pages.pollFirst();
            pageMap.remove(pageToRemove.getId());
            fileDataManager.savePage(pageToRemove);
        }
        pageMap.put(page.getId(), page);
        pages.add(page);
    }

    public void pinPage(BasePage page) throws IOException {
        page.pin();
        if (pages.remove(page)) {
            pinedPages.add(page);
        }
    }

    public void unPinned(BasePage page) throws IOException {
        assert (page.isPin());
        page.unpin();
        if (!page.isPin() && !pinedPages.remove(page)) {
//            throw new RuntimeException("Pinned exception ");
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

    public BasePage getFirstFreePage() throws IOException {
        BasePage page = fileDataManager.getFirstFreePage();
        addPageToBuffer(page);
        return page;
    }
}

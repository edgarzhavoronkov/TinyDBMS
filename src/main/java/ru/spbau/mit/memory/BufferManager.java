package ru.spbau.mit.memory;

import ru.spbau.mit.DatabaseProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class for working with cache
 *
 * Created by John on 9/12/2015.
 */
public class BufferManager {

    private final int CAPACITY = 100;

    private long operationId;

    private FileDataManager fileDataManager;

    private TreeSet<Page> pages;

    private TreeSet<Page> pinedPages;


    public BufferManager(DatabaseProperties properties) {
        this.pages = new TreeSet<>((o1, o2) -> (int) (o1.getLastOperationId() - o2.getLastOperationId()));
        pinedPages = new TreeSet<>();
    }


    public Page getPage(Integer id) throws IOException {
        Page page = new PageImpl(id);
        if (pages.contains(page)) {
            page = pages.floor(page);
            removePageFromBuffer(page);
            addPageToBuffer(page);
            return page;
        }
        if (pinedPages.contains(page)) {
            return pinedPages.floor(page);
        }
        page = fileDataManager.getPageById(id);
        addPageToBuffer(page);
        return page;
    }

    private void removePageFromBuffer(Page page) {
        if (pages.contains(page)) {
            pages.remove(page);
        }
    }

    private void addPageToBuffer(Page page) throws IOException {
        page.updateOperationId(operationId++);
        if (CAPACITY == (pages.size() + pinedPages.size())){
            fileDataManager.savePage(pages.pollFirst());
        }
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
        assert (!page.isPin());
        page.unpin();
        if (!page.isPin() && !pinedPages.remove(page)){
            //todo add Exception
            throw new Exception();
        }
        addPageToBuffer(page);
    }
}

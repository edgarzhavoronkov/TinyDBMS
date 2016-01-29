package ru.spbau.mit.memory;

import ru.spbau.mit.PropertiesManager;
import ru.spbau.mit.memory.page.BasePage;
import ru.spbau.mit.memory.page.BasePageImpl;
import ru.spbau.mit.memory.page.RecordPage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by John on 9/12/2015.
 */
public class FileDataManager {
    public static final Integer INIT_PAGE_COUNT = 10;

    private RandomAccessFile file;
    private Long pageCount;
    private int firstFreePage;

    private static int pageGetCount;

    public static int getPageGetCount() {
        return pageGetCount;
    }

    public static void resetGetCount() {
        pageGetCount = 0;
    }

    public FileDataManager() throws IOException {

        String dirPath = PropertiesManager.getProperties().getProperty("dir_path");
        String dataFileName = PropertiesManager.getProperties().getProperty("data_file_name");
        new File(dirPath).mkdirs();
        File file = new File(dirPath + "/" + dataFileName);

        boolean isNewFile = false;
        if (!file.exists()) {
            isNewFile = true;
        }
        this.file = new RandomAccessFile(file, "rw");
        if (isNewFile) {
            initNewDataFile();
        } else {
            firstFreePage = Integer.parseInt(PropertiesManager.getProperties().getProperty("first_free_page"));
        }

        pageCount = this.file.length() / BasePage.SIZE;
        assert (pageCount > firstFreePage);
    }

    private void initNewDataFile() throws IOException {
        firstFreePage = 0;
        file.setLength((long) INIT_PAGE_COUNT * RecordPage.SIZE);
    }

    private void takeToPageStart(BasePage page) throws IOException {
        file.seek((long) page.getId() * BasePage.SIZE);
    }


    public BasePage getPageById(Integer id) throws IOException {
        assert (id < pageCount);

        pageGetCount++;

        byte[] data = new byte[BasePage.SIZE];
        BasePage page = new BasePageImpl(data, id);
        takeToPageStart(page);
        file.readFully(data, 0, BasePage.SIZE);

        return page;
    }

    public BasePage getFirstFreePage() throws IOException {
        BasePage result = getPageById(firstFreePage);
        firstFreePage++;
        if (firstFreePage >= pageCount) {
            append();
        }
        return result;
    }

    public void savePage(BasePage page) throws IOException {
        assert (page.getId() < pageCount);
        if (!page.isDirty()) return;
        page.flush();
        takeToPageStart(page);
        file.write(page.getData(), 0, BasePage.SIZE);
    }

    private void append() throws IOException {
        file.setLength(file.length() + INIT_PAGE_COUNT * BasePage.SIZE);
        pageCount += INIT_PAGE_COUNT;
    }

    public void close() throws IOException {
        PropertiesManager.getProperties().setProperty("first_free_page", String.valueOf(firstFreePage));
        file.close();
    }
}

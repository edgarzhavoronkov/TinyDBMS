package ru.spbau.mit.memory;

import ru.spbau.mit.PropertiesManager;

import java.io.File;
import java.io.FileNotFoundException;
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
        if (isNewFile){
            initNewDataFile();
        } else {
            firstFreePage = Integer.parseInt(PropertiesManager.getProperties().getProperty("first_free_page"));
            assert (pageCount > firstFreePage);
        }

        pageCount = this.file.length() / Page.SIZE;
    }

    private void initNewDataFile() throws IOException {
        firstFreePage = 0;
        file.setLength((long) INIT_PAGE_COUNT * Page.SIZE);
    }

    private void takeToPageStart(Page page) throws IOException {
        file.seek((long) page.getId() * Page.SIZE);
    }


    public Page getPageById(Integer id) throws IOException {
        assert (id < pageCount);

        byte[] data = new byte[Page.SIZE];
        Page page = new PageImpl(data, id);
        takeToPageStart(page);
        file.readFully(data, 0, Page.SIZE);

        return page;
    }

    public Page getFirstFreePage() throws IOException {
        Page result = getPageById(firstFreePage);
        firstFreePage++;
        if (firstFreePage >= pageCount) {
            append();
        }
        return result;
    }

    public void savePage(Page page) throws IOException {
        assert (page.getId() < pageCount);
        if (!page.isDirty()) return;
        takeToPageStart(page);
        file.write(page.getData(), 0, Page.SIZE);
    }

    private void append() throws IOException {
        file.setLength(file.length() + INIT_PAGE_COUNT * Page.SIZE);
        pageCount += INIT_PAGE_COUNT;
    }

    public void onQuit() throws IOException {
        PropertiesManager.getProperties().setProperty("first_free_page", String.valueOf(firstFreePage));
        file.close();
    }
}

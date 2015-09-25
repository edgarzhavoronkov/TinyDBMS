package ru.spbau.mit.memory;

import ru.spbau.mit.DatabaseProperties;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by John on 9/12/2015.
 */
public class FileDataManager {
    public static final String DEFAULT_FILE_NAME = "data.tdb";
    public static final Integer INIT_PAGE_COUNT = 10;

    private RandomAccessFile file;
    private Long pageCount;

    public FileDataManager(DatabaseProperties databaseProperties) throws IOException {
        File file = new File(databaseProperties.getDirPath() + "\\" + databaseProperties.getFileName());

        boolean isNewFile = false;
        if (!file.exists()) {
            isNewFile = true;
        }
        this.file = new RandomAccessFile(file, "rw");
        if (isNewFile){
            initNewDataFile();
        }

        pageCount = this.file.length() / Page.SIZE;
    }

    private void initNewDataFile() throws IOException {
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

    public void savePage(Page page) throws IOException {
        assert (page.getId() < pageCount);
        if (!page.isDirty()) return;
        takeToPageStart(page);
        file.write(page.getData(), 0, Page.SIZE);
    }

    public void onQuit() throws IOException {
        file.close();
    }

}

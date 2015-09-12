package ru.spbau.mit;

import ru.spbau.mit.memory.FileDataManager;

import java.util.InvalidPropertiesFormatException;

/**
 * Class for storage settings
 * Expected format is argName=argValue
 *
 * Created by John on 9/12/2015.
 */
public class DatabaseProperties {

    private static final String DIR = "dir";
    private static final String FILE = "file";
    private static final String CACHE = "cache";

    private static final int MIN_CACHE_SIZE = 10;

    private String dirPath;
    private String fileName;
    private Integer cacheSize;

    private DatabaseProperties() {
    }

    public static DatabaseProperties setOptions(String[] args) throws InvalidPropertiesFormatException {
        DatabaseProperties databaseProperties = new DatabaseProperties();
        for (String arg1 : args) {
            String[] arg = arg1.split("=");

            if (arg.length != 2) {
                throw new InvalidPropertiesFormatException("Invalid option format! " + arg1);
            }

            String argName = arg[0];
            String argValue = arg[1];

            switch (argName) {
                case DIR:
                    databaseProperties.setDirPath(argValue);
                    break;
                case FILE:
                    databaseProperties.setFileName(argValue);
                    break;
                case CACHE:
                    try {
                        int cacheSize = Integer.parseInt(argValue);
                        if (cacheSize < MIN_CACHE_SIZE) throw new IllegalArgumentException();
                        databaseProperties.setCacheSize(cacheSize);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Cache size must be number >= " + MIN_CACHE_SIZE + "! But " + argValue, e);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected arg! " + arg1);
            }
        }
        setDefaultOptions(databaseProperties);
        return databaseProperties;
    }

    private static void setDefaultOptions(DatabaseProperties databaseProperties) {
        if (databaseProperties.getDirPath() == null) databaseProperties.setDirPath(System.getProperty("user.dir"));
        if (databaseProperties.getCacheSize() == null) databaseProperties.setCacheSize(MIN_CACHE_SIZE);
        if (databaseProperties.getFileName() == null) databaseProperties.setFileName(FileDataManager.DEFAULT_FILE_NAME);
    }

    public String getDirPath() {
        return dirPath;
    }

    private void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getFileName() {
        return fileName;
    }

    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getCacheSize() {
        return cacheSize;
    }

    private void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }
}

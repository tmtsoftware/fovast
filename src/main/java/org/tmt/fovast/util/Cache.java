/*
 *  Copyright 2011 TMT.
 * 
 *  License and source copyright header text to be decided
 *  
 */
package org.tmt.fovast.util;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements a simple cache for downloaded items. Maps URLs to a relative path
 * in cache.
 *
 * Note: This class does not bother about the HTTP cache directives
 * 
 * @author vivekananda_moosani
 */
public class Cache {

    private Logger logger = LoggerFactory.getLogger(Cache.class);

    private static int DEFAULT_MAX_ENTRIES = 20;

    private final File downloadCacheFile;

    private final File downloadCacheDir;

    private LinkedHashMap<String, String> index = new LinkedHashMap<String, String>();

    private final int maxEntries;

    /**
     * Cache will store max 20 (DEFAULT_MAX_ENTRIES) files.
     * 
     * @param downloadCacheFile - directory where images are cached
     * @param downloadCacheDir - file in which URL/cache-id to file mapping is stored
     */
    public Cache(File downloadCacheFile, File downloadCacheDir) {
        this(downloadCacheFile, downloadCacheDir, DEFAULT_MAX_ENTRIES);
    }

    /**
     *
     * @param downloadCacheFile - directory where images are cached
     * @param downloadCacheDir - file in which URL/cache-id to file mapping is stored
     * @param maxEntries - number of files to store in cache
     */
    public Cache(File downloadCacheFile, File downloadCacheDir, int maxEntries) {
        this.downloadCacheFile = downloadCacheFile;
        this.downloadCacheDir = downloadCacheDir;
        this.maxEntries = maxEntries;

        if (!downloadCacheDir.exists()) {
            downloadCacheDir.mkdir();
        }

        //load index file into map object
        load();
    }

    /**
     * Save the content of the URL to cache directory
     * Note: This class does not bother about the HTTP cache directives
     * 
     * @param url - URL to download file from and cache
     * @param sl - SaveListener object
     * @return
     */
    public File save(URL url, SaveListener sl) throws Exception {
        //TODO: should we go for random unique names ?
        String relativePath = "file" + System.currentTimeMillis();
        File fileToStore = new File(downloadCacheDir, relativePath);
        long bytes = 0;
        long lastBytes = 0;
        byte[] bytea = new byte[2 * 1024 * 1024]; //2MB cache
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            URLConnection conn = url.openConnection();
            bos = new BufferedOutputStream(new FileOutputStream(fileToStore));
            bis = new BufferedInputStream(conn.getInputStream());
            int read = 0;
            while ((read = bis.read(bytea)) != -1) {
                bos.write(bytea, 0, read);
                bytes += read;
                //call listener every 10 KB
                if (sl != null && (bytes - lastBytes) > (10 * 1024)) {
                    lastBytes = bytes;
                    try {
                        sl.bytesRead(bytes);
                    } catch (Exception ex) {
                        logger.warn("Listener threw an exception", ex);
                    }
                }
            }
            bos.flush();
            index.put(url.toString(), relativePath);
            checkSize();
            return fileToStore;
        } catch (Exception ex) {
            logger.warn("Exception while saving the file to cache", ex);
            throw ex;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception ex) {
                    logger.warn("Exception while closeing input stream from " + url.toString(),
                            ex);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception ex) {
                    logger.warn("Exception while closing output stream to " + fileToStore.toString(),
                            ex);
                }
            }
        }
    }

    /**
     * Retrives the cached file of the URL.
     * 
     * @param url
     * @return returns File object of the cached file .. returns null if no entry
     * is present in cache for the given URL
     */
    public File getFile(URL url) {
        String relativePath = index.get(url.toString());
        if (relativePath != null) {
            return new File(downloadCacheDir, relativePath);
        } else {
            return null;
        }
    }

    /**
     * Remove the entry from the cache for the given URL
     *
     * @param url
     * @return
     */
    public void remove(URL url) {
        remove(url.toString());
    }

    /**
     * Saves the map of cache entries to disk
     */
    public void save() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(downloadCacheFile);
            Iterator<String> ite = index.keySet().iterator();
            while (ite.hasNext()) {
                String key = ite.next();
                writer.println(key);
                writer.println(index.get(key));
            }
        } catch (Exception ex) {
            // donot do any thing
            logger.warn("Exception while saving cache index", ex);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * loads index map from cache index file
     * 
     */
    public void load() {
        if (downloadCacheFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(downloadCacheFile));
                String prevLine = null;
                String line = null;
                boolean value = false;
                while ((line = reader.readLine()) != null) {
                    if (value) {
                        index.put(prevLine, line);
                        value = false;
                    } else {
                        value = true;
                    }
                    prevLine = line;
                }
            } catch (Exception ex) {
                // donot do any thing
                logger.warn("Exception while saving cache index", ex);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        logger.warn("Error while closing cache file reader", ex);
                    }
                }
            }
        }

        cleanup();
    }

    /**
     * Cleanup files which are not present in index
     * 
     */
    private void cleanup() {
        try {
            File[] children = downloadCacheDir.listFiles();
            Collection<String> values = index.values();
            for (int i = 0; i < children.length; i++) {
                //relative path (+1 below is to count for the separator)
                String relativePath = getRelativePath(children[i]);
                if (!values.contains(relativePath)) {
                    children[i].delete();
                }
            }
        } catch (Exception ex) {
            logger.warn("Error while cleanup. It is recommended to manuall delete the cache dir contents");
        }
    }

    private String getRelativePath(File file) throws IOException {
        String childPath = file.getCanonicalPath();
        return childPath.substring(downloadCacheDir.getCanonicalPath().length() + 1);
    }

    /**
     * maintains the num entries below max entries
     */
    private void checkSize() {
        if (index.size() > 20) {
            Iterator<String> ite = index.keySet().iterator();
            String key = ite.next();
            remove(key);
        }
    }

    /**
     * Deletes the given entry from cache and corresponding file in cache dir
     * 
     * @param string
     */
    private void remove(String string) {
        String relativePath = index.get(string);
        if (relativePath != null) {
            File file = new File(downloadCacheDir, relativePath);
            if (file.exists()) {
                file.delete();
            }
            index.remove(string);
        }

    }

    /** 
     * Listener object to track bytes downloaded
     */
    public static interface SaveListener {

        public void bytesRead(long bytes);

    }
}

package org.jbake.app.crawler;

import org.jbake.app.ContentStore;
import org.jbake.app.Crawler;
import org.jbake.app.FileUtil;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.model.DocumentTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

public class AssetCrawler extends Crawler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetCrawler.class);

    public AssetCrawler(ContentStore db, JBakeConfiguration config) {
        super(db, config);
    }

    @Override
    public void crawl() {
        crawl(config.getContentFolder());

        LOGGER.info("Content detected:");
        for (String docType : DocumentTypes.getDocumentTypes()) {
            long count = db.getDocumentCount(docType);
            if (count > 0) {
                LOGGER.info("Parsed {} files of type: {}", count, docType);
            }
        }
    }

    @Override
    protected FileFilter getFileFilter() {
        return FileUtil.getNotContentFileFilter();
    }

    @Override
    protected Map<String, Object> processFile(File sourceFile) {
        Map<String, Object>
    }

    protected String[] getDocumentTypes() {
        return new String[]{"asset"};
    }
}

package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.Crawler.Attributes;
import org.jbake.app.DocumentList;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.model.DocumentTypes;
import org.jbake.template.RenderingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class DocumentsRenderer implements RenderingTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentsRenderer.class);

    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
        int renderedCount = 0;
        final long start = new Date().getTime();
        final List<String> errors = new LinkedList<>();
        for (String docType : DocumentTypes.getDocumentTypes()) {
            DocumentList documentList = db.getUnrenderedContent(docType);

            if (documentList == null) continue;

            int index = 0;

            Map<String, Object> nextDocument = null;

            while (index < documentList.size()) {
                try {
                    Map<String, Object> document = documentList.get(index);
                    document.put("nextContent", null);
                    document.put("previousContent", null);

                    if (index > 0) {
                        document.put("nextContent", getContentForNav(nextDocument));
                    }

                    if (index < documentList.size() - 1) {
                        Map<String, Object> tempNext = documentList.get(index + 1);
                        document.put("previousContent", getContentForNav(tempNext));
                    }

                    nextDocument = document;

                    renderer.render(document);
                    renderedCount++;

                } catch (Exception e) {
                    errors.add(e.getMessage());
                }

                index++;
            }

            db.markContentAsRendered(docType);
        }
        final long end = new Date().getTime();
        LOGGER.info("Document rendering took {}ms", end - start);
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to render documents. Cause(s):");
            for (String error : errors) {
                sb.append("\n").append(error);
            }
            throw new RenderingException(sb.toString());
        } else {
            return renderedCount;
        }
    }

    /**
     * Creates a simple content model to use in individual post navigations.
     *
     * @param document
     * @return
     */
    private Map<String, Object> getContentForNav(Map<String, Object> document) {
        Map<String, Object> navDocument = new HashMap<>();
        navDocument.put(Attributes.NO_EXTENSION_URI, document.get(Attributes.NO_EXTENSION_URI));
        navDocument.put(Attributes.URI, document.get(Attributes.URI));
        navDocument.put(Attributes.TITLE, document.get(Attributes.TITLE));
        return navDocument;
    }

    @Override
    public int render(Renderer renderer, ContentStore db, File destination, File templatesPath, CompositeConfiguration config) throws RenderingException {
        return render(renderer, db, null);
    }
}
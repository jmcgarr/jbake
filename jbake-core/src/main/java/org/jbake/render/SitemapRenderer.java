package org.jbake.render;

import org.apache.commons.configuration.CompositeConfiguration;
import org.jbake.app.ContentStore;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.app.configuration.JBakeConfigurationFactory;
import org.jbake.template.RenderingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;


public class SitemapRenderer implements RenderingTool {
    private static final Logger LOGGER = LoggerFactory.getLogger(SitemapRenderer.class);


    @Override
    public int render(Renderer renderer, ContentStore db, JBakeConfiguration config) throws RenderingException {
        if (config.getRenderSiteMap()) {
            try {
                //TODO: refactor this. the renderer has a reference to the configuration
                final long start = new Date().getTime();
                renderer.renderSitemap(config.getSiteMapFileName());
                final long end = new Date().getTime();
                LOGGER.info("Sitemap rendering took {}ms", end - start);
                return 1;
            } catch (Exception e) {
                throw new RenderingException(e);
            }
        } else {
            return 0;
        }
    }

    @Override
    public int render(Renderer renderer, ContentStore db, File destination, File templatesPath, CompositeConfiguration config) throws RenderingException {
        JBakeConfiguration configuration = new JBakeConfigurationFactory().createDefaultJbakeConfiguration(templatesPath.getParentFile(), config);
        return render(renderer, db, configuration);
    }

}
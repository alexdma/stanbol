package org.apache.stanbol.commons.solr;

import java.io.File;

import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.osgi.framework.Constants;

/**
 * Defines the keys used to register {@link SolrCore}s as OSGI services
 * @author Rupert Westenthaler
 *
 */
public final class SolrConstants {

    private SolrConstants(){/*do not create instances*/}
    /**
     * Used as prefix for all {@link CoreContainer} related properties
     */
    private static final String PROPERTY_SOLR_SERVER = CoreContainer.class.getName();
    /**
     * Used as prefix for all {@link SolrCore} related properties
     */
    private static final String PROPERTY_SOLR_CORE = SolrCore.class.getName();
    /**
     * Property used for the human readable name of a SolrServer. This will be used
     * as alternative to the absolute file path of the solr.xml file used for the
     * initialisation of the solr server
     */
    public static final String PROPERTY_SERVER_NAME = PROPERTY_SOLR_SERVER+".name";
    /**
     * The directory of the SolrServer. Values are expected to be {@link File} 
     * objects with <code>{@link File#isDirectory()}==true</code> or {@link String}
     * values containing a file path. The {@link File#getAbsolutePath()} will be
     * used to initialise the SolrServer.
     */
    public static final String PROPERTY_SERVER_DIR = PROPERTY_SOLR_SERVER+".dir";
    /**
     * the name of the solr.xml file defining the configuration for the Solr
     * Server. If not defined {@link #SOLR_XML_NAME} is used as default<p>
     */
    public static final String PROPERTY_SOLR_XML_NAME = PROPERTY_SOLR_SERVER+".solrXml";
    /**
     * The registered {@link SolrCore} names for this server. Values are expected
     * to be a read only collection of names.
     */
    public static final String PROPERTY_SERVER_CORES = PROPERTY_SOLR_SERVER+".cores";
    /**
     * The {@link Constants#SERVICE_RANKING service ranking} of the Solr server.
     * If not defined that '0' is used as default.<p>
     * Values are expected to be Integers. This Property uses 
     * {@link Constants#SERVICE_RANKING} as key.
     */
    public static final String PROPERTY_SERVER_RANKING = Constants.SERVICE_RANKING;
    /**
     * Property used for the name of a solr core. This is typically set by the
     * {@link ManagedSolrServer} implementation based on the name of the 
     * cores registered with a SolrServer.
     */
    public static final String PROPERTY_CORE_NAME = PROPERTY_SOLR_CORE+".name";
    /**
     * The directory of this core. This needs to be set if the
     * core is not located within a sub-directory within the
     * {@link #PROPERTY_SERVER_DIR} with the name {@link #PROPERTY_CORE_NAME}.
     */
    public static final String PROPERTY_CORE_DIR = PROPERTY_SOLR_CORE+".dir";
    /**
     * The data directory of a core. Set by the {@link ManagedSolrServer} when
     * registering a SolrCore based on {@link SolrCore#getDataDir()}
     */
    public static final String PROPERTY_CORE_DATA_DIR = PROPERTY_SOLR_CORE+".dadadir";
    /**
     * The index directory of a core. Set by the {@link ManagedSolrServer} when
     * registering a SolrCore based on {@link SolrCore#getIndexDir()}
     */
    public static final String PROPERTY_CORE_INDEX_DIR = PROPERTY_SOLR_CORE+".indexdir";
    /**
     * The name of the "schema.xml" file defining the solr schema for this core.
     * If not defined {@link #SOLR_SCHEMA_NAME} is used as default.
     */
    public static final String PROPERTY_CORE_SCHEMA = PROPERTY_SOLR_CORE+".schema";
    /**
     * The name of the "solrconf.xml" file defining the configuration for this
     * core. If not defined {@link #SOLR_SCHEMA_NAME} is used as default.
     */
    public static final String PROPERTY_CORE_SOLR_CONF = PROPERTY_SOLR_CORE+".solrconf";
    /**
     * The {@link Constants#SERVICE_RANKING service ranking} of the SolrCore. 
     * The ranking of the SolrServer is used as default if not defined. If also no 
     * ServiceRanking is defined for the server than '0' is used.<p>
     * Values are expected to be Integers. This Property uses 
     * {@link Constants#SERVICE_RANKING} as key.
     */
    public static final String PROPERTY_CORE_RANKING = Constants.SERVICE_RANKING;
    
    /**
     * Default name of the solr.xml file needed for the initialisation of a
     * {@link CoreContainer Solr server} 
     */
    public static final String SOLR_XML_NAME = "solr.xml";
    /**
     * default name of the Solrconfig.xml file needed for the initialisation of 
     * a {@link SolrCore}
     */
    public static final String SOLR_CONFIG_NAME = "solrconfig.xml";
    /**
     * Defualt name of the schema.xml file needed for the initialisation of 
     * a {@link SolrCore}
     */
    public static final String SOLR_SCHEMA_NAME = "schema.xml";

    
}

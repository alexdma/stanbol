package org.apache.stanbol.ontologymanager.ontonet.api.io;

import org.apache.clerezza.rdf.core.Graph;
import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.access.TcManager;

/**
 * An {@link OntologyInputSource} that gets ontologies from either a Clerezza {@link Graph} (or {@link MGraph}
 * ), or its identifier and an optionally supplied triple collection manager.
 * 
 * @author alexdma
 * 
 */
public class GraphSource extends AbstractClerezzaGraphInputSource {

    public GraphSource(UriRef graphId) {
        this(graphId, TcManager.getInstance());
    }

    public GraphSource(TripleCollection graph) {
        if (graph instanceof Graph) bindRootOntology((Graph) graph);
        else if (graph instanceof MGraph) bindRootOntology(((MGraph) graph).getGraph());
        else throw new IllegalArgumentException("GraphSource supports only Graph and MGraph types. "
                                                + graph.getClass() + " is not supported.");
        try {
            // TODO how can I know the physical iri?
            // bindPhysicalIri(rootOntology.getOntologyID().getDefaultDocumentIRI());
        } catch (Exception e) {
            // Ontology might be anonymous, no physical IRI then...
            bindPhysicalIri(null);
        }
    }

    /**
     * This constructor can be used to hijack ontologies using a physical IRI other than their default one.
     * 
     * @param rootOntology
     * @param phyicalIRI
     */
    public GraphSource(UriRef graphId, TcManager tcManager) {
        this(tcManager.getTriples(graphId));
    }

    @Override
    public String toString() {
        return "GRAPH<" + rootOntology + ">";
    }

}

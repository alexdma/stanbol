/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.stanbol.ontologymanager.ontonet.impl.io;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.TripleCollection;
import org.apache.clerezza.rdf.core.UriRef;
import org.apache.clerezza.rdf.core.access.EntityAlreadyExistsException;
import org.apache.clerezza.rdf.core.access.TcManager;
import org.apache.clerezza.rdf.core.access.WeightedTcProvider;
import org.apache.clerezza.rdf.core.impl.SimpleGraph;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.clerezza.rdf.core.sparql.ParseException;
import org.apache.clerezza.rdf.core.sparql.QueryParser;
import org.apache.clerezza.rdf.core.sparql.query.Query;
import org.apache.stanbol.ontologymanager.ontonet.impl.ontology.NoSuchStoreException;
import org.apache.stanbol.owl.transformation.JenaToClerezzaConverter;
import org.apache.stanbol.owl.transformation.JenaToOwlConvert;
import org.apache.stanbol.owl.transformation.OWLAPIToClerezzaConverter;
import org.apache.stanbol.owl.util.OWLUtils;
import org.osgi.service.component.ComponentContext;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class ClerezzaOntologyStorage {

    private static Logger log = LoggerFactory.getLogger(ClerezzaOntologyStorage.class);

    public static final String URI = "http://ontologydesignpatterns.org/ont/iks/oxml.owl";

    TcManager tcManager;

    WeightedTcProvider weightedTcProvider;

    /**
     * This default constructor is <b>only</b> intended to be used by the OSGI environment with Service
     * Component Runtime support.
     * <p>
     * DO NOT USE to manually create instances - the ClerezzaStorage instances do need to be configured! YOU
     * NEED TO USE {@link #ClerezzaStorage(TcManager, WeightedTcProvider, OntologyStoreProvider)} or its
     * overloads, to parse the configuration and then initialise the rule store if running outside a OSGI
     * environment.
     */
    protected ClerezzaOntologyStorage() {

    }

    /**
     * Basic constructor to be used if outside of an OSGi environment. Invokes default constructor.
     * 
     * @param tcManager
     * @param wtcProvider
     * @param osProvider
     */
    public ClerezzaOntologyStorage(TcManager tcManager, WeightedTcProvider wtcProvider) {
        this();
        this.tcManager = tcManager;
        this.weightedTcProvider = wtcProvider;
        activate(new Hashtable<String,Object>());
    }

    @SuppressWarnings("unchecked")
    protected void activate(ComponentContext context) {
        log.info("in " + ClerezzaOntologyStorage.class + " activate with context " + context);
        if (context == null) {
            throw new IllegalStateException("No valid" + ComponentContext.class + " parsed in activate!");
        }
        activate((Dictionary<String,Object>) context.getProperties());
    }

    protected void activate(Dictionary<String,Object> configuration) {

    }

    public void clear() {
        // TODO Auto-generated method stub
    }

    protected void deactivate(ComponentContext context) {
        log.info("in " + ClerezzaOntologyStorage.class + " deactivate with context " + context);
        tcManager = null;
        weightedTcProvider = null;
    }

    public void delete(IRI ontologyId) {
        // TODO Auto-generated method stub
    }

    public void deleteAll(Set<IRI> ontologyIds) {
        // TODO Auto-generated method stub
    }

    public MGraph getGraph(UriRef graphId) throws NoSuchStoreException {
        if (tcManager != null) return tcManager.getMGraph(graphId);
        else throw new NoSuchStoreException("No store registered or activated in the environment.");
    }

    public OWLOntology getGraph(IRI ontologyID) throws NoSuchStoreException {
        MGraph mGraph = getGraph(new UriRef(ontologyID.toString()));
        OWLOntology ontology = null;
        JenaToOwlConvert jowl = new JenaToOwlConvert();
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM, FileManager.get()
                .loadModel(URI));
        ontModel.add(JenaToClerezzaConverter.clerezzaMGraphToJenaModel(mGraph));
        ontology = jowl.ModelJenaToOwlConvert(ontModel, "RDF/XML");
        // ontology =
        // OWLAPIToClerezzaConverter.clerezzaMGraphToOWLOntology(mGraph);
        return ontology;
    }

    public Set<IRI> listGraphs() {

        Set<IRI> iris = null;
        Set<UriRef> uriRefs = tcManager.listTripleCollections();
        if (uriRefs != null) {
            iris = new HashSet<IRI>();
            for (UriRef uriRef : uriRefs)
                iris.add(IRI.create(uriRef.toString()));
        }
        return iris;

    }

    public OWLOntology load(IRI ontologyId) {
        MGraph triples = TcManager.getInstance().getMGraph(new UriRef(ontologyId.toString()));
        Model om = JenaToClerezzaConverter.clerezzaMGraphToJenaModel(triples);
        JenaToOwlConvert converter = new JenaToOwlConvert();
        return converter.ModelJenaToOwlConvert(om, "RDF/XML");
    }

    public OWLOntology sparqlConstruct(String sparql, String datasetURI) {

        Query query;
        MGraph mGraph = new SimpleMGraph();
        try {
            query = QueryParser.getInstance().parse(sparql);
            UriRef datasetUriRef = new UriRef(datasetURI);
            MGraph dataset = weightedTcProvider.getMGraph(datasetUriRef);
            mGraph.addAll((SimpleGraph) tcManager.executeSparqlQuery(query, dataset));
        } catch (ParseException e) {
            log.error("Unable to execute SPARQL. ", e);
        }

        Model om = JenaToClerezzaConverter.clerezzaMGraphToJenaModel(mGraph);
        JenaToOwlConvert converter = new JenaToOwlConvert();

        return converter.ModelJenaToOwlConvert(om, "RDF/XML");
    }

    public void store(OWLOntology o) {
        // // Why was it using two converters earlier?
        // JenaToOwlConvert converter = new JenaToOwlConvert();
        // OntModel om = converter.ModelOwlToJenaConvert(o, "RDF/XML");
        // MGraph mg = JenaToClerezzaConverter.jenaModelToClerezzaMGraph(om);
        TripleCollection mg = OWLAPIToClerezzaConverter.owlOntologyToClerezzaMGraph(o);
        MGraph mg2 = null;
        IRI iri = OWLUtils.guessOntologyIdentifier(o);
        UriRef ref = new UriRef(iri.toString());
        try {
            mg2 = tcManager.createMGraph(ref);
        } catch (EntityAlreadyExistsException ex) {
            log.info("Entity " + ref + " already exists in store. Replacing...");
            mg2 = tcManager.getMGraph(ref);
        }

        mg2.addAll(mg);
    }

    public void store(OWLOntology o, IRI ontologyID) {

        JenaToOwlConvert converter = new JenaToOwlConvert();
        OntModel om = converter.ModelOwlToJenaConvert(o, "RDF/XML");
        MGraph mg = JenaToClerezzaConverter.jenaModelToClerezzaMGraph(om);
        // MGraph mg = OWLAPIToClerezzaConverter.owlOntologyToClerezzaMGraph(o);
        MGraph mg2 = tcManager.createMGraph(new UriRef(ontologyID.toString()));
        mg2.addAll(mg);
    }
}

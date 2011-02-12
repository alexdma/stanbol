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
package org.apache.stanbol.entityhub.servicesapi.yard;

import org.apache.stanbol.entityhub.servicesapi.Entityhub;
import org.apache.stanbol.entityhub.servicesapi.model.EntityMapping;
import org.apache.stanbol.entityhub.servicesapi.model.Representation;
import org.apache.stanbol.entityhub.servicesapi.model.Symbol;
import org.apache.stanbol.entityhub.servicesapi.model.ValueFactory;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQuery;
import org.apache.stanbol.entityhub.servicesapi.query.FieldQueryFactory;
import org.apache.stanbol.entityhub.servicesapi.query.QueryResultList;
import org.apache.stanbol.entityhub.servicesapi.site.ReferencedSite;
import org.apache.stanbol.entityhub.servicesapi.site.ReferencedSiteManager;


/**
 * <p>The Yard represents a local cache for {@link Representation}s of the Entities
 * and Symbols managed by a referenced site.<p>
 *
 * <p> Referenced Sites need to provide the configuration if there representations
 * should be cached locally. This is done by using one of the defined {@link CacheStrategy}.
 *
 * <p>The Idea is not to have one big Yard that caches all the representations, but
 * to provide the possibility to use different caches. This means, that each
 * {@link ReferencedSite} can have its own Yard instance. However
 * several Sites might also use the same Yard.</p>
 * <p>The {@link YardManager} is an singleton services that manages the different
 * Yard instances and provides an central point of access for the {@link Entityhub}
 * and the {@link ReferencedSiteManager}.</p>
 *
 * <p> This should also allow for implementing Yards that are based on
 * <ul>
 * <li> Indexes generated by dumps of the referred Site</li>
 * <li> local installations of the software used by the referred site</li>
 * <li> and so on ... </li>
 * </ul>
 *
 * TODO: The framework need to provide a special Yard for storing {@link Symbol}
 * and {@link EntityMapping} information. This Yard is currently referenced as
 * Entityhub-Yard. Do we need also a special API for this Yard?
 * One could still provide a "default" implementation that implements this
 * interface based on a Component that provides the normal {@link Yard} service.
 * <p/>
 * <p>Side note: The name yard was chosen as name because in farming "yard" 
 * refers to a piece of enclosed land for farm animals or other agricultural 
 * purpose and the storage component of the entityhub does the same thing for
 * Entities. <p/>
 *
 * @author Rupert Westenthaler
 */
public interface Yard {

    String ID = "org.apache.stanbol.entityhub.yard.id";
    String NAME = "org.apache.stanbol.entityhub.yard.name";
    String DESCRIPTION = "org.apache.stanbol.entityhub.yard.description";

    /**
     * Getter for the unique ID of the Yard
     *
     * @return the id
     */
    String getId();

    String getName();

    String getDescription();

    /**
     * Creates a new empty representation and stores it in the Yard. 
     * The Yard is responsible to assign a valid ID.
     *
     * @return the created Representation initialised with a valid ID
     * @throws YardException On any error while creating or storing the 
     * representation
     */
    Representation create() throws YardException;

    /**
     * Creates a new representation for the given id
     *
     * @param id the id for the new representation or <code>null</code> to
     * indicate that the Yard should assign an id.
     * @return the created Representation
     * @throws IllegalArgumentException if the parsed id is not valid (especially
     * if an empty string is parsed as ID) or there
     * exists already a representation with the parsed id.
     * @throws YardException On any error while creating or storing the 
     * representation
     */
    Representation create(String id) throws IllegalArgumentException, YardException;

    /**
     * Stores the representation in the Yard. if the parsed representation is
     * not present in the Yard it will add it to the Yard.
     *
     * @param representation the representation
     * @return the representation as stored
     * @throws NullPointerException if <code>null</code> is parsed as argument.
     * @throws YardException On any error related to the Yard
     */
    Representation store(Representation representation) throws NullPointerException, YardException;

    /**
     * Stores all the parsed representation in a single chunk in the Yard. This
     * can improve performance, because it does not require multiple commits.<br>
     * <code>null</code> values are ignored and added as <code>null</code> in
     * the returned Iterable.
     * Otherwise same as {@link #store(Representation)}.
     *
     * @param representations all the representations to store. <code>null</code>
     * values are ignored and MUST NOT throw any exceptions. Parsing an {@link Iterable}
     * without any element will have none effect but also MUST NOT throw an exception.
     * @return the stored representations in the same iteration order
     * @throws NullPointerException if <code>null</code> is parsed as Iterable,
     * but NOT if the parsed Iterable does not contain any Elements or the
     * <code>null</code> value.
     * @throws YardException On any error related to the Yard
     */
    Iterable<Representation> store(Iterable<Representation> representations) throws NullPointerException, YardException;

    /**
     * Removes the {@link Representation} with the given id
     *
     * @param id the id
     * @throws IllegalArgumentException if the parsed ID is null or not valid
     * formatted
     * @throws YardException On any error related to the Yard
     */
    void remove(String id) throws IllegalArgumentException, YardException;

    /**
     * Removes all the {@link Representation} of the parsed ids. <code>null</code>
     * values are ignored.
     *
     * @param ids the iterable over the ids to remove
     * @throws IllegalArgumentException if <code>null</code> is parsed as iterable
     * @throws YardException On any error related to the Yard
     */
    void remove(Iterable<String> ids) throws IllegalArgumentException, YardException;

    /**
     * checks if a representation with the given id is present in the Yard
     *
     * @param id the id. Calls with <code>null</code> are ignored
     * @return <code>true</code> if a representation with the id is present in
     *         the Yard. Otherwise <code>false</code>.
     * @throws NullPointerException if <code>null</code> is parsed as ID
     * @throws IllegalArgumentException if the parsed ID is not valid
     * formatted (especially if an empty String is parsed as ID
     * @throws YardException On any error related to the Yard
     */
    boolean isRepresentation(String id) throws YardException, IllegalArgumentException;

    /**
     * Getter for the representation based on the id. Calls with
     * <code>id = null</code> should return null.
     *
     * @param id the id.
     * @return The representation with the parsed id or <code>null</code> if
     *         no representation with this id is present in the Yard
     * @throws NullPointerException if <code>null</code> is parsed as id
     * @throws IllegalArgumentException if the parsed ID is not valid formatted
     * @throws YardException On any error related to the Yard
     */
    Representation getRepresentation(String id) throws YardException, IllegalArgumentException;

    /**
     * Updates the store with the new state of the parsed representation
     *
     * @param represnetation the representation
     * @return the representation as stored
     * @throws NullPointerException If <code>null</code> is parsed as representation
     * @throws IllegalArgumentException if the parsed representation is not present 
     * in the Yard (and can not be updated therefore). TODO: evaluate if this should
     * really throw only a {@link RuntimeException}.
     * @throws YardException On any error related to the Yard
     */
    Representation update(Representation represnetation) throws YardException, IllegalArgumentException;

    /**
     * Updates the store with the new state of the parsed representations. This
     * can improve performance, because it does not require multiple commits.<br>
     * <code>null</code> values are ignored and added as <code>null</code> in
     * the returned Iterable. Otherwise same as {@link #update(Representation)}.
     *
     * @param representations the representations to update
     * @return the updated stored representations in the same iteration order
     * @throws YardException On any error related to the Yard
     * @throws IllegalArgumentException if <code>null</code> is parsed as Iterable
     */
    Iterable<Representation> update(Iterable<Representation> representations) throws YardException, IllegalArgumentException;

    /**
     * Finds all representations base on the parse query. Selected fields of the
     * query may be ignored, because only the ids of the found representations
     * are returned
     *
     * @param query The query
     * @return A ResultList containing the IDs of the found representations
     * @throws IllegalArgumentException if <code>null</code> is parsed as Query
     * @throws YardException On any error related to the Yard
     */
    QueryResultList<String> findReferences(FieldQuery query) throws YardException, IllegalArgumentException;

    /**
     * Getter for a view onto the Representations selected by the Query. Note
     * that the returned Representations will only include fields that are
     * selected by the parsed query. And such fields will only contain values
     * that are not filtered by the query.
     *
     * @param query the query
     * @return the view onto selected representations as defined by the query
     * @throws IllegalArgumentException if <code>null</code> is parsed as Query
     * @throws YardException On any error related to the Yard
     */
    QueryResultList<Representation> find(FieldQuery query) throws YardException, IllegalArgumentException;

    /**
     * Searches for all the Representation fulfilling the constraints of the
     * query and returns the (whole) representation as stored in the Yard.
     * Note that any as selected fields of the parsed query are ignored. Use
     * {@link #find(FieldQuery)} to retrieve representations that only contain
     * values for fields marked as selected by the parsed query.
     *
     * @param query the Query used to select representations in the Yard
     * @return the selected representations as stored in this yard.
     * @throws IllegalArgumentException if <code>null</code> is parsed as Query
     * @throws YardException On any error related to the Yard
     */
    QueryResultList<Representation> findRepresentation(FieldQuery query) throws YardException, IllegalArgumentException;

    /**
     * Getter for the FieldQueryFactory used by this Yard
     *
     * @return the FieldQueryFactory
     */
    FieldQueryFactory getQueryFactory();

    /**
     * Getter for the {@link ValueFactory} instance used by this Yard to create
     * {@link Representation}, {@link Reference} and {@link Text} instances.
     *
     * @return the valueFactory used by the store
     */
    ValueFactory getValueFactory();
/*
 * TODO: Maybe one would like to add status Information about cached
 *       Representations for registered Sites.
 */
// enum ProcessingStatus {
//        scheduled,
//        processing,
//        stored
//    };
//    ProcessingStatus getStatus(Site site);
//    ProcessingStatus getStatus(String entityId);
}

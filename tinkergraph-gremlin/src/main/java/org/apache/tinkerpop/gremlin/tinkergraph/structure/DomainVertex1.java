/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.tinkergraph.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.*;

public class DomainVertex1 extends SpecialisedTinkerVertex {
    public static String label = DomainVertex1.class.getSimpleName();

    public static String STRING_A = "stringA";
    public static String STRING_B = "stringB";
    public static String INTEGER_A = "integerA";
    public static String INTEGER_B = "integerB";
    public static Set<String> specificKeys = new HashSet<>(Arrays.asList(STRING_A, STRING_B, INTEGER_A, INTEGER_B));

    // properties
    private final String stringA;
    private final String stringB;
    private final Integer integerA;
    private final Integer integerB;

    // edges
    private Set<DomainEdge1> domainEdges1Out;
    private Set<DomainEdge1> domainEdges1In;

    public DomainVertex1(Object id, TinkerGraph graph, String stringA, String stringB, Integer integerA, Integer integerB) {
        super(id, DomainVertex1.label, graph, specificKeys);

        this.stringA = stringA;
        this.stringB = stringB;
        this.integerA = integerA;
        this.integerB = integerB;
    }

    /* note: usage of `==` (pointer comparison) over `.equals` (String content comparison) is intentional for performance - use the statically defined strings */
    @Override
    protected <V> V specificProperty(String key) {
        if (key == STRING_A) {
            return (V) stringA;
        } else if (key == STRING_B) {
            return (V) stringB;
        } else if (key == INTEGER_A) {
            return (V) integerA;
        } else if (key == INTEGER_B) {
            return (V) integerB;
        } else {
            throw new NoSuchElementException(key);
        }
    }

    /* note: usage of `==` (pointer comparison) over `.equals` (String content comparison) is intentional for performance - use the statically defined strings */
    @Override
    protected Iterator<Edge> specificEdges(Direction direction, String... edgeLabels) {
        List<Iterator<?>> iterators = new LinkedList<>();
        for (String label : edgeLabels) {
            if (label == DomainEdge1.label) {
                if (direction == Direction.IN || direction == Direction.BOTH) {
                    iterators.add(getDomainEdges1In().iterator());
                }
                if (direction == Direction.OUT || direction == Direction.BOTH) {
                    iterators.add(getDomainEdges1Out().iterator());
                }
            }
        }

        Iterator<Edge>[] iteratorsArray = iterators.toArray(new Iterator[iterators.size()]);
        return IteratorUtils.concat(iteratorsArray);
    }

    @Override
    protected Iterator<Vertex> specificVertices(Direction direction, String... edgeLabels) {
        Iterator<Edge> edges = specificEdges(direction, edgeLabels);
        if (direction == Direction.IN) {
            return IteratorUtils.map(edges, Edge::outVertex);
        } else if (direction == Direction.OUT) {
            return IteratorUtils.map(edges, Edge::inVertex);
        } else if (direction == Direction.BOTH) {
            return IteratorUtils.flatMap(edges, Edge::bothVertices);
        } else {
            return Collections.emptyIterator();
        }
    }

    @Override
    protected void addSpecialisedOutEdge(Edge edge) {
        if (edge instanceof DomainEdge1) {
            getDomainEdges1Out().add((DomainEdge1) edge);
        } else {
            throw new IllegalArgumentException("edge type " + edge.getClass() + " not supported");
        }
    }

    @Override
    protected void addSpecialisedInEdge(Edge edge) {
        if (edge instanceof DomainEdge1) {
            getDomainEdges1In().add((DomainEdge1) edge);
        } else {
            throw new IllegalArgumentException("edge type " + edge.getClass() + " not supported");
        }
    }

    private Set<DomainEdge1> getDomainEdges1Out() {
        if (domainEdges1Out == null) {
            domainEdges1Out = new HashSet<>();
        }
        return domainEdges1Out;
    }
    
    private Set<DomainEdge1> getDomainEdges1In() {
        if (domainEdges1In == null) {
            domainEdges1In = new HashSet<>();
        }
        return domainEdges1In;
    }
}

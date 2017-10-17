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

import org.apache.commons.lang3.NotImplementedException;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;

import java.util.*;

public abstract class SpecialisedTinkerVertex extends TinkerVertex {

    private final Set<String> specificKeys;

    protected SpecialisedTinkerVertex(Object id, String label, TinkerGraph graph, Set<String> specificKeys) {
        super(id, label, graph);
        this.specificKeys = specificKeys;
    }

    @Override
    public Set<String> keys() {
        return specificKeys;
    }

    @Override
    public <V> VertexProperty<V> property(String key) {
        // TODO cache instantiated TinkerVertexProperties
        return new TinkerVertexProperty<V>(this, key, specificProperty(key));
    }

    protected abstract <V> V specificProperty(String key);

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        if (propertyKeys.length == 0) {
            return (Iterator) specificKeys.stream().map(key -> property(key)).iterator();
        } else {
            Set<String> keys = new HashSet<>(Arrays.asList(propertyKeys));
            keys.retainAll(specificKeys);
            return (Iterator) keys.stream().map(key -> property(key)).iterator();
        }
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        throw new NotImplementedException("doesn't (yet) support mutation");
    }

    @Override
    public Edge addEdge(String label, Vertex vertex, Object... keyValues) {
        if (null == vertex) throw Graph.Exceptions.argumentCanNotBeNull("inVertex");
        if (this.removed) throw elementAlreadyRemoved(Vertex.class, this.id);

        // TODO: replace with some register of factories for specialised edges
        if (DomainEdge1.label.equals(label)) {
            Object idValue = graph.edgeIdManager.convert(ElementHelper.getIdValue(keyValues).orElse(null));
            if (null != idValue) {
                if (graph.edges.containsKey(idValue))
                    throw Graph.Exceptions.edgeWithIdAlreadyExists(idValue);
            } else {
                idValue = graph.edgeIdManager.getNextId(graph);
            }

            final TinkerVertex inVertex = (TinkerVertex) vertex;
            final TinkerVertex outVertex = this;

            ElementHelper.legalPropertyKeyValueArray(keyValues);
            Map<String, Object> keyValueMap = ElementHelper.asMap(keyValues);
            System.out.println("special handling for DomainEdge1 with " + keyValueMap);
            String stringZ = (String) keyValueMap.get("stringZ");
            Integer integerZ = (Integer) keyValueMap.get("integerZ");

            final Edge edge = new DomainEdge1(idValue, outVertex, label, inVertex, stringZ, integerZ);
            graph.edges.put(edge.id(), edge);
            // TODO: replace with specific version as well: no more hashMaps for outEdges/inEdges
            TinkerHelper.addOutEdge(outVertex, label, edge);
            TinkerHelper.addInEdge(inVertex, label, edge);
            return edge;
        } else {
            return super.addEdge(label, vertex, keyValues);
        }
    }
}

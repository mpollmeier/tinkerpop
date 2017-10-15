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

import com.koloboke.collect.map.hash.HashObjObjMaps;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.apache.tinkerpop.gremlin.structure.util.ElementHelper;
import org.apache.tinkerpop.gremlin.structure.util.StringFactory;
import org.apache.tinkerpop.gremlin.tinkergraph.process.computer.TinkerGraphComputerView;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TinkerVertexProperty<V> extends TinkerElement implements VertexProperty<V> {

    private final TinkerVertex vertex;
    private final String key;
    private final V value;

    /**
     * This constructor will not validate the ID type against the {@link Graph}.  It will always just use a
     * {@code Long} for its identifier.  This is useful for constructing a {@link VertexProperty} for usage
     * with {@link TinkerGraphComputerView}.
     */
    public TinkerVertexProperty(final TinkerVertex vertex, final String key, final V value, final Object... propertyKeyValues) {
        super(((TinkerGraph) vertex.graph()).vertexPropertyIdManager.getNextId((TinkerGraph) vertex.graph()), key);
        this.vertex = vertex;
        this.key = key;
        this.value = value;
        ElementHelper.legalPropertyKeyValueArray(propertyKeyValues);
        ElementHelper.attachProperties(this, propertyKeyValues);
    }

    /**
     * Use this constructor to construct {@link VertexProperty} instances for {@link TinkerGraph} where the {@code id}
     * can be explicitly set and validated against the expected data type.
     */
    public TinkerVertexProperty(final Object id, final TinkerVertex vertex, final String key, final V value, final Object... propertyKeyValues) {
        super(id, key);
        this.vertex = vertex;
        this.key = key;
        this.value = value;
        ElementHelper.legalPropertyKeyValueArray(propertyKeyValues);
        ElementHelper.attachProperties(this, propertyKeyValues);
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public V value() {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public String toString() {
        return StringFactory.propertyString(this);
    }

    @Override
    public Object id() {
        return this.id;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }

    @Override
    public Set<String> keys() {
        return Collections.emptySet();
    }

    @Override
    public <U> Property<U> property(final String key) {
        return Property.empty();
    }

    @Override
    public <U> Property<U> property(final String key, final U value) {
        throw new NotImplementedException("made TinkerVertexProperty non-recursive...");
    }

    @Override
    public Vertex element() {
        return this.vertex;
    }

    @Override
    public void remove() {
        if (null != this.vertex.properties && this.vertex.properties.containsKey(this.key)) {
            this.vertex.properties.remove(this.key);
            TinkerHelper.removeIndex(this.vertex, this.key, this.value);
            final AtomicBoolean delete = new AtomicBoolean(true);
            this.vertex.properties(this.key).forEachRemaining(property -> {
                if (property.value().equals(this.value))
                    delete.set(false);
            });
            if (delete.get()) TinkerHelper.removeIndex(this.vertex, this.key, this.value);
            this.removed = true;
        }
    }

    @Override
    public <U> Iterator<Property<U>> properties(final String... propertyKeys) {
        return Collections.emptyIterator();
    }
}

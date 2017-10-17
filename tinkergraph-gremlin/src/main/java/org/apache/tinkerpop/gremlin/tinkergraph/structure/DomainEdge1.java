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

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class DomainEdge1 extends SpecialisedTinkerEdge {
    public static String label = DomainEdge1.class.getSimpleName();

    public static String STRING_Z = "stringZ";
    public static String INTEGER_Z = "integerZ";
    public static Set<String> specificKeys = new HashSet<>(Arrays.asList(STRING_Z,INTEGER_Z));

    private final String stringZ;
    private final Integer integerZ;

    public DomainEdge1(Object id, Vertex outVertex, String label, Vertex inVertex, String stringZ, Integer integerZ) {
        super(id, outVertex, label, inVertex, specificKeys);
        this.stringZ = stringZ;
        this.integerZ = integerZ;
    }

    @Override
    protected <V> V specificProperty(String key) {
        // note: usage of `==` (pointer comparison) over `.equals` (String content comparison) is intentional for performance - use the statically defined strings
        if (key == STRING_Z) {
            return (V) stringZ;
        } else if (key == INTEGER_Z) {
            return (V) integerZ;
        } else {
            throw new NoSuchElementException(key);
        }
    }
}

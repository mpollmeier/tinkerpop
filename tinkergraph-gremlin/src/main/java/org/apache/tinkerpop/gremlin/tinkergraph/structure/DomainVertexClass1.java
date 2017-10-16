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

import java.util.*;

public class DomainVertexClass1 extends SpecialisedTinkerVertex {
    public static String STRING_A = "stringA";
    public static String STRING_B = "stringB";
    public static String INTEGER_A = "integerA";
    public static String INTEGER_B = "integerB";
    public static Set<String> specificKeys = new HashSet<>(Arrays.asList(STRING_A, STRING_B, INTEGER_A, INTEGER_B));
    public static String label = DomainVertexClass1.class.getSimpleName();
    
    private final String stringA;
    private final String stringB;
    private final Integer integerA;
    private final Integer integerB;

    public DomainVertexClass1(Object id, TinkerGraph graph, String stringA, String stringB, Integer integerA, Integer integerB) {
        super(id, DomainVertexClass1.label, graph, specificKeys);

        this.stringA = stringA;
        this.stringB = stringB;
        this.integerA = integerA;
        this.integerB = integerB;
    }

    @Override
    protected <V> V specificProperty(String key) {
        // note: usage of `==` (pointer comparison) over `.equals` (String content comparison) is intentional for performance - use the statically defined strings
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
}

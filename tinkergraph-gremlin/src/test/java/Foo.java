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
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.DomainEdge1;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.DomainVertex1;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;

public class Foo {

    public static void main(String[] args) {
        TinkerGraph graph = TinkerGraph.open();

        Vertex from = graph.addVertex(
            T.label, DomainVertex1.label,
            "stringA", "fromStringA",
            "stringB", "fromStringB",
            "integerA", 1,
            "integerB", 2
        );

        Vertex to = graph.addVertex(
            T.label, DomainVertex1.label,
            "stringA", "toStringA",
            "stringB", "toStringB",
            "integerA", 3,
            "integerB", 4
        );

        from.addEdge(
            DomainEdge1.label,
            to,
            "stringZ", "edgeStringZ", "integerZ", 99);

        GraphTraversalSource g = graph.traversal();
//        g.V().valueMap().toList().forEach(v -> System.out.println(v));
//        g.E().valueMap().toList().forEach(v -> System.out.println(v));

        System.out.println(g.V(from.id()).outE(DomainEdge1.label).toList());
        System.out.println(g.V(from.id()).outE(DomainEdge1.label).inV().toList());
        System.out.println(g.V(from.id()).out(DomainEdge1.label).toList());

        System.out.println(g.V(from.id()).bothE(DomainEdge1.label).toList());
        System.out.println(g.V(from.id()).both(DomainEdge1.label).toList());
    }
}

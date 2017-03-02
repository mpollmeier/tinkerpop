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
package org.apache.tinkerpop.gremlin.process.traversal.step.util;

import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.process.traversal.step.TraversalParent;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public class ParametersTest {
    @Test
    public void shouldGetKeyValuesEmpty() {
        final Parameters parameters = new Parameters();
        assertThat(Arrays.equals(parameters.getKeyValues(mock(Traverser.Admin.class)), new Object[0]), is(true));
    }

    @Test
    public void shouldGetKeyValues() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        final Object[] params = parameters.getKeyValues(mock(Traverser.Admin.class));
        assertEquals(6, params.length);
        assertThat(Arrays.equals(new Object[] {"a", "axe", "b", "bat", "c", "cat"}, params), is(true));
    }

    @Test
    public void shouldGetKeyValuesIgnoringSomeKeys() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        final Object[] params = parameters.getKeyValues(mock(Traverser.Admin.class), "b");
        assertEquals(4, params.length);
        assertThat(Arrays.equals(new Object[] {"a", "axe", "c", "cat"}, params), is(true));
    }

    @Test
    public void shouldGetUsingTraverserOverload() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        assertEquals(Collections.singletonList("axe"), parameters.get(mock(Traverser.Admin.class), "a", () -> "x"));
        assertEquals(Collections.singletonList("bat"), parameters.get(mock(Traverser.Admin.class), "b", () -> "x"));
        assertEquals(Collections.singletonList("cat"), parameters.get(mock(Traverser.Admin.class), "c", () -> "x"));
        assertEquals(Collections.singletonList("zebra"), parameters.get(mock(Traverser.Admin.class), "z", () -> "zebra"));
    }

    @Test
    public void shouldGetMultipleUsingTraverserOverload() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat");

        final Map<Object,List<Object>> params = parameters.getRaw();
        assertEquals(3, params.size());
        assertEquals(Arrays.asList("axe", "ant"), parameters.get(mock(Traverser.Admin.class), "a", () -> "x"));
        assertEquals(Arrays.asList("bat", "ball"), parameters.get(mock(Traverser.Admin.class), "b", () -> "x"));
        assertEquals(Collections.singletonList("cat"), parameters.get(mock(Traverser.Admin.class), "c", () -> "x"));
        assertEquals(Collections.singletonList("zebra"), parameters.get(mock(Traverser.Admin.class), "z", () -> "zebra"));
    }

    @Test
    public void shouldGetRaw() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        final Map<Object,List<Object>> params = parameters.getRaw();
        assertEquals(3, params.size());
        assertEquals("axe", params.get("a").get(0));
        assertEquals("bat", params.get("b").get(0));
        assertEquals("cat", params.get("c").get(0));
    }

    @Test
    public void shouldGetRawWithMulti() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "a", "ant", "c", "cat");

        final Map<Object,List<Object>> params = parameters.getRaw();
        assertEquals(3, params.size());
        assertThat(params.get("a"), contains("axe", "ant"));
        assertEquals("bat", params.get("b").get(0));
        assertEquals("cat", params.get("c").get(0));
    }

    @Test
    public void shouldGetRawEmptyAndUnmodifiable() {
        final Parameters parameters = new Parameters();
        final Map<Object,List<Object>> params = parameters.getRaw();
        assertEquals(Collections.emptyMap(), params);
    }

    @Test
    public void shouldGetRawExcept() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        final Map<Object,List<Object>> params = parameters.getRaw("b");
        assertEquals(2, params.size());
        assertEquals("axe", params.get("a").get(0));
        assertEquals("cat", params.get("c").get(0));
    }

    @Test
    public void shouldRemove() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        final Map<Object,List<Object>> before = parameters.getRaw();
        assertEquals(3, before.size());
        assertEquals("axe", before.get("a").get(0));
        assertEquals("bat", before.get("b").get(0));
        assertEquals("cat", before.get("c").get(0));

        parameters.remove("b");

        final Map<Object,List<Object>> after = parameters.getRaw("b");
        assertEquals(2, after.size());
        assertEquals("axe", after.get("a").get(0));
        assertEquals("cat", after.get("c").get(0));
    }

    @Test
    public void shouldRename() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        parameters.rename("a", "z");

        final Map<Object,List<Object>> before = parameters.getRaw();
        assertEquals(3, before.size());
        assertEquals("axe", before.get("z").get(0));
        assertEquals("bat", before.get("b").get(0));
        assertEquals("cat", before.get("c").get(0));
    }

    @Test
    public void shouldContainKey() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        assertThat(parameters.contains("b"), is(true));
    }

    @Test
    public void shouldNotContainKey() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        assertThat(parameters.contains("z"), is(false));
    }

    @Test
    public void shouldGetSetMultiple() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat");

        final Map<Object,List<Object>> params = parameters.getRaw();
        assertEquals(3, params.size());
        assertEquals("axe", params.get("a").get(0));
        assertEquals("ant", params.get("a").get(1));
        assertEquals("bat", params.get("b").get(0));
        assertEquals("ball", params.get("b").get(1));
        assertEquals("cat", params.get("c").get(0));
    }

    @Test
    public void shouldGetDefault() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "b", "bat", "c", "cat");

        assertEquals(Collections.singletonList("axe"), parameters.get("a", () -> "x"));
        assertEquals(Collections.singletonList("bat"), parameters.get("b", () -> "x"));
        assertEquals(Collections.singletonList("cat"), parameters.get("c", () -> "x"));
        assertEquals(Collections.singletonList("zebra"), parameters.get("z", () -> "zebra"));
    }

    @Test
    public void shouldClone() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat");

        final Parameters parametersClone = parameters.clone();

        assertEquals(parameters.getRaw(), parametersClone.getRaw());
    }

    @Test
    public void shouldBeDifferent() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat");

        final Parameters parametersDifferent = new Parameters();
        parametersDifferent.set("a", "ant", "a", "axe", "b", "bat", "b", "ball", "c", "cat");

        assertNotEquals(parameters.getRaw(), parametersDifferent.getRaw());
    }

    @Test
    public void shouldGetNoTraversals() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat");
        assertEquals(Collections.emptyList(), parameters.getTraversals());
    }

    @Test
    public void shouldGetTraversals() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat", "t", __.outE("knows"));
        assertEquals(Collections.singletonList(__.outE("knows")), parameters.getTraversals());
    }

    @Test
    public void shouldIntegrateTraversals() {
        final Parameters parameters = new Parameters();
        parameters.set("a", "axe", "a", "ant", "b", "bat", "b", "ball", "c", "cat", "t", __.outE("knows"));

        final TraversalParent mock = mock(TraversalParent.class);

        // the mock can return nothing of consequence as the return isn't used in this case. we just need to
        // validate that the method gets called as a result of calls to set/integrateTraversals()
        when(mock.integrateChild(__.outE("knows").asAdmin())).thenReturn(null);

        parameters.integrateTraversals(mock);

        verify(mock).integrateChild(__.outE("knows").asAdmin());
    }
}

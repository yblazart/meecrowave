/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.meecrowave.junit;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.spi.ContextsService;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ScopeRule implements TestRule {
    private final List<Class<? extends Annotation>> scopes;

    public ScopeRule(final Class<? extends Annotation>... scopes) {
        this.scopes = new ArrayList<>(asList(scopes));
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                final ContextsService contextsService = WebBeansContext.currentInstance().getContextsService();
                try {
                    scopes.forEach(s -> contextsService.startContext(s, null));
                    base.evaluate();
                } finally {
                    Collections.reverse(scopes);
                    scopes.forEach(s -> contextsService.endContext(s, null));
                    Collections.reverse(scopes);
                }
            }
        };
    }
}

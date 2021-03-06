/*
 * Copyright (c) 2018, 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.posix;

import java.util.Collections;
import java.util.List;

import org.graalvm.nativeimage.Feature;
import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.function.CEntryPointLiteral;

import com.oracle.svm.core.CompilerCommandPlugin;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jdk.RuntimeFeature;
import com.oracle.svm.core.jdk.RuntimeSupport;

/**
 * Command returns a path of the object file defining the symbol. The command requires a single
 * {@link String} parameter containing the symbol name or single {@link CEntryPointLiteral}
 * parameter containing a function pointer to symbol.
 */
@Platforms({Platform.LINUX.class, Platform.DARWIN.class})
public class PosixObjectFile implements CompilerCommandPlugin {

    @Override
    public String name() {
        return "com.oracle.svm.core.posix.GetObjectFile";
    }

    @Override
    public Object apply(Object[] args) {
        if (args.length == 1) {
            if (args[0] instanceof String) {
                return PosixProcessPropertiesSupport.getObjectPathDefiningSymbol((String) args[0]);
            } else if (args[0] instanceof CEntryPointLiteral) {
                return PosixProcessPropertiesSupport.getObjectPathDefiningAddress(((CEntryPointLiteral<?>) args[0]).getFunctionPointer());
            }
        }
        throw new IllegalArgumentException("Expecting single String or CEntryPointLiteral agrument.");
    }

    @AutomaticFeature
    public static class ExposeObjectFileNameFeature implements Feature {
        @Override
        public List<Class<? extends Feature>> getRequiredFeatures() {
            return Collections.singletonList(RuntimeFeature.class);
        }

        @Override
        public void afterRegistration(AfterRegistrationAccess access) {
            RuntimeSupport rs = RuntimeSupport.getRuntimeSupport();
            rs.addCommandPlugin(new PosixObjectFile());
        }
    }
}

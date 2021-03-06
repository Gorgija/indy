/**
 * Copyright (C) 2011-2018 Red Hat, Inc. (https://github.com/Commonjava/indy)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.commonjava.indy.core.content.group;

import java.util.Collection;

import org.apache.maven.artifact.repository.metadata.Metadata;
import org.commonjava.indy.model.core.Group;
import org.commonjava.maven.galley.model.Transfer;

public interface MetadataMerger
{

    byte[] merge( final Collection<Transfer> sources, final Group group, final String path );

    Metadata mergeFromMetadatas( final Collection<Metadata> sources, final Group group, final String path );

}

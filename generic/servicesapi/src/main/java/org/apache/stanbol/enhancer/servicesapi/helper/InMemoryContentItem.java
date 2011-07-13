/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.stanbol.enhancer.servicesapi.helper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.clerezza.rdf.core.MGraph;
import org.apache.clerezza.rdf.core.impl.SimpleMGraph;
import org.apache.stanbol.enhancer.servicesapi.ContentItem;


/**
 * Base content item implementation that holds a complete copy of the data in
 * memory.
 * <p>
 * This implementation can be used independently of any store implementation and
 * is suitable for stateless processing.
 */
public class InMemoryContentItem implements ContentItem {
    // private final Logger log = LoggerFactory.getLogger(getClass());

    private final MGraph metadata;

    private final String id;

    private final String mimeType;

    private final byte[] data;

    public InMemoryContentItem(String id) {
        this(id, null, null, null);
    }

    public InMemoryContentItem(byte[] content, String mimetype) {
        this(null, content, mimetype, null);
    }

    public InMemoryContentItem(String id, byte[] content, String mimeType) {
        this(id, content, mimeType, null);
    }

    public InMemoryContentItem(String id, byte[] content, String mimeType,
            MGraph metadata) {
        if (id == null) {
            id = ContentItemHelper.makeDefaultUrn(content).getUnicodeString();
        }

        if (metadata == null) {
            metadata = new SimpleMGraph();
        }
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        } else {
            // Keep only first part of content-types like text/plain ; charset=UTF-8
            mimeType = mimeType.split(";")[0].trim();
        }
        if (content == null) {
            content = new byte[0];
        }

        this.id = id;
        this.data = content;
        this.mimeType = mimeType;
        this.metadata = metadata;
    }

    protected static final InMemoryContentItem fromString(String content) {
        return new InMemoryContentItem(content.getBytes(), "text/plain");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id=[" + id + "], mimeType[="
                + mimeType + "], data=[" + data.length + "] bytes"
                + ", metadata=" + metadata;
    }

    public String getId() {
        return id;
    }

    public MGraph getMetadata() {
        return metadata;
    }

    public String getMimeType() {
        return mimeType;
    }

    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }

}

/*
 * Copyright © 2023 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.redhat.exhort;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.exhort.tools.Ecosystem;
import java.io.IOException;
import java.nio.file.Path;

/**
 * The Provider abstraction is used for contracting providers providing a {@link Content} per
 * manifest type for constructing backend requests.
 */
public abstract class Provider {

  public static final String PROP_MATCH_MANIFEST_VERSIONS = "MATCH_MANIFEST_VERSIONS";

  /**
   * Content is used to aggregate a content buffer and a content type. These will be used to
   * construct the backend API request.
   */
  public static class Content {
    public final byte[] buffer;
    public final String type;

    public Content(byte[] buffer, String type) {
      this.buffer = buffer;
      this.type = type;
    }
  }

  /** The ecosystem of this provider, i.e. maven. */
  public final Ecosystem.Type ecosystem;

  public final Path manifest;

  protected final ObjectMapper objectMapper = new ObjectMapper();

  protected Provider(Ecosystem.Type ecosystem, Path manifest) {
    this.ecosystem = ecosystem;
    this.manifest = manifest;
  }

  /**
   * Use for providing content for a stack analysis request.
   *
   * @return A Content record aggregating the body content and content type
   * @throws IOException when failed to load the manifest file
   */
  public abstract Content provideStack() throws IOException;

  /**
   * Use for providing content for a component analysis request.
   *
   * @return A Content record aggregating the body content and content type
   * @throws IOException when failed to load the manifest content
   */
  public abstract Content provideComponent() throws IOException;

  /**
   * If a package manager requires having a lock file it must exist in the provided path
   *
   * @param lockFileDir Path to the directory where the lock file must exist
   */
  public void validateLockFile(Path lockFileDir) {
    // Default implementation. Do not require a lock file.
  }

  /**
   * Returns the name or path of the executable used by this provider (e.g., "mvn" or "gradle").
   *
   * <p>Implementations should resolve the appropriate executable name or its full path, possibly by
   * checking environment variables, configuration files, or defaulting to standard command names.
   *
   * @param command the command name
   * @return the resolved executable name or full path as a {@code String}
   * @throws IOException if the executable cannot be found, resolved, or is not accessible
   */
  protected abstract String getExecutable(String command) throws IOException;
}

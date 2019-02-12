
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.carrot2.util.StreamUtils;
import org.slf4j.LoggerFactory;

/**
 * A local filesystem resource. This loader provides cached content of
 * returned resources and closes the underlying stream handle in {@link #open()}.
 */
public final class FileResource implements IResource
{
    /**
     * File pointed to by this resource.
     */
    private final Path file;

    public FileResource(Path file)
    {
        this.file = file;
    }

    public InputStream open() throws IOException
    {
        return StreamUtils.prefetch(Files.newInputStream(file));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof FileResource)
        {
            return ((FileResource) obj).file.equals(this.file);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return this.file.hashCode();
    }

    @Override
    public String toString()
    {
        return getPath().toAbsolutePath().toString();
    }

    public String getFileName()
    {
        Path p = file.getFileName();
        if (p != null) {
          return p.toString();
        } else {
          return file.toString();
        }
    }

    public static FileResource valueOf(String path)
    {
        // Return non-null value only if the string is a path to some existing file.
        try {
          Path p = Paths.get(path);
          if (Files.exists(p)) {
            return new FileResource(p);
          } else {
            return null;
          }
        } catch (InvalidPathException e) {
          return null;
        } catch (Throwable e) {
          // CARROT-1162 (IKVM throws unchecked exceptions from Files.* on inaccessible folders.
          LoggerFactory.getLogger(FileResource.class)
            .warn("Could not access path: " + path, e);
          return null;
        }
    }

    public Path getPath() {
      return file;
    }
}
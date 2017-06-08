////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.checkstyle.regression.internal;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

/**
 * Contains utility methods for git component test.
 * @author LuoLiangchen
 */
public final class GitUtils {
    /** Prevents instantiation. */
    private GitUtils() {
    }

    public static Repository createNewRepository() throws IOException {
        final File repoDir = File.createTempFile("TestTempRepository", "");
        if (!repoDir.delete()) {
            throw new IOException("Could not delete temporary file " + repoDir);
        }
        final Repository repository = FileRepositoryBuilder.create(new File(repoDir, ".git"));
        repository.create();
        return repository;
    }

    public static void createNewBranchAndCheckout(Repository repository, String branchName)
            throws GitAPIException {
        try (Git git = new Git(repository)) {
            if (git.branchList().call().stream()
                    .anyMatch(ref -> ref.getName().equals(Constants.R_HEADS + branchName))) {
                git.branchDelete().setBranchNames(branchName).setForce(true).call();
            }
            git.branchCreate().setName(branchName).call();
            git.checkout().setName(branchName).call();
        }
    }

    public static File addAnEmptyFileAndCommit(Repository repository, String fileName)
            throws IOException, GitAPIException {
        try (Git git = new Git(repository)) {
            final File file = new File(repository.getDirectory().getParent(), fileName);
            if (!file.createNewFile()) {
                throw new IOException("Could not create file " + file);
            }
            git.add().addFilepattern(fileName).call();
            git.commit().setMessage("add " + fileName).call();
            return file;
        }
    }

    public static void addAllAndCommit(Repository repository, String message)
            throws GitAPIException {
        try (Git git = new Git(repository)) {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(message).call();
        }
    }
}

/*
 * Copyright (C) 2014 Vandal LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vandalsoftware.tools.gradle

import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.errors.RepositoryNotFoundException
import org.eclipse.jgit.errors.RevisionSyntaxException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.util.io.NullOutputStream
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Given a range, retrieves a set of changed files in a Git repository.
 *
 * This Task accepts the following extension properties:
 *
 * <ul>
 *     <li><code>revision</code>. Closure.</li>
 *     <li><code>fromRevision</code>. Closure.</li>
 * </ul>
 *
 * @author Jonathan Le
 */
class GetChangedFiles extends DefaultTask {
    Set<File> files
    OutputStream outputStream

    GetChangedFiles() {
        files = new HashSet<>()
    }

    @TaskAction
    void getModifiedFiles() {
        def file = new File(project.rootDir, '.git')
        Repository repository = new FileRepositoryBuilder()
                .setGitDir(file)
                .readEnvironment()
                .findGitDir()
                .build()
        if (!repository.getObjectDatabase().exists()) {
            throw new RepositoryNotFoundException("Unknown repository: " + file)
        }
        RevWalk revWalk = new RevWalk(repository)
        try {
            RevCommit commit = getCommit(repository, revWalk)
            RevCommit fromCommit = getFromCommit(repository, revWalk, commit)
            if (fromCommit) {
                DiffFormatter diffFormatter = new DiffFormatter(NullOutputStream.INSTANCE)
                diffFormatter.setRepository(repository)
                diffFormatter.setDiffComparator(RawTextComparator.DEFAULT)
                diffFormatter.setDetectRenames(true)
                List<DiffEntry> diffs = diffFormatter.scan(fromCommit.getTree(), commit.getTree())
                for (DiffEntry diff : diffs) {
                    switch (diff.getChangeType()) {
                        case DiffEntry.ChangeType.ADD:
                        case DiffEntry.ChangeType.MODIFY:
                        case DiffEntry.ChangeType.RENAME:
                            files.add(new File(diff.getNewPath()))
                            break
                        case DiffEntry.ChangeType.COPY:
                            files.add(new File(diff.getOldPath()))
                            files.add(new File(diff.getNewPath()))
                            break
                        default:
                            break
                    }
                }
            } else {
                TreeWalk treeWalk = new TreeWalk(repository)
                treeWalk.addTree(commit.getTree())
                treeWalk.setRecursive(true)
                while (treeWalk.next()) {
                    files.add(new File(treeWalk.getPathString()))
                }
            }
        } finally {
            revWalk.dispose()
            repository.close()
        }
    }

    private RevCommit getFromCommit(Repository repository, RevWalk revWalk, RevCommit commit) {
        RevCommit oldCommit = null
        // Try the "from" revision string if one is given
        if (hasProperty('fromRevision')) {
            ObjectId fromCommitId = repository.resolve(fromRevision() as String)
            oldCommit = revWalk.parseCommit(fromCommitId)
        }
        // Get the commit's direct parent
        if (!oldCommit && commit.getParentCount() > 0) {
            oldCommit = revWalk.parseCommit(commit.getParent(0).getId())
        }
        return oldCommit
    }

    private RevCommit getCommit(Repository repository, RevWalk revWalk) {
        ObjectId commitId
        if (hasProperty('revision')) {
            String revStr = revision() as String
            commitId = repository.resolve(revStr)
            if (commitId == null) {
                throw new RevisionSyntaxException(revStr)
            }
        } else {
            commitId = repository.resolve(Constants.HEAD)
        }
        return revWalk.parseCommit(commitId)
    }
}

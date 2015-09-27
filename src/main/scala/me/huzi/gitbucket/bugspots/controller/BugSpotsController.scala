package me.huzi.gitbucket.bugspots.controller

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk._
import gitbucket.core.controller._
import gitbucket.core.service._
import gitbucket.core.util._
import gitbucket.core.util.ControlUtil._
import gitbucket.core.util.Directory._
import me.huzi.gitbucket.bugspots.html
import me.huzi.gitbucket.bugspots.util._
import me.huzi.gitbucket.bugspots.util.BugSpotUtil._

class BugSpotsController extends BugSpotsControllerBase
  with RepositoryService with AccountService
  with ReferrerAuthenticator

trait BugSpotsControllerBase extends ControllerBase {
  self: RepositoryService with AccountService with ReferrerAuthenticator =>

  private val logger = org.slf4j.LoggerFactory.getLogger(classOf[BugSpotsControllerBase])

  get("/:owner/:repository/bugspots")(referrersOnly { repository =>
    if (repository.commitCount == 0) {
      html.guide(repository)
    } else {
      using(Git.open(getRepositoryDir(repository.owner, repository.name))) { git =>
        using(new RevWalk(git.getRepository)) { revWalk =>
          val fixes = BugSpotUtil.getFixList(git, revWalk, repository.repository.defaultBranch)
          val spots = BugSpotUtil.getBugSpots(fixes)
          html.list(repository, fixes, spots)
        }
      }
    }
  })
}

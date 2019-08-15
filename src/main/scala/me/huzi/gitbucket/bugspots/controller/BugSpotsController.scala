package me.huzi.gitbucket.bugspots.controller

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk._
import gitbucket.core.controller._
import gitbucket.core.service._
import gitbucket.core.util._
import gitbucket.core.util.SyntaxSugars._
import gitbucket.core.util.Directory._
import me.huzi.gitbucket.bugspots.html
import me.huzi.gitbucket.bugspots.util._
import scala.util.Using

class BugSpotsController extends BugSpotsControllerBase
  with RepositoryService with AccountService
  with ReferrerAuthenticator

trait BugSpotsControllerBase extends ControllerBase {
  self: RepositoryService with AccountService with ReferrerAuthenticator =>

  private val logger = org.slf4j.LoggerFactory.getLogger(classOf[BugSpotsControllerBase])
  private val DEFAULT_REGEX = """(?i).*\b(fix(ed|es)?|close(s|d)?)\b.*""".r

  get("/:owner/:repository/bugspots")(referrersOnly { repository =>
    Using.resource(Git.open(getRepositoryDir(repository.owner, repository.name))) { git =>
      if (JGitUtil.isEmpty(git)) {
        html.guide(repository)
      } else {
        val regex = params.get("regex").map(_.r).getOrElse(DEFAULT_REGEX)
        Using.resource(new RevWalk(git.getRepository)) { revWalk =>
          val fixes = BugSpotUtil.getFixList(git, revWalk, repository.repository.defaultBranch) { rc =>
            regex.findFirstIn(rc.getFullMessage).nonEmpty
          }
          val spots = BugSpotUtil.getBugSpots(fixes)
          html.list(repository, regex.regex, fixes, spots)
        }
      }
    }
  })
}

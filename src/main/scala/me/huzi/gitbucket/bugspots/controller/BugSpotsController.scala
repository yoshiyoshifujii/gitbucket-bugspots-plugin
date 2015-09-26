package me.huzi.gitbucket.bugspots.controller

import scala.collection.JavaConverters._
import scala.sys.process._
import org.eclipse.jgit.api.Git
import gitbucket.core.controller._
import gitbucket.core.model._
import gitbucket.core.service._
import gitbucket.core.util._
import gitbucket.core.util.ControlUtil._
import gitbucket.core.util.Directory._
import gitbucket.core.util.Implicits._
import gitbucket.core.util.JGitUtil._
import me.huzi.gitbucket.bugspots.html

class BugSpotsController extends BugSpotsControllerBase
  with RepositoryService with AccountService
  with ReferrerAuthenticator

trait BugSpotsControllerBase extends ControllerBase {
  self: RepositoryService with AccountService with ReferrerAuthenticator =>

  get("/:owner/:repository/bugspots")(referrersOnly { repository =>
    html.list(repository)
  })
}


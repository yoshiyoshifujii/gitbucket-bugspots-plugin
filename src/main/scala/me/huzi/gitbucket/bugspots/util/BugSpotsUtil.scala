package me.huzi.gitbucket.bugspots.util

import java.time._
import scala.collection.JavaConverters._
import scala.util.matching.Regex
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk._
import org.eclipse.jgit.treewalk._
import gitbucket.core.util._
import gitbucket.core.util.JGitUtil._

object BugSpotUtil {

  case class Fix(
    commit: CommitInfo,
    message: String,
    date: java.util.Date,
    files: List[String])

  object Fix {
    def apply(commitInfo: CommitInfo, files: List[String]): Fix = {
      Fix(
        commit = commitInfo,
        message = commitInfo.fullMessage.split("\n").head,
        date = if (commitInfo.isDifferentFromAuthor) commitInfo.commitTime else commitInfo.authorTime,
        files = files)
    }
  }

  case class Spot(file: String, score: Double)

  implicit class RichZonedDateTime(self: ZonedDateTime) {
    def -(d: java.util.Date) = (self.toEpochSecond - toZonedDateTime(d).toEpochSecond).toDouble./(1000)
    private def toZonedDateTime(d: java.util.Date) = d.toInstant.atZone(ZoneOffset.UTC)
  }

  def getFixList(git: Git, revWalk: RevWalk, target: String)(isIncluded: RevCommit => Boolean) = {
    revWalk.markStart(revWalk.parseCommit(git.getRepository.resolve(target)))
    revWalk.sort(RevSort.TOPO, true)
    revWalk.sort(RevSort.REVERSE, true)
    revWalk.iterator().asScala.toStream.filter(isIncluded(_)).map { rc =>
      val ci = new CommitInfo(rc)
      val files = rc.getParents.headOption.map { oc =>
        getDiffs(git, rc.getName, oc.getName).map(_.oldPath)
      }.getOrElse(Nil)
      Fix(ci, files)
    }.toList
  }

  private def getDiffs(git: Git, from: String, to: String): List[DiffInfo] = {
    val reader = git.getRepository.newObjectReader
    val oldTreeIter = new CanonicalTreeParser
    oldTreeIter.reset(reader, git.getRepository.resolve(from + "^{tree}"))

    val newTreeIter = new CanonicalTreeParser
    newTreeIter.reset(reader, git.getRepository.resolve(to + "^{tree}"))

    git.diff.setNewTree(newTreeIter).setOldTree(oldTreeIter).call.asScala.map { diff =>
      val oldIsImage = FileUtil.isImage(diff.getOldPath)
      val newIsImage = FileUtil.isImage(diff.getNewPath)
      DiffInfo(
        diff.getChangeType,
        diff.getOldPath,
        diff.getNewPath,
        None,
        None,
        oldIsImage,
        newIsImage,
        Option(diff.getOldId).map(_.name),
        Option(diff.getNewId).map(_.name),
        diff.getOldMode.toString,
        diff.getNewMode.toString,
        false)
    }.toList
  }

  def getBugSpots(fixes: List[Fix]) = {
    val now = ZonedDateTime.now(ZoneOffset.UTC)
    val first = fixes.headOption
    fixes.flatMap { fix =>
      fix.files.map { file =>
        val t = 1 - ((now - fix.date) / (now - first.get.date))
        (file, 1 / (1 + java.lang.Math.exp((-12 * t.toFloat) + 12)))
      }
    }.groupBy(_._1).map {
      case (file, l) =>
        (file, l.foldLeft(0d)((i, t) => i + t._2))
    }.toList.sortBy(_._2).reverse.map {
      t => Spot(t._1, t._2)
    }
  }

}
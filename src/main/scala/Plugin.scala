import gitbucket.core.controller.Context
import gitbucket.core.plugin.Link
import gitbucket.core.service.RepositoryService.RepositoryInfo
import io.github.gitbucket.solidbase.model.Version
import me.huzi.gitbucket.bugspots.controller.BugSpotsController

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "bugspots"

  override val pluginName: String = "Bug Spots"

  override val description: String = "Bug Prediction at Google"

  override val versions: Seq[Version] = Seq(
    new Version("1.0"),
    new Version("3.12"),
    new Version("4.0.0"),
    new Version("4.5.0"),
    new Version("4.10.0")
  )

  override val controllers = Seq(
    "/*" -> new BugSpotsController
  )

  override val repositoryMenus = Seq(
    (repository: RepositoryInfo, context: Context) =>
      Some(Link(
        id = "BugSpots",
        label = "Bug Spots",
        path = s"/bugspots",
        icon = Some("menu-icon octicon octicon-bug")
      ))
  )

}

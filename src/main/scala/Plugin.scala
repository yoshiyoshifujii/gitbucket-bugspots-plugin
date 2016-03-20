

import gitbucket.core.plugin.PluginRegistry
import gitbucket.core.service.SystemSettingsService.SystemSettings
import gitbucket.core.util.Version
import javax.servlet.ServletContext
import me.huzi.gitbucket.bugspots.controller.BugSpotsController

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "bugspots"

  override val pluginName: String = "Bug Spots"

  override val description: String = "Bug Prediction at Google"

  override val versions: Seq[Version] = Seq(
    Version(3, 12),
    Version(1, 0))

  override val controllers = Seq(
    "/*" -> new BugSpotsController
  )

  override def javaScripts(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Seq[(String, String)] = {
    // Add Snippet link to the header
    val path = settings.baseUrl.getOrElse(context.getContextPath)
    Seq(
      ".*/(?!bugspots)[^/]*" -> s"""
        |$$('ul.headmenu').map(function(i) {
        |  var owner = $$("input[type=hidden][name=owner]").val();
        |  var repository = $$("input[type=hidden][name=repository]").val();
        |  var s = $$(this);
        |  var lc = s.children(':last').remove().clone();
        |  s.append(
        |    $$('<li></li>').append(
        |      $$('<a><i class="menu-icon octicon octicon-bug"></i> Bug Spots</a>').attr('href', '${path}/' + owner + '/' + repository + '/bugspots')
        |    )
        |  );
        |  s.append(lc);
        |});
      """.stripMargin,
      ".*/bugspots" -> s"""
        |$$('ul.headmenu').map(function(i) {
        |  var owner = $$("input[type=hidden][name=owner]").val();
        |  var repository = $$("input[type=hidden][name=repository]").val();
        |  var s = $$(this);
        |  var lc = s.children(':last').remove().clone();
        |  s.append(
        |    $$('<li class="active"></li>').append(
        |      $$('<a><i class="menu-icon menu-icon-active octicon octicon-bug"></i> Bug Spots</a>').attr('href', '${path}/' + owner + '/' + repository + '/bugspots')
        |    )
        |  );
        |  s.append(lc);
        |});
      """.stripMargin)
  }

}

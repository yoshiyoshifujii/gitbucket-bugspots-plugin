

import gitbucket.core.plugin.PluginRegistry
import gitbucket.core.service.SystemSettingsService.SystemSettings
import gitbucket.core.util.Version
import javax.servlet.ServletContext

class Plugin extends gitbucket.core.plugin.Plugin {
  override val pluginId: String = "bugspots"

  override val pluginName: String = "Bug Spots"

  override val description: String = "Bug Prediction at Google"

  override val versions: Seq[Version] = Seq(
    Version(1, 0))

  override def javaScripts(registry: PluginRegistry, context: ServletContext, settings: SystemSettings): Seq[(String, String)] = {
    // Add Snippet link to the header
    val path = settings.baseUrl.getOrElse(context.getContextPath)
    Seq(
      ".*/(?!bugspots)[^/]*" -> s"""
        |$$('ul.sidemenu .menu-icon.octicon-book').parents('ul.sidemenu').map(function(i) {
        |  var owner = $$("input[type=hidden][name=owner]").val();
        |  var repository = $$("input[type=hidden][name=repository]").val();
        |  var s = $$(this);
        |  var lc = s.children(':last').remove().clone();
        |  if($$("li[data-toggle=tooltip] i.menu-icon.octicon-book", s).length === 0) {
        |    s.append(
        |      $$('<li>').append(
        |        $$('<a><i class="menu-icon octicon octicon-pulse"></i> Bug Spots</a></li>').attr('href', '${path}/' + owner + '/' + repository + '/bugspots')
        |      )
        |    );
        |  } else {
        |    s.append(
        |      $$('<li data-toggle="tooltip" data-placement="left" data-original-title="Bug Spots"></li>').append(
        |        $$('<a href=""><i class="menu-icon octicon octicon-pulse"></i></a>').attr('href', '${path}/' + owner + '/' + repository + '/bugspots')
        |      )
        |    );
        |  }
        |  s.append(lc);
        |});
      """.stripMargin,
      ".*/bugspots" -> s"""
        |$$('ul.sidemenu .menu-icon.octicon-book').parents('ul.sidemenu').map(function(i) {
        |  var owner = $$("input[type=hidden][name=owner]").val();
        |  var repository = $$("input[type=hidden][name=repository]").val();
        |  var s = $$(this);
        |  var lc = s.children(':last').remove().clone();
        |  s.append(
        |    $$('<li class="active" data-toggle="tooltip" data-placement="left" data-original-title="Bug Spots"></li>').append(
        |      $$('<a href=""><i class="menu-icon menu-icon-active octicon octicon-pulse"></i></a>').attr('href', '${path}/' + owner + '/' + repository + '/bugspots')
        |    )
        |  );
        |  s.append(lc);
        |});
      """.stripMargin)
  }

}

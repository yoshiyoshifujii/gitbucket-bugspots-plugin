# gitbucket-bugspots-plugin

This plugin enhances [gitbucket/gitbucket](https://github.com/gitbucket/gitbucket) by providing an viewing bugspots.

## Features

It will viewing the bug prediction at Google.
[Bug Predicition at Google](http://google-engtools.blogspot.jp/2011/12/bug-prediction-at-google.html)

## Usage

- Open a shell window at the root of the project, hit `sbt package`
- if you update gitbucket-bugspots-plugin, remove any existing copy of gitbucket-bugspots-plugin from GITBUCKET_HOME/plugins
- Copy target/scala-2.11/gitbucket-bugspots-plugin-plugin_2.11-x.x.jar into GITBUCKET_HOME/plugins
- Restart GitBucket


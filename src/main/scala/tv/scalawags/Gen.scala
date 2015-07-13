package tv.scalawags

import java.io.File

import html.podcast

object Gen {
  def main(args: Array[String]): Unit = {
    val outDir = new File("target/html")
    val episodes = Feeds.load
    genResources(outDir)
    episodes foreach genEpisodeHtml(outDir)
  }

  def genEpisodeHtml(outDir: File)(podcast: Podcast): Unit = {
    val outFile = new File(outDir, podcast.relativeFile)
    val htmlText = html.podcast(podcast)
    System.err.println(s"Generating episode: $outFile")
    sbt.io.IO.write(outFile, htmlText.toString)
  }

  def genResources(outDir: File): Unit = {
    def copyResource(name: String): Unit = {
      val in = this.getClass.getClassLoader.getResourceAsStream(s"theme/$name")
      try {
        val out = new File(outDir, name)
        sbt.io.IO.transfer(in, out)
      } finally in.close()
    }
    copyResource("buttons.png")
    copyResource("load.gif")
    copyResource("pirate.png")
    copyResource("scalawags.png")
    copyResource("social.png")
    copyResource("main.css")
    copyResource("jquery.min.js")
  }
}

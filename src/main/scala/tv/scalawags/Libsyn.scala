package tv.scalawags

import org.joda.time.format.DateTimeFormat
import sbt.io._

import scala.xml.Node

/**
 * Helper for reading data form libsyn.
 */
object Libsyn {
   /** Loads the libsyn podcast feed. */
   def loadFeed: Seq[AudioDetails] = readFeed(loadXml)

   private val location = new java.net.URL("http://scalawags.tv/rss")
   private val fmt = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss Z");
   private def loadXml: scala.xml.Elem = scala.xml.XML.load(location)
   private def readFeed(feed: Node): Seq[AudioDetails] = {
      for(entry <- (feed \\ "item")) yield {
         val title = (entry \\ "title").text
         //System.err.println(s"Parsing $title")
         val link = (entry \\ "link").text
         val desc = (entry \\ "description").text
         val url = (entry \\ "enclosure" \\ "@url").text
         val dateText = (entry \\ "pubDate").text
         val date = fmt.parseDateTime(dateText)
         //System.err.println("Parsed date")
         new AudioDetails(
           id = PodcastId(date.toLocalDate),
           title = title,
           link = link,  // TODO - check as URL
           description = desc,
           audioUrl = url  // TODO - check as url
         )
      }
   }
}

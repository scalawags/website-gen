package tv.scalawags

import java.net.URL

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import scala.util.parsing.json.JSON


/**
 * Pulls our feed from youtube.
 *
 * TODO - Rewrite this to not use scala's JSON library, which is so bad as to make this code really hard to understand, when it should be dead simple.
 */
object Youtube {
  private val apiKey = "AIzaSyCPmR2Jp203pMy8FbefTuFOjKpiCbvBvDM"
  private val url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet%2CcontentDetails&playlistId=UUHxNwi3l5CGZo1kG47k7i2Q&key=" + apiKey

  //2015-07-02T15:40:35.000Z
  private val fmt = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  private def readRaw = sbt.io.IO.readLinesURL(new URL(url)).mkString("")

  private def readJson: Option[Any] = JSON.parseFull(readRaw)

  def read: Option[Seq[VideoDetails]] = readJson map parseYoutubeFeed


  private def parseYoutubeFeed(parsed: Any): Seq[VideoDetails] = {
    child(parsed, "items") match {
      case Some(items) =>
        for {
          item <- items.asInstanceOf[Seq[Any]]
          parsed = parseFeedItem(item)
          if parsed.isDefined
        } yield parsed.get
      case _ =>
        System.err.println(s"Failed to find items in $parsed")
        Nil
    }
  }

  private def parseFeedItem(json: Any): Option[VideoDetails] = {
    child(json, "snippet")  match {
      case Some(snippet) =>
        for {
          d <- getPublishedAt(snippet)
          yid <- getVideoId(json)
          title <- getTitle(snippet)
          turl <- getThumbnail(snippet)
        } yield VideoDetails(PodcastId(d), title, turl, yid)
      case _ =>
        System.err.println(s"Failed to find snippet!")
        None
    }
  }

  private def getThumbnail(snippet: Any): Option[String] = {
    child(snippet, "thumbnails").flatMap(child(_, "default")).flatMap(child(_, "url")) match {
      case Some(x: String) => Some(x)
      case _ =>
        System.err.println(s"Failed to parse thumbnail url")
        None
    }
  }


  private def getTitle(snippet: Any): Option[String] = {
    child(snippet, "title") match {
      case Some(x: String) => Some(x)
      case _ =>
        System.err.println(s"Failed to parse title")
        None
    }
  }

  private def getPublishedAt(snippet: Any): Option[LocalDate] = {
    child(snippet, "publishedAt") match {
      case Some(x: String) => Some(fmt.parseLocalDate(x))
      case _ =>
        System.err.println(s"Failed to parse publishedAt")
        None
    }
  }
  private def getVideoId(item: Any): Option[String] = {
    child(item, "contentDetails") match {
      case Some(contentDetails) =>
        child(contentDetails, "videoId") match {
          case Some(x: String) => Some(x)
          case _ =>
            System.err.println(s"Failed to parse videoId")
            None
        }
      case _ => None
    }
  }

  def child(json: Any, name: String): Option[Any] = {
    json match {
      case x: Map[String, Any] => x get name
      case _ =>
        System.err.println(s"Failed to find $name in $json")
        None
    }
  }

}

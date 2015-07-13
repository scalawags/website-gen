package tv.scalawags

import org.joda.time.{LocalDate, DateTime}

/** Represents something that can identify a particular podcast. */
sealed trait PodcastDetails {
  def id: PodcastId
}

/** The "unqiue id" of a podcast is the date it was recorded. */
case class PodcastId(date: LocalDate) {
  def getYear = date.getYear
  def getMonth = date.getMonthOfYear
  def getDay = date.getDayOfMonth

  override def toString = s"<$getYear-$getMonth-$getDay>"
}
object PodcastId {
  implicit object PodcastIdOrdering extends Ordering[PodcastId] {
    override def compare(x: PodcastId, y: PodcastId): Int = {
      val result  = (x.date.toDateTimeAtStartOfDay.getMillis - y.date.toDateTimeAtStartOfDay.getMillis)
      if(result == 0) 0
      else if(result > 0) 1
      else -1
    }
  }
}


/** Audio details for a podcast. */
class AudioDetails(val id: PodcastId, val title: String, val link: String, val description: String, val audioUrl: String) extends PodcastDetails {
  override def toString = s"AudioDetails($id, $title, $link, $audioUrl, ...)"
}


/**
 * The details of a podcast
 */
class Podcast(val id: PodcastId, audio: Option[AudioDetails], video: Option[VideoDetails]) extends PodcastDetails {
  def title: String =
    video match {
      case Some(d) => d.title
      case _ =>
        audio match {
          case Some(a) => a.title
          case _ => s"Invalid Podcast"
        }
  }
  def hasAudio: Boolean = audio.isDefined
  def audioUrl: String = audio.map(_.audioUrl).getOrElse("")

  def hasNotes: Boolean = audio.isDefined
  def notesHtml: String = audio.map(_.description).getOrElse("")

  def hasVideo: Boolean = video.isDefined
  def videoUrl: String = video.map(_.youtubeId).map(id => s"http://www.youtube.com/v/$id?version=3&enablejsapi=1").getOrElse(sys.error(s"No video id on $this!"))


  // The name of the file generated
  def relativeFile: String = s"episode-${id.getYear}-${id.getMonth}-${id.getDay}.html"

  override def toString = s"Podcast: $title @ $id ${if(hasAudio) "A" else "X"}${if(hasVideo) "V" else "X"}"
}




/** Video details for a podcast. */
case class VideoDetails(id: PodcastId, title: String, thumbnailUrl: String, youtubeId: String) extends PodcastDetails
case class ShowNotes() {
  def html: String = "TODO - render form markdown?"
}
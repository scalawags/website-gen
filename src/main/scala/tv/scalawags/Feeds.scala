package tv.scalawags

/**
 * Loads our feeds
 */
object Feeds {

  def load: Seq[Podcast] = {
    // TODO - Error messages
    val yt = Youtube.read.getOrElse(sys.error("Unable to load youtube videos!"))
    val ls = Libsyn.loadFeed

    val ids = (yt.map(_.id) ++ ls.map(_.id)).distinct.sorted
    val videoMap = yt.groupBy(_.id)
    val audioMap = ls.groupBy(_.id)
    for {
      id <- ids
      audio = audioMap.get(id).flatMap(_.headOption)
      video = videoMap.get(id).flatMap(_.headOption)
    } yield new Podcast(id, audio, video)
  }
}

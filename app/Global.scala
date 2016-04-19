import play.api._
import java.io.File

import scala.util.Try
import scala.collection.JavaConversions

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    val tmp = new File("./tmp")
    if (!tmp.exists)
      tmp.mkdir

    //tmp.listFiles.foreach(f => Try {f.delete})
  }  
  
}

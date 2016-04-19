package eu.unicredit

import scala.util.Try
import scala.concurrent.duration._

import play.api.mvc.{ Action, Controller }
import play.api.libs.json._

import akka.actor._
import javax.inject._

import java.util.UUID

import java.io.File
import dao._
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path

class Application @Inject() (demoDao: DemoDAO, system: ActorSystem) extends Controller {

  def upload() = Action(parse.temporaryFile) { request =>

    val uuid = UUID.randomUUID()

    val file = new File(s"./tmp/$uuid")

    request.body.moveTo(file)

    import system._
    system.scheduler.scheduleOnce(24 hours)(Try {file.delete})

    Ok(Json.obj(
      "uploaded" -> true,
      "uuid" -> uuid.toString
    ))
  }

  def save(uuid1: String) = Action {

    val path = Paths.get(s"./tmp/$uuid1")
    
    val blob1 = Files.readAllBytes(path)

    val demoEntry = Demo(img1 = Some(blob1))

    import scala.util.{Success, Failure}
    import system._
    demoDao.insert(demoEntry).onComplete{
      case Success(k) => println("YAY "+k)
      case Failure(err) => println("error"); err.printStackTrace
    }
    Ok("YAY")
  }

}
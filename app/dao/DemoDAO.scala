package dao

import scala.concurrent.Future
import java.io.File

import javax.inject.{ Singleton, Inject }
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

case class Demo(
  id: Option[Int] = None,
  img1: Option[Array[Byte]] = None,
  img2: Option[Array[Byte]] = None,
  img3: Option[Array[Byte]] = None)

trait DemoComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class DemoTable(tag: Tag) extends Table[Demo](tag, "demo") {

    def id = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)
    def img1 = column[Option[Array[Byte]]]("img1")
    def img2 = column[Option[Array[Byte]]]("img2")
    def img3 = column[Option[Array[Byte]]]("img3")

    def * = (id, img1, img2, img3) <> (Demo.tupled, Demo.unapply)
  }
}

@Singleton()
class DemoDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends DemoComponent with HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val demos = TableQuery[DemoTable]

  def count = {
    demos.length
  }

  def getById(id: Int): Future[Seq[Demo]] = {
    db.run(demos.filter(_.id === id).result)
  }

  def insert(demo: Demo): Future[Demo] = {

    //There is a bug and the increment id made 2 times

    db.run((demos returning demos.map(_.id)) += demo)
    val insertQuery =
      demos returning demos.map(_.id) into ((item, id) => item.copy(id = id) )
    
    val action = (insertQuery += demo).transactionally
    db.run(action)
  }

}

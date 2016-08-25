package services





import javax.inject.Inject
import com.google.inject.Singleton
import model.Account
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


/**
  * Created by weizhang on 8/25/16.
  */
@Singleton
class AccountServiceImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends AccountService {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import slick.driver.PostgresDriver.api._
  import dbConfig._
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def accountNo = column[String]("account_no")
    def name = column[String]("name")
    def * = (id, accountNo, name) <> (Account.tupled, Account.unapply)
  }

  implicit val accounts = TableQuery[AccountTable]
  def all: Future[Seq[Account]] = {
    db.run(accounts.result)
  }


}

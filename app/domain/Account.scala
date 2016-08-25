package domain
import slick.driver.PostgresDriver.api._


case class Account(
  id: Int,
  accountNo: String,
  name: String
)

class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def accountNo = column[String]("account_no")
  def name = column[String]("name")
  def * = (id, accountNo, name) <> (Account.tupled, Account.unapply)
}


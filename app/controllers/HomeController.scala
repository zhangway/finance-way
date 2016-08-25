package controllers

import javax.inject._

import play.api.mvc._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import domain.{Account, Accounts}

import scala.concurrent.{ExecutionContext, Future}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(dbConfigProvider: DatabaseConfigProvider) (implicit ec:ExecutionContext) extends Controller {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def accounts = Action.async { implicit request =>
    import slick.driver.PostgresDriver.api._
    val accounts = TableQuery[Accounts]
    val a: Future[Seq[Account]] = dbConfig.db.run(accounts.result)
    a.map { accounts =>
      Ok(views.html.accounts(accounts))
    }
  }

}

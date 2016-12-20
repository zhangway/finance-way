package controllers


import java.io.{File, FileInputStream}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime, LocalTime, ZoneId}
import javax.inject._

import domain.TradeType
import model.Transaction
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import play.api.mvc._
import services.AccountService

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}


/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(accountService: AccountService)(implicit ec: ExecutionContext) extends Controller {

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
    accountService.all.map { accounts =>
      Ok(views.html.accounts(accounts))
    }
  }

  def excel = Action {


    Ok("he")
  }

  private def getCellString(cell: Cell) = {
    cell.getCellType match {
      case Cell.CELL_TYPE_STRING => {
        println(cell.getStringCellValue)
        cell.getStringCellValue
      }
      case Cell.CELL_TYPE_NUMERIC => {
        cell.getNumericCellValue
      }
      case _ =>
        " "
    }
  }

  private def gjensidigeAllTransactions(file: File) = {
    val fs = new POIFSFileSystem(new FileInputStream(file))
    val wb: HSSFWorkbook = new HSSFWorkbook(fs)
    val sheet = wb.getSheetAt(0)

    val rows = sheet.rowIterator().drop(1)
    rows.map { row =>
      val d = row.getCell(0).getStringCellValue
      val dt = LocalDate.parse(d, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
      val trandeDateTime = LocalDateTime.of(dt, LocalTime.of(0, 0))
      val description = row.getCell(3).getStringCellValue
      val withdrawCell = row.getCell(4)
      val depositCell = row.getCell(5)
      val tradeType = if ((withdrawCell == null || withdrawCell.getCellType == Cell.CELL_TYPE_BLANK) && (depositCell != null || depositCell.getCellType != Cell.CELL_TYPE_BLANK)) TradeType.Deposit else TradeType.WithDrawal
      val amount = tradeType match {
        case TradeType.WithDrawal => withdrawCell.getNumericCellValue.abs
        case TradeType.Deposit => depositCell.getNumericCellValue
        case _ => -1
      }
      //println(s"${tradeType}: ${tradeDateTime} - ${amount} - Desc: ${description}")
      Transaction(trandeDateTime, description, tradeType, amount)
    }.toList
  }

  private def dbnAllTransactions(file: File) = {
    val fs = new POIFSFileSystem(new FileInputStream(file))
    val wb: HSSFWorkbook = new HSSFWorkbook(fs)
    val sheet = wb.getSheetAt(0)

    val rows = sheet.rowIterator().drop(1)
    rows.map { row =>
      val d = row.getCell(0).getDateCellValue
      val dt = LocalDateTime.ofInstant(d.toInstant, ZoneId.of("Europe/Oslo"))
      val description = row.getCell(1).getStringCellValue
      val clock = description.split(" ").lastOption
      val time = clock flatMap { s =>
        val f = DateTimeFormatter.ofPattern("HH.mm")
        val d = Try(LocalTime.parse(s, f))
        d match {
          case Success(t) => {
            Some(t)
          }
          case Failure(e) => None
        }
      }
      val tradeDateTime = dt.withHour(time.map(_.getHour) getOrElse 0).withMinute(time.map(_.getMinute) getOrElse 0)
      val withdrawCell = row.getCell(3)
      val depositCell = row.getCell(4)
      val tradeType = if ((withdrawCell == null || withdrawCell.getCellType == Cell.CELL_TYPE_BLANK) && (depositCell != null || depositCell.getCellType != Cell.CELL_TYPE_BLANK)) TradeType.Deposit else TradeType.WithDrawal
      val amount = tradeType match {
        case TradeType.WithDrawal => withdrawCell.getNumericCellValue
        case TradeType.Deposit => depositCell.getNumericCellValue
        case _ => -1
      }
      //println(s"${tradeType}: ${tradeDateTime} - ${amount} - Desc: ${description}")
      Transaction(tradeDateTime, description, tradeType, amount)
    }.toList
  }


  def selectaccount = Action {
    val accounts = Seq("bergen", "oslo", "uit")
    Ok(views.html.selectAccount(accounts))
  }

  def upload = Action {
    Ok(views.html.upload())
  }



  def fileUpload = Action(parse.multipartFormData) { request =>
    val multipartFormData = request.body.asFormUrlEncoded
    for ((k, v) <- multipartFormData) println(k + "--->" + v.mkString(","))
    def allTransactions(bank: String, file: File) = {
      bank.toLowerCase match {
        case "dnb"        => dbnAllTransactions(file)
        case "gjensidige" => gjensidigeAllTransactions(file)
      }
    }
    val bank = multipartFormData.get("bank").map { x => x.mkString } getOrElse ("")
    request.body.file("transaction").map { transaction =>
      val all = allTransactions(bank.toLowerCase, transaction.ref.file)
      val withDrawalTotal = all.filter(_.tradeType == TradeType.WithDrawal)
      val withDrawalMoreThan1k = withDrawalTotal.filter(_.amount >= 1000)
      val deposits = all.filter(_.tradeType == TradeType.Deposit)

      val salary = deposits.filter(_.description.toLowerCase.contains("lønn"))


      Ok(views.html.deposits(deposits.sortWith(_.amount > _.amount), salary, withDrawalTotal.sortWith(_.amount > _.amount), withDrawalMoreThan1k.sortWith(_.amount > _.amount)))
    }.getOrElse {
      Redirect(routes.HomeController.upload).flashing(
        "error" -> "Missing file")
    }
  }
}

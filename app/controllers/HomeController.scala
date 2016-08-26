package controllers


import java.io.{File, FileInputStream}
import javax.inject._

import org.apache.poi.hssf.usermodel.{HSSFDateUtil, HSSFWorkbook}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted
import org.apache.poi.ss.usermodel.{Cell, DataFormatter, DateUtil, WorkbookFactory}
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import play.api.mvc._
import services.AccountService

import scala.collection.JavaConversions._
import scala.concurrent.{ExecutionContext, Future}


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
    val fs = new POIFSFileSystem(new FileInputStream("dnb3.xls"))
    val wb: HSSFWorkbook = new HSSFWorkbook(fs)
    val sheet = wb.getSheetAt(0)
    println(sheet.getPhysicalNumberOfRows)
    def getCellString(cell: Cell) = {
      cell.getCellType match {
        case Cell.CELL_TYPE_STRING => {
          println(cell.getStringCellValue)
          cell.getStringCellValue
        }
        case Cell.CELL_TYPE_NUMERIC => {
          cell.getNumericCellValue
        }
        case _                      =>
          " "
      }
    }
    sheet.rowIterator().foreach { row =>
      val cellI = row.cellIterator()
      cellI.foreach { cell =>
        cell.getCellType match {
          case Cell.CELL_TYPE_STRING => println(s"String: ${cell.getStringCellValue}")
          case Cell.CELL_TYPE_NUMERIC => {
            if(isCellDateFormatted(cell)){
              println(cell.getDateCellValue)
            } else println(s"number: ${cell.getNumericCellValue}")

          }
          case _  => "unknown type"
        }
      }
    }

    Ok("he")
  }

}

package services

import com.google.inject.ImplementedBy
import model.Account

import scala.concurrent.Future

/**
  * Created by weizhang on 8/25/16.
  */
@ImplementedBy(classOf[AccountServiceImpl])
trait AccountService {
  def all : Future[Seq[Account]]
}

package domain

/**
  * Created by weizhang on 8/26/16.
  */
object TradeType {
  sealed trait EnumVal
  case object WithDrawal extends EnumVal
  case object Deposit extends EnumVal
}

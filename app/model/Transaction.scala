package model

import java.time.LocalDateTime


import domain.TradeType.EnumVal

/**
  * Created by weizhang on 12/20/16.
  */
case class Transaction (
  dateTime: LocalDateTime,
  description: String,
  tradeType: EnumVal,
  amount: BigDecimal

)

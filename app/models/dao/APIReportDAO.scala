package models.dao
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
import models.APIReport
class APIReportDAO extends MongoSalatDAO[APIReport,String](APIReport.getTableName)
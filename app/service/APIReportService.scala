package service
import models.dao.APIConfigDAO
import models.APIReport

trait APIReportService {
  
	def getListAPIReport() : List[APIReport]
}

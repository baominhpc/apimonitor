package service.impl

import scala.collection.mutable.ListBuffer
import models.APIOperation
import models.APIParameter

import service.AbstractService
import service.APIReportService
import models.APIReport

class APIReportServiceImpl extends  APIReportService with AbstractService {
  
  def getListAPIReport(): List[APIReport]={
    return apiReportDAO.findAll()
  }
}
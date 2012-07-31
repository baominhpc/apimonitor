package util
import play.Play

object ConfigUtils {
  val ENV: String = Play.application.configuration.getString("env");
  val API_DEFAULT_HOST: String = Play.application.configuration.getString(ENV + ".api.default.host");
  val API_DEFAULT_PORT: Int = Play.application.configuration.getInt(ENV + ".api.default.port");
  val API_DEFAULT_PATH: String = Play.application.configuration.getString(ENV + ".api.default.path");
  var CURRENT_VERSION = "20120703"
    
  val RAWDATA_DIR_PATH: String = Play.application.configuration.getString(ENV + ".rawdata.dir.path");
  val DATA_DIR_PATH: String = Play.application.configuration.getString(ENV + ".data.dir.path");
}

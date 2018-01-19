package downloadmanager.utilities

case object StartDownload
case object FtpHttpDownload
case class SuccessResponse(msg:String)
case class InitiateDownload(url:String,fileName:String)
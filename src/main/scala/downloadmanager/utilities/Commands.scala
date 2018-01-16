package downloadmanager.utilities

case object StartHttpDownload
case class SuccessResponse(msg:String)
case class InitiateHttpDownload(url:String,fileName:String)
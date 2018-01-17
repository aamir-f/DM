package downloadmanager.utilities

case class StartHttpDownload(url:String,fileName:String)
case class SuccessResponse(msg:String)
case class InitiateHttpDownload(url:String,fileName:String)
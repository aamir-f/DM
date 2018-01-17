/*
import java.util.concurrent.TimeUnit

import akka.actor.Props
import org.joda.time.DateTime

import scala.concurrent.duration.Duration

class DeviceMissCallingActor(obj: TwoWaySerialComm2) extends  {
  var MAX_RETRIES = ZERO
  receiver {
    case cmd: InitiateDeviceMissCallProcess => {
      callTheNumber(cmd.devisePhoneNumber)
      context.system.scheduler.scheduleOnce(Duration.create(5, TimeUnit.SECONDS), self, CheckForMissCallWorking(cmd.devisePhoneNumber, cmd.topicId))
    }
    case cmd: CheckForMissCallWorking => {
      if (MAX_RETRIES > 6) {
        val errMsg = "failed to  call devise after multiple retries,contact system admin"
        val topicId = s"deviceStatus_${cmd.topicId}"
        sendToWS(topicId, ConnectToDevice(None, None, Some(MessageBody(errMsg, DateTime.now()))))
        context.stop(self)
        self ! PoisonPill
      } else {
        val result = obj.getStreamFinalString
        val matchString = "OK"
        val processString = processMissCallString(matchString, result.trim, cmd.devicePhoneNumber, cmd.topicId)
        if (processString) {
          println("LOGGER ==> miss call attempt successful")
          println("LOGGER ==> disconnecting the call")
          val callDisconnectActorRef = ActorSystemContainer.system.actorOf(Props(classOf[CallDisconnectActor], obj))
          context.system.scheduler.scheduleOnce(Duration.create(20, TimeUnit.SECONDS), callDisconnectActorRef, InitiateCallDisconnect(cmd.topicId))
          self ! PoisonPill
        } else {
          MAX_RETRIES = MAX_RETRIES + 2
          self ! InitiateDeviceMissCallProcess(cmd.devicePhoneNumber, cmd.topicId)
        }
      }
    }

      context.stop(self)
        self ! PoisonPill
  }*/
+
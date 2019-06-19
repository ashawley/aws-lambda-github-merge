package prs

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.amazonaws.services.lambda.runtime.Context

import scala.collection.JavaConverters._

/**
 * AWS Lambda interface for Java
 */
trait LambdaApp {

  /**
   * Handlers
   */
  def handleRequest(request: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent


  def cleanUp() = {}

  /**
   * Trimmed down example from
   * http://developer.github.com/v3/activity/events/types/#pullrequestevent
   */
  def pullRequestEvent: String =
    """{
      |  "action": "opened",
      |  "number": 1,
      |  "pull_request": {
      |    "state": "open",
      |    "head": {
      |      "ref": "changes",
      |      "sha": "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
      |      "repo": {
      |        "name": "public-repo",
      |        "owner": {
      |          "login": "baxterthehacker"
      |        }
      |      }
      |    },
      |    "base": {
      |      "ref": "master",
      |      "repo": {
      |        "name": "public-repo",
      |        "owner": {
      |          "login": "baxterthehacker"
      |        }
      |      }
      |    }
      |  }
      |}
      |""".stripMargin

  /**
   * Trimmed down example from
   * https://developer.github.com/v3/activity/events/types/#pushevent
   */
  val pushEvent: String =
    """{
      |  "ref": "refs/heads/changes",
      |  "before": "9049f1265b7d61be4a8904a9a27120d2064dab3b",
      |  "after": "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
      |  "commits": [
      |  ],
      |  "head_commit": {
      |    "id": "0d1a26e67d8f5eaf1f6ba5c57fc3c7d91ac0fd1c",
      |    "tree_id": "f9d2a07e9488b91af2641b26b9407fe22a451433"
      |  },
      |  "repository": {
      |    "name": "public-repo",
      |    "full_name": "baxterthehacker/public-repo",
      |    "owner": {
      |      "name": "baxterthehacker"
      |    },
      |  },
      |  "pusher": {
      |    "name": "baxterthehacker"
      |  },
      |  "sender": {
      |    "login": "baxterthehacker"
      |  }
      |}""".stripMargin

  /**
   * Driver for testing handler locally
   */
  def main(args: Array[String]): Unit = {

    val headers = Map("X-GitHub-Event" -> "push")
    val request = new APIGatewayProxyRequestEvent
    request.setBody(pushEvent)
    request.setHeaders(headers.asJava)

    val uuid = java.util.UUID.randomUUID
    val version = "$LATEST"
    org.apache.log4j.MDC.put("AWSRequestId", uuid.toString)

    println(s"START RequestId: $uuid Version: $version")

    val t0 = System.nanoTime
    var result = new APIGatewayProxyResponseEvent
    try {
      val nullContext = new com.amazonaws.services.lambda.runtime.Context {
        def getAwsRequestId(): String = ???
        def getClientContext(): com.amazonaws.services.lambda.runtime.ClientContext = ???
        def getFunctionName(): String = ???
        def getFunctionVersion(): String = ???
        def getIdentity(): com.amazonaws.services.lambda.runtime.CognitoIdentity = ???
        def getInvokedFunctionArn(): String = ???
        def getLogGroupName(): String = ???
        def getLogStreamName(): String = ???
        def getLogger(): com.amazonaws.services.lambda.runtime.LambdaLogger = ???
        def getMemoryLimitInMB(): Int = ???
        def getRemainingTimeInMillis(): Int = ???
      }
      result = handleRequest(request, nullContext)
    } catch {
      case e: Throwable => e.printStackTrace
    } finally {
      cleanUp()
    }
    println(s"END RequestId: $uuid")

    val t1 = System.nanoTime
    val duration = (t1 - t0) / 1e6
    val billedDuration = scala.math.ceil(duration / 100d).toLong * 100

    val env = Runtime.getRuntime
    val memorySize = env.totalMemory / 1024 / 1024
    val memoryUsed = (env.totalMemory - env.freeMemory) / 1024 / 1024

    println(s"""REPORT RequestId: $uuid
                 |  Duration: ${duration} ms
                 |  Billed Duration: ${billedDuration} ms
                 |  Memory Size: ${memorySize} MB
                 |  Max Memory Used: ${memoryUsed} MB""".stripMargin
      .replaceAll("\n", ""))

    println(result.toString)
  }

  def escString(s: String) =
    "\"" + s.replaceAll("\"", "\\\"") + "\""
}

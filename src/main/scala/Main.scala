import akka._
import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent._
import scala.util._

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("demo")
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val graph = RunnableGraph.fromGraph {
    GraphDSL.create() { implicit builder =>

      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))

      val source = Source(1 to 10)

      val sink1 = Sink.foreach { x: Int =>
        Thread.sleep(1000)
        println(x)
      }.async

      val sink2 = Sink.foreach(println)

      source ~> broadcast.in

      broadcast.out(0) ~> sink1
      broadcast.out(1) ~> sink2

      ClosedShape
    }
  }

  graph.run()

  sys.addShutdownHook {
    system.terminate()
  }

}

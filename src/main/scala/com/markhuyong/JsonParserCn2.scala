package com.markhuyong

import org.apache.commons.io.FileUtils
import java.io.File
import net.liftweb.common.Loggable
import net.liftweb.json._
import scala._
import scala.Predef._
import scala.collection.generic.CanBuildFrom

object JsonParserCn2 extends App with Loggable {
  val getCurrentDirectory = new java.io.File(".").getCanonicalPath
  logger.debug(s"CurrentDirectory:${getCurrentDirectory}")

  val fileName = "./src/main/resources/location_chs.json" //allunivlist.json
//val fileName = "./src/main/resources/allunivlist.json"
  logger.debug(s"fileName: ${fileName}")
  val lines = scala.io.Source.fromFile(fileName).mkString

  //  System.out.println(lines)

  val utf8 = "UTF-8"

  /**
   * read file from js source file
   */
  val jsonstr = FileUtils readFileToString(getFile(fileName), utf8)


  val outfileName = "./src/main/scala/com/markhuyong/Location_chs.json"
  logger.debug(s"outfileName: ${outfileName}")


  implicit val formats = DefaultFormats

  //  case class City(name: String)
  //  case class State(name: String, citys:City *)
  //  case class Country(name: String,states: State *)
  //  case class World(countrys: Country *)

  case class Row(key: String, value: String)

  case class City(city: Row)

  case class State(state:Row, cities:List[City])

  case class Country(country: Row,states:List[ State])

  case class World(world:Row, countries: List[Country])

//  val js = Stringify(Run(jsonstr).toJsCmd).toString()
//  logger.debug("js:"+js)
//  val json = parse(jsonstr).asInstanceOf[JObject]
  val json = parse(jsonstr)
  val values = json.values
//  val res = values match {
//    case coun: Map[String, _] => logger.debug(s"country_name:${coun.get("n")}"); coun.get("n")
//    case _ => logger.debug(s"error country"); JNothing
//  }

//  json
 logger.debug("json1" + json)
//  val test= json.children.map {
//    v =>
//      logger.debug("child=" + v.values)
//      v.values
//
//  }

  //get country
  val country_name = "ALA" :: "AFG" :: Nil

  def getCountry(countrys:List[String]):Map[String,JValue] = countrys match {
    case head :: Nil  => Map(head -> json \ head)
    case head :: tail => Map(head -> json \ head) ++ getCountry(tail)
  }

//    def getCountry(country:Seq[String] ) = json \ country

  val couns= getCountry(country_name)

  couns map { c =>

    logger.debug(s"couns:name=${c._1}:${compactRender(c._2)}")
  }

  val alastr =
    """
      |{"ALA": {
      |    "0": {
      |        "0": {
      |            "n": "othercity"
      |        },
      |        "n": "otherstate"
      |    },
      |    "n": "奥兰群岛"
      |}, "CHL": {
      |    "0": {
      |        "n": "other_state",
      |        "AR": {
      |            "n": "阿劳卡尼亚大区"
      |        },
      |        "AT": {
      |            "n": "阿塔卡马大区"
      |        },
      |        "AN": {
      |            "n": "安托法加斯塔大区"
      |        },
      |        "BI": {
      |            "n": "比奥比奥大区"
      |        },
      |        "LI": {
      |            "n": "复活节岛"
      |        },
      |        "LL": {
      |            "n": "湖大区"
      |        },
      |        "CO": {
      |            "n": "科金博大区"
      |        },
      |        "ML": {
      |            "n": "马乌莱大区"
      |        },
      |        "MA": {
      |            "n": "麦哲伦-智利南极大区"
      |        },
      |        "RM": {
      |            "n": "圣地亚哥"
      |        },
      |        "TA": {
      |            "n": "塔拉帕卡大区"
      |        },
      |        "VS": {
      |            "n": "瓦尔帕莱索大区"
      |        },
      |        "AI": {
      |            "n": "伊瓦涅斯将军的艾森大区"
      |        }
      |    },
      |     "1": {
      |        "n": "other_state1",
      |        "AR": {
      |            "n": "阿劳卡尼亚大区1"
      |        },
      |        "AT": {
      |            "n": "阿塔卡马大区1"
      |        }
      |    },
      |    "n": "智利"
      |}}
    """.stripMargin

  val zl = Row("CHL","智利")
  val zg = Row("1","中国")
  //  getAllCountry
  val globle_key = "n"
  val alajson = parse(alastr)
//  val alajvalue = JObject(List(JField("ALA",JObject(List(JField(0,JObject(List(JField(0,JObject(List(JField(n,JString())))), JField(n,JString())))), JField(n,JString(奥兰群岛)))))))

  logger.debug("alajson:: "+ alajson)

  def getAllCountry = alajson match {
    case JObject(cs) => cs map {
      case JField(country_key, JObject(countries)) => {
        logger.debug("CountryKey:" + country_key)
        countries.map {
          case JField(count_key, JString(country_name)) => logger.debug("CountryName:" + country_name)
          case JField(_, JObject(states)) => {
            states.map {
              case JField(state_key, JString(state_name)) => logger.debug("StateName:" + state_name)
              case JField(_, JObject(cities)) => {
                cities.map {
                  case JField(city_key, JString(city_name)) => logger.debug("CityName:" + city_name)
                  case _ => logger.debug("parse cities meet errors:")
                }
              }
              case _ => logger.debug("parse states meet errors:")
            }
          }
          case _ => logger.debug("parse countries meet errors:")
        }
      }
      case _ => logger.debug("parse world meet errors:")
    }
    case _ => logger.debug("parse json meet errors:")
  }

  def getAllCountryName = json match {
    case JObject(cs) => cs map {
      case JField(country_key, JObject(_)) => {

        val c = (country_key, json \ country_key \ globle_key values)
        logger.debug(s"country_key=${c._1}, country_name=${c._2}")
      }
      case _ => ???
    }
    case _ => ???
  }
  getAllCountryName

  def getAllCountryName2 = alajson.children map {
      case JField(country_key, JObject(_)) => {

        val c = (country_key, alajson \ country_key \ globle_key values)
        logger.debug(s"country_key=${c._1}, country_name=${c._2}")
      }
    }
  getAllCountryName2

  def getAllCountryO3 = json match {
    case JObject(cs) => val world = cs collect {
      case JField(country_key, JObject(country)) => {
        val country_name = json \ country_key \ globle_key values
        val states  = country collect {
          case JField(state_key,  JObject(state)) =>{
            val state_name = json \ country_key \ state_key \ globle_key values
            val stat = json \ country_key children

            logger.debug(s"country:wo3:${stat  toString}")
            val cities = state collect {
              case JField(city_key,JObject(city)) =>
                val city_name =  json \ country_key \ state_key \ city_key \ globle_key values

                logger.debug("wo3:city_name:" + city_name)
                 City(Row(city_key, city_name toString))
            }
            State(Row(state_key, state_name toString),cities)
          }
        }
         Country(Row(country_key, country_name toString),states)
      }
    }
    World(Row("world", "earth"), world)
   case _ => logger.debug("parse json meet errors:");???
  }

  val wo3 = getAllCountryO3
  logger.debug("wo3:" + wo3)

   val getzl3 = wo3.countries.groupBy(_.country).get(zl)

  logger.debug("getzl3:" + getzl3)

  val getzg3 = wo3.countries.groupBy(_.country).get(zg)

  logger.debug("getzg3:" + getzg3)
//  def getAllCountryP = getCountry(country_name) map {
//    case c  =>logger.debug("allparser:" + c)
//    case _ =>
//  }
//
//  getAllCountryP

  //JField(IRN,JObject(List(JField(0,JObject(List(JField(0,JObject(List(JField(n,JString())))), JField(n,JString())))), JField(n,JString(伊朗)))))

  import net.liftweb.json.JsonAST._
  import net.liftweb.json.Extraction._
  import net.liftweb.json.Printer._

//    val toJson = compact(render(decompose(json)))
  //val toJson = decompose(test.head.asInstanceOf[Map[String,_]])
//  val head = test.head
//  val toJson = decompose(test.head)
//  logger.debug("toJson: " + toJson)
  //  val test = json.children.map {
  //    cou => cou.children map { s => s.children map { c =>
  //      logger.debug("child_city=" + c )
  //      c
  //    }

  //
  //    }
  //
  //  }

//  logger.debug(s"json: ${json}")

//  logger.debug(s"json.values: ${values}")

  //  case class Fields(country_id: String,(: Double, field3: Boolean)

  //  val result = json.extract[World]
  //val result = json.extract[Map[String,Country]]

  /**
   * output scala source file
   */
//    FileUtils write(getFile(outfileName), jsonstr, utf8)
//    logger.debug(result)

  // standard using block definition
  def using[X <: {def close()}, A](resource: X)(f: X => A) = {
    try {
      f(resource)
    } finally {
      resource.close()
    }
  }

  def getFile(filename: String): File = {
    logger.debug(s"filename = ${filename}")
    val file = new File(s"${filename}")
    if (!file.exists()) file.createNewFile()
    file
  }
  def partition[X,A,B,CC[X] <: Traversable[X], To, To2](xs : CC[X])(f : X => Either[A,B])(
       implicit cbf1 : CanBuildFrom[CC[X],A,To], cbf2 : CanBuildFrom[CC[X],B,To2]) : (To, To2) = {
       val left = cbf1()
       val right = cbf2()
       xs.foreach(f(_).fold(left +=, right +=))
       (left.result(), right.result())
     }
}
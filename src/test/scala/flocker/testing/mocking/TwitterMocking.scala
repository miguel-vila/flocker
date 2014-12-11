package flocker.testing.mocking

import java.text.SimpleDateFormat
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import twitter4j.{ResponseList, Status, Twitter}

/**
 * Created by mglvl on 10/23/14.
 */
trait TwitterMocking extends MockitoSugar {

  val sf = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy")
  sf.setLenient(true)

  def mockStatus(dateString: String, text: String, isRetweeted: Boolean): Status = {
    val st = mock[Status]
    val d = sf.parse(dateString)
    when(st.getCreatedAt()).thenReturn(d)
    when(st.getText()).thenReturn(text)
    when(st.isRetweeted()).thenReturn(isRetweeted)
    st
  }

  def mockResponse(statuses: Status*): ResponseList[Status] = {
    val response = mock[ResponseList[Status]]
    var i = 0
    statuses.foreach { s =>
      when(response.get(i)).thenReturn(s)
      i+=1
    }
    when(response.size()).thenReturn(statuses.length)
    response
  }

  def mockTwitterWithStatuses(statuses: Status*): Twitter = {
    val twitter = mock[Twitter]
    val response = mockResponse(statuses:_*)
    when(twitter.getUserTimeline(any(classOf[String]))).thenReturn(response)
    twitter
  }

  def mockTwitterWithSomeStatuses(): Twitter = {
    val d1 = "Fri Oct 24 01:46:11 +0000 2014"
    val t1 = "Pidamos que el magnicidio de Alvaro Gomez sea declarado de lesa humanidad\nPor lo menos así tendremos la ilusión d q habrá algún día justicia"
    val rt1 = false
    val st1 = mockStatus(d1,t1,rt1)

    val d2 = "Thu Oct 23 01:33:33 +0000 2014"
    val t2 = "RT @RedMasNoticias: Ex Presidentes, a excepción de @ernestosamperp, piden que crimen de Álvaro Gómez sea declarado de lesa humanidad. #MásI…"
    val rt2 = false
    val st2 = mockStatus(d2,t2,rt2)

    val d3 = "Thu Oct 23 01:09:46 +0000 2014"
    val t3 = "RT @Las2Orillas: .@CLOPEZanalista y @PalomaValenciaL las duras del congreso que incomodan a @JuanManSantos | http://t.co/DDkmFbBf9S http://…"
    val rt3 = false
    val st3 = mockStatus(d3,t3,rt3)

    val d4 = "Wed Oct 22 21:55:34 +0000 2014"
    val t4 = "RT @eduardomackenz1: Ejército frustra ataques terroristas de las Farc contra acueductos en Huila y Meta http://t.co/lYREt8xBlw"
    val rt4 = false
    val st4 = mockStatus(d4,t4,rt4)

    mockTwitterWithStatuses(st1,st2,st3,st4)
  }

}

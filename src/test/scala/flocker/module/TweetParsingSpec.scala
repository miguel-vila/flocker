package flocker.module

import flocker.model.{BeforePhraseStart, AfterPhraseEnd, Word}
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by mglvl on 10/21/14.
 */
class TweetParsingSpec extends FlatSpec with Matchers with TweetParsing {

  val t1 = "RT @SRV18: 2. NEXOS de @ANNCOL2 y TERRORISTAS FARC. Correos de \" Ra\u00fal Reyes\". @_El_Patriota @FBI @TheJusticeDept @FBIMiamiFL http://t.co/oc\u2026"
  val t2 = "Esto es progreso. Inversi\u00f3n. Empleo. Impuestos. Bienes p\u00fablicos de calidad. Cero demagogia. Capitalismo puro. http://t.co/BMPaTNCqsp"

  "El módulo TweetParsing" should "partir correctamente un tweet en palabras" in {
    splitIntoWords(t1) should equal (Stream(BeforePhraseStart,Word("RT"),Word("@SRV18:"),Word("2."),Word("NEXOS"),Word("de"),Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS"),Word("FARC."),Word("Correos"),Word("de"),Word("\""),Word("Ra\u00fal"),Word("Reyes\"."),Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL"),Word("http://t.co/oc\u2026"),AfterPhraseEnd))
    splitIntoWords(t2) should equal (Stream(BeforePhraseStart,Word("Esto"),Word("es"),Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos"),Word("de"),Word("calidad."),Word("Cero"),Word("demagogia."),Word("Capitalismo"),Word("puro."),Word("http://t.co/BMPaTNCqsp"),AfterPhraseEnd))
  }

}
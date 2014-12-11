package flocker.model

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by mglvl on 10/21/14.
 */
class TokensSpec extends FlatSpec with Matchers {

  "Un Tokens" should "descartar palabras inválidas" in {
    val s1 = Stream(BeforePhraseStart,Word("RT"),Word("@SRV18:"),Word("2."),Word("NEXOS"),Word("de"),Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS"),Word("FARC."),Word("Correos"),Word("de"),Word("\""),Word("Ra\u00fal"),Word("Reyes\"."),Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL"),Word("http://t.co/oc\u2026"),AfterPhraseEnd)
    Tokens(s1).filterOutInvalidWords should equal (Tokens(Stream(BeforePhraseStart,Word("@SRV18:"),Word("2."),Word("NEXOS"),Word("de"),Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS"),Word("FARC."),Word("Correos"),Word("de"),Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL"),AfterPhraseEnd)))

    val s2 = Stream(BeforePhraseStart,Word("Esto"),Word("es"),Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos"),Word("de"),Word("calidad."),Word("Cero"),Word("demagogia."),Word("Capitalismo"),Word("puro."),Word("http://t.co/BMPaTNCqsp"),AfterPhraseEnd)
    Tokens(s2).filterOutInvalidWords should equal (Tokens(Stream(BeforePhraseStart,Word("Esto"),Word("es"),Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos"),Word("de"),Word("calidad."),Word("Cero"),Word("demagogia."),Word("Capitalismo"),Word("puro."),AfterPhraseEnd)))
  }

  it should "computar los trigramas correctamente" in {
    val s1 = Stream(BeforePhraseStart,Word("@SRV18:"),Word("2."),Word("NEXOS"),Word("de"),Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS"),Word("FARC."),Word("Correos"),Word("de"),Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL"),AfterPhraseEnd)
    Tokens(s1).trigrams should equal(Stream(
      Trigram(BeforePhraseStart,Word("@SRV18:"),Word("2.")),
      Trigram(Word("@SRV18:"),Word("2."),Word("NEXOS")),
      Trigram(Word("2."),Word("NEXOS"),Word("de")),
      Trigram(Word("NEXOS"),Word("de"),Word("@ANNCOL2")),
      Trigram(Word("de"),Word("@ANNCOL2"),Word("y")),
      Trigram(Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS")),
      Trigram(Word("y"),Word("TERRORISTAS"),Word("FARC.")),
      Trigram(Word("TERRORISTAS"),Word("FARC."),Word("Correos")),
      Trigram(Word("FARC."),Word("Correos"),Word("de")),
      Trigram(Word("Correos"),Word("de"),Word("Ra\u00fal")),
      Trigram(Word("de"),Word("Ra\u00fal"),Word("Reyes.")),
      Trigram(Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota")),
      Trigram(Word("Reyes."),Word("@_El_Patriota"),Word("@FBI")),
      Trigram(Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept")),
      Trigram(Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL")),
      Trigram(Word("@TheJusticeDept"),Word("@FBIMiamiFL"),AfterPhraseEnd)
    ))

    val s2 = Stream(BeforePhraseStart,Word("Esto"),Word("es"),Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos"),Word("de"),Word("calidad."),Word("Cero"),Word("demagogia."),Word("Capitalismo"),Word("puro."),AfterPhraseEnd)
    Tokens(s2).trigrams should equal(Stream(
      Trigram(BeforePhraseStart,Word("Esto"),Word("es")),
      Trigram(Word("Esto"),Word("es"),Word("progreso.")),
      Trigram(Word("es"),Word("progreso."),Word("Inversi\u00f3n.")),
      Trigram(Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo.")),
      Trigram(Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos.")),
      Trigram(Word("Empleo."),Word("Impuestos."),Word("Bienes")),
      Trigram(Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos")),
      Trigram(Word("Bienes"),Word("p\u00fablicos"),Word("de")),
      Trigram(Word("p\u00fablicos"),Word("de"),Word("calidad.")),
      Trigram(Word("de"),Word("calidad."),Word("Cero")),
      Trigram(Word("calidad."),Word("Cero"),Word("demagogia.")),
      Trigram(Word("Cero"),Word("demagogia."),Word("Capitalismo")),
      Trigram(Word("demagogia."),Word("Capitalismo"),Word("puro.")),
      Trigram(Word("Capitalismo"),Word("puro."),AfterPhraseEnd)
    ))
  }

  it should "filtrar los trigramas correctamente" in {
    val s1 = Stream(BeforePhraseStart,Word("@SRV18:"),Word("2."),Word("NEXOS"),Word("de"),Word("@ANNCOL2"),Word("y"),Word("TERRORISTAS"),Word("FARC."),Word("Correos"),Word("de"),Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept"),Word("@FBIMiamiFL"),AfterPhraseEnd)
    Tokens(s1).validTrigrams should equal(Stream(
      Trigram(Word("2."),Word("NEXOS"),Word("de")),
      Trigram(Word("y"),Word("TERRORISTAS"),Word("FARC.")),
      Trigram(Word("Correos"),Word("de"),Word("Ra\u00fal")),
      Trigram(Word("de"),Word("Ra\u00fal"),Word("Reyes."))
    ))

    val s2 = Stream(BeforePhraseStart,Word("Esto"),Word("es"),Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo."),Word("Impuestos."),Word("Bienes"),Word("p\u00fablicos"),Word("de"),Word("calidad."),Word("Cero"),Word("demagogia."),Word("Capitalismo"),Word("puro."),AfterPhraseEnd)
    Tokens(s2).validTrigrams should equal (Stream(
      Trigram(BeforePhraseStart,Word("Esto"),Word("es")),
      Trigram(Word("Esto"),Word("es"),Word("progreso.")),
      Trigram(Word("Bienes"),Word("p\u00fablicos"),Word("de")),
      Trigram(Word("p\u00fablicos"),Word("de"),Word("calidad.")),
      Trigram(Word("Capitalismo"),Word("puro."),AfterPhraseEnd)
    ))
  }

}

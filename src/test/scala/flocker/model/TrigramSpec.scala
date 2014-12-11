package flocker.model

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by mglvl on 10/21/14.
 */
class TrigramSpec extends FlatSpec with Matchers {

  "Un trigrama" should "identificar correctamente particiones de frases" in {
    Trigram(Word("y"),Word("TERRORISTAS"),Word("FARC.")).splitsAPhrase should be (false)
    Trigram(Word("TERRORISTAS"),Word("FARC."),Word("Correos")).splitsAPhrase should be (true)
    Trigram(Word("FARC."),Word("Correos"),Word("de")).splitsAPhrase should be (true)
    Trigram(Word("de"),Word("Ra\u00fal"),Word("Reyes.")).splitsAPhrase should be (false)
    Trigram(Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota")).splitsAPhrase should be (true)
    Trigram(Word("Reyes."),Word("@_El_Patriota"),Word("@FBI")).splitsAPhrase should be (true)

    Trigram(Word("Esto"),Word("es"),Word("progreso.")).splitsAPhrase should be (false)
    Trigram(Word("es"),Word("progreso."),Word("Inversi\u00f3n.")).splitsAPhrase should be (true)
    Trigram(Word("progreso."),Word("Inversi\u00f3n."),Word("Empleo.")).splitsAPhrase should be (true)
    Trigram(Word("Cero"),Word("demagogia."),Word("Capitalismo")).splitsAPhrase should be (true)
  }

  it should "identificar correctamente las particiones de frases a su inicio" in {
    Trigram(BeforePhraseStart,Word("2."),Word("NEXOS")).splitsAPhrase should be (false)
  }

  it should "identificar correctamente las particiones de frases a su final" in {
    Trigram(Word("Ra\u00fal"),Word("Reyes."),AfterPhraseEnd).splitsAPhrase should be (false)
  }

  it should "identificar correctamente enumeraciones que no representan particiones de frases" in {
    Trigram(Word("@SRV18:"),Word("2."),Word("NEXOS")).splitsAPhrase should be (true)
    Trigram(Word("2."),Word("NEXOS"),Word("de")).splitsAPhrase should be (false)
  }

  it should "identificar correctamente menciones" in {
    Trigram(BeforePhraseStart,Word("2."),Word("NEXOS")).containsAMention should be (false)
    Trigram(Word("Ra\u00fal"),Word("Reyes."),AfterPhraseEnd).containsAMention should be (false)
    Trigram(Word("Ra\u00fal"),Word("Reyes."),Word("@_El_Patriota")).containsAMention should be (true)
    Trigram(Word("@_El_Patriota"),Word("@FBI"),Word("@TheJusticeDept")).containsAMention should be (true)
    Trigram(Word("Esto"),Word("es"),Word("progreso.")).containsAMention should be (false)
  }

}

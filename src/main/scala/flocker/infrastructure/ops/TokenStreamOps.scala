package flocker.infrastructure.ops

import flocker.model.{Word, Token}
import scalaz.std.stream.streamSyntax._

/**
 * Created by mglvl on 12/14/14.
 */
final class TokenStreamOps(self: Stream[Token]) {

  final def concatenateToLessThan140Chars(): String = {
    def toString(token: Token): String = {
      token match {
        case Word(word) => word
        case _ => ""
      }
    }
    self.map(toString).intersperse(" ").scanLeft("")(_+_).takeWhile(_.length<=140).last.trim
  }

}

trait ToTokenStreamOps {
  implicit def ToTokenStreamOps(self: Stream[Token]): TokenStreamOps = new TokenStreamOps(self)
}

package site.kirimin_chan.board.exceptions

import java.lang.Exception

data class TokenCheckException(val token: String): Exception()
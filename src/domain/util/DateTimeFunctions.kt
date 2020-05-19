import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

private val DEF_FMT: DateTimeFormatter = DateTimeFormat.mediumDateTime()
fun toFormattedDateTime(dateTime: DateTime) = DEF_FMT.print(dateTime.toDateTime(DateTimeZone.forID("Asia/Tokyo")))!!

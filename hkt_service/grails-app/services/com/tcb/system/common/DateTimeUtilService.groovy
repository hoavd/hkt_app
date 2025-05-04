package com.tcb.system.common

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.regex.Matcher
import java.util.regex.Pattern

class DateTimeUtilService {
    private static SimpleDateFormat ddMMYYY = new SimpleDateFormat("dd/MM/yyyy")

    def formatStringToDate(String para, String type) {
        SimpleDateFormat format = new SimpleDateFormat(type)
        Date date = format.parse(para)
        return date
    }

    def formatStringToDate(String para, String type, int timezone) {
        SimpleDateFormat format = new SimpleDateFormat(type)
        Date date = format.parse(para)
        return rollHour(date, timezone)
    }

    def formatDateToString(Date date, String type) {
        if (date) {
            SimpleDateFormat format = new SimpleDateFormat(type)
            String strDate = format.format(date)
            return strDate
        } else {
            return ""
        }
    }

    /**
     * TĂNG THÊM N giây
     * */
    static Date rollSecond(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.SECOND, amount)
        return calendar.getTime()
    }

    /**
     * TĂNG THÊM N phút
     * */
    static Date rollMinute(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.MINUTE, amount)
        return calendar.getTime()
    }

    /**
     * TĂNG THÊM N giờ
     * */
    static Date rollHour(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.HOUR, amount)
        return calendar.getTime()
    }

    /**
     * TĂNG THÊM N NGÀY
     * 28/02/2018 + AMOUNT = 2 -> 02/03/2018
     * 02/03/2018 + AMOUNT = -2 -> 28/02/2018
     */
    static Date rollDate(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.DATE, amount)
        return calendar.getTime()
    }

    /**
     * TĂNG THÊM N THÁNG
     * 15/01/2018 + AMOUNT = 3 -> 15/04/2018
     * 15/04/2018 + AMOUNT = -3 -> 15/01/2018
     */
    static Date rollMonth(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.MONTH, amount)
        return calendar.getTime()
    }

    /**
     * TĂNG THÊM N NĂM
     * 15/01/2018 + AMOUNT = 3 -> 15/01/2021
     * 15/01/2018 + AMOUNT = -3 -> 15/01/2015
     */
    static Date rollYear(Date date, int amount) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.add(Calendar.YEAR, amount)
        return calendar.getTime()
    }

    /**
     * LẤY NGÀY ĐẦU TIÊN CỦA NĂM
     * 15/08/2018 -> RETURN 01/01/2018
     */
    static Date getStartOfYear(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    /**
     * LẤY NGÀY CUỐI CÙNG CỦA NĂM
     * 25/02/2018 -> RETURN 31/12/2018
     */
    static Date getEndOfYear(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.lastDayOfYear()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    /**
     * LẤY NGÀY ĐẦU TIÊN CỦA THÁNG
     * 15/01/2018 -> RETURN 01/01/2018
     */
    static Date getStartOfMonth(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    /**
     * LẤY NGÀY CUỐI CÙNG CỦA THÁNG
     * 25/02/2018 -> RETURN 28/02/2018
     */
    static Date getEndOfMonth(Date date) {
        return Date.from(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    /**
     * TÍNH SỐ THÁNG GIỮA KHOẢNG THỜI GIAN
     * VÍ DỤ: START 15/01/2018, END 25/03/2018 -> RETURN 3
     * VÍ DỤ: START 15/01/2018, END 10/03/2018 -> RETURN 2
     * VÍ DỤ: START 15/01/2018, END 10/02/2018 -> RETURN 1
     * VÍ DỤ: START 15/01/2018, END 25/01/2018 -> RETURN 0
     */
    static int getMonths(Date startDate, Date endDate) {
        int months = 0
        /** CÙNG THÁNG **/
        if (getStartOfMonth(startDate).compareTo(getStartOfMonth(endDate)) == 0) {
            return months
        }
        while (startDate.compareTo(endDate) < 0) {
            ++months
            startDate = rollMonth(startDate, 1)
        }
        return months
    }

    /**
     * LẤY BẮT ĐẦU NGÀY HIỆN TẠI
     */
    def getStartOfDay(Date date) {
        return formatStringToDate(formatDateToString(date, "dd/MM/yyyy"), "dd/MM/yyyy")
/*        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.getTime()*/
    }

    /**
     * LẤY KẾT THÚC NGÀY HIỆN TẠI
     */
    static Date getEndOfDay(Date date) {
        GregorianCalendar calendar = new GregorianCalendar()
        calendar.setTime(date)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        return calendar.getTime()
    }

    /**
     * LẤY TỔNG SỐ NGÀY GIỮA 2 NGÀY
     */
    static int getDays(Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return (int) ChronoUnit.DAYS.between(start, end)
    }

    /**
     * LẤY TỔNG SỐ GIÂY GIỮA 2 NGÀY
     */
    static int getSeconds(Date startDate, Date endDate) {
        Long timeDiff = Math.round((startDate.time - endDate.time) / 1000)
        return timeDiff
    }

    /**
     * LẤY THỜI GIAN HIỆN TẠI
     */
    static String currentYYYMMddHHmmss() {
        String result = null
        try {
            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss")
            result = dateFormat.format(new Date())
        } catch (Exception e) {
            e.printStackTrace()
        }
        return result
    }

    static def convertToDate7(def date) {
        return date.getAt(Calendar.YEAR) * 1000 + date.getAt(Calendar.DAY_OF_YEAR)
    }

    static def convertFromDate7(def date) {
        date = date.toInteger()
        def d = new GregorianCalendar()
        d.set(Calendar.YEAR, Math.round(date / 1000i).toInteger())
        d.set(Calendar.DAY_OF_YEAR, date % 1000i)
        def d1 = d.getTime()
        d1.clearTime()
        return d1
    }

    static def convertStringDateToddMMyyyy(def date) {
        if (date == null || date.toString().equals("null")) {
            return ''
        }
        //2019-12-25 16:04:50.974

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s")
        Date strDate = dateFormat.parse(date.toString())
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy")
        return format.format(strDate)
    }

    static def convertDateToddMMyyyy(def date) {
        if (date == null) return ''
        //2019-12-25 16:04:50.974
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy")
        return format.format(date)
    }

    static def compareDate7(def d1, def d2) {
        convertFromDate7(d1) - convertFromDate7(d2)
    }

    static def convertInputToDate7(def date) {
        return convertToDate7(parseInputDate(date))
    }

    static def parseInputDate(def date) {
        return Date.parse("dd/MM/yyyy HH:mm:ss", date)
    }

    static def parseInputShortDate(def date) {
        return Date.parse("dd/MM/yyyy HH:mm:ss", date + " 00:00:00")
    }

    static def parseInputShortDate1(def date) {
        return Date.parse("yyyy-MM-dd hh:mm:ss", date + " 00:00:00")
    }

    static def formatDate(def date) {
        if (!date) return ""
        return date.format('dd/MM/yyyy')
    }

    static def formatDateNotDate(def date) {
        if (!date) return ""
        return date.format('MM/yyyy')
    }

    static def formatTime(def date) {
        return date.getAt(date.getHours()) + ':' + (date.getMinutes())
    }

    static def formatDetailDate(def date) {
        return formatDateTime(date)
    }

    static def formatDateTime(def date) {
        if (!date) return ""
        return date.format('dd/MM/yyyy HH:mm')
    }

    static def date2String(def date) {
        if (!date) return ""
        return ddMMYYY.format(date)
    }

    static def formatDateTimeFromMessageCode(def date) {
        if (!date) return ""
        return ddMMYYY.format(date)
    }

    static def getTimeDifference(Date fromDate, Date toDate) {
        Long timeDiff = toDate.time - fromDate.time
        return timeDiff
    }

    static boolean checkDate(String date) {
        String DATE_PATTERN = '(0?[1-9]|[12]\\d|3[01])[\\/\\-.](0?[1-9]|[12]\\d|3[01])[\\/\\-.](\\d{2}|\\d{4})'
        Pattern pattern = Pattern.compile(DATE_PATTERN)
        boolean result = false;

        Matcher matcher = pattern.matcher(date);

        if (matcher.matches()) {

            // it is a valid date format yyyy-mm-dd
            // assign true first, later we will check the leap year and odd or even months
            result = true;

            // (?:19|20), match but don't capture it, otherwise it will messy the group order
            // for example, 2020-2-30, it will create 4 groups.
            // group(1) = 2020, group(2) matches (19|20) = 20, group(3) = 2, group(4) = 30
            // So, we put (?:19|20), don't capture this group.
            String day = matcher.group(1);
            // why string? month matches 02 or 2
            String month = matcher.group(2);
            int year = Integer.parseInt(matcher.group(3));

            // 30 or 31 days checking
            // only 1,3,5,7,8,10,12 has 31 days
            if ((month.equals("4") || month.equals("6") || month.equals("9") ||
                    month.equals("04") || month.equals("06") || month.equals("09") ||
                    month.equals("11")) && day.equals("31")) {
                result = false;
            } else if (month.equals("2") || month.equals("02")) {
                if (day.equals("30") || day.equals("31")) {
                    result = false;
                } else if (day.equals("29")) {  // leap year, feb 29 days.
                    if (!isLeapYear(year)) {
                        result = false;
                    }
                }
            }

        }

        return result
    }

    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
    }

    def getDayOfWeek(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE")
            String stringDate = sdf.format(date)
            return stringDate.toUpperCase()
        } catch (Exception e) {
            return ""
        }
    }

}

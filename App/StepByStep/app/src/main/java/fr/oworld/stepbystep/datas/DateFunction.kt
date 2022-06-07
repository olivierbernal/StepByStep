import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//
//  Datefuntion.swift
//  MPP
//
//  Created by Olivier BERNAL on 15/03/17.
//  Copyright © 2017 oworld. All rights reserved.
//

class DateFuntion private constructor() {

    companion object {
        val sharedInstance = DateFuntion()
    }
    val dateFormatter: SimpleDateFormat

    init {
        this.dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        this.dateFormatter.timeZone = TimeZone.getTimeZone("UTC")
    }

    fun shortDateToDisplay(date : Date) : String {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date)
    }
    
    fun shortDateTimeToDisplay(date : Date) : String {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date)
    }
}

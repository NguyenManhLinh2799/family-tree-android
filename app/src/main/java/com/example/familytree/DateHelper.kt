package com.example.familytree

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        fun dateToIso(dateStr: String?): String? {
            if (dateStr == null || dateStr == "") {
                return ""
            }

            val dateFormat = SimpleDateFormat("dd/mm/yyyy")
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

            val date = dateFormat.parse(dateStr)

            return isoFormat.format(date)
        }

        fun isoToDate(iso: String?): String? {
            if (iso == null || iso == "") {
                return ""
            }

            val dateFormat = SimpleDateFormat("dd/mm/yyyy")
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

            val date = isoFormat.parse(iso)

            return dateFormat.format(date)
        }
    }
}
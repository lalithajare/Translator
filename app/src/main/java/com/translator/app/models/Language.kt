package com.translator.app.models

/**
 * 1. This class is a model to encapsulate the data of 'Language Code' and 'Language'
 *
 * @property langCode : This ISO code of a language like -> 'en','ko','vi'.
 * @property language : The actual Language like -> 'English','Korean', 'Vietnamese'.
 */
data class Language(var langCode: String = "en", var language: String)
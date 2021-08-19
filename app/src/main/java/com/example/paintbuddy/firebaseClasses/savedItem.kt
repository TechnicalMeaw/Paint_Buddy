package com.example.paintbuddy.firebaseClasses

class SavedItem(val id: String, val title: String, val thumbUri: String, val lastModified: String, val timeInMillis: String) {
    constructor() : this("", "", "", "", "")
}
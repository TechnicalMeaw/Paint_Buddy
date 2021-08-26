package com.example.paintbuddy.firebaseClasses

class SavedItem(val drawId: String, val userId: String, val title: String, val thumbUri: String, val lastModified: Long, val created: Long) {
    constructor() : this("", "", "", "", -1, -1)
}
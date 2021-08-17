package com.example.paintbuddy.firebaseClasses


class UserItem(val firstName: String,
               val lastName: String,
               val phoneNumber: String,
               val email: String,
               val profileImage: String,
               val countryName: String,
               val uid: String,
               val notificationToken: String,
               val status: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "")
}
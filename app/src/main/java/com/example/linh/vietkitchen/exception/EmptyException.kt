package com.example.linh.vietkitchen.exception

class EmptyException : Exception() {
    override fun toString(): String {
        return "no data found!"
    }
}
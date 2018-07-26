package com.example.linh.vietkitchen.data.cloud

data class Category(var groups: List<Map<String, List<Map<String, Boolean>>>>?){
    constructor(): this(null)
}
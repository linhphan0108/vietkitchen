package com.example.linh.vietkitchen.ui.model

class DrawerNavGroupItem(val headerTile: String, val path: String, val numberItems: Int, val itemsList: List<DrawerNavChildItem>? = null,
                         var isChildrenVisible: Boolean = false)
    : Entity()
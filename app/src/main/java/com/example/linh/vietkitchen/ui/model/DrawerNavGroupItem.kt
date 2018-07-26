package com.example.linh.vietkitchen.ui.model

class DrawerNavGroupItem(val headerTile: String, val itemsList: List<DrawerNavChildItem>,
                         var isChildrenVisible: Boolean = false)
    : Entity()
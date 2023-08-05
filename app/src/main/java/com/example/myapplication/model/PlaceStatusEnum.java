package com.example.myapplication.model;

/**
 * 格子的状态
 */
public enum PlaceStatusEnum {

    /** 不可访问 */
    BLOCKED,

    /** 无棋子 */
    SPACE,

    /** 有棋子 */
    PEG,

    /** 棋子选中 **/
    PEG_SELECTED
}
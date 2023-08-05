# Android 作业：单人跳棋游戏

## 背景

> SAST SUMMER 2023 HOMEWORK for Android

单人跳棋，也被称为孤独跳棋、独粒钻石棋，是一种玩家独自进行的棋类游戏。与传统的跳棋相比，单人跳棋在棋盘上只有一个位置没有棋子，玩家的目标是通过跳跃和消除棋子，最终留下尽可能少的棋子在棋盘上。

## 游戏规则

- 棋子放置：初始时，棋盘上除了中心位置的一个交叉点外，其他交叉点上都会放置一个棋子。

- 移动规则：玩家可以选择一个棋子进行移动。棋子可以沿着棋盘上的线水平或垂直方向移动，但不能斜向移动。当棋子被选择时，其颜色由默认的棕色变为红色，表示被选择；如果你选中之前被选择的红色棋子，则棋子被取消选择，相应地，颜色变成棕色。

- 跳跃规则：玩家可以用一个棋子跳过相邻的另一个棋子，到达该方向的下一个格子，作为“落点”。注意，操作棋子、被跳过的棋子和落点必须是**一条直线**，且落点是一个**空位**；不满足此规则的跳跃将不被执行。被跳过的棋子将被移除。

- 胜负判定：如果在跳跃后，**仍然存在可以跳过的棋子**，玩家可以选择继续跳跃；这样的连续跳跃可以一直进行下去，直到**没有可跳过的棋子为止**。这样的一系列操作算一步。玩家的目标是通过一系列的跳跃，最终只剩下**一个棋子**在棋盘上。最佳的结果是剩下中心位置的那个棋子。若棋盘上没有可跳过的棋子且棋盘剩余棋子数大于1，则游戏失败。此时，玩家可以选择重新玩一局游戏。


## 体验单人跳棋游戏

本项目打包形成`apk`安装包，见[Peg_solitaire.apk](./release/Peg_solitaire.apk)，或本项目的Github Release.

## 作业完成情况

本作业实现了所有`TODO`部分，完成了基本要求。目标Android SDK版本为33，最小SDK版本为24. 本项目后续有待进一步完善、实现作业要求的扩展功能及其他更丰富的功能。
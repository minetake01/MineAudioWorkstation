# MineAudioWorkstation
音ブロックの音域を拡張したり、Minecraftでの音楽制作を補助する機能を追加します。

## Minecraft version
1.20.1

## 前提MOD
- Fabric
- Fabric API

## Features
- pitchプロパティの他にoctaveプロパティを持った`拡張音ブロック`を新規追加。
  configからoctaveの最大値を変更可能。 0なら1オクターブのみ、2なら上下2オクターブ(-2~+3)演奏可能になる。
- `調律棒`というアイテムを追加。これで`拡張音ブロック`を右クリックすることでoctaveを調整できる。
- ゲーム内速度を変更するコマンドを追加。
  - `/playspeed repeater [delay_level] [bpm]`: RSリピーターの遅延レベル(1~4)を指定し、その遅延間隔がbpmになるようにサーバーのティックレートを変更する。
  - `/playspeed multiply [formula]`: 現在のサーバーのティックレートに乗算を行う。formulaは数値と文字列で簡単な式を記述可能。
  - `/playspeed reset`: サーバーのティックレートをデフォルトに戻す。
  - `/movespeed [player] [distance] [bpm]`: 指定したプレイヤーの移動速度を、指定した距離が指定したbpmの1拍分の時間で移動できる速度に変更する。
  - `/movespeed reset [player]`: 指定したプレイヤーの移動速度をデフォルトに戻す。
  - `/keepdirection [player] [direction]`: 指定したプレイヤーの向いている方向を指定した方向に常に強制する。縦方向には動かせる。
  - `/keepdirection purge [player]`: 指定したプレイヤーの視線強制を解除する。
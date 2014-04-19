(ns wyvern.game-view)

(defprotocol T
  (player-id [this])
  (a-legal-move [this])
  (terminal? [this])
  (score [this]))

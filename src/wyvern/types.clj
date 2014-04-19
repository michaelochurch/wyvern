(ns wyvern.types)

(defprotocol Game
  (all-players [this])
  (view [this player-id])
  (legal-actions [this player-id])
  (move [this player-actions])
  (terminal? [this])
  (score [this player-id]))

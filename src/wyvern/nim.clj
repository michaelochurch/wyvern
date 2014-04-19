(ns wyvern.nim
  (:use wyvern.types
        wyvern.utils))

(defrecord State [active stones])

(def ^:const stones-per-turn-limit 3)

(defn nim-move-1 [game-state player action]
  (if (= action :no-op)
    game-state
    (update game-state
            :active #(rem (inc %) 2)
            :stones #(- % action))))

(defn nim-move [game-state player-actions]
  (reduce (fn [state [player action]]
            (nim-move-1 state player action))
          game-state
          player-actions))

(defrecord NimGame [state]
  Game
  ; two players named 0 and 1
  (all-players [this] [0 1])
  ; perfect information -- view is full game state.
  (view [this _] state)
  ; actions are :no-op and {1, 2, 3} (number of stones taken).
  (legal-actions [this player-id]
    (if (= player-id (:active state))
      (range 1 (min (inc stones-per-turn-limit) (:stones state)))
      [:no-op]))
  ; a Nim move is to take stones off the board.
  (move [this player-actions]
    (NimGame. (nim-move state player-actions)))
  ; game ends when no stones are left. 
  (terminal? [this]
    (= (:stones state) 0))
  ; active player at game end is loser (score = 0.0) and other wins (1.0).
  (score [this player-id]
    (if (= (:active state) player-id) 0.0 1.0)))

(defn make-NimGame [& [stones]]
  (let [stones (or stones 16)]
    (NimGame. (State. 0 stones))))

(def Spec
  {:new make-NimGame
   :players 2
   :impl NimGame})

(ns wyvern.nim
  (:require [wyvern.game-state :as state]
            [wyvern.game-view  :as view])
  (:use wyvern.utils))

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

(defrecord NimView [state player-id]
  view/T
  (player-id [this] player-id)
  ;; we can use the game-state methods because it's a perfect information game.
  (a-legal-move [this]
    (state/a-legal-move state player-id))
  (terminal? [this]
             (state/terminal? state))
  (score [this]
         (state/score state player-id)))

(defrecord NimGame [state]
  state/T
  ; two players named 0 and 1
  (all-players [this] [0 1])
  ; perfect information -- view is full game state.
  (view [this player-id] (NimView. this player-id))
  ; actions are :no-op and {1, 2, 3} (number of stones taken).
  (legal-action [this player-id action]
    (if (= player-id (:active state))
      (and (integer? action)
           (>= action 1)
           (<= action stones-per-turn-limit)
           (<= action (:stones state)))
      (= action :no-op)))
  (a-legal-move [this player-id]
    (if (= player-id (:active state))
      (inc (rand-int (min stones-per-turn-limit (:stones state))))
      :no-op))
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
   :n-players [2]
   :impl NimGame
   :view-impl NimView})

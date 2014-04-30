(ns wyvern.examples
  (:use wyvern.utils
        wyvern.defgame))

(defn nim-move [game-state actions]
  (let [{:keys [active-player stones-left]} game-state]
    (assoc game-state
      :active-player (rem (inc active-player) 2)
      :stones-left   (- stones-left (get actions active-player)))))

;; TODO: in practice, it will be rare that core functions like :legal, :move are
;; written inline in the defgame. They'll usually be declared externally. I want
;; to support both forms, e.g.:

;; `` :legal (if (= $player-id active-player) ... ) ``
;; and...
;; `` :legal nim-legal `` ~= `` :legal (nim-legal $game-state $player-id $action) ``

(defgame nim
  :n-players [2]
  :fields  [active-player stones-left]
  :defaults {:active-player 0
             :stones-left   16}
  :view      $game-state           ;; perfect information
  :constants {max-stones 3}
  :legal     (if (= $player-id active-player)
               (and (number? $action)
                    (<= 1 $action (min max-stones stones-left)))
               (= $action :no-op))
  :legal-1   (if (= $player-id active-player)
               (inc (rand-int (min max-stones stones-left)))
               :no-op)
  :move      (nim-move $game-state $actions)
  :terminal? (= stones-left 0)
  :score     (not= $player-id active-player)) ;; true ~= 1.0, false ~= 0.0
                                              ;; this allows a "win condition" to be the API

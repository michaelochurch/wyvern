(ns wyvern.defgame
  (:use wyvern.utils))

(defn keywordize-map [m]
  (into {} (map (fn [[k v]] [(keyword nil (name k)) v]) m)))

(defn nil-valued-map [coll]
  (into {} (map (fn [k] [k nil]) coll)))

(defn double-convert-wrapper [x]
  (cond (number? x)  (double x)
        (true?  x)   1.0
        (false? x)   0.0
        :else (throw (IllegalArgumentException. "type error in double-convert-wrapper"))))

;; NOTE: the game contract requires that :legal and :legal-1 perform identically
;; on views-- returned by (:view <game-state> <player-id>)-- as on the game
;; state. This means that a View must contain enough information that the player
;; can tell if an action is legal.

(defmacro defgame [name & {:keys [n-players constants defaults fields view
                                  legal legal-1 move terminal? score]}]
  (let [all-names-used (concat fields (keys defaults) (keys constants))]
    `(def ~name
       {:n-players ~n-players
        :init ~(let [base (merge (keywordize-map (nil-valued-map fields))
                                 (keywordize-map defaults)
                                 (keywordize-map constants))]
                 (fn [& [config]] (merge base config)))

        :legal (fn [{:keys [~@all-names-used]
                     :as ~(symbol nil "$game-state")}
                    ~(symbol nil "$player-id")
                    ~(symbol nil "$action")]
                 ~legal)

        :legal-1 (fn [{:keys [~@all-names-used]
                       :as ~(symbol nil "$game-state")}
                      ~(symbol nil "$player-id")]
                 ~legal-1)

        :view (fn [{:keys [~@all-names-used]
                    :as ~(symbol nil "$game-state")}
                   ~(symbol nil "$player-id")]
                ~view)

        :move (fn [{:keys [~@all-names-used]
                    :as ~(symbol nil "$game-state")}
                   ~(symbol nil "$actions")]
                ~move)

        :terminal? (fn [{:keys [~@all-names-used]
                         :as ~(symbol nil "$game-state")}]
                    ~terminal?)

        :score (fn [{:keys [~@all-names-used]
                      :as ~(symbol nil "$game-state")}
                     ~(symbol nil "$player-id")]
                 (double-convert-wrapper ~score))})))

(comment
(defgame hearts
  :players [4]
  :random true ;; creates a 5th player ID for :random
  :fields [... hands ...]
  :visible {:hands (get hands $player-id)}
                   ;; true means the field is visible, false means it's omitted,
                   ;; a function allows partial visibility (e.g. that player's hand).
))

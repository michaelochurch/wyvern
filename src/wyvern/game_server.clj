(ns wyvern.game-server
  (:require wyvern.nim)
  (:use wyvern.types))

(def ^:dynamic *verbose* false)

(defn set-verbose [x]
  (alter-var-root #'*verbose* (fn [_] x)))

(defn random-legal-play [view action-list]
  (first (shuffle action-list)))

(defn default-player-ids [n-players]
  (into [] (range n-players)))

(defn default-player-fns [player-ids]
  (into {}
        (map (fn [id]
               [id random-legal-play])
             player-ids)))

(defn default-on-view [player-ids]
  (into {}
        (map (fn [id]
               [id (fn [view] (printf "Player %s sees: %s\n" id (pr-str view)))])
             player-ids)))

;; TODO: legal-move determination should be a method of the GameView, not Game (players
;; don't have access to the full game state). 
(defn run-player-actions [game player-fns]
  (into {}
        (map (fn [[player-id action-fn]]
               [player-id (action-fn (view game player-id)
                                     (legal-actions game player-id))])
             player-fns)))

(defn check-player-actions [game actions]
  (every? (fn [[player-id action]]
            (contains? (legal-actions game player-id) action))
          actions))

;; TODO: this will need to fail more gracefully. 
(defn illegal-action [& args]
  (throw (Exception. (str "Illegal action: " args))))

(defn run-game [game-spec & {:keys [game-config player-ids player-fns on-view]}]
  (let [player-ids (or player-ids 
                       (default-player-ids (first (:n-players game-spec))))
        player-fns (or player-fns
                       (default-player-fns player-ids))
        on-view    (or on-view
                       (default-on-view player-ids))]
    (loop [game ((:new game-spec) game-config)]
      ;; Any view-based side effects happen each turn. 
      (doseq [[player-id on-view-fn] on-view]
        (on-view-fn (view game player-id)))
      
      (if (terminal? game)
        (all-scores game)
        (let [actions (run-player-actions game player-fns)]
          (when *verbose*
            (doseq [[player-id action] actions]
              (printf "Action of Player %s is %s\n" player-id action)))

          (if (check-player-actions game actions)
            (recur (move game actions))
            (illegal-action actions)))))))

(defn nim-test []
  (binding [*verbose* true]
    (run-game wyvern.nim/Spec)))

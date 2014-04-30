(ns wyvern.game-runner
  (:use wyvern.utils))

;; TODO: when the needs of this module are clearer, replace explicit keyword
;; accesses with prototype methods or multimethods.

(def ^:dynamic *verbose* false)

(defn set-verbose [x]
  (alter-var-root #'*verbose* (fn [_] x)))

(defn default-player-ids [n-players]
  (into [] (range n-players)))

(defn default-player-fns [game player-ids]
  (image-map (fn [id]
               (fn [view] ((:legal-1 game) view id)))
             player-ids))

(defn- print-view [player-id view]
  (printf "Player %s sees: %s\n" player-id (pr-str view)))

(defn default-on-view [player-ids]
  (image-map (fn [id]
               #(print-view id %))
             player-ids))

(defn game-instance [game & {:keys [n-players config player-ids player-fns on-view]}]
  (let [n-players (or n-players (first (:n-players game)))
        player-ids (or player-ids (default-player-ids n-players))
        init-state ((:init game) config)]
    {:game       game
     :state      init-state
     :player-ids player-ids
     :player-fns (or player-fns (default-player-fns game player-ids))
     :history    [[:init init-state]]
     :on-view    (or on-view    (default-on-view player-ids))}))

(defn get-view [game-instance player-id]
  ((get-in game-instance [:game :view]) (:state game-instance) player-id))

(defn run-player-actions [game-instance]
  (into {}
        (map (fn [[player-id f]]
               [player-id (f (get-view game-instance player-id))])
             (:player-fns game-instance))))

(defn check-player-actions [game-instance actions]
  (let [legal? (-> game-instance :game :legal)
        check  #(legal? (:state game-instance) %1 %2)]
    (every? (fn [[player-id action]]
              (check player-id action))
            actions)))

;; TODO: calls like `move` should work on game and game-instance. (multimethods)

(defn run-game-instance-one-step [game-instance]
  ;; 1. Execute any on-view functions for players.
  (doseq [[player-id on-view-fn] (:on-view game-instance)]
    (on-view-fn ((get-in game-instance [:game :view]) (:state game-instance) player-id)))

  (let [actions (run-player-actions game-instance)]
    ;; 2. Legality check.
    (when-not (check-player-actions game-instance actions)
      (throw (Exception. (format "run-game-instance-one-step: received illegal actions"))))

    ;; 3. When *verbose*, print out the actions taken.
    (when *verbose*
      (doseq [[player-id action] actions]
        (printf "Action of Player %s is %s\n" player-id action)))

    ;; 4. Return game-instance with updated :state and :history.
    (let [new-state ((get-in game-instance [:game :move])
                     (:state game-instance) actions)]
      (update game-instance :state (fn [_] new-state)
              :history #(conj % [actions new-state])))))

(defn terminal-check [game-instance]
  ((get-in game-instance [:game :terminal?]) (get game-instance :state)))

(defn run-game-instance [game-instance]
  (loop [gi game-instance]
    (if (terminal-check gi)
      gi
      (recur (run-game-instance-one-step gi)))))

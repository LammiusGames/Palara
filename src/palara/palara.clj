(ns palara.palara)

(def game (atom {}))

(def harvestrates {:north {:stone 2 :copper 1 :food 1 :wood 1}
                   :south {:stone 1 :copper 1 :food 1 :wood 2}
                   :east {:stone 1 :copper 1 :food 2 :wood 1}
                   :west {:stone 1 :copper 2 :food 1 :wood 1}})

(defn rolldie [n]
  (inc (rand-int n)))

(defn luckymove? [] (= 20 (rolldie 20)))

(defn actioncost [action]
  (cond (= action :move)  2
   :else 1))

(defn addplayer [username]
 (let [init-user { (keyword username) { :state {:inventory {:stone 0, :copper 0, :food 0, :wood 0,} :actions 2 :location :north}}}]
   (swap! game conj init-user)))

(defn reset-moves
  "sets a player's actions to a specified quantity"
  [player n]
  (swap! game conj (-> @game
                       (update-in [player :state] conj {:actions n}))))

(defn reset-action-quota
  "resets ever player's actions to 2"
  []
  (let [players (keys @game)]
   (doall (map #(reset-moves % 2) players))
   @game))

(defn harvest
   "updates games tate to give the specified player a random
    dice roll of the comodity harvested"
  [player commodity]
  (let [playerstate (:state (player @game))
        location (:location playerstate)
        harvestrate (->> harvestrates
                         location
                         commodity)
        harvestqty (->> (repeatedly #(rolldie 5))
                        (take harvestrate)
                        (reduce +))
        newstate (-> @game
                     (update-in [player :state :inventory commodity] + harvestqty)
                     (update-in [player :state :actions] - (actioncost :harvest)))
        actionsleft  (->> newstate player :state :actions)
        actionstr (str "Harvested " harvestqty " " commodity ". You have " actionsleft " actions remaining for today. Your current stock is " (->> newstate player :state :inventory))]
    (if (neg? actionsleft)
      (str "Not enough actions remaining to harvest " commodity)
      (do (swap! game conj  newstate)
          actionstr))))

(defn move "updates game state to move player to specified destination region"
  [player destination]
  (let [playerstate (:state (player @game))
        fromloc (:location playerstate)
        moves (:actions playerstate)]
   (cond (= fromloc destination) "Journey to nowhere"
         (< moves 2) "No time to get there"
    :else (swap! game conj (-> @game
                               (update-in [player :state] conj {:location destination})
                               (update-in [player :state] conj {:actions (- moves (actioncost :move))}))))))



(defn init-test-game []
  (def game (atom {}))
  (addplayer "josh")
  (addplayer :wilma))

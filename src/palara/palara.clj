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
  (cond (= action :move) (if (luckymove?) 1 2)
   :else 1))

(defn addplayer [username]
 (let [init-user { (keyword username) { :state {:inventory {:stone 0, :copper 0, :food 0, :wood 0,} :actions 0 :location :north}}}]
   (swap! game conj init-user)))


(defn harvest [player commodity]
  (let [playerstate (:state (player @game))
        location (:location playerstate)
        actioncount (:actions playerstate)
        harvestrate (->> harvestrates
                         location
                         commodity)
        harvestqty (->> (repeatedly #(rolldie 5))
                        (take harvestrate)
                        (reduce +))
        newinv   (update-in @game [player :state :inventory commodity] + harvestqty)
        newstate (update-in newinv [player :state :actions] - (actioncost :harvest))
        actionstr (str "Harvested " harvestqty " " commodity ". You have " (->> newstate player :state :actions) " actions remaining for today. Your current stock is " (->> newstate player :state :inventory))]
    (swap! game conj  newstate)
    actionstr))


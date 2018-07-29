(ns palera.test
  (:require [clojure.test :refer :all]
            [palara.palara :refer :all]))
(defn reset-test-state []
  (init-test-game)
  (addplayer :franklin))

(deftest addplayer-test
  (init-test-game)
  (addplayer "franklin")
  (is (= (-> @game :franklin :state :inventory ) {:stone 0, :copper 0, :food 0, :wood 0}))
  (is (= (-> @game :franklin :state :actions) 2))
  (is (contains? #{:north :south :east :west} (-> @game :franklin :state :location))))

(deftest movelimit-test
  (harvest :franklin :wood)
  (harvest :franklin :wood)
  (is (= "Not enough actions remaining to harvest :wood" (harvest :franklin :wood))))

(deftest harvest-test
  (reset-test-state)
  (reset-moves :franklin 4)
  (harvest :franklin :stone)
  (harvest :franklin :wood)
  (harvest :franklin :copper)
  (harvest :franklin :food)
  (let [inv-vals (vals (-> @game :franklin :state :inventory))
        bigharvest (filter #(< 5 %) inv-vals)
        toobig (filter #(< 10 %) inv-vals)]
   (is (> 2 (count bigharvest)))
   (is (= 0 (count toobig)))))

(deftest move-test
  (reset-test-state)
  (let [location (-> @game :franklin :state :location)
        unoccupied (first (disj #{:north :south :east :west} location))]
    (is (= "Journey to nowhere" (move :franklin location)))
    (is (= "moved" (subs (move :franklin unoccupied) 0 5)))
    (is (= "No time to get there" (move :franklin location)))))







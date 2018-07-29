(ns palera.test
  (:require [clojure.test :refer :all]
            [palara.palara :refer :all]))
(defn reset-test-state []
  (init-test-game)
  (addplayer :franklin))

(deftest addplayer-test
  (init-test-game)
  (addplayer "franklin")
  (is (= (-> @game :franklin :state :inventory ) {:stone 0, :copper 0, :food 0, :wood 0}) "user and inventory created")
  (is (= (-> @game :franklin :state :actions) 2) "actions available to use ")
  (is (contains? #{:north :south :east :west} (-> @game :franklin :state :location)) "random location assigned"))

(deftest movelimit-test
  (harvest :franklin :wood)
  (harvest :franklin :wood)
  (is (= "Not enough actions remaining to harvest :wood" (harvest :franklin :wood))"cannot harvest 3 times"))

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
   (is (> 2 (count bigharvest))"0 or 1 commodities harvest >5 units")
   (is (= 0 (count toobig)))"no commodities harvest > 10 units"))

(deftest move-test
  (reset-test-state)
  (let [location (-> @game :franklin :state :location)
        unoccupied (first (disj #{:north :south :east :west} location))]
    (is (= "Journey to nowhere" (move :franklin location))"cannot move to the location you are in")
    (is (= "moved" (subs (move :franklin unoccupied) 0 5)) "move when you move the first time.")
    (is (= "No time to get there" (move :franklin location))) "cannot move without actions remaining"))

(deftest trade-test
  (reset-test-state)
  (swap! game conj (-> @game (update-in [:franklin :state :inventory :food] + 5)))
  (move :franklin :north)
  (reset-moves :franklin 2)
  (trade :franklin :food :stone 1)
  (is (=  2 (-> @game :franklin :state :inventory :stone) )"harvest double in abundant region")
  (is (= 4 (-> @game :franklin :state :inventory :food) )"trading decreases the commodty we trade")
  (trade :franklin :food :wood 1)
  (is (= 1 (-> @game :franklin :state :inventory :wood) )"harvest single in normal region")
  (is (= "You don't have enough actions remainign to trade"
         (trade :franklin :food :wood 1)) "get error when we try to trade with no actions")
  (reset-moves :franklin 2)
  (is (= "don't have 5 :food" (trade :franklin :food :wood 5))"cannot trade more than we have"))







(ns detector-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [detector]))

(deftest exact-matching-single-invader
  (testing "single match"
    (let [invader ["-o-"
                   "ooo"
                   "-o-"]
          radar ["-----"
                 "--o--"
                 "-ooo-"
                 "--o--"
                 "-----"]]
      (is (= [{:x 1 :y 1 :width 3 :height 3}]
             (detector/detect radar invader)))))

  (testing "multiple matches"
    (let [invader ["-o"
                   "oo"]
          radar ["----o"
                 "-o-oo"
                 "oo-o-"
                 "--oo-"
                 "-----"]]
      (is (= [{:x 0 :y 1 :width 2 :height 2}
              {:x 2 :y 2 :width 2 :height 2}
              {:x 3 :y 0 :width 2 :height 2}]
             (detector/detect radar invader)))))

  (testing "no matches"
    (let [invader ["-o"
                   "oo"]
          radar ["---oo"
                 "-o-o-"
                 "-oo--"
                 "---o-"]]
      (is (= []
             (detector/detect radar invader))))))

(deftest exact-match-multiple-invaders
  (testing "multiple invaders"
    (let [single ["oo"]
          multiple ["o"
                    "o"]
          missing ["oo"
                   "oo"]
          radar ["-o---"
                 "-o-oo"
                 "o----"
                 "-o-o-"
                 "---o-"]]
      (is (= [{:x 3 :y 1 :width 2 :height 1}
              {:x 1 :y 0 :width 1 :height 2}
              {:x 3 :y 3 :width 1 :height 2}]
             (detector/detect-all radar [single multiple missing]))))))

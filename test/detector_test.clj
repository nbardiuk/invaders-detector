(ns detector-test
  (:require
    [clojure.test :refer [deftest is testing]]
    [detector]))

(deftest exact-matching
  (testing "single match"
    (let [invader ["-o-"
                   "ooo"
                   "-o-"]
          radar ["-----"
                 "--o--"
                 "-ooo-"
                 "--o--"
                 "-----"]]
      (is (= [[1 1]]
             (detector/detect invader radar)))))

  (testing "multiple matches"
    (let [invader ["-o"
                   "oo"]
          radar ["----o"
                 "-o-oo"
                 "oo-o-"
                 "--oo-"
                 "-----"]]
      (is (= [[0 1] [2 2] [3 0]]
             (detector/detect invader radar)))))

  (testing "no matches"
    (let [invader ["-o"
                   "oo"]
          radar ["---oo"
                 "-o-o-"
                 "-oo--"
                 "---o-"]]
      (is (= []
             (detector/detect invader radar))))))



(ns script-test
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [clojure.test :refer [deftest is testing]]
    [script]))

(defn- script-output-lines [& args]
  (str/split-lines
    (with-out-str
      (apply script/-main args))))

(defn- resource-full-path [name]
  (str (io/file (io/resource name))))

(deftest input-validation
  (testing "summary of usage on argument error"
    (is (= ["Radar file is required"
            "Invaders file is required"
            ""
            "Usage:"
            "  -r, --radar FILE              Path to radar file"
            "  -i, --invaders FILE           Path to invaders patterns file"
            "  -t, --threshold NUMBER  0.75  Threshold for similarity with invader pattern"]
           (script-output-lines))))

  (testing "invalid threshold validation"
    (is (= "Failed to validate \"-t 2\": Must be a number between 0 and 1"
           (first (script-output-lines "-t" "2"))))

    (is (= "Failed to validate \"--threshold 1/5\": Must be a number between 0 and 1"
           (first (script-output-lines "--threshold" "1/5")))))

  (testing "invalid radar file"
    (is (= "Failed to validate \"--radar ./deps.edn\": Must contain only `-` and `o` characters"
           (first (script-output-lines "--radar" "./deps.edn")))))

  (testing "invalid invaders file"
    (is (= "Failed to validate \"--invaders ./deps.edn\": Must contain only `-` and `o` characters"
           (first (script-output-lines "--invaders" "./deps.edn"))))))

(deftest end-to-end-example

  (testing "example input with match"
    (is (= (str/split-lines (slurp (io/resource "example-output")))
           (script-output-lines
             "--threshold" "0.87"
             "--radar" (resource-full-path "example-radar")
             "--invaders" (resource-full-path "example-invaders")))))

  (testing "example input without match"
    (is (= ["No invaders found"]
           (script-output-lines
             "--threshold" "1"
             "--radar" (resource-full-path "example-radar")
             "--invaders" (resource-full-path "example-invaders"))))))

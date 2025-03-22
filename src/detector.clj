(ns detector
  (:require
    [clojure.string :as str]))

(defn- image-size [image]
  {:width (count (first image))
   :height (count image)})

(defn- extract
  [image {:keys [x y width height]}]
  (let [total-size (image-size image)]
    (for [row (range y (min (+ y height) (:height total-size)))]
      (str/join (take width (drop x (image row)))))))

(defn detect [radar invader]
  (let [invader-size (image-size invader)
        radar-size (image-size radar)]
    (for [x (range 0 (:width radar-size))
          y (range 0 (:height radar-size))
          :let [area (assoc invader-size :x x :y y)]
          :when (= invader (extract radar area))]
      area)))

(defn detect-all [radar invaders]
  (mapcat #(detect radar %) invaders))
